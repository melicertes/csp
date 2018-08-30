package com.intrasoft.csp.anon.server.utils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class CryptoPAN {
    public static class Mask {
        public long mask;
        public long pad;

        public Mask(long mask, long pad) {
            this.mask = mask;
            this.pad = pad;
        }
    }

    private Cipher cipher;
    private Mask[] masks;
    private String aesKey;
    private byte[] pad;

    public CryptoPAN(String key) {
        if (key.length() != 32) {
            throw new IllegalArgumentException("key must me a 32 byte long string");
        }

        this.aesKey = key.substring(0, 16);

        SecretKeySpec keyspec = new SecretKeySpec(aesKey.getBytes(), "AES");
        try {
            cipher = javax.crypto.Cipher.getInstance("AES/CBC/NoPadding");
            cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, keyspec, new IvParameterSpec(new byte[16]));
            this.pad = cipher.doFinal(key.substring(16).getBytes());
            byte[] f4 = Arrays.copyOf(this.pad, 4);
            long f4bp = toInt(f4);

            this.masks = new Mask[32];
            for (int p = 0; p < 32; ++p) {
                long mask = 0xFFFFFFFFL >> (32 - p) << (32 - p);
                masks[p] = new Mask(mask, f4bp & (~mask));
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }

    }

    private long toInt(byte[] arr) {
        long l0 = ((long) arr[0] & 0xffL) << (8 * (3 - 0));
        long l1 = ((long) arr[1] & 0xffL) << (8 * (3 - 1));
        long l2 = ((long) arr[2] & 0xffL) << (8 * (3 - 2));
        long l3 = ((long) arr[3] & 0xffL) << (8 * (3 - 3));

        return l0 | l1 | l2 | l3;
    }

    public int[] anonymizei(String ip) {
        long result = 0;
        byte[] addria = new byte[4];
        {
            int i = 0;

            for (String a : ip.split("\\.")) {
                addria[i++] = (byte) Integer.parseInt(a.trim());
            }
            if (i != 4)
                throw new IllegalArgumentException("Invalid IPv4 address");
        }
        long addri = toInt(addria);

        long[] addresses = new long[32];
        for (int i = 0; i < this.masks.length; ++i) {
            addresses[i] = (addri & masks[i].mask) | masks[i].pad;
        }
        int[] calcResult = new int[32];
        try {
            for (int i = 0; i < 32; ++i) {
                calcResult[i] = calc(addresses[i]);
            }

            result = 0;
            for (int i = 0; i < 32; ++i) {
                result = (result << 1) | calcResult[i];
            }

            byte[] rarr = toArray(result ^ addri);
            int[] iresult = new int[4];
            iresult[0] = ((int) rarr[0]) & 0xff;
            iresult[1] = ((int) rarr[1]) & 0xff;
            iresult[2] = ((int) rarr[2]) & 0xff;
            iresult[3] = ((int) rarr[3]) & 0xff;
            return iresult;
        } catch (IllegalBlockSizeException e) {
            throw new IllegalStateException(e);
        } catch (BadPaddingException e) {
            throw new IllegalStateException(e);
        }
    }

    public String anonymize(String ip) {
        int[] result = anonymizei(ip);
        StringBuilder sb = new StringBuilder();
        sb.append(result[0]);
        sb.append(".");
        sb.append(result[1]);
        sb.append(".");
        sb.append(result[2]);
        sb.append(".");
        sb.append(result[3]);
        return sb.toString();
    }

    private byte[] toArray(long n) {
        byte[] result = new byte[4];
        for (int i = 3; i > -1; --i) {
            result[3 - i] = (byte) (n >> (i * 8) & 0xFF);
        }
        return result;
    }

    // calculate the first bit for Crypto-PAN
    private int calc(long a) throws IllegalBlockSizeException, BadPaddingException {
        pad[3] = (byte) (a & 0xFF);
        a >>= 8;
        pad[2] = (byte) (a & 0xFF);
        a >>= 8;
        pad[1] = (byte) (a & 0xFF);
        a >>= 8;
        pad[0] = (byte) (a & 0xFF);

        byte[] doFinal = cipher.doFinal(pad);

        return doFinal[0] < 0 ? 1 : 0;
    }

    public static void main(String[] args) throws InterruptedException {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 32; ++i) {
            sb.append((char) i);
        }
        CryptoPAN c = new CryptoPAN(sb.toString());
        System.out.println(c.anonymize("192.0.2.2"));
        System.out.println(c.anonymize("192.0.2.1"));
        System.out.println(c.anonymize("192.0.3.1"));
        System.out.println(c.anonymize("192.1.2.1"));

        System.out.println(new CryptoPAN(sb.toString()).anonymize("192.0.2.1"));

        System.out.println("2.90.93.17".equals(c.anonymize("192.0.2.1")));
        long started = System.currentTimeMillis();
        for (int i = 0; i < 50000; ++i) {
            c.anonymize("192.0.2.1");
        }
        System.out.println("elapsed: " + (System.currentTimeMillis() - started));
    }
}
