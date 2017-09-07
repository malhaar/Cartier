/*
 * This file is part of FastClasspathScanner.
 * 
 * Author: Luke Hutchison
 * 
 * Hosted at: https://github.com/lukehutch/fast-classpath-scanner
 * 
 * --
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Luke Hutchison
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without
 * limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so, subject to the following
 * conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO
 * EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN
 * AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 */
package xyz.vopen.cartier.classpathscanner.classloaderhandler;

import xyz.vopen.cartier.classpathscanner.scanner.ClasspathFinder;
import xyz.vopen.cartier.classpathscanner.utils.LogNode;
import xyz.vopen.cartier.classpathscanner.utils.ReflectionUtils;

/**
 * Extract classpath entries from the Weblogic ClassLoader. See:
 */
public class WeblogicClassLoaderHandler implements ClassLoaderHandler {
    @Override
    public boolean handle(final ClassLoader classLoader, final ClasspathFinder classpathFinder, final LogNode log)
            throws Exception {
        for (Class<?> c = classLoader.getClass(); c != null; c = c.getSuperclass()) {
            if ("weblogic.utils.classloaders.ChangeAwareClassLoader".equals(c.getName())) {
                final String classpath = (String) ReflectionUtils.invokeMethod(classLoader, "getClassPath");
                return classpathFinder.addClasspathElements(classpath, classLoader, log);
            }
        }
        return false;
    }
}
