package xyz.vopen.cartier.classified.logger;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.LoggerComparator;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * xyz.vopen.cartier.classified.logger
 *
 * @author Elve.xu [xuhw@yyft.com]
 * @version v1.0 - 10/08/2017.
 */
public class MainLogger {

    private static Logger logger = LoggerFactory.getLogger(MainLogger.class);

    public static void main (String[] args) throws InterruptedException {
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.submit(new MyRunner());
//        service.execute(new MyRunner());
//        service.submit(new MyRunner2());

        service.shutdown();
    }

//    @ClassifiedLogger(loggerType = "MyRunner-TYPE", loggerFile = "")
    private static class MyRunner implements Runnable {
        @Override
        public void run () {
            try {
                dynamicLogger(MyRunner.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

//    @ClassifiedLogger(loggerType = "MyRunner2-TYPE", loggerFile = "")
    private static class MyRunner2 implements Callable<Void> {
        @Override
        public Void call () throws Exception {
            try {
                dynamicLogger(MyRunner2.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private static void dynamicLogger (Class clazz) throws Exception {

        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

        System.out.println(context);

        List<ch.qos.logback.classic.Logger> list = context.getLoggerList();

        System.out.println("---------------------------------------------");
        for (ch.qos.logback.classic.Logger logger1 : list) {
            System.out.println(logger1);
        }
        System.out.println("---------------------------------------------");
        String name = "nonlogger-" + System.currentTimeMillis();

        ch.qos.logback.classic.Logger nonlogger = context.getLogger(name);

        System.out.println(nonlogger);

        nonlogger.info("nonlogger");

        setLoggerProperties(name, nonlogger, context, clazz);

        nonlogger.info("@after set properties ..");

        // remove
        Field field = context.getClass().getDeclaredField("loggerCache");
        System.out.println(field);
        field.setAccessible(true);
        Object o = field.get(context);

        if (o instanceof ConcurrentHashMap<?, ?>) {
            ConcurrentHashMap<String, ch.qos.logback.classic.Logger> map = (ConcurrentHashMap) o;
            Collection<ch.qos.logback.classic.Logger> collection = map.values();
            List<ch.qos.logback.classic.Logger> loggerList = new ArrayList<ch.qos.logback.classic.Logger>(collection);
            Collections.sort(loggerList, new LoggerComparator());

            for (ch.qos.logback.classic.Logger logger1 : loggerList) {
                System.out.println(logger1.getName());
            }
//            for (Map.Entry<String, ch.qos.logback.classic.Logger> entry : map.entrySet()) {
//                String key = entry.getKey();
//                ch.qos.logback.classic.Logger logger = entry.getValue();
//            }

//            map.remove(name);
        }


//        nonlogger.info("....@@@");
        print(context);

        list = context.getLoggerList();
        System.out.println("------------------------end---------------------");
        for (ch.qos.logback.classic.Logger logger1 : list) {
            System.out.println(logger1);
        }
        System.out.println("---------------------------------------------");
        logger.info("end");
    }

    static void print (LoggerContext context) throws Exception {
        Field field = context.getClass().getDeclaredField("loggerCache");
        System.out.println(field);
        field.setAccessible(true);
        Object o = field.get(context);
        if (o instanceof ConcurrentHashMap<?, ?>) {
            ConcurrentHashMap<String, ch.qos.logback.classic.Logger> map = (ConcurrentHashMap) o;
            Collection<ch.qos.logback.classic.Logger> collection = map.values();
            List<ch.qos.logback.classic.Logger> loggerList = new ArrayList<ch.qos.logback.classic.Logger>(collection);
            Collections.sort(loggerList, new LoggerComparator());

            for (ch.qos.logback.classic.Logger logger1 : loggerList) {
                System.out.println("-->" + logger1.getName());
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static void setLoggerProperties (String name, ch.qos.logback.classic.Logger logger, LoggerContext context, Class targetClass) throws Exception {

        System.out.println("----------------------------------------------------");
        System.out.println(targetClass);
        boolean exist = targetClass.isAnnotationPresent(ClassifiedLogger.class);
        System.out.println(exist);
        ClassifiedLogger classifiedLogger = (ClassifiedLogger) targetClass.getAnnotation(ClassifiedLogger.class);
        System.out.println(classifiedLogger.loggerType());

        System.out.println("----------------------------------------------------");

        // Don't inherit root appender
        logger.setAdditive(true);

        RollingFileAppender rollingFile = new RollingFileAppender();
        rollingFile.setContext(context);
        rollingFile.setName("dynamic_logger_fileAppender");

        // Optional
        rollingFile.setFile("logs" + File.separator + classifiedLogger.loggerType() + ".log");
        rollingFile.setAppend(true);

        // Set up rolling policy
        TimeBasedRollingPolicy rollingPolicy = new TimeBasedRollingPolicy();
        rollingPolicy.setFileNamePattern("logs" + File.separator + "%d{yyyy-MM,aux}" + File.separator + classifiedLogger.loggerType() + "_%d{yyyy-MM-dd}.txt");
        rollingPolicy.setParent(rollingFile);
        rollingPolicy.setContext(context);
        rollingPolicy.start();

        // set up pattern encoder
        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(context);
        encoder.setPattern("%msg%n");
        encoder.start();

        rollingFile.setRollingPolicy(rollingPolicy);
        rollingFile.setEncoder(encoder);
        rollingFile.start();

        // Atach appender to logger
        logger.addAppender(rollingFile);

    }


}
