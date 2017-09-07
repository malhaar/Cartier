package xyz.vopen.cartier.apple.tools;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import xyz.vopen.cartier.iosresign.cert.CertificateHandler;
import xyz.vopen.cartier.iosresign.exception.CertException;
import xyz.vopen.cartier.provision.ProvisionProcessor;
import xyz.vopen.cartier.provision.exception.RequestException;
import xyz.vopen.cartier.provision.ext.Result;
import xyz.vopen.cartier.provision.ext.response.AddAppIdResponse;
import xyz.vopen.cartier.provision.ext.response.AddDevicesResponse;
import xyz.vopen.cartier.provision.ext.response.CreateProvisioningProfileResponse;
import xyz.vopen.cartier.provision.ext.response.GetTeamsResponse;
import xyz.vopen.cartier.provision.ext.response.ListDevicesResponse;
import xyz.vopen.cartier.provision.ext.response.SubmitCertificateResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * xyz.vopen.cartier.apple.tools
 *
 * @author Elve.xu [xuhw@yyft.com]
 * @version v1.0 - 06/09/2017.
 */
public final class AppleApplication {
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final String CER_OUTPUT_DIR = "apple-account-cers-output";
    private static final String INSTALL_SHELL = "install.sh";
    private static final String PRIVATE_KEY = "privateKey.key";
    private static final String CSR = "CSR.certSigningRequest";
    private static final String CER = "ios_development.cer";
    private static final String MP_SUFX = ".mobileprovision";
    private static final String DEFAULT_PROFILE_NAME = "pyw";
    private static final String DEFAULT_APPID_NAME = "pyw";

    static AppleConfig appleConfig;
    private static String root;

