package xyz.vopen.cartier.provision.ext.response;

/**
 * 请求证书响应值
 *
 * @author Elve.xu [xuhw@yyft.com]
 * @version v1.0 - 14/07/2017.
 */
public class SubmitCertificateResponse extends ErrorBasicResponse {


    /**
     * userLocale : en_US
     * isAgent : true
     * requestId : c8839b97-36f6-46e5-yd6a-1f6b7e9f1714
     * requestUrl : https://developer.apple.com:443/services-account/QH65B2/account/ios/certificate/submitCertificateRequest.action?content-type=text/x-url-arguments&accept=application/json&requestId=c8839b97-36f6-46e5-yd6a-1f6b7e9f1714&userLocale=en_US&teamId=KCQ55TH55X
     * creationTimestamp : 2017-07-12T09:15:45Z
     * resultCode : 0
     * protocolVersion : QH65B2
     * isAdmin : true
     * isMember : false
     * responseId : 13b2b474-32f3-4df3-a999-b544c11c68a5
     * certRequest : {"ownerType":"teamMember","serialNum":"3F435D24534299E5","dateRequested":"2017-07-12T09:15:45Z","certificateId":"4XM8363UWF","certificate":{"expirationDateString":"Jul 12, 2018","serialNumber":"3F435D24534299E5","displayName":"Mingjun Lee","certificateId":"4XM8363UWF","name":"iOS Development: Mingjun Lee","certificatePlatform":"ios","hasAskKey":false,"status":"Issued","statusCode":0,"expirationDate":"2018-07-12","certificateType":{"ownerType":"teamMember","permissionType":"development","distributionMethod":"app","distributionType":"development","daysOverlap":364,"name":"iOS Development","certificateTypeDisplayId":"5QPB9NHCEI","platform":"ios","maxActive":1}},"certRequestId":"4XM8363UWF","ownerId":"XGBQNMQ39P","certificateStatusCode":0,"expirationDateString":"Jul 12, 2018","dateRequestedString":"Jul 12, 2017","dateCreated":"2017-07-12T09:15:45Z","certRequestStatusCode":4,"ownerName":"Mingjun Lee","csrPlatform":"ios","statusString":"Issued","name":"Mingjun Lee","typeString":"iOS Development","certificateTypeDisplayId":"5QPB9NHCEI","statusCode":4,"expirationDate":"2018-07-12T09:05:45Z","certificateType":{"ownerType":"teamMember","permissionType":"development","distributionMethod":"app","distributionType":"development","daysOverlap":364,"name":"iOS Development","certificateTypeDisplayId":"5QPB9NHCEI","platform":"ios","maxActive":1}}
     */
    private String userLocale;
    private boolean isAgent;
    private String requestId;
    private String requestUrl;
    private String creationTimestamp;
    private int resultCode;
    private String protocolVersion;
    private boolean isAdmin;
    private boolean isMember;
    private String responseId;
    private CertRequestEntity certRequest;

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

