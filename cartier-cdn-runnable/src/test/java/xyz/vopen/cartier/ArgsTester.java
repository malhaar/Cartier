package xyz.vopen.cartier;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.Test;

import java.util.Arrays;

/**
 * xyz.vopen.cartier
 *
 * @author Elve.xu [xuhw@yyft.com]
 * @version v1.0 - 26/07/2017.
 */
public class ArgsTester {

    private static final String[] paramKeys = { "-t", "-u", "-d", "-c", "-sp", "-dp" };

    @Test
    public void testParams () throws Exception {

        System.out.println(Arrays.binarySearch(paramKeys ,"-t"));
        System.out.println(Arrays.binarySearch(paramKeys ,"t"));

        System.out.println(ArrayUtils.contains(paramKeys , "-t"));
        System.out.println(ArrayUtils.contains(paramKeys , "t"));

        System.out.println(ArrayUtils.indexOf(paramKeys ,"-c"));
        System.out.println(ArrayUtils.indexOf(paramKeys ,"c"));

    }

}
