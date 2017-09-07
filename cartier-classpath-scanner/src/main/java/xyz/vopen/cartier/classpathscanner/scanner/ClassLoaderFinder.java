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
package xyz.vopen.cartier.classpathscanner.scanner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import xyz.vopen.cartier.classpathscanner.FastClasspathScanner;
import xyz.vopen.cartier.classpathscanner.utils.AdditionOrderedSet;
import xyz.vopen.cartier.classpathscanner.utils.LogNode;

/** A class to find the unique ordered classpath elements. */
public class ClassLoaderFinder {
    /** Used for resolving the classes in the call stack. Requires RuntimePermission("createSecurityManager"). */
    private static CallerResolver CALLER_RESOLVER;

    static {
        try {
            // This can fail if the current SecurityManager does not allow
            // RuntimePermission ("createSecurityManager"):
            CALLER_RESOLVER = new CallerResolver();
        } catch (final SecurityException e) {
            // Handled in findAllClassLoaders()
        }
    }

    // Using a SecurityManager gets around the fact that Oracle removed sun.reflect.Reflection.getCallerClass, see:
    // https://www.infoq.com/news/2013/07/Oracle-Removes-getCallerClass
    // http://www.javaworld.com/article/2077344/core-java/find-a-way-out-of-the-classloader-maze.html
    private static final class CallerResolver extends SecurityManager {
        @Override
        protected Class<?>[] getClassContext() {
            return super.getClassContext();
        }
    }

    // -------------------------------------------------------------------------------------------------------------

    /** A class to find the unique ordered classpath elements. */
    public static List<ClassLoader> findClassLoaders(final ScanSpec scanSpec, final LogNode log) {
        List<ClassLoader> classLoadersRaw = scanSpec.overrideClassLoaders;
        LogNode classLoadersFoundLog = null;
        if (classLoadersRaw == null) {
            // ClassLoaders were not overridden

            // Add the ClassLoaders in the order system, caller, context; then remove any of them that are
            // parents/ancestors of one or more other classloaders (performed below).
            // There will generally only be one class left after this. In rare cases, you may have a separate
            // callerLoader and contextLoader, but those cases are ill-defined -- see:
            // http://www.javaworld.com/article/2077344/core-java/find-a-way-out-of-the-classloader-maze.html?page=2

            // Get system classloader
            classLoadersRaw = new ArrayList<>();
            classLoadersRaw.add(ClassLoader.getSystemClassLoader());

            // Get caller classloader
            if (CALLER_RESOLVER == null) {
                if (log != null) {
                    log.log(ClassLoaderFinder.class.getSimpleName() + " could not create "
                            + CallerResolver.class.getSimpleName() + ", current SecurityManager does not grant "
                            + "RuntimePermission(\"createSecurityManager\")");
                }
            } else {
                final Class<?>[] callStack = CALLER_RESOLVER.getClassContext();
                if (callStack == null) {
                    if (log != null) {
                        log.log(ClassLoaderFinder.class.getSimpleName() + ": "
                                + CallerResolver.class.getSimpleName() + "#getClassContext() returned null");
                    }
                } else {
                    final String fcsPkgPrefix = FastClasspathScanner.class.getPackage().getName() + ".";
                    int fcsIdx;
                    for (fcsIdx = callStack.length - 1; fcsIdx >= 0; --fcsIdx) {
                        if (callStack[fcsIdx].getName().startsWith(fcsPkgPrefix)) {
                            break;
                        }
                    }
                    if (fcsIdx < 0 || fcsIdx == callStack.length - 1) {
                        // Should not happen
                        throw new RuntimeException("Could not find caller of " + fcsPkgPrefix + "* in call stack");
                    }

                    // Get the caller's current classloader
                    classLoadersRaw.add(callStack[fcsIdx + 1].getClassLoader());
                }
            }

            // Get context classloader
            classLoadersRaw.add(Thread.currentThread().getContextClassLoader());

            // Add any custom-added classloaders after system/context classloaders
            if (scanSpec.addedClassLoaders != null) {
                classLoadersRaw.addAll(scanSpec.addedClassLoaders);
            }
            classLoadersFoundLog = log == null ? null : log.log("Found ClassLoaders:");

        } else {
            // ClassLoaders were overridden
            classLoadersFoundLog = log == null ? null : log.log("Override ClassLoaders:");
        }

        // Remove null ClassLoaders
        List<ClassLoader> classLoadersWorking = new ArrayList<>(classLoadersRaw.size());
        for (final ClassLoader classLoader : classLoadersRaw) {
            if (classLoader != null) {
                classLoadersWorking.add(classLoader);
            }
        }

        // Dedup ClassLoaders
        classLoadersWorking = AdditionOrderedSet.dedup(classLoadersWorking);

        // Remove all ancestral classloaders (they are called automatically during class load)
        final Set<ClassLoader> ancestralClassLoaders = new HashSet<>(classLoadersWorking.size());
        for (final ClassLoader classLoader : classLoadersWorking) {
            for (ClassLoader cl = classLoader.getParent(); cl != null; cl = cl.getParent()) {
                ancestralClassLoaders.add(cl);
            }
        }
        final List<ClassLoader> classLoaderOrder = new ArrayList<>(classLoadersWorking.size());
        for (final ClassLoader classLoader : classLoadersWorking) {
            if (!ancestralClassLoaders.contains(classLoader)) {
                // Build final ClassLoader order
                classLoaderOrder.add(classLoader);
            }
        }

        // Log all identified ClassLoaders
        if (classLoadersFoundLog != null) {
            for (final ClassLoader cl : classLoaderOrder) {
                classLoadersFoundLog.log("" + cl);
            }
        }
        return classLoaderOrder;
    }
}