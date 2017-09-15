package com.intrasoft.csp.conf.clientcspapp.service;

import com.intrasoft.csp.conf.clientcspapp.util.FileHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

/**
 * Created by tangelatos on 10/09/2017.
 */
@Service
@Slf4j
public class SimpleStorageService {

    private ExecutorService threadpool = Executors.newCachedThreadPool();

    @Value("${installation.temp.directory}")
    private String tempDirectory;

    public String storeFileTemporarily(InputStream stream, String name) throws IOException {
        File target = new File(tempDirectory, name);
        if (target.exists()) {
            log.warn("Target {} existed, will remove",target.getName());
            target.delete();
        }

        FileHelper.copy(stream, target.toPath(), (input, output) -> log.info("Downloaded : {} ", FileHelper.bytesToKB(output.getBytesWritten())));

        log.info("Saved {} (size: {} bytes)", target.getName(), target.length());
        return target.getAbsolutePath();
    }


    /**
     * extract archive from location to a directory
     * @param archivePath
     * @param directory
     * @return the directory within modules that this has been extracted to
     */
    public String extractArchive(String archivePath, String directory) throws IOException {

        // create a target directory
        File dir = new File(directory);
        dir.mkdirs();
        try (ZipFile zipFile = new ZipFile(archivePath)){
            final Enumeration<ZipArchiveEntry> entries = zipFile.getEntriesInPhysicalOrder();
            while (entries.hasMoreElements()) {
                ZipArchiveEntry entry = entries.nextElement();
                if (entry.getSize() != 0) {
                    //element MAY contain a directory!
                    File targetFile = new File(dir, entry.getName());
                    File containerDir = targetFile.getParentFile();
                    log.info("Parsed as folder {} and file {}",containerDir, targetFile.getName());
                    containerDir.mkdirs();
                    long bytes = FileHelper.copy(zipFile.getInputStream(entry), targetFile.toPath(),
                            (input, output) -> log.info("Extracted : {}", FileHelper.bytesToKB(output.getBytesWritten())));
                    if (bytes == entry.getSize()) {
                        log.info("Extracted : {}, size {} bytes", entry.getName(), entry.getSize());
                    } else {
                        log.warn("Extracted : {}, extracted {} != {} ", entry.getName(), bytes, entry.getSize());
                    }
                } else {
                    log.info("Directory or 0-size file not extracted: {}",entry.getName());
                }
            }
            return directory;
        } catch (IOException e) {
            log.error("Exception while extracting {} - error: {}", archivePath, e.getMessage(),e);
            throw e;
        }
    }

    public Stream<String> filesInArchive(String archivePath) {
        try (ZipFile zipFile = new ZipFile(archivePath)) {
            final Enumeration<ZipArchiveEntry> entries = zipFile.getEntriesInPhysicalOrder();
            List<String> filenames = new ArrayList<>();
            while (entries.hasMoreElements()) {
                filenames.add(entries.nextElement().getName());
            }
            return filenames.stream();
        } catch (IOException e) {
            log.error("File is not archive or could not be opened? error {}", e.getMessage(),e);
            return Stream.empty();
        }
    }

    /**
     * delete directory and it's contents incl. other directories (recursive)
     * @param directory
     * @throws IOException
     */
    public void deleteDirectoryAndContents(String directory) throws IOException {
        Path rootPath = Paths.get(directory);
        Files.walk(rootPath, FileVisitOption.FOLLOW_LINKS)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .peek(System.out::println)
                .forEach(File::delete);
    }
}
