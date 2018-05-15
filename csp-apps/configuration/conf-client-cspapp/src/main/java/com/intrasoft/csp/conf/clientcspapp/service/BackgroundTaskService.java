package com.intrasoft.csp.conf.clientcspapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intrasoft.csp.conf.client.ConfClient;
import com.intrasoft.csp.conf.clientcspapp.model.*;
import com.intrasoft.csp.conf.clientcspapp.model.json.Environment;
import com.intrasoft.csp.conf.clientcspapp.model.json.Manifest;
import com.intrasoft.csp.conf.clientcspapp.model.json.Service;
import com.intrasoft.csp.conf.clientcspapp.util.FileHelper;
import com.intrasoft.csp.conf.clientcspapp.util.TimeHelper;
import com.intrasoft.csp.conf.commons.model.api.*;
import com.intrasoft.csp.conf.commons.utils.VersionParser;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by tangelatos on 06/09/2017.
 */
@Component
@Slf4j
public class BackgroundTaskService {

    public static final String DOCKERCOMPOSE_YML_TEMPLATE = "docker-compose.yml.j2";

    @Value("${installation.reqs.memoryInGB}")
    public int memoryGb;

    @Value("${installation.reqs.diskFreeInGB}")
    public int diskGb;

    @Value("${installation.reqs.cpus}")
    public int vcpus;

    @Value("${installation.forced:false}")
    private Boolean canProceedForced;

    @Cacheable(cacheNames = {"req.check"})
    public Boolean canInstallAsPerRequirements() {
        log.info("Requirements: Verifying H/W requirements: {}GB total memory, {}GB free disk, {} CPUs available",
                memoryGb,diskGb,vcpus);
        int vcpusFound = Runtime.getRuntime().availableProcessors();
        int diskFreeMB = (int) (new File("/opt/csp").getFreeSpace() / 1024 / 1024);
        int memoryFoundMB = (int) (((com.sun.management.OperatingSystemMXBean) ManagementFactory
                .getOperatingSystemMXBean()).getTotalPhysicalMemorySize()/ 1024 / 1024 );

        int success = 0;
        log.info("Found OS         : {}", System.getProperty("os.name"));

        if (vcpusFound < vcpus) {
            log.warn("Found CPUs       : {}, Required: {} - FAIL", vcpusFound, vcpus);
            success++;
        }
        if (diskFreeMB < diskGb * 1024) {
            log.warn("Found Free space : {}MB, Required: {}MB - FAIL", diskFreeMB, diskGb * 1024);
            success++;
        }

        if (memoryFoundMB < memoryGb * 1024) {
            log.warn("Found Total RAM  : {}MB, Required: {}MB - FAIL", memoryFoundMB, memoryGb * 1024);
            success++;
        }
        if (success > 0) {
            log.error("Requirements check result: FAILED");
        }

        if (canProceedForced) {
            log.warn("Installation is forced due to configuration override");
            return true;
        } else
            return success <= 0;
    }

    @Getter
    enum ControlScript {
        FIRST_TIME("exec_first-time.sh", "FIRST_TIME_SH"),
        LAST_TIME("exec_last-time.sh", "LAST_TIME_SH");

        private final String wrapper;
        private final String envVar;

        ControlScript(String wrapper, String envVar) {
            this.wrapper = wrapper;
            this.envVar = envVar;
        }
    }

    public static final String INTERNAL_CERTS_SH = "internalCerts.sh";

    public static final String EXTERNAL_CERTS_SH = "externalCerts.sh";

    public static final String ENV_CREATION_SH = "createEnv.sh";


    public static final String ENV_MODULE_CREATION_SH = "createModuleEnv.sh";

    public static final String EXEC_CONT_SCRIPT_SH = "exec_contained_script.sh";

    private ArrayBlockingQueue<BackgroundTask> backgroundTasks =
            new ArrayBlockingQueue<>(200);

    private Map<BackgroundTask, BackgroundTaskResult> completedTasks = new HashMap<>();

    private Boolean internetAvailable = false;

    @Value("${installation.vhost.directory}")
    String vhostDirectory;

    @Value("${conf.server.host}")
    String host;

    @Value("${conf.server.port}")
    Integer port;

