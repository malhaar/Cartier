#Cartier Apple Tools

> Need for Speed !<br/> 
> Release your hands From Tedious And tedious initialization Work.


### Start Cartier Tool
```
sh ./cartier.sh --help
 
usage: cartier [-rit] [-rvk] [-udid <Apple-DEVICE-UDID>] [-b] [-sjson <SOURCE JSON FILE>] 
               [-u <USERNAME>] [-p <PASSWORD>] [-an <APPLE AppId NAME>] [-pn <APPLE PROFILE NAME>] [-v]
 
 Options:
  
 -rit,--focusReInit                           Focus Re-init Apple Account
 -rvk,--focusReInvoke                         Focus Re-Revoke Apple Account
 -udid <Apple-DEVICE-UDID>                    Apple Device's udid (40bit)
 -b,--batch                                   Multi-Apple-Accounts to Initialize
 -sjson,--sourceJsonFile <SOURCE JSON FILE>   Assign Batch Apple Accounts Json(xxxx.json) File .
                                              Json Example :
                                              [
                                                {
                                                    "u" : "Apple Account Username",
                                                    "p" : "Apple Account Password",
                                                    "pn": "Apple Profile Name, Default: pyw",
                                                    "an": "Apple AppId Name, Default: pyw"
                                                }
                                              ]
 
 -u,--username <USERNAME>                     Apple Account Username
 -p,--password <PASSWORD>                     Apple Account Password
 -pn,--profileName <APPLE PROFILE NAME>       Apple Development Profile Name ,Default: pyw
 -an,--appIdName <APPLE AppId NAME>           Apple Development AppId Name ,Default: pyw
 -v,--verbose                                 Enable Processor Logger

 
WarnsAndTips :
If option contain [-b | --batch] , those options [-u ,-p ,-pn ,-an] will not working anymore
You can initialize Apple Account Alone ,use Option [-u ,-p ,-pn ,-an] .
Or You can initialize Apple Accounts batch with json file ,use Option [-b | --batch]
Enjoy it!

```

### Demo Command
```
    # Single Account
    ./cartier.sh -rit -rvk -u vmtryg@163.com -p xxxx -pn pyw -an pyw
    
    # Multi-Accounes
    ./cartier.sh -rit -rvk -b -sjson /path/accounts.json
    
```

### Multi Accounts Json
```json
    [
        {
            "u": "Apple Account Username -1",
            "p": "Apple Account Password -1",
            "pn": "Apple Profile Name, Default: pyw",
            "an": "Apple AppId Name, Default: pyw"
        },
        {
            "u": "Apple Account Username -2",
            "p": "Apple Account Password -2"
        },
        {
            "u": "Apple Account Username -3",
            "p": "Apple Account Password -3",
            "pn": "Apple Profile Name, Default: pyw",
            "an": "Apple AppId Name, Default: pyw"
        }
    ]

```


### Release Version
> 1.1.0-RELEASE

### How to build

You require the following to build Cartier:

* Latest stable [Oracle JDK 7](http://www.oracle.com/technetwork/java/)
* Latest stable [Apache Maven](http://maven.apache.org/)

```
    git clone http://xxxxx.git
    
    sh release.sh

```



### Power By PYW
> Bug Contact Author: @mail: iskp.me@gmail.com