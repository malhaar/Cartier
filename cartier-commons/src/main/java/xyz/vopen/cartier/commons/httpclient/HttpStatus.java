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

public class HttpStatus {
    /**
     * 成功返回
     */
    public static final int SC_OK = 200;

    /**
     * 没有响应内容
     */
    public static final int SC_NO_CONTENT = 204;

    /**
     * 禁止访问
     */
    public static final int SC_FORBIDDEN = 403;

    /**
     * 资源没有找到
     */
    public static final int SC_NOT_FOUND = 404;

    /**
     * 访问错误
     */
    public static final int SC_INTERNAL_SERVER_ERROR = 500;

}
