package xyz.vopen.cartier.provision.ext.response;

import java.util.List;

/**
 * xyz.vopen.cartier.provision.ext.response
 *
 * @author Elve.xu [xuhw@yyft.com]
 * @version v1.0 - 19/07/2017.
 */
public class ListProvisioningProfilesResponse extends ErrorBasicResponse {


    private static final long serialVersionUID = 7105515406349812349L;


    /**
     * totalRecords : 1
     * userLocale : en_US
     * pageNumber : 1
     * resultCode : 0
     * pageSize : 500
     * provisioningProfiles : [{"provisioningProfileId":"D775XF725T","deviceIds":[],"distributionType":"limited","proProPlatform":"ios","dateExpire":"2018-07-05T10:51:22Z","isTemplateProfile":false,"isTeamProfile":false,"type":"iOS Development","version":"3","distributionMethod":"limited","dateExpireString":"Jul 5, 2018","certificateIds":[],"name":"pyw","UUID":"bea399c8-cc2a-426e-b7a7-38d03155a68c","isFreeProvisioningProfile":false,"status":"Active"}]
     * isAdmin : true
     * isMember : false
     * isAgent : true
     * requestId : 68f72ab3-e3de-43cb-yeac-a00c94e41c9d
     * requestUrl : https://developer.apple.com:443/services-account/QH65B2/account/ios/profile/listProvisioningProfiles.action?content-type=text/x-url-arguments&accept=application/json&requestId=68f72ab3-e3de-43cb-yeac-a00c94e41c9d&userLocale=en_US&teamId=KCQ55TH55X&includeInactiveProfiles=true&onlyCountLists=true
     * creationTimestamp : 2017-07-12T09:36:57Z
     * protocolVersion : QH65B2
     * responseId : f28bd5c2-8606-4f40-9da9-24adbc9a3442
     */
    private int totalRecords;
    private String userLocale;
    private int pageNumber;
    private int resultCode;
    private int pageSize;
    private List<ProvisioningProfilesEntity> provisioningProfiles;
    private boolean isAdmin;
    private boolean isMember;
    private boolean isAgent;
    private String requestId;
    private String requestUrl;
    private String creationTimestamp;
    private String protocolVersion;
    private String responseId;

    public void setTotalRecords (int totalRecords) {
        this.totalRecords = totalRecords;
    }

    public void setUserLocale (String userLocale) {
        this.userLocale = userLocale;
    }

    public void setPageNumber (int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public void setResultCode (int resultCode) {
        this.resultCode = resultCode;
    }

    public void setPageSize (int pageSize) {
        this.pageSize = pageSize;
    }

    public void setProvisioningProfiles (List<ProvisioningProfilesEntity> provisioningProfiles) {
        this.provisioningProfiles = provisioningProfiles;
    }

    public void setIsAdmin (boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public void setIsMember (boolean isMember) {
        this.isMember = isMember;
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

    public String getUserLocale () {
        return userLocale;
    }

    public int getPageNumber () {
        return pageNumber;
    }

    public int getResultCode () {
        return resultCode;
    }

    public int getPageSize () {
        return pageSize;
    }

    public List<ProvisioningProfilesEntity> getProvisioningProfiles () {
        return provisioningProfiles;
    }

    public boolean isIsAdmin () {
        return isAdmin;
    }

    public boolean isIsMember () {
        return isMember;
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

    public static class ProvisioningProfilesEntity {
        /**
         * provisioningProfileId : D775XF725T
         * deviceIds : []
         * distributionType : limited
         * proProPlatform : ios
         * dateExpire : 2018-07-05T10:51:22Z
         * isTemplateProfile : false
         * isTeamProfile : false
         * type : iOS Development
         * version : 3
         * distributionMethod : limited
         * dateExpireString : Jul 5, 2018
         * certificateIds : []
         * name : pyw
         * UUID : bea399c8-cc2a-426e-b7a7-38d03155a68c
         * isFreeProvisioningProfile : false
         * status : Active
         */
        private String provisioningProfileId;
        private List<?> deviceIds;
        private String distributionType;
        private String proProPlatform;
        private String dateExpire;
        private boolean isTemplateProfile;
        private boolean isTeamProfile;
        private String type;
        private String version;
        private String distributionMethod;
        private String dateExpireString;
        private List<?> certificateIds;
        private String name;
        private String UUID;
        private boolean isFreeProvisioningProfile;
        private String status;

        public void setProvisioningProfileId (String provisioningProfileId) {
            this.provisioningProfileId = provisioningProfileId;
        }

        public void setDeviceIds (List<?> deviceIds) {
            this.deviceIds = deviceIds;
        }

        public void setDistributionType (String distributionType) {
            this.distributionType = distributionType;
        }

        public void setProProPlatform (String proProPlatform) {
            this.proProPlatform = proProPlatform;
        }

        public void setDateExpire (String dateExpire) {
            this.dateExpire = dateExpire;
        }

        public void setIsTemplateProfile (boolean isTemplateProfile) {
            this.isTemplateProfile = isTemplateProfile;
        }

        public void setIsTeamProfile (boolean isTeamProfile) {
            this.isTeamProfile = isTeamProfile;
        }

        public void setType (String type) {
            this.type = type;
        }

        public void setVersion (String version) {
            this.version = version;
        }

        public void setDistributionMethod (String distributionMethod) {
            this.distributionMethod = distributionMethod;
        }

        public void setDateExpireString (String dateExpireString) {
            this.dateExpireString = dateExpireString;
        }

        public void setCertificateIds (List<?> certificateIds) {
            this.certificateIds = certificateIds;
        }

        public void setName (String name) {
            this.name = name;
        }

        public void setUUID (String UUID) {
            this.UUID = UUID;
        }

        public void setIsFreeProvisioningProfile (boolean isFreeProvisioningProfile) {
            this.isFreeProvisioningProfile = isFreeProvisioningProfile;
        }

        public void setStatus (String status) {
            this.status = status;
        }

        public String getProvisioningProfileId () {
            return provisioningProfileId;
        }

        public List<?> getDeviceIds () {
            return deviceIds;
        }

        public String getDistributionType () {
            return distributionType;
        }

        public String getProProPlatform () {
            return proProPlatform;
        }

        public String getDateExpire () {
            return dateExpire;
        }

        public boolean isIsTemplateProfile () {
            return isTemplateProfile;
        }

        public boolean isIsTeamProfile () {
            return isTeamProfile;
        }

        public String getType () {
            return type;
        }

        public String getVersion () {
            return version;
        }

        public String getDistributionMethod () {
            return distributionMethod;
        }

        public String getDateExpireString () {
            return dateExpireString;
        }

        public List<?> getCertificateIds () {
            return certificateIds;
        }

        public String getName () {
            return name;
        }

        public String getUUID () {
            return UUID;
        }

        public boolean isIsFreeProvisioningProfile () {
            return isFreeProvisioningProfile;
        }

        public String getStatus () {
            return status;
        }
    }
}
