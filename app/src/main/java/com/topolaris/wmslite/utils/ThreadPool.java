package com.topolaris.wmslite.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author toPolaris
 * description 线程池工具类
 * @date 2021/5/19 14:39
 */
public class ThreadPool {
    /**
     * 提供用来进行耗时操作的线程池
     */
    public static final ExecutorService executor = Executors.newCachedThreadPool();
}