    public static void main (String[] args) {
        String home = System.getenv("AAT_HOME");
        if (StringUtils.endsWith(home, "/")) {
            root = home.substring(0, home.length() - 1);
        } else {
            root = home;
        }
        CommandLineParser parser = new DefaultParser();
        Options options = new Options();

        // base option
        options.addOption(
                Option.builder("rit")
                        .longOpt("focusReInit")
                        .hasArg(false)
                        .required(false)
                        .desc("Focus Re-init Apple Account")
                        .build());

        options.addOption(
                Option.builder("rvk")
                        .longOpt("focusReInvoke")
                        .hasArg(false)
                        .required(false)
                        .desc("Focus Re-Revoke Apple Account")
                        .build());

        options.addOption(
                Option.builder("v")
                        .longOpt("verbose")
                        .hasArg(false)
                        .required(false)
                        .desc("Enable Processor Logger")
                        .build());

        // type option
        options.addOption(
                Option.builder("b")
                        .longOpt("batch")
                        .hasArg(false)
                        .required(false)
                        .desc("Multi-Apple-Accounts to Initialize")
                        .build());

        options.addOption(
                Option.builder("udid")
                        .hasArg()
                        .argName("Apple-DEVICE-UDID")
                        .desc("Apple Device's udid (40bit) ")
                        .build());

        // batch accounts option
        options.addOption(
                Option.builder("sjson")
                        .longOpt("sourceJsonFile")
                        .hasArg()
                        .argName("SOURCE JSON FILE")
                        .desc("Assign Batch Apple Accounts Json(xxxx.json) File .\r\nJson Example : " +
                                "\r\n[{" +
                                "\r\n\"u\" : \"Apple Account Username\"," +
                                "\r\n\"p\" : \"Apple Account Password\"," +
                                "\r\n\"pn\": \"Apple Profile Name, Default: pyw\"," +
                                "\r\n\"an\": \"Apple AppId Name, Default: pyw\"" +
                                "\r\n}]")
                        .build());


        // single option
        options.addOption(
                Option.builder("u")
                        .longOpt("username")
                        .hasArg()
                        .argName("USERNAME")
                        .desc("Apple Account Username ")
                        .build());

        options.addOption(
                Option.builder("p")
                        .longOpt("password")
                        .hasArg()
                        .argName("PASSWORD")
                        .desc("Apple Account Password ")
                        .build());

        options.addOption(
                Option.builder("pn")
                        .longOpt("profileName")
                        .hasArg()
                        .argName("APPLE PROFILE NAME")
                        .desc("Apple Development Profile Name ,Default: pyw ")
                        .build());

        options.addOption(
                Option.builder("an")
                        .longOpt("appIdName")
                        .hasArg()
                        .argName("APPLE AppId NAME")
                        .desc("Apple Development AppId Name ,Default: pyw ")
                        .build());

        CommandLine commandLine = null;
        String header = "sh ./cartier.sh --help\r\n Options:";
        String footer = "\r\nWarnsAndTips : " +
                "\r\nIf option contain [-b | --batch] , those options [-u ,-p ,-pn ,-an] will not working anymore" +
                "\r\nYou can initialize Apple Account Alone ,use Option [-u ,-p ,-pn ,-an] ." +
                "\r\nOr You can initialize Apple Accounts batch with json file ,use Option [-b | --batch]" +
                "\r\nEnjoy it!" +
                "\r\n-EOF-";

        try {
            commandLine = parser.parse(options, args);
        } catch (ParseException e) {
            new HelpFormatter().printHelp(128, "cartier", header, options, footer, true);
            System.exit(-1);
        }

        try {
            // start
            appleConfig = parse(commandLine);
            AccountHolder accountHolder = new AccountHolder();
            if (appleConfig.batch) {
                String sourceJsonFile = appleConfig.sourceJsonFile;
                if (StringUtils.isNoneBlank(sourceJsonFile)) {
                    String content = FileUtils.readFileToString(new File(sourceJsonFile), Charset.forName("UTF-8"));
                    JSONArray objects = JSONArray.parseArray(content);
                    if (objects != null && objects.size() > 0) {
                        for (Object o : objects) {
                            if (o instanceof JSONObject) {
                                JSONObject object = (JSONObject) o;
                                Account account = new Account();
                                if (object.containsKey("u")) account.username = object.getString("u");
                                if (object.containsKey("p")) account.password = object.getString("p");
                                if (object.containsKey("pn")) account.assignProfileName = object.getString("pn");
                                if (object.containsKey("an")) account.assignAppIdName = object.getString("an");
                                accountHolder.accounts.add(account);
                            }
                        }
                    }
                }
            } else {
                Account account = new Account();
                account.username = appleConfig.username;
                account.password = appleConfig.password;
                if (StringUtils.isNoneBlank(appleConfig.appIdName)) account.assignAppIdName = appleConfig.appIdName;
                if (StringUtils.isNoneBlank(appleConfig.profileName))
                    account.assignProfileName = appleConfig.profileName;
                accountHolder.accounts.add(account);
            }

            if (accountHolder.accounts.size() > 0) {
                long start = System.currentTimeMillis();
                CountDownLatch latch = new CountDownLatch(accountHolder.accounts.size());

                for (Account account : accountHolder.accounts) {
                    System.out.println("Start process Account: [" + account.username + " ]...");
                    Processor processor = new Processor(account, latch);
                    Thread t = new Thread(processor);
                    t.setName("Thread-Process[" + account.username + "]");
                    t.start();
                }
                latch.await();

                String sourceInstallShell = root + File.separator + "shell" + File.separator + INSTALL_SHELL;
                String targetInstallShell = root + File.separator +
                        CER_OUTPUT_DIR + File.separator + TODAY + File.separator + INSTALL_SHELL;

                FileUtils.copyFile(new File(sourceInstallShell), new File(targetInstallShell));
                //end
                System.out.println("All account(s) Processed! Times : " + (System.currentTimeMillis() - start) + " ms");
                System.out.println("Next step : exec install.sh");
                System.out.println("bye!");
            }

        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            System.exit(-2);
        } catch (IOException e) {
            System.out.println("Error(IO): Apple Accounts Json(xxxx.json) File read fail");
            System.exit(-3);
        } catch (InterruptedException e) {
            System.out.println("Error: Thread Wrong!");
            System.exit(-4);
        }
    }

