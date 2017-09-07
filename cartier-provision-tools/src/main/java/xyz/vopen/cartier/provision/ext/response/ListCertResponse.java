package xyz.vopen.cartier.provision.ext.response;

import java.util.List;

/**
 * xyz.vopen.cartier.provision.ext.response
 *
 * @author Elve.xu [xuhw@yyft.com]
 * @version v1.0 - 18/07/2017.
 */
public class ListCertResponse extends ErrorBasicResponse {


    private static final long serialVersionUID = 5837129787779962115L;
    /**
     * totalRecords : 2
     * userLocale : en_US
     * pageNumber : 1
     * certRequests : [{"ownerType":"teamMember","serialNum":"585487A32AE0E5F7","dateRequested":"2017-06-30T09:58:44Z","certificateId":"S33G3E2X3S","certificate":{"expirationDateString":"Jun 30, 2018","serialNumber":"585487A32AE0E5F7","displayName":null,"certificateId":"S33G3E2X3S","name":"iOS Development: Mingjun Lee","certificatePlatform":"ios","hasAskKey":false,"status":"Issued","statusCode":0,"expirationDate":"2018-06-30","certificateType":{"ownerType":"teamMember","permissionType":"development","distributionMethod":"app","distributionType":"development","daysOverlap":364,"name":"iOS Development","certificateTypeDisplayId":"5QPB9NHCEI","platform":"ios","maxActive":1}},"certRequestId":"S33G3E2X3S","ownerId":"XGBQNMQ39P","certificateStatusCode":0,"expirationDateString":"Jun 30, 2018","dateRequestedString":"Jun 30, 2017","dateCreated":"2017-06-30T09:58:44Z","certRequestStatusCode":4,"ownerName":"Mingjun Lee","csrPlatform":"ios","statusString":"Issued","name":"Mingjun Lee","typeString":"iOS Development","canDownload":true,"certificateTypeDisplayId":"5QPB9NHCEI","canRevoke":true,"statusCode":4,"expirationDate":"2018-06-30T09:48:44Z","certificateType":{"ownerType":"teamMember","permissionType":"development","distributionMethod":"app","distributionType":"development","daysOverlap":364,"name":"iOS Development","certificateTypeDisplayId":"5QPB9NHCEI","platform":"ios","maxActive":1}},{"ownerType":"teamMember","serialNum":"60BCB65631481A25","dateRequested":"2017-07-13T01:39:36Z","certificateId":"ECW3QG6CJU","certificate":{"expirationDateString":"Jul 12, 2018","serialNumber":"60BCB65631481A25","displayName":null,"certificateId":"ECW3QG6CJU","name":"iOS Development: Mingjun Lee","certificatePlatform":"ios","hasAskKey":false,"status":"Issued","statusCode":0,"expirationDate":"2018-07-12","certificateType":{"ownerType":"teamMember","permissionType":"development","distributionMethod":"app","distributionType":"development","daysOverlap":364,"name":"iOS Development","certificateTypeDisplayId":"5QPB9NHCEI","platform":"ios","maxActive":1}},"certRequestId":"ECW3QG6CJU","ownerId":"XGBQNMQ39P","certificateStatusCode":0,"expirationDateString":"Jul 12, 2018","dateRequestedString":"Jul 12, 2017","dateCreated":"2017-07-13T01:39:36Z","certRequestStatusCode":4,"ownerName":"Mingjun Lee","csrPlatform":"ios","statusString":"Issued","name":"Mingjun Lee","typeString":"iOS Development","canDownload":true,"certificateTypeDisplayId":"5QPB9NHCEI","canRevoke":true,"statusCode":4,"expirationDate":"2018-07-13T01:29:37Z","certificateType":{"ownerType":"teamMember","permissionType":"development","distributionMethod":"app","distributionType":"development","daysOverlap":364,"name":"iOS Development","certificateTypeDisplayId":"5QPB9NHCEI","platform":"ios","maxActive":1}}]
     * resultCode : 0
     * pageSize : 500
     * isAdmin : true
     * isMember : false
     * isAgent : true
     * requestId : 14103b11-07dd-437f-y18b-9aa96973545c
     * requestUrl : https://developer.apple.com:443/services-account/QH65B2/account/ios/certificate/listCertRequests.action?content-type=text/x-url-arguments&accept=application/json&requestId=14103b11-07dd-437f-y18b-9aa96973545c&userLocale=en_US&teamId=KCQ55TH55X&types=5QPB9NHCEI,BKLRAVXMGM&status=4&certificateStatus=0&type=development
     * creationTimestamp : 2017-07-18T06:52:48Z
     * protocolVersion : QH65B2
     * responseId : 34642491-bb15-4f6f-9d09-b27c0eb178d9
     */
    private int totalRecords;
    private String userLocale;
    private int pageNumber;
    private List<CertRequestsEntity> certRequests;
    private int resultCode;
    private int pageSize;
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

    public void setCertRequests (List<CertRequestsEntity> certRequests) {
        this.certRequests = certRequests;
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

    public List<CertRequestsEntity> getCertRequests () {
        return certRequests;
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

    public static class CertRequestsEntity {
        /**
         * ownerType : teamMember
         * serialNum : 585487A32AE0E5F7
         * dateRequested : 2017-06-30T09:58:44Z
         * certificateId : S33G3E2X3S
         * certificate : {"expirationDateString":"Jun 30, 2018","serialNumber":"585487A32AE0E5F7","displayName":null,"certificateId":"S33G3E2X3S","name":"iOS Development: Mingjun Lee","certificatePlatform":"ios","hasAskKey":false,"status":"Issued","statusCode":0,"expirationDate":"2018-06-30","certificateType":{"ownerType":"teamMember","permissionType":"development","distributionMethod":"app","distributionType":"development","daysOverlap":364,"name":"iOS Development","certificateTypeDisplayId":"5QPB9NHCEI","platform":"ios","maxActive":1}}
         * certRequestId : S33G3E2X3S
         * ownerId : XGBQNMQ39P
         * certificateStatusCode : 0
         * expirationDateString : Jun 30, 2018
         * dateRequestedString : Jun 30, 2017
         * dateCreated : 2017-06-30T09:58:44Z
         * certRequestStatusCode : 4
         * ownerName : Mingjun Lee
         * csrPlatform : ios
         * statusString : Issued
         * name : Mingjun Lee
         * typeString : iOS Development
         * canDownload : true
         * certificateTypeDisplayId : 5QPB9NHCEI
         * canRevoke : true
         * statusCode : 4
         * expirationDate : 2018-06-30T09:48:44Z
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
        private boolean canDownload;
        private String certificateTypeDisplayId;
        private boolean canRevoke;
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

        public void setCanDownload (boolean canDownload) {
            this.canDownload = canDownload;
        }

        public void setCertificateTypeDisplayId (String certificateTypeDisplayId) {
            this.certificateTypeDisplayId = certificateTypeDisplayId;
        }

        public void setCanRevoke (boolean canRevoke) {
            this.canRevoke = canRevoke;
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

        public boolean isCanDownload () {
            return canDownload;
        }

        public String getCertificateTypeDisplayId () {
            return certificateTypeDisplayId;
        }

        public boolean isCanRevoke () {
            return canRevoke;
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
             * expirationDateString : Jun 30, 2018
             * serialNumber : 585487A32AE0E5F7
             * displayName : null
             * certificateId : S33G3E2X3S
             * name : iOS Development: Mingjun Lee
             * certificatePlatform : ios
             * hasAskKey : false
             * status : Issued
             * statusCode : 0
             * expirationDate : 2018-06-30
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
