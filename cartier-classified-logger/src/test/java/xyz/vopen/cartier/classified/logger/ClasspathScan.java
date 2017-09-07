package xyz.vopen.cartier.classified.logger;

import xyz.vopen.cartier.classpathscanner.FastClasspathScanner;
import xyz.vopen.cartier.classpathscanner.matchprocessor.ClassAnnotationMatchProcessor;

/**
 * xyz.vopen.cartier.classified.logger
 *
 * @author Elve.xu [xuhw@yyft.com]
 * @version v1.0 - 16/08/2017.
 */
public class ClasspathScan {
    public static void main (String[] args) {

        FastClasspathScanner scanner = new FastClasspathScanner();
        scanner.matchClassesWithAnnotation(ClassifiedLogger.class, new ClassAnnotationMatchProcessor() {
            @Override
            public void processMatch (Class<?> classWithAnnotation) {
                System.out.println(classWithAnnotation);
            }
        });

        scanner.scan();
    }
}