    @Value("${installation.modules.directory}")
    String modulesDirectory;

    @Value("${installation.temp.directory}")
    String tempDirectory;

    @Value("${installation.oam.name:oam}")
    String moduleOAMname;
    @Value("${installation.apache.name:apache}")
    String moduleAPCname;


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
    ObjectMapper jackson;


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
                    log.info("/api/appinfo JSON Payload {}", jackson.writeValueAsString(appInfo));
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
                final String envJsonFile = extractResource("shellscripts/templ/common.env.json", modulesDirectory);
                final String envFile = extractResource("shellscripts/templ/common.env.j2", modulesDirectory);


                Map<String,String> env = new HashMap<String, String>();
                env.put("ENVJSON", envJsonFile);
                env.put("J2ENV", envFile);
                //env.put("SITESC", cspSitesFile);
                env.put("INT_IP", state.getCspRegistration().getInternalIPs().get(0));

                if (smtp != null && smtp.getPort()!=null) {
                    env.put("MAIL_HOST", smtp.getHost());
                    env.put("MAIL_PORT", smtp.getPort().toString());
                    env.put("MAIL_USERNAME", smtp.getUserName());
                    env.put("MAIL_PASSWORD", smtp.getPassword());

                    if (smtp.getSenderEmail() == null) {
                        env.put("MAIL_SENDER_NAME", "Notification - Do Not Reply");
                        env.put("MAIL_SENDER_EMAIL", smtp.getUserName());
                        log.warn("Sender Email is not specified, using {}", smtp.getUserName());
                    } else {
                        env.put("MAIL_SENDER_NAME", smtp.getSenderName());
                        env.put("MAIL_SENDER_EMAIL", smtp.getSenderEmail());
                    }
                } else {
                    log.warn("SMTP Details not specified, empty values provided!");

                    env.put("MAIL_HOST", "");
                    env.put("MAIL_PORT", "");
                    env.put("MAIL_USERNAME", "");
                    env.put("MAIL_PASSWORD", "");
                    env.put("MAIL_SENDER_NAME", "");
                    env.put("MAIL_SENDER_EMAIL", "");
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
                    manifest = getManifest(moduleInstallDirectory);
                    if (manifest.getFormat() == 1.0) {
                        log.warn("Manifest version 1.0 detected! LEGACY MODE = ON");
                        isLegacy = true;
                    } else if (manifest.getFormat() == 1.1) {
                        log.warn("Manifest version 1.1 detected!");
                        if (installationService.moduleContains(installingModule, "env.json") ) {
                            customEnv = jackson.readValue(new File(moduleInstallDirectory, "env.json"), Environment.class);
                            if (customEnv.getServices().size() == 1) {
                                installingModule.setExternalName(customEnv.getServices().get(0).getExternalName());

                            } else {
                                List<String> names = customEnv.getServices().stream()
                                        .filter( s -> s.isAgent())
                                        .map( s -> s.getExternalName()).collect(Collectors.toList());
                                String namesjoined = String.join("|", names);
                                installingModule.setExternalName(namesjoined);
                            }
                            log.info("Service {} external names found {}", installingModule.getName(), installingModule.getExternalName().split(Pattern.quote("|")));
                        }
                    } else {
                        log.error("Unsupported manifest.json !!! Rejected!!");
                        return new BackgroundTaskResult<>(false, -1);

                    }

                    installingModule.setManifestJsonAsText(jackson.writeValueAsString(manifest));
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

                // create custom environment
                if (manifest.getFormat() > 1.0 && customEnv != null) { // env.json is not mandatory
                    createCustomEnv(customEnv, installingModule);
                }
                // c. execute any first-time.sh found
                String ftime = "first-time.sh"; //compliant with 1.0 manifest
                if (manifest.getFormat() > 1.0 && manifest.getShFirst() != null) {
                    ftime = manifest.getShFirst();
                }

                if (ftime != null && installationService.moduleContains(installingModule, ftime)) {
                    log.info("First time init script {} detected. Will launch for module {}",ftime, installingModule.getName());
                    executeModuleShScript(installingModule, moduleInstallDirectory, ftime, ControlScript.FIRST_TIME);
                }

                // d. set module to INSTALLED + ACTIVE = TRUE
                installingModule.setActive(true);
                installingModule.setModuleState(ModuleState.INSTALLED);
                installingModule.setInstallDate(new LocalDateTime());
                installingModule.setModulePath(moduleDir.getPath());
                installationService.saveSystemModuleService(installingModule, isLegacy, needsAgent(customEnv),
                        needsVhost(customEnv));

                //fix previous module version, if any
                final String moduleName = installingModule.getName();
                final String moduleHash = installingModule.getHash();
                installationService.queryAllModulesInstalled(true).stream()
                        .filter( m -> m.getActive())
                        .filter( m -> m.getName().contentEquals(moduleName)) //only same name
                        .filter( m -> !m.getHash().contentEquals(moduleHash)) // only not ours
                        .forEach( m -> {
                            m.setActive(false);
                            log.info("Module {} with path {} is now set inactive",m.getModulePath(), m.getName());
                            installationService.saveSystemModule(m);


                            // b1 make sure there are no previous containers with the same name!
                            Map<String,String> params = new HashMap<>();
                            params.put("SERVICE_DIR", m.getModulePath());
                            params.put("SERVICE_NAME", m.getName());
                            try {
                                executeScriptSimple("rmContainers.sh", params);
                                log.info("Containers for module {} are now removed / path {} / active {}",
                                        m.getName(), m.getModulePath(), m.getActive());
                            } catch (IOException e) {
                                log.error("PROBLEM!!!! Failed to clear containers for module {} id {}", m.getName(), m.getId());
                            }


                        });

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

    private boolean executeModuleShScript(SystemModule module, String moduleInstallDirectory, String scriptName, ControlScript type) throws IOException {
        File scriptInModule = new File(moduleInstallDirectory, scriptName);
        if (scriptInModule.exists()) {
            scriptInModule.setExecutable(true,true);

            Map<String,String> env = new HashMap<>();
            env.put("DIR", moduleInstallDirectory);
            env.put(type.getEnvVar(), scriptName);
            final BackgroundTaskResult<Boolean, Integer> result = executeScriptSimple(type.getWrapper(), env);
            if (result.getSuccess() != false) {
                return true;
            }
            log.error("Failed execution detected on {} script - *WILL PROCEED* WITH INSTALLATION!",type);
        } else {
            log.error("First time file {}  was not found although present in module {} !",
                    scriptInModule.getAbsolutePath(), module.getName());
        }
        return false;
    }

    private Manifest getManifest(String moduleInstallDirectory) throws IOException {
        Manifest manifest;File manifestFile = new File(moduleInstallDirectory, "manifest.json");
        manifest = jackson.readValue(manifestFile, Manifest.class);
        return manifest;
    }

    private void createCustomEnv(Environment customEnv, SystemModule installingModule) throws IOException {
        //not final; it may be overwritten by module
        String cspSitesFile = extractResource("shellscripts/templ/csp-sites.conf.j2", modulesDirectory);
        String modulePrefix = installingModule.getName() + "." + installingModule.getStartPriority();

        final String envTemplateFile = extractResource("shellscripts/templ/module.env.j2", modulesDirectory);
        final SystemInstallationState state = installationService.getState();
        final SmtpDetails smtp = state.getSmtpDetails();

        mergeEnvironment(customEnv, installingModule.getModulePath());

        //custom file for csp-sites.conf.j2
        File customSitesConf = new File(installingModule.getModulePath(), "site-config");
        if (customSitesConf.exists() && customSitesConf.isDirectory()) {
            File customSiteConfiguration = customSitesConf.listFiles()[0];
            if (customSiteConfiguration.exists() && customSiteConfiguration.isFile()) {
                log.info("Custom SITE CONFIGURATION (vhosts) has been detected: {}", customSiteConfiguration.getAbsolutePath());
                cspSitesFile = customSiteConfiguration.getAbsolutePath();
            }

        }



        Map<String,String> env = new HashMap<String, String>();
        env.put("ENVJSON", installingModule.getModulePath() + "/env.merged.json");
        env.put("J2ENV",  envTemplateFile);
        env.put("SITESC", cspSitesFile);
        env.put("MODULE_PREFIX", modulePrefix);
        env.put("INT_IP", state.getCspRegistration().getInternalIPs().get(0));

        if (smtp != null && smtp.getPort()!=null) {
            env.put("MAIL_HOST", smtp.getHost());
            env.put("MAIL_PORT", smtp.getPort().toString());
            env.put("MAIL_USERNAME", smtp.getUserName());
            env.put("MAIL_PASSWORD", smtp.getPassword());
        }
        log.info("Configured environment variables: {}",env);

        executeScriptSimple(ENV_MODULE_CREATION_SH, env);

    }

    /**
     * read the default environment from the classpath and merge it into this custom one.
     * @param customEnv
     * @param modulePath
     */
    private void mergeEnvironment(Environment customEnv, String modulePath) throws IOException {
        final String commonEnv = extractResource("shellscripts/templ/common.env.json", modulesDirectory);
        Environment global = jackson.readValue(new File(commonEnv), Environment.class);
        if (customEnv!= null) {
            global.setServices(customEnv.getServices());
        }
        jackson.writeValue(new File(modulePath, "env.merged.json"), global);//we write the merged one.
    }

    private boolean needsVhost(Environment customEnv) {
        if (customEnv == null) {
            return false;
        }
        long vHostCounter = customEnv.getServices().stream()
                .filter( s -> !StringUtils.isEmpty(s.getExternalName()) || !StringUtils.isEmpty(s.getInternalName()))
                .count();
        return vHostCounter >= 1;

    }

    private boolean needsAgent(Environment customEnv) {
        return customEnv != null && customEnv.getServices().stream().filter(Service::isAgent).count() >= 1;
    }

    private void copyEnvironment(String targetDirectory) throws IOException{
        //by now we expect environment to be prepared.
        File rootEnvDir = new File(System.getProperty("user.home"));
        TreeMap<Integer,String> envMap = new TreeMap<>();
        for (File envFile : rootEnvDir.listFiles((dir, name) -> name.endsWith("env"))) {
            String[] parts = envFile.getName().split(Pattern.quote("."));
            if (parts.length != 3) {
                log.warn("env file {} is not valid", envFile);
                continue;
            }
            envMap.put(Integer.parseInt(parts[1]), envFile.getName());
        }
        log.info("Module ENV variables: {} ", envMap);

        StringBuilder allText = new StringBuilder();
        envMap.values().forEach( fileName -> {
            try {
                allText.append("# file: ").append(fileName).append(" start\n");
                IOUtils.readLines(new FileInputStream(new File(rootEnvDir,fileName)), "UTF8")
                        .forEach( l -> allText.append(l).append("\n"));
                allText.append("# file: ").append(fileName).append(" end\n");
            } catch (IOException e) {
                log.error("Unable to read file {}/{}",rootEnvDir,fileName);
            }
        });
        File targetFile = new File(targetDirectory, ".env");
        try (FileWriter writer = new FileWriter(targetFile) ) {
            IOUtils.write(allText.toString(), writer);
        } catch (Exception e) {
            log.error("Failed to write to {}",targetFile);
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
            final SystemService service = installationService.queryService(module);
            if (service.getServiceState() == ServiceState.RUNNING) {
                log.error("Service is running and cannot be deleted! STOP first and then delete.");
                return new BackgroundTaskResult<>(false, -1000);
            }

            try {
                Manifest manifest = getManifest(module.getModulePath());
                if (manifest.getFormat() > 1.0 && manifest.getShLast() != null
                        &&  installationService.moduleContains(module, manifest.getShLast())) { //last-time is only 1.1+
                    executeModuleShScript(module, module.getModulePath(), manifest.getShLast(), ControlScript.LAST_TIME);
                }

                storageService.deleteDirectoryAndContents(module.getModulePath());
                module.setModuleState(ModuleState.DOWNLOADED);

                module.setActive(false);
                installationService.removeService(service);

                File vHostDir = new File(vhostDirectory);
                final File[] files = vHostDir.listFiles(file -> file.getName().contains(module.getName() + "." + module.getStartPriority()));

                for (File file : files) {
                    log.info("Deleting {} from vhost directory",file);
                    boolean d = file.delete();
                    if (!d) {
                        log.error("Failed to delete file: {} - problem in installation and re-install",file);
                    }
                }

                log.info("Module {} has been removed and service deleted", module.getName());
                return new BackgroundTaskResult<>(true, 0, module.getName());
            } catch (IOException e) {
                log.error("Exception removing module {} with error {}",module.getName(), e.getMessage(),e);
            }
            return new BackgroundTaskResult<>(false, -1, module.getName());
        });
    }


