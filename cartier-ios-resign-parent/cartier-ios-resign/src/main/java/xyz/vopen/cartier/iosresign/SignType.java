package xyz.vopen.cartier.iosresign;

/**
 * 签名类型
 *
 * @author Elve.xu [xuhw@yyft.com]
 * @version v1.0 - 28/07/2017.
 */
public enum SignType {

    /**个人证书真机调试签名*/
    development,

    /**个人证书 AppStore 正式签*/
    distribution,

    /**企业证书内部签名*/
    adHoc;


}
