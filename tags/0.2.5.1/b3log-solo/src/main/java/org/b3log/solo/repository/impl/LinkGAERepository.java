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
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.gae.AbstractGAERepository;
import org.b3log.solo.model.Link;
import org.b3log.solo.repository.LinkRepository;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Link Google App Engine repository.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.3, Jan 12, 2011
 */
public final class LinkGAERepository extends AbstractGAERepository
        implements LinkRepository {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(LinkGAERepository.class.getName());

    @Override
    public String getName() {
        return Link.LINK;
    }

    @Override
    public JSONObject getByAddress(final String address) {
        final Query query = new Query(getName());
        query.addFilter(Link.LINK_ADDRESS, Query.FilterOperator.EQUAL, address);
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
        query.addSort(Link.LINK_ORDER, Query.SortDirection.DESCENDING);
        final PreparedQuery preparedQuery = getDatastoreService().prepare(query);
        final List<Entity> links =
                preparedQuery.asList(FetchOptions.Builder.withDefaults());
        if (links.isEmpty()) {
            return -1;
        }

        try {
            final Map<String, Object> properties = links.get(0).getProperties();
            return new JSONObject(properties).getInt(Link.LINK_ORDER);
        } catch (final JSONException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new RepositoryException(e);
        }
    }

    @Override
    public JSONObject getByOrder(final int order) {
        final Query query = new Query(getName());
        query.addFilter(Link.LINK_ORDER, Query.FilterOperator.EQUAL, order);
        final PreparedQuery preparedQuery = getDatastoreService().prepare(query);
        final Entity entity = preparedQuery.asSingleEntity();
        if (null == entity) {
            return null;
        }

        final Map<String, Object> properties = entity.getProperties();

        return new JSONObject(properties);
    }

    /**
     * Gets the {@link LinkGAERepository} singleton.
     *
     * @return the singleton
     */
    public static LinkGAERepository getInstance() {
        return SingletonHolder.SINGLETON;
    }

    /**
     * Private default constructor.
     */
    private LinkGAERepository() {
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
        private static final LinkGAERepository SINGLETON =
                new LinkGAERepository();

        /**
         * Private default constructor.
         */
        private SingletonHolder() {
        }
    }
}
