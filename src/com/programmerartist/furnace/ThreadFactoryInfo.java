package com.programmerartist.furnace;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程工厂
 *
 * Created by 程序员Artist on 2016/11/7.
 */
public class ThreadFactoryInfo implements ThreadFactory {
    String pre;
    AtomicInteger suffix;

    /**
     *
     * @param pre
     * @param suffix
     */
    public ThreadFactoryInfo(String pre, AtomicInteger suffix) {
        this.pre = pre;
        this.suffix = suffix;
    }

    @Override
    public Thread newThread(Runnable r) {

        Thread thread = new Thread(r, pre + suffix.getAndIncrement());
        thread.setDaemon(true);
        return thread;
    }
}
