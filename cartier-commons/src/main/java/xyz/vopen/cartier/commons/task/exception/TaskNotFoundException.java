package xyz.vopen.cartier.commons.task.exception;

/**
 * Task Not Found exception
 *
 * @author Elve.xu [xuhw@yyft.com]
 * @version v1.0 - 27/02/2017.
 */
public class TaskNotFoundException extends RuntimeException {

    /**
     * Constructs a new runtime exception with {@code null} as its
     * detail message.  The cause is not initialized, and may subsequently be
     * initialized by a call to {@link #initCause}.
     */
    public TaskNotFoundException () {
    }

    /**
     * Constructs a new runtime exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public TaskNotFoundException (String message) {
        super(message);
    }
}
