package xyz.vopen.cartier.provision.ext.response;

import java.util.List;

/**
 * xyz.vopen.cartier.provision.ext.response
 *
 * @author Elve.xu [xuhw@yyft.com]
 * @version v1.0 - 19/07/2017.
 */
public class AddAppIdResponse extends ErrorBasicResponse {


    private static final long serialVersionUID = 8558657426546841944L;


    /**
     * userLocale : en_US
     * isAgent : true
     * requestId : 346b6372-c809-4046-y620-781bb44dc5ab
     * requestUrl : https://developer.apple.com:443/services-account/QH65B2/account/ios/identifiers/addAppId.action?content-type=text/x-url-arguments&accept=application/json&requestId=346b6372-c809-4046-y620-781bb44dc5ab&userLocale=en_US&teamId=KCQ55TH55X
     * appId : {"identifier":"*","enabledFeatures":[],"isProdPushEnabled":false,"associatedCloudContainers":null,"associatedIdentifiers":null,"associatedApplicationGroupsCount":null,"prefix":"KCQ55TH55X","canEdit":null,"features":{"gameCenter":false,"HK421J6T7P":false,"iCloud":false,"V66P55NK2I":false,"inAppPurchase":false,"WC421J6T7P":false,"APG3427HIY":false,"push":false,"dataProtection":"","homeKit":false,"LPLF93JG7M":false,"IAD53UNK2F":false,"passbook":false,"cloudKitVersion":1,"SKC3T5S89Y":false},"isDevPushEnabled":false,"associatedIdentifiersCount":null,"name":"randomAppIdName","associatedCloudContainersCount":null,"isWildCard":true,"canDelete":null,"isDuplicate":false,"associatedApplicationGroups":null,"appIdId":"D5DUF498A2","appIdPlatform":"ios"}
     * creationTimestamp : 2017-07-12T09:31:43Z
     * resultCode : 0
     * protocolVersion : QH65B2
     * isAdmin : true
     * isMember : false
     * responseId : 1ac4094a-6530-4833-b709-eb127c507a51
     */
    private String userLocale;
    private boolean isAgent;
    private String requestId;
    private String requestUrl;
    private AppIdEntity appId;
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

