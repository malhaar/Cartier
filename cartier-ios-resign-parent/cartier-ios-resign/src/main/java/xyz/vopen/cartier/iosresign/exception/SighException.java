package xyz.vopen.cartier.iosresign.exception;

import java.security.PrivilegedActionException;

/**
 * 签名异常
 *
 * @author Elve.xu [xuhw@yyft.com]
 * @version v1.0 - 29/07/2017.
 */
public class SighException extends Throwable {

    /**
     * Constructs a new throwable with {@code null} as its detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     * <p>
     * <p>The {@link #fillInStackTrace()} method is called to initialize
     * the stack trace data in the newly created throwable.
     */
    public SighException () {
    }

    /**
     * Constructs a new throwable with the specified detail message.  The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     * <p>
     * <p>The {@link #fillInStackTrace()} method is called to initialize
     * the stack trace data in the newly created throwable.
     *
     * @param message
     *         the detail message. The detail message is saved for
     *         later retrieval by the {@link #getMessage()} method.
     */
    public SighException (String message) {
        super(message);
    }

    /**
     * Constructs a new throwable with the specified detail message and
     * cause.  <p>Note that the detail message associated with
     * {@code cause} is <i>not</i> automatically incorporated in
     * this throwable's detail message.
     * <p>
     * <p>The {@link #fillInStackTrace()} method is called to initialize
     * the stack trace data in the newly created throwable.
     *
     * @param message
     *         the detail message (which is saved for later retrieval
     *         by the {@link #getMessage()} method).
     * @param cause
     *         the cause (which is saved for later retrieval by the
     *         {@link #getCause()} method).  (A {@code null} value is
     *         permitted, and indicates that the cause is nonexistent or
     *         unknown.)
     *
     * @since 1.4
     */
    public SighException (String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new throwable with the specified cause and a detail
     * message of {@code (cause==null ? null : cause.toString())} (which
     * typically contains the class and detail message of {@code cause}).
     * This constructor is useful for throwables that are little more than
     * wrappers for other throwables (for example, {@link
     * PrivilegedActionException}).
     * <p>
     * <p>The {@link #fillInStackTrace()} method is called to initialize
     * the stack trace data in the newly created throwable.
     *
     * @param cause
     *         the cause (which is saved for later retrieval by the
     *         {@link #getCause()} method).  (A {@code null} value is
     *         permitted, and indicates that the cause is nonexistent or
     *         unknown.)
     *
     * @since 1.4
     */
    public SighException (Throwable cause) {
        super(cause);
    }
}
