package xyz.vopen.cartier.iosresign.cert;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.vopen.cartier.commons.command.LinuxCommand;
import xyz.vopen.cartier.iosresign.exception.CertException;
import xyz.vopen.cartier.provision.Shellx;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * xyz.vopen.cartier.iosresign.cert
 *
 * @author Elve.xu [xuhw@yyft.com]
 * @version v1.0 - 28/07/2017.
 */
public final class CertificateHandler {

    private static Logger logger = LoggerFactory.getLogger(CertificateHandler.class);

    // @@@:off
    private CertificateHandler () {}
    private static class InstanceHolder {private static CertificateHandler INSTANCE = new CertificateHandler();}
    public static CertificateHandler getInstance () {return CertificateHandler.InstanceHolder.INSTANCE;}
    // @@@:on

    /**
     * 获取 CSR 创建的 Handler (建议使用: {@link #getHandler(String)})
     * <br/>
     *
     * @return handler
     *
     * @see #getHandler(String)
     */
    public Handler getHandler () throws CertException {
        Handler handler = new Handler();
        if (StringUtils.isBlank(handler.shellBasePath)) {
            // default path : $user.dir/.tempshell/
            handler.init(defaultx());
        }
        return handler;
    }

    private static volatile Boolean loaded = Boolean.FALSE;
    private static volatile Lock loadLock = new ReentrantLock();

