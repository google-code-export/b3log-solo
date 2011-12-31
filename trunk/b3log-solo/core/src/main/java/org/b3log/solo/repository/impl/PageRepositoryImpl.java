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
package org.b3log.solo.repository.impl;

import java.util.List;
import java.util.logging.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.repository.AbstractRepository;
import org.b3log.latke.repository.FilterOperator;
import org.b3log.latke.repository.Query;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.SortDirection;
import org.b3log.latke.util.CollectionUtils;
import org.b3log.solo.model.Page;
import org.b3log.solo.repository.PageRepository;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Page repository.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.9, Dec 31, 2011
 * @since 0.3.1
 */
public final class PageRepositoryImpl extends AbstractRepository
        implements PageRepository {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(PageRepositoryImpl.class.getName());

    @Override
    public JSONObject getByPermalink(final String permalink)
            throws RepositoryException {
        final Query query = new Query().addFilter(Page.PAGE_PERMALINK,
                                                  FilterOperator.EQUAL,
                                                  permalink).
                setPageCount(1);
        final JSONObject result = get(query);
        final JSONArray array = result.optJSONArray(Keys.RESULTS);

        if (0 == array.length()) {
            return null;
        }

        return array.optJSONObject(0);
    }

    @Override
    public int getMaxOrder() throws RepositoryException {
        final Query query = new Query().addSort(Page.PAGE_ORDER,
                                                SortDirection.DESCENDING).
                setPageCount(1);
        final JSONObject result = get(query);
        final JSONArray array = result.optJSONArray(Keys.RESULTS);

        if (0 == array.length()) {
            return -1;
        }

        return array.optJSONObject(0).optInt(Page.PAGE_ORDER);
    }

    @Override
    public JSONObject getUpper(final String id) throws RepositoryException {
        final JSONObject page = get(id);
        if (null == page) {
            return null;
        }

        final Query query = new Query().addFilter(
                Page.PAGE_ORDER, FilterOperator.LESS_THAN,
                page.optInt(Page.PAGE_ORDER)).
                addSort(Page.PAGE_ORDER,
                        SortDirection.DESCENDING).
                setCurrentPageNum(1).
                setPageSize(1).setPageCount(1);

        final JSONObject result = get(query);
        final JSONArray array = result.optJSONArray(Keys.RESULTS);

        if (1 != array.length()) {
            return null;
        }

        return array.optJSONObject(0);
    }

    @Override
    public JSONObject getUnder(final String id) throws RepositoryException {
        final JSONObject page = get(id);
        if (null == page) {
            return null;
        }

        final Query query = new Query().addFilter(
                Page.PAGE_ORDER, FilterOperator.GREATER_THAN,
                page.optInt(Page.PAGE_ORDER)).
                addSort(Page.PAGE_ORDER,
                        SortDirection.ASCENDING).setCurrentPageNum(1).
                setPageSize(1).
                setPageCount(1);

        final JSONObject result = get(query);
        final JSONArray array = result.optJSONArray(Keys.RESULTS);

        if (1 != array.length()) {
            return null;
        }

        return array.optJSONObject(0);
    }

    @Override
    public JSONObject getByOrder(final int order) throws RepositoryException {
        final Query query = new Query().addFilter(Page.PAGE_ORDER,
                                                  FilterOperator.EQUAL, order).
                setPageCount(1);
        final JSONObject result = get(query);
        final JSONArray array = result.optJSONArray(Keys.RESULTS);

        if (0 == array.length()) {
            return null;
        }

        return array.optJSONObject(0);
    }

    @Override
    public List<JSONObject> getPages() throws RepositoryException {
        final Query query = new Query().addSort(
                Page.PAGE_ORDER, SortDirection.ASCENDING).setPageCount(1);
        final JSONObject result = get(query);

        return CollectionUtils.jsonArrayToList(result.optJSONArray(Keys.RESULTS));
    }

    /**
     * Gets the {@link PageRepositoryImpl} singleton.
     *
     * @return the singleton
     */
    public static PageRepositoryImpl getInstance() {
        return SingletonHolder.SINGLETON;
    }

    /**
     * Private constructor.
     * 
     * @param name the specified name
     */
    private PageRepositoryImpl(final String name) {
        super(name);
    }

    /**
     * Singleton holder.
     *
     * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
     * @version 1.0.0.0, Jan 12, 2011
     */
    private static final class SingletonHolder {

        /**
         * Singleton.
         */
        private static final PageRepositoryImpl SINGLETON =
                new PageRepositoryImpl(Page.PAGE);

        /**
         * Private default constructor.
         */
        private SingletonHolder() {
        }
    }
}
