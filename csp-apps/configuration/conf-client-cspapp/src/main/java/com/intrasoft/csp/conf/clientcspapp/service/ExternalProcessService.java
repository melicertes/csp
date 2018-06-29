package com.intrasoft.csp.conf.clientcspapp.service;

import com.intrasoft.csp.conf.clientcspapp.model.LoggingEvent;
import com.intrasoft.csp.conf.clientcspapp.repo.LoggingEventRepository;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
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
import java.util.stream.Stream;

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
        final StreamGobbler inputGobbler = new StreamGobbler(proc.getInputStream(), log::info );
        final StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(), log::error );

        try  {
            threadPool.submit(inputGobbler);
            threadPool.submit(errorGobbler);

            int exitCode = proc.waitFor();
            log.info("EXEC: "+Arrays.toString(processAndArguments)+" END | CODE: "+exitCode);
            return exitCode;
        } catch (InterruptedException e) {
            log.error("Process {} failed in execution: {}", processAndArguments, e.getMessage());
            log.error("Exception",e);
        } finally {
            IOUtils.closeQuietly(inputGobbler.getStream());
            IOUtils.closeQuietly(errorGobbler.getStream());
        }
        return EXEC_ERROR;
    }



    @Getter
    private static class StreamGobbler implements Runnable {
        private InputStream stream;
        private Consumer<String> consumer;

        public StreamGobbler(InputStream inputStream, Consumer<String> consumer) {
            this.stream = inputStream;
            this.consumer = consumer;
        }

        @Override
        public void run() {
            log.debug("StreamGobler started");
            Stream<String> lines = new BufferedReader(new InputStreamReader(stream)).lines();
            lines.forEach(consumer);
            log.debug("StreamGobler complete");
        }
    }
}
