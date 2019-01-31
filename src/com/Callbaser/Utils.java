package com.Callbaser;

public class Utils {

    public static String getFileExtansion(String fileName) {

        String fname = fileName.trim().toUpperCase();
        int i = fname.lastIndexOf('.');
        if (i > 0) {
            return fname.substring(i + 1);
        }
        return "";
    }

    public static boolean isPDF(String fname) {
        return Utils.getFileExtansion(fname.trim().toUpperCase()).equals("PDF");
    }

    public static boolean isChl(String fname) {
        return Utils.getFileExtansion(fname.trim().toUpperCase()).equals("CHL");
    }

    public static void sleep(long mills){
        try
        {
            Thread.sleep(mills);
        }
        catch(InterruptedException ex)
        {
            Thread.currentThread().interrupt();
        }
    }
}
