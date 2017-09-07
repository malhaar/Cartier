package xyz.vopen.cartier.provision.ext.response;

import java.util.List;

/**
 * xyz.vopen.cartier.provision.ext.response
 *
 * @author Elve.xu [xuhw@yyft.com]
 * @version v1.0 - 18/07/2017.
 */
public class ListAppIdsResponse extends ErrorBasicResponse {


    /**
     * totalRecords : 2
     * userLocale : en_US
     * pageNumber : 1
     * resultCode : 0
     * pageSize : 500
     * isAdmin : true
     * isMember : false
     * appIds : [{"identifier":"*","enabledFeatures":[],"isProdPushEnabled":null,"associatedCloudContainers":null,"associatedIdentifiers":null,"associatedApplicationGroupsCount":null,"prefix":"KCQ55TH55X","canEdit":true,"features":{},"isDevPushEnabled":null,"associatedIdentifiersCount":null,"name":"pyw","associatedCloudContainersCount":null,"isWildCard":true,"canDelete":true,"isDuplicate":false,"associatedApplicationGroups":null,"appIdId":"NSJMQ2QW8G","appIdPlatform":"ios"},{"identifier":"*","enabledFeatures":[],"isProdPushEnabled":null,"associatedCloudContainers":null,"associatedIdentifiers":null,"associatedApplicationGroupsCount":null,"prefix":"KCQ55TH55X","canEdit":true,"features":{},"isDevPushEnabled":null,"associatedIdentifiersCount":null,"name":"randomAppIdName","associatedCloudContainersCount":null,"isWildCard":true,"canDelete":true,"isDuplicate":false,"associatedApplicationGroups":null,"appIdId":"D5DUF498A2","appIdPlatform":"ios"}]
     * isAgent : true
     * requestId : 46d323a6-fedf-4e3a-y256-044eecc033b6
     * requestUrl : https://developer.apple.com:443/services-account/QH65B2/account/ios/identifiers/listAppIds.action?content-type=text/x-url-arguments&accept=application/json&requestId=46d323a6-fedf-4e3a-y256-044eecc033b6&userLocale=en_US&teamId=KCQ55TH55X&onlyCountLists=true
     * creationTimestamp : 2017-07-18T10:56:16Z
     * protocolVersion : QH65B2
     * responseId : a0abad63-7a44-4e66-aa64-56c76b6853f2
     */
    private int totalRecords;
    private String userLocale;
    private int pageNumber;
    private int resultCode;
    private int pageSize;
    private boolean isAdmin;
    private boolean isMember;
    private List<AppIdsEntity> appIds;
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

