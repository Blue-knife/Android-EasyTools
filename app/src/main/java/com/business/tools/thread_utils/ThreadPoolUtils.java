package com.business.tools.thread_utils;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Petterp
 * on 2019-11-07
 * Function:全局线程池，无需关闭
 * 注意：开发中若使用 FixedThreadPool生成固定线程数的线程池，使用完切记关闭，否则将造成内存泄漏
 */
public class ThreadPoolUtils {

    public static ThreadPoolExecutor threadPool;

    /**
     * 默认操作，添加任务
     *
     * @param runnable
     */
    public static void execute(Runnable runnable) {
        getThreadPool().execute(runnable);
    }

    /**
     * 获取执行结果
     *
     * @param callable
     * @param <T>
     * @return
     */
    public static <T> Future<T> submit(Callable<T> callable) {
        return getThreadPool().submit(callable);
    }


    /**
     * 获取线程池对象
     *
     * @return
     */
    public static ThreadPoolExecutor getThreadPool() {
        if (threadPool != null) {
            return threadPool;
        }
        synchronized (ThreadPoolUtils.class) {
            //核心线程数为8，最长为16，任务队列长度32，非核心线程等待时间60s,饱和策略采用调用者所在的线程执行任务
            if (threadPool == null) {
                threadPool = new ThreadPoolExecutor(8, 16, 60, TimeUnit.SECONDS,
                        new LinkedBlockingQueue<Runnable>(32),new ThreadPoolExecutor.CallerRunsPolicy());
            }
        }
        return threadPool;
    }

}
