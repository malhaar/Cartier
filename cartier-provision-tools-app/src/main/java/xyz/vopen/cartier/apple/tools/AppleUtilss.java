package xyz.vopen.cartier.apple.tools;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class AppleUtilss {


    private static Pattern pattern = Pattern.compile("(?s)(iPhone\\s(Distribution|Developer).+?(?=\\()(\\(.+?(?=\\))\\)))");

    /**
     * 解析开发者信息
     *
     * @param content
     *
     * @return
     */
    public static String parseDeveloper (String content) {
        try {
            int dictIndex = content.indexOf("<dict>");
            if (dictIndex > 0) {
                content = content.substring(dictIndex);
                dictIndex = content.lastIndexOf("</dict>");
                if (dictIndex > 0) {
                    content = content.substring(0, dictIndex + "</dict>".length());
                    int dataIndex = content.indexOf("<data>");
                    if (dataIndex > 0) {
                        content = content.substring(dataIndex);
                        dataIndex = content.lastIndexOf("</data>");
                        if (dataIndex > 0) {
                            content = content.substring("</data>".length() - 1, dataIndex);
                            String temp = new String(Base64.decodeBase64(content), "UTF-8");
                            Matcher matcher = pattern.matcher(temp);
                            if (matcher.find()) {
                                return matcher.group(0);
                            }

                        }
                    }
                }
            }
        } catch (Exception ignored) {
            throw new RuntimeException("");
        }
        return null;
    }

    public static String parseDeveloper (File file) throws Exception {
        return parseDeveloper(FileUtils.readFileToString(file, "UTF-8"));
    }


    public static String nextAppIdName () {
        return toSerialCode(System.nanoTime()).toUpperCase();
    }


    public static String nextMobileProvisionName () {
        return toSerialCode(System.nanoTime()).toUpperCase();
    }


    /**
     * 自定义进制(0,1没有加入,容易与o,l混淆)
     */
    private static final char[] r = new char[]{ 'q', 'w', 'e', '8', 'a', 's', '2', 'd', 'z', 'x', '9', 'c', '7', 'p', '5', 'i', 'k', '3', 'm', 'j', 'u', 'f', 'r', '4', 'v', 'y', 'l', 't', 'n', '6', 'b', 'g', 'h' };

    /**
     * 增强型混淆
     */
    private static final char[] r3 = new char[]{ 'b', 'f', 'd', 'b', 'a', '1', '2', 'f', '3', 'a', '2', '6', '9', '8', '5', 'd', '3', '9', '8', '3', '7', 'c', 'c', 'a', '8', 'b', 'd', 'f', '3', 'd', 'c', 'd', 'c', '7', '5', '5', 'f', '1', '5', '2' };
    private static final char b = 'o';
    private static final int binLen = r.length;
    private static final int binLen3 = r3.length;
    private static final int s = 6;

    private static String toSerialCode (long id) {
        char[] buf = new char[32];
        int charPos = 32;

        while ((id / binLen) > 0) {
            int ind = (int) (id % binLen);
            buf[--charPos] = r[ind];
            id /= binLen;
        }
        buf[--charPos] = r[(int) (id % binLen)];
        String str = new String(buf, charPos, (32 - charPos));
        if (str.length() < s) {
            StringBuilder sb = new StringBuilder();
            sb.append(b);
            Random rnd = new Random();
            for (int i = 1; i < s - str.length(); i++) {
                sb.append(r[rnd.nextInt(binLen)]);
            }
            str += sb.toString();
        }
        return str;
    }

    private static long codeToId (String code) {
        char chs[] = code.toCharArray();
        long res = 0L;
        for (int i = 0; i < chs.length; i++) {
            int ind = 0;
            for (int j = 0; j < binLen; j++) {
                if (chs[i] == r[j]) {
                    ind = j;
                    break;
                }
            }
            if (chs[i] == b) {
                break;
            }
            if (i > 0) {
                res = res * binLen + ind;
            } else {
                res = ind;
            }
        }
        return res;
    }

    static String toSerialKey () {
        StringBuilder key1 = new StringBuilder();
        for (int i = binLen3 - 1; i >= 0; --i) {
            // TODO 混淆
            key1.append(r3[i]);
        }
        return key1.toString();
    }

}

