package xyz.vopen.cartier.classified.logger;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;

/**
 * Classified logger annotation
 *
 * @author Elve.xu [xuhw@yyft.com]
 * @version v1.0 - 10/08/2017.
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(value = { TYPE })
public @interface ClassifiedLogger {

    /**
     * log type
     */
    String loggerType ();

    /**
     * assigned log file
     */
    String loggerFile ();

    /**
     * logger level
     */
    Level level () default Level.INFO;

    /**
     * is additive
     */
    boolean additive () default true;

    /**
     * log path: default path -> /var/log/${application}/${loggerType}/xxx.log
     */
    String directory () default ".";

    /**
     * formatter
     */
    String pattern () default DefaultValues.PATTERN;

    /**
     * asyn mode
     */
    boolean asyn () default false;

    /**
     * sortable
     */
    boolean sortable () default false;

    /**
     * sortable timeout for flush-disk (default :3min)
     */
    long timeout () default 3 * 60;
}
