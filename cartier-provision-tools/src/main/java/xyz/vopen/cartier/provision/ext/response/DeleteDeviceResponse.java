package xyz.vopen.cartier.provision.ext.response;

import xyz.vopen.cartier.commons.utils.DomainSerializable;

/**
 * xyz.vopen.cartier.provision.ext.response
 *
 * @author Elve.xu [xuhw@yyft.com]
 * @version v1.0 - 19/07/2017.
 */
public class DeleteDeviceResponse extends ErrorBasicResponse {


    private static final long serialVersionUID = 830445997361409191L;


    /**
     * validationMessages : null
     * userLocale : en_US
     * resultCode : 0
     * nextDeviceResetDate : null
     * isAdmin : true
     * isMember : false
     * failedDevices : null
     * isAgent : true
     * requestId : 5c705cac-b7e9-44bb-y68d-6706309e83a1
     * requestUrl : https://developer.apple.com:443/services-account/QH65B2/account/ios/device/deleteDevice.action
     * creationTimestamp : 2017-07-19T06:04:51Z
     * protocolVersion : QH65B2
     * responseId : 95d49eb2-0c6c-49e6-a79e-b8596c704ff3
     */
    private String validationMessages;
    private String userLocale;
    private int resultCode;
    private String nextDeviceResetDate;
    private boolean isAdmin;
    private boolean isMember;
    private String failedDevices;
    private boolean isAgent;
    private String requestId;
    private String requestUrl;
    private String creationTimestamp;
    private String protocolVersion;
    private String responseId;

    public void setValidationMessages (String validationMessages) {
        this.validationMessages = validationMessages;
    }

    public void setUserLocale (String userLocale) {
        this.userLocale = userLocale;
    }

    public void setResultCode (int resultCode) {
        this.resultCode = resultCode;
    }

    public void setNextDeviceResetDate (String nextDeviceResetDate) {
        this.nextDeviceResetDate = nextDeviceResetDate;
    }

    public void setIsAdmin (boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public void setIsMember (boolean isMember) {
        this.isMember = isMember;
    }

    public void setFailedDevices (String failedDevices) {
        this.failedDevices = failedDevices;
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

    public void setProtocolVersion (String protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public void setResponseId (String responseId) {
        this.responseId = responseId;
    }

    public String getValidationMessages () {
        return validationMessages;
    }

    public String getUserLocale () {
        return userLocale;
    }

    public int getResultCode () {
        return resultCode;
    }

    public String getNextDeviceResetDate () {
        return nextDeviceResetDate;
    }

    public boolean isIsAdmin () {
        return isAdmin;
    }

    public boolean isIsMember () {
        return isMember;
    }

    public String getFailedDevices () {
        return failedDevices;
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

    public String getProtocolVersion () {
        return protocolVersion;
    }

    public String getResponseId () {
        return responseId;
    }
}
