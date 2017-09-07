package xyz.vopen.cartier.iosresign.sigh;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.vopen.cartier.commons.command.LinuxCommand;
import xyz.vopen.cartier.iosresign.ShellHandlerSupport;
import xyz.vopen.cartier.iosresign.SignType;
import xyz.vopen.cartier.iosresign.exception.SighException;

import java.io.InputStream;
import java.lang.ref.Reference;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 重签处理类
 *
 * @author Elve.xu [xuhw@yyft.com]
 * @version v1.0 - 28/07/2017.
 */
public class FloatsignHandler extends ShellHandlerSupport {


    private static Logger logger = LoggerFactory.getLogger(FloatsignHandler.class);

    // @@@:off
    private FloatsignHandler () {}
    private static class InstanceHolder {private static FloatsignHandler INSTANCE = new FloatsignHandler();}
    public static FloatsignHandler getInstance () {return FloatsignHandler.InstanceHolder.INSTANCE;}
    // @@@:on

    public FloatsignHandler.Handler getHandler () throws SighException {
        Handler handler = new Handler();
        if (StringUtils.isBlank(handler.shellBasePath)) {
            try {
                handler.init(defaultx());
            } catch (ShellTransformException e) {
                throw new SighException(e);
            }
        }
        return handler;
    }

    public FloatsignHandler.Handler getHandler (String shellBasePath) {
        Handler handler = new Handler();
        if (StringUtils.isBlank(handler.shellBasePath)) {
            if (StringUtils.isNoneBlank(shellBasePath)) {
                handler.init(shellBasePath);
            } else {
                try {
                    handler.init(defaultx());
                } catch (ShellTransformException ignored) {
                }
            }
        }
        return handler;
    }

    private static volatile Boolean loaded = Boolean.FALSE;
    private static volatile Lock loadLock = new ReentrantLock();

    private String defaultx () throws ShellTransformException {
        String userDir = System.getProperty("user.dir");
        String defaultPath = StringUtils.endsWith(userDir, "/") ? userDir + ".tempshell/" : userDir + "/.tempshell/";
        if (loaded) {
            return defaultPath;
        }

        try {
            loadLock.lock();
            InputStream stream = SignType.class.getResourceAsStream("/assembly/bash/floatsign.sh");
            String result = transformShellStream(stream, null, "floatsign.sh");
            loaded = Boolean.TRUE;
            return result;
        } finally {
            loadLock.unlock();
        }

    }

