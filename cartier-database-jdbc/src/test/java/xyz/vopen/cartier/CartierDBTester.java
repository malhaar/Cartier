package xyz.vopen.cartier;

import com.alibaba.fastjson.JSONArray;
import org.junit.Test;
import xyz.vopen.cartier.database.jdbc.CartierDB;
import xyz.vopen.cartier.database.jdbc.JSONArrayHandler;
import xyz.vopen.cartier.database.jdbc.datasource.CartierDataSource;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

/**
 * xyz.vopen.cartier
 *
 * @author Elve.xu [xuhw@yyft.com]
 * @version v1.0 - 09/03/2017.
 */
public class CartierDBTester {

    @Test
    public void testConnection () throws Exception {

        Properties properties = new Properties();
        properties.load(new FileInputStream(new File("/Users/ive/git-pyw-repo/cartier/cartier-database-jdbc/src/test/java/xyz/vopen/cartier/db.properties")));
        DataSource dataSource = new CartierDataSource.Builder().properties(properties).build();
        CartierDB cartierDB = new CartierDB(dataSource);
        JSONArray jsonArray = cartierDB.query("select * from ios_provision_app limit 1" , new JSONArrayHandler());
        
        if(jsonArray != null) {
            System.out.println(jsonArray);
        }

        
    }

}
