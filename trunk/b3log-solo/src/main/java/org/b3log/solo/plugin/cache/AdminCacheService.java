/*
 * Copyright (c) 2009, 2010, 2011, B3log Team
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
package org.b3log.solo.plugin.cache;

import org.b3log.solo.model.Cache;
import org.b3log.solo.model.Common;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.latke.Keys;
import org.b3log.latke.action.ActionException;
import org.b3log.latke.action.util.PageCaches;
import org.b3log.latke.action.util.Paginator;
import org.b3log.latke.model.Pagination;
import org.b3log.solo.jsonrpc.AbstractGAEJSONRpcService;
import org.b3log.solo.model.Page;
import org.b3log.solo.util.Users;
import static org.b3log.latke.action.AbstractCacheablePageAction.*;
import org.b3log.latke.repository.Transaction;
import org.b3log.solo.model.Preference;
import org.b3log.solo.repository.UserRepository;
import org.b3log.solo.repository.impl.UserGAERepository;
import org.b3log.solo.util.Preferences;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Admin cache service for JavaScript client.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.5, Aug 24, 2011
 */
public final class AdminCacheService extends AbstractGAEJSONRpcService {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(AdminCacheService.class.getName());
    /**
     * User utilities.
     */
    private Users userUtils = Users.getInstance();
    /**
     * Preference utilities.
     */
    private Preferences preferenceUtils = Preferences.getInstance();
    /**
     * User repository.
     */
    private UserRepository userRepository = UserGAERepository.getInstance();

    /**
     * Test method.
     */
    public void test() {
        LOGGER.entering(AdminCacheService.class.getName(), "test()");
    }

    /**
     * Gets page cache status with the specified http servlet request and http
     * servlet response.
     *
     * @param response the specified http servlet response
     * @return for example,
     * <pre>
     * {
     *     "cacheCachedCount": long,
     *     "cacheHitCount": long,
     *     "cachedBytes": long,
     *     "hitBytes": long,
     *     "cacheMissCount": long,
     *     "pageCacheEnabled": boolean,
     *     "pageCachedCnt": int
     * }
     * </pre>
     * @throws ActionException action exception
     * @throws IOException io exception
     */
    public JSONObject getPageCache(final HttpServletResponse response)
            throws ActionException, IOException {
        final JSONObject ret = new JSONObject();
        if (!userUtils.isAdminLoggedIn()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return ret;
        }

        final org.b3log.latke.cache.Cache<String, Object> cache =
                PageCaches.getCache();
        final long cachedCount = cache.getCachedCount();
        final long hitCount = cache.getHitCount();
        final long missCount = cache.getMissCount();
        final long cachedBytes = cache.getCachedBytes();
        final long hitBytes = cache.getHitBytes();

        try {
            ret.put(Cache.CACHE_CACHED_COUNT, cachedCount);
            ret.put(Cache.CACHE_HIT_COUNT, hitCount);
            ret.put(Cache.CACHE_CACHED_BYTES, cachedBytes);
            ret.put(Cache.CACHE_HIT_BYTES, hitBytes);
            ret.put(Cache.CACHE_MISS_COUNT, missCount);

            final JSONObject preference = preferenceUtils.getPreference();
            final boolean pageCacheEnabled =
                    preference.getBoolean(Preference.PAGE_CACHE_ENABLED);
            ret.put(Preference.PAGE_CACHE_ENABLED, pageCacheEnabled);

            ret.put(Common.PAGE_CACHED_CNT, PageCaches.getKeys().size());
        } catch (final JSONException e) {
            LOGGER.log(Level.SEVERE, "Gets page cache status error: {0}",
                       e.getMessage());
            throw new ActionException(e);
        }

        return ret;
    }

