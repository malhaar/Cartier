package xyz.vopen.cartier.surface;

import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * New Application Start method , support our bash shells.
 *
 * @author Elve.xu [xuhw@yyft.com]
 * @version v1.0 - 28/07/2017.
 */
public class ApplicationRunner {

    private static final Logger LOG = Logger.getLogger(ApplicationRunner.class.getName());

    private static final String APPLICATION_META_INF = "conf/META-INF/xyz.vopen.cartier.surface.CartierApplication";
    private static final String APPLICATION_PROPERTIES = "conf/application.properties";

    /**
     * Main Method
     *
     * @param args
     *         shell all args
     */
    public static void main (String[] args) {

        try {

            if (args != null && args.length >= 1) {

                String[] appArgs = new String[args.length - 1];
                if (args.length > 1) {
                    System.arraycopy(appArgs, 1, appArgs, 0, args.length - 1);
                }

                String home = args[0];

                if (home != null && home.length() > 0) {
                    home = home.endsWith("/") ? home : home + "/";
                    String applicationFilePath = home + APPLICATION_META_INF;
                    // all application
                    String[] keys = getApplications(home);
                    boolean runAll = keys == null;
                    List<String> lines = Files.readAllLines(Paths.get(applicationFilePath), Charset.forName("UTF-8"));
                    for (String line : lines) {
                        if (line != null && line.trim().length() > 0) {
                            String[] tarry = line.split("=");
                            if (tarry.length != 2) {
                                continue;
                            }

                            // RUN -ALL
                            if(runAll) {
                                String applicationClassName = tarry[1];
                                if (applicationClassName != null && applicationClassName.length() > 0) {
                                    Class aClass = Class.forName(applicationClassName);
                                    if (aClass != null) {
                                        run(aClass, appArgs, home);
                                    }
                                }

                                continue;
                            }

                            // RUN -TARGET
                            String key = tarry[0];
                            boolean contain = false;
                            for (String temp : keys) {
                                if (key.equals(temp)) {
                                    contain = true;
                                    break;
                                }
                            }
                            if (contain) { // application.properties assgin application
                                String applicationClassName = tarry[1];
                                if (applicationClassName != null && applicationClassName.length() > 0) {
                                    Class aClass = Class.forName(applicationClassName);
                                    if (aClass != null) {
                                        run(aClass, appArgs, home);
                                    }
                                }
                            }
                        }
                    }
                }
            }

        } catch (Exception ignored) {
            System.err.println("Couldn't start application:\n" + ignored);
            System.exit(-1);
        }
    }

    /**
     * 获取启动程序(s)
     *
     * @param home
     *         Home
     *
     * @return 启动程序的 Key
     */
    private static String[] getApplications (String home) {
        try {
            String applicationPropertiesFile = home + APPLICATION_PROPERTIES;
            Properties temp = new Properties();
            temp.load(new FileInputStream(applicationPropertiesFile));
            if (temp.containsKey("applications.keys")) {
                String tempValue = temp.getProperty("applications.keys");
                if (tempValue != null && tempValue.trim().length() > 0) {
                    return tempValue.trim().split(",");
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }


    private static Pattern[] classpathPatterns = new Pattern[]{
            // maven project on idea
            Pattern.compile("(^(?!/target/classes).+)/(target/classes)/?"),
            Pattern.compile("(^(?!/target/test-classes).+)/(target/test-classes)/?")

            // maven project on eclipse
            // TODO ...

    };

    /**
     * default properties
     */
    private static String construct_of_assembly = "src/main/assembly";
    private static String construct_of_meta_inf_application = "resources/META-INF/xyz.vopen.cartier.surface.CartierApplication";

    /**
     * Run Local Test's case without package
     * <pre>
     * Just Like:
     *
     *      ApplicationRunner.test(CartierApplication.class);
     *
     * </pre>
     */
    public static <T extends CartierApplication> void test (Class<T> applicationClass) {

        try {
            String basePath = null;
            if (applicationClass != null) {

                String classDiskPath = applicationClass.getResource("/").getPath();
                if (classDiskPath != null && classDiskPath.trim().length() > 0) {
                    for (Pattern pattern : classpathPatterns) {
                        Matcher matcher = pattern.matcher(classDiskPath);
                        if (matcher.find()) {
                            basePath = matcher.group(1);
                            if (basePath != null && basePath.trim().length() > 0) {
                                basePath = basePath.endsWith("/") ? basePath : basePath + "/";
                                System.out.println("Work Root: " + basePath);
                                break;
                            }
                        }
                    }


                    if (basePath != null && basePath.trim().length() > 0) {
                        // ready to test
                        run(applicationClass, new String[]{}, basePath + construct_of_assembly);
                    } else {
                        System.out.println("Sorry ,We can't auto recognize base work directory ! ");
                        System.exit(-1);
                    }


                }
            }
        } catch (Exception e) {
            System.err.println("Couldn't start test application:\n" + e);
            System.exit(-1);
        }
    }

    /**
     * run application
     *
     * @param applicationClass
     *         application class
     * @param args
     *         application args
     */
    private static <T extends CartierApplication> void run (Class<T> applicationClass, String[] args, String home) {

        try {
            CartierApplication.run(args, applicationClass, home);
        } catch (Exception e) {
            ApplicationRunner.LOG.log(Level.SEVERE, "Cound nor create application", e);
        }

    }
}
