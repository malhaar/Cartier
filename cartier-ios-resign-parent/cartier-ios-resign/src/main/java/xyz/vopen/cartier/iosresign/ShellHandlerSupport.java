package xyz.vopen.cartier.iosresign;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.security.PrivilegedActionException;
import java.util.Set;

/**
 * xyz.vopen.cartier.iosresign
 *
 * @author Elve.xu [xuhw@yyft.com]
 * @version v1.0 - 01/08/2017.
 */
public class ShellHandlerSupport {

    /**
     * 转换 Shell 脚本流
     *
     * @param destShellPath
     *         shell full path (if null will use default <code>user.dir</code>)
     * @param shellStream
     *         shell stream
     * @param destShellName
     *         shell file name
     *
     * @throws ShellTransformException
     *         换换异常
     */
    protected String transformShellStream (InputStream shellStream, String destShellPath, String destShellName) throws ShellTransformException {
        try {
            String defaultPath = null;
            if (StringUtils.isBlank(destShellName)) {
                throw new ShellTransformException("Shell name must not be null.");
            }
            if (StringUtils.isBlank(destShellPath)) {
                String userDir = System.getProperty("user.dir");
                defaultPath = StringUtils.endsWith(userDir, "/") ? userDir + ".tempshell/" : userDir + "/.tempshell/";
            } else {
                defaultPath = destShellPath;
                defaultPath = StringUtils.endsWith(defaultPath, "/") ? defaultPath : defaultPath + "/";
            }

            Path t = Paths.get(defaultPath);
            if (Files.notExists(t)) {
                Files.createDirectories(t);
            }

            String targetShellPath = defaultPath + destShellName;

            // check shell file
            if (Files.notExists(Paths.get(targetShellPath))) {
                byte[] content = IOUtils.toByteArray(shellStream);
                // -rwxr-xr-x default
                Set<PosixFilePermission> perms = PosixFilePermissions.fromString("rwxr-xr-x");
                FileAttribute<Set<PosixFilePermission>> fileAttributes = PosixFilePermissions.asFileAttribute(perms);
                Path finalFile = Files.createFile(Paths.get(targetShellPath), fileAttributes);
                Files.write(finalFile, content, StandardOpenOption.WRITE);
            }

            return defaultPath;
        } catch (Exception e) {
            throw new ShellTransformException(e);
        }
    }


    /**
     * Shell Stream transform exception
     */
    protected static class ShellTransformException extends Throwable {
        /**
         * Constructs a new throwable with {@code null} as its detail message.
         * The cause is not initialized, and may subsequently be initialized by a
         * call to {@link #initCause}.
         * <p>
         * <p>The {@link #fillInStackTrace()} method is called to initialize
         * the stack trace data in the newly created throwable.
         */
        public ShellTransformException () {
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
        public ShellTransformException (String message) {
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
        public ShellTransformException (String message, Throwable cause) {
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
        public ShellTransformException (Throwable cause) {
            super(cause);
        }
    }
}
