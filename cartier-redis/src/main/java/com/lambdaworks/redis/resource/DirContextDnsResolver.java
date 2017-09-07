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
package com.lambdaworks.redis.resource;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;

import com.google.common.net.InetAddresses;
import com.lambdaworks.redis.LettuceStrings;
import com.lambdaworks.redis.internal.LettuceAssert;

/**
 * DNS Resolver based on Java's {@link com.sun.jndi.dns.DnsContextFactory}. This resolver resolves hostnames to IPv4 and IPv6
 * addresses using {@code A}, {@code AAAA} and {@code CNAME} records. Java IP stack preferences are read from system properties
 * and taken into account when resolving names.
 * <p>
 * The default configuration uses system-configured DNS server addresses to perform lookups but server adresses can be specified
 * using {@link #DirContextDnsResolver(Iterable)}. Custom DNS servers can be specified by using
 * {@link #DirContextDnsResolver(String)} or {@link #DirContextDnsResolver(Iterable)}.
 * </p>
 *
 * @author Mark Paluch
 * @since 4.2
 */
public class DirContextDnsResolver implements DnsResolver, Closeable {

    static final String PREFER_IPV4_KEY = "java.net.preferIPv4Stack";
    static final String PREFER_IPV6_KEY = "java.net.preferIPv6Stack";

    private static final String CTX_FACTORY_NAME = "com.sun.jndi.dns.DnsContextFactory";
    private static final String INITIAL_TIMEOUT = "com.sun.jndi.dns.timeout.initial";
    private static final String LOOKUP_RETRIES = "com.sun.jndi.dns.timeout.retries";

    private static final String DEFAULT_INITIAL_TIMEOUT = "1000";
    private static final String DEFAULT_RETRIES = "4";

    private final boolean preferIpv4;
    private final boolean preferIpv6;
    private final Properties properties;
    private final InitialDirContext context;

    /**
     * Creates a new {@link DirContextDnsResolver} using system-configured DNS servers.
     */
    public DirContextDnsResolver() {
        this(new Properties(), new StackPreference());
    }

    /**
     * Creates a new {@link DirContextDnsResolver} using a collection of DNS servers.
     * 
     * @param dnsServer must not be {@literal null} and not empty.
     */
    public DirContextDnsResolver(String dnsServer) {
        this(Collections.singleton(dnsServer));
    }

    /**
     * Creates a new {@link DirContextDnsResolver} using a collection of DNS servers.
     *
     * @param dnsServers must not be {@literal null} and not empty.
     */
    public DirContextDnsResolver(Iterable<String> dnsServers) {
        this(getProperties(dnsServers), new StackPreference());
    }

    /**
     * Creates a new {@link DirContextDnsResolver} for the given stack preference and {@code properties}.
     * 
     * @param preferIpv4 flag to prefer IPv4 over IPv6 address resolution.
     * @param preferIpv6 flag to prefer IPv6 over IPv4 address resolution.
     * @param properties custom properties for creating the context, must not be {@literal null}.
     */
    public DirContextDnsResolver(boolean preferIpv4, boolean preferIpv6, Properties properties) {

        this.preferIpv4 = preferIpv4;
        this.preferIpv6 = preferIpv6;
        this.properties = properties;
        this.context = createContext(properties);
    }

    private DirContextDnsResolver(Properties properties, StackPreference stackPreference) {

        this.properties = new Properties(properties);
        this.preferIpv4 = stackPreference.preferIpv4;
        this.preferIpv6 = stackPreference.preferIpv6;
        this.context = createContext(properties);
    }

