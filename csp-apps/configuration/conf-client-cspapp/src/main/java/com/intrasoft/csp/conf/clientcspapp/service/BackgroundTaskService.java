package com.intrasoft.csp.conf.clientcspapp.service;

import com.intrasoft.csp.conf.client.ConfClient;
import com.intrasoft.csp.conf.clientcspapp.model.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by tangelatos on 06/09/2017.
 */
@Service
@Slf4j
public class BackgroundTaskService {

    private ArrayBlockingQueue<BackgroundTask> backgroundTasks =
            new ArrayBlockingQueue<>(20);

    private Map<BackgroundTask, BackgroundTaskResult> completedTasks = new HashMap<>();

    private Boolean internetAvailable = false;

    @Value("${conf.server.host}")
    String host;

    @Value("${conf.server.port}")
    Integer port;

    @Autowired
    ConfClient client;

    @Autowired
    InstallationService installationService;

    @Autowired
    SimpleStorageService storageService;

    @SneakyThrows
    public <S, R> void addTask(BackgroundTask<S, R> task) {
        while (!backgroundTasks.offer(task)) {
            log.warn("Task {} was not submitted at this time, capacity full. Waiting 200ms", task);
            Thread.sleep(200);
        }
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
                    resetModule(module);
                    log.error("IO exception in download: {}",ioe.getMessage(),ioe);
                    return new BackgroundTaskResult<SystemModule, Boolean>(module,false);
                }
            } else {
                log.error("Installation is not ready. Module cannot be downloaded {}", module.getHash());
                return new BackgroundTaskResult<SystemModule, Boolean>(module, false);
            }
        });

    }

    private void resetModule(SystemModule module) {
        module.setModuleState(ModuleState.UNKNOWN);
        module.setArchivePath(null);
        installationService.saveSystemModule(module);
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