    /**
     * floatsign source "iPhone Distribution: Name" uuid -p "path/to/profile" [-d "display name"]  [-e entitlements] [-k keychain] [-P keychain-password] [-b "BundleIdentifier"] outputIpa
     * // 企业签
     * // sh floatsign.sh "/Users/ive/git-pyw-repo/cartier/cartier-ios-resign-parent/cartier-ios-resign/src/test/resources/20170731_20170727_com.tycs3_443916~eb760c58_21.ipa" "iPhone Distribution: Jiangsu Changshu Rural Commercial Bank Co., Ltd" "XXX-UUID-1" -v -p "/Users/ive/git-pyw-repo/cartier/cartier-ios-resign-parent/cartier-ios-resign/src/test/resources/qiye.mobileprovision" -b "com.changshuqiye." "/Users/ive/git-pyw-repo/cartier/cartier-ios-resign-parent/cartier-ios-resign/src/test/resources/output/output-company-1.ipa"
     * // sh floatsign.sh "/Users/ive/git-pyw-repo/cartier/cartier-ios-resign-parent/cartier-ios-resign/src/test/resources/20170731_20170727_com.tycs3_443916~eb760c58_21.ipa" "iPhone Distribution: Jiangsu Changshu Rural Commercial Bank Co., Ltd" "XXX-UUID-2" -v -p "/Users/ive/git-pyw-repo/cartier/cartier-ios-resign-parent/cartier-ios-resign/src/test/resources/CSQiYe.mobileprovision" "/Users/ive/git-pyw-repo/cartier/cartier-ios-resign-parent/cartier-ios-resign/src/test/resources/output/output-company-2.ipa"
     * // sh floatsign.sh "/Users/ive/git-pyw-repo/cartier/cartier-ios-resign-parent/cartier-ios-resign/src/test/resources/20170801_20170704_com.idreamsky.cqb.pyw.ipa" "iPhone Distribution: Funnysafe, Inc." "XXX-UUID-Funnysafe" -v -p "/Users/ive/git-pyw-repo/cartier/cartier-ios-resign-parent/cartier-ios-resign/src/test/resources/fanny.mobileprovision" -b "com.idreamsky.cqb.pyw" "/Users/ive/git-pyw-repo/cartier/cartier-ios-resign-parent/cartier-ios-resign/src/test/resources/output/output-company-2.ipa"
     * <p>
     * // 个人签
     * // sh floatsign.sh "/Users/ive/git-pyw-repo/cartier/cartier-ios-resign-parent/cartier-ios-resign/src/test/resources/20170801_20170704_com.idreamsky.cqb.pyw.ipa" "iPhone Developer: Mingjun Lee (XGBQNMQ39P)" "XXX-UUID-2" -v -p "/Users/ive/git-pyw-repo/cartier/cartier-ios-resign-parent/cartier-ios-resign/src/test/resources/test4dev-2.mobileprovision" -b "com.idreamsky.cqb.pyw" "/Users/ive/git-pyw-repo/cartier/cartier-ios-resign-parent/cartier-ios-resign/src/test/resources/output/cqb-single-2.ipa"
     **/
    // @@@:off
    public static class FloatsignShellCommand {
        final String SHELL = "floatsign.sh";
        final String SPACE = " ";
        final String QUOTE = "\"";
        final String basePath;// 跟路径
        final String source;// 原包
        final String identify;// 开发者信息
        final String output;// 输出包
        final String uuid;// 唯一标识(供多线程使用)
        final String profile;// 描述文件
        final String keychain;// 钥匙串
        final String keychainPassword;// 钥匙串密码
        final String bundleId;// 包 ID
        final boolean verbose;// 输出信息
        final boolean keepBundleId;
        private FloatsignShellCommand (Builder builder) {this.keepBundleId = builder.keepBundleId;this.basePath = builder.basePath;this.source = builder.source;this.identify = builder.identify;this.output = builder.output;this.uuid = builder.uuid;this.profile = builder.profile;this.keychain = builder.keychain;this.keychainPassword = builder.keychainPassword;this.bundleId = builder.bundleId;this.verbose = builder.verbose;}
        public static Builder newBuilder () {return new Builder();}
        //floatsign source "iPhone Distribution: Name" uuid -p "path/to/profile" [-d "display name"]  [-e entitlements] [-k keychain] [-P keychain-password] [-b "BundleIdentifier"] outputIpa
        public String renderShell() throws SighException {
            if(StringUtils.isAnyBlank(this.basePath ,this.source,this.identify,this.output,this.profile)){throw new SighException("Sign Properties: [basePath|source|identify|output|uuid|profile] must not be null.");}
            StringBuilder command = new StringBuilder();
            command.append(this.basePath).append(SHELL);
            command.append(SPACE).append(this.source);
            command.append(SPACE).append(QUOTE).append(this.identify).append(QUOTE);
            command.append(SPACE).append(uuid);
            if(this.verbose){command.append(SPACE).append("-v");}
            if(this.keepBundleId){command.append(SPACE).append("-keep");}
            command.append(SPACE).append("-p").append(SPACE).append(QUOTE).append(this.profile).append(QUOTE);
            if(StringUtils.isNoneBlank(this.keychain)){command.append(SPACE).append("-k").append(SPACE).append(QUOTE).append(this.keychain).append(QUOTE);}
            if(StringUtils.isNoneBlank(this.keychainPassword)){command.append(SPACE).append("-P").append(SPACE).append(QUOTE).append(this.keychainPassword).append(QUOTE);}
            if(StringUtils.isNoneBlank(this.bundleId)){command.append(SPACE).append("-b").append(SPACE).append(QUOTE).append(this.bundleId).append(QUOTE);}
            command.append(SPACE).append(QUOTE).append(output).append(QUOTE);
            return command.toString();
        }

        private static class Builder {
            private String basePath;private String source;private String identify;private String output;private String uuid;private String profile;private String keychain;private String keychainPassword;private String bundleId;private boolean verbose;private boolean keepBundleId;
            private Builder () {}
            public Builder basePath (String basePath) {this.basePath = basePath;return this;}
            public Builder source (String source) {this.source = source;return this;}
            public Builder identity (String identify) {this.identify = identify;return this;}
            public Builder uuid (String uuid) {this.uuid = uuid;return this;}
            public Builder profile (String profile) {this.profile = profile;return this;}
            public Builder keyChain (String keychain, String keychainPassword) {this.keychain = keychain;this.keychainPassword = keychainPassword;return this;}
            public Builder bundleId (String bundleId) {this.bundleId = bundleId;return this;}
            public Builder verbose(Boolean verbose) {this.verbose = verbose;return this;}
            public Builder keepBundleId(Boolean keepBundleId) {this.keepBundleId = keepBundleId;return this;}
            public Builder output(String output) {this.output = output;return this;}
            public FloatsignShellCommand build() {return new FloatsignShellCommand(this);}
        }
    }
    // @@@:on

    /**
     * Handler instance
     */
    public static class Handler {

        private String shellBasePath;

