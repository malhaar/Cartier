package xyz.vopen.cartier.provision.ext.response;

import java.util.List;

/**
 * xyz.vopen.cartier.provision.ext.response
 *
 * @author Elve.xu [xuhw@yyft.com]
 * @version v1.0 - 19/07/2017.
 */
public class ListDevicesResponse extends ErrorBasicResponse {


    private static final long serialVersionUID = 2896671509980929957L;


    /**
     * totalRecords : 2
     * validationMessages : null
     * userLocale : en_US
     * pageNumber : 1
     * devices : [{"name":"106e64c7","deviceClass":"ipad","model":"iPad Air 2 (Model A1566)","devicePlatform":"ios","deviceId":"J7JB35M69Y","deviceNumber":"106e64c71573527435b8f4c7f0579f066adfdd02","status":"c"},{"name":"9b3d32f9","deviceClass":"ipad","model":"iPad 2 Wi-Fi","devicePlatform":"ios","deviceId":"46GN4ZQC45","deviceNumber":"9b3d32f974724864fe31c0377a113b2323e763b2","status":"c"}]
     * resultCode : 0
     * pageSize : 500
     * nextDeviceResetDate : null
     * isAdmin : true
     * isMember : false
     * failedDevices : null
     * deviceBenefits : [{"availableQuantity":100,"maxQuantity":100,"deviceClass":"PC","deviceClassEnum":"AppleTV","benefitId":40,"platform":"ios"},{"availableQuantity":100,"maxQuantity":100,"deviceClass":"WATCH","deviceClassEnum":"WATCH","benefitId":41,"platform":"ios"},{"availableQuantity":98,"maxQuantity":100,"deviceClass":"IPAD","deviceClassEnum":"IPAD","benefitId":42,"platform":"ios"},{"availableQuantity":16,"maxQuantity":100,"deviceClass":"IPHONE","deviceClassEnum":"IPHONE","benefitId":43,"platform":"ios"},{"availableQuantity":99,"maxQuantity":100,"deviceClass":"IPOD","deviceClassEnum":"IPOD","benefitId":44,"platform":"ios"}]
     * isAgent : true
     * requestId : ef5d37c2-b498-4375-y824-95f85db30916
     * requestUrl : https://developer.apple.com:443/services-account/QH65B2/account/ios/device/listDevices.action?content-type=text/x-url-arguments&accept=application/json&requestId=ef5d37c2-b498-4375-y824-95f85db30916&userLocale=en_US&teamId=KCQ55TH55X&includeRemovedDevices=true&includeAvailability=true&deviceClasses=ipad
     * creationTimestamp : 2017-07-19T03:51:07Z
     * protocolVersion : QH65B2
     * responseId : 186d841c-be1f-4fc1-93ee-0ddac76dc272
     */
    private int totalRecords;
    private String validationMessages;
    private String userLocale;
    private int pageNumber;
    private List<DevicesEntity> devices;
    private int resultCode;
    private int pageSize;
    private String nextDeviceResetDate;
    private boolean isAdmin;
    private boolean isMember;
    private String failedDevices;
    private List<DeviceBenefitsEntity> deviceBenefits;
    private boolean isAgent;
    private String requestId;
    private String requestUrl;
    private String creationTimestamp;
    private String protocolVersion;
    private String responseId;

    public void setTotalRecords (int totalRecords) {
        this.totalRecords = totalRecords;
    }

    public void setValidationMessages (String validationMessages) {
        this.validationMessages = validationMessages;
    }

    public void setUserLocale (String userLocale) {
        this.userLocale = userLocale;
    }

    public void setPageNumber (int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public void setDevices (List<DevicesEntity> devices) {
        this.devices = devices;
    }

    public void setResultCode (int resultCode) {
        this.resultCode = resultCode;
    }

    public void setPageSize (int pageSize) {
        this.pageSize = pageSize;
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

    public void setDeviceBenefits (List<DeviceBenefitsEntity> deviceBenefits) {
        this.deviceBenefits = deviceBenefits;
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

    public int getTotalRecords () {
        return totalRecords;
    }

    public String getValidationMessages () {
        return validationMessages;
    }

    public String getUserLocale () {
        return userLocale;
    }

    public int getPageNumber () {
        return pageNumber;
    }

    public List<DevicesEntity> getDevices () {
        return devices;
    }

    public int getResultCode () {
        return resultCode;
    }

    public int getPageSize () {
        return pageSize;
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

    public List<DeviceBenefitsEntity> getDeviceBenefits () {
        return deviceBenefits;
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
         * name : 106e64c7
         * deviceClass : ipad
         * model : iPad Air 2 (Model A1566)
         * devicePlatform : ios
         * deviceId : J7JB35M69Y
         * deviceNumber : 106e64c71573527435b8f4c7f0579f066adfdd02
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

    public static class DeviceBenefitsEntity {
        /**
         * availableQuantity : 100
         * maxQuantity : 100
         * deviceClass : PC
         * deviceClassEnum : AppleTV
         * benefitId : 40
         * platform : ios
         */
        private int availableQuantity;
        private int maxQuantity;
        private String deviceClass;
        private String deviceClassEnum;
        private int benefitId;
        private String platform;

        public void setAvailableQuantity (int availableQuantity) {
            this.availableQuantity = availableQuantity;
        }

        public void setMaxQuantity (int maxQuantity) {
            this.maxQuantity = maxQuantity;
        }

        public void setDeviceClass (String deviceClass) {
            this.deviceClass = deviceClass;
        }

        public void setDeviceClassEnum (String deviceClassEnum) {
            this.deviceClassEnum = deviceClassEnum;
        }

        public void setBenefitId (int benefitId) {
            this.benefitId = benefitId;
        }

        public void setPlatform (String platform) {
            this.platform = platform;
        }

        public int getAvailableQuantity () {
            return availableQuantity;
        }

        public int getMaxQuantity () {
            return maxQuantity;
        }

        public String getDeviceClass () {
            return deviceClass;
        }

        public String getDeviceClassEnum () {
            return deviceClassEnum;
        }

        public int getBenefitId () {
            return benefitId;
        }

        public String getPlatform () {
            return platform;
        }
    }
}
