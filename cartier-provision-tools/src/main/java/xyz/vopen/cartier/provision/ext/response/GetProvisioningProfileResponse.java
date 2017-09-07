package xyz.vopen.cartier.provision.ext.response;

import java.util.List;

/**
 * xyz.vopen.cartier.provision.ext.response
 *
 * @author Elve.xu [xuhw@yyft.com]
 * @version v1.0 - 11/08/2017.
 */
public class GetProvisioningProfileResponse extends ErrorBasicResponse {


    /**
     * userLocale : en_US
     * isAgent : true
     * requestId : 5e497f15-cdde-4abd-y3f1-26762000e68d
     * requestUrl : https://developer.apple.com:443/services-account/QH65B2/account/ios/profile/getProvisioningProfile.action?content-type=text/x-url-arguments&accept=application/json&requestId=5e497f15-cdde-4abd-y3f1-26762000e68d&userLocale=en_US&teamId=B7W45E3MGB&includeInactiveProfiles=true&onlyCountLists=false&type=limited
     * creationTimestamp : 2017-08-11T13:24:25Z
     * resultCode : 0
     * provisioningProfile : {"provisioningProfileId":"8VCJL6EYLS","deviceIds":["MRZM3396F6"],"distributionType":"limited","proProPlatform":"ios","dateExpire":"2018-08-11T08:56:08Z","devices":[{"name":"251f557","deviceClass":"iphone","model":"iPhone 6 Plus","devicePlatform":"ios","deviceId":"MRZM3396F6","deviceNumber":"251f557cdcd3fdb8acc73893d58962a3f21abdfb","status":"c"}],"isTemplateProfile":false,"isTeamProfile":false,"type":"iOS Development","version":"3","distributionMethod":"limited","dateExpireString":"Aug 11, 2018","certificateIds":["Z45SA3ZDZH"],"certificates":[{"expirationDateString":"Aug 11, 2018","serialNumber":"1E74E43685CC7134","displayName":null,"certificateId":"Z45SA3ZDZH","name":"iOS Development: Nan Jun","certificatePlatform":"ios","hasAskKey":false,"status":"Issued","statusCode":0,"expirationDate":"2018-08-11","certificateType":{"ownerType":"teamMember","permissionType":"development","distributionMethod":"app","distributionType":"development","daysOverlap":364,"name":"iOS Development","certificateTypeDisplayId":"5QPB9NHCEI","platform":"ios","maxActive":1}}],"appId":{"identifier":"*","enabledFeatures":[],"isProdPushEnabled":false,"associatedCloudContainers":null,"associatedIdentifiers":null,"associatedApplicationGroupsCount":null,"prefix":"B7W45E3MGB","canEdit":null,"features":{"gameCenter":false,"HK421J6T7P":false,"iCloud":false,"V66P55NK2I":false,"inAppPurchase":false,"WC421J6T7P":false,"APG3427HIY":false,"push":false,"dataProtection":"","homeKit":false,"LPLF93JG7M":false,"IAD53UNK2F":false,"passbook":false,"cloudKitVersion":1,"SKC3T5S89Y":false},"isDevPushEnabled":false,"associatedIdentifiersCount":null,"name":"64H234BQUH4S","associatedCloudContainersCount":null,"isWildCard":true,"canDelete":null,"isDuplicate":false,"associatedApplicationGroups":null,"appIdId":"R6J9MR45NY","appIdPlatform":"ios"},"name":"64H23VRPLSRF","UUID":"9723fb38-0de0-45e2-8656-08f3c37af5ec","isFreeProvisioningProfile":false,"status":"Active","appIdId":"R6J9MR45NY"}
     * protocolVersion : QH65B2
     * isAdmin : true
     * isMember : false
     * responseId : c0d8a7f6-1b52-4db5-9d5b-632385212913
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

    public class ProvisioningProfileEntity {
        /**
         * provisioningProfileId : 8VCJL6EYLS
         * deviceIds : ["MRZM3396F6"]
         * distributionType : limited
         * proProPlatform : ios
         * dateExpire : 2018-08-11T08:56:08Z
         * devices : [{"name":"251f557","deviceClass":"iphone","model":"iPhone 6 Plus","devicePlatform":"ios","deviceId":"MRZM3396F6","deviceNumber":"251f557cdcd3fdb8acc73893d58962a3f21abdfb","status":"c"}]
         * isTemplateProfile : false
         * isTeamProfile : false
         * type : iOS Development
         * version : 3
         * distributionMethod : limited
         * dateExpireString : Aug 11, 2018
         * certificateIds : ["Z45SA3ZDZH"]
         * certificates : [{"expirationDateString":"Aug 11, 2018","serialNumber":"1E74E43685CC7134","displayName":null,"certificateId":"Z45SA3ZDZH","name":"iOS Development: Nan Jun","certificatePlatform":"ios","hasAskKey":false,"status":"Issued","statusCode":0,"expirationDate":"2018-08-11","certificateType":{"ownerType":"teamMember","permissionType":"development","distributionMethod":"app","distributionType":"development","daysOverlap":364,"name":"iOS Development","certificateTypeDisplayId":"5QPB9NHCEI","platform":"ios","maxActive":1}}]
         * appId : {"identifier":"*","enabledFeatures":[],"isProdPushEnabled":false,"associatedCloudContainers":null,"associatedIdentifiers":null,"associatedApplicationGroupsCount":null,"prefix":"B7W45E3MGB","canEdit":null,"features":{"gameCenter":false,"HK421J6T7P":false,"iCloud":false,"V66P55NK2I":false,"inAppPurchase":false,"WC421J6T7P":false,"APG3427HIY":false,"push":false,"dataProtection":"","homeKit":false,"LPLF93JG7M":false,"IAD53UNK2F":false,"passbook":false,"cloudKitVersion":1,"SKC3T5S89Y":false},"isDevPushEnabled":false,"associatedIdentifiersCount":null,"name":"64H234BQUH4S","associatedCloudContainersCount":null,"isWildCard":true,"canDelete":null,"isDuplicate":false,"associatedApplicationGroups":null,"appIdId":"R6J9MR45NY","appIdPlatform":"ios"}
         * name : 64H23VRPLSRF
         * UUID : 9723fb38-0de0-45e2-8656-08f3c37af5ec
         * isFreeProvisioningProfile : false
         * status : Active
         * appIdId : R6J9MR45NY
         */
        private String provisioningProfileId;
        private List<String> deviceIds;
        private String distributionType;
        private String proProPlatform;
        private String dateExpire;
        private List<DevicesEntity> devices;
        private boolean isTemplateProfile;
        private boolean isTeamProfile;
        private String type;
        private String version;
        private String distributionMethod;
        private String dateExpireString;
        private List<String> certificateIds;
        private List<CertificatesEntity> certificates;
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

