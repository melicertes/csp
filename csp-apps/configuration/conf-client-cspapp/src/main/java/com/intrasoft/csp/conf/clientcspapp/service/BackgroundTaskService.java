//package com.intrasoft.csp.conf.clientcspapp.service;
//
//import com.intrasoft.csp.conf.clientcspapp.model.BackgroundTask;
//import com.intrasoft.csp.conf.clientcspapp.model.BackgroundTaskResult;
//import lombok.SneakyThrows;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.ArrayBlockingQueue;
//
///**
// * Created by tangelatos on 06/09/2017.
// */
//@Service
//@Slf4j
//public class BackgroundTaskService {
//
//    private ArrayBlockingQueue<BackgroundTask> backgroundTasks =
//            new ArrayBlockingQueue<>(20);
//
//    private Map<BackgroundTask, BackgroundTaskResult> completedTasks = new HashMap<>();
//
//
//    @SneakyThrows
//    public <S, R> void addTask(BackgroundTask<S, R> task) {
//        while (!backgroundTasks.offer(task)) {
//            log.warn("Task {} was not submitted at this time, capacity full. Waiting 200ms", task);
//            Thread.sleep(200);
//        }
//    }
//
//    public <S, R> BackgroundTaskResult<S, R> retrieveResult(BackgroundTask<S, R> task) {
//        return completedTasks.get(task);
//    }
//
//    @Scheduled(fixedDelay = 10000, initialDelay = 30000)
//    public void pollAndProcessTasks() {
//        if (backgroundTasks.size() == 0) {
//            log.debug("Nothing to execute, sleeping....");
//        } else {
//            List<BackgroundTask> toDo = new ArrayList<>(5);
//            backgroundTasks.drainTo(toDo);
//            log.info("Found {} tasks to execute in the background",toDo.size());
//            for (BackgroundTask t : toDo) {
//                log.info("Executing {}",t);
//                final BackgroundTaskResult result = t.execute();
//                log.info("Completed, result is {}", result);
//                completedTasks.put(t,result);
//            }
//        }
//
//    }
//
//}
