package xyz.vopen.cartier.cdn.runnable;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.vopen.cartier.cdn.CDNHandler;
import xyz.vopen.cartier.cdn.CDNType;
import xyz.vopen.cartier.cdn.exception.BucketException;
import xyz.vopen.cartier.cdn.exception.CDNException;
import xyz.vopen.cartier.cdn.exception.ObjectException;
import xyz.vopen.cartier.cdn.ks3.Ks3Config;
import xyz.vopen.cartier.cdn.ks3.Ks3Handler;

import java.io.Console;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Properties;

import static org.fusesource.jansi.Ansi.ansi;

/**
 * 工具Jar运行的主类
 *
 * @author Elve.xu [xuhw@yyft.com]
 * @version v1.0 - 20/07/2017.
 */
public final class CDNMain {

    private static Logger logger = LoggerFactory.getLogger(CDNMain.class);
    private static Boolean debug = false;

    private static enum Command {
        upload,
        download,
        cli,
    }

    private static void printHelp () {
        System.out.println();
        System.out.println("HELP");
        System.out.println("    [Usage]:");
        System.out.println("        java -jar cdn-tools-[version.x.x.x].jar [key1=[value1]] [key2=[value2]]...");
        System.out.println();
        System.out.println("    [Command Usage Example]:");
        System.out.println();
        System.out.println("        #上传文件");
        System.out.println("        java -jar cdn-tools-[version.x.x.x].jar -upload --t=CDN类型 --c=配置文件路径 --s=源文件路径 --b=存储目录 --k=存储目标文件名称KEY");
        System.out.println();
        System.out.println("        #下载文件");
        System.out.println("        java -jar cdn-tools-[version.x.x.x].jar -download --t=CDN类型 --c=配置文件路径 --d=下载文件本地存储路径 --b=存储目录 --k=存储目标文件名称KEY");
        System.out.println();
        System.out.println("        # 开启命令行模式");
        System.out.println("        java -jar cdn-tools-[version.x.x.x].jar -cli [--t=ks3 --c=/path/ks3.properties]          -> 开启命令行模式");
        System.out.println("            # 命令行子命令");
        System.out.println("            > ls bucketName 100             -> 查看目录bucketName下的100个文件");
        System.out.println("            > del bucketName fileName       -> 删除bucketName目录下面的fileName文件");
        System.out.println("            > desc bucketName               -> 查看文件夹属性");
        System.out.println("            > desc bucketName fileName      -> 查看文件属性");
        System.out.println("            > create bucketName             -> 创建目录");
        System.out.println();
        System.out.println("    [Params Infos]:");
        System.out.println("        --t CDN 类型");
        System.out.println("        --c 配置文件");
        System.out.println("        --s 源文件");
        System.out.println("        --d 目标文件");
        System.out.println("        --b 存储空间");
        System.out.println("        --k 目标存储文件的KEY");
        System.out.println();
        System.out.println("EOF");
        System.out.println();
    }

