package xyz.vopen.cartier.surface;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import org.slf4j.LoggerFactory;
import xyz.vopen.cartier.surface.exception.CartierOptionException;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Abstract Main Application
 *
 * @author Elve.xu [xuhw@yyft.com]
 * @version v1.0 - 15/02/2017.
 */
public abstract class CartierApplication {

    private static final Logger LOG = Logger.getLogger(CartierApplication.class.getName());
    private static final String APPLICATION_PROPERTIES = "conf/application.properties";
    // DEFAULT LOG
    private static final String LOGBACK_KEY = "application.logback";
    private static final String LOG4J2_KEY = "application.log4j2";

    protected static CartierApplication application;
    protected static Properties applicationProperties = new Properties();
    protected CartierApplicationContext context;
    protected static String root;

    /**
     * 参数格式 ,<br/>
     * <pre>
     *    Runner Command for JAVA_OPT:
     *        1.-O&lt;name&gt;=&lt;value&gt; set a system property
     *
     *    Examples : ('-O options' must be set after *.jar)
     *        java -jar demo.jar -Oprofile=production    // set an option profile = production
     *
     *    Code Usage Example :
     *        String profile = Applications.getOption("profile");
     *
     * </pre>
     * <p>
     * <pre>
     *
     *   Recommend Package :
     *
     *     if you don't want write start shell for you application ,we support some shells ,
     *     like :   start.sh
     *              stop.sh
     *              restart.sh
     *              server.sh
     *              dump.sh
     *     Also Support Windows:
     *              start.bat
     *
     *     See : {@link ApplicationRunner}
     *     Code Conf:
     *
     *          Create "<code>resources/META-INF/xyz.vopen.cartier.surface.CartierApplication</code>" File
     *          Like this :
     *              # conf you application into upper file
     *              application=xyz.vopen.cartier.iosresign.runnable.IOSResignApplication
     *
     *     Maven Conf:
     *
     *        &lt;plugin&gt;
     *              &lt;artifactId&gt;maven-dependency-plugin&lt;/artifactId&gt;
     *              &lt;executions&gt;
     *                  &lt;execution&gt;
     *                      &lt;id&gt;unpack&lt;/id&gt;
     *                      &lt;phase&gt;package&lt;/phase&gt;
     *                      &lt;goals&gt;
     *                          &lt;goal&gt;unpack&lt;/goal&gt;
     *                      &lt;/goals&gt;
     *                      &lt;configuration&gt;
     *                          &lt;artifactItems&gt;
     *                              &lt;artifactItem&gt;
     *                                  &lt;groupId&gt;xyz.vopen.cartier&lt;/groupId&gt;
     *                                  &lt;artifactId&gt;cartier-surface&lt;/artifactId&gt;
     *                                  &lt;version&gt;${project.parent.version}&lt;/version&gt;
     *                                  &lt;outputDirectory&gt;${project.build.directory}/bin&lt;/outputDirectory&gt;
     *                                  &lt;includes&gt;assembly/bin/**&lt;/includes&gt;
     *                              &lt;/artifactItem&gt;
     *                          &lt;/artifactItems&gt;
     *                      &lt;/configuration&gt;
     *                  &lt;/execution&gt;
     *              &lt;/executions&gt;
     *       &lt;/plugin&gt;
     *
     * </pre>
     *
     * @param args
     *         args
     */
    protected static <T extends CartierApplication> void run (String[] args, Class<T> clazz) {

        CartierApplication.LOG.log(Level.INFO, "Main Method Args : " + Arrays.toString(args));
        try {
            application = clazz.newInstance();
            CartierApplicationContext context = null;
            if (application != null) {
                context = new CartierApplicationContext() {};
                context.setOptions(parseMainArgs(args));
                context.setProperties(applicationProperties);

                if (root != null && root.trim().length() > 0) {
                    context.setRuntimeRoot(root);
                }

                application.context = context;

                // init application
                CartierApplication.LOG.log(Level.INFO, "Start invoke application's init method");
                application.init(context);

                // start application
                CartierApplication.LOG.log(Level.INFO, "Start invoke application's start method");
                application.start(context);

                // shutdown hook
                CartierApplication.LOG.log(Level.INFO, "Start invoke application's registerShutdownHook method");
                registerShutdownHook(context);
            }

        } catch (Exception e) {
            CartierApplication.LOG.log(Level.SEVERE, "Can't run application", e);
        } finally {

        }

    }