    /**
     * Gets page cache list by the specified request json object.
     * 
     * @param requestJSONObject the specified request json object, for example,
     * <pre>
     * {
     *     "paginationCurrentPageNum": 1,
     *     "paginationPageSize": 20,
     *     "paginationWindowSize": 10
     * }, see {@link Pagination} for more details
     * </pre>
     * @param request the specified http servlet request
     * @param response the specified http servlet response
     * @return for example,
     * <pre>
     * {
     *     "pagination": {
     *         "paginationPageCount": 100,
     *         "paginationPageNums": [1, 2, 3, 4, 5]
     *     },
     *     "pages": [{
     *         "link": "",
     *         "cachedType": "",
     *         "cachedTitle": "",
     *      }, ....]
     *     "sc": boolean
     * }
     * </pre>, order by article update date and sticky(put top).
     * @throws ActionException action exception
     * @throws IOException io exception
     * @see Pagination
     */
    public JSONObject getPages(final JSONObject requestJSONObject,
                               final HttpServletRequest request,
                               final HttpServletResponse response)
            throws ActionException, IOException {
        final JSONObject ret = new JSONObject();
        if (!userUtils.isLoggedIn()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return ret;
        }

        try {
            final int currentPageNum = requestJSONObject.getInt(
                    Pagination.PAGINATION_CURRENT_PAGE_NUM);
            final int pageSize = requestJSONObject.getInt(
                    Pagination.PAGINATION_PAGE_SIZE);
            final int windowSize = requestJSONObject.getInt(
                    Pagination.PAGINATION_WINDOW_SIZE);

            PageCaches.syncKeys();

            List<String> keys =
                    new ArrayList<String>(PageCaches.getKeys());
            // Paginates
            final int pageCount =
                    (int) Math.ceil((double) keys.size() / (double) pageSize);
            final JSONObject pagination = new JSONObject();
            ret.put(Pagination.PAGINATION, pagination);
            final List<Integer> pageNums =
                    Paginator.paginate(currentPageNum, pageSize, pageCount,
                                       windowSize);
            pagination.put(Pagination.PAGINATION_PAGE_COUNT, pageCount);
            pagination.put(Pagination.PAGINATION_PAGE_NUMS, pageNums);

            final int start = pageSize * (currentPageNum - 1);
            int end = start + pageSize;
            end = end > keys.size() ? keys.size() : end;

            keys = keys.subList(start, end);

            // Retrives cached pages
            final List<JSONObject> pages = new ArrayList<JSONObject>();
            for (final String key : keys) {
                LOGGER.log(Level.FINER, "Cached page[key={0}]", key);

                final JSONObject cachedPage = PageCaches.get(key, false);

                if (null != cachedPage) {
                    cachedPage.remove(CACHED_CONTENT);
                    pages.add(cachedPage);
                }
            }

            ret.put(Page.PAGES, pages);

            ret.put(Keys.STATUS_CODE, true);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new ActionException(e);
        }

        return ret;
    }

    /**
     * Sets page cache states with the specified http servlet response and 
     * settings.
     *
     * @param response the specified http servlet response
     * @param settings the specified settings, for example,
     * <pre>
     * {
     *     "pageCacheEnabled": boolean,
     * }
     * </pre>
     * @throws ActionException action exception
     * @throws IOException io exception
     */
    public void setPageCache(final HttpServletResponse response,
                             final JSONObject settings)
            throws ActionException, IOException {
        if (!userUtils.isAdminLoggedIn()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);

            return;
        }

        final Transaction transaction = userRepository.beginTransaction();
        try {
            final boolean pageCacheEnabled =
                    settings.getBoolean(Preference.PAGE_CACHE_ENABLED);

            final JSONObject preference = preferenceUtils.getPreference();
            preference.put(Preference.PAGE_CACHE_ENABLED, pageCacheEnabled);

            preferenceUtils.setPreference(preference);

            transaction.commit();
        } catch (final Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            LOGGER.log(Level.SEVERE, "Sets page cache error: {0}",
                       e.getMessage());
            throw new ActionException(e);
        }

        PageCaches.removeAll();
    }

    /**
     * Gets the {@link PageCacheListService} singleton.
     *
     * @return the singleton
     */
    public static AdminCacheService getInstance() {
        return SingletonHolder.SINGLETON;
    }

    /**
     * Private default constructor.
     */
    private AdminCacheService() {
    }

    /**
     * Singleton holder.
     *
     * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
     * @version 1.0.0.0, Jun 20, 2011
     */
    private static final class SingletonHolder {

        /**
         * Singleton.
         */
        private static final AdminCacheService SINGLETON =
                new AdminCacheService();

        /**
         * Private default constructor.
         */
        private SingletonHolder() {
        }
    }
}
