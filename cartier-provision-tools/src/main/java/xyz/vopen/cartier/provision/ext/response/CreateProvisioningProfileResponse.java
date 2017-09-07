package xyz.vopen.cartier.provision.ext.response;

import java.util.List;

/**
 * xyz.vopen.cartier.provision.ext.response
 *
 * @author Elve.xu [xuhw@yyft.com]
 * @version v1.0 - 19/07/2017.
 */
public class CreateProvisioningProfileResponse extends ErrorBasicResponse {
    private static final long serialVersionUID = -1957287616665400185L;


    /**
     * userLocale : en_US
     * isAgent : true
     * requestId : 372418f4-da48-47a3-yd07-d29d79bf01b0
     * requestUrl : https://developer.apple.com:443/services-account/QH65B2/account/ios/profile/createProvisioningProfile.action?content-type=text/x-url-arguments&accept=application/json&requestId=372418f4-da48-47a3-yd07-d29d79bf01b0&userLocale=en_US&teamId=KCQ55TH55X
     * creationTimestamp : 2017-07-12T09:43:09Z
     * resultCode : 0
     * provisioningProfile : {"provisioningProfileId":"G623CD3HUB","deviceIds":["CJ7K4L28AJ","G35G37Q23G"],"distributionType":"limited","proProPlatform":"ios","dateExpire":"2018-07-12T09:43:09Z","isTemplateProfile":false,"isTeamProfile":false,"type":"iOS Development","version":"3","encodedProfile":"#这里是一段文件内容#","distributionMethod":"limited","dateExpireString":"Jul 12, 2018","certificateIds":["4XM8363UWF"],"filename":"randomProvisioningProfileName.mobileprovision","appId":{"identifier":"*","enabledFeatures":[],"isProdPushEnabled":false,"associatedCloudContainers":null,"associatedIdentifiers":null,"associatedApplicationGroupsCount":null,"prefix":"KCQ55TH55X","canEdit":null,"features":{"gameCenter":false,"HK421J6T7P":false,"iCloud":false,"V66P55NK2I":false,"inAppPurchase":false,"WC421J6T7P":false,"APG3427HIY":false,"push":false,"dataProtection":"","homeKit":false,"LPLF93JG7M":false,"IAD53UNK2F":false,"passbook":false,"cloudKitVersion":1,"SKC3T5S89Y":false},"isDevPushEnabled":false,"associatedIdentifiersCount":null,"name":"randomAppIdName","associatedCloudContainersCount":null,"isWildCard":true,"canDelete":null,"isDuplicate":false,"associatedApplicationGroups":null,"appIdId":"D5DUF498A2","appIdPlatform":"ios"},"name":"randomProvisioningProfileName","UUID":"05991af1-6402-4ba7-b71b-1e90b54379b1","isFreeProvisioningProfile":false,"status":"Active","appIdId":"D5DUF498A2"}
     * protocolVersion : QH65B2
     * isAdmin : true
     * isMember : false
     * responseId : 465145fb-f14d-48eb-bbba-a2a1126f5d74
     */
    private String userLocale;
    private boolean isAgent;
    private String requestId;
    private String requestUrl;
    private String creationTimestamp;
    private int resultCode;
    private ProvisioningProfileEntity provisioningProfile;
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

    public void setProvisioningProfile (ProvisioningProfileEntity provisioningProfile) {
        this.provisioningProfile = provisioningProfile;
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

    public ProvisioningProfileEntity getProvisioningProfile () {
        return provisioningProfile;
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

    public static class ProvisioningProfileEntity {
        /**
         * provisioningProfileId : G623CD3HUB
         * deviceIds : ["CJ7K4L28AJ","G35G37Q23G"]
         * distributionType : limited
         * proProPlatform : ios
         * dateExpire : 2018-07-12T09:43:09Z
         * isTemplateProfile : false
         * isTeamProfile : false
         * type : iOS Development
         * version : 3
         * encodedProfile : #这里是一段文件内容#
         * distributionMethod : limited
         * dateExpireString : Jul 12, 2018
         * certificateIds : ["4XM8363UWF"]
         * filename : randomProvisioningProfileName.mobileprovision
         * appId : {"identifier":"*","enabledFeatures":[],"isProdPushEnabled":false,"associatedCloudContainers":null,"associatedIdentifiers":null,"associatedApplicationGroupsCount":null,"prefix":"KCQ55TH55X","canEdit":null,"features":{"gameCenter":false,"HK421J6T7P":false,"iCloud":false,"V66P55NK2I":false,"inAppPurchase":false,"WC421J6T7P":false,"APG3427HIY":false,"push":false,"dataProtection":"","homeKit":false,"LPLF93JG7M":false,"IAD53UNK2F":false,"passbook":false,"cloudKitVersion":1,"SKC3T5S89Y":false},"isDevPushEnabled":false,"associatedIdentifiersCount":null,"name":"randomAppIdName","associatedCloudContainersCount":null,"isWildCard":true,"canDelete":null,"isDuplicate":false,"associatedApplicationGroups":null,"appIdId":"D5DUF498A2","appIdPlatform":"ios"}
         * name : randomProvisioningProfileName
         * UUID : 05991af1-6402-4ba7-b71b-1e90b54379b1
         * isFreeProvisioningProfile : false
         * status : Active
         * appIdId : D5DUF498A2
         */
        private String provisioningProfileId;
        private List<String> deviceIds;
        private String distributionType;
        private String proProPlatform;
        private String dateExpire;
        private boolean isTemplateProfile;
        private boolean isTeamProfile;
        private String type;
        private String version;
        private String encodedProfile;
        private String distributionMethod;
        private String dateExpireString;
        private List<String> certificateIds;
        private String filename;
        private AppIdEntity appId;
        private String name;
        private String UUID;
        private boolean isFreeProvisioningProfile;
        private String status;
        private String appIdId;

        public void setProvisioningProfileId (String provisioningProfileId) {
            this.provisioningProfileId = provisioningProfileId;
        }

        public void setDeviceIds (List<String> deviceIds) {
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

        public void setEncodedProfile (String encodedProfile) {
            this.encodedProfile = encodedProfile;
        }

        public void setDistributionMethod (String distributionMethod) {
            this.distributionMethod = distributionMethod;
        }

        public void setDateExpireString (String dateExpireString) {
            this.dateExpireString = dateExpireString;
        }

        public void setCertificateIds (List<String> certificateIds) {
            this.certificateIds = certificateIds;
        }

        public void setFilename (String filename) {
            this.filename = filename;
        }

        public void setAppId (AppIdEntity appId) {
            this.appId = appId;
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

        public void setAppIdId (String appIdId) {
            this.appIdId = appIdId;
        }

        public String getProvisioningProfileId () {
            return provisioningProfileId;
        }

        public List<String> getDeviceIds () {
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

        public String getEncodedProfile () {
            return encodedProfile;
        }

        public String getDistributionMethod () {
            return distributionMethod;
        }

        public String getDateExpireString () {
            return dateExpireString;
        }

        public List<String> getCertificateIds () {
            return certificateIds;
        }

        public String getFilename () {
            return filename;
        }

        public AppIdEntity getAppId () {
            return appId;
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

        public String getAppIdId () {
            return appIdId;
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
}
