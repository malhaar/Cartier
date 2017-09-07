package xyz.vopen.cartier.provision.ext.response;

import java.util.List;

/**
 * xyz.vopen.cartier.provision.ext.response
 *
 * @author Elve.xu [xuhw@yyft.com]
 * @version v1.0 - 24/07/2017.
 */
public class RevokeCertificateResponse extends ErrorBasicResponse {


    /**
     * userLocale : en_US
     * isAgent : true
     * certRequests : [{"ownerType":"teamMember","serialNum":"4BD03B161E2FC28D","dateRequested":"2017-07-19T02:43:22Z","certificateId":"33JKB6FVU8","certificate":{"expirationDateString":"Jul 18, 2018","serialNumber":"4BD03B161E2FC28D","displayName":null,"certificateId":"33JKB6FVU8","name":null,"certificatePlatform":"ios","hasAskKey":false,"status":"Revocation Pending","statusCode":2,"expirationDate":"2018-07-18","certificateType":{"ownerType":"teamMember","permissionType":"development","distributionMethod":"app","distributionType":"development","daysOverlap":364,"name":"iOS Development","certificateTypeDisplayId":"5QPB9NHCEI","platform":"ios","maxActive":1}},"certRequestId":"33JKB6FVU8","certificateStatusCode":2,"expirationDateString":"Jul 18, 2018","dateRequestedString":"Jul 18, 2017","dateCreated":"2017-07-19T02:43:22Z","certRequestStatusCode":8,"csrPlatform":"ios","statusString":"Revocation Pending","typeString":"iOS Development","certificateTypeDisplayId":"5QPB9NHCEI","statusCode":8,"expirationDate":"2018-07-19T02:33:22Z","certificateType":{"ownerType":"teamMember","permissionType":"development","distributionMethod":"app","distributionType":"development","daysOverlap":364,"name":"iOS Development","certificateTypeDisplayId":"5QPB9NHCEI","platform":"ios","maxActive":1}}]
     * requestId : 2e7ecffb-1c15-434b-y13f-49bbcadf1705
     * requestUrl : https://developer.apple.com:443/services-account/QH65B2/account/ios/certificate/revokeCertificate.action?content-type=text/x-url-arguments&accept=application/json&requestId=2e7ecffb-1c15-434b-y13f-49bbcadf1705&userLocale=en_US&teamId=KCQ55TH55X&certificateId=33JKB6FVU8&type=5QPB9NHCEI
     * creationTimestamp : 2017-07-24T03:06:42Z
     * resultCode : 0
     * protocolVersion : QH65B2
     * isAdmin : true
     * isMember : false
     * responseId : 50bc05cb-418b-4386-ba7b-3a567e97065c
     */
    private String userLocale;
    private boolean isAgent;
    private List<CertRequestsEntity> certRequests;
    private String requestId;
    private String requestUrl;
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

    public void setCertRequests (List<CertRequestsEntity> certRequests) {
        this.certRequests = certRequests;
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

    public String getUserLocale () {
        return userLocale;
    }

    public boolean isIsAgent () {
        return isAgent;
    }

    public List<CertRequestsEntity> getCertRequests () {
        return certRequests;
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

    public static class CertRequestsEntity {
        /**
         * ownerType : teamMember
         * serialNum : 4BD03B161E2FC28D
         * dateRequested : 2017-07-19T02:43:22Z
         * certificateId : 33JKB6FVU8
         * certificate : {"expirationDateString":"Jul 18, 2018","serialNumber":"4BD03B161E2FC28D","displayName":null,"certificateId":"33JKB6FVU8","name":null,"certificatePlatform":"ios","hasAskKey":false,"status":"Revocation Pending","statusCode":2,"expirationDate":"2018-07-18","certificateType":{"ownerType":"teamMember","permissionType":"development","distributionMethod":"app","distributionType":"development","daysOverlap":364,"name":"iOS Development","certificateTypeDisplayId":"5QPB9NHCEI","platform":"ios","maxActive":1}}
         * certRequestId : 33JKB6FVU8
         * certificateStatusCode : 2
         * expirationDateString : Jul 18, 2018
         * dateRequestedString : Jul 18, 2017
         * dateCreated : 2017-07-19T02:43:22Z
         * certRequestStatusCode : 8
         * csrPlatform : ios
         * statusString : Revocation Pending
         * typeString : iOS Development
         * certificateTypeDisplayId : 5QPB9NHCEI
         * statusCode : 8
         * expirationDate : 2018-07-19T02:33:22Z
         * certificateType : {"ownerType":"teamMember","permissionType":"development","distributionMethod":"app","distributionType":"development","daysOverlap":364,"name":"iOS Development","certificateTypeDisplayId":"5QPB9NHCEI","platform":"ios","maxActive":1}
         */
        private String ownerType;
        private String serialNum;
        private String dateRequested;
        private String certificateId;
        private CertificateEntity certificate;
        private String certRequestId;
        private int certificateStatusCode;
        private String expirationDateString;
        private String dateRequestedString;
        private String dateCreated;
        private int certRequestStatusCode;
        private String csrPlatform;
        private String statusString;
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

        public void setCsrPlatform (String csrPlatform) {
            this.csrPlatform = csrPlatform;
        }

        public void setStatusString (String statusString) {
            this.statusString = statusString;
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

        public String getCsrPlatform () {
            return csrPlatform;
        }

        public String getStatusString () {
            return statusString;
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
             * expirationDateString : Jul 18, 2018
             * serialNumber : 4BD03B161E2FC28D
             * displayName : null
             * certificateId : 33JKB6FVU8
             * name : null
             * certificatePlatform : ios
             * hasAskKey : false
             * status : Revocation Pending
             * statusCode : 2
             * expirationDate : 2018-07-18
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
