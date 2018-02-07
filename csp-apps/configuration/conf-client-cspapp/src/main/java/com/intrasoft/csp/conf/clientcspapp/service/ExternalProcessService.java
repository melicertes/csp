package com.intrasoft.csp.conf.clientcspapp.service;

import com.intrasoft.csp.conf.clientcspapp.model.LoggingEvent;
import com.intrasoft.csp.conf.clientcspapp.repo.LoggingEventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.stream.Collector;

/**
 * Created by tangelatos on 10/09/2017.
 */

@Service
@Slf4j
public class ExternalProcessService {

    public static final int EXEC_ERROR = Integer.MIN_VALUE;
    private ExecutorService threadPool = Executors.newCachedThreadPool();


    @Autowired
    LoggingEventRepository logRepository;


    @Transactional
    public List<LoggingEvent> getLastEntries(int lastEntries) {
        return logRepository.findAll(new PageRequest(0, lastEntries, new Sort(Sort.Direction.DESC, "timestamp"))).getContent();
    }


    public static <T> Collector<T, ?, List<T>> lastN(int n) {
        return Collector.<T, Deque<T>, List<T>>of(ArrayDeque::new, (acc, t) -> {
            if(acc.size() == n) {
                acc.pollFirst();
            }
            acc.add(t);
        }, (acc1, acc2) -> {
            while(acc2.size() < n && !acc1.isEmpty()) {
                acc2.addFirst(acc1.pollLast());
            }
            return acc2;
        }, ArrayList<T>::new);
    }


    public int executeExternalProcess(String workingDirectory, Optional<Map<String,String>> environment, String... processAndArguments) {

        final ProcessBuilder builder = new ProcessBuilder();

        builder.directory(new File(workingDirectory));
        builder.command(processAndArguments);

        if (environment.isPresent()) {
            final Map<String, String> envFinal = environment.orElse(new HashMap<>());
            log.info("EXEC: Additional environment variables defined: "+ envFinal);
            final Map<String, String> env = builder.environment();
            env.putAll(envFinal);
        }

        final Process proc;
        try {
            proc = builder.start();
            log.info("EXEC: "+Arrays.toString(processAndArguments)+" START");
        } catch (IOException e) {
            log.error("Process {} failed to start!", processAndArguments);
            return EXEC_ERROR;
        }
        // we should be ready.
        try  {
            final StreamGobbler inputGobbler = new StreamGobbler(proc.getInputStream(), log::info );
            final StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(), log::error );
            threadPool.submit(inputGobbler);
            threadPool.submit(errorGobbler);

            int exitCode = proc.waitFor();
            log.info("EXEC: "+Arrays.toString(processAndArguments)+" END | CODE: "+exitCode);
            return exitCode;
        } catch (InterruptedException e) {
            log.error("Process {} failed in execution: {}", processAndArguments, e.getMessage());
            log.error("Exception",e);
        }
        return EXEC_ERROR;
    }



    private static class StreamGobbler implements Runnable {
        private InputStream inputStream;
        private Consumer<String> consumer;

        public StreamGobbler(InputStream inputStream, Consumer<String> consumer) {
            this.inputStream = inputStream;
            this.consumer = consumer;
        }

        @Override
        public void run() {
            new BufferedReader(new InputStreamReader(inputStream)).lines()
                    .forEach(consumer);
        }
    }
}
