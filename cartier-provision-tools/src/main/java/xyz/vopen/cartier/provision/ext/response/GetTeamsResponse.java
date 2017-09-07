package xyz.vopen.cartier.provision.ext.response;

import java.util.List;

/**
 * xyz.vopen.cartier.provision.ext.response
 *
 * @author Elve.xu [xuhw@yyft.com]
 * @version v1.0 - 18/07/2017.
 */
public class GetTeamsResponse extends ErrorBasicResponse {


    /**
     * userLocale : en_US
     * teams : [{"agent":{"firstName":"Mingjun","lastName":"Lee","teamMemberId":"XGBQNMQ39P","developerStatus":"active","personId":11185553088,"email":"mingjunlee34@163.com"},"address":{"country":"China","city":"Guangzhou","postalCode":null,"streetAddress1":"No.1268 Guangzhou Dadaozhong Tianhe Area","state":"Guangdong"},"entityType":"i","teamMemberId":"XGBQNMQ39P","memberCount":0,"nextDeviceResetDate":1527231599000,"program":{"autoRenewPrice":"RMB 688","dateExpires":1527231599000,"name":"Apple Developer Program","autoRenew":false,"type":"ad19"},"serverCount":0,"phone":null,"teamId":"KCQ55TH55X","adminCount":0,"name":"Mingjun Lee","userRole":"AGENT"}]
     * creationTimestamp : 2017-07-18T03:24:06Z
     * resultCode : 0
     * team : null
     */
    private String userLocale;
    private List<TeamsEntity> teams;
    private String creationTimestamp;
    private int resultCode;
    private String team;

    public void setUserLocale (String userLocale) {
        this.userLocale = userLocale;
    }

    public void setTeams (List<TeamsEntity> teams) {
        this.teams = teams;
    }

    public void setCreationTimestamp (String creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }

    public void setResultCode (int resultCode) {
        this.resultCode = resultCode;
    }

    public void setTeam (String team) {
        this.team = team;
    }

    public String getUserLocale () {
        return userLocale;
    }

    public List<TeamsEntity> getTeams () {
        return teams;
    }

    public String getCreationTimestamp () {
        return creationTimestamp;
    }

    public int getResultCode () {
        return resultCode;
    }

    public String getTeam () {
        return team;
    }

    public static class TeamsEntity {
        /**
         * agent : {"firstName":"Mingjun","lastName":"Lee","teamMemberId":"XGBQNMQ39P","developerStatus":"active","personId":11185553088,"email":"mingjunlee34@163.com"}
         * address : {"country":"China","city":"Guangzhou","postalCode":null,"streetAddress1":"No.1268 Guangzhou Dadaozhong Tianhe Area","state":"Guangdong"}
         * entityType : i
         * teamMemberId : XGBQNMQ39P
         * memberCount : 0
         * nextDeviceResetDate : 1527231599000
         * program : {"autoRenewPrice":"RMB 688","dateExpires":1527231599000,"name":"Apple Developer Program","autoRenew":false,"type":"ad19"}
         * serverCount : 0
         * phone : null
         * teamId : KCQ55TH55X
         * adminCount : 0
         * name : Mingjun Lee
         * userRole : AGENT
         */
        private AgentEntity agent;
        private AddressEntity address;
        private String entityType;
        private String teamMemberId;
        private int memberCount;
        private long nextDeviceResetDate;
        private ProgramEntity program;
        private int serverCount;
        private String phone;
        private String teamId;
        private int adminCount;
        private String name;
        private String userRole;

        public void setAgent (AgentEntity agent) {
            this.agent = agent;
        }

        public void setAddress (AddressEntity address) {
            this.address = address;
        }

        public void setEntityType (String entityType) {
            this.entityType = entityType;
        }

        public void setTeamMemberId (String teamMemberId) {
            this.teamMemberId = teamMemberId;
        }

        public void setMemberCount (int memberCount) {
            this.memberCount = memberCount;
        }

        public void setNextDeviceResetDate (long nextDeviceResetDate) {
            this.nextDeviceResetDate = nextDeviceResetDate;
        }

        public void setProgram (ProgramEntity program) {
            this.program = program;
        }

        public void setServerCount (int serverCount) {
            this.serverCount = serverCount;
        }

        public void setPhone (String phone) {
            this.phone = phone;
        }

