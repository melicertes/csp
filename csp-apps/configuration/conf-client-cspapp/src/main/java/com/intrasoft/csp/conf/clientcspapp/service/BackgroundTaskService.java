package com.intrasoft.csp.conf.clientcspapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intrasoft.csp.conf.client.ConfClient;
import com.intrasoft.csp.conf.clientcspapp.model.*;
import com.intrasoft.csp.conf.clientcspapp.model.json.Environment;
import com.intrasoft.csp.conf.clientcspapp.model.json.Manifest;
import com.intrasoft.csp.conf.clientcspapp.util.FileHelper;
import com.intrasoft.csp.conf.clientcspapp.util.TimeHelper;
import com.intrasoft.csp.conf.commons.model.api.*;
import com.intrasoft.csp.conf.commons.utils.VersionParser;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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

    @Autowired
    ObjectMapper jacksonObjectMapper;


    @Scheduled(initialDelay = 10000, fixedRate = 600000)
    public void registerCspHeartbeat() {
        final SystemInstallationState state = installationService.getState();

        if (state.getInstallationState() != InstallationState.NOT_STARTED &&
                internetAvailable) {
            final AppInfoDTO appInfo = new AppInfoDTO();

            try {
                log.info("HEARTBEAT enabled and internet connectivity verified. Will try to connect");
                appInfo.setName(state.getCspRegistration().getName());
                appInfo.setRecordDateTime(TimeHelper.isoNow());

                ModulesInfoDTO modsInfo = new ModulesInfoDTO();
                modsInfo.setModules(
                        installationService.queryModulesAsServicesInstalled().stream()
                            .map(m -> {
                                ModuleInfoDTO info = new ModuleInfoDTO();
                                info.setName(m.getName());

                                ModuleDataDTO data = new ModuleDataDTO();
                                data.setActive(m.getActive());
                                data.setFullName(m.getName()+ ":" + m.getVersion());
                                data.setHash(m.getHash());
                                data.setInstalledOn(TimeHelper.isoFormat(m.getInstallDate()));
                                data.setStartPriority(m.getStartPriority());
                                data.setVersion(VersionParser.fromString(m.getVersion()));
                                info.setAdditionalProperties(data);
                                return info;
                            })
                            .collect(Collectors.toList())
                );
                appInfo.setModuleInfo(modsInfo);
                final ResponseDTO resp = client.appInfo(state.getCspId(), appInfo);
                log.info("HEARTBEAT sent to CENTRAL, response was {} - {}", resp.getResponseText(), resp.getResponseCode());
            } catch (Exception e) {
                try {
                    log.info("/api/appinfo JSON Payload {}", new ObjectMapper().writeValueAsString(appInfo));
                } catch (JsonProcessingException e1) {
                    //e1.printStackTrace();
                }
                log.error("HEARTBEAT was not sent to CENTRAL, issue was {}",e.getMessage(),e);
            }
        }

    }


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
                log.info("Attempting to download module {}:{} - this may take some time!  {}",
                        module.getName(), module.getVersion(), module.getHash());
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

                if (smtp != null && smtp.getPort()!=null) {
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

    public void scheduleInstall(final SystemModule module) {
        addTask(() -> {
            //extract module to destination location
            final File moduleDir = new File(modulesDirectory, module.getName() + module.getHash().substring(0,12));
            boolean isLegacy = false;
            Manifest manifest;
            Environment customEnv = null;
            SystemModule installingModule = module;
            try {
                if (installingModule.getActive() == false && moduleDir.exists()) {
                    storageService.deleteDirectoryAndContents(moduleDir.getAbsolutePath());
                }
                String moduleInstallDirectory = storageService.extractArchive(installingModule.getArchivePath(),
                        moduleDir.getAbsolutePath());
                // check to see if manifest.json exists (mandatory!)
                if (installationService.moduleContains(installingModule, "manifest.json") ) {
                    File manifestFile = new File(moduleInstallDirectory, "manifest.json");
                    manifest = jacksonObjectMapper.readValue(manifestFile, Manifest.class);
                    if (manifest.getFormat() == 1.0) {
                        log.warn("Manifest version 1.0 detected! LEGACY MODE = ON");
                        isLegacy = true;
                    } else if (manifest.getFormat() == 1.1) {
                        log.warn("Manifest version 1.1 detected!");
                        if (installationService.moduleContains(installingModule, "env.json") ) {
                            customEnv = jacksonObjectMapper.readValue(new File(moduleInstallDirectory, "env.json"), Environment.class);
                        }
                    } else {
                        log.error("Unsupported manifest.json !!! Rejected!!");
                        return new BackgroundTaskResult<>(false, -1);

                    }

                    installingModule.setManifestJsonAsText(jacksonObjectMapper.writeValueAsString(manifest));
                } else {
                    log.error("Module does not contain a manifest.json description! Rejected!");
                    return new BackgroundTaskResult<>(false, -1);
                }


                // set module to INSTALLING
                installingModule.setModuleState(ModuleState.INSTALLING);
                installingModule.setModulePath(moduleInstallDirectory);
                installingModule = installationService.saveSystemModule(installingModule);


                //perform all operations:
                // a. load any docker.tar found
                installationService.installDockerImages(installingModule);

                // b. copy .env from homedir to the module dir
                copyEnvironment(moduleInstallDirectory);

                createCustomEnv(customEnv, installingModule);
                // c. execute any first-time.sh found
                // TODO 1.1 change "first-time.sh" to defined name for first time file

                if (installationService.moduleContains(installingModule, "first-time.sh")) {
                    log.info("First time init script detected. Will launch for module {}",installingModule.getName());
                    File firstTime = new File(moduleInstallDirectory, "first-time.sh");
                    if (firstTime.exists()) {
                        firstTime.setExecutable(true,true);
                    } else {
                        log.error("First time file {}  was not found although present in module {} !",
                                firstTime.getAbsolutePath(), installingModule.getName());
                    }

                    Map<String,String> env = new HashMap<>();
                    env.put("DIR", moduleInstallDirectory);
                    final BackgroundTaskResult<Boolean, Integer> result = executeScriptSimple("exec_first-time.sh", env);
                    if (result.getSuccess() == false) {
                        log.error("Failed execution detected on first-time.sh script - *WILL PROCEED* WITH INSTALLATION!");
                    }
                }

                // d. set module to INSTALLED + ACTIVE = TRUE
                installingModule.setActive(true);
                installingModule.setModuleState(ModuleState.INSTALLED);
                installingModule.setInstallDate(new LocalDateTime());
                installingModule.setModulePath(moduleDir.getPath());
                installationService.saveSystemModuleService(installingModule, isLegacy, needsAgent(customEnv),
                        needsVhost(customEnv));

                return new BackgroundTaskResult<>(true, 0);



            } catch (IOException e) {
                log.error("Failed to extract and install module",e);
                installingModule.setModuleState(ModuleState.DOWNLOADED);
                installingModule.setModulePath(null);
                installationService.saveSystemModule(installingModule);
                return new BackgroundTaskResult<>(false, -1);
            }
        });
    }

    private void createCustomEnv(Environment customEnv, SystemModule installingModule) {
        // TODO 1.1 create env.modulename from j2 env.json
        // we execute j2 on the env.json and relevant conf template to create additional environment variables here
        // we save the env as env.startpriority.modulename here and in /root
        // we append this env to the ".env" found here
        // we save the vhost and overwrite (to vhost configured location)
    }

    private boolean needsVhost(Environment customEnv) {
        if (customEnv == null) {
            return false;
        }
        com.intrasoft.csp.conf.clientcspapp.model.json.Service srv = customEnv.getServices().get(0);
        return !StringUtils.isEmpty(srv.getInternalName()) ||
                !StringUtils.isEmpty(srv.getExternalName());

    }

    private boolean needsAgent(Environment customEnv) {
        return customEnv == null ? false : customEnv.getServices().get(0).isAgent();
    }

    private void copyEnvironment(String targetDirectory) throws IOException{
        //by now we expect environment to be prepared.
        File rootEnv = new File(System.getProperty("user.home"), "env");

        // TODO 1.1 concatenate all "env.<modulename>" files to a global .env file here
        // we re-copy all env files in proper order, to this directory using START_PRIORITY
        // split them with new lines! on concatenation


        // split filename on "." - e.g. env.800.rt = String[] { "env", "800", "rt" }
        // we use the 2nd arg to add to a TreeMap<Double,"filename">
        // we read them ascending and concatenate to a global "new" env (this gets copied BUT NOT SAVED)


        if (rootEnv.exists()) {
            FileHelper.copy(rootEnv.toPath(), new File(targetDirectory, ".env").toPath());
        } else {
            log.error("Root environment (env) not detected, cause of installation problems!");
        }

    }

    public void scheduleReInstall(SystemModule module) {
        addTask(() -> {
            // the tasks below will happen in sequence:
            log.info("Re-install scheduling a DELETE with an INSTALL afterwards...");
            scheduleDelete(module);
            scheduleInstall(module);
            return new BackgroundTaskResult<>(false, -1);
        });
    }


    public void scheduleDelete(SystemModule module) {
        addTask(() -> {
            // TODO check if module is running before delete

            try {
                // execute any last-time.sh found
                // TODO 1.1 execute "last-time.sh" as defined name for last time file, JSON is in module entity

                storageService.deleteDirectoryAndContents(module.getModulePath());
                module.setModuleState(ModuleState.DOWNLOADED);
                module.setActive(false);
                final SystemService service = installationService.queryService(module);
                installationService.removeService(service);
                log.info("Module {} has been removed and service deleted", module.getName());
                return new BackgroundTaskResult<>(true, 0);
            } catch (IOException e) {
                log.error("Exception removing module {} with error {}",module.getName(), e.getMessage(),e);
            }
            return new BackgroundTaskResult<>(false, -1);
        });
    }


    public void scheduleStartActiveModules() {
        addTask(() -> {
            // for every active module, sort by priority

            final List<BackgroundTaskResult<Boolean, Integer>> results = installationService.queryAllModulesInstalled(true).stream().map(module -> {
                SystemService service = installationService.queryService(module);
                if (service.getStartable() == true) {

                    //copy the environment again before starting
                    try {
                        copyEnvironment(module.getModulePath());
                    } catch (IOException e) {
                        log.error("Failed to copy ENVIRONMENT! {}", e.getMessage(),e);
                    }

                    checkOAMAgentCreation(module,service);
                    checkVHostCreation(module,service);

                    if (service.getServiceState() == ServiceState.NOT_RUNNING) {
                        log.info("About to start service {} with start priority {}", service.getName(), module.getStartPriority());
                        Map<String, String> env = new HashMap<String, String>();
                        env.put("SERVICE_NAME", module.getName());
                        env.put("SERVICE_DIR", module.getModulePath());
                        env.put("SERVICE_PRIO", module.getStartPriority().toString());
                        try {
                            BackgroundTaskResult<Boolean, Integer> result = executeScriptSimple("startService.sh", env);
                            if (result.getSuccess() == true) {
                                if (installationService.moduleContains(module, "proc_ready.source")) {
                                    result = executeAdvancedStartMonitor(module);
                                }
                                service = installationService.updateServiceState(service, ServiceState.RUNNING);

                            } else {
                                log.error("Executing the start script was not successful for service {}", module.getName());
                            }

                            // set the name
                            result.setModuleName(module.getName());

                            return result == null ? new BackgroundTaskResult<>(false, -1000, module.getName()) : result;
                        } catch (IOException e) {
                            log.error("Failed to start {} with start priority {}", service.getName(), module.getStartPriority());
                            log.error("Exception was {}", e.getMessage(), e);
                            service = installationService.updateServiceState(service, ServiceState.NOT_RUNNING);
                        }

                        return new BackgroundTaskResult<Boolean, Integer>(false, -100, module.getName());
                    } else {
                        log.warn("Service {} is marked running!??? No action", service.getName());
                    }
                    log.info("Service {} state {}", service.getName(), service.getServiceState());
                } else {
                    log.info("Service {} is not a startable service, moving on", service.getName());
                }
                return new BackgroundTaskResult<Boolean, Integer>(true, 0, module.getName());
            }).distinct().collect(Collectors.toList());

            final BackgroundTaskResult<Boolean,Integer> finalResult = new BackgroundTaskResult<Boolean, Integer>(true,0,"all");
            results.stream().filter( r -> r.getSuccess() == false).forEach(failed -> {
                log.error("Service {} failed to start, error code {}", failed.getModuleName(), failed.getErrorCode());
                if (finalResult.getSuccess() == true) {
                    finalResult.setSuccess(false);
                }
            });
            return finalResult;
        });
    }

    private void checkVHostCreation(SystemModule module, SystemService service) {
        //TODO 1.1 Check if VHost is required and CREATE IT
        if (service.getVHostNecessary() && service.getVhostCreated() == null) {
            //do it here
        }

    }

    private void checkOAMAgentCreation(SystemModule module, SystemService service) {
        //TODO 1.1 Check if OAM agent is required and CREATE IT

        if (service.getOamAgentNecessary() && service.getOamAgentCreated() == null) {
            //do it here

        }
    }

    /**
     * execute the advanced start monitor process
     * @param module
     */
    private BackgroundTaskResult<Boolean, Integer> executeAdvancedStartMonitor(SystemModule module) throws IOException {

        log.info("Monitoring the module startup process...");
        Map<String,String> env = new HashMap<String, String>();
        env.put("SERVICE_NAME", module.getName());
        env.put("SERVICE_DIR", module.getModulePath());
        env.put("SERVICE_PRIO", module.getStartPriority().toString());

        return executeScriptSimple("startModuleMonitor.sh", env);
    }


    public void scheduleStopActiveModules() {
        addTask(() -> {
            // for every active module, sort by priority
            installationService.queryAllModulesInstalled(false).forEach(module -> {
                SystemService service = installationService.queryService(module);
                if (service.getStartable() == true) {
                    if (service.getServiceState() == ServiceState.RUNNING) {
                        log.info("About to stop service {} with start priority {}", service.getName(), module.getStartPriority());
                        Map<String,String> env = new HashMap<String, String>();
                        env.put("SERVICE_NAME", module.getName());
                        env.put("SERVICE_DIR", module.getModulePath());
                        env.put("SERVICE_PRIO", module.getStartPriority().toString());
                        try {
                            //TODO handle stop return value
                            executeScriptSimple("stopService.sh", env);
                            service = installationService.updateServiceState(service,ServiceState.NOT_RUNNING);
                        } catch (IOException e) {
                            log.error("Failed to start {} with start priority {}", service.getName(), module.getStartPriority());
                            log.error("Exception was {}",e.getMessage(),e);
                            service = installationService.updateServiceState(service,ServiceState.RUNNING);
                        }
                    } else {
                        log.warn("Service {} is marked NOT running!???", service.getName());
                    }
                    log.info("Service {} state {}", service.getName(), service.getServiceState());
                } else {
                    log.info("Service {} is not a startable service, moving on");
                }
            });
            return new BackgroundTaskResult<>(true, 0);
        });
    }


    public BackgroundTaskResult<Boolean, Integer> executeScriptSimple(String scriptName, Map<String,String> env) throws IOException {
        //generate script
        File script = new File(extractResource("shellscripts/" + scriptName, modulesDirectory));
        script.setExecutable(true, true); //make it executable!

        // vars
        final SystemInstallationState state = installationService.getState();
        Map<String, String> envVars = new HashMap<String, String>();
        envVars.put("CSPNAME", state.getCspRegistration().getName());
        envVars.put("CSPDOMAIN", state.getCspRegistration().getDomainName());
        envVars.put("CSPHOME", modulesDirectory);
        envVars.put("HOME",System.getProperty("user.home")); // make sure $HOME is set
        if (env != null) {
            envVars.putAll(env);
        }

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
        //scriptname is full path, we only need the name
        String target = new File(scriptName).getName();
        final File targetFile = new File(targetDirectory, target);
        if (targetFile.exists()) {
            targetFile.delete();
        }
        log.info("Extracting {} to {}",scriptName, targetFile.getAbsolutePath());

        //long total = FileHelper.copy(script.getInputStream(), targetFile.toPath(), null, StandardCopyOption.REPLACE_EXISTING);
        FileHelper.copy(script.getFile().toPath(), targetFile.toPath());
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