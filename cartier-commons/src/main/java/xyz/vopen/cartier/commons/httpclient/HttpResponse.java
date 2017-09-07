/**
 * Copyright 2006-2015 pyw.cn
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package xyz.vopen.cartier.commons.httpclient;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * @author Zero.zhao
 */
public class HttpResponse {
    private String encoding = "UTF-8";

    private int statusCode = 0;

    private String result;
    private byte[] resultByte;

    private boolean parseCookie;
    private Map<String, String> cookieMap = Maps.newHashMap();

    private boolean parseHeader;
    private Map<String, String> headerMap = Maps.newHashMap();

    public HttpResponse () {
    }

    /**
     * Create new response instance
     *
     * @param needCookie
     *         is parse response cookie from <code>org.apache.http.HttpResponse</code> default <code>false</code>
     * @param needHeader
     *         is parse response header from <code>org.apache.http.HttpResponse</code> default <code>false</code>
     */
    public HttpResponse (Boolean needCookie, Boolean needHeader) {
        this.parseCookie = needCookie;
        this.parseHeader = needHeader;
    }

    /**
     * 得到字符编码
     *
     * @return {@link String}, 字符编码, 缺省为UTF-8.
     */
    public String getEncoding () {
        return encoding;
    }

    /**
     * 设置字符编码
     *
     * @param encoding
     *         字符编码
     */
    public void setEncoding (String encoding) {
        this.encoding = encoding;
    }

    /**
     * 得到状态码
     *
     * @return HTTP状态码
     */
    public int getStatusCode () {
        return statusCode;
    }

    /**
     * 设置状态码
     *
     * @param statusCode
     */
    protected void setStatusCode (int statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * 得到http结果
     *
     * @return {@link String}, 返回结果
     */
    public String getResult () {
        return result;
    }

    /**
     * 设置结果
     *
     * @param result
     */
    public void setResult (String result) {
        this.result = result;
    }

    public Map<String, String> getCookieMap () {
        return cookieMap;
    }

    public void setCookieMap (Map<String, String> cookieMap) {
        this.cookieMap = cookieMap;
    }

    public boolean isParseCookie () {
        return parseCookie;
    }

    public void setParseCookie (boolean parseCookie) {
        this.parseCookie = parseCookie;
    }

    public boolean isParseHeader () {
        return parseHeader;
    }

    public void setParseHeader (boolean parseHeader) {
        this.parseHeader = parseHeader;
    }

    public Map<String, String> getHeaderMap () {
        return headerMap;
    }

    public void setHeaderMap (Map<String, String> headerMap) {
        this.headerMap = headerMap;
    }

    public byte[] getResultByte () {
        return resultByte;
    }

    public void setResultByte (byte[] resultByte) {
        this.resultByte = resultByte;
    }
}
