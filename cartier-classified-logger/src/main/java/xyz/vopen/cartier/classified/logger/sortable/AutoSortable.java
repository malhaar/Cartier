package xyz.vopen.cartier.classified.logger.sortable;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;

/**
 * xyz.vopen.cartier.classified.logger.sortable
 *
 * @author Elve.xu [xuhw@yyft.com]
 * @version v1.0 - 21/08/2017.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(value = { TYPE })
public @interface AutoSortable {

}