    /**
     * <pre>
     * [Usage]:
     *      Command:    java -jar cdn-tools-[version.x.x.x].jar [key1=[value1]] [key2=[value2]]...
     *
     * [Command Usage Example]:
     *
     *      #上传文件
     *      java -jar cdn-tools-[version.x.x.x].jar -upload --t=CDN类型 --c=配置文件路径 --s=源文件路径 --b=存储目录 --k=存储目标文件名称KEY
     *
     *      #下载文件
     *      java -jar cdn-tools-[version.x.x.x].jar -download --t=CDN类型 --c=配置文件路径 --d=下载文件本地存储路径 --b=存储目录 --k=存储目标文件名称KEY
     *
     *      # 开启命令行模式
     *      java -jar cdn-tools-[version.x.x.x].jar -cli [--t=ks3 --c=/path/ks3.properties]          -> 开启命令行模式
     *          # 命令行子命令
     *          > ls bucketName 100             -> 查看目录bucketName下的100个文件
     *          > del bucketName fileName       -> 删除bucketName目录下面的fileName文件
     *          > desc bucketName               -> 查看文件夹属性
     *          > desc bucketName fileName      -> 查看文件属性
     *          > create bucketName             -> 创建目录
     *
     * [Params Infos]:
     *      --t CDN 类型
     *      --c 配置文件
     *      --s 源文件
     *      --d 目标文件
     *      --b 存储空间
     *      --k 目标存储文件的KEY
     *
     * </pre>
     *
     * @param args
     *         参数
     */
    public static void main (String[] args) {

        if (ArrayUtils.isEmpty(args)) {
            printHelp();
        }

        // 获取命令
        String command = args[0].trim().replace("-", "");

        try {
            Command commandEnum = Command.valueOf(command);

            // --t CDN 类型
            CDNType type = null;
            // --c 配置文件
            Properties properties = null;
            // --s 源文件
            String sourceFile = null;
            // --d 目标文件
            String destFile = null;
            // --b 存储空间
            String storage = null;
            // --k 目标存储文件的KEY
            String fileKey = null;

            switch (commandEnum) {

                // 命令行模式(支持 ls .del .desc)
                case cli:

                    print("Welcome to use CDN-TOOLS !", true);

                    Console console = System.console();
                    if (console == null) {
                        print("No console: not in interactive mode!", true);
                        System.exit(0);
                    }

                    // check input types
                    if (args.length > 1) {
                        for (int i = 1; i < args.length; i++) {
                            String tempArgNV = args[i];
                            if (tempArgNV != null && tempArgNV.trim().length() > 0 && tempArgNV.contains("=")) {
                                String[] tempArray = tempArgNV.split("=");
                                if ("--t".equals(tempArray[0])) {
                                    type = CDNType.valueOf(tempArray[1]);
                                }

                                if ("--c".equals(tempArray[0])) {
                                    properties = new Properties();
                                    properties.load(new FileInputStream(tempArray[1]));
                                }
                            }
                        }
                    }

                    // if type is undefined
                    if (type == null) {
                        CDNType[] cdnTypes = CDNType.values();

                        print("This is all CDN type supported :", true);
                        for (int i = 0; i < cdnTypes.length; i++) {
                            print(String.format("  [%s] %s", i, cdnTypes[i].name()), true);
                        }

                        boolean right = false;
                        do {
                            print("cdn>", false);
                            String inputIndex = console.readLine();
                            isExist(inputIndex);
                            try {
                                int index = Integer.valueOf(inputIndex);
                                type = cdnTypes[index];
                                right = true;
                            } catch (Exception ignored) {
                            }
                        } while (!right);
                    }

                    // check properties
                    if (properties == null) {
                        boolean right = false;
                        do {
                            try {
                                print("Please input CND:" + type.name() + "'s config properties full path : ", false);
                                String inputPath = console.readLine();
                                isExist(inputPath);
                                if (StringUtils.isNoneBlank(inputPath)) {
                                    if (Files.exists(Paths.get(inputPath))) {
                                        properties = new Properties();
                                        properties.load(new FileInputStream(inputPath));
                                        right = true;
                                    } else {
                                        print("Sorry , No such file or directory !", true);
                                    }
                                }
                            } catch (Exception ignored) {
                            }
                        } while (!right);
                    }

                    CDNHandler handlerHolder = null;
                    // init cdn
                    switch (type) {
                        case ks3:

                            handlerHolder = new Ks3Handler();
                            Ks3Config ks3Config = new Ks3Config(type, properties);
                            // 初始化
                            handlerHolder.initialize(ks3Config);

                            print(" Ks3 Client Login Success !", true);

                            break;
                    }

                    // wait use input command
                    boolean exit = false;
                    do {
                        print("cdn>", false);
                        String exec = console.readLine();

                        if ("exit".equals(exec)) {
                            exit = true;
                            print("Bye", true);
                            break;
                        }

                        try {
                            // sub-command params
                            if (StringUtils.isNoneBlank(exec)) {

                                String[] tempA = exec.split("\\s+");
                                String subCommand = tempA[0];

                                // 列表查询
                                if ("ls".equals(subCommand)) {
                                    int limit = 500;
                                    String bucketName = null;
                                    if (tempA.length == 3) {
                                        String tLimit = tempA[2];
                                        bucketName = tempA[1];
                                        try {
                                            limit = Integer.valueOf(tLimit);
                                        } catch (Exception ignored) {
                                        }
                                    } else {
                                        err(" Sub command: ls , parameters's count must be 3 !");
                                        err("   Like : > ls bucketName 100 ");
                                        continue;
                                    }

                                    long start = System.currentTimeMillis();
                                    List list = handlerHolder.listFiles(bucketName, limit);

//                                    List<String> headersList = Arrays.asList("ID", "SUMMARY");
//                                    List<List<String>> rowsList = new ArrayList<>();

                                    if (list != null && list.size() > 0) {
                                        for (int i = 0; i < list.size(); i++) {
                                            System.out.println("[" + i + "] " + list.get(i));
//                                            rowsList.add(Arrays.asList(i + "", list.get(i).toString()));
                                        }
                                    }

//                                    Board board = new Board(75);
//                                    String tableString = board.setInitialBlock(new Table(board, 75, headersList, rowsList).tableToBlocks()).build().getPreview();
//                                    System.out.println(tableString);


                                    end(list.size(), (System.currentTimeMillis() - start));

                                    continue;
                                }

                                // 删除
                                if ("del".equals(subCommand)) {

                                    continue;
                                }

                                // 查看详情
                                if ("desc".equals(subCommand)) {

                                    continue;
                                }

                                //
                                if ("create".equals(subCommand)) {

                                    continue;
                                }


                            }

                        } catch (ObjectException e) {
                            e.printStackTrace();
                        } catch (BucketException e) {
                            e.printStackTrace();
                        } catch (CDNException e) {
                            e.printStackTrace();
                        } catch (Exception e) {

                        }
                    } while (!exit);


                    break;


                // 上传和下载只支持调用模式
                // command : --t=ks3 --c=/Users/ive/git-pyw-repo/cartier/cartier-cdn-runnable/src/main/resources/ks3-2.properties --s=/Users/ive/Downloads/javaforosx.dmg --b=ipa-test-4-ios --k=javaforosx-2.dmg
                case upload:

                    for (int i = 1; i < args.length; i++) {
                        String tempArgNV = args[i];

                        if (tempArgNV != null && tempArgNV.trim().length() > 0 && tempArgNV.contains("=")) {
                            String[] tempArray = tempArgNV.split("=");
                            String tempParamName = tempArray[0];
                            String tempParamValue = tempArray[1];

                            if ("--t".equals(tempParamName)) {
                                type = CDNType.valueOf(tempParamValue);
                            }

                            if ("--c".equals(tempParamName)) {
                                properties = new Properties();
                                properties.load(new FileInputStream(tempParamValue));
                            }

                            if ("--s".equals(tempParamName)) {
                                sourceFile = tempParamValue;
                            }

                            if ("--b".equals(tempParamName)) {
                                storage = tempParamValue;
                            }

                            if ("--k".equals(tempParamName)) {
                                fileKey = tempParamValue;
                            }
                        }
                    }

                    // check option values
                    if (type != null && StringUtils.isNoneBlank(sourceFile, storage, fileKey)) {

                        // invoke upload
                        switch (type) {
                            case ks3:

                                Ks3Handler handler = new Ks3Handler();
                                Ks3Config ks3Config = new Ks3Config(type, properties);
                                // 初始化
                                handler.initialize(ks3Config);
                                // 上传
                                try {
                                    handler.superUpload(sourceFile, storage, fileKey);

                                } catch (BucketException | CDNException | ObjectException e) {
                                    e.printStackTrace();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                break;

                            case aliyun:
                                System.out.println("Aliyun's CDN is not supported yet ~");
                                break;
                        }

                    }


                    break;

                case download:
//                    --t=ks3 --c=/Users/ive/git-pyw-repo/cartier/cartier-cdn-runnable/src/main/resources/ks3-2.properties --d=/Users/ive/Downloads/javaforosx-download.dmg --b=ipa-test-4-ios --k=javaforosx-2.dmg
                    for (int i = 1; i < args.length; i++) {
                        String tempArgNV = args[i];

                        if (tempArgNV != null && tempArgNV.trim().length() > 0 && tempArgNV.contains("=")) {
                            String[] tempArray = tempArgNV.split("=");
                            String tempParamName = tempArray[0];
                            String tempParamValue = tempArray[1];

                            if ("--t".equals(tempParamName)) {
                                type = CDNType.valueOf(tempParamValue);
                            }

                            if ("--c".equals(tempParamName)) {
                                properties = new Properties();
                                properties.load(new FileInputStream(tempParamValue));
                            }

                            if ("--d".equals(tempParamName)) {
                                destFile = tempParamValue;
                            }

                            if ("--b".equals(tempParamName)) {
                                storage = tempParamValue;
                            }

                            if ("--k".equals(tempParamName)) {
                                fileKey = tempParamValue;
                            }
                        }
                    }

                    // check option values
                    if (type != null && StringUtils.isNoneBlank(destFile, storage, fileKey)) {

                        // invoke download
                        switch (type) {
                            case ks3:

                                Ks3Handler handler = new Ks3Handler();
                                Ks3Config ks3Config = new Ks3Config(type, properties);
                                // 初始化
                                handler.initialize(ks3Config);
                                try {
                                    handler.superDownload(storage, fileKey, destFile);
                                } catch (BucketException | CDNException | ObjectException e) {
                                    e.printStackTrace();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                break;

                            case aliyun:
                                System.out.println("Aliyun's CDN is not supported yet ~");
                                break;
                        }

                    }

                    break;

                default:
                    System.out.println("Command not found: -" + type.name());
                    printHelp();
                    break;
            }

        } catch (Exception e) {
            System.out.println("Something is Wrong , Cause by " + e.getMessage());
            printHelp();
            System.exit(-1);
        } finally {
        }

    }

    private static void isExist (String input) {
        if (StringUtils.equalsAny(input, "exit", "quit")) {
            print("Bye", true);
            System.exit(-1);
        }
    }

    private static void print (String content, Boolean ln) {
        if (ln) {
            System.out.println(ansi().render(String.format("@|green %s |@", content)));
        } else {
            System.out.print(ansi().render(String.format("@|green %s |@", content)));
        }
    }

    private static void err (String content) {
        System.out.println(ansi().render(String.format("@|red %s |@", content)));
    }

    private static DecimalFormat decimalFormat = new DecimalFormat("#.00");

    private static void end (int size, long sec) {
        System.out.println(size + " rows in set (" + decimalFormat.format(sec / 1000.0) + " sec)");
    }

}
