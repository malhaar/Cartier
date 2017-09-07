package xyz.vopen.cartier.commons.spring.jdbc;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * 数据库连接池(基于Tomcat Jdbc Pool)
 *
 * @author Elve.xu [xuhw@yyft.com]
 * @version v1.0 - 24/02/2017.
 */
public class CartierDataSource implements FactoryBean<DataSource>, InitializingBean {

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(CartierDataSource.class);

    /**
     * Return an instance (possibly shared or independent) of the object
     * managed by this factory.
     * <p>As with a {@link org.springframework.beans.factory.BeanFactory}, this allows support for both the
     * Singleton and Prototype design pattern.
     * <p>If this FactoryBean is not fully initialized yet at the time of
     * the call (for example because it is involved in a circular reference),
     * throw a corresponding {@link org.springframework.beans.factory.FactoryBeanNotInitializedException}.
     * <p>As of Spring 2.0, FactoryBeans are allowed to return {@code null}
     * objects. The factory will consider this as normal value to be used; it
     * will not throw a FactoryBeanNotInitializedException in this case anymore.
     * FactoryBean implementations are encouraged to throw
     * FactoryBeanNotInitializedException themselves now, as appropriate.
     *
     * @return an instance of the bean (can be {@code null})
     * @throws Exception in case of creation errors
     * @see org.springframework.beans.factory.FactoryBeanNotInitializedException
     */
    @Override
    public DataSource getObject () throws Exception {
        return datasource;
    }

    /**
     * Return the type of object that this FactoryBean creates,
     * or {@code null} if not known in advance.
     * <p>This allows one to check for specific types of beans without
     * instantiating objects, for example on autowiring.
     * <p>In the case of implementations that are creating a singleton object,
     * this method should try to avoid singleton creation as far as possible;
     * it should rather estimate the type in advance.
     * For prototypes, returning a meaningful type here is advisable too.
     * <p>This method can be called <i>before</i> this FactoryBean has
     * been fully initialized. It must not rely on state created during
     * initialization; of course, it can still use such state if available.
     * <p><b>NOTE:</b> Autowiring will simply ignore FactoryBeans that return
     * {@code null} here. Therefore it is highly recommended to implement
     * this method properly, using the current state of the FactoryBean.
     *
     * @return the type of object that this FactoryBean creates,
     * or {@code null} if not known at the time of the call
     * @see org.springframework.beans.factory.ListableBeanFactory#getBeansOfType
     */
    @Override
    public Class<?> getObjectType () {
        return this.datasource != null ? this.datasource.getClass() : DataSource.class;
    }

    /**
     * Is the object managed by this factory a singleton? That is,
     * will {@link #getObject()} always return the same object
     * (a reference that can be cached)?
     * <p><b>NOTE:</b> If a FactoryBean indicates to hold a singleton object,
     * the object returned from {@code getObject()} might get cached
     * by the owning BeanFactory. Hence, do not return {@code true}
     * unless the FactoryBean always exposes the same reference.
     * <p>The singleton status of the FactoryBean itself will generally
     * be provided by the owning BeanFactory; usually, it has to be
     * defined as singleton there.
     * <p><b>NOTE:</b> This method returning {@code false} does not
     * necessarily indicate that returned objects are independent instances.
     * An implementation of the extended {@link org.springframework.beans.factory.SmartFactoryBean} interface
     * may explicitly indicate independent instances through its
     * {@link org.springframework.beans.factory.SmartFactoryBean#isPrototype()} method. Plain {@link FactoryBean}
     * implementations which do not implement this extended interface are
     * simply assumed to always return independent instances if the
     * {@code isSingleton()} implementation returns {@code false}.
     *
     * @return whether the exposed object is a singleton
     * @see #getObject()
     * @see org.springframework.beans.factory.SmartFactoryBean#isPrototype()
     */
    @Override
    public boolean isSingleton () {
        return true;
    }

    /**
     * Invoked by a BeanFactory after it has set all bean properties supplied
     * (and satisfied BeanFactoryAware and ApplicationContextAware).
     * <p>This method allows the bean instance to perform initialization only
     * possible when all bean properties have been set and to throw an
     * exception in the event of misconfiguration.
     *
     * @throws Exception in the event of misconfiguration (such
     *                   as failure to set an essential property) or if initialization fails.
     */
    @Override
    public void afterPropertiesSet () throws Exception {
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

        // 超过removetime后, 是否进行没用连接废弃, 默认为true
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

        datasource = ds;
    }

    private DataSource datasource;
    private Properties connectorProperties;

    /**
     * Set Connector Properties for Database
     *
     * @param connectorProperties
     */
    public void setConnectorProperties (Properties connectorProperties) {
        this.connectorProperties = connectorProperties;
    }
}
