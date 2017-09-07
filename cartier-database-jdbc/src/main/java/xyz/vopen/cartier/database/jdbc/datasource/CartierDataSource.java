package xyz.vopen.cartier.database.jdbc.datasource;

import org.slf4j.LoggerFactory;
import xyz.vopen.cartier.database.jdbc.exception.CartierDBException;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * 数据库连接池(基于Tomcat Jdbc Pool)
 * <p>
 * <pre>
 *     MYSQL :
 *     
 *      # Driver
 *      jdbc.driver_class_name=com.mysql.jdbc.Driver
 *      # url 
 *      jdbc.url=jdbc:mysql:loadbalance://192.168.20.49:3306/xiaopeng2?roundRobinLoadBalance=true&zeroDateTimeBehavior=convertToNull&useUnicode=true&characterEncoding=utf8
 *      # username
 *      jdbc.username=username
 *      # password
 *      jdbc.password=password
 *      # 池启动时创建的连接数量
 *      jdbc.initial_size=10
 *      # 同一时间可以从池
 *      jdbc.max_active=50
 *      # 池里最大空闲连接, 超出空闲时间, 放会被释放
 *      jdbc.max_idle=10
 *      # 池里最小空闲连接, 低于这个数量将会被创建
 *      jdbc.min_idle=5
 *      # 最大等待时间
 *      jdbc.max_wait=10000
 *      # 超过指定时间后, 是否进行没用连接废弃
 *      jdbc.remove_abandoned=true
 *      # 废弃超时时间
 *      jdbc.remove_abandoned_timeout=180
 *
 * </pre>
 *
 * @author Elve.xu [xuhw@yyft.com]
 * @version v1.0 - 09/03/2017.
 */
public final class CartierDataSource {

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(CartierDataSource.class);

    private final Properties connectorProperties;

    private CartierDataSource (Builder builder) {
        this.connectorProperties = builder.connectorProperties;
    }

    private DataSource build () {
        return newCartierDataSource();
    }

    public static class Builder {
        private Properties connectorProperties;

        public Builder properties (Properties connectorProperties) {
            this.connectorProperties = connectorProperties;
            return this;
        }

        public DataSource build () throws CartierDBException {
            if (connectorProperties == null) {
                throw new CartierDBException("ERROR: Must set Database Connection Properties .");
            }
            return new CartierDataSource(this).build();
        }
    }

    private DataSource newCartierDataSource () {

        if (connectorProperties == null) {
            throw new RuntimeException("Database connector properties must not be null !");
        }
        org.apache.tomcat.jdbc.pool.DataSource ds = new org.apache.tomcat.jdbc.pool.DataSource();
        String driverClassName = connectorProperties.getProperty("jdbc.driver_class_name", "com.mysql.jdbc.Driver");
        ds.setDriverClassName(driverClassName);
        String url = connectorProperties.getProperty("jdbc.url");
        ds.setUrl(url);

        logger.debug("数据库连接池正在启动: {}", url);

        String username = connectorProperties.getProperty("jdbc.username", "");
        ds.setUsername(username);
        String password = connectorProperties.getProperty("jdbc.password", "");
        ds.setPassword(password);

        // 池启动时创建的连接数量
        int initialSize = Integer.parseInt(connectorProperties.getProperty("jdbc.initial_size", "5"));
        ds.setInitialSize(initialSize);
        logger.debug("池启动时创建的连接数量: {}", initialSize);

        // 同一时间可以从池分配的最多连接数量
        int maxActive = Integer.parseInt(connectorProperties.getProperty("jdbc.max_active", "15"));
        ds.setMaxActive(maxActive);
        logger.debug("同一时间可以从池分配的最多连接数量: {}", maxActive);

        // 池里不会被释放的最多空闲连接数量
        int maxIdle = Integer.parseInt(connectorProperties.getProperty("jdbc.max_idle", "10"));
        ds.setMaxIdle(maxIdle);
        logger.debug("池里不会被释放的最多空闲连接数量: {}", maxIdle);

        // 在不新建连接的条件下, 池中保持空闲的最少连接数
        int minIdle = Integer.parseInt(connectorProperties.getProperty("jdbc.min_idle", "5"));
        ds.setMinIdle(minIdle);
        logger.debug("在不新建连接的条件下, 池中保持空闲的最少连接数: {}", minIdle);

        // 在抛出异常之前, 池等待连接被回收的最长时间
        int maxWait = Integer.parseInt(connectorProperties.getProperty("jdbc.max_wait", "10000"));
        ds.setMaxWait(maxWait);
        logger.debug("在抛出异常之前, 池等待连接被回收的最长时间: {}", maxWait);

        // 超过remove time后, 是否进行没用连接废弃, 默认为true
        boolean removeAbandoned = Boolean
                .parseBoolean(connectorProperties.getProperty("jdbc.remove_abandoned", "true"));
        ds.setRemoveAbandoned(removeAbandoned);
        logger.debug("超过指定之间后, 是否进行没用连接废弃: {}", removeAbandoned);

        // 超时时间限制
        int removeAbandonedTimeout = Integer
                .parseInt(connectorProperties.getProperty("jdbc.remove_abandoned_timeout", "180"));
        ds.setRemoveAbandonedTimeout(removeAbandonedTimeout);
        logger.debug("移除没用连接超时时间: {}", removeAbandoned);

        // 探测
        ds.setValidationQuery(connectorProperties.getProperty("jdbc.validation_query", "SELECT 1"));
        // 从连接池获取连接时, 是否运行validationQuery, 默认为true
        ds.setTestOnBorrow(Boolean.parseBoolean(connectorProperties.getProperty("jdbc.test_on_borrow", "true")));
        // 将连接归还连接池前是否运行validationQuery, 默认为false
        ds.setTestOnReturn(Boolean.parseBoolean(connectorProperties.getProperty("jdbc.test_on_return", "false")));

        return ds;
    }

}
