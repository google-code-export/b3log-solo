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
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.b3log.solo.model.Article;
import org.b3log.latke.Keys;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.gae.AbstractGAERepository;
import org.b3log.solo.model.Sign;
import org.b3log.solo.repository.ArticleSignRepository;
import org.json.JSONObject;

/**
 * Article-Sign relation Google App Engine repository.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Jan 12, 2011
 */
public final class ArticleSignGAERepository extends AbstractGAERepository
        implements ArticleSignRepository {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(ArticleSignGAERepository.class.getName());

    @Override
    public String getName() {
        return Article.ARTICLE + "_" + Sign.SIGN;
    }

    @Override
    public List<JSONObject> getBySignId(final String signId)
            throws RepositoryException {
        final Query query = new Query(getName());
        query.addFilter(Sign.SIGN + "_" + Keys.OBJECT_ID,
                        Query.FilterOperator.EQUAL, signId);
        query.addSort(Keys.OBJECT_ID, Query.SortDirection.DESCENDING);
        final PreparedQuery preparedQuery = getDatastoreService().prepare(query);

        final List<JSONObject> ret = new ArrayList<JSONObject>();
        for (final Entity entity : preparedQuery.asIterable()) {
            final Map<String, Object> properties = entity.getProperties();
            final JSONObject e = new JSONObject(properties);

            ret.add(e);
        }

        return ret;
    }

    @Override
    public JSONObject getByArticleId(final String articleId)
            throws RepositoryException {
        final Query query = new Query(getName());
        query.addFilter(Article.ARTICLE + "_" + Keys.OBJECT_ID,
                        Query.FilterOperator.EQUAL, articleId);
        final PreparedQuery preparedQuery = getDatastoreService().prepare(query);

        final Entity entity = preparedQuery.asSingleEntity();
        if (null == entity) {
            return null;
        }

        final Map<String, Object> properties = entity.getProperties();

        return new JSONObject(properties);
    }

    /**
     * Gets the {@link ArticleSignGAERepository} singleton.
     *
     * @return the singleton
     */
    public static ArticleSignGAERepository getInstance() {
        return SingletonHolder.SINGLETON;
    }

    /**
     * Private default constructor.
     */
    private ArticleSignGAERepository() {
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
        private static final ArticleSignGAERepository SINGLETON =
                new ArticleSignGAERepository();

        /**
         * Private default constructor.
         */
        private SingletonHolder() {
        }
    }
}