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
package xyz.vopen.cartier.classpathscanner.utils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Resolve relative paths and URLs/URIs against a base path in a way that is faster than Java's URL/URI parser (and
 * much faster than Path), while aiming for cross-platform compatibility, and hopefully in particular being robust
 * to the many forms of Windows path weirdness.
 */
public class FastPathResolver {

    /** Match %-encoded characters in URLs. */
    private static final Pattern percentMatcher = Pattern.compile("([%][0-9a-fA-F][0-9a-fA-F])+");

    /** True if we're running on Windows. */
    private static final boolean WINDOWS = File.separatorChar == '\\';

    /** Translate backslashes to forward slashes, optionally removing trailing separator. */
    private static void translateSeparator(final String path, final int startIdx, final int endIdx,
            final boolean stripFinalSeparator, final StringBuilder buf) {
        for (int i = startIdx; i < endIdx; i++) {
            final char c = path.charAt(i);
            if (c == '\\' || c == '/') {
                // Strip trailing separator, if necessary
                if (i < endIdx - 1 || !stripFinalSeparator) {
                    // Remove duplicate separators
                    final char prevChar = buf.length() == 0 ? '\0' : buf.charAt(buf.length() - 1);
                    if (prevChar != '/') {
                        buf.append('/');
                    }
                }
            } else {
                buf.append(c);
            }
        }
    }

    /** Unescape runs of percent encoding, e.g. "%20%20" -> " ". */
    private static void unescapePercentEncoding(final String path, final int startIdx, final int endIdx,
            final StringBuilder buf) {
        if (endIdx - startIdx == 3 && path.charAt(startIdx + 1) == '2' && path.charAt(startIdx + 2) == '0') {
            // Fast path for "%20"
            buf.append(' ');
        } else {
            final byte[] bytes = new byte[(endIdx - startIdx) / 3];
            for (int i = startIdx, j = 0; i < endIdx; i += 3, j++) {
                final char c1 = path.charAt(i + 1);
                final char c2 = path.charAt(i + 2);
                final int digit1 = (c1 >= '0' && c1 <= '9') ? (c1 - '0')
                        : (c1 >= 'a' && c1 <= 'f') ? (c1 - 'a' + 10) : (c1 - 'A' + 10);
                final int digit2 = (c2 >= '0' && c2 <= '9') ? (c2 - '0')
                        : (c2 >= 'a' && c2 <= 'f') ? (c2 - 'a' + 10) : (c2 - 'A' + 10);
                bytes[j] = (byte) ((digit1 << 4) | digit2);
            }
            final String str = new String(bytes, StandardCharsets.UTF_8);
            // Prevent double escaping issues by translating any escaped separators 
            translateSeparator(str, 0, str.length(), false, buf);
        }
    }

    /**
     * Parse percent encoding, e.g. "%20" -> " "; convert '/' or '\\' to SEP; remove trailing separator char if
     * present.
     */
    private static String normalizePath(final String path) {
        final boolean hasPercent = path.indexOf('%') >= 0;
        if (!hasPercent && path.indexOf('\\') < 0 && !path.endsWith("/")) {
            return path;
        } else {
            final int len = path.length();
            final StringBuilder buf = new StringBuilder();
            if (!hasPercent) {
                // Fast path -- no '%', don't do regexp matching
                translateSeparator(path, 0, len, /* stripFinalSeparator = */ true, buf);
                return buf.toString();
            } else {
                // Translate '%'-encoding
                int prevEndMatchIdx = 0;
                final Matcher matcher = percentMatcher.matcher(path);
                while (matcher.find()) {
                    final int startMatchIdx = matcher.start();
                    final int endMatchIdx = matcher.end();
                    translateSeparator(path, prevEndMatchIdx, startMatchIdx, /* stripFinalSeparator = */ false,
                            buf);
                    unescapePercentEncoding(path, startMatchIdx, endMatchIdx, buf);
                    prevEndMatchIdx = endMatchIdx;
                }
                translateSeparator(path, prevEndMatchIdx, len, /* stripFinalSeparator = */ true, buf);
            }
            return buf.toString();
        }
    }

