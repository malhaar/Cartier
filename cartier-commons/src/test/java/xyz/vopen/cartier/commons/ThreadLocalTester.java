package xyz.vopen.cartier.commons;

import org.apache.commons.lang3.RandomUtils;

import java.util.concurrent.CountDownLatch;

/**
 * xyz.vopen.cartier.commons
 *
 * @author Elve.xu [xuhw@yyft.com]
 * @version v1.0 - 18/08/2017.
 */
public class ThreadLocalTester {

    public static void main (String[] args) throws InterruptedException {

        final CountDownLatch count = new CountDownLatch(10);
        for (int i = 0; i < 10; i++) {
            new Thread(new Runnable() {
                @Override
                public void run () {

                    long ran = RandomUtils.nextLong(1, 100);
                    longThreadLocal.set(ran);
                    try {
                        Thread.sleep(RandomUtils.nextLong(1, 500));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(ran + " -> " + longThreadLocal.get());
                    count.countDown();
                }
            }, "t" + i).start();
        }


        count.await();
    }

    static ThreadLocal<Long> longThreadLocal = new ThreadLocal<>();

}
