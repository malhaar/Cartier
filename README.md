<h3 align="center">
  <img src="favicon.png" alt="cartier Logo" />
</h3>

# Cartier Development Framework
[![Maven Central](https://img.shields.io/badge/release-1.1.0--RELEASE-blue.svg)](https://github.com/CiNC0/Cartier)
[![Travis](https://img.shields.io/travis/rust-lang/rust.svg)](https://github.com/CiNC0/Cartier)
[![Coveralls](https://img.shields.io/coveralls/jekyll/jekyll.svg)]([![Travis](https://img.shields.io/travis/rust-lang/rust.svg)](https://github.com/CiNC0/Cartier))
[![Twitter URL](https://img.shields.io/twitter/url/http/shields.io.svg?style=social)](https://twitter.com/@ytuaebi)

> Java Distribution Framework , Provide Common-utils ,Like : Database operation , Memcached Utils ,Redis Utils, Classified Logger ,Apple iOS Security Kit ,Apple-Development-Account kit,Java Application Surface kit to build jvm-application.


## Framework Modules

- Application Surface Module
- Redis Support
- Memcached Support
- Database Operator Support
- Common Utils [@see #Commons Modules]
- Classpath Scanner Support
- Spring Common Tools
- Apple Re-Sign on macOS (Java)
- Apple Re-Sign on Linux (Python)
- CDN Support (list: ks3, aliyun)

## Commons Modules
- Linux Command Tools
- HttpClient Tools (Base on Apache HttpClient)
- Apple Plist File Tools
- Apple Security Tools
- Inner Http-Server
- Yaml Tools 

## Getting started

### Check out
```
    git clone https://github.com/CiNC0/Cartier.git
    
    mvn clean install
```

### Create Application With Surface

> Eidt `pom.xml` with plugin `maven-dependency-plugin` and `maven-assembly-plugin`
```xml
<build>
    <plugins>
        <plugin>
            <artifactId>maven-dependency-plugin</artifactId>
            <executions>
                <execution>
                    <id>unpack</id>
                    <phase>package</phase>
                    <goals>
                        <goal>unpack</goal>
                    </goals>
                    <configuration>
                        <artifactItems>

                            <artifactItem>
                                <groupId>xyz.vopen.cartier</groupId>
                                <artifactId>cartier-surface</artifactId>
                                <version>1.1.0-RELEASE</version>
                                <outputDirectory>${project.build.directory}/bin</outputDirectory>
                                <includes>assembly/bin/**</includes>
                            </artifactItem>
                        </artifactItems>
                    </configuration>
                </execution>
            </executions>
        </plugin>
        <plugin>
            <artifactId>maven-assembly-plugin</artifactId>
            <configuration>
                <descriptor>src/main/assembly/assembly.xml</descriptor>
            </configuration>
            <executions>
                <execution>
                    <id>make-assembly</id>
                    <phase>package</phase>
                    <goals>
                        <goal>single</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>

    </plugins>
</build>
```

> Edit `src/main/assembly/assembly.xml`

```xml
<assembly>
    <id>assembly</id>
    <formats>
        <format>tar.gz</format>
    </formats>
    <includeBaseDirectory>true</includeBaseDirectory>
    <fileSets>
        <fileSet>
            <directory>${project.build.directory}/bin/assembly/bin</directory>
            <outputDirectory>bin</outputDirectory>
            <fileMode>0755</fileMode>
        </fileSet>

        <fileSet>
            <directory>src/main/assembly/conf</directory>
            <outputDirectory>conf</outputDirectory>
            <fileMode>0644</fileMode>
        </fileSet>

        <fileSet>
            <directory>src/main/resources/META-INF</directory>
            <outputDirectory>conf/META-INF</outputDirectory>
            <fileMode>0644</fileMode>
        </fileSet>

    </fileSets>
    <dependencySets>
        <dependencySet>
            <outputDirectory>lib</outputDirectory>
        </dependencySet>
    </dependencySets>
</assembly>
``` 


> New Application 

```java
package xyz.vopen.cartier;

import xyz.vopen.cartier.surface.CartierApplication;
import xyz.vopen.cartier.surface.CartierApplicationContext;
import xyz.vopen.cartier.surface.exception.CartierOptionException;

import java.util.Map;

public class TestApplication extends CartierApplication {

    @Override
    public void init (CartierApplicationContext context) throws Exception {
        // init ...
    }

    @Override
    public void start (CartierApplicationContext context) throws Exception {
        // start ...
    }

    @Override
    public void shutdown (CartierApplicationContext context) throws Exception {
        // shutdown ...
    }

    @Override
    public void checkOptions (Map<String, String> options) throws CartierOptionException {

    }
}
```

### Config META-INF

> New File `xyz.vopen.cartier.surface.CartierApplication` 
```
    application=xyz.vopen.cartier.TestApplication
```

### Package Application
```
    mvn clean package
```

### Running
> bin commands : `start.sh` ,`stop.sh` ,`dump.sh` ,`restart.sh` 
```
    sh start.sh
    ....
```


### Apple Provision Tools Usage:

#### How Use Tools To Initialize Apple Account
```java
// Get Tools instance
ProvisionProcessor provisionProcessor = ProvisionProcessor.getInstance();
ProvisionProcessor.ProvisionHandler provisionHandler = provisionProcessor.getHandler();

// login
provisionHandler.login(account.username, account.password);

// Get TeamId 
GetTeamsResponse teamsResponse = provisionHandler.getTeams();

// Submit CSR file
SubmitCertificateResponse scr = provisionHandler.submitCertificateRequest(teamId, csrFilePath);

// download cer file
provisionHandler.downloadCertificateContent(teamId, certificateId, destCerFilePath);

// Add AppId
AddAppIdResponse addAppIdResponse = provisionHandler.addAppId(teamId, appIdName);

// Add Devices ,delete device ,enable deveice , disDevice ....
AddDevicesResponse addDevicesResponse = provisionHandler.addDevice(teamId, udid, ProvisionProcessor.Device.iphone);

// .... more methods...
//@see xyz.vopen.cartier.provision.ProvisionProcessor;

```

#### How Use Tools To Operator Keychain

> Java interface to operator macOS Keychain

```java

/**
 * create csr file
 *
 * @param baseDir
 *         base dir
 * @param keyFileName
 *         key file name
 * @param csrFileName
 *         CSR file name
 * @param email
 *         CSR email
 * @param username
 *         CSR username
 */
public void generateCSR (String baseDir, String keyFileName, String csrFileName, String email, String username) throws CertException;


/**
 * init private and import cert
 *
 * @param privateKey
 *         private key
 * @param certPath
 *         cer 
 * @param userPwd
 *         os pwd
 * @param keychain
 *         keychain
 * @param keychainPwd
 *         keychain password
 */
public void initAndImportCert (String privateKey, String certPath, String userPwd, String keychain, String keychainPwd) throws CertException;

/**
 * create keychain
 * <pre>
 *
 *     Like :
 *      Example Command:
 *          security create-keychain -p 123456  /Users/ive/Library/Keychains/t-two.keychain
 *
 * </pre>
 *
 * @param keychain
 *         keychain path
 * @param keychainPwd
 *         keychain password
 */
public void createKeychain (String keychain, String keychainPwd) throws CertException;

```

#### How to use redis

##### Basic Usage

```java
RedisClient client = RedisClient.create("redis://localhost");
StatefulRedisConnection<String, String> connection = client.connect();
RedisStringCommands sync = connection.sync();
String value = sync.get("key");
```
> Each Redis command is implemented by one or more methods with names identical
  to the lowercase Redis command name. Complex commands with multiple modifiers
  that change the result type include the CamelCased modifier as part of the
  command name, e.g. zrangebyscore and zrangebyscoreWithScores.


##### Asynchronous API
```java
StatefulRedisConnection<String, String> connection = client.connect();
RedisStringAsyncCommands<String, String> async = connection.async();
RedisFuture<String> set = async.set("key", "value");
RedisFuture<String> get = async.get("key");

async.awaitAll(set, get) == true;

set.get() == "OK";
get.get() == "value";
```

##### Reactive API
```java
StatefulRedisConnection<String, String> connection = client.connect();
RedisStringReactiveCommands<String, String> reactive = connection.reactive();
Observable<String> set = reactive.set("key", "value");
Observable<String> get = reactive.get("key");

set.subscribe();

get.toBlocking().single() == "value";
```

##### Pub/Sub
```java
RedisPubSubCommands<String, String> connection = client.connectPubSub().sync();
connection.addListener(new RedisPubSubListener<String, String>() { ... });
connection.subscribe("channel");
```


#### How to use Memcached Client

The main entry point to the folsom API is the MemcacheClientBuilder class. It has
chainable setter methods to configure various aspects of the client. The methods connectBinary()
and connectAscii() constructs MemcacheClient instances utilising the [binary protocol] and
[ascii protocol] respectively. For details on their differences see **Protocol** below.

All calls to the folsom API that interacts with a memcache server is asynchronous and the
result is typically accessible from ListenableFuture instances. An exception to this rule
are the methods that connects clients to their remote endpoints,
MemcacheClientBuilder.connectBinary() and MemcacheClientBuilder.connectAscii() which will
return a MemcacheClient immediately while asynchronously attempting to connect to the configured
remote endpoint(s).

As code using the folsom API should be written so that it handles failing intermittently with
MemcacheClosedException anyway, waiting for the initial connect to complete is not something
folsom concerns itself with. For single server connections, ConnectFuture provides functionality
to wait for the initial connection to succeed, as can be seen in the example below.

```Java
final MemcacheClient<String> client = MemcacheClientBuilder.newStringClient()
    .withAddresses(Collections.singletonList(host))
    .connectAscii();
// make we wait until the client has connected to the server
ConnectFuture.connectFuture(client).get();

client.set("key", "value", 10000).get();
client.get("key").get();

client.shutdown();
```

Clients are single use, after `shutdown` has been invoked the client can no
longer be used.



#### How to use Database Operator `CartierDB`

> `CartierDataSource` Properties File
```
# connection conf
jdbc.driver_class_name=
jdbc.url=
jdbc.username=
jdbc.password=

# pool conf
jdbc.initial_size=5
jdbc.max_active=10
jdbc.max_idle=10
jdbc.min_idle=5
jdbc.max_wait=10000
jdbc.remove_abandoned=true
jdbc.remove_abandoned_timeout=180
```

##### Usage:
```java

// init cartierDB
CartierDB cartierDB = new CartierDB(
                              new CartierDataSource.Builder()
                                      .properties(applicationProperties)
                                      .build()
                      );

// create 
public <T> int create (Class<T> cls, T bean);
//....

// query
public <T> T query (StringBuffer sql, ResultSetHandler<T> rsh,List<Object> params);
public <T> T query (String sql, ResultSetHandler<T> rsh, Object... params);

// batch
public int[] batch (String sql, Object[][] params);

// update 
public int update (StringBuffer sql, List<Object> params);
public int update (String sql, Object... params);


```


#### How to use CDN
> `ks3.properties` conf
```
# key and secret
accessKeyID=CH
accessKeySecret=WI

# 
domain=
enableHttps=true
autoCreateBucket=true

# block size  default 100M   104857600
block.length=104857600

# min block download file size ,default 200M   209715200
min.download.file.length=209715200

# min block upload file size 默认200M   209715200
min.upload.file.length=209715200

```

>  initialize first

```java
CDNHandler handlerHolder = new Ks3Handler();
Ks3Config ks3Config = new Ks3Config(type, properties);
// 初始化
handlerHolder.initialize(ks3Config);

```

> Java interface
```java
 
/**
 * upload file
 *
 * @param bucket
 *         bucket name
 * @param filePath
 *         source file
 * @param relativePathKey
 *         bucket file key
 *
 * @throws BucketException
 *         bucket exception
 * @throws ObjectException
 *         object exception
 * @throws CDNException
 *         cdn exception
 */
void upload (String filePath, String bucket, String relativePathKey) throws BucketException, ObjectException, CDNException;

/**
 * super download
 *
 * @param bucket
 *         bucket name
 * @param filePath
 *         source file
 * @param relativePathKey
 *         bucket file key
 *
 * @throws BucketException
 *         bucket exception
 * @throws ObjectException
 *         object exception
 * @throws CDNException
 *         cdn exception
 */
void superUpload (String filePath, String bucket, String relativePathKey) throws BucketException, ObjectException, CDNException;

/**
 * download file
 *
 * @param bucket
 *         bucket
 * @param relativePathKey
 *         bucket file key
 * @param destFilePath
 *         dest file
 *
 * @throws BucketException
 *         bucket exception
 * @throws ObjectException
 *         object exception
 * @throws CDNException
 *         cdn exception
 */
void download (String bucket, String relativePathKey, String destFilePath) throws BucketException, ObjectException, CDNException;

/**
 * super download file
 *
 * @param bucket
 *         bucket
 * @param relativePathKey
 *         bucket file key
 * @param destFilePath
 *         dest file
 *
 * @throws BucketException
 *         bucket exception
 * @throws ObjectException
 *         object exception
 * @throws CDNException
 *         cdn exception
 */
void superDownload (String bucket, String relativePathKey, String destFilePath) throws BucketException, ObjectException, CDNException;


/**
 * query file list
 *
 * @param bucket
 *         bucket name
 * @param limit
 *         like mysql limit
 *
 * @return file list
 *
 * @throws BucketException
 *         bucket exception
 * @throws ObjectException
 *         object exception
 * @throws CDNException
 *         cdn exception
 */
List listFiles (String bucket, Integer limit) throws BucketException, ObjectException, CDNException;

/**
 * delete files
 *
 * @param bucket
 *         bucket name
 * @param relativePathKeys
 *         file keys
 *
 * @throws BucketException
 *         bucket exception
 * @throws ObjectException
 *         object exception
 * @throws CDNException
 *         cdn exception
 */
void deleteFiles (String bucket, String... relativePathKeys) throws BucketException, ObjectException, CDNException;

/**
 * check file
 *
 * @param bucket
 *         bucket name
 * @param relativePathKeys
 *         file key
 *
 * @return true is exist ,false is not found
 *
 * @throws ObjectException
 *         object exception
 * @throws CDNException
 *         cdn exception
 */
Boolean checkFile (String bucket, String relativePathKeys) throws ObjectException, CDNException;

/**
 * create bucket
 *
 * @param bucketName
 *         bucket name
 *
 * @throws BucketException
 *         bucket exception
 * @throws CDNException
 *         cdn exception
 */
void createBucket (String bucketName) throws BucketException, CDNException;

/**
 * check bucket info
 *
 * @param bucketName
 *         bucket name
 *
 * @throws BucketException
 *         bucket exception
 * @throws CDNException
 *         cdn exception
 */
int checkBucket (String bucketName) throws BucketException, CDNException;

```

### Apple Account Command Tools
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

#### Demo Command
```
    # Single Account
    ./cartier.sh -rit -rvk -u vmtryg@163.com -p xxxx -pn pyw -an pyw
    
    # Multi-Accounes
    ./cartier.sh -rit -rvk -b -sjson /path/accounts.json
    
```

#### Multi Accounts Json
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



## Binaries And Maven
```xml

    <dependency>
        <groupId>xyz.vopen.cartier</groupId>
        <artifactId>cartier-provision-tools</artifactId>
        <version>RELEASE</version>
    </dependency>
    
    <dependency>
        <groupId>xyz.vopen.cartier</groupId>
        <artifactId>cartier-commons</artifactId>
        <version>RELEASE</version>
    </dependency>
    
    <dependency>
        <groupId>xyz.vopen.cartier</groupId>
        <artifactId>cartier-database-jdbc</artifactId>
        <version>RELEASE</version>
    </dependency>
    
    <dependency>
        <groupId>xyz.vopen.cartier</groupId>
        <artifactId>cartier-mcd</artifactId>
        <version>RELEASE</version>
    </dependency>
    
    <dependency>
        <groupId>xyz.vopen.cartier</groupId>
        <artifactId>cartier-redis</artifactId>
        <version>RELEASE</version>
    </dependency>
    
    <dependency>
        <groupId>xyz.vopen.cartier</groupId>
        <artifactId>cartier-surface</artifactId>
        <version>RELEASE</version>
    </dependency>
    
    <dependency>
        <groupId>xyz.vopen.cartier</groupId>
        <artifactId>cartier-spring</artifactId>
        <version>RELEASE</version>
    </dependency>
    
    <dependency>
        <groupId>xyz.vopen.cartier</groupId>
        <artifactId>cartier-classpath-scanner</artifactId>
        <version>RELEASE</version>
    </dependency>
    
    <dependency>
        <groupId>xyz.vopen.cartier</groupId>
        <artifactId>cartier-ios-resign</artifactId>
        <version>RELEASE</version>
    </dependency>
    
``` 


## License

This project is licensed under the terms of the Apache license. See the [LICENSE](LICENSE) file.