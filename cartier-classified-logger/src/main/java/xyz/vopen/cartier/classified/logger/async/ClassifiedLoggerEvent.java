package xyz.vopen.cartier.classified.logger.async;

import org.slf4j.Logger;
import xyz.vopen.cartier.classified.logger.ClassifiedLogger;

import java.util.ArrayList;
import java.util.List;

/**
 * logger event
 *
 * @author Elve.xu [xuhw@yyft.com]
 * @version v1.0 - 17/08/2017.
 */
public class ClassifiedLoggerEvent {

    /**
     * logback logger instance
     */
    private Logger logger;

    /**
     * logger type
     */
    private ClassifiedLogger classifiedLogger;

    private boolean sortable = false;

    /**
     * logger content
     */
    private String content;

    private List<String> contents = new ArrayList<>();

    public ClassifiedLoggerEvent () {
    }

    public ClassifiedLoggerEvent (ClassifiedLogger classifiedLogger, String content, Logger logger) {
        this.classifiedLogger = classifiedLogger;
        this.content = content;
        this.logger = logger;
    }

    public ClassifiedLogger getClassifiedLogger () {
        return classifiedLogger;
    }

    public void setClassifiedLogger (ClassifiedLogger classifiedLogger) {
        this.classifiedLogger = classifiedLogger;
    }

    public Logger getLogger () {
        return logger;
    }

    public void setLogger (Logger logger) {
        this.logger = logger;
    }

    public String getContent () {
        return content;
    }

    public void setContent (String content) {
        this.content = content;
    }

    public boolean isSortable () {
        return sortable;
    }

    public void setSortable (boolean sortable) {
        this.sortable = sortable;
    }

    public List<String> getContents () {
        return contents;
    }

    public void setContents (List<String> contents) {
        this.contents = contents;
    }

}