        /**
         * 初始化 Shell根目录
         *
         * @param shellBasePath
         *         shell base path
         */
        void init (String shellBasePath) {
            this.shellBasePath = shellBasePath;
            if (StringUtils.isNoneBlank(this.shellBasePath)) {
                if (!StringUtils.endsWith(this.shellBasePath, "/")) {
                    this.shellBasePath = this.shellBasePath + "/";
                }
            }
        }

        /**
         * 个人签
         *
         * @param source
         *         源文件
         * @param profile
         *         描述文件
         * @param bundleId
         *         包 ID(个人签默认不需要改包ID , null)
         * @param output
         *         输出文件
         * @param identity
         *         开发者信息
         * @param keychain
         *         钥匙串
         * @param keychainPwd
         *         钥匙串密码
         */
        public void development (String source, String identity, String profile, String bundleId,
                                 String output, String keychain, String keychainPwd) throws SighException {

            String shell = new FloatsignShellCommand.Builder()
                    .basePath(this.shellBasePath)
                    .source(source)
                    .identity(identity)
                    .uuid(UUID.randomUUID().toString().replace("-", ""))
                    .profile(profile)
                    .output(output)
                    .bundleId(bundleId)
                    .keyChain(keychain, keychainPwd)
                    .verbose(true)
                    .build().renderShell();

            int code = LinuxCommand.executeCommand(shell, new LinuxCommand.CmdExecCallback() {
                @Override
                public void onComplete (Boolean aBoolean, int resultCode, String err, String stout, String shell) {
                    logger.info("@development-sign Command Execute STDOUT : \r\n {}", stout);
                    logger.info("@development-sign Command Execute Error (if any) : \r\n {}", err);
                }
            });

            if (code != 0) {
                throw new SighException("@adhoc fail : code = " + code);
            }
        }

        /**
         * 企业签, 企业证书默认安装在 login.keychain ?
         *
         * @param source
         *         源文件
         * @param identify
         *         开发者信息
         * @param profile
         *         描述文件
         * @param output
         *         输出
         * @param bundleId
         *         包 Id
         * @param keychain
         *         钥匙串
         * @param keychainPwd
         *         密码
         */
        public void adhoc (String source, String identify, String profile, String bundleId,
                           String output, String keychain, String keychainPwd) throws SighException {

            FloatsignShellCommand.Builder builder = new FloatsignShellCommand.Builder();
            builder.source(source)
                    .basePath(this.shellBasePath)
                    .identity(identify)
                    .uuid(UUID.randomUUID().toString().replace("-", ""))
                    .profile(profile)
                    .output(output)
                    .keyChain(keychain, keychainPwd)
                    .verbose(true);
            if (StringUtils.isNoneBlank(bundleId)) {
                builder.bundleId(bundleId).keepBundleId(false);
            } else {
                builder.keepBundleId(true);
            }

            String shell = builder.build().renderShell();

            int code = LinuxCommand.executeCommand(shell, new LinuxCommand.CmdExecCallback() {
                @Override
                public void onComplete (Boolean aBoolean, int resultCode, String err, String stout, String shell) {
                    logger.info("@adhoc-sign Command Execute STDOUT : \r\n {}", stout);
                    logger.info("@adhoc-sign Command Execute Error (if any) : \r\n {}", err);
                }
            });

            if (code != 0) {
                throw new SighException("@adhoc fail : code = " + code);
            }

        }

    }

    @Deprecated
    private void cleanThreadLocals () {
        try {
            // Get a reference to the thread locals table of the current thread
            Thread thread = Thread.currentThread();
            Field threadLocalsField = Thread.class.getDeclaredField("threadLocals");
            threadLocalsField.setAccessible(true);
            Object threadLocalTable = threadLocalsField.get(thread);

            // Get a reference to the array holding the thread local variables inside the
            // ThreadLocalMap of the current thread
            Class threadLocalMapClass = Class.forName("java.lang.ThreadLocal$ThreadLocalMap");
            Field tableField = threadLocalMapClass.getDeclaredField("table");
            tableField.setAccessible(true);
            Object table = tableField.get(threadLocalTable);

            // The key to the ThreadLocalMap is a WeakReference object. The referent field of this object
            // is a reference to the actual ThreadLocal variable
            Field referentField = Reference.class.getDeclaredField("referent");
            referentField.setAccessible(true);

            for (int i = 0; i < Array.getLength(table); i++) {
                // Each entry in the table array of ThreadLocalMap is an Entry object
                // representing the thread local reference and its value
                Object entry = Array.get(table, i);
                if (entry != null) {
                    // Get a reference to the thread local object and remove it from the table
                    ThreadLocal threadLocal = (ThreadLocal) referentField.get(entry);
                    threadLocal.remove();
                }
            }
        } catch (Exception e) {
            // We will tolerate an exception here and just log it
            throw new IllegalStateException(e);
        }
    }

}
