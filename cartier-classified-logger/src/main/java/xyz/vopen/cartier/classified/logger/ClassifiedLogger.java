package xyz.vopen.cartier.classified.logger;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;

/**
 * xyz.vopen.cartier.classified.logger
 *
 * @author Elve.xu [xuhw@yyft.com]
 * @version v1.0 - 10/08/2017.
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(value = { TYPE })
public @interface ClassifiedLogger {

    /**
     * 日志类型,一般分类的日志类型(单独记录的日志文件)
     */
    String loggerType ();

    /**
     * 日志文件
     */
    String loggerFile ();

    /**
     * 日志级别
     */
    Level level () default Level.INFO;

    /**
     * 是否继续传递
     */
    boolean additive () default true;

    /**
     * 分类存储的文件夹,不指定,系统将使用默认值: default path -> /var/log/${application}/${loggerType}/xxx.log
     */
    String directory () default ".";

    /**
     * 日志格式
     */
    String pattern () default DefaultValues.PATTERN;

    /**
     * 是否开启异步处理
     */
    boolean asyn () default false;

    /**
     * 排序
     */
    boolean sortable () default false;

    /**
     * 排序超时时间(根据业务线程执行时间设定,默认为3分钟)
     */
    long timeout () default 3 * 60;
}
