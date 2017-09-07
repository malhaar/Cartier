package xyz.vopen.cartier.commons;

import org.apache.commons.lang3.RandomUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.lang.ref.Reference;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * xyz.vopen.cartier.commons
 *
 * @author Elve.xu [xuhw@yyft.com]
 * @version v1.0 - 18/08/2017.
 */
public class ExecutorServiceUsedThreadLocalTester {


    private ThreadPoolExecutor service;
    private ThreadLocal<InnerClass> threadLocal;
    private Integer loop = 20;

    private Map<InnerClass, List<String>> counts = new ConcurrentHashMap<>();

    @Before
    public void before () throws Exception {
        System.out.println(" test before");
        service = new ThreadPoolExecutor(8,
                20,
                100,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(100),
                new ThreadFactory() {
                    @Override
                    public Thread newThread (Runnable r) {
                        return new Thread(r);
                    }
                }, new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution (Runnable r, ThreadPoolExecutor executor) {
                System.out.println(r);
            }
        });


        threadLocal = new ThreadLocal<InnerClass>() {
            @Override
            protected InnerClass initialValue () {
                System.out.println("new class ");
                return new InnerClass();
            }
        };
    }


    @Test
    public void test () throws Exception {

        System.out.println(" test running");

        final CountDownLatch count = new CountDownLatch(loop);
        for (int i = 0; i < loop; i++) {

            service.submit(new Runnable() {
                @Override
                public void run () {

                    try {

                        String threadName = Thread.currentThread().getName();
                        InnerClass innerClass = threadLocal.get();

//                        System.out.println(threadName + " -> " + innerClass);

                        if (counts.containsKey(innerClass)) {
                            List<String> vs = counts.get(innerClass);
                            vs.add(threadName);
                            counts.put(innerClass, vs);
                        } else {
                            List<String> temp = new ArrayList<>();
                            temp.add(threadName);
                            counts.put(innerClass, temp);
                        }

                        Thread.sleep(RandomUtils.nextLong(1, 500));

                        threadLocal.remove();


                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        count.countDown();
                    }
                }
            });

            if (i == 10) {
                System.out.println("clean");
                cleanThreadLocals();
            }
        }

        count.await();

        for (Map.Entry<InnerClass, List<String>> entry : counts.entrySet()) {

            System.out.println("Class: " + Integer.toHexString(entry.getKey().hashCode()) + "  ,Count: " + entry.getValue().size());

        }

    }


    @After
    public void end () throws Exception {
        if (service != null) {
            service.shutdown();
        }
        System.out.println(" test end");
    }


    private static class InnerClass {
    }

    private void cleanThreadLocals () {
        try {
            // Get a reference to the thread locals table of the current thread
            Thread thread = Thread.currentThread();
            Field threadLocalsField = Thread.class.getDeclaredField("threadLocals");
            threadLocalsField.setAccessible(true);
            Object threadLocalTable = threadLocalsField.get(thread);

            // Get a reference to the array holding the thread local variables inside the
            // ThreadLocalMap of the current thread
            Class threadLocalMapClass = Class.forName("java.lang.ThreadLocal$ThreadLocalMap");
            Field tableField = threadLocalMapClass.getDeclaredField("table");
            tableField.setAccessible(true);
            Object table = tableField.get(threadLocalTable);

            // The key to the ThreadLocalMap is a WeakReference object. The referent field of this object
            // is a reference to the actual ThreadLocal variable
            Field referentField = Reference.class.getDeclaredField("referent");
            referentField.setAccessible(true);

            for (int i = 0; i < Array.getLength(table); i++) {
                // Each entry in the table array of ThreadLocalMap is an Entry object
                // representing the thread local reference and its value
                Object entry = Array.get(table, i);
                if (entry != null) {
                    // Get a reference to the thread local object and remove it from the table
                    ThreadLocal threadLocal = (ThreadLocal) referentField.get(entry);
                    threadLocal.remove();
                }
            }
        } catch (Exception e) {
            // We will tolerate an exception here and just log it
            throw new IllegalStateException(e);
        }
    }

}
