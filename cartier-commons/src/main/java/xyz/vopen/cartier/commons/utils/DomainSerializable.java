/**
 * Copyright (C) 2010-2013 Xilamuren Group Holding Limited
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
package xyz.vopen.cartier.commons.utils;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;
import java.nio.charset.Charset;


/**
 * Domain Serializable
 */
public abstract class DomainSerializable implements Serializable {

    private static final long serialVersionUID = 3995722594587083725L;

    public static String toJson (final Object obj, boolean prettyFormat) {
        return JSON.toJSONString(obj, prettyFormat);
    }

    public static <T> T fromJson (String json, Class<T> classOfT) {
        return JSON.parseObject(json, classOfT);
    }

    public static byte[] encode (final Object obj) {
        final String json = toJson(obj, false);
        return json.getBytes(Charset.forName("UTF-8"));
    }

    public static <T> T decode (final byte[] data, Class<T> classOfT) {
        final String json = new String(data, Charset.forName("UTF-8"));
        return fromJson(json, classOfT);
    }

    public String toJson () {
        return toJson(false);
    }

    public void print () {
        JSONPretty.printJson(toJson());
    }

    public String toJson (final boolean prettyFormat) {
        return toJson(this, prettyFormat);
    }

    public byte[] encode () {
        final String json = this.toJson();
        if (json != null) {
            return json.getBytes();
        }
        return null;
    }

}
