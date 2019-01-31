package com.Callbaser;

import java.io.IOException;
import java.io.File;
import java.lang.reflect.Field;
import java.util.Scanner;

public class Config {

    public static boolean ready = false;
    public static String MONITORFOLDER;
    public static String PRINTSERVICE;

    static {
        load();
    }


    private Config() {

    }

    private static void load() {
        File file = new File("config.cfg");
        try (Scanner sc = new Scanner(file)) {
            while (sc.hasNextLine()) {
                String nextLine = sc.nextLine();
                if (!nextLine.startsWith("#") && nextLine.contains("=")) {
                    String key = nextLine.split("=")[0].trim().toUpperCase();
                    String value = nextLine.split("=")[1].trim();
                    Field f = Config.class.getDeclaredField(key);
                    if (f.getType() == int.class) {
                        f.set(Config.class, Integer.parseInt(value));
                    } else {
                        f.set(Config.class, value);
                    }
                }
            }
        } catch (NoSuchFieldException e) {
            Clog.warn("NoSuchFieldException", e);
            ready = false;
        } catch (SecurityException e) {
            Clog.warn("SecurityException", e);
            ready = false;
        } catch (IllegalAccessException e) {
            Clog.warn("IllegalAccessException", e);
            ready = false;
        } catch (IOException e) {
            Clog.warn("IOException", e);
            ready = false;
        } catch (Exception e) {
            Clog.warn("Exception", e);
            ready = false;
        }


        ready = true;

    }

}
