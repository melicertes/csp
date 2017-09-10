package com.intrasoft.csp.conf.clientcspapp.service;

import com.intrasoft.csp.conf.client.ConfClient;
import com.intrasoft.csp.conf.clientcspapp.model.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by tangelatos on 06/09/2017.
 */
@Service
@Slf4j
public class BackgroundTaskService {

    public static final String INTERNAL_CERTS_SH = "internalCerts.sh";

    public static final String EXTERNAL_CERTS_SH = "externalCerts.sh";

    public static final String ENV_CREATION_SH = "createEnv.sh";

    private ArrayBlockingQueue<BackgroundTask> backgroundTasks =
            new ArrayBlockingQueue<>(200);

    private Map<BackgroundTask, BackgroundTaskResult> completedTasks = new HashMap<>();

    private Boolean internetAvailable = false;

    @Value("${conf.server.host}")
    String host;

    @Value("${conf.server.port}")
    Integer port;

    @Value("${installation.modules.directory}")
    String modulesDirectory;

    @Value("${installation.temp.directory}")
    String tempDirectory;

    private AtomicInteger countTasks = new AtomicInteger(0);

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    ConfClient client;

    @Autowired
    InstallationService installationService;

    @Autowired
    SimpleStorageService storageService;

    @Autowired
    ExternalProcessService externalProcessService;

    @SneakyThrows
    public <S, R> void addTask(BackgroundTask<S, R> task) {
        while (!backgroundTasks.offer(task)) {
            log.warn("Task {} was not submitted at this time, capacity full. Waiting 200ms", task);
            Thread.sleep(200);
        }
        log.info("Task {} was added for background work", countTasks.incrementAndGet());
    }

    public <S, R> BackgroundTaskResult<S, R> retrieveResult(BackgroundTask<S, R> task) {
        return completedTasks.get(task);
    }

    @Scheduled(fixedDelay = 10000, initialDelay = 30000)
    public void pollAndProcessTasks() {
        if (backgroundTasks.size() == 0) {
            log.debug("Nothing to execute, sleeping....");
        } else {
            List<BackgroundTask> toDo = new ArrayList<>(5);
            backgroundTasks.drainTo(toDo);
            log.info("Found {} tasks to execute in the background",toDo.size());
            for (BackgroundTask t : toDo) {
                log.info("Executing {}",t);
                final BackgroundTaskResult result = t.execute();
                log.info("Completed, result is {}", result);
                completedTasks.put(t,result);
            }
        }

    }


    @Scheduled(fixedDelay = 360000, initialDelay = 5000)
    public void verifyInternetConnectivity() {
        try {
            internetAvailable = InternetAvailabilityChecker.isInternetAvailable(host, port);
            log.info("Internet connectivity test has passed, connection is OK");
        } catch (IOException e) {
            log.error("Internet connectivity check failed!");
        }
    }

    public Boolean isInternetAvailable() {
        return internetAvailable;
    }

    public void scheduleDownload(final SystemModule module) {
        addTask(() -> {
            if (installationService.canDownload() && internetAvailable) {
                module.setModuleState(ModuleState.DOWNLOADING);
                installationService.updateSystemModuleState(module.getId(), ModuleState.DOWNLOADING);
                log.info("Attempting to download {}",module.getHash());
                final SystemInstallationState state = installationService.getState();
                final ResponseEntity entity = client.update(state.getCspId(), module.getHash());
                log.info("Entity for {} received, code {}",module.getHash(), entity.getStatusCodeValue());
                try {
                    if (entity.getStatusCodeValue() == HttpStatus.OK.value()) { // we are downloading!
                        Resource resource = (Resource) entity.getBody();
                        // lets copy this directly to a temp location
                        String location = storageService.storeFileTemporarily(resource.getInputStream(), module.getHash() + ".zip");
                        // update module information
                        module.setModuleState(ModuleState.DOWNLOADED);
                        module.setArchivePath(location);
                        installationService.saveSystemModule(module);
                        log.info("File has been received in temporary location for {}", module.getHash());
                        return new BackgroundTaskResult<SystemModule, Boolean>(module, true);
                    } else {
                        return new BackgroundTaskResult<SystemModule, Boolean>(module,false);
                    }
                } catch (IOException ioe) {
                    module.setModuleState(ModuleState.UNKNOWN);
                    module.setArchivePath(null);
                    installationService.saveSystemModule(module);
                    log.error("IO exception in download: {}",ioe.getMessage(),ioe);
                    return new BackgroundTaskResult<SystemModule, Boolean>(module,false);
                }
            } else {
                log.error("Installation is not ready. Module cannot be downloaded {}", module.getHash());
                return new BackgroundTaskResult<SystemModule, Boolean>(module, false);
            }
        });

    }


    public void scheduleInternalCertsGeneration() {
        addTask(() -> {
            try {
                return executeScriptSimple(INTERNAL_CERTS_SH, null);
            } catch (IOException e) {
                log.error("Failed to execute script for certificates", e.getMessage(), e);
            }
            return new BackgroundTaskResult<>(false, -1);
        });
    }

