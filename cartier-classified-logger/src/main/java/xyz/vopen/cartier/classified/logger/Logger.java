package xyz.vopen.cartier.classified.logger;

import org.slf4j.helpers.MessageFormatter;
import xyz.vopen.cartier.classified.logger.async.ClassifiedLoggerEvent;
import xyz.vopen.cartier.classified.logger.sortable.AutoSortable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Classified logger instance
 *
 * @author Elve.xu [xuhw@yyft.com]
 * @version v1.0 - 11/08/2017.
 */
public final class Logger {

    public static final String LOGGER = Logger.class.getName();
    private Class definedClass;

    Logger (Class clazz) {
        this.definedClass = clazz;
    }

    private volatile boolean builded = false;

    /**
     * 刷新日志
     */
    public void flush (Object caller) {
        ClassifiedContext.getClassifiedContext().flush(caller);
    }

    /**
     * 记录日志方法
     *
     * @param formatContent
     *         文本内容
     * @param values
     *         值
     */
    public void info (String formatContent, Object... values) {
        info(null, formatContent, values);
    }

    /**
     * 记录日志方法
     *
     * @param caller
     *         caller
     * @param formatContent
     *         content
     * @param values
     *         args
     */
    public void info (Object caller, String formatContent, Object[] values) {
        if (values == null || values.length == 0) {
            log(caller, formatContent);
        } else {
            log(caller, format(formatContent, values));
        }
    }

    private String format (String formatContent, Object... values) {
        if (formatContent != null && formatContent.trim().length() > 0) {
            if (values != null && values.length > 0) {
                return MessageFormatter.arrayFormat(formatContent, values).getMessage();
            }
        }
        return "";
    }

    /**
     * 记录日志内容
     *
     * @param content
     *         内容
     */
    private void log (Object caller, String content) {

        StackTraceElement el = parseTargetClass();
        Class clazz = null;
        boolean extLog = false;
        if (el != null) {
            try {
                clazz = Class.forName(el.getClassName());
            } catch (ClassNotFoundException e) {
                System.err.println(e.getMessage());
            }
            if (clazz != null) {
                boolean exist = clazz.isAnnotationPresent(ClassifiedLogger.class);
                boolean sortable = clazz.isAnnotationPresent(AutoSortable.class);
                if (exist) {
                    ClassifiedLogger classifiedLogger = (ClassifiedLogger) clazz.getAnnotation(ClassifiedLogger.class);
                    if (classifiedLogger != null) {
                        extLog = classifiedLogger.additive();
                        org.slf4j.Logger logger = LoggerFactory.getLogger0(classifiedLogger.loggerType());
                        // need compare sort ?
                        if (sortable || classifiedLogger.sortable()) {
                            if (caller != null) {
                                ClassifiedContext.getClassifiedContext().pushSortableLogger(caller, classifiedLogger, content, logger);
                            } else {
                                System.err.println("WARN: something is wrong ,@see : " + el.toString());
                            }
                        } else {
                            // check async
                            if (classifiedLogger.asyn()) {
                                ClassifiedContext.getClassifiedContext().pushLogger(
                                        new ClassifiedLoggerEvent(classifiedLogger, content, logger)
                                );
                            } else {
                                logger.info(content);
                            }
                        }
                    }
                } else {
                    extLog = true;
                }
            }
        }

        if (extLog) {
            org.slf4j.Logger logger = xyz.vopen.cartier.classified.logger.LoggerFactory.getSlf4jLogger(clazz);
            logger.info(content);
        }

        // drop
    }

    private StackTraceElement parseTargetClass () {

        try {

            Thread thread = Thread.currentThread();

            StackTraceElement[] elements = thread.getStackTrace();
            Set<String> elementsSet = new HashSet<>();
            List<StackTraceElement> classes = new ArrayList<>();
            for (StackTraceElement element : elements) {
                // remove the same class
                if (elementsSet.add(element.getClassName())) {
                    classes.add(element);
                }
            }

            StackTraceElement el = null;
            if (classes.size() >= 2) {
                for (int i = 0; i < classes.size(); i++) {
                    StackTraceElement clazzElement = classes.get(i);
                    if (LOGGER.equals(clazzElement.getClassName())) {
                        el = classes.get(i + 1);
                        break;
                    }
                }
            }

            if (el != null) {
                return el;
            }
        } catch (Exception ignore) {
        }
        return null;
    }


}
