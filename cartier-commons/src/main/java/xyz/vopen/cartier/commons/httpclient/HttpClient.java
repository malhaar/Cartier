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

/**
 * http client
 */
public class HttpClient extends AbstractHttpClient {

    private HttpClient () {
    }

    public static HttpClient getInstance () {
        return SingletonHolder.INSTANCE;
    }

    /**
     * request请求
     *
     * @param method
     * @param url
     *         请求地址
     * @param options
     *         option
     * @param response
     *         响应
     */
    public void request (METHOD method, String url, HttpOptions options, HttpResponse response) {
        request(method, url, (HttpParams) null, options, response);
    }

    /**
     * request请求
     *
     * @param method
     * @param url
     *         请求地址
     * @param params
     *         参数
     * @param options
     *         option
     * @param response
     *         响应
     */
    public void request (METHOD method, String url, HttpParams params, HttpOptions options,
                         HttpResponse response) {
        if (method == METHOD.GET) {
            get(url, params, options, response);
        }

        if (method == METHOD.POST) {
            post(url, params, options, response);
        }
    }

    /**
     * 下载文件(支持 GET 方式 )
     *
     * @param url
     *         请求地址
     * @param params
     *         参数
     * @param options
     *         option
     * @param response
     *         响应
     */
    public void download (String url, HttpParams params, HttpOptions options,
                          HttpResponse response) {
        file(url, params, options, response);
    }

    private static class SingletonHolder {
        private static final HttpClient INSTANCE = new HttpClient();
    }
}