    public void scheduleStartActiveModules() {
        addTask(() -> {

            //we assume that following the guide, start is pressed after installation is complete.
            SystemInstallationState state = installationService.getState();
            state.setInstallationState(InstallationState.COMPLETED);
            state = installationService.updateSystemInstallationState(state);

            final List<BackgroundTaskResult<Boolean, Integer>> results = installationService.queryAllModulesInstalled(true)
                    .stream()
                    .filter(m -> m.getActive())
                    .map(module -> {
                SystemService service = installationService.queryService(module);
                log.info("Starting service {} id {} [linked to id {}, active {}] from Module id {}",
                        service.getName(), service.getId(), service.getModule().getId(), service.getModule().getActive(), module.getId());
                if (service == null) {
                    log.error("Module {} has no service!",module.getName());
                } else if (service.getStartable() == true) {

                    //copy the environment again before starting
                    try {
                        copyEnvironment(module.getModulePath());
                        log.info("Environment merged");

                        service = checkOAMAgentCreation(module,service);

                        service = checkVHostCreation(module,service);

                    } catch (IOException e) {
                        log.error("Failed to copy ENVIRONMENT! {}", e.getMessage(),e);
                    }
                    if (service.getServiceState() == ServiceState.NOT_RUNNING) {
                        return startSingleService(module, service);
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
            results.stream().filter( r -> !r.getSuccess()).forEach(failed -> {
                log.error("Service {} failed to start, error code {}", failed.getModuleName(), failed.getErrorCode());
                if (finalResult.getSuccess()) {
                    finalResult.setSuccess(false);
                }
            });
            return finalResult;
        });
    }

    private BackgroundTaskResult<Boolean, Integer> startSingleService(SystemModule module, SystemService service) {
        log.info("About to start service {} with start priority {}", service.getName(), module.getStartPriority());
        if (service.getServiceState()==ServiceState.RUNNING) {
            log.warn("Service {} is already running! - did not start it",service.getName());
            return new BackgroundTaskResult<>(true, 0, module.getName());
        }

        if (installationService.moduleContains(module, DOCKERCOMPOSE_YML_TEMPLATE)) {
            List<String> domains = parseDomainsFromEnv(module);
            log.info("Module {} has a j2 template, now will populate with {}", module.getName(), domains);
            fixComposeWithDomains(module, domains);
        }

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
                installationService.updateServiceState(service, ServiceState.RUNNING);

            } else {
                log.error("Executing the start script was not successful for service {}", module.getName());
            }

            // set the name
            result.setModuleName(module.getName());

            return result;
        } catch (IOException e) {
            log.error("Failed to start {} with start priority {}", service.getName(), module.getStartPriority());
            log.error("Exception was {}", e.getMessage(), e);
            installationService.updateServiceState(service, ServiceState.NOT_RUNNING);
        }

        return new BackgroundTaskResult<Boolean, Integer>(false, -100, module.getName());
    }

    private void fixComposeWithDomains(SystemModule module, List<String> domains) {

        try {
            Map<String,String> env = new HashMap<>();
            env.put("J2_TEMPLATE", DOCKERCOMPOSE_YML_TEMPLATE);
            env.put("DATA_JSON",writeDomainsToJson(domains,module.getModulePath()));
            env.put("J2_OUTPUT","docker-compose.yml");
            env.put("WORK_DIR",module.getModulePath());

            if (!executeScriptSimple("execComposeJ2.sh", env).getSuccess()) {
                throw new IOException("Failed to complete J2 template execution for this docker-compose.yml template! module "+module);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String writeDomainsToJson(List<String> domains, String modulePath) throws IOException {
        File f = new File(modulePath, "j2data"+Long.toHexString(System.currentTimeMillis())+".json");
        StringBuilder formatted = new StringBuilder(" { \"local_domains\" : [");

        domains.forEach( d -> {
            formatted.append("\"").append(d).append("\",");
        });
        formatted.setLength(formatted.length()-1); //leave the last ,
        formatted.append(" ] } ");


        Files.write(f.toPath(), formatted.toString().getBytes(Charset.forName("UTF-8")));
        return f.getAbsolutePath();

    }

    private List<String> parseDomainsFromEnv(SystemModule module) {
        List<String> domains = new ArrayList<>();
        File envFile = new File(module.getModulePath(), ".env");

        try {
            domains.addAll(Files.readAllLines(envFile.toPath()).stream()
                    .filter(line -> line.contains("_LOCAL"))
                    .map(line -> line.split(Pattern.quote("="))[0]).distinct()
                    .collect(Collectors.toList()));
            log.info("Domains discovered: {}",domains);
        } catch (IOException e) {
            log.error("Unable to parse domains from list, error {}",e.getMessage(),e);
        }
        return domains;
    }

    private SystemService checkVHostCreation(SystemModule module, SystemService service) {
        //we copy the vhost configuration from the $HOME folder to the apache folders
        if (service.getVHostNecessary() && service.getVhostCreated() == null) {
            String confFileName = "csp-sites." + module.getName() + "." + module.getStartPriority() + ".conf";
            File rootEnvDir = new File(System.getProperty("user.home"));
            File confFile = new File(rootEnvDir, confFileName);
            File outFile = new File(vhostDirectory, confFileName);
            if (confFile.exists()) {
                try (Reader input = new FileReader(confFile); Writer output = new FileWriter(outFile)) {
                    IOUtils.copy(input, output);
                    service.setVhostCreated(LocalDateTime.now());
                    service = installationService.updateSystemService(service);
                    log.info("Service {} vHost configuration copied to apache", service.getName());
                } catch (IOException ioe) {
                    log.error("Failed to copy {}",ioe.getMessage(),ioe);
                }
            } else {
                log.error("Module {}: site configuration (vhost) does not exist in location {}",
                        module.getName(), confFile.getAbsolutePath());
            }
        }
        return service;
    }

    private SystemService checkOAMAgentCreation(SystemModule module, SystemService service) throws IOException {
        final SystemModule moduleOAM = installationService.queryModuleByName(moduleOAMname, true);
        if (moduleOAM == null) {
            log.error("SYSTEM ERROR; Module with name {} is not present in this installation!",moduleOAMname);
        }
        final SystemModule moduleAPC = installationService.queryModuleByName(moduleAPCname, true);
        if (moduleAPC == null) {
            log.error("SYSTEM ERROR; Module with name {} is not present in this installation!",moduleAPCname);
        }
        BackgroundTaskResult<Boolean, Integer> oamStarted = null;
        if (service.getOamAgentNecessary() && service.getOamAgentCreated() == null) {
            // we need to have OAM running and APACHE for this.
            // due to start priority there are the following cases:
            // 1. this is OAM now - needs an agent and it has not started
            //    ->> we need to start it and proceed
            // 2. this is NOT OAM but OAM has not yet started
            //    ->> we need to start it and proceed
            // 3. this is NOT OAM and OAM is running
            //    ->> happy path do nothing :D
            // same for apache - to create the registration for the agent,
            // we need to have apache running. This is simpler, it is an "on-off"
            if (service.getName().contentEquals(moduleOAMname)) { // case 1
                log.info("1: OAM - registering own web agent");
                //we need to start OAM to register OWN agent.
                oamStarted = startSingleService(module, installationService.queryService(moduleOAM));
            } else if (!service.getName().contentEquals(moduleOAMname)) {
                boolean oamRunning = installationService.queryService(moduleOAM).getServiceState()==ServiceState.RUNNING;
                if (!oamRunning) { // 2
                    log.info("2: OAM - registering other web agent, but not yet running");
                    oamStarted = startSingleService(moduleOAM, installationService.queryService(moduleOAM));
                } else { //3
                    log.info("3: OAM - registering other web agent, running already");
                }
            }
            BackgroundTaskResult<Boolean, Integer> rOAM = null;
            BackgroundTaskResult<Boolean, Integer> rAPC = null;

            // multiple agents and apache registrations are needed?
            boolean totalSuccess = true;
            String[] serviceNames = service.getModule().getExternalName().split(Pattern.quote("|"));
            for (String serviceName : serviceNames) {
                if (installationService.queryService(moduleOAM).getServiceState()==ServiceState.RUNNING) {
                    // so lets register the agent now
                    Map<String, String> envOAM = new HashMap<>();
                    envOAM.put("C_NAME", "csp-" + moduleOAMname);
                    envOAM.put("C_SCRIPT", "create-agent.sh");
                    envOAM.put("C_EXTNAME", serviceName);

                    rOAM = executeScriptSimple(EXEC_CONT_SCRIPT_SH, envOAM);
                    //start apache if not started
                    boolean apacheStarted = true; //assume it is already started.
                    if (installationService.queryService(moduleAPC).getServiceState()==ServiceState.NOT_RUNNING) {
                        apacheStarted = startSingleService(moduleAPC, installationService.queryService(moduleAPC)).getSuccess();
                    }

                    if (apacheStarted) {
                        Map<String, String> envAPC = new HashMap<>();
                        envAPC.put("C_NAME", "csp-" + moduleAPCname);
                        envAPC.put("C_SCRIPT", "create-agent.sh");
                        envAPC.put("C_EXTNAME", serviceName);
                        rAPC = executeScriptSimple(EXEC_CONT_SCRIPT_SH, envAPC);

                        stopSingleService(moduleAPC, installationService.queryService(moduleAPC));

                    } else {
                        log.error("Apache is not running? failure!!!");
                    }
                    if (rOAM.getSuccess() && rAPC.getSuccess()) {
                        totalSuccess &= rOAM.getSuccess();
                    } else {
                        totalSuccess = false;
                    }
                } else {
                    log.error("OAM IS NOT RUNNING - failure! for service {}",service.getName());
                }
            }
            if (totalSuccess) {
                log.info("Saving OAM creation date for service {}",service.getName());
                service.setOamAgentCreated(LocalDateTime.now());
                service = installationService.updateSystemService(service);
            }

            log.info("ACTIONS COMPLETED: OAM : {} - APC : {} for service {}",
                    rOAM != null ? rOAM.getSuccess() : false,
                    rAPC != null ? rAPC.getSuccess() : false, service.getName());


        } else {
            log.info("Service {}. No action - either already created or not needed",service.getName());
        }
        return service;
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
            installationService.queryAllModulesInstalled(false)
                .stream()
                .filter( m -> m.getActive())
                .forEach(module -> {
                SystemService service = installationService.queryService(module);
                stopSingleService(module, service);
            });
            return new BackgroundTaskResult<>(true, 0);
        });
    }

    private void stopSingleService(SystemModule module, SystemService service) {
        if (service != null) {
            if (service.getStartable() == true) {
                if (service.getServiceState() == ServiceState.RUNNING) {
                    log.info("About to stop service {} with start priority {}", service.getName(), module.getStartPriority());
                    Map<String, String> env = new HashMap<String, String>();
                    env.put("SERVICE_NAME", module.getName());
                    env.put("SERVICE_DIR", module.getModulePath());
                    env.put("SERVICE_PRIO", module.getStartPriority().toString());
                    try {
                        executeScriptSimple("stopService.sh", env);
                        service = installationService.updateServiceState(service, ServiceState.NOT_RUNNING);
                    } catch (IOException e) {
                        log.error("Failed to start {} with start priority {}", service.getName(), module.getStartPriority());
                        log.error("Exception was {}", e.getMessage(), e);
                        service = installationService.updateServiceState(service, ServiceState.RUNNING);
                    }
                } else {
                    log.warn("Service {} is marked NOT running!???", service.getName());
                }
                log.info("Service {} state {}", service.getName(), service.getServiceState());
            } else {
                log.info("Service {} is not a startable service, moving on", service.getName());
            }
        }
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
        log.info("Environment for execution: {}", envVars);
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

        long total = FileHelper.copy(script.getInputStream(), targetFile.toPath(), null, StandardCopyOption.REPLACE_EXISTING);
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