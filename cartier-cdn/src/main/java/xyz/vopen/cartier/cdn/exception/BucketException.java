package xyz.vopen.cartier.cdn.exception;

/**
 * xyz.vopen.cartier.cdn.exception
 *
 * @author Elve.xu [xuhw@yyft.com]
 * @version v1.0 - 24/07/2017.
 */
public class BucketException extends Throwable {

    public BucketException () {
    }

    public BucketException (String message) {
        super(message);
    }

    public BucketException (String message, Throwable cause) {
        super(message, cause);
    }

    public BucketException (Throwable cause) {
        super(cause);
    }

    public BucketException (String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
