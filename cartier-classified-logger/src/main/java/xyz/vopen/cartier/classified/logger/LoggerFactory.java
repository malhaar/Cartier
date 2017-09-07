package xyz.vopen.cartier.classified.logger;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import org.apache.logging.log4j.LogManager;
import xyz.vopen.cartier.classpathscanner.FastClasspathScanner;
import xyz.vopen.cartier.classpathscanner.matchprocessor.ClassAnnotationMatchProcessor;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * xyz.vopen.cartier.classified.logger
 *
 * @author Elve.xu [xuhw@yyft.com]
 * @version v1.0 - 11/08/2017.
 */
public abstract class LoggerFactory {

    private static Map<Class, Logger> loggerCaches = new ConcurrentHashMap<>();
    private static Map<Class, org.slf4j.Logger> slf4jLoggerCaches = new ConcurrentHashMap<>();
    private static Map<String, org.slf4j.Logger> lcaches = new ConcurrentHashMap<>();

    static {
        FastClasspathScanner scanner = new FastClasspathScanner();
        scanner.matchClassesWithAnnotation(ClassifiedLogger.class, new ClassAnnotationMatchProcessor() {
            @Override
            public void processMatch (Class<?> classWithAnnotation) {
                ClassifiedLogger classifiedLogger = classWithAnnotation.getAnnotation(ClassifiedLogger.class);
                if (classifiedLogger != null) {
                    ClassifiedContext.annotationsMap.put(classWithAnnotation,classifiedLogger);
                    String loggerTypeName = classifiedLogger.loggerType();
                    if (!lcaches.containsKey(loggerTypeName)) {
                        lcaches.put(loggerTypeName, newLogbackLogger(loggerTypeName, classifiedLogger));
                        if (classifiedLogger.asyn()) {
                            ClassifiedContext.DisruptorHolder.disruptor(classifiedLogger);
                        }
                        System.out.println("INFO: classified log class :[ " + classWithAnnotation + " ]");
                    }
                }
            }
        });
        scanner.scan();
    }

//    private static org.slf4j.Logger

    private static ch.qos.logback.classic.Logger newLogbackLogger (String name, ClassifiedLogger classifiedLogger) {
        LoggerContext context = (LoggerContext) org.slf4j.LoggerFactory.getILoggerFactory();
        ClassifiedContext classifiedContext = ClassifiedContext.getClassifiedContext();
        ch.qos.logback.classic.Logger logger = context.getLogger(name);
        logger.setAdditive(classifiedLogger.additive());
        RollingFileAppender rollingFile = new RollingFileAppender();
        rollingFile.setContext(context);
        rollingFile.setName("dynamic_logger_fileAppender");

        String directory = classifiedLogger.directory();
        if (directory.trim().length() == 0) {
            directory = classifiedContext.getLogDirectory();
        }

        // use default
        if (".".equals(directory)) {
            directory = "logs/";
        } else {
            // end with "/"
            if (!directory.endsWith("/")) {
                directory = directory + "/";
            }
        }

        // /var/log/${logType}-${sed}.log
        rollingFile.setFile(directory + classifiedLogger.loggerType() + "/msg.log");
        rollingFile.setAppend(true);

        // Set up rolling policy
        TimeBasedRollingPolicy rollingPolicy = new TimeBasedRollingPolicy();
//        rollingPolicy.setFileNamePattern(directory + File.separator + "%d{yyyy-MM,aux}" + File.separator + classifiedLogger.loggerType() + "_%d{yyyy-MM-dd}.txt");
        String fileNamePattern = directory + classifiedLogger.loggerType() + "/" + "%d{yyyy-MM,aux}" + File.separator + "msg_%d{yyyy-MM-dd}.txt";
        rollingPolicy.setFileNamePattern(fileNamePattern);
        rollingPolicy.setParent(rollingFile);
        rollingPolicy.setContext(context);
        rollingPolicy.start();

        // set up pattern encoder
        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(context);
        encoder.setPattern(classifiedLogger.pattern());
        encoder.start();

        rollingFile.setRollingPolicy(rollingPolicy);
        rollingFile.setEncoder(encoder);
        rollingFile.start();

        // Atach appender to logger
        logger.addAppender(rollingFile);
        Level level = classifiedLogger.level();
        ch.qos.logback.classic.Level targetLevel = ch.qos.logback.classic.Level.INFO;
        try {
            targetLevel = ch.qos.logback.classic.Level.toLevel(level.name(), ch.qos.logback.classic.Level.INFO);
        } catch (Exception ignored) {
        }
        logger.setLevel(targetLevel);

        return logger;
    }

    private static org.apache.logging.log4j.Logger newLog4j2Logger (String name, ClassifiedLogger classifiedLogger) {

        final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
//        org.apache.logging.log4j.Logger logger = null;
//        logger.info("");
        return null;
    }

    public static Logger getLogger (Class clazz) {
        if (loggerCaches.containsKey(clazz)) {
            return loggerCaches.get(clazz);
        }
        Logger logger = new Logger(clazz);
        loggerCaches.put(clazz, logger);

        // check clazz
        if (!slf4jLoggerCaches.containsKey(clazz)) {
            org.slf4j.Logger slf4jLogger = org.slf4j.LoggerFactory.getLogger(clazz);
            slf4jLoggerCaches.put(clazz, slf4jLogger);
        }
        return logger;
    }

    static org.slf4j.Logger getLogger0 (String name) {
        return lcaches.get(name);
    }

    public static org.slf4j.Logger getSlf4jLogger (Class clazz) {
        if (!slf4jLoggerCaches.containsKey(clazz)) {
            org.slf4j.Logger slf4jLogger = org.slf4j.LoggerFactory.getLogger(clazz);
            slf4jLoggerCaches.put(clazz, slf4jLogger);
        }
        return slf4jLoggerCaches.get(clazz);
    }

}
