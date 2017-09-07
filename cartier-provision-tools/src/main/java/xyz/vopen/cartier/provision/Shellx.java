package xyz.vopen.cartier.provision;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import xyz.vopen.cartier.commons.utils.DomainSerializable;
import xyz.vopen.cartier.commons.yaml.YamlException;
import xyz.vopen.cartier.commons.yaml.YamlReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.util.Map;

/**
 * Shell ext.
 *
 * @author Elve.xu [xuhw@yyft.com]
 * @version v1.0 - 29/07/2017.
 */
public abstract class Shellx {

    /**
     * Shell Item
     */
    public enum Item {
        buildCSR,
        initAndImportKeychain,
        certs
    }

    private static Map<Item, String> resources = Maps.newHashMap();

    static {
        resources.put(Item.buildCSR, "/assembly/bash/buildCSR.sh");
        resources.put(Item.initAndImportKeychain, "/assembly/bash/initAndImportKeychain.sh");
        resources.put(Item.certs, "/assembly/bash/certs.sh");
    }

    /**
     * 获取Shell脚本流
     *
     * @param item
     *         shell 分类
     *
     * @return 脚本流
     *
     * @throws ShellNotFoundException
     *         not found exception
     */
    public static InputStream getShell (Item item) throws ShellNotFoundException {

        if (item != null) {
            if (resources.containsKey(item)) {
                return Shellx.class.getResourceAsStream(resources.get(item));
            }
        }
        return null;
    }


    public static class ShellBean extends DomainSerializable {}

    /**
     * Shell: <shell>certs.sh</shell> execute output result
     */
    public static class KeyChainsBean extends ShellBean {
        private KeyChain[] keyChains;

        public KeyChain[] getKeyChains () {
            return keyChains;
        }

        public void setKeyChains (KeyChain[] keyChains) {
            this.keyChains = keyChains;
        }

        public static class KeyChain extends DomainSerializable {

            private String keys;
            private String[] items;

            public String getKeys () {
                return keys;
            }

            public void setKeys (String keys) {
                this.keys = keys;
            }

            public String[] getItems () {
                return items;
            }

            public void setItems (String[] items) {
                this.items = items;
            }
        }
    }

    /**
     * 将Shell执行结果序列化成实体类型
     *
     * @param outputYamlFile
     *         shell输出的Yaml结果文件路径
     * @param clazz
     *         class instance of T
     * @param <T>
     *         T
     *
     * @return return instance of output result
     */
    public static <T extends ShellBean> T getShellResultBean (String outputYamlFile, Class<T> clazz) {
        if (StringUtils.isNoneBlank(outputYamlFile)) {
            try {
                YamlReader reader = new YamlReader(new FileReader(outputYamlFile));
                return reader.read(clazz);
            } catch (FileNotFoundException | YamlException ignored) {
            }
        }
        return null;
    }

    public static class ShellNotFoundException extends Throwable {
        /**
         * Constructs a new throwable with {@code null} as its detail message.
         * The cause is not initialized, and may subsequently be initialized by a
         * call to {@link #initCause}.
         * <p>
         * <p>The {@link #fillInStackTrace()} method is called to initialize
         * the stack trace data in the newly created throwable.
         */
        public ShellNotFoundException () {
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
        public ShellNotFoundException (String message) {
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
        public ShellNotFoundException (String message, Throwable cause) {
            super(message, cause);
        }
    }

}
