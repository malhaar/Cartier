package xyz.vopen.cartier.provision.ext.response;

import xyz.vopen.cartier.commons.utils.DomainSerializable;

/**
 * xyz.vopen.cartier.provision.ext.response
 *
 * @author Elve.xu [xuhw@yyft.com]
 * @version v1.0 - 19/07/2017.
 */
public class ErrorBasicResponse extends DomainSerializable {
    private static final long serialVersionUID = -1883223605135071138L;

    /**
     * 与resultCode对应的描述信息
     */
    private String resultString;

    /**
     *
     */
    private String userString;

    /**
     * HTTP 状态码
     */
    private int httpCode;

    public String getResultString () {
        return resultString;
    }

    public void setResultString (String resultString) {
        this.resultString = resultString;
    }

    public String getUserString () {
        return userString;
    }

    public void setUserString (String userString) {
        this.userString = userString;
    }

    public int getHttpCode () {
        return httpCode;
    }

    public void setHttpCode (int httpCode) {
        this.httpCode = httpCode;
    }

}
