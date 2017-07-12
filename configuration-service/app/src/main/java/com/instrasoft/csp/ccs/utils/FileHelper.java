package com.instrasoft.csp.ccs.utils;


import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FileHelper {

    public static String saveUploadedFile(String fileTemp, String fileRepository, MultipartFile file, String digestAlgorithm) throws IOException, NoSuchAlgorithmException {
        /*
        Save file to temp
         */
        byte[] bytes = file.getBytes();
        File f = new File(fileTemp + file.getOriginalFilename());
        Path path = Paths.get(f.getAbsolutePath());
        Files.write(path, bytes);

        /*
        Calculate hash
         */
        String hash = hashFile(digestAlgorithm, f.getAbsolutePath());

        /*
        Clean up files
         */
        /**
         * @TODO What is file has the same hash? Can it happen?? Hash Unique
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

    private static String hashFile(String digestAlgorithm, String filePath) throws NoSuchAlgorithmException, IOException {
        MessageDigest md = MessageDigest.getInstance(digestAlgorithm);
        FileInputStream fis = new FileInputStream(filePath);

        byte[] dataBytes = new byte[1024];

        int nread = 0;
        while ((nread = fis.read(dataBytes)) != -1) {
            md.update(dataBytes, 0, nread);
        };
        byte[] mdbytes = md.digest();
        fis.close();

        //convert the byte to hex format
        StringBuffer hexString = new StringBuffer();
        for (int i=0;i<mdbytes.length;i++) {
            hexString.append(Integer.toHexString(0xFF & mdbytes[i]));
        }

        return hexString.toString();
    }
}
