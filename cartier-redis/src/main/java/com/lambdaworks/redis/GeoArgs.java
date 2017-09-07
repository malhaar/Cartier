/*
 * Copyright 2011-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lambdaworks.redis;

import com.lambdaworks.redis.internal.LettuceAssert;
import com.lambdaworks.redis.protocol.CommandArgs;
import com.lambdaworks.redis.protocol.CommandKeyword;

/**
 * Args for {@literal GEORADIUS} and {@literal GEORADIUSBYMEMBER} commands.
 * 
 * @author Mark Paluch
 */
public class GeoArgs {

    private boolean withdistance;
    private boolean withcoordinates;
    private boolean withhash;
    private Long count;
    private Sort sort = Sort.none;

    /**
     * Request distance for results.
     * 
     * @return {@code this}
     */
    public GeoArgs withDistance() {
        withdistance = true;
        return this;
    }

    /**
     * Request coordinates for results.
     * 
     * @return {@code this}
     */
    public GeoArgs withCoordinates() {
        withcoordinates = true;
        return this;
    }

    /**
     * Request geohash for results.
     * 
     * @return {@code this}
     */
    public GeoArgs withHash() {
        withhash = true;
        return this;
    }

    /**
     * Limit results to {@code count} entries.
     * 
     * @param count number greater 0
     * @return {@code this}
     */
    public GeoArgs withCount(long count) {
        LettuceAssert.isTrue(count > 0, "Count must be greater 0");
        this.count = count;
        return this;
    }

    /**
     * 
     * @return {@literal true} if distance is requested.
     */
    public boolean isWithDistance() {
        return withdistance;
    }

    /**
     * 
     * @return {@literal true} if coordinates are requested.
     */
    public boolean isWithCoordinates() {
        return withcoordinates;
    }

    /**
     * 
     * @return {@literal true} if geohash is requested.
     */
    public boolean isWithHash() {
        return withhash;
    }

    /**
     * Sort results ascending.
     * 
     * @return {@code this}
     */
    public GeoArgs asc() {
        return sort(Sort.asc);
    }

    /**
     * Sort results descending.
     * 
     * @return {@code this}
     */
    public GeoArgs desc() {
        return sort(Sort.desc);
    }

    /**
     * Sort results.
     * 
     * @param sort sort order, must not be {@literal null}
     * @return {@code this}
     */
    public GeoArgs sort(Sort sort) {
        LettuceAssert.notNull(sort, "Sort must not be null");

        this.sort = sort;
        return this;
    }

    /**
     * Sort order.
     */
    public enum Sort {
        /**
         * ascending.
         */
        asc,

        /**
         * descending.
         */
        desc,

        /**
         * no sort order.
         */
        none;
    }

    /**
     * Supported geo unit.
     */
    public enum Unit {
        /**
         * meter.
         */
        m,
        /**
         * kilometer.
         */
        km,
        /**
         * feet.
         */
        ft,
        /**
         * mile.
         */
        mi;
    }

    public <K, V> void build(CommandArgs<K, V> args) {
        if (withdistance) {
            args.add("withdist");
        }

        if (withhash) {
            args.add("withhash");
        }

        if (withcoordinates) {
            args.add("withcoord");
        }

        if (sort != null && sort != Sort.none) {
            args.add(sort.name());
        }

        if (count != null) {
            args.add(CommandKeyword.COUNT).add(count);
        }

    }
}
