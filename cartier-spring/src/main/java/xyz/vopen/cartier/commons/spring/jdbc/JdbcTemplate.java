/**
 * Copyright 2006-2015 pyw.cn
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package xyz.vopen.cartier.commons.spring.jdbc;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

/**
 * 数据库操作类
 *
 * @author Hongvi Xu
 */
public class JdbcTemplate extends org.springframework.jdbc.core.JdbcTemplate {

    /**
     * 数据库分页sql
     */
    public static final String PAGESQL = "select * from (select t1.*,rownum r from ( ${sql} )t1 )t2  where t2.r>=? and t2.r<=?";
    protected Logger logger = Logger.getLogger(JdbcTemplate.class.getName());
    /**
     * runner
     */
    private QueryRunner queryRunner = new QueryRunner();

    public JdbcTemplate () {
        super();
    }

    public JdbcTemplate (DataSource dataSource) {
        super(dataSource);
    }

    public JdbcTemplate (DataSource dataSource, boolean lazyInit) {
        super(dataSource, lazyInit);
    }

    public QueryRunner getQueryRunner () {
        return queryRunner;
    }

    /**
     * 获取一个数据库连接，这个连接不需要关闭的，由Spring容器来管理
     *
     * @return
     */
    private Connection getConnection () {
        return DataSourceUtils.getConnection(this.getDataSource());
    }

    /**
     * 获得分页sql
     *
     * @param sql
     * @return
     */
    public String getSqlForPage (String sql) {
        return PAGESQL.replace("${sql}", sql);
    }

    /**
     * 使用反射得到指定对象信息
     *
     * @param sql          sql语句
     * @param params       sql中所用到的参数
     * @param requiredType 返回的具体类型，比如 Bean.class，返回就是Bean类型
     * @return
     */
    public Object queryForBean (String sql, Object[] params, Class requiredType) {
        return query(sql, params, new BeanHandler(requiredType));
    }

    /**
     * 使用反射得到指定对象的List集合
     *
     * @param sql          sql语句
     * @param params       sql中所用到的参数
     * @param requiredType 返回的具体类型，比如 Bean.class，返回就是Bean类型
     * @return
     */
    public List queryForBeanList (String sql, Object[] params, Class requiredType) {
        return (List) query(sql, params, new BeanListHandler(requiredType));
    }

    /**
     * 使用反射获取单个指定对象信息(注：这个用户不可以直接调用)
     *
     * @param sql
     * @param params
     * @param handler
     * @return
     */
    protected Object query (String sql, Object[] params, ResultSetHandler handler) {
        Connection conn = null;
        try {
            conn = getConnection();
            // logger.info("dbutils.query方法获取一个数据库连接");
            return queryRunner.query(conn, sql, handler, params);
//            return queryRunner.query(conn, sql, params, handler);
        } catch (SQLException t) {
            System.out.println("调用反射方法错误：" + t.getMessage());
            throw new DataIntegrityViolationException(t.getMessage());
        } finally {
            DataSourceUtils.releaseConnection(conn, this.getDataSource());
            // logger.info("dbutils.query方法获关闭一个数据库连接");
        }

    }

    public int[] batch (String sql, Object[][] params) {
        Connection conn = null;
        try {
            conn = getConnection();
            // logger.info("dbutils.batch方法获取一个数据库连接");
            return queryRunner.batch(conn, sql, params);
        } catch (SQLException t) {
            System.out.println(t.getMessage());
            throw new DataIntegrityViolationException(t.getMessage());
        } finally {
            DataSourceUtils.releaseConnection(conn, this.getDataSource());
            // logger.info("dbutils.batch方法关闭一个数据库连接");
        }
    }

    /**
     * 获取oracle SQL分页语句
     *
     * @param sql
     * @return
     */
    public String getPageSql (String sql) {
        return "select tb.* from ( select tb.*, rownum as nums from (" + sql
                + " ) tb )tb where tb.nums between ? and ? ";
    }

    /**
     * 获取Mysql SQL分页语句
     *
     * @param sql
     * @return
     */
    public String getMySqlPageSql (String sql) {
        return sql + " limit ?, ?";
    }

}