        public void setDevices (List<DevicesEntity> devices) {
            this.devices = devices;
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

        public void setCertificateIds (List<String> certificateIds) {
            this.certificateIds = certificateIds;
        }

        public void setCertificates (List<CertificatesEntity> certificates) {
            this.certificates = certificates;
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

        public List<DevicesEntity> getDevices () {
            return devices;
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

        public List<String> getCertificateIds () {
            return certificateIds;
        }

        public List<CertificatesEntity> getCertificates () {
            return certificates;
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

        public class DevicesEntity {
            /**
             * name : 251f557
             * deviceClass : iphone
             * model : iPhone 6 Plus
             * devicePlatform : ios
             * deviceId : MRZM3396F6
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

        public class CertificatesEntity {
            /**
             * expirationDateString : Aug 11, 2018
             * serialNumber : 1E74E43685CC7134
             * displayName : null
             * certificateId : Z45SA3ZDZH
             * name : iOS Development: Nan Jun
             * certificatePlatform : ios
             * hasAskKey : false
             * status : Issued
             * statusCode : 0
             * expirationDate : 2018-08-11
             * certificateType : {"ownerType":"teamMember","permissionType":"development","distributionMethod":"app","distributionType":"development","daysOverlap":364,"name":"iOS Development","certificateTypeDisplayId":"5QPB9NHCEI","platform":"ios","maxActive":1}
             */
            private String expirationDateString;
            private String serialNumber;
            private String displayName;
            private String certificateId;
            private String name;
            private String certificatePlatform;
            private boolean hasAskKey;
            private String status;
            private int statusCode;
            private String expirationDate;
            private CertificateTypeEntity certificateType;

            public void setExpirationDateString (String expirationDateString) {
                this.expirationDateString = expirationDateString;
            }

            public void setSerialNumber (String serialNumber) {
                this.serialNumber = serialNumber;
            }

            public void setDisplayName (String displayName) {
                this.displayName = displayName;
            }

            public void setCertificateId (String certificateId) {
                this.certificateId = certificateId;
            }

            public void setName (String name) {
                this.name = name;
            }

            public void setCertificatePlatform (String certificatePlatform) {
                this.certificatePlatform = certificatePlatform;
            }

            public void setHasAskKey (boolean hasAskKey) {
                this.hasAskKey = hasAskKey;
            }

            public void setStatus (String status) {
                this.status = status;
            }

            public void setStatusCode (int statusCode) {
                this.statusCode = statusCode;
            }

            public void setExpirationDate (String expirationDate) {
                this.expirationDate = expirationDate;
            }

            public void setCertificateType (CertificateTypeEntity certificateType) {
                this.certificateType = certificateType;
            }

            public String getExpirationDateString () {
                return expirationDateString;
            }

            public String getSerialNumber () {
                return serialNumber;
            }

            public String getDisplayName () {
                return displayName;
            }

            public String getCertificateId () {
                return certificateId;
            }

            public String getName () {
                return name;
            }

            public String getCertificatePlatform () {
                return certificatePlatform;
            }

            public boolean isHasAskKey () {
                return hasAskKey;
            }

            public String getStatus () {
                return status;
            }

            public int getStatusCode () {
                return statusCode;
            }

            public String getExpirationDate () {
                return expirationDate;
            }

            public CertificateTypeEntity getCertificateType () {
                return certificateType;
            }

            public class CertificateTypeEntity {
                /**
                 * ownerType : teamMember
                 * permissionType : development
                 * distributionMethod : app
                 * distributionType : development
                 * daysOverlap : 364
                 * name : iOS Development
                 * certificateTypeDisplayId : 5QPB9NHCEI
                 * platform : ios
                 * maxActive : 1
                 */
                private String ownerType;
                private String permissionType;
                private String distributionMethod;
                private String distributionType;
                private int daysOverlap;
                private String name;
                private String certificateTypeDisplayId;
                private String platform;
                private int maxActive;

                public void setOwnerType (String ownerType) {
                    this.ownerType = ownerType;
                }

                public void setPermissionType (String permissionType) {
                    this.permissionType = permissionType;
                }

                public void setDistributionMethod (String distributionMethod) {
                    this.distributionMethod = distributionMethod;
                }

                public void setDistributionType (String distributionType) {
                    this.distributionType = distributionType;
                }

                public void setDaysOverlap (int daysOverlap) {
                    this.daysOverlap = daysOverlap;
                }

                public void setName (String name) {
                    this.name = name;
                }

                public void setCertificateTypeDisplayId (String certificateTypeDisplayId) {
                    this.certificateTypeDisplayId = certificateTypeDisplayId;
                }

                public void setPlatform (String platform) {
                    this.platform = platform;
                }

                public void setMaxActive (int maxActive) {
                    this.maxActive = maxActive;
                }

                public String getOwnerType () {
                    return ownerType;
                }

                public String getPermissionType () {
                    return permissionType;
                }

                public String getDistributionMethod () {
                    return distributionMethod;
                }

                public String getDistributionType () {
                    return distributionType;
                }

                public int getDaysOverlap () {
                    return daysOverlap;
                }

                public String getName () {
                    return name;
                }

                public String getCertificateTypeDisplayId () {
                    return certificateTypeDisplayId;
                }

                public String getPlatform () {
                    return platform;
                }

                public int getMaxActive () {
                    return maxActive;
                }
            }
        }

        public class AppIdEntity {
            /**
             * identifier : *
             * enabledFeatures : []
             * isProdPushEnabled : false
             * associatedCloudContainers : null
             * associatedIdentifiers : null
             * associatedApplicationGroupsCount : null
             * prefix : B7W45E3MGB
             * canEdit : null
             * features : {"gameCenter":false,"HK421J6T7P":false,"iCloud":false,"V66P55NK2I":false,"inAppPurchase":false,"WC421J6T7P":false,"APG3427HIY":false,"push":false,"dataProtection":"","homeKit":false,"LPLF93JG7M":false,"IAD53UNK2F":false,"passbook":false,"cloudKitVersion":1,"SKC3T5S89Y":false}
             * isDevPushEnabled : false
             * associatedIdentifiersCount : null
             * name : 64H234BQUH4S
             * associatedCloudContainersCount : null
             * isWildCard : true
             * canDelete : null
             * isDuplicate : false
             * associatedApplicationGroups : null
             * appIdId : R6J9MR45NY
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

            public class FeaturesEntity {
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
