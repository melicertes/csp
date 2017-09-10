package com.intrasoft.csp.conf.clientcspapp.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.stream.Collector;
import java.util.stream.Stream;

/**
 * Created by tangelatos on 10/09/2017.
 */

@Service
@Slf4j
public class ExternalProcessService {

    @Value("${client.ui.maxLinesInMemory:500000}")
    Integer maxLinesInMemory;

    public static final int EXEC_ERROR = Integer.MIN_VALUE;
    private ExecutorService threadPool = Executors.newCachedThreadPool();

    /**
     * implement a size-limited list of entries
     */
    private List<String> logEntries = new LinkedList<String>() {
        @Override
        public boolean add(String o) {
            boolean added = super.add(o);
            while (added && size() > maxLinesInMemory) {
                super.remove();
            }
            return added;
        }
    };

    public Stream<String> getAllLogEntries() {
        return logEntries.stream();
    }

    public List<String> getLastEntries(int lastEntries) {
        return getAllLogEntries().collect(lastN(lastEntries));
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
            final Map<String, String> env = builder.environment();
            env.putAll(environment.get());
        }
        logLine("EXEC: Additional environment variables defined: "+environment.orElse(new HashMap<>()));

        final Process proc;
        try {
            proc = builder.start();
            logLine("EXEC: "+Arrays.toString(processAndArguments)+" START");
        } catch (IOException e) {
            log.error("Process {} failed to start!", processAndArguments);
            return EXEC_ERROR;
        }
        // we should be ready.
        try  {
            final StreamGobbler inputGobbler = new StreamGobbler(proc.getInputStream(), this::logLine );
            final StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(), this::logLine );
            threadPool.submit(inputGobbler);
            threadPool.submit(errorGobbler);

            int exitCode = proc.waitFor();
            logLine("EXEC: "+Arrays.toString(processAndArguments)+" END | CODE: "+exitCode);
            return exitCode;
        } catch (InterruptedException e) {
            log.error("Process {} failed in execution: {}", processAndArguments, e.getMessage());
            log.error("Exception",e);
        }
        return EXEC_ERROR;
    }



    private void logLine(String line) {
        logEntries.add(TimeHelper.localNow() + " " + line);
        log.info(line);
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
