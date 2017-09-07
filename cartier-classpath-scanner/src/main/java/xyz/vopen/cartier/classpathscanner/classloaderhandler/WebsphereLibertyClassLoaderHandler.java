/*
 * This file is part of FastClasspathScanner.
 * 
 * Author: R. Kempees
 * 
 * Hosted at: https://github.com/lukehutch/fast-classpath-scanner
 * 
 * --
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2017 R. Kempees
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

import java.io.File;
import java.util.List;

import xyz.vopen.cartier.classpathscanner.scanner.ClasspathFinder;
import xyz.vopen.cartier.classpathscanner.utils.LogNode;
import xyz.vopen.cartier.classpathscanner.utils.ReflectionUtils;

/**
 * WebsphereLibertyClassLoaderHandler.
 *
 * Used to support WAS Liberty Profile classloading in io.github.lukehutch.fastclasspathscanner
 *
 * @author R. Kempees
 */
public class WebsphereLibertyClassLoaderHandler implements ClassLoaderHandler {

    private static final String PKG_PREFIX = "com.ibm.ws.classloading.internal.";
    private static final String IBM_APP_CLASS_LOADER = PKG_PREFIX + "AppClassLoader";
    private static final String IBM_THREAD_CONTEXT_CLASS_LOADER = PKG_PREFIX + "ThreadContextClassLoader";

    @Override
    public boolean handle(final ClassLoader classLoader, final ClasspathFinder classpathFinder, final LogNode log)
            throws Exception {

        for (Class<?> c = classLoader.getClass(); c != null; c = c.getSuperclass()) {

            if (IBM_APP_CLASS_LOADER.equals(c.getName()) || IBM_THREAD_CONTEXT_CLASS_LOADER.equals(c.getName())) {
                Object smartClassPath = null;

                if (IBM_THREAD_CONTEXT_CLASS_LOADER.equals(c.getName())) {
                    final Object appLoader = ReflectionUtils.getFieldVal(classLoader, "appLoader");

                    if (appLoader == null) {
                        return false;
                    }
                    smartClassPath = ReflectionUtils.getFieldVal(appLoader, "smartClassPath");

                } else if (IBM_APP_CLASS_LOADER.equals(c.getName())) {
                    smartClassPath = ReflectionUtils.getFieldVal(classLoader, "smartClassPath");
                }

                if (smartClassPath == null) {
                    return false;
                }

                final List<?> classPathElements = (List<?>) ReflectionUtils.getFieldVal(smartClassPath,
                        "classPath");

                if (classPathElements != null) {
                    for (final Object classpath : classPathElements) {
                        final String path = getPath(classpath);

                        if (path != null && path.length() > 0) {
                            classpathFinder.addClasspathElement(path, classLoader, log);
                        }
                    }
                    return true;
                }
                return false;
            }

        }
        return false;
    }

    private String getPath(final Object classpath) throws Exception {
        final Object container = ReflectionUtils.getFieldVal(classpath, "container");
        if (container == null) {
            return "";
        }

        final Object delegate = ReflectionUtils.getFieldVal(container, "delegate");
        if (delegate == null) {
            return "";
        }

        final String path = (String) ReflectionUtils.getFieldVal(delegate, "path");
        if (path != null && path.length() > 0) {
            return path;
        }

        final Object base = ReflectionUtils.getFieldVal(delegate, "base");
        if (base == null) {
            // giving up.
            return "";
        }

        final Object archiveFile = ReflectionUtils.getFieldVal(base, "archiveFile");
        if (archiveFile != null) {
            final File file = (File) archiveFile;
            return file.getAbsolutePath();
        }
        return "";
    }
}