package com.viettel.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import org.apache.log4j.Logger;

public class Utils {
    public static void copy(File source, File target)
            throws FileNotFoundException, IOException {
        copy(source, target, new byte[1024 * 1024]);
    }

    private static void copy(File source, File target, byte[] buffer)
            throws FileNotFoundException, IOException {
        InputStream in = new FileInputStream(source);
        // create parent directory of target-file if necessary:
        File parent = target.getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }
        if (target.isDirectory()) {
            target = new File(target, source.getName());
        }
        OutputStream out = new FileOutputStream(target);
        int read;
        try {
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
        } catch (IOException e) {
            throw e;
        } finally {
            in.close();
            out.close();
        }
    }

    public static void downloadFile(String fileRbt, String fileLocal) {
        BufferedInputStream in = null;
        FileOutputStream fout = null;
        try {
            in = new BufferedInputStream(new URL(fileRbt).openStream());
            fout = new FileOutputStream(fileLocal);

            byte data[] = new byte[1024];
            int count;
            while ((count = in.read(data, 0, 1024)) != -1) {
                fout.write(data, 0, count);
            }
        } catch (IOException ex) {           
            MyLog.Error(ex.getMessage());
        } finally {
            if (in != null || fout != null) {
                try {
                    in.close();
                    fout.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private static String processString(String url) {
        String filename = null;
        String[] part = url.split("//");
        int i = part.length;
        filename = part[i - 1];
        return filename;
    }

    public static String formatMsisdn(String msisdn) {
        String ret = msisdn;
        if (null == msisdn) {
            return "";
        }
        if (msisdn.startsWith("0")) {
            ret = msisdn.substring(1);
        }
        if (msisdn.startsWith("84")) {
            ret = msisdn.substring(2);
        }
        return ret;
    }

    public static String fmtString(String str) {
        String response = str;
        response = response.trim().replace("'", "").replace("  ", "").replace(" ", "");
        return response;
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub
//        String str = "  ab    c d ";
//        String resp = fmtString(str);
//        System.out.println("str=" + resp);
//        System.out.println("length=" + resp.length());
    }
}