    /**
     * 此方法仅供内部方法使用,在没有调用 init 方法传递根目录时, 系统默认将jar文件中的内置脚本拷贝到用户临时目录进行操作
     */
    private String defaultx () throws CertException {

        try {

            String userDir = System.getProperty("user.dir");
            String defaultPath = StringUtils.endsWith(userDir, "/") ? userDir + ".tempshell/" : userDir + "/.tempshell/";
            Path t = Paths.get(defaultPath);
            if (Files.notExists(t)) {
                Files.createDirectory(t);
            }

            if (loaded) return defaultPath;
            loadLock.lock();

            logger.info("@未指定根目录,默认的目录执行:{}", defaultPath);

            // buildCSR.sh
            String targetShellPath = defaultPath + Shellx.Item.buildCSR.name() + ".sh";
            if (Files.notExists(Paths.get(targetShellPath))) {
                byte[] content = IOUtils.toByteArray(Shellx.getShell(Shellx.Item.buildCSR));
                // -rwxr-xr-x default
                Path finalFile = Files.createFile(Paths.get(targetShellPath), PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rwxr-xr-x")));
                Files.write(finalFile, content, StandardOpenOption.WRITE);
            }

            // initAndImportKeychain.sh
            targetShellPath = defaultPath + Shellx.Item.initAndImportKeychain.name() + ".sh";
            if (Files.notExists(Paths.get(targetShellPath))) {
                byte[] content = IOUtils.toByteArray(Shellx.getShell(Shellx.Item.initAndImportKeychain));
                // -rwxr-xr-x default
                Path finalFile = Files.createFile(Paths.get(targetShellPath), PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rwxr-xr-x")));
                Files.write(finalFile, content, StandardOpenOption.WRITE);
            }

            // certs.sh
            targetShellPath = defaultPath + Shellx.Item.certs.name() + ".sh";
            if (Files.notExists(Paths.get(targetShellPath))) {
                byte[] content = IOUtils.toByteArray(Shellx.getShell(Shellx.Item.certs));
                // -rwxr-xr-x default
                Path finalFile = Files.createFile(Paths.get(targetShellPath), PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rwxr-xr-x")));
                Files.write(finalFile, content, StandardOpenOption.WRITE);
            }

            loaded = Boolean.TRUE;
            return defaultPath;
        } catch (Shellx.ShellNotFoundException | Exception e) {
            throw new CertException("defaultx error", e);
        } finally {
            loadLock.unlock();
        }
    }

    /**
     * 获取 CSR 创建的 Handler
     * <br/>
     *
     * @param shellBasePath
     *         Shell脚本根目录
     *
     * @return handler
     */
    public Handler getHandler (String shellBasePath) {
        Handler handler = new Handler();
        if (StringUtils.isNoneBlank(shellBasePath)) {
            handler.init(shellBasePath);
        }
        return handler;
    }


    /**
     * Certificate Handler
     */
    public static class Handler {

        /**
         * Shell base path
         */
        String shellBasePath;

        /**
         * Shell Command for csr without base path
         */
        private String buildCSRShell = "buildCSR.sh '%s' '%s' '%s' '%s' '%s'";

        /**
         * Shell Command for init without base path
         */
        private String initAndImportShell = "initAndImportKeychain.sh '%s' '%s' '%s' '%s' '%s'";

        /**
         * 初始化 Shell根目录
         *
         * @param shellBasePath
         *         shell base path
         */
        public void init (String shellBasePath) {
            this.shellBasePath = shellBasePath;
            if (StringUtils.isNoneBlank(this.shellBasePath)) {
                if (!StringUtils.endsWith(this.shellBasePath, "/")) {
                    this.shellBasePath = this.shellBasePath + "/";
                }
            }
        }

        /**
         * 生成完整的 Shell 命令
         *
         * @param baseDir
         *         根目录
         * @param keyFileName
         *         key
         * @param csrFileName
         *         csr
         * @param email
         *         email
         * @param username
         *         username
         *
         * @return 返回完整的 shell 脚本
         */
        private String renderCSRShell (String baseDir, String keyFileName, String csrFileName, String email, String username) {
            String command = String.format(buildCSRShell, baseDir, keyFileName, csrFileName, email, username);
            return shellBasePath + command;
        }

        /**
         * 生成初始化脚本
         *
         * @param privateKey
         *         私钥
         * @param certPath
         *         证书
         * @param userPwd
         *         机器密码
         * @param keychain
         *         钥匙串
         * @param keychainPwd
         *         密码
         *
         * @return 完整脚本
         */
        private String renderInitAndImportShell (String privateKey, String certPath, String userPwd, String keychain, String keychainPwd) {
            String command = String.format(initAndImportShell, privateKey, certPath, userPwd, keychain, keychainPwd);
            return shellBasePath + command;
        }

        /**
         * 创建 CSR 文件
         *
         * @param baseDir
         *         文件存储根目录
         * @param keyFileName
         *         私钥名称
         * @param csrFileName
         *         CSR 文件名称
         * @param email
         *         生成CSR 文件的邮箱
         * @param username
         *         生成 CSR 的用户名
         */
        public void generateCSR (String baseDir, String keyFileName, String csrFileName, String email, String username) throws CertException {

            if (StringUtils.isNoneBlank(baseDir, keyFileName, csrFileName, email, username)) {
                String shell = renderCSRShell(baseDir, keyFileName, csrFileName, email, username);
                logger.info("@generateCSR Command : {}", shell);
                int code = LinuxCommand.executeCommand(shell, new LinuxCommand.CmdExecCallback() {
                    @Override
                    public void onComplete (Boolean aBoolean, int resultCode, String err, String stout, String shell) {
                        logger.info("@generateCSR Command Execute STDOUT : \r\n {}", stout);
                        logger.info("@generateCSR Command Execute Error (if any) : \r\n {}", err);
                    }
                });

                if (code != 0) {
                    throw new CertException("@generateCSR fail : code = " + code);
                }

            } else {
                throw new CertException("@创建CSR文件的参数异常");
            }
        }

        /**
         * 初始化密钥和添加钥匙串
         *
         * @param privateKey
         *         私钥
         * @param certPath
         *         证书
         * @param userPwd
         *         机器密码
         * @param keychain
         *         钥匙串
         * @param keychainPwd
         *         密码
         */
        public void initAndImportCert (String privateKey, String certPath, String userPwd, String keychain, String keychainPwd) throws CertException {

            if (StringUtils.isNoneBlank(privateKey, certPath, userPwd, keychain, keychainPwd)) {
                String shell = renderInitAndImportShell(privateKey, certPath, userPwd, keychain, keychainPwd);
                logger.info("@initAndImportCert Command : {}", shell);
                int code = LinuxCommand.executeCommand(shell, new LinuxCommand.CmdExecCallback() {
                    @Override
                    public void onComplete (Boolean aBoolean, int resultCode, String err, String stout, String shell) {
                        logger.info("@initAndImportCert Command Execute STDOUT : \r\n {}", stout);
                        logger.info("@initAndImportCert Command Execute Error (if any) : \r\n {}", err);
                    }
                });

                if (code != 0) {
                    throw new CertException("@initAndImportCert fail : code = " + code);
                }
            }

        }

        /**
         * 创建钥匙串
         * <pre>
         *
         *     Like :
         *      Example Command:
         *          security create-keychain -p 123456  /Users/ive/Library/Keychains/t-two.keychain
         *
         * </pre>
         *
         * @param keychain
         *         钥匙串路径
         * @param keychainPwd
         *         密码
         */
        public void createKeychain (String keychain, String keychainPwd) throws CertException {
            if (StringUtils.isNoneBlank(keychain, keychainPwd)) {
                String shell = "security create-keychain -p " + keychainPwd + " " + keychain;
                int code = LinuxCommand.executeCommand(shell, null);
                if (code != 0) {
                    throw new CertException("@createKeychain fail : code = " + code);
                }
            }
        }


    }

}