    /**
     * Strip away any "jar:" prefix from a filename URI, and convert it to a file path, handling possibly-broken
     * mixes of filesystem and URI conventions; resolve relative paths relative to resolveBasePath. Returns null if
     * relativePathStr is an "http(s):" path.
     */
    public static String resolve(final String resolveBasePath, final String relativePathStr) {
        // See:
        // http://stackoverflow.com/a/17870390/3950982
        // https://weblogs.java.net/blog/kohsuke/archive/2007/04/how_to_convert.html

        if (relativePathStr == null || relativePathStr.isEmpty()) {
            return resolveBasePath;
        }
        // We don't fetch remote classpath entries, although they are theoretically valid if using a URLClassLoader
        if (relativePathStr.startsWith("http:") || relativePathStr.startsWith("https:")) {
            return null;
        }
        int startIdx = 0;
        // Ignore "jar:", we look for ".jar" on the end of filenames instead
        if (relativePathStr.startsWith("jar:", startIdx)) {
            startIdx += 4;
        }
        // Strip off any "file:" prefix from relative path
        boolean isAbsolutePath = false;
        String prefix = "";
        if (relativePathStr.startsWith("file:", startIdx)) {
            startIdx += 5;
            if (WINDOWS) {
                if (relativePathStr.startsWith("\\\\\\\\", startIdx)
                        || relativePathStr.startsWith("////", startIdx)) {
                    // Windows UNC URL 
                    startIdx += 4;
                    prefix = "//";
                    isAbsolutePath = true;
                } else {
                    if (relativePathStr.startsWith("\\\\", startIdx)) {
                        startIdx += 2;
                    }
                }
            }
            if (relativePathStr.startsWith("//", startIdx)) {
                startIdx += 2;
            }
        } else if (WINDOWS && (relativePathStr.startsWith("//") || relativePathStr.startsWith("\\\\"))) {
            // Windows UNC path
            startIdx += 2;
            prefix = "//";
            isAbsolutePath = true;
        }
        // Handle Windows paths starting with a drive designation as an absolute path
        if (WINDOWS) {
            if (relativePathStr.length() - startIdx > 2 && Character.isLetter(relativePathStr.charAt(startIdx))
                    && relativePathStr.charAt(startIdx + 1) == ':') {
                isAbsolutePath = true;
            } else if (relativePathStr.length() - startIdx > 3
                    && (relativePathStr.charAt(startIdx) == '/' || relativePathStr.charAt(startIdx) == '\\')
                    && Character.isLetter(relativePathStr.charAt(startIdx + 1))
                    && relativePathStr.charAt(startIdx + 2) == ':') {
                isAbsolutePath = true;
                startIdx++;
            }
        }
        // Catch-all for paths starting with separator
        if (relativePathStr.length() - startIdx > 1
                && (relativePathStr.charAt(startIdx) == '/' || relativePathStr.charAt(startIdx) == '\\')) {
            isAbsolutePath = true;
        }

        // Normalize the path, then add any UNC prefix
        String pathStr = normalizePath(startIdx == 0 ? relativePathStr : relativePathStr.substring(startIdx));
        if (!prefix.isEmpty()) {
            pathStr = prefix + pathStr;
        }

        if (resolveBasePath == null || isAbsolutePath) {
            // There is no base path to resolve against, or path is an absolute path => ignore the base path
            return pathStr;
        } else {
            // Path is a relative path -- resolve it relative to the base path
            return resolveBasePath + "/" + pathStr;
        }
    }

    /**
     * Strip away any "jar:" prefix from a filename URI, and convert it to a file path, handling possibly-broken
     * mixes of filesystem and URI conventions. Returns null if relativePathStr is an "http(s):" path.
     */
    public static String resolve(final String pathStr) {
        return resolve(null, pathStr);
    }
}
