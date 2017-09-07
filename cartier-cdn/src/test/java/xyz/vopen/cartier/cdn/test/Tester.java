package xyz.vopen.cartier.cdn.test;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * xyz.vopen.cartier.cdn.test.ks3
 *
 * @author Elve.xu [xuhw@yyft.com]
 * @version v1.0 - 20/07/2017.
 */
public class Tester {

    public static void main (String[] args) throws IOException {


        byte[] sourceBytes = IOUtils.toByteArray(Paths.get("/Users/ive/Downloads/javaforosx.dmg").toUri());

        System.out.println(sourceBytes.length);

        System.out.println(20971520);
        System.out.println(10 * 1024 * 1024);

        
        
    }
    
}
