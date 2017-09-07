package xyz.vopen.cartier.surface;

import java.util.Map;
import java.util.Properties;

/**
 * Application Context for Cartier
 *
 * @author Elve.xu [xuhw@yyft.com]
 * @version v1.0 - 06/03/2017.
 */
public abstract class CartierApplicationContext {

    /**
     * main method execute parameters
     */
    static Map<String, String> _options;
    static String _root;
    static Properties _applicationProperties;


    public CartierApplicationContext () {
    }

    /**
     * get option
     *
     * @return option's map
     */
    public Map<String, String> getOptions () {
        return _options;
    }

    /**
     * set _options
     *
     * @param options
     *         option's map
     */
    final public void setOptions (Map<String, String> options) {
        _options = options;
    }

    final public void setRuntimeRoot (String root) {
        _root = root;
    }

    final public void setProperties (Properties properties) {
        _applicationProperties = properties;
    }

    /**
     * 获取 Home 目录
     */
    public String getHome () {
        return _root;
    }

    public void release () {

        // release option container
        if (_options != null) {
            _options.clear();
            _options = null;
        }
    }

}
