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

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.RunsOnEnv;
import org.b3log.latke.cache.Cache;
import org.b3log.latke.cache.CacheFactory;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.SortDirection;
import org.b3log.latke.repository.gae.AbstractGAERepository;
import org.b3log.solo.model.Page;
import org.b3log.solo.repository.PageRepository;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Page Google App Engine repository.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.4, Jan 20, 2011
 */
public final class PageGAERepository extends AbstractGAERepository
        implements PageRepository {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(PageGAERepository.class.getName());
    /**
     * Cache.
     */
    private static final Cache<String, Object> CACHE;

    static {
        final RunsOnEnv runsOnEnv = Latkes.getRunsOnEnv();
        if (!runsOnEnv.equals(RunsOnEnv.GAE)) {
            throw new RuntimeException(
                    "GAE repository can only runs on Google App Engine, please "
                    + "check your configuration and make sure "
                    + "Latkes.setRunsOnEnv(RunsOnEnv.GAE) was invoked before "
                    + "using GAE repository.");
        }

        CACHE = CacheFactory.getCache("PageGAERepositoryCache");
    }

    @Override
    public String getName() {
        return Page.PAGE;
    }

    @Override
    public JSONObject getByPermalink(final String permalink) {
        final Query query = new Query(getName());
        query.addFilter(Page.PAGE_PERMALINK,
                        Query.FilterOperator.EQUAL, permalink);
        final PreparedQuery preparedQuery = getDatastoreService().prepare(query);
        final Entity entity = preparedQuery.asSingleEntity();
        if (null == entity) {
            return null;
        }

        final Map<String, Object> properties = entity.getProperties();

        return new JSONObject(properties);
    }

    @Override
    public int getMaxOrder() throws RepositoryException {
        final Query query = new Query(getName());
        query.addSort(Page.PAGE_ORDER, Query.SortDirection.DESCENDING);
        final PreparedQuery preparedQuery = getDatastoreService().prepare(query);
        final List<Entity> links =
                preparedQuery.asList(FetchOptions.Builder.withDefaults());
        if (links.isEmpty()) {
            return -1;
        }

        try {
            final Map<String, Object> properties = links.get(0).getProperties();
            return new JSONObject(properties).getInt(Page.PAGE_ORDER);
        } catch (final JSONException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new RepositoryException(e);
        }
    }

    @Override
    public JSONObject getByOrder(final int order) {
        final Query query = new Query(getName());
        query.addFilter(Page.PAGE_ORDER, Query.FilterOperator.EQUAL, order);
        final PreparedQuery preparedQuery = getDatastoreService().prepare(query);
        final Entity entity = preparedQuery.asSingleEntity();
        if (null == entity) {
            return null;
        }

        final Map<String, Object> properties = entity.getProperties();

        return new JSONObject(properties);
    }

    @Override
    public List<JSONObject> getPages() throws RepositoryException {
        final String cacheKey = "[pages]";
        @SuppressWarnings("unchecked")
        List<JSONObject> ret = (List<JSONObject>) CACHE.get(cacheKey);
        if (null != ret) {
            LOGGER.log(Level.FINEST, "Got the pages from cache");
        } else {
            ret = new ArrayList<JSONObject>();
           final org.b3log.latke.repository.Query
                    query = new org.b3log.latke.repository.Query().
                    addSort(Page.PAGE_ORDER, SortDirection.ASCENDING);
            final JSONObject result = get(query);

            try {
                ret = org.b3log.latke.util.CollectionUtils.jsonArrayToList(result.
                        getJSONArray(Keys.RESULTS));
            } catch (final JSONException e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
                throw new RepositoryException(e);
            }

            CACHE.put(cacheKey, ret);

            LOGGER.log(Level.FINEST,
                       "Got the pages, then put it into cache");
        }

        return ret;
    }

    /**
     * Gets the {@link PageGAERepository} singleton.
     *
     * @return the singleton
     */
    public static PageGAERepository getInstance() {
        return SingletonHolder.SINGLETON;
    }

    /**
     * Private default constructor.
     */
    private PageGAERepository() {
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
        private static final PageGAERepository SINGLETON =
                new PageGAERepository();

        /**
         * Private default constructor.
         */
        private SingletonHolder() {
        }
    }
}
