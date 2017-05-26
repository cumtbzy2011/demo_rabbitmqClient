package com.bian.utils;

/**
 * Created by Mr.Bi on 2017/5/26.
 */
public class ThreadUtils {
    public static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
