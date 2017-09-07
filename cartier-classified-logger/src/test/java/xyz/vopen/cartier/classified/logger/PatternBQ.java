package xyz.vopen.cartier.classified.logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * xyz.vopen.cartier.classified.logger
 *
 * @author Elve.xu [xuhw@yyft.com]
 * @version v1.0 - 14/08/2017.
 */
public class PatternBQ {
    public static void main (String[] args) {
        String str = "if a ansda {} asda , { } sadasdas,{}{}";
        Pattern pattern = Pattern.compile("\\{}|\\{\\s+}");
        Matcher matcher = pattern.matcher(str);
        while (matcher.find()) {
            System.out.println(matcher.group() + matcher.start());
        }

    }
}