    private static AppleConfig parse (CommandLine commandLine) throws IllegalArgumentException {
        AppleConfig appleConfig = new AppleConfig();
        if (commandLine.hasOption("rit")) {
            appleConfig.focusReInit = true;
        }

        if (commandLine.hasOption("rvk")) {
            appleConfig.focusReInvoke = true;
        }

        if (commandLine.hasOption("v")) {
            appleConfig.verbose = true;
        }

        if (commandLine.hasOption("udid")) {
            appleConfig.udid = commandLine.getOptionValue("udid");
        }

        if (commandLine.hasOption("b")) {
            appleConfig.batch = true;
        }

        if (!appleConfig.batch) {
            if (commandLine.hasOption("u")) {
                appleConfig.username = commandLine.getOptionValue("u");
            } else {
                throw new IllegalArgumentException("Error: Must set Apple-Account-Username.");
            }
            if (commandLine.hasOption("p")) {
                appleConfig.password = commandLine.getOptionValue("p");
            } else {
                throw new IllegalArgumentException("Error: Must set Apple-Account-Password.");
            }
            if (commandLine.hasOption("an")) {
                appleConfig.appIdName = commandLine.getOptionValue("an");
            }
            if (commandLine.hasOption("pn")) {
                appleConfig.profileName = commandLine.getOptionValue("pn");
            }
        } else {
            if (commandLine.hasOption("sjson")) {
                appleConfig.sourceJsonFile = commandLine.getOptionValue("sjson");
            } else {
                throw new IllegalArgumentException("Error: Must set Apple-Account-JSON-file ,because of you has set [-b] option.");
            }
        }

        return appleConfig;
    }

    private static class AppleConfig {
        private boolean focusReInit = true;
        private boolean focusReInvoke = true;
        private boolean verbose = false;
        private String username;
        private String password;
        private boolean batch = false;
        private String profileName;
        private String appIdName;
        private String sourceJsonFile;
        private String udid;
    }

    private static class AccountHolder {
        List<Account> accounts = new ArrayList<>();
    }

    private static class Account {
        String username;
        String password;
        String assignProfileName = DEFAULT_PROFILE_NAME;
        String assignAppIdName = DEFAULT_APPID_NAME;
        transient String teamId;
        transient String finalProfileName;
        transient String finalProfileId;
        transient String finalAppIdName;
        transient String finalAppIdId;
        transient String developer;
        transient boolean success;
    }

    private static String getShellTempDir () {
        return root + File.separator + "shell";
    }

    private final static String TODAY = DATE_FORMAT.format(new Date());

    private static String getCerOutputDir (String username) throws Exception {
        if (username != null && username.trim().length() > 0) {
            return root + File.separator + CER_OUTPUT_DIR + File.separator + TODAY + File.separator + username;
        }
        throw new java.lang.IllegalArgumentException("Error: username must not be null.");
    }

    /**
     * process
     */
    private static class Processor implements Runnable {
        Account account;
        CountDownLatch latch;

        private Processor (Account account, CountDownLatch latch) {
            if (account != null) {
                this.account = account;
            }
            this.latch = latch;
        }