    public void setAppId (AppIdEntity appId) {
        this.appId = appId;
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

    public AppIdEntity getAppId () {
        return appId;
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

    public static class AppIdEntity {
        /**
         * identifier : *
         * enabledFeatures : []
         * isProdPushEnabled : false
         * associatedCloudContainers : null
         * associatedIdentifiers : null
         * associatedApplicationGroupsCount : null
         * prefix : KCQ55TH55X
         * canEdit : null
         * features : {"gameCenter":false,"HK421J6T7P":false,"iCloud":false,"V66P55NK2I":false,"inAppPurchase":false,"WC421J6T7P":false,"APG3427HIY":false,"push":false,"dataProtection":"","homeKit":false,"LPLF93JG7M":false,"IAD53UNK2F":false,"passbook":false,"cloudKitVersion":1,"SKC3T5S89Y":false}
         * isDevPushEnabled : false
         * associatedIdentifiersCount : null
         * name : randomAppIdName
         * associatedCloudContainersCount : null
         * isWildCard : true
         * canDelete : null
         * isDuplicate : false
         * associatedApplicationGroups : null
         * appIdId : D5DUF498A2
         * appIdPlatform : ios
         */
        private String identifier;
        private List<?> enabledFeatures;
        private boolean isProdPushEnabled;
        private String associatedCloudContainers;
        private String associatedIdentifiers;
        private String associatedApplicationGroupsCount;
        private String prefix;
        private String canEdit;
        private FeaturesEntity features;
        private boolean isDevPushEnabled;
        private String associatedIdentifiersCount;
        private String name;
        private String associatedCloudContainersCount;
        private boolean isWildCard;
        private String canDelete;
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

        public void setIsProdPushEnabled (boolean isProdPushEnabled) {
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

        public void setCanEdit (String canEdit) {
            this.canEdit = canEdit;
        }

        public void setFeatures (FeaturesEntity features) {
            this.features = features;
        }

        public void setIsDevPushEnabled (boolean isDevPushEnabled) {
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

        public void setCanDelete (String canDelete) {
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

        public boolean isIsProdPushEnabled () {
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

        public String getCanEdit () {
            return canEdit;
        }

        public FeaturesEntity getFeatures () {
            return features;
        }

        public boolean isIsDevPushEnabled () {
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

        public String getCanDelete () {
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

        public static class FeaturesEntity {
            /**
             * gameCenter : false
             * HK421J6T7P : false
             * iCloud : false
             * V66P55NK2I : false
             * inAppPurchase : false
             * WC421J6T7P : false
             * APG3427HIY : false
             * push : false
             * dataProtection :
             * homeKit : false
             * LPLF93JG7M : false
             * IAD53UNK2F : false
             * passbook : false
             * cloudKitVersion : 1
             * SKC3T5S89Y : false
             */
            private boolean gameCenter;
            private boolean HK421J6T7P;
            private boolean iCloud;
            private boolean V66P55NK2I;
            private boolean inAppPurchase;
            private boolean WC421J6T7P;
            private boolean APG3427HIY;
            private boolean push;
            private String dataProtection;
            private boolean homeKit;
            private boolean LPLF93JG7M;
            private boolean IAD53UNK2F;
            private boolean passbook;
            private int cloudKitVersion;
            private boolean SKC3T5S89Y;

            public void setGameCenter (boolean gameCenter) {
                this.gameCenter = gameCenter;
            }

            public void setHK421J6T7P (boolean HK421J6T7P) {
                this.HK421J6T7P = HK421J6T7P;
            }

            public void setICloud (boolean iCloud) {
                this.iCloud = iCloud;
            }

            public void setV66P55NK2I (boolean V66P55NK2I) {
                this.V66P55NK2I = V66P55NK2I;
            }

            public void setInAppPurchase (boolean inAppPurchase) {
                this.inAppPurchase = inAppPurchase;
            }

            public void setWC421J6T7P (boolean WC421J6T7P) {
                this.WC421J6T7P = WC421J6T7P;
            }

            public void setAPG3427HIY (boolean APG3427HIY) {
                this.APG3427HIY = APG3427HIY;
            }

            public void setPush (boolean push) {
                this.push = push;
            }

            public void setDataProtection (String dataProtection) {
                this.dataProtection = dataProtection;
            }

            public void setHomeKit (boolean homeKit) {
                this.homeKit = homeKit;
            }

            public void setLPLF93JG7M (boolean LPLF93JG7M) {
                this.LPLF93JG7M = LPLF93JG7M;
            }

            public void setIAD53UNK2F (boolean IAD53UNK2F) {
                this.IAD53UNK2F = IAD53UNK2F;
            }

            public void setPassbook (boolean passbook) {
                this.passbook = passbook;
            }

            public void setCloudKitVersion (int cloudKitVersion) {
                this.cloudKitVersion = cloudKitVersion;
            }

            public void setSKC3T5S89Y (boolean SKC3T5S89Y) {
                this.SKC3T5S89Y = SKC3T5S89Y;
            }

            public boolean isGameCenter () {
                return gameCenter;
            }

            public boolean isHK421J6T7P () {
                return HK421J6T7P;
            }

            public boolean isICloud () {
                return iCloud;
            }

            public boolean isV66P55NK2I () {
                return V66P55NK2I;
            }

            public boolean isInAppPurchase () {
                return inAppPurchase;
            }

            public boolean isWC421J6T7P () {
                return WC421J6T7P;
            }

            public boolean isAPG3427HIY () {
                return APG3427HIY;
            }

            public boolean isPush () {
                return push;
            }

            public String getDataProtection () {
                return dataProtection;
            }

            public boolean isHomeKit () {
                return homeKit;
            }

            public boolean isLPLF93JG7M () {
                return LPLF93JG7M;
            }

            public boolean isIAD53UNK2F () {
                return IAD53UNK2F;
            }

            public boolean isPassbook () {
                return passbook;
            }

            public int getCloudKitVersion () {
                return cloudKitVersion;
            }

            public boolean isSKC3T5S89Y () {
                return SKC3T5S89Y;
            }
        }
    }
}
