package xyz.vopen.cartier.provision.ext.response;

/**
 * xyz.vopen.cartier.provision.ext.response
 *
 * @author Elve.xu [xuhw@yyft.com]
 * @version v1.0 - 24/07/2017.
 */
public class DeleteResponse extends ErrorBasicResponse {


    private static final long serialVersionUID = -9058232799059479982L;

    /**
     * userLocale : en_US
     * isAgent : true
     * requestId : bca6d0f0-2e46-4386-y840-c4663bdfef49
     * requestUrl : https://developer.apple.com:443/services-account/QH65B2/account/ios/identifiers/deleteAppId.action
     * creationTimestamp : 2017-07-24T02:50:16Z
     * resultCode : 0
     * protocolVersion : QH65B2
     * isAdmin : true
     * isMember : false
     * responseId : 515bc25e-663f-4c72-a558-3ea5f0caa34f
     */
    private String userLocale;
    private boolean isAgent;
    private String requestId;
    private String requestUrl;
    private String creationTimestamp;
    private int resultCode;
    private String protocolVersion;
    private boolean isAdmin;
    private boolean isMember;
    private String responseId;

    public void setUserLocale (String userLocale) {
        this.userLocale = userLocale;
    }

    public void setIsAgent (boolean isAgent) {
        this.isAgent = isAgent;
    }

    public void setRequestId (String requestId) {
        this.requestId = requestId;
    }

    public void setRequestUrl (String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public void setCreationTimestamp (String creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }

    public void setResultCode (int resultCode) {
        this.resultCode = resultCode;
    }

    public void setProtocolVersion (String protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public void setIsAdmin (boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public void setIsMember (boolean isMember) {
        this.isMember = isMember;
    }

    public void setResponseId (String responseId) {
        this.responseId = responseId;
    }

    public String getUserLocale () {
        return userLocale;
    }

    public boolean isIsAgent () {
        return isAgent;
    }

    public String getRequestId () {
        return requestId;
    }

    public String getRequestUrl () {
        return requestUrl;
    }

    public String getCreationTimestamp () {
        return creationTimestamp;
    }

    public int getResultCode () {
        return resultCode;
    }

    public String getProtocolVersion () {
        return protocolVersion;
    }

    public boolean isIsAdmin () {
        return isAdmin;
    }

    public boolean isIsMember () {
        return isMember;
    }

    public String getResponseId () {
        return responseId;
    }
}
