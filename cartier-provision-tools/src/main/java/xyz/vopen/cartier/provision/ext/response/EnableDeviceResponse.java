package xyz.vopen.cartier.provision.ext.response;

/**
 * xyz.vopen.cartier.provision.ext.response
 *
 * @author Elve.xu [xuhw@yyft.com]
 * @version v1.0 - 19/07/2017.
 */
public class EnableDeviceResponse extends ErrorBasicResponse {

    private static final long serialVersionUID = 7701695519514250252L;


    /**
     * validationMessages : null
     * userLocale : en_US
     * resultCode : 0
     * nextDeviceResetDate : null
     * isAdmin : true
     * isMember : false
     * failedDevices : null
     * isAgent : true
     * requestId : 4532a964-be17-4ef0-y238-b5110be3ec36
     * requestUrl : https://developer.apple.com:443/services-account/QH65B2/account/ios/device/enableDevice.action?content-type=text/x-url-arguments&accept=application/json&requestId=4532a964-be17-4ef0-y238-b5110be3ec36&userLocale=en_US&teamId=KCQ55TH55X&deviceId=&deviceNumber=251f557cdcd3fdb8acc73893d58962a3f21abdfb&displayId=G35G37Q23G
     * creationTimestamp : 2017-07-19T06:08:04Z
     * protocolVersion : QH65B2
     * device : {"name":"251f557c","deviceClass":"iphone","model":"iPhone 6 Plus","devicePlatform":"ios","deviceId":"G35G37Q23G","deviceNumber":"251f557cdcd3fdb8acc73893d58962a3f21abdfb","status":"c"}
     * responseId : ca420132-6b0e-4be9-b414-9b38a2973512
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
    private DeviceEntity device;
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

    public void setDevice (DeviceEntity device) {
        this.device = device;
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

    public DeviceEntity getDevice () {
        return device;
    }

    public String getResponseId () {
        return responseId;
    }

    public static class DeviceEntity {
        /**
         * name : 251f557c
         * deviceClass : iphone
         * model : iPhone 6 Plus
         * devicePlatform : ios
         * deviceId : G35G37Q23G
         * deviceNumber : 251f557cdcd3fdb8acc73893d58962a3f21abdfb
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
