package xyz.vopen.cartier;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * xyz.vopen.cartier
 *
 * @author Elve.xu [xuhw@yyft.com]
 * @version v1.0 - 06/03/2017.
 */
public class TestOptionPattern {

    private final String CARTIER_OPTION_PATTERN = "^-O([A-Za-z]+)=(\\w+)$";
    
    @Test
    public void testPattern() {
        
        String temp = "-Otemp=a";

        Pattern pattern = Pattern.compile(CARTIER_OPTION_PATTERN);

        Matcher matcher = pattern.matcher(temp);

        if(matcher.find()) {
            System.out.println(matcher.group(1));
            System.out.println(matcher.group(2));
        }
        
    }
    
    
}
