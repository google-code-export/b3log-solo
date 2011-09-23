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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
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
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Page Google App Engine repository.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.5, Aug 20, 2011
 */
public final class PageRepositoryImpl extends AbstractRepository
        implements PageRepository {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(PageRepositoryImpl.class.getName());

    @Override
    public JSONObject getByPermalink(final String permalink) {
        final Query query = new Query();
        query.addFilter(Page.PAGE_PERMALINK,
                        FilterOperator.EQUAL, permalink);
        try {
            final JSONObject result = get(query);
            final JSONArray array = result.getJSONArray(Keys.RESULTS);

            if (0 == array.length()) {
                return null;
            }

            return array.getJSONObject(0);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);

            return null;
        }
    }

    @Override
    public int getMaxOrder() throws RepositoryException {
        final Query query = new Query();
        query.addSort(Page.PAGE_ORDER, SortDirection.DESCENDING);
        final JSONObject result = get(query);
        final JSONArray array = result.optJSONArray(Keys.RESULTS);

        if (0 == array.length()) {
            return -1;
        }

        try {
            return array.getJSONObject(0).getInt(Page.PAGE_ORDER);
        } catch (final JSONException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new RepositoryException(e);
        }
    }

    @Override
    public JSONObject getByOrder(final int order) {
        final Query query = new Query();
        query.addFilter(Page.PAGE_ORDER, FilterOperator.EQUAL, order);
        try {
            final JSONObject result = get(query);
            final JSONArray array = result.getJSONArray(Keys.RESULTS);

            if (0 == array.length()) {
                return null;
            }

            return array.getJSONObject(0);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);

            return null;
        }
    }

    @Override
    public List<JSONObject> getPages() throws RepositoryException {
        List<JSONObject> ret = new ArrayList<JSONObject>();
        final Query query = new Query().addSort(
                Page.PAGE_ORDER, SortDirection.ASCENDING);
        final JSONObject result = get(query);

        try {
            ret = CollectionUtils.jsonArrayToList(
                    result.getJSONArray(Keys.RESULTS));
        } catch (final JSONException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new RepositoryException(e);
        }

        return ret;
    }

    /**
     * Gets the {@link PageGAERepository} singleton.
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
