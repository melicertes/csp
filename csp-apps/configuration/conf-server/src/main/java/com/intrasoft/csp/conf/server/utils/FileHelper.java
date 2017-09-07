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

public class FileHelper {

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
        Save file from Multipart to temp **using streams for >600mb files**
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
            hash = Hex.encodeHexString(
                    outputStream.getMessageDigest().digest() );
        }
        /*
        Calculate hash
         */
        //String hash = hashFile(digestAlgorithm, f.getAbsolutePath());

        /*
        Clean up files
         */
        //overwrite existing file, if exists
        CopyOption[] options = new CopyOption[]{
                StandardCopyOption.REPLACE_EXISTING,
                StandardCopyOption.COPY_ATTRIBUTES
        };
        File target = new File(fileRepository + hash + "." + FilenameUtils.getExtension(f.getAbsolutePath()));
        Path FROM = Paths.get(f.getAbsolutePath());

        Path TO = Paths.get(target.getAbsolutePath());
        Files.copy(FROM, TO, options);
        Files.delete(FROM);

        return hash;
    }

//    private static String hashFile(String digestAlgorithm, String filePath) throws NoSuchAlgorithmException, IOException {
//        MessageDigest md = MessageDigest.getInstance(digestAlgorithm);
//        FileInputStream fis = new FileInputStream(filePath);
//
//        byte[] dataBytes = new byte[1024];
//
//        int nread = 0;
//        while ((nread = fis.read(dataBytes)) != -1) {
//            md.update(dataBytes, 0, nread);
//        };
//        byte[] mdbytes = md.digest();
//        fis.close();
//
//        //convert the byte to hex format
//        StringBuffer hexString = new StringBuffer();
//        for (int i=0;i<mdbytes.length;i++) {
//            hexString.append(Integer.toHexString(0xFF & mdbytes[i]));
//        }
//
//        return hexString.toString();
//    }

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
