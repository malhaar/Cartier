package xyz.vopen.cartier.surface;

/**
 * Application Utils
 *
 * @author Elve.xu [xuhw@yyft.com]
 * @version v1.0 - 06/03/2017.
 */
public final class Applications {

    /**
     * Get Application Runtime Option with main method args
     *
     * @param key
     *         option's key
     *
     * @return if key is set with -O , return value of key ,otherwise return null
     */
    public static String getOption (String key) {

        if (key == null || key.trim().length() == 0 || CartierApplicationContext._options == null) {
            return null;
        }

        return CartierApplicationContext._options.get(key);
    }

    /**
     * Get Application Runtime Properties with main method args
     *
     * @param key
     *         option's key
     *
     * @return value
     */
    public static Object getProperties (String key) {
        if (key == null || key.trim().length() == 0 || CartierApplicationContext._applicationProperties == null) {
            return null;
        }
        return CartierApplicationContext._applicationProperties.get(key);
    }

    /**
     * Get Application runtime Home Path
     *
     * @return return application base home path
     */
    public static String getRuntimeHome () {
        return CartierApplicationContext._root;
    }

}
