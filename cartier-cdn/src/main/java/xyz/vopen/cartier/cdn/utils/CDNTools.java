package xyz.vopen.cartier.cdn.utils;

import xyz.vopen.cartier.cdn.CDNType;
import xyz.vopen.cartier.cdn.ks3.Ks3Handler;

/**
 * CDN tools
 *
 * @author Elve.xu [xuhw@yyft.com]
 * @version v1.0 - 09/08/2017.
 */
public final class CDNTools {


    /**
     * Get Publish file http(s) url
     *
     * @param type
     *         cdn type
     * @param bucket
     *         cdn storage dir name
     * @param key
     *         file key
     *
     * @return https url
     */
    public static String getPublicHttpsUrl (CDNType type, String bucket, String key, boolean https) {

        if (type != null
                && bucket != null
                && key != null
                && bucket.trim().length() > 0
                && key.trim().length() > 0) {

            switch (type) {
                case ks3:
                    // endpoint/{bucket}/{key}
                    return (https ? "https://" : "http://") + Ks3Handler.DEFAULT_ENDPOINT + "/" + bucket + "/" + key;
            }
        }
        return null;
    }

}
