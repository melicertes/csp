package com.intrasoft.csp.conf.server.utils;


import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


public class FileHelper {

    /**
     * Size of the buffer to read/write data
     */
    private static final int BUFFER_SIZE = 4096;

    public static void removeFile(String filePath, String fileName) throws IOException {
        File f = new File(filePath + fileName);
        Path path = Paths.get(f.getAbsolutePath());
        Files.delete(path);
    }

    public static String getFileFromHash(String filePath, String hash) throws FileNotFoundException {
        File folder = new File(filePath);
        File[] files = folder.listFiles();
        String fileName;
        int lastPeriodPos;
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                fileName = files[i].getName();
                lastPeriodPos = fileName.lastIndexOf('.');
                if (lastPeriodPos > 0 && fileName.substring(0, lastPeriodPos).equals(hash)) {
                    return fileName;
                }
            }
        }

        return null;
    }

    public static String saveUploadedFile(String fileTemp, String fileRepository, MultipartFile file, String digestAlgorithm) throws IOException, NoSuchAlgorithmException {
        /*
        Create temp directory if does not exist
         */
        createDirectory(fileTemp);

        /*
        Create data directory of does not exist
         */
        createDirectory(fileRepository);

        /*
        Save file to temp
         */
        final File f = new File(fileTemp + file.getOriginalFilename());
        final String hash; // to be initialized further down

        try (InputStream inputStream = file.getInputStream();
             DigestOutputStream outputStream = new DigestOutputStream(
                     new FileOutputStream(f.getAbsoluteFile()),
                     MessageDigest.getInstance(digestAlgorithm)) ) {
            IOUtils.copy(inputStream, outputStream );
            // copy is done, digest is ready.
            hash = Hex.encodeHexString(outputStream.getMessageDigest().digest());
        }

        //rename file within temp
        CopyOption[] options = new CopyOption[]{
                StandardCopyOption.REPLACE_EXISTING,
                StandardCopyOption.COPY_ATTRIBUTES
        };
        File target = new File(fileTemp + hash + "." + FilenameUtils.getExtension(f.getAbsolutePath()));
        Path FROM = Paths.get(f.getAbsolutePath());
        Path TO = Paths.get(target.getAbsolutePath());
        Files.copy(FROM, TO, options);
        Files.delete(FROM);

        return hash;
    }

    public static void copyFromTempToRepo(String fileTemp, String fileRepository, String hash) throws IOException {
        String fileName = FileHelper.getFileFromHash(fileTemp, hash);

        /*
        Clean up files
         */
        //overwrite existing file, if exists
        CopyOption[] options = new CopyOption[]{
                StandardCopyOption.REPLACE_EXISTING,
                StandardCopyOption.COPY_ATTRIBUTES
        };
        Path FROM = Paths.get(fileTemp + fileName);
        Path TO = Paths.get(fileRepository + fileName);
        Files.copy(FROM, TO, options);
        Files.delete(FROM);
    }

    /**
     * Extracts a zip file specified by the zipFilePath inside a timestamp-based temp directory specified by destDirectory.
     * Path containing zip contents is returned
     * @param zipFilePath {@link String}
     * @param destDirectory {@link String}
     * @throws IOException
     * @return String
     */
    public static String unzip(String zipFilePath, String destDirectory) throws IOException {
        //create a temp timestamp-based folder inside directory to unzip
        if (destDirectory.charAt(destDirectory.length()-1) != File.separator.charAt(0) )  {
            destDirectory += File.separator;
        }
        String t = Long.toString(System.currentTimeMillis()).replaceAll(";", "");


        destDirectory = destDirectory + t + File.separator;

        File destDir = new File(destDirectory);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
        ZipEntry entry = zipIn.getNextEntry();
        // iterates over entries in the zip file
        while (entry != null) {
            String filePath = destDirectory + File.separator + entry.getName();
            if (!entry.isDirectory()) {
                // if the entry is a file, extracts it
                extractFile(zipIn, filePath);
            } else {
                // if the entry is a directory, make the directory
                File dir = new File(filePath);
                dir.mkdir();
            }
            zipIn.closeEntry();
            entry = zipIn.getNextEntry();
        }
        zipIn.close();

        return destDirectory;
    }

    /**
     * Check if file exists, case insensitive
     */
    public static boolean exists(String dir, String filename){
        String[] files = (new File(dir)).list();
        if (files == null) return false;
        for(String file : files)
            if(file.toLowerCase().equals(filename.toLowerCase()))
                return true;
        return false;
    }

    /**
     * Extracts a zip entry (file entry)
     * @param zipIn zip {@link InputStream}
     * @param filePath {@link String}
     * @throws IOException
     */
    private static void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[BUFFER_SIZE];
        int read = 0;
        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
        bos.close();
    }

    private static void createDirectory(String directory) throws IOException {
        File tempDir = new File(directory);
        if (!tempDir.exists()) {
            try{
                tempDir.mkdirs();
            }
            catch(SecurityException se){
                throw new IOException();
            }
        }
    }
}