    private InitialDirContext createContext(Properties properties) {

        LettuceAssert.notNull(properties, "Properties must not be null");

        Properties hashtable = (Properties) properties.clone();
        hashtable.put(InitialContext.INITIAL_CONTEXT_FACTORY, CTX_FACTORY_NAME);

        if (!hashtable.containsKey(INITIAL_TIMEOUT)) {
            hashtable.put(INITIAL_TIMEOUT, DEFAULT_INITIAL_TIMEOUT);
        }

        if (!hashtable.containsKey(LOOKUP_RETRIES)) {
            hashtable.put(LOOKUP_RETRIES, DEFAULT_RETRIES);
        }

        try {
            return new InitialDirContext(hashtable);
        } catch (NamingException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void close() throws IOException {
        try {
            context.close();
        } catch (NamingException e) {
            throw new IOException(e);
        }
    }

    /**
     * Perform hostname to address resolution.
     * 
     * @param host the hostname, must not be empty or {@literal null}.
     * @return array of one or more {@link InetAddress adresses}
     * @throws UnknownHostException
     */
    @Override
    public InetAddress[] resolve(String host) throws UnknownHostException {

        if (InetAddresses.isInetAddress(host)) {
            return new InetAddress[] { InetAddresses.forString(host) };
        }

        List<InetAddress> inetAddresses = new ArrayList<>();
        try {
            resolve(host, inetAddresses);
        } catch (NamingException e) {
            throw new UnknownHostException(String.format("Cannot resolve %s to a hostname because of %s", host, e));
        }

        if (inetAddresses.isEmpty()) {
            throw new UnknownHostException(String.format("Cannot resolve %s to a hostname", host));
        }

        return inetAddresses.toArray(new InetAddress[inetAddresses.size()]);
    }

    /**
     * Resolve a hostname
     * 
     * @param hostname
     * @param inetAddresses
     * @throws NamingException
     * @throws UnknownHostException
     */
    private void resolve(String hostname, List<InetAddress> inetAddresses) throws NamingException, UnknownHostException {

        if (preferIpv6 || (!preferIpv4 && !preferIpv6)) {

            inetAddresses.addAll(resolve(hostname, "AAAA"));
            inetAddresses.addAll(resolve(hostname, "A"));
        } else {

            inetAddresses.addAll(resolve(hostname, "A"));
            inetAddresses.addAll(resolve(hostname, "AAAA"));
        }

        if (inetAddresses.isEmpty()) {
            inetAddresses.addAll(resolveCname(hostname));
        }
    }

    /**
     * Resolves {@code CNAME} records to {@link InetAddress adresses}.
     * 
     * @param hostname
     * @return
     * @throws NamingException
     */
    @SuppressWarnings("rawtypes")
    private List<InetAddress> resolveCname(String hostname) throws NamingException {

        List<InetAddress> inetAddresses = new ArrayList<>();

        Attributes attrs = context.getAttributes(hostname, new String[] { "CNAME" });
        Attribute attr = attrs.get("CNAME");

        if (attr != null && attr.size() > 0) {
            NamingEnumeration e = attr.getAll();

            while (e.hasMore()) {
                String h = (String) e.next();

                if (h.endsWith(".")) {
                    h = h.substring(0, h.lastIndexOf('.'));
                }
                try {
                    InetAddress[] resolved = resolve(h);
                    for (InetAddress inetAddress : resolved) {
                        inetAddresses.add(InetAddress.getByAddress(hostname, inetAddress.getAddress()));
                    }

                } catch (UnknownHostException e1) {
                    // ignore
                }
            }
        }

        return inetAddresses;
    }

    /**
     * Resolve an attribute for a hostname.
     * 
     * @param hostname
     * @param attrName
     * @return
     * @throws NamingException
     * @throws UnknownHostException
     */
    @SuppressWarnings("rawtypes")
    private List<InetAddress> resolve(String hostname, String attrName) throws NamingException, UnknownHostException {

        Attributes attrs = context.getAttributes(hostname, new String[] { attrName });

        List<InetAddress> inetAddresses = new ArrayList<>();
        Attribute attr = attrs.get(attrName);

        if (attr != null && attr.size() > 0) {
            NamingEnumeration e = attr.getAll();

            while (e.hasMore()) {
                InetAddress inetAddress = InetAddress.getByName("" + e.next());
                inetAddresses.add(InetAddress.getByAddress(hostname, inetAddress.getAddress()));
            }
        }

        return inetAddresses;
    }

    private static Properties getProperties(Iterable<String> dnsServers) {

        Properties properties = new Properties();
        StringBuffer providerUrl = new StringBuffer();

        for (String dnsServer : dnsServers) {

            LettuceAssert.isTrue(LettuceStrings.isNotEmpty(dnsServer), "DNS Server must not be empty");
            if (providerUrl.length() != 0) {
                providerUrl.append(' ');
            }
            providerUrl.append(String.format("dns://%s", dnsServer));
        }

        if (providerUrl.length() == 0) {
            throw new IllegalArgumentException("DNS Servers must not be empty");
        }

        properties.put(Context.PROVIDER_URL, providerUrl.toString());

        return properties;
    }

    /**
     * Stack preference utility.
     */
    private static final class StackPreference {

        final boolean preferIpv4;
        final boolean preferIpv6;

        public StackPreference() {

            boolean preferIpv4 = false;
            boolean preferIpv6 = false;

            if (System.getProperty(PREFER_IPV4_KEY) == null && System.getProperty(PREFER_IPV6_KEY) == null) {
                preferIpv4 = false;
                preferIpv6 = false;
            }

            if (System.getProperty(PREFER_IPV4_KEY) == null && System.getProperty(PREFER_IPV6_KEY) != null) {

                preferIpv6 = Boolean.getBoolean(PREFER_IPV6_KEY);
                if (!preferIpv6) {
                    preferIpv4 = true;
                }
            }

            if (System.getProperty(PREFER_IPV4_KEY) != null && System.getProperty(PREFER_IPV6_KEY) == null) {

                preferIpv4 = Boolean.getBoolean(PREFER_IPV4_KEY);
                if (!preferIpv4) {
                    preferIpv6 = true;
                }
            }

            if (System.getProperty(PREFER_IPV4_KEY) != null && System.getProperty(PREFER_IPV6_KEY) != null) {

                preferIpv4 = Boolean.getBoolean(PREFER_IPV4_KEY);
                preferIpv6 = Boolean.getBoolean(PREFER_IPV6_KEY);
            }

            this.preferIpv4 = preferIpv4;
            this.preferIpv6 = preferIpv6;
        }
    }
}
