package xyz.vopen.cartier.classified.logger;

import xyz.vopen.cartier.classified.logger.sortable.AutoSortable;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * xyz.vopen.cartier.classified.logger
 *
 * @author Elve.xu [xuhw@yyft.com]
 * @version v1.0 - 11/08/2017.
 */
public class ClassContainerTester {

    private static Logger logger = LoggerFactory.getLogger(ClassContainerTester.class);

    private static ClassA classA = new ClassA();

    private static ExecutorService service = Executors.newCachedThreadPool();

    public static void main (String[] args) throws Exception {

        Thread.sleep(10000);
        logger.info("main-xxxx");
        // 1
//        classA.print();

        // 2
        Thread t1 = new Thread(new InvokerClassRunnable(false), "t1");
        t1.start();


        // 3
//        InvokerClassThread t2 = new InvokerClassThread();
//        t2.setName("t2");
//        t2.start();

        // 4
        for (int i = 0; i < 5; i++) {
            service.execute(new InvokerClassRunnable());
        }

        // 5
//        service.submit(new InvokerClassCallable());
    }

    @ClassifiedLogger(loggerType = "mt-1", additive = false, asyn = true, loggerFile = "mt-1", timeout = 3)
    @AutoSortable
    private static class InvokerClassRunnable implements Runnable {

        InvokerClassRunnable () {
        }

        boolean flag;

        InvokerClassRunnable (Boolean flag) {
            this.flag = flag;
        }

        @Override
        public void run () {
//            classA.print();
            long start = System.currentTimeMillis();
            for (int i = 0; i < 2000000; i++) {
                logger.info(this, "{} ,{} - {}", new Object[]{ Thread.currentThread().getName(), System.nanoTime(), i });
            }

            if (!flag) {
                logger.flush(this);
            }
            System.out.println(Thread.currentThread().getName() + " -> MT-1 TIME : " + (System.currentTimeMillis() - start) + " ms");
        }
    }

    @ClassifiedLogger(loggerType = "mt-2", loggerFile = "mt-2", additive = false)
    private static class InvokerClassThread extends Thread {
        @Override
        public void run () {
//            classA.print();
            long start = System.currentTimeMillis();
            for (int i = 0; i < 2000000; i++) {
                logger.info("InvokerClassThreadLoggerContent");
            }
            System.out.println(Thread.currentThread().getName() + " -> MT-2 TIME : " + (System.currentTimeMillis() - start) + " ms");
        }
    }

    @ClassifiedLogger(loggerType = "mt-3", loggerFile = "mt-3", additive = false, asyn = true)
    private static class InvokerClassCallable implements Callable<Void> {

        @Override
        public Void call () throws Exception {
//            classA.print();
            long start = System.currentTimeMillis();
            for (int i = 0; i < 2000000; i++) {
                logger.info("{} ,{} - {}", Thread.currentThread().getName(), System.nanoTime(), i);
            }
            System.out.println(Thread.currentThread().getName() + " -> MT-3 TIME : " + (System.currentTimeMillis() - start) + " ms");
            return null;
        }
    }

}

//loop-1
//pool-2-thread-1-async -> MT-1 TIME : 28028 ms
//pool-2-thread-2-sync  -> MT-3 TIME : 71815 ms

//loop-2
//pool-2-thread-1 -> MT-1 TIME : 32066 ms
//pool-2-thread-2 -> MT-3 TIME : 37283 ms