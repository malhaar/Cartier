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
 * Copyright (c) 2017 Luke Hutchison, with significant contributions from Davy De Durpel
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
import java.lang.reflect.Array;
import java.util.Map;

import xyz.vopen.cartier.classpathscanner.scanner.ClasspathFinder;
import xyz.vopen.cartier.classpathscanner.utils.ClasspathUtils;
import xyz.vopen.cartier.classpathscanner.utils.LogNode;
import xyz.vopen.cartier.classpathscanner.utils.ReflectionUtils;

/**
 * Extract classpath entries from the JBoss ClassLoader. See:
 *
 * https://github.com/jboss-modules/jboss-modules/blob/master/src/main/java/org/jboss/modules/ModuleClassLoader.java
 * 
 */
public class JBossClassLoaderHandler implements ClassLoaderHandler {
    @Override
    public boolean handle(final ClassLoader classLoader, final ClasspathFinder classpathFinder, final LogNode log)
            throws Exception {
        boolean handled = false;
        for (Class<?> c = classLoader.getClass(); c != null; c = c.getSuperclass()) {
            if ("org.jboss.modules.ModuleClassLoader".equals(c.getName())) {

                final Object module = ReflectionUtils.invokeMethod(classLoader, "getModule");
                final Object serviceLoader = ReflectionUtils.invokeMethod(module, "getCallerModuleLoader");
                @SuppressWarnings("unchecked")
                final Map<Object, Object> moduleMap = (Map<Object, Object>) ReflectionUtils
                        .getFieldVal(serviceLoader, "moduleMap");
                for (final Object key : moduleMap.keySet()) {
                    final Object futureModule = moduleMap.get(key);
                    final Object realModule = ReflectionUtils.invokeMethod(futureModule, "getModule");
                    final Object moduleLoader = ReflectionUtils.invokeMethod(realModule, "getClassLoader");

                    // type VFSResourceLoader[]
                    final Object vfsResourceLoaders = ReflectionUtils.invokeMethod(moduleLoader,
                            "getResourceLoaders");
                    if (vfsResourceLoaders != null) {
                        for (int i = 0, n = Array.getLength(vfsResourceLoaders); i < n; i++) {
                            String path = null;
                            // type VFSResourceLoader
                            final Object resourceLoader = Array.get(vfsResourceLoaders, i);
                            if (resourceLoader != null) {
                                // type VirtualFile
                                final Object root = ReflectionUtils.getFieldVal(resourceLoader, "root");
                                final File physicalFile = (File) ReflectionUtils.invokeMethod(root,
                                        "getPhysicalFile");
                                if (physicalFile != null) {
                                    final String name = (String) ReflectionUtils.invokeMethod(root, "getName");
                                    if (name != null) {
                                        final File file = new java.io.File(physicalFile.getParentFile(), name);
                                        if (ClasspathUtils.canRead(file)) {
                                            path = file.getAbsolutePath();
                                        } else {
                                            path = physicalFile.getAbsolutePath();
                                        }
                                    } else {
                                        path = physicalFile.getAbsolutePath();
                                    }
                                } else {
                                    // Fallback
                                    path = (String) ReflectionUtils.invokeMethod(root, "getPathName");
                                    if (path == null) {
                                        // Try File:
                                        Object file = root;
                                        if (file == null) {
                                            // Try JarFileResource:
                                            file = ReflectionUtils.getFieldVal(resourceLoader, "fileOfJar");
                                        }
                                        path = (String) ReflectionUtils.invokeMethod(file, "getAbsolutePath");
                                    }
                                }
                            }
                            handled |= classpathFinder.addClasspathElement(path, (ClassLoader) moduleLoader, log);
                        }
                    }
                }
            }
        }
        return handled;
    }
}