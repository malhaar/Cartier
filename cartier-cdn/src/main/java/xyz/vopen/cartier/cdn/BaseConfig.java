package xyz.vopen.cartier.cdn;

import java.util.Properties;

/**
 * xyz.vopen.cartier.cdn
 *
 * @author Elve.xu [xuhw@yyft.com]
 * @version v1.0 - 20/07/2017.
 */
public class BaseConfig {

    protected CDNType type;

    protected Properties config;

    public BaseConfig (CDNType type, Properties config) {
        this.type = type;
        this.config = config;
    }

    public CDNType getType () {
        return type;
    }

    public Properties getConfigProperties () {
        return config;
    }
}
