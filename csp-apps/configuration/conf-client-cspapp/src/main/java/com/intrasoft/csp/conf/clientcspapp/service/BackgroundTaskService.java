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

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
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

    @Value("${installation.temp.directory}")
    String tempDirectory;

    @Autowired
    ConfClient client;

    @Autowired
    InstallationService installationService;



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

    public void scheduleDownload(String hash) {

        addTask(() -> {
            if (installationService.canDownload() && internetAvailable) {
                SystemModule module = installationService.findModuleByHash(hash);
                module.setModuleState(ModuleState.DOWNLOADING);
                module = installationService.updateSystemModuleState(module.getId(), ModuleState.DOWNLOADING);
                log.info("Attempting to download {}",hash);
                final SystemInstallationState state = installationService.getState();
                final ResponseEntity entity = client.update(state.getCspId(), hash);
                log.info("Entity for {} received, code {}",hash, entity.getStatusCodeValue());
                try {
                    if (entity.getStatusCodeValue() == HttpStatus.OK.value()) { // we are downloading!
                        Resource resource = (Resource) entity.getBody();
                        // lets copy this directly to a modules location
                        File target = new File(tempDirectory, hash + ".zip");
                        Files.copy(resource.getInputStream(), target.toPath());
                        // update module information
                        module.setModuleState(ModuleState.DOWNLOADED);
                        module.setArchivePath(target.getAbsolutePath());

                        module = installationService.saveSystemModule(module);
                        log.info("File has been received (size: {}) in temporary location for {}",target.length(), hash);
                        return new BackgroundTaskResult<String, Boolean>(hash, true);
                    } else {
                        return new BackgroundTaskResult<String, Boolean>(entity.getStatusCode().toString(),false);
                    }
                } catch (IOException ioe) {
                    log.error("IO exception in download: {}",ioe.getMessage(),ioe);
                    return new BackgroundTaskResult<String, Boolean>(ioe.getMessage(),false);
                }
            } else {
                log.error("Installation is not ready. Module cannot be downloaded {}", hash);
                return new BackgroundTaskResult<String, Boolean>(hash, false);
            }
        });

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