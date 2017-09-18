package xyz.vopen.cartier.classified.logger;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventTranslator;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.slf4j.Logger;
import xyz.vopen.cartier.classified.logger.async.ClassifiedLoggerEvent;
import xyz.vopen.cartier.classified.logger.async.ClassifiedLoggerWorkHandler;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * xyz.vopen.cartier.classified.logger
 *
 * @author Elve.xu [xuhw@yyft.com]
 * @version v1.0 - 11/08/2017.
 */
public class ClassifiedContext {

    private ClassifiedContext () {
        final Thread thread = SortableHolder.checker();
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run () {
                try {
                    SortableHolder.doCheck(true);
                    thread.interrupt();
                } catch (Exception e) {
                }
                DisruptorHolder.release();
            }
        }));
    }

    private static class InstanceHolder {
        private static ClassifiedContext INSTANCE = new ClassifiedContext();
    }

    static ClassifiedContext getClassifiedContext () {
        return InstanceHolder.INSTANCE;
    }

    private static final String DEFAULT_LOG_PATH = "/var/log";
    private String logDirectory = DEFAULT_LOG_PATH;

    public void setLogDirectory (String dir) {
        try {
            if (dir != null && dir.trim().length() > 0) {

                if (Files.notExists(Paths.get(dir))) {
                    Files.createDirectories(Paths.get(dir));
                }
            }
        } catch (Exception ignore) {
        }
    }

    String getLogDirectory () {
        return logDirectory;
    }

    public static LogType defaultLogType = LogType.LOGBACK;

    public void setLogType (LogType logType) {
        if (logType != null) {
            ClassifiedContext.defaultLogType = logType;
        }
    }

    public enum LogType {
        LOGBACK,
        LOG4J2
    }


    static Map<Class, ClassifiedLogger> annotationsMap = new HashMap<>();

    static class DisruptorHolder {
        private static Map<String, Disruptor<ClassifiedLoggerEvent>> disruptors = new ConcurrentHashMap<>();
        private static final Integer RING_BUFF_SIZE = 4 * 1024;

        static void release () {
            if (disruptors.size() > 0) {
                for (Map.Entry<String, Disruptor<ClassifiedLoggerEvent>> entry : disruptors.entrySet()) {
                    try {
                        entry.getValue().shutdown();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        static void disruptor (ClassifiedLogger classifiedLogger) {
            // CHECK
            String type = classifiedLogger.loggerType();
            if (!disruptors.containsKey(type)) {

                Disruptor<ClassifiedLoggerEvent> disruptor = new Disruptor<ClassifiedLoggerEvent>(
                        // event factory
                        new EventFactory<ClassifiedLoggerEvent>() {
                            @Override
                            public ClassifiedLoggerEvent newInstance () {
                                return new ClassifiedLoggerEvent();
                            }
                        },

                        // ring buff size
                        RING_BUFF_SIZE,

                        // thread factory
                        new ThreadFactory() {
                            @Override
                            public Thread newThread (Runnable r) {
                                return new Thread(r, "async-logger-thread");
                            }
                        },

                        // multi producer
                        ProducerType.MULTI,

                        // wait strategy
                        new BlockingWaitStrategy());

                disruptor.handleEventsWithWorkerPool(new ClassifiedLoggerWorkHandler());

                RingBuffer<ClassifiedLoggerEvent> ringBuffer = disruptor.start();

                disruptors.put(type, disruptor);
            }
        }
    }

    static class SortableHolder {
        private static final int timeout = 3;
        private static Map<Object, SortableLoggerCarrier> tpsh = new ConcurrentHashMap<>();
        static volatile Lock flushLocker = new ReentrantLock();

        static void append (Object caller, SortableLoggerCarrier carrier) {
            tpsh.put(caller, carrier);
        }

        static void flush (Object caller) {
            flushLocker.lock();
            try {
                if (caller != null) {
                    if (tpsh.containsKey(caller)) {
                        SortableLoggerCarrier carrier = tpsh.get(caller);
                        // trans queue
                        doPush(carrier.classifiedLogger, carrier.logger, true, carrier.loggerQueue.toArray(new String[carrier.loggerQueue.size()]));
                        tpsh.remove(caller);
                    }
                }
            } finally {
                flushLocker.unlock();
            }
        }

        static void doCheck (boolean focus) {
            try {
                Iterator<Map.Entry<Object, SortableLoggerCarrier>> iterator = tpsh.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<Object, SortableLoggerCarrier> entry = iterator.next();
                    Object caller = entry.getKey();
                    int offset = timeout;
                    if (annotationsMap.containsKey(caller.getClass())) {
                        offset = (int) annotationsMap.get(caller.getClass()).timeout();
                    }
                    SortableLoggerCarrier carrier = entry.getValue();
                    if (focus || carrier.isTimeout(offset)) {
                        // flush
                        SortableHolder.flush(caller);
                        tpsh.remove(caller);
                        System.out.println("FLUSH: flush timeout loggers ,Class:" + caller);
                    }
                }
            } catch (Exception ignored) {
            }
        }

        static Thread checker () {
            Thread checker = new Thread(new Runnable() {
                @Override
                public void run () {
                    for (; ; ) {
                        doCheck(false);
                        try {
                            Thread.sleep(1000 * 60);
                        } catch (InterruptedException ignored) {
                        }
                    }
                }
            }, "thread-sortable-timeout-checker");
            checker.setDaemon(true);
            checker.start();
            return checker;
        }

    }

    static class SortableLoggerCarrier {
        Date timestamp = new Date();

        // OOM
        List<String> loggerQueue = new ArrayList<>();

        ClassifiedLogger classifiedLogger;
        Logger logger;

        public boolean isTimeout (int offset) {
            Calendar t1 = Calendar.getInstance();
            t1.setTime(timestamp);
            t1.add(Calendar.MINUTE, offset);
            Calendar now = Calendar.getInstance();
            return now.before(t1);
        }
    }

    /**
     * flush thread logger ,if not call on finally block ,application will auto-flush 3min
     *
     * @param caller
     *         caller
     */
    void flush (Object caller) {
        if (caller != null) {
            SortableHolder.flush(caller);
        }
    }

    /**
     * 推送日志
     */
    void pushLogger (final ClassifiedLoggerEvent source) {

        if (source != null) {
            doPush(source.getClassifiedLogger(), source.getLogger(), false, source.getContent());
        }
    }

    private static void doPush (final ClassifiedLogger classifiedLogger, final Logger logger, final boolean sortable, final String... contents) {
        if (DisruptorHolder.disruptors.containsKey(classifiedLogger.loggerType())) {
            DisruptorHolder.disruptors.get(classifiedLogger.loggerType()).publishEvent(new EventTranslator<ClassifiedLoggerEvent>() {
                @Override
                public void translateTo (ClassifiedLoggerEvent event, long sequence) {
                    event.setClassifiedLogger(classifiedLogger);
                    event.setSortable(sortable);
                    event.setLogger(logger);
                    if (sortable) {
                        event.setContents(Arrays.asList(contents));
                    } else {
                        event.setContent(contents[0]);
                    }
                }
            });
        }
    }

    /**
     * TODO 推送排序日志
     *
     * @param classifiedLogger
     *         x
     * @param content
     *         c
     * @param logger
     *         slf.log
     */
    void pushSortableLogger (Object caller, final ClassifiedLogger classifiedLogger, final String content, final Logger logger) {
        if (SortableHolder.tpsh.containsKey(caller)) {
            SortableLoggerCarrier carrier = SortableHolder.tpsh.get(caller);
            if (carrier != null) {
                // TODO fix OOM
                //carrier.loggerQueue.add(content);
                

                carrier.timestamp = new Date();
            }
        } else {
            SortableLoggerCarrier carrier = new SortableLoggerCarrier();
            carrier.loggerQueue.add(content);
            carrier.timestamp = new Date();
            carrier.logger = logger;
            carrier.classifiedLogger = classifiedLogger;
            SortableHolder.append(caller, carrier);
        }
    }
}
