package xyz.vopen.cartier.commons.task;

import org.junit.Test;

import java.util.Enumeration;
import java.util.Properties;

/**
 * xyz.vopen.cartier.commons.task
 *
 * @author Elve.xu [xuhw@yyft.com]
 * @version v1.0 - 28/02/2017.
 */
public class EnvTest {

    @Test
    public void testEnv () {

        Properties properties = System.getProperties();
        Enumeration enumeration = properties.keys();
        while (enumeration.hasMoreElements()) {
            Object o = enumeration.nextElement();
            System.out.println(o + " -> " + properties.get(o));
        }
    }

}
