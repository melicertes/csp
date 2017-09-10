package com.intrasoft.csp.conf.clientcspapp.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

/**
 * Created by tangelatos on 10/09/2017.
 */
@Service
@Slf4j
public class SimpleStorageService {

    @Value("${installation.temp.directory}")
    private String tempDirectory;

    public String storeFileTemporarily(InputStream stream, String name) throws IOException {
        File target = new File(tempDirectory, name);
        if (target.exists()) {
            log.warn("Target {} existed, will remove",target.getName());
            target.delete();
        }

        Files.copy(stream, target.toPath());
        log.info("Saved {} (size: {} bytes)", target.getName(), target.length());
        return target.getAbsolutePath();
    }



}
