package xyz.vopen.cartier.commons.spring.utils;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * Spring 自定义配置文件
 *
 * @author Elve.xu [xuhw@yyft.com]
 * @version v1.0 - 09/03/2017.
 */
public class PropertyPlaceholderConfigurer extends org.springframework.beans.factory.config.PropertyPlaceholderConfigurer {

    private static Properties properties = new Properties();

    public PropertyPlaceholderConfigurer () {
    }

    public void setLocations (List<String> locations) {
        Resource[] resources = new Resource[locations.size()];
        int i = 0;

        for (Iterator iterator = locations.iterator(); iterator.hasNext(); ++i) {
            String string = (String) iterator.next();
            if (!string.startsWith("/")) {
                string = "/" + string;
            }

            string = System.getProperty("user.dir") + "/" + "conf" + string;

            try {
                resources[i] = new UrlResource(Paths.get(string, new String[0]).toUri());
            } catch (MalformedURLException var7) {
                var7.printStackTrace();
            }
        }

        super.setLocations(resources);
    }

    protected void processProperties (ConfigurableListableBeanFactory beanFactoryToProcess, Properties props) throws BeansException {
        this.logger.info("start to load properties file");
        long start = System.currentTimeMillis();
        super.processProperties(beanFactoryToProcess, props);
        Iterator i$ = props.keySet().iterator();

        while (i$.hasNext()) {
            Object key = i$.next();
            String keyStr = key.toString();
            String value = props.getProperty(keyStr);
            properties.put(keyStr, value);
            this.logger.info("read properties, key = [" + key + "], value = [" + value + "]");
        }

        this.logger.info("loaded !, Times : " + (System.currentTimeMillis() - start) + "ms");
    }

    public static String getContextProperty (String key) {
        return properties.getProperty(key);
    }

    public static String getContextProperty (String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

}