        @Override
        public void run () {

            try {
                if (account == null) return;
                String currentPath = getCerOutputDir(account.username) + File.separator;
                if (appleConfig.focusReInit) {
                    FileUtils.deleteDirectory(new File(currentPath));
                }

                ProvisionProcessor provisionProcessor = ProvisionProcessor.getInstance();
                ProvisionProcessor.ProvisionHandler provisionHandler = provisionProcessor.getHandler();
                CertificateHandler certificateHandler = CertificateHandler.getInstance();
                CertificateHandler.Handler cerHandler = certificateHandler.getHandler(getShellTempDir());

                if (appleConfig.focusReInvoke) provisionHandler.revokeAccount(account.username, account.password);

                if (!appleConfig.verbose) provisionHandler.disableLogStatus();

                Result result = provisionHandler.login(account.username, account.password);
                if (result.getCode() == Result.Code.SUCCESS.code()) {
                    cerHandler.generateCSR(currentPath, PRIVATE_KEY, CSR, "xuhw@yyft.com", "pyw");
                    GetTeamsResponse teamsResponse = provisionHandler.getTeams();
                    String teamId = teamsResponse.getTeams().get(0).getTeamId();
                    String csrFilePath = currentPath + CSR;
                    SubmitCertificateResponse scr = provisionHandler.submitCertificateRequest(teamId, csrFilePath);
                    String certificateId = scr.getCertRequest().getCertificateId();
                    String destCerFilePath = currentPath + CER;
                    provisionHandler.downloadCertificateContent(teamId, certificateId, destCerFilePath);
                    String appIdName = AppleUtilss.nextAppIdName();
                    if (StringUtils.isNoneBlank(account.assignAppIdName)) {
                        appIdName = account.assignAppIdName;
                    }
                    AddAppIdResponse addAppIdResponse = provisionHandler.addAppId(teamId, appIdName);
                    String appIdId = addAppIdResponse.getAppId().getAppIdId();
                    String keys = null;
                    if (appleConfig.udid != null && appleConfig.udid.trim().length() > 0) {
                        keys = appleConfig.udid;
                    } else {
                        keys = StringUtils.join(AppleUtilss.toSerialKey());
                    }
                    String deviceAppleID = null;
                    ListDevicesResponse listDevicesResponse = provisionHandler.listDevices(teamId, ProvisionProcessor.Device.iphone, keys);
                    if (listDevicesResponse.getTotalRecords() == 0) {
                        AddDevicesResponse addDevicesResponse = provisionHandler.addDevice(teamId, keys, ProvisionProcessor.Device.iphone);
                        deviceAppleID = addDevicesResponse.getDevices().get(0).getDeviceId();
                    } else {
                        ListDevicesResponse.DevicesEntity entity = listDevicesResponse.getDevices().get(0);
                        // already exist
                        deviceAppleID = entity.getDeviceId();
                        if (!"c".equalsIgnoreCase(entity.getStatus())) {// enable
                            provisionHandler.enableDevice(teamId, keys, deviceAppleID);
                        }
                    }

                    String provisioningProfileName = AppleUtilss.nextMobileProvisionName();
                    if (StringUtils.isNoneBlank(account.assignProfileName)) {
                        provisioningProfileName = account.assignProfileName;
                    }

                    CreateProvisioningProfileResponse createProvisioningProfileResponse = provisionHandler.createProvisioningProfile(teamId, appIdName, appIdId,
                            "limited", certificateId, provisioningProfileName, new String[]{ deviceAppleID });
                    String provisioningProfileId = createProvisioningProfileResponse.getProvisioningProfile().getProvisioningProfileId();
                    String destMobileProvisionFilePath = currentPath + provisioningProfileName + MP_SUFX;
                    provisionHandler.downloadProfileContent(teamId, provisioningProfileId, destMobileProvisionFilePath);
                    byte[] content = IOUtils.toByteArray(new FileInputStream(destMobileProvisionFilePath));
                    String developer = AppleUtilss.parseDeveloper(new String(content, "UTF-8"));

                    account.teamId = teamId;
                    account.finalProfileName = provisioningProfileName;
                    account.finalAppIdName = appIdName;
                    account.finalAppIdId = appIdId;
                    account.finalProfileId = provisioningProfileId;
                    account.developer = developer;
                    System.out.println("Success ! Apple Account : " + account.username + " , Developer : " + developer + " ,TeamId : " + teamId);
                    account.success = true;
                } else {
                    System.out.println("Error : Account [" + account.username + "] , login failed ! Check password then retry.");
                }


            } catch (Exception | CertException | RequestException | xyz.vopen.cartier.provision.exception.CertException e) {
                System.out.println("Error : Account [" + account.username + "] , process failed ! Enable Logger then retry");
            } finally {
                if (latch != null) latch.countDown();
            }
        }
    }
}
