package com.intrasoft.csp.conf.clientcspapp.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.CountingInputStream;
import org.apache.commons.compress.utils.CountingOutputStream;

import java.io.*;
import java.nio.file.CopyOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;

/**
 * Created by tangelatos on 12/09/2017.
 */
@Slf4j
public final class FileHelper {

    static final int BUFFER_SIZE=64*1024;
    static final int CALLBACK_SIZE=10*1024*1024; // every 10 MB downloaded


    public static String bytesToKB(long bytesWritten) {
        return ((int) (bytesWritten/1024.0) ) + "KB";
    }

    /**
     * callback to implement status updates for long copy operations
     */
    public interface ProgressCallBack {
        void callback(CountingInputStream input, CountingOutputStream output);
    }

    public static long copy(InputStream in, Path out, ProgressCallBack callBack, CopyOption... options) throws IOException {

        boolean overwrite = Arrays.stream(options).anyMatch(o -> o == StandardCopyOption.REPLACE_EXISTING);

        try (CountingInputStream input = new CountingInputStream(in);
             CountingOutputStream output = new CountingOutputStream(
                     new BufferedOutputStream(new FileOutputStream(out.toFile(),
                        overwrite), BUFFER_SIZE) ) ) {

            long nread = 0L;
            long lastWrite = 0L;

            byte[] buf = new byte[BUFFER_SIZE];
            int n;
            while ((n = input.read(buf)) > 0) {
                output.write(buf, 0, n);
                nread += n;

                if (callBack != null) // check and callback for progress
                    if (nread - lastWrite > CALLBACK_SIZE) {
                        callBack.callback(input,output);
                        lastWrite = nread;
                    }
            }
            output.flush();
            if (callBack != null) { // final callback before closing of streams
                callBack.callback(input,output);
            }
            return nread;

        }
    }


    public static long copy(Path from, Path to) throws IOException {
        return copy(new FileInputStream(from.toFile()), to, null, StandardCopyOption.REPLACE_EXISTING);
    }


}
