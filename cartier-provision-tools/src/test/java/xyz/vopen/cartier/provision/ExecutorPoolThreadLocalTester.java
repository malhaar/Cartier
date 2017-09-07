package xyz.vopen.cartier.provision;

import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * xyz.vopen.cartier.provision
 *
 * @author Elve.xu [xuhw@yyft.com]
 * @version v1.0 - 14/08/2017.
 */
public class ExecutorPoolThreadLocalTester {

    ThreadPoolExecutor service = new ThreadPoolExecutor(
            3,
            6,
            10,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(120));

    @Test
    public void get () throws Exception {

        for (int i = 0; i < 20; i++) {
            service.execute(new Runnable() {
                @Override
                public void run () {
                    Processor.Handler handler = Processor.get();
                    long time = RandomUtils.nextInt(100,1000);
                    System.out.println(Thread.currentThread() + "\t->" + handler + " , " + time);
                    try {
                        Thread.sleep(time);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Processor.remove();
                }
            });
        }

        Thread.sleep(50000);
    }


}