    public void setIsAdmin (boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public void setIsMember (boolean isMember) {
        this.isMember = isMember;
    }

    public void setAppIds (List<AppIdsEntity> appIds) {
        this.appIds = appIds;
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

    public boolean isIsAdmin () {
        return isAdmin;
    }

    public boolean isIsMember () {
        return isMember;
    }

    public List<AppIdsEntity> getAppIds () {
        return appIds;
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

    public static class AppIdsEntity {
        /**
         * identifier : *
         * enabledFeatures : []
         * isProdPushEnabled : null
         * associatedCloudContainers : null
         * associatedIdentifiers : null
         * associatedApplicationGroupsCount : null
         * prefix : KCQ55TH55X
         * canEdit : true
         * features : {}
         * isDevPushEnabled : null
         * associatedIdentifiersCount : null
         * name : pyw
         * associatedCloudContainersCount : null
         * isWildCard : true
         * canDelete : true
         * isDuplicate : false
         * associatedApplicationGroups : null
         * appIdId : NSJMQ2QW8G
         * appIdPlatform : ios
         */
        private String identifier;
        private List<?> enabledFeatures;
        private String isProdPushEnabled;
        private String associatedCloudContainers;
        private String associatedIdentifiers;
        private String associatedApplicationGroupsCount;
        private String prefix;
        private boolean canEdit;
        private FeaturesEntity features;
        private String isDevPushEnabled;
        private String associatedIdentifiersCount;
        private String name;
        private String associatedCloudContainersCount;
        private boolean isWildCard;
        private boolean canDelete;
        private boolean isDuplicate;
        private String associatedApplicationGroups;
        private String appIdId;
        private String appIdPlatform;

        public void setIdentifier (String identifier) {
            this.identifier = identifier;
        }

        public void setEnabledFeatures (List<?> enabledFeatures) {
            this.enabledFeatures = enabledFeatures;
        }

        public void setIsProdPushEnabled (String isProdPushEnabled) {
            this.isProdPushEnabled = isProdPushEnabled;
        }

        public void setAssociatedCloudContainers (String associatedCloudContainers) {
            this.associatedCloudContainers = associatedCloudContainers;
        }

        public void setAssociatedIdentifiers (String associatedIdentifiers) {
            this.associatedIdentifiers = associatedIdentifiers;
        }

        public void setAssociatedApplicationGroupsCount (String associatedApplicationGroupsCount) {
            this.associatedApplicationGroupsCount = associatedApplicationGroupsCount;
        }

        public void setPrefix (String prefix) {
            this.prefix = prefix;
        }

        public void setCanEdit (boolean canEdit) {
            this.canEdit = canEdit;
        }

        public void setFeatures (FeaturesEntity features) {
            this.features = features;
        }

        public void setIsDevPushEnabled (String isDevPushEnabled) {
            this.isDevPushEnabled = isDevPushEnabled;
        }

        public void setAssociatedIdentifiersCount (String associatedIdentifiersCount) {
            this.associatedIdentifiersCount = associatedIdentifiersCount;
        }

        public void setName (String name) {
            this.name = name;
        }

        public void setAssociatedCloudContainersCount (String associatedCloudContainersCount) {
            this.associatedCloudContainersCount = associatedCloudContainersCount;
        }

        public void setIsWildCard (boolean isWildCard) {
            this.isWildCard = isWildCard;
        }

        public void setCanDelete (boolean canDelete) {
            this.canDelete = canDelete;
        }

        public void setIsDuplicate (boolean isDuplicate) {
            this.isDuplicate = isDuplicate;
        }

        public void setAssociatedApplicationGroups (String associatedApplicationGroups) {
            this.associatedApplicationGroups = associatedApplicationGroups;
        }

        public void setAppIdId (String appIdId) {
            this.appIdId = appIdId;
        }

        public void setAppIdPlatform (String appIdPlatform) {
            this.appIdPlatform = appIdPlatform;
        }

        public String getIdentifier () {
            return identifier;
        }

        public List<?> getEnabledFeatures () {
            return enabledFeatures;
        }

        public String getIsProdPushEnabled () {
            return isProdPushEnabled;
        }

        public String getAssociatedCloudContainers () {
            return associatedCloudContainers;
        }

        public String getAssociatedIdentifiers () {
            return associatedIdentifiers;
        }

        public String getAssociatedApplicationGroupsCount () {
            return associatedApplicationGroupsCount;
        }

        public String getPrefix () {
            return prefix;
        }

        public boolean isCanEdit () {
            return canEdit;
        }

        public FeaturesEntity getFeatures () {
            return features;
        }

        public String getIsDevPushEnabled () {
            return isDevPushEnabled;
        }

        public String getAssociatedIdentifiersCount () {
            return associatedIdentifiersCount;
        }

        public String getName () {
            return name;
        }

        public String getAssociatedCloudContainersCount () {
            return associatedCloudContainersCount;
        }

        public boolean isIsWildCard () {
            return isWildCard;
        }

        public boolean isCanDelete () {
            return canDelete;
        }

        public boolean isIsDuplicate () {
            return isDuplicate;
        }

        public String getAssociatedApplicationGroups () {
            return associatedApplicationGroups;
        }

        public String getAppIdId () {
            return appIdId;
        }

        public String getAppIdPlatform () {
            return appIdPlatform;
        }

        public class FeaturesEntity {}
    }
}
