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

import org.apache.http.Header;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;

/**
 * http请求参数
 */
public class HttpOptions {

    /**
     * socket超时时间
     */
    private final int socketTimeout;

    /**
     * 连接超时时间
     */
    private final int connectTimeout;

    /**
     * 请求超时时间
     */
    private final int connectionRequestTimeout;

    /**
     * 头信息
     */
    private final Header[] headers;

    private final CookieStore cookieStore;

    private final CredentialsProvider credentialsProvider;

    private final boolean expectContinueEnabled;

    private HttpOptions (int socketTimeout, int connectTimeout, int connectionRequestTimeout, Header[] headers,
                         CookieStore cookieStore, CredentialsProvider credentialsProvider, boolean expectContinueEnabled) {
        this.socketTimeout = socketTimeout;
        this.connectTimeout = connectTimeout;
        this.connectionRequestTimeout = connectionRequestTimeout;
        this.headers = headers;
        this.cookieStore = cookieStore;
        this.credentialsProvider = credentialsProvider;
        this.expectContinueEnabled = expectContinueEnabled;
    }

    public int getSocketTimeout () {
        return socketTimeout;
    }

    public int getConnectTimeout () {
        return connectTimeout;
    }

    public int getConnectionRequestTimeout () {
        return connectionRequestTimeout;
    }

    public Header[] getHeaders () {
        return headers;
    }

    public CookieStore getCookieStore () {
        return cookieStore;
    }

    public CredentialsProvider getCredentialsProvider () {
        return credentialsProvider;
    }

    public boolean getExpectContinueEnabled () {
        return expectContinueEnabled;
    }

    public static class Builder {

        /**
         * socket超时时间, 缺省5000
         */
        private int socketTimeout = 5000;

        /**
         * 连接超时时间, 缺省5000
         */
        private int connectTimeout = 5000;

        /**
         * 请求超时时间, 缺省5000
         */
        private int connectionRequestTimeout = 5000;

        /**
         * 头信息
         */
        private Header[] headers;

        private CookieStore cookieStore;

        private CredentialsProvider credentialsProvider;

        /**
         * 不等  直接发
         */
        private boolean expectContinueEnabled = false;

        public void setSocketTimeout (int socketTimeout) {
            this.socketTimeout = socketTimeout;
        }

        public void setConnectTimeout (int connectTimeout) {
            this.connectTimeout = connectTimeout;
        }

        public void setConnectionRequestTimeout (int connectionRequestTimeout) {
            this.connectionRequestTimeout = connectionRequestTimeout;
        }

        public void setHeaders (Header[] headers) {
            this.headers = headers;
        }

        public void setCookieStore (CookieStore cookieStore) {
            this.cookieStore = cookieStore;
        }

        public void setCredentialsProvider (CredentialsProvider credentialsProvider) {
            this.credentialsProvider = credentialsProvider;
        }

        public void setExpectContinueEnabled (boolean bool) {
            this.expectContinueEnabled = bool;
        }

        public HttpOptions build () {
            return new HttpOptions(socketTimeout, connectTimeout, connectionRequestTimeout, headers, cookieStore,
                    credentialsProvider, expectContinueEnabled);
        }
    }
}
