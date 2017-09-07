package xyz.vopen.cartier.commons.httpclient;

import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieIdentityComparator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

/**
 * xyz.vopen.cartier.commons.httpclient
 *
 * @author Elve.xu [xuhw@yyft.com]
 * @version v1.0 - 18/08/2017.
 */
public class ThreadLocalCookieStore implements CookieStore, Serializable {

    private static final ThreadLocal<TreeSet<Cookie>> cookieCached = new ThreadLocal<TreeSet<Cookie>>() {
        @Override
        protected TreeSet<Cookie> initialValue () {
            return new TreeSet<Cookie>(new CookieIdentityComparator());
        }
    };

    public ThreadLocalCookieStore () {
    }

    /**
     * Adds an {@link Cookie HTTP cookie}, replacing any existing equivalent cookies.
     * If the given cookie has already expired it will not be added, but existing
     * values will still be removed.
     *
     * @param cookie
     *         the {@link Cookie cookie} to be added
     *
     * @see #addCookies(Cookie[])
     */
    @Override
    public void addCookie (final Cookie cookie) {
        if (cookie != null) {
            TreeSet<Cookie> cookies = cookieCached.get();
            // first remove any old cookie that is equivalent
            cookies.remove(cookie);
            if (!cookie.isExpired(new Date())) {
                cookies.add(cookie);
            }
        }
    }

    /**
     * Adds an array of {@link Cookie HTTP cookies}. Cookies are added individually and
     * in the given array order. If any of the given cookies has already expired it will
     * not be added, but existing values will still be removed.
     *
     * @param cookies
     *         the {@link Cookie cookies} to be added
     *
     * @see #addCookie(Cookie)
     */
    public void addCookies (final Cookie[] cookies) {
        if (cookies != null) {
            for (final Cookie cooky : cookies) {
                this.addCookie(cooky);
            }
        }
    }

    /**
     * Returns an immutable array of {@link Cookie cookies} that this HTTP
     * state currently contains.
     *
     * @return an array of {@link Cookie cookies}.
     */
    @Override
    public List<Cookie> getCookies () {
        //create defensive copy so it won't be concurrently modified
        TreeSet<Cookie> cookies = cookieCached.get();
        return new ArrayList<Cookie>(cookies);
    }

    /**
     * Removes all of {@link Cookie cookies} in this HTTP state
     * that have expired by the specified {@link java.util.Date date}.
     *
     * @return true if any cookies were purged.
     *
     * @see Cookie#isExpired(Date)
     */
    @Override
    public boolean clearExpired (final Date date) {
        if (date == null) {
            return false;
        }
        boolean removed = false;
        TreeSet<Cookie> cookies = cookieCached.get();
        for (final Iterator<Cookie> it = cookies.iterator(); it.hasNext(); ) {
            if (it.next().isExpired(date)) {
                it.remove();
                removed = true;
            }
        }
        return removed;
    }

    /**
     * Clears all cookies.
     */
    @Override
    public void clear () {
        TreeSet<Cookie> cookies = cookieCached.get();
        cookies.clear();
    }

    @Override
    public String toString () {
        TreeSet<Cookie> cookies = cookieCached.get();
        return cookies.toString();
    }
}
