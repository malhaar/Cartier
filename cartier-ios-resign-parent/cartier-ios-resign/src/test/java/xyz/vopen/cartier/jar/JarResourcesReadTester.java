package xyz.vopen.cartier.jar;

import java.io.IOException;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * xyz.vopen.cartier.jar
 *
 * @author Elve.xu [xuhw@yyft.com]
 * @version v1.0 - 29/07/2017.
 */
public class JarResourcesReadTester {

    public static void main (String[] args) throws IOException {

        JarFile jarFile = new JarFile("/Users/ive/git-pyw-repo/cartier/cartier-ios-resign-parent/cartier-ios-resign/src/test/resources/cartier-provision-tools-1.0.0-SNAPSHOT.jar");

        System.out.println(jarFile);

        JarEntry jarEntry = jarFile.getJarEntry("assembly/bash/buildCSR.sh");



        System.out.println(jarEntry);

    }

}
