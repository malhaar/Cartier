package xyz.vopen.cartier.provision.ext.response;

import java.util.List;

/**
 * xyz.vopen.cartier.provision.ext.response
 *
 * @author Elve.xu [xuhw@yyft.com]
 * @version v1.0 - 19/07/2017.
 */
public class AddDevicesResponse extends ErrorBasicResponse {


    /**
     * validationMessages : []
     * userLocale : en_US
     * devices : [{"name":"54ac5bd5","deviceClass":"iphone","model":"iPhone 6","devicePlatform":"ios","deviceId":"C78F4C9P5E","deviceNumber":"54ac5bd5602fa151743a707de98dcc1e6f47db2f","status":"c"}]
     * resultCode : 0
     * nextDeviceResetDate : null
     * isAdmin : true
     * isMember : false
     * failedDevices : []
     * isAgent : true
     * requestId : e6e3e846-a0a3-4b64-ybfe-5fc63f6390cd
     * requestUrl : https://developer.apple.com:443/services-account/QH65B2/account/ios/device/addDevices.action?content-type=text/x-url-arguments&accept=application/json&requestId=e6e3e846-a0a3-4b64-ybfe-5fc63f6390cd&userLocale=en_US&teamId=KCQ55TH55X
     * creationTimestamp : 2017-07-19T09:31:18Z
     * protocolVersion : QH65B2
     * responseId : 0269a91b-e12a-46c5-bedc-ab6a6651b5d0
     */
    private List<?> validationMessages;
    private String userLocale;
    private List<DevicesEntity> devices;
    private int resultCode;
    private String nextDeviceResetDate;
    private boolean isAdmin;
    private boolean isMember;
    private List<?> failedDevices;
    private boolean isAgent;
    private String requestId;
    private String requestUrl;
    private String creationTimestamp;
    private String protocolVersion;
    private String responseId;

    public void setValidationMessages (List<?> validationMessages) {
        this.validationMessages = validationMessages;
    }

    public void setUserLocale (String userLocale) {
        this.userLocale = userLocale;
    }

    public void setDevices (List<DevicesEntity> devices) {
        this.devices = devices;
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

    public void setFailedDevices (List<?> failedDevices) {
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

    public List<?> getValidationMessages () {
        return validationMessages;
    }

    public String getUserLocale () {
        return userLocale;
    }

    public List<DevicesEntity> getDevices () {
        return devices;
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

    public List<?> getFailedDevices () {
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

    public static class DevicesEntity {
        /**
         * name : 54ac5bd5
         * deviceClass : iphone
         * model : iPhone 6
         * devicePlatform : ios
         * deviceId : C78F4C9P5E
         * deviceNumber : 54ac5bd5602fa151743a707de98dcc1e6f47db2f
         * status : c
         */
        private String name;
        private String deviceClass;
        private String model;
        private String devicePlatform;
        private String deviceId;
        private String deviceNumber;
        private String status;

        public void setName (String name) {
            this.name = name;
        }

        public void setDeviceClass (String deviceClass) {
            this.deviceClass = deviceClass;
        }

        public void setModel (String model) {
            this.model = model;
        }

        public void setDevicePlatform (String devicePlatform) {
            this.devicePlatform = devicePlatform;
        }

        public void setDeviceId (String deviceId) {
            this.deviceId = deviceId;
        }

        public void setDeviceNumber (String deviceNumber) {
            this.deviceNumber = deviceNumber;
        }

        public void setStatus (String status) {
            this.status = status;
        }

        public String getName () {
            return name;
        }

        public String getDeviceClass () {
            return deviceClass;
        }

        public String getModel () {
            return model;
        }

        public String getDevicePlatform () {
            return devicePlatform;
        }

        public String getDeviceId () {
            return deviceId;
        }

        public String getDeviceNumber () {
            return deviceNumber;
        }

        public String getStatus () {
            return status;
        }
    }
}
