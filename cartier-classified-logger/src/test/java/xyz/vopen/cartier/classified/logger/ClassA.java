package xyz.vopen.cartier.classified.logger;

/**
 * xyz.vopen.cartier.classified.logger
 *
 * @author Elve.xu [xuhw@yyft.com]
 * @version v1.0 - 11/08/2017.
 */
public class ClassA {


    public void print() {
        System.out.println("print method");

        Thread thread = Thread.currentThread();
        System.out.println("Current Thread Name = " + thread.getName());
        StackTraceElement[] elements = thread.getStackTrace();

        System.out.println("StackTraceElement: ");
        for (StackTraceElement element : elements) {
            System.out.println("\t " + element.getClassName());
        }

        System.out.println("----------------------------------------------------------------------");

    }

}
