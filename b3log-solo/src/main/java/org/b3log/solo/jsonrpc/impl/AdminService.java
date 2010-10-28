/*
 * Copyright (C) 2009, 2010, B3log Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.b3log.solo.jsonrpc.impl;

import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.latke.action.AbstractCacheablePageAction;
import org.b3log.latke.action.ActionException;
import org.b3log.solo.jsonrpc.AbstractGAEJSONRpcService;
import org.b3log.solo.model.Cache;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Administrator service for JavaScript client.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.4, Oct 27, 2010
 */
public final class AdminService extends AbstractGAEJSONRpcService {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(AdminService.class.getName());
    /**
     * User service.
     */
    private com.google.appengine.api.users.UserService userService =
            UserServiceFactory.getUserService();

    /**
     * Determines whether the administrator is logged in.
     * 
     * @return {@code true} if logged in, returns {@code false} otherwise
     */
    public boolean isAdminLoggedIn() {
        return userService.isUserLoggedIn() && userService.isUserAdmin();
    }

    /**
     * Gets the administrator logout.
     *
     * @param request the specified http servlet request
     * @param response the specified http servlet response
     * @return logout URL
     * @throws ActionException action exception
     * @throws IOException io exception
     */
    public String getLogoutURL(final HttpServletRequest request,
                               final HttpServletResponse response)
            throws ActionException, IOException {
        checkAuthorized(request, response);

        return userService.createLogoutURL("/");
    }

    /**
     * Gets the administrator login.
     *
     * @param request the specified http servlet request
     * @param response the specified http servlet response
     * @return login URL
     * @throws ActionException action exception
     * @throws IOException io exception
     */
    public String getLoginURL(final HttpServletRequest request,
                              final HttpServletResponse response)
            throws ActionException, IOException {
        return userService.createLoginURL("/admin-index.do");
    }

    /**
     * Gets page cache states with the specified http servlet request and http
     * servlet response.
     *
     * @param request the specified http servlet request
     * @param response the specified http servlet response
     * @return for example,
     * <pre>
     * {
     *     "cacheCachedCount": long,
     *     "cacheHitCount": long,
     *     "cacheMaxCount": long,
     *     "cacheMissCount": long
     * }
     * </pre>
     * @throws ActionException action exception
     * @throws IOException io exception
     */
    public JSONObject getPageCache(final HttpServletRequest request,
                                   final HttpServletResponse response)
            throws ActionException, IOException {
        checkAuthorized(request, response);

        final long cachedCount =
                AbstractCacheablePageAction.PAGE_CACHE.getCachedCount();
        final long hitCount =
                AbstractCacheablePageAction.PAGE_CACHE.getHitCount();
        final long maxCount =
                AbstractCacheablePageAction.PAGE_CACHE.getMaxCount();
        final long missCount =
                AbstractCacheablePageAction.PAGE_CACHE.getMissCount();
        LOGGER.log(Level.FINE,
                   "Cache[cachedCount={0}, hitCount={1}, maxCount={2}, missCount={3}",
                   new Object[]{cachedCount, hitCount, maxCount, missCount});

        final JSONObject ret = new JSONObject();
        try {
            ret.put(Cache.CACHE_CACHED_COUNT, cachedCount);
            ret.put(Cache.CACHE_HIT_COUNT, hitCount);
            ret.put(Cache.CACHE_MAX_COUNT, maxCount);
            ret.put(Cache.CACHE_MISS_COUNT, missCount);
        } catch (final JSONException e) {
            LOGGER.log(Level.SEVERE, "Get page cache error: {0}", e.getMessage());
            throw new ActionException(e);
        }

        return ret;
    }

    /**
     * Clears a page cache specified by the given cached page key.
     *
     * @param cachedPageKey the given cached page key
     * @param request the specified http servlet request
     * @param response the specified http servlet response
     * @throws ActionException action exception
     * @throws IOException io exception
     */
    public void clearPageCache(final String cachedPageKey,
                               final HttpServletRequest request,
                               final HttpServletResponse response)
            throws ActionException, IOException {
        // FIXME: issue 20
        checkAuthorized(request, response);

        AbstractCacheablePageAction.PAGE_CACHE.remove(cachedPageKey);
    }

    /**
     * Clears all page cache.
     * 
     * @param request the specified http servlet request
     * @param response the specified http servlet response
     * @throws ActionException action exception
     * @throws IOException io exception
     */
    public void clearAllPageCache(final HttpServletRequest request,
                                  final HttpServletResponse response)
            throws ActionException, IOException {
        checkAuthorized(request, response);

        AbstractCacheablePageAction.PAGE_CACHE.removeAll();
    }
}