    public void scheduleExternalCertsGeneration() {
        addTask(() -> {
            try {
                Map<String,String> env = new HashMap<String, String>();
                env.put("TMPDIR", tempDirectory);

                return executeScriptSimple(EXTERNAL_CERTS_SH, env);
            } catch (IOException e) {
                log.error("Failed to execute script for certificates", e.getMessage(), e);
            }
            return new BackgroundTaskResult<>(false, -1);
        });
    }


    public void scheduleEnvironmentCreation() {
        addTask(() -> {
            try {
                final SystemInstallationState state = installationService.getState();
                final SmtpDetails smtp = state.getSmtpDetails();

                //extract resources needed
                final String envJsonFile = extractResource("shellscripts/templ/env.json", modulesDirectory);
                final String envFile = extractResource("shellscripts/templ/env.j2", modulesDirectory);
                final String cspSitesFile = extractResource("shellscripts/templ/csp-sites.conf.j2", modulesDirectory);


                Map<String,String> env = new HashMap<String, String>();
                env.put("ENVJSON", envJsonFile);
                env.put("J2ENV", envFile);
                env.put("SITESC", cspSitesFile);
                env.put("INT_IP", state.getCspRegistration().getInternalIPs().get(0));

                if (smtp != null) {
                    env.put("MAIL_HOST", smtp.getHost());
                    env.put("MAIL_PORT", smtp.getPort().toString());
                    env.put("MAIL_USERNAME", smtp.getUserName());
                    env.put("MAIL_PASSWORD", smtp.getPassword());
                } else {
                    log.warn("SMTP Details not specified, empty values provided!");

                    env.put("MAIL_HOST", "");
                    env.put("MAIL_PORT", "");
                    env.put("MAIL_USERNAME", "");
                    env.put("MAIL_PASSWORD", "");
                }
                log.debug("Configured environment variables: {}",env);
                return executeScriptSimple(ENV_CREATION_SH, env);
            } catch (IOException e) {
                log.error("Failed to execute script for certificates", e.getMessage(), e);
            }
            return new BackgroundTaskResult<>(false, -1);
        });

    }

    public void scheduleInstall(SystemModule module) {
        addTask(() -> {
            // TODO implement install
            //extract module to destination location

            // set module to INSTALLING

            //perform all operations:
            // a. load any docker.tar found
            // b. copy .env from homedir to the module dir
            // c. execute any first-time.sh found
            // d. set module to INSTALLED + ACTIVE = TRUE
            return new BackgroundTaskResult<>(false, -1);
        });
    }

    public void scheduleReInstall(SystemModule module) {
        addTask(() -> {
            // the tasks below will happen in sequence:
            scheduleDelete(module);
            scheduleInstall(module);
            return new BackgroundTaskResult<>(false, -1);
        });
    }


    public void scheduleDelete(SystemModule module) {
        addTask(() -> {
            // TODO implement delete module
            // stop module ? check if module is now running!

            // remove module directory

            // set module to DOWNLOADED + ACTIVE = FALSE

            return new BackgroundTaskResult<>(false, -1);
        });
    }


    public void scheduleStartActiveModules() {
        addTask(() -> {
            // TODO implement start active modules
            // for every active module, sort by priority

            // execute start action

            return new BackgroundTaskResult<>(false, -1);
        });
    }

    public void scheduleStopActiveModules() {
        addTask(() -> {
            // TODO implement stop active modules

            // for every active module, sort by priority reversed

            // execute start action

            return new BackgroundTaskResult<>(false, -1);
        });
    }


    private BackgroundTaskResult<Boolean, Integer> executeScriptSimple(String scriptName, Map<String,String> env) throws IOException {
        //generate script
        extractResource("shellscripts/"+scriptName, modulesDirectory);

        // vars
        final SystemInstallationState state = installationService.getState();
        Map<String, String> envVars = new HashMap<String, String>();
        envVars.put("CSPNAME", state.getCspRegistration().getName());
        envVars.put("CSPDOMAIN", state.getCspRegistration().getDomainName());
        envVars.putAll(env);

        //execute script
        int exitCode = externalProcessService.executeExternalProcess(modulesDirectory, Optional.of(envVars),
                "sh", "-c", "./"+ scriptName);
        if (exitCode == 0) { // success!
            log.info("Executed script {} returned SUCCESS", scriptName);
            return new BackgroundTaskResult<>(true, exitCode);
        }
        return new BackgroundTaskResult<>(false, exitCode);
    }

    /**
     * retrieves a file from the classpath. Saves in the directory specified.
     * @param scriptName
     * @throws IOException
     * @return full path of extracted resource
     */
    private String extractResource(String scriptName, String targetDirectory) throws IOException {
        final Resource script = resourceLoader.getResource("classpath:"+scriptName);
        final File targetFile = new File(targetDirectory, scriptName);
        Files.copy(script.getInputStream(), targetFile.toPath());
        return targetFile.getAbsolutePath();
    }
}

class InternetAvailabilityChecker
{

    public static boolean isInternetAvailable(String host, Integer port) throws IOException
    {
        return isHostAvailable("google.com",443) && isHostAvailable(host, port);
    }

    private static boolean isHostAvailable(String hostName,Integer port) throws IOException
    {
        try(Socket socket = new Socket())
        {
            InetSocketAddress socketAddress = new InetSocketAddress(hostName, port);
            socket.connect(socketAddress, 3000);

            return true;
        }
        catch(UnknownHostException unknownHost)
        {
            return false;
        }
    }
}