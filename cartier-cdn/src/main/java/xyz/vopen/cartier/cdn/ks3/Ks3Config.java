package xyz.vopen.cartier.cdn.ks3;

import xyz.vopen.cartier.cdn.BaseConfig;
import xyz.vopen.cartier.cdn.CDNType;

import java.util.Properties;

/**
 * Ks3 config
 *
 * @author Elve.xu [xuhw@yyft.com]
 * @version v1.0 - 20/07/2017.
 * @see <code>https://docs.ksyun.com/read/latest/65/_book/sdk/java.html</code>
 */
public class Ks3Config extends BaseConfig {

    public Ks3Config (CDNType type, Properties config) {
        super(type, config);
    }

    /**
     * key
     */
    public String getAccessKeyID () {
        return config.getProperty("accessKeyID");
    }

    /**
     * secret
     */
    public String getAccessKeySecret () {
        return config.getProperty("accessKeySecret");
    }

    /**
     * domaon
     *
     * @return domain
     */
    public String getDomain () {
        return config.getProperty("domain");
    }

    /**
     * enable https or not
     *
     * @return true or false
     */
    public Boolean isEnableHttps () {
        if (config == null) return true;
        return Boolean.valueOf(config.getProperty("enableHttps", "true"));
    }

    /**
     * auto create bucket
     *
     * @return true or false
     */
    public Boolean isAutoCreateBucket () {
        if (config == null) return false;
        return Boolean.valueOf(config.getProperty("autoCreateBucket", "false"));
    }

    /**
     * block size
     */
    public long blockLength () {
        if (config.containsKey("block.length")) {
            String value = config.getProperty("block.length", "104857600");
            return Long.valueOf(value);
        }
        return 104857600;
    }

    /**
     * min down block file size
     */
    public long minDownloadFileLength () {
        if (config.containsKey("min.download.file.length")) {
            String value = config.getProperty("min.download.file.length", "209715200");
            return Long.valueOf(value);
        }
        return 209715200;
    }

    /**
     * min block upload size
     */
    public long minUploadFileLength () {
        if (config.containsKey("min.upload.file.length")) {
            String value = config.getProperty("min.upload.file.length", "209715200");
            return Long.valueOf(value);
        }
        return 209715200;
    }


}
