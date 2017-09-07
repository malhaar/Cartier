package xyz.vopen.cartier.commons.security;

import org.apache.commons.lang3.StringUtils;
import xyz.vopen.cartier.commons.command.LinuxCommand;

/**
 * 安全检查工具
 *
 * @author Elve.xu [xuhw@yyft.com]
 * @version v1.2 - 09/08/2017.
 */
public final class SecurityTools {


    /**
     * pem -> p12
     *
     * @param inPem
     *         in pem file
     * @param outP12
     *         out p12 file
     */
    public static int pem2p12 (String inPem, String outP12) {
        if (StringUtils.isNoneBlank(inPem, outP12)) {
            String command = "openssl pkcs12 -export -out %s -passout pass: -in %s";
            return LinuxCommand.executeCommand(String.format(command, outP12, inPem), null);
        }
        return -1;
    }

    /**
     * p12 -> crt
     *
     * @param inP12
     *         in p12 file
     * @param inP12Password
     *         password ,can be null
     * @param outCrt
     *         out crt file
     * @param outCrtPassword
     *         out password ,can be null
     */
    public static int p12Tocrt (String inP12, String inP12Password, String outCrt, String outCrtPassword) {
        if (StringUtils.isNoneBlank(inP12, outCrt)) {
            String command = "openssl pkcs12 -in %s -passin pass:%s -out %s -passout pass:%s";
            command = String.format(command, inP12, StringUtils.isNoneBlank(inP12Password) ? inP12Password : "", outCrt,
                    StringUtils.isNoneBlank(outCrtPassword) ? outCrtPassword : "");
            return LinuxCommand.executeCommand(command, null);
        }
        return -1;
    }

    /**
     * crt 文件 转换成 cer
     *
     * @param inCrt
     *         crt
     * @param outCer
     *         cer
     */
    public static int crt2cer (String inCrt, String outCer) {
        if (StringUtils.isNoneBlank(inCrt, outCer)) {
            String command = "openssl x509 -inform pem -in %s -outform der -out %s";
            return LinuxCommand.executeCommand(String.format(command, inCrt, outCer), null);
        }
        return -1;
    }

    /**
     * Cer 证书文件转pem格式文件
     *
     * @param inCer
     *         cer
     * @param ourPem
     *         pem
     */
    public static int cer2pem (String inCer, String ourPem) {
        if (StringUtils.isNoneBlank(inCer, ourPem)) {
            String command = "openssl x509 -inform der -in %s -out %s";
            return LinuxCommand.executeCommand(String.format(command, inCer, ourPem), null);
        }
        return -1;
    }

    /**
     * 导出P12文件
     *
     * @param inPrivateKey
     *         私钥
     * @param inCertFilePem
     *         PEM 格式的证书 ,(*.cer 格式转 *.pem 参考方法{@link #cer2pem(String, String)})
     * @param outP12
     *         out p12 file
     * @param outP12Password
     *         p12密码
     */
    public static int exportP12WithPrivateKeyAndCertpem (String inPrivateKey, String inCertFilePem, String outP12, String outP12Password) {
        if (StringUtils.isNoneBlank(inPrivateKey, inCertFilePem)) {
            String command = "openssl pkcs12 -export -inkey  %s -in %s -out %s -password pass:%s";
            return LinuxCommand.executeCommand(String.format(command, inPrivateKey, inCertFilePem, outP12, outP12Password), null);
        }
        return -1;
    }

    /**
     * 从P12文件提取用户证书文件
     *
     * @param inP12
     *         in p12 file
     * @param inP12Password
     *         p12 password
     * @param outWithKeys
     *         true add param <code>-nokeys</code> false not
     * @param outCertFile
     *         输出证书到文件(两种格式:*.pem  or *.crt)
     */
    public static int extractUserCertFromP12 (String inP12, String inP12Password, boolean outWithKeys, String outCertFile) {

        if (StringUtils.isNoneBlank(inP12, outCertFile)) {
            String command = "openssl pkcs12 -in %s -passin pass:%s -clcerts %s -out cert.pem";
            return LinuxCommand.executeCommand(String.format(command,
                    inP12,
                    StringUtils.isNoneBlank(inP12Password) ? inP12Password : "",
                    outWithKeys ? "-nokeys" : "",
                    outCertFile
            ), null);
        }
        return -1;
    }


    /**
     * 从p12提取用户私钥
     *
     * @param inP12
     *         in p12 file
     * @param inP12Password
     *         p12 password
     * @param outPrivateKey
     *         out private key (两种格式都可以: *.pem or *.key)
     */
    public static int extractPrivateKeyFromP12 (String inP12, String inP12Password, String outPrivateKey) {

        if (StringUtils.isNoneBlank(inP12, outPrivateKey)) {
            String command = "openssl pkcs12 -in %s -passin pass:%s -nocerts -out %s";
            return LinuxCommand.executeCommand(String.format(command,
                    inP12,
                    StringUtils.isNoneBlank(inP12Password) ? inP12Password : "",
                    outPrivateKey
            ), null);
        }
        return -1;
    }

    /**
     * 清楚私钥密码
     *
     * @param inPrivateKey
     *         in
     * @param outPrivateKey
     *         out
     */
    public static int cleanPrivateKeysPassPhrase (String inPrivateKey, String outPrivateKey) {
        if (StringUtils.isNoneBlank(inPrivateKey, outPrivateKey)) {
            String command = "openssl rsa -in %s -out %s";
            return LinuxCommand.executeCommand(String.format(command, inPrivateKey, outPrivateKey), null);
        }
        return -1;
    }

    /**
     * 校验证书有效性
     *
     * @param cert
     *         证书路径
     */
    public static Boolean validateCert (String cert) {
        if (StringUtils.isNoneBlank(cert)) {
            int code = LinuxCommand.executeCommand("security verify-cert -c " + cert, null);
            return code == 0;
        }
        return false;
    }

}
