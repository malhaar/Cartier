package xyz.vopen.cartier.commons.task.exception;

/**
 * Task Alread exist exception
 *
 * @author Elve.xu [xuhw@yyft.com]
 * @version v1.0 - 27/02/2017.
 */
public class TaskAlreadyExistException extends RuntimeException {

    /**
     * Constructs a new runtime exception with {@code null} as its
     * detail message.  The cause is not initialized, and may subsequently be
     * initialized by a call to {@link #initCause}.
     */
    public TaskAlreadyExistException () {
    }

    /**
     * Constructs a new runtime exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public TaskAlreadyExistException (String message) {
        super(message);
    }
}
