/**
 * Copyright 2006-2015 vopen.xyz
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
 * JdbcTemplate for cartier
 *
 * @author Elve Xu
 */
public class JdbcTemplate extends org.springframework.jdbc.core.JdbcTemplate {

    /**
     * db page sql
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
     * Get Connection
     *
     * @return connection instance
     */
    private Connection getConnection () {
        return DataSourceUtils.getConnection(this.getDataSource());
    }

    /**
     * page sql
     *
     * @param sql
     *         sql
     *
     * @return page sql
     */
    public String getSqlForPage (String sql) {
        return PAGESQL.replace("${sql}", sql);
    }

    /**
     * Query for bean
     *
     * @param sql
     *         sql
     * @param params
     *         sql params
     * @param requiredType
     *         class type
     *
     * @return Object
     */
    public Object queryForBean (String sql, Object[] params, Class requiredType) {
        return query(sql, params, new BeanHandler(requiredType));
    }

    /**
     * queryForBeanList
     *
     * @param sql
     *         sql
     * @param params
     *         sql params
     * @param requiredType
     *         class type
     *
     * @return result list
     */
    public List queryForBeanList (String sql, Object[] params, Class requiredType) {
        return (List) query(sql, params, new BeanListHandler(requiredType));
    }

    /**
     * query
     *
     * @param sql
     *         sql
     * @param params
     *         params
     * @param handler
     *         ResultSetHandler
     *
     * @return result object
     *
     * @see ResultSetHandler
     */
    protected Object query (String sql, Object[] params, ResultSetHandler handler) {
        Connection conn = null;
        try {
            conn = getConnection();
            return queryRunner.query(conn, sql, handler, params);
        } catch (SQLException t) {
            System.out.println("Reflection Error：" + t.getMessage());
            throw new DataIntegrityViolationException(t.getMessage());
        } finally {
            DataSourceUtils.releaseConnection(conn, this.getDataSource());
        }

    }

    public int[] batch (String sql, Object[][] params) {
        Connection conn = null;
        try {
            conn = getConnection();
            return queryRunner.batch(conn, sql, params);
        } catch (SQLException t) {
            System.out.println(t.getMessage());
            throw new DataIntegrityViolationException(t.getMessage());
        } finally {
            DataSourceUtils.releaseConnection(conn, this.getDataSource());
        }
    }

    /**
     * get page sql for oracle
     *
     * @param sql
     *
     * @return
     */
    public String getPageSql (String sql) {
        return "select tb.* from ( select tb.*, rownum as nums from (" + sql
                + " ) tb )tb where tb.nums between ? and ? ";
    }

    /**
     * get page sql for mysql
     *
     * @param sql
     *
     * @return
     */
    public String getMySqlPageSql (String sql) {
        return sql + " limit ?, ?";
    }

}