    public void setCertRequest (CertRequestEntity certRequest) {
        this.certRequest = certRequest;
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

    public CertRequestEntity getCertRequest () {
        return certRequest;
    }

    public static class CertRequestEntity {
        /**
         * ownerType : teamMember
         * serialNum : 3F435D24534299E5
         * dateRequested : 2017-07-12T09:15:45Z
         * certificateId : 4XM8363UWF
         * certificate : {"expirationDateString":"Jul 12, 2018","serialNumber":"3F435D24534299E5","displayName":"Mingjun Lee","certificateId":"4XM8363UWF","name":"iOS Development: Mingjun Lee","certificatePlatform":"ios","hasAskKey":false,"status":"Issued","statusCode":0,"expirationDate":"2018-07-12","certificateType":{"ownerType":"teamMember","permissionType":"development","distributionMethod":"app","distributionType":"development","daysOverlap":364,"name":"iOS Development","certificateTypeDisplayId":"5QPB9NHCEI","platform":"ios","maxActive":1}}
         * certRequestId : 4XM8363UWF
         * ownerId : XGBQNMQ39P
         * certificateStatusCode : 0
         * expirationDateString : Jul 12, 2018
         * dateRequestedString : Jul 12, 2017
         * dateCreated : 2017-07-12T09:15:45Z
         * certRequestStatusCode : 4
         * ownerName : Mingjun Lee
         * csrPlatform : ios
         * statusString : Issued
         * name : Mingjun Lee
         * typeString : iOS Development
         * certificateTypeDisplayId : 5QPB9NHCEI
         * statusCode : 4
         * expirationDate : 2018-07-12T09:05:45Z
         * certificateType : {"ownerType":"teamMember","permissionType":"development","distributionMethod":"app","distributionType":"development","daysOverlap":364,"name":"iOS Development","certificateTypeDisplayId":"5QPB9NHCEI","platform":"ios","maxActive":1}
         */
        private String ownerType;
        private String serialNum;
        private String dateRequested;
        private String certificateId;
        private CertificateEntity certificate;
        private String certRequestId;
        private String ownerId;
        private int certificateStatusCode;
        private String expirationDateString;
        private String dateRequestedString;
        private String dateCreated;
        private int certRequestStatusCode;
        private String ownerName;
        private String csrPlatform;
        private String statusString;
        private String name;
        private String typeString;
        private String certificateTypeDisplayId;
        private int statusCode;
        private String expirationDate;
        private CertificateTypeEntity certificateType;

        public void setOwnerType (String ownerType) {
            this.ownerType = ownerType;
        }

        public void setSerialNum (String serialNum) {
            this.serialNum = serialNum;
        }

        public void setDateRequested (String dateRequested) {
            this.dateRequested = dateRequested;
        }

        public void setCertificateId (String certificateId) {
            this.certificateId = certificateId;
        }

        public void setCertificate (CertificateEntity certificate) {
            this.certificate = certificate;
        }

        public void setCertRequestId (String certRequestId) {
            this.certRequestId = certRequestId;
        }

        public void setOwnerId (String ownerId) {
            this.ownerId = ownerId;
        }

        public void setCertificateStatusCode (int certificateStatusCode) {
            this.certificateStatusCode = certificateStatusCode;
        }

        public void setExpirationDateString (String expirationDateString) {
            this.expirationDateString = expirationDateString;
        }

        public void setDateRequestedString (String dateRequestedString) {
            this.dateRequestedString = dateRequestedString;
        }

        public void setDateCreated (String dateCreated) {
            this.dateCreated = dateCreated;
        }

        public void setCertRequestStatusCode (int certRequestStatusCode) {
            this.certRequestStatusCode = certRequestStatusCode;
        }

        public void setOwnerName (String ownerName) {
            this.ownerName = ownerName;
        }

        public void setCsrPlatform (String csrPlatform) {
            this.csrPlatform = csrPlatform;
        }

        public void setStatusString (String statusString) {
            this.statusString = statusString;
        }

        public void setName (String name) {
            this.name = name;
        }

        public void setTypeString (String typeString) {
            this.typeString = typeString;
        }

        public void setCertificateTypeDisplayId (String certificateTypeDisplayId) {
            this.certificateTypeDisplayId = certificateTypeDisplayId;
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

        public String getOwnerType () {
            return ownerType;
        }

        public String getSerialNum () {
            return serialNum;
        }

        public String getDateRequested () {
            return dateRequested;
        }

        public String getCertificateId () {
            return certificateId;
        }

        public CertificateEntity getCertificate () {
            return certificate;
        }

        public String getCertRequestId () {
            return certRequestId;
        }

        public String getOwnerId () {
            return ownerId;
        }

        public int getCertificateStatusCode () {
            return certificateStatusCode;
        }

        public String getExpirationDateString () {
            return expirationDateString;
        }

        public String getDateRequestedString () {
            return dateRequestedString;
        }

        public String getDateCreated () {
            return dateCreated;
        }

        public int getCertRequestStatusCode () {
            return certRequestStatusCode;
        }

        public String getOwnerName () {
            return ownerName;
        }

        public String getCsrPlatform () {
            return csrPlatform;
        }

        public String getStatusString () {
            return statusString;
        }

        public String getName () {
            return name;
        }

        public String getTypeString () {
            return typeString;
        }

        public String getCertificateTypeDisplayId () {
            return certificateTypeDisplayId;
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

        public static class CertificateEntity {
            /**
             * expirationDateString : Jul 12, 2018
             * serialNumber : 3F435D24534299E5
             * displayName : Mingjun Lee
             * certificateId : 4XM8363UWF
             * name : iOS Development: Mingjun Lee
             * certificatePlatform : ios
             * hasAskKey : false
             * status : Issued
             * statusCode : 0
             * expirationDate : 2018-07-12
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

            public static class CertificateTypeEntity {
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

        public static class CertificateTypeEntity {
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
}
