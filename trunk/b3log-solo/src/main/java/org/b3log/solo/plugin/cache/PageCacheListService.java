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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
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
import org.b3log.solo.model.Link;
import org.b3log.solo.model.Page;
import org.b3log.solo.util.Users;
import static org.b3log.latke.action.AbstractCacheablePageAction.*;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Page cache list service for JavaScript client.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Jun 20, 2011
 */
public final class PageCacheListService extends AbstractGAEJSONRpcService {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(PageCacheListService.class.getName());
    /**
     * User utilities.
     */
    private Users userUtils = Users.getInstance();

    /**
     * Test method.
     */
    public void test() {
        LOGGER.entering(PageCacheListService.class.getName(), "test()");
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

            final List<JSONObject> pages = new ArrayList<JSONObject>();
            final Set<String> keys = PageCaches.getKeys();
            for (final String key : keys) {
                LOGGER.log(Level.FINER, "Cached page[key={0}]", key);
                try {
                    final JSONObject cachedPage = PageCaches.get(key);

                    final JSONObject page = new JSONObject();
                    page.put(Link.LINK, key);
                    page.put(CACHED_TYPE, cachedPage.getString(CACHED_TYPE));
                    page.put(CACHED_TITLE, cachedPage.getString(CACHED_TITLE));

                    pages.add(page);
                } catch (final JSONException ex) {
                    LOGGER.log(Level.SEVERE, "Page cache plug failed", ex);
                }
            }

            Collections.sort(pages, new Comparator<JSONObject>() {

                @Override
                public int compare(final JSONObject page1,
                                   final JSONObject page2) {
                    return page1.optString(CACHED_TYPE).compareTo(page2.
                            optString(CACHED_TYPE));
                }
            });

            final int pageCount =
                    (int) Math.ceil((double) pages.size() / (double) pageSize);
            final JSONObject pagination = new JSONObject();
            ret.put(Pagination.PAGINATION, pagination);
            final List<Integer> pageNums =
                    Paginator.paginate(currentPageNum, pageSize, pageCount,
                                       windowSize);
            pagination.put(Pagination.PAGINATION_PAGE_COUNT, pageCount);
            pagination.put(Pagination.PAGINATION_PAGE_NUMS, pageNums);

            final int start = pageSize * (currentPageNum - 1);
            int end = start + pageSize;
            end = end > pages.size() ? pages.size() : end;
            ret.put(Page.PAGES, pages.subList(start, end));

            ret.put(Keys.STATUS_CODE, true);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new ActionException(e);
        }

        return ret;
    }

    /**
     * Gets the {@link PageCacheListService} singleton.
     *
     * @return the singleton
     */
    public static PageCacheListService getInstance() {
        return SingletonHolder.SINGLETON;
    }

    /**
     * Private default constructor.
     */
    private PageCacheListService() {
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
        private static final PageCacheListService SINGLETON =
                new PageCacheListService();

        /**
         * Private default constructor.
         */
        private SingletonHolder() {
        }
    }
}
