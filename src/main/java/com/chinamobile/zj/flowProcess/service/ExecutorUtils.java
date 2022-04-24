package com.chinamobile.zj.flowProcess.service;

import com.chinamobile.zj.comm.InternalException;
import com.chinamobile.zj.util.DateUtil;

import java.util.Date;
import java.util.concurrent.*;

public class ExecutorUtils {

    private static final ExecutorService POOL = Executors.newFixedThreadPool(5);

    /**
     * 异步阻塞执行，执行结束后返回
     * 提交任务，空闲时执行。若沒有空闲线程，则一直阻塞在 get() 方法等待返回执行结果
     * @param callable
     * @return
     * @param <T>
     */
    public static <T> T submit(Callable<T> callable) {
        Future<T> a = POOL.submit(callable);
        T b;
        try {
            b = a.get();
        } catch (InterruptedException | ExecutionException ex) {
            throw new InternalException(ex);
        }
        return b;
    }

    /**
     * 立即返回
     * 提交任务，空闲时执行。若沒有空闲线程，则一直阻塞
     * @param runnable
     */
    public static void execute(Runnable runnable) {
        POOL.execute(runnable);
    }

    /**
     * https://blog.csdn.net/mdw0730/article/details/75645503
     * https://stackoverflow.com/questions/18516131/how-to-properly-shutdown-executor-services-with-spring
     * <p>
     * todo zj 线程关闭 shutdown问题
     *
     * @param args
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Future<?> futureRunnable = POOL.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("runnable start: " + DateUtil.format(new Date(), DateUtil.DATE_NANO_TIME_REGEX));
                    TimeUnit.SECONDS.sleep(3);
                    System.out.println("runnable end: " + DateUtil.format(new Date(), DateUtil.DATE_NANO_TIME_REGEX));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        Future<?> futureCallable = POOL.submit(new Callable<Object>() {
            @Override
            public Object call() {
                try {
                    System.out.println("callable start: " + DateUtil.format(new Date(), DateUtil.DATE_NANO_TIME_REGEX));
                    TimeUnit.SECONDS.sleep(1);
                    System.out.println("callable end: " + DateUtil.format(new Date(), DateUtil.DATE_NANO_TIME_REGEX));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                return "success";
            }
        });
        System.out.println("outside start: " + DateUtil.format(new Date(), DateUtil.DATE_NANO_TIME_REGEX));
        System.out.println(futureRunnable.get());
        System.out.println(futureCallable.get());
        System.out.println("outside end: " + DateUtil.format(new Date(), DateUtil.DATE_NANO_TIME_REGEX));
    }

}