        public void setTeamId (String teamId) {
            this.teamId = teamId;
        }

        public void setAdminCount (int adminCount) {
            this.adminCount = adminCount;
        }

        public void setName (String name) {
            this.name = name;
        }

        public void setUserRole (String userRole) {
            this.userRole = userRole;
        }

        public AgentEntity getAgent () {
            return agent;
        }

        public AddressEntity getAddress () {
            return address;
        }

        public String getEntityType () {
            return entityType;
        }

        public String getTeamMemberId () {
            return teamMemberId;
        }

        public int getMemberCount () {
            return memberCount;
        }

        public long getNextDeviceResetDate () {
            return nextDeviceResetDate;
        }

        public ProgramEntity getProgram () {
            return program;
        }

        public int getServerCount () {
            return serverCount;
        }

        public String getPhone () {
            return phone;
        }

        public String getTeamId () {
            return teamId;
        }

        public int getAdminCount () {
            return adminCount;
        }

        public String getName () {
            return name;
        }

        public String getUserRole () {
            return userRole;
        }

        public static class AgentEntity {
            /**
             * firstName : Mingjun
             * lastName : Lee
             * teamMemberId : XGBQNMQ39P
             * developerStatus : active
             * personId : 11185553088
             * email : mingjunlee34@163.com
             */
            private String firstName;
            private String lastName;
            private String teamMemberId;
            private String developerStatus;
            private long personId;
            private String email;

            public void setFirstName (String firstName) {
                this.firstName = firstName;
            }

            public void setLastName (String lastName) {
                this.lastName = lastName;
            }

            public void setTeamMemberId (String teamMemberId) {
                this.teamMemberId = teamMemberId;
            }

            public void setDeveloperStatus (String developerStatus) {
                this.developerStatus = developerStatus;
            }

            public void setPersonId (long personId) {
                this.personId = personId;
            }

            public void setEmail (String email) {
                this.email = email;
            }

            public String getFirstName () {
                return firstName;
            }

            public String getLastName () {
                return lastName;
            }

            public String getTeamMemberId () {
                return teamMemberId;
            }

            public String getDeveloperStatus () {
                return developerStatus;
            }

            public long getPersonId () {
                return personId;
            }

            public String getEmail () {
                return email;
            }
        }

        public static class AddressEntity {
            /**
             * country : China
             * city : Guangzhou
             * postalCode : null
             * streetAddress1 : No.1268 Guangzhou Dadaozhong Tianhe Area
             * state : Guangdong
             */
            private String country;
            private String city;
            private String postalCode;
            private String streetAddress1;
            private String state;

            public void setCountry (String country) {
                this.country = country;
            }

            public void setCity (String city) {
                this.city = city;
            }

            public void setPostalCode (String postalCode) {
                this.postalCode = postalCode;
            }

            public void setStreetAddress1 (String streetAddress1) {
                this.streetAddress1 = streetAddress1;
            }

            public void setState (String state) {
                this.state = state;
            }

            public String getCountry () {
                return country;
            }

            public String getCity () {
                return city;
            }

            public String getPostalCode () {
                return postalCode;
            }

            public String getStreetAddress1 () {
                return streetAddress1;
            }

            public String getState () {
                return state;
            }
        }

        public static class ProgramEntity {
            /**
             * autoRenewPrice : RMB 688
             * dateExpires : 1527231599000
             * name : Apple Developer Program
             * autoRenew : false
             * type : ad19
             */
            private String autoRenewPrice;
            private long dateExpires;
            private String name;
            private boolean autoRenew;
            private String type;

            public void setAutoRenewPrice (String autoRenewPrice) {
                this.autoRenewPrice = autoRenewPrice;
            }

            public void setDateExpires (long dateExpires) {
                this.dateExpires = dateExpires;
            }

            public void setName (String name) {
                this.name = name;
            }

            public void setAutoRenew (boolean autoRenew) {
                this.autoRenew = autoRenew;
            }

            public void setType (String type) {
                this.type = type;
            }

            public String getAutoRenewPrice () {
                return autoRenewPrice;
            }

            public long getDateExpires () {
                return dateExpires;
            }

            public String getName () {
                return name;
            }

            public boolean isAutoRenew () {
                return autoRenew;
            }

            public String getType () {
                return type;
            }
        }
    }
}