    /**
     * New Run Method ,auto support application runtime root path : {@link #root}
     */
    protected static <T extends CartierApplication> void run (String[] args, Class<T> clazz, String root) {
        if (root != null && root.trim().length() > 0) {
            CartierApplication.root = root.endsWith("/") ? root : root + "/";
        }

        String propertiesPath = CartierApplication.root + APPLICATION_PROPERTIES;
        Path propertiesPaths = Paths.get(propertiesPath);
        if (Files.exists(propertiesPaths)) {
            try {
                applicationProperties.load(new FileInputStream(propertiesPaths.toFile()));
            } catch (IOException ignored) {
            }
        }

        try {
            // init logback
            if (applicationProperties.containsKey(LOGBACK_KEY)) {

                String logback = CartierApplication.root + applicationProperties.get(LOGBACK_KEY).toString();
                LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
                JoranConfigurator configurator = new JoranConfigurator();
                configurator.setContext(lc);
                lc.reset();
                configurator.doConfigure(logback);

            } else if (applicationProperties.containsKey(LOG4J2_KEY)) {
                // TODO init log4j
            }
        } catch (Exception ignored) {
        }

        // run
        run(args, clazz);
    }

    /**
     * customer option pattern
     **/
    private static final String CARTIER_OPTION_PATTERN = "^-O([A-Za-z]+)=(.+)$";

    /**
     * Parse Main method input parameters into Map
     *
     * @param args
     *         parameter
     *
     * @return return map
     */
    protected static Map<String, String> parseMainArgs (String[] args) {
        Pattern pattern = Pattern.compile(CARTIER_OPTION_PATTERN);
        Map<String, String> options = null;
        if (args != null && args.length > 0) {
            options = new ConcurrentHashMap<String, String>();
            for (String arg : args) {
                if (arg != null && arg.trim().length() > 0) {
                    Matcher matcher = pattern.matcher(arg);
                    if (matcher.find()) {
                        String optionKey = matcher.group(1);
                        String optionValue = matcher.group(2);
                        if (options.containsKey(optionKey)) {
                            CartierApplication.LOG.warning("WARN: Option : [" + optionKey + "] is repeated!");
                        }
                        options.put(optionKey, optionValue);
                    } else {
                        CartierApplication.LOG.warning("WARN: Wrong Option Config , Demo : -OdemoKey=demoValue");
                    }
                }
            }

            if (options.size() > 0) {
                try {
                    application.checkOptions(options);
                } catch (CartierOptionException e) {
                    CartierApplication.LOG.log(Level.SEVERE, "Option Config Wrong! ", e);
                }
            }

        }
        return options;
    }

    protected static void registerShutdownHook (final CartierApplicationContext context) {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            private volatile boolean hasShutdown = false;
            private AtomicInteger shutdownTimes = new AtomicInteger(0);

            @Override
            public void run () {
                synchronized (this) {
                    CartierApplication.LOG.info("++++++++++++++++++++++++++++++++++++++++++++++#####");
                    CartierApplication.LOG.info("shutdown hook was invoked, " + this.shutdownTimes.incrementAndGet());
                    if (!this.hasShutdown) {
                        this.hasShutdown = true;
                        long begineTime = System.currentTimeMillis();


                        if (application != null) {
                            try {
                                application.shutdown(context);
                            } catch (Exception e) {
                            }
                            try {
                                application.context.release();
                            } catch (Exception e) {
                            }
                        }

                        long consumingTimeTotal = System.currentTimeMillis() - begineTime;
                        CartierApplication.LOG.info("shutdown hook over, consuming time total(ms): " + consumingTimeTotal);
                    }
                    CartierApplication.LOG.info("++++++++++++++++++++++++++++++++++++++++++++++*****");
                }
            }
        }, "ShutdownHook"));
    }

    /**
     * New init method for sub-application
     *
     * @param context
     *         application context
     */
    public abstract void init (CartierApplicationContext context) throws Exception;

    /**
     * start method
     *
     * @throws Exception
     *         exception
     */
    public abstract void start (CartierApplicationContext context) throws Exception;

    /**
     * shutdown method
     *
     * @throws Exception
     *         exception
     */
    public abstract void shutdown (CartierApplicationContext context) throws Exception;

    /**
     * check application option parameter ,<br/>
     * if config error ,just throw <code>{@link CartierOptionException}</code>
     *
     * @param options
     *         option map
     *
     * @throws CartierOptionException
     *         option exception
     * @deprecated use set properties on [appliation.properties] instead of
     */
    @Deprecated
    public abstract void checkOptions (Map<String, String> options) throws CartierOptionException;

}
