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

import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.repository.AbstractRepository;
import org.b3log.latke.repository.FilterOperator;
import org.b3log.latke.repository.Query;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.SortDirection;
import org.b3log.solo.model.Link;
import org.b3log.solo.repository.LinkRepository;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Link repository.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.4, Nov 2, 2011
 * @since 0.3.1
 */
public final class LinkRepositoryImpl extends AbstractRepository
        implements LinkRepository {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(LinkRepositoryImpl.class.getName());

    @Override
    public JSONObject getByAddress(final String address) {
        final Query query = new Query();
        query.addFilter(Link.LINK_ADDRESS, FilterOperator.EQUAL, address);

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
        query.addSort(Link.LINK_ORDER, SortDirection.DESCENDING);

        final JSONObject result = get(query);
        final JSONArray array = result.optJSONArray(Keys.RESULTS);

        if (0 == array.length()) {
            return -1;
        }

        try {
            return array.getJSONObject(0).getInt(Link.LINK_ORDER);
        } catch (final JSONException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new RepositoryException(e);
        }
    }

    @Override
    public JSONObject getByOrder(final int order) {
        final Query query = new Query();
        query.addFilter(Link.LINK_ORDER, FilterOperator.EQUAL, order);

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
    public JSONObject getUpper(final String id) throws RepositoryException {
        try {
            final JSONObject link = get(id);
            if (null == link) {
                return null;
            }

            final Query query = new Query();
            query.addFilter(Link.LINK_ORDER,
                            FilterOperator.LESS_THAN,
                            link.getInt(Link.LINK_ORDER));
            query.setCurrentPageNum(1);
            query.setPageSize(1);

            final JSONObject result = get(query);
            final JSONArray array = result.getJSONArray(Keys.RESULTS);

            if (1 != array.length()) {
                return null;
            }

            return array.getJSONObject(0);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new RepositoryException(e);
        }
    }

    @Override
    public JSONObject getUnder(final String id) throws RepositoryException {
        try {
            final JSONObject link = get(id);
            if (null == link) {
                return null;
            }

            final Query query = new Query();
            query.addFilter(Link.LINK_ORDER,
                            FilterOperator.GREATER_THAN,
                            link.getInt(Link.LINK_ORDER));
            query.setCurrentPageNum(1);
            query.setPageSize(1);

            final JSONObject result = get(query);
            final JSONArray array = result.getJSONArray(Keys.RESULTS);

            if (1 != array.length()) {
                return null;
            }

            return array.getJSONObject(0);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new RepositoryException(e);
        }
    }

    /**
     * Gets the {@link LinkRepositoryImpl} singleton.
     *
     * @return the singleton
     */
    public static LinkRepositoryImpl getInstance() {
        return SingletonHolder.SINGLETON;
    }

    /**
     * Private constructor.
     * 
     * @param name the specified name
     */
    private LinkRepositoryImpl(final String name) {
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
        private static final LinkRepositoryImpl SINGLETON =
                new LinkRepositoryImpl(Link.LINK);

        /**
         * Private default constructor.
         */
        private SingletonHolder() {
        }
    }
}
