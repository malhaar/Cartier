package xyz.vopen.cartier.provision.ext.response;

import java.util.List;

/**
 * xyz.vopen.cartier.provision.ext.response
 *
 * @author Elve.xu [xuhw@yyft.com]
 * @version v1.0 - 18/07/2017.
 */
public class CheckPermissionsResponse extends ErrorBasicResponse {


    private static final long serialVersionUID = 8924145479901632988L;
    /**
     * userLocale : en_US
     * requestUrl : https://developer.apple.com:443/services-account/checkPermissions
     * permissions : [{"permission":"team.certificate.ca.available","accessAllowed":true}]
     * creationTimestamp : 2017-07-18T09:20:20Z
     * resultCode : 0
     * protocolVersion :
     * responseId : 8c047b4a-c6b1-4e3d-8068-a0ebe6ef523c
     */
    private String userLocale;
    private String requestUrl;
    private List<PermissionsEntity> permissions;
    private String creationTimestamp;
    private int resultCode;
    private String protocolVersion;
    private String responseId;

    public void setUserLocale (String userLocale) {
        this.userLocale = userLocale;
    }

    public void setRequestUrl (String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public void setPermissions (List<PermissionsEntity> permissions) {
        this.permissions = permissions;
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

    public void setResponseId (String responseId) {
        this.responseId = responseId;
    }

    public String getUserLocale () {
        return userLocale;
    }

    public String getRequestUrl () {
        return requestUrl;
    }

    public List<PermissionsEntity> getPermissions () {
        return permissions;
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

    public String getResponseId () {
        return responseId;
    }

    public static class PermissionsEntity {
        /**
         * permission : team.certificate.ca.available
         * accessAllowed : true
         */
        private String permission;
        private boolean accessAllowed;

        public void setPermission (String permission) {
            this.permission = permission;
        }

        public void setAccessAllowed (boolean accessAllowed) {
            this.accessAllowed = accessAllowed;
        }

        public String getPermission () {
            return permission;
        }

        public boolean isAccessAllowed () {
            return accessAllowed;
        }
    }
}
