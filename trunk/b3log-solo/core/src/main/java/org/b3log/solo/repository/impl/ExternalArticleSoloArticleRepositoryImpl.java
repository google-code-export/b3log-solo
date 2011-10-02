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
import org.b3log.solo.model.Article;
import org.b3log.solo.model.BlogSync;
import org.b3log.solo.repository.ExternalArticleSoloArticleRepository;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * External blog article-Solo article repository.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.4, Jan 12, 2011
 * @since 0.3.1
 */
public final class ExternalArticleSoloArticleRepositoryImpl
        extends AbstractRepository
        implements ExternalArticleSoloArticleRepository {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(ExternalArticleSoloArticleRepositoryImpl.class.
            getName());

    @Override
    public String getSoloArticleId(final String externalArticleId,
                                   final String externalBloggingSys)
            throws RepositoryException {
        final Query query = new Query();
        query.addFilter(BlogSync.BLOG_SYNC_EXTERNAL_ARTICLE_ID,
                        FilterOperator.EQUAL,
                        externalArticleId);
        query.addFilter(BlogSync.BLOG_SYNC_EXTERNAL_BLOGGING_SYS,
                        FilterOperator.EQUAL, externalBloggingSys);

        final JSONObject result = get(query);
        final JSONArray array = result.optJSONArray(Keys.RESULTS);

        if (0 == array.length()) {
            return null;
        }

        return array.optJSONObject(0).optString(Article.ARTICLE + "_"
                                                + Keys.OBJECT_ID);
    }

    @Override
    public JSONObject getSoloArticle(final String externalArticleId,
                                     final String externalBloggingSys)
            throws RepositoryException {
        final JSONObject relation =
                getByExternalArticleId(externalArticleId, externalBloggingSys);
        if (null == relation) {
            return null;
        }

        try {
            final JSONObject ret = relation;
            final String articleId = relation.getString(Article.ARTICLE + "_"
                                                        + Keys.OBJECT_ID);
            // replace oId for article
            ret.put(Keys.OBJECT_ID, articleId);
            // remove relation properties
            ret.remove(Article.ARTICLE + "_" + Keys.OBJECT_ID);
            ret.remove(BlogSync.BLOG_SYNC_EXTERNAL_BLOGGING_SYS);
            ret.remove(BlogSync.BLOG_SYNC_EXTERNAL_ARTICLE_ID);

            return ret;
        } catch (final JSONException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public JSONObject getByExternalArticleId(final String externalArticleId,
                                             final String externalBloggingSys)
            throws RepositoryException {
        final Query query = new Query();
        query.addFilter(BlogSync.BLOG_SYNC_EXTERNAL_ARTICLE_ID,
                        FilterOperator.EQUAL,
                        externalArticleId);
        query.addFilter(BlogSync.BLOG_SYNC_EXTERNAL_BLOGGING_SYS,
                        FilterOperator.EQUAL, externalBloggingSys);

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
    public JSONObject getBySoloArticleId(final String soloArticleId,
                                         final String externalBloggingSys)
            throws RepositoryException {
        final Query query = new Query();
        query.addFilter(Article.ARTICLE + "_" + Keys.OBJECT_ID,
                        FilterOperator.EQUAL, soloArticleId);
        query.addFilter(BlogSync.BLOG_SYNC_EXTERNAL_BLOGGING_SYS,
                        FilterOperator.EQUAL, externalBloggingSys);
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

    /**
     * Gets the {@link ExternalArticleSoloArticleGAERepository} singleton.
     *
     * @return the singleton
     */
    public static ExternalArticleSoloArticleRepositoryImpl getInstance() {
        return SingletonHolder.SINGLETON;
    }

    /**
     * Private constructor.
     * 
     * @param name the specified name
     */
    private ExternalArticleSoloArticleRepositoryImpl(final String name) {
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
        private static final ExternalArticleSoloArticleRepositoryImpl SINGLETON =
                new ExternalArticleSoloArticleRepositoryImpl(BlogSync.BLOG_SYNC_EXTERNAL_ARTICLE
                                                             + "_"
                                                             + Article.ARTICLE);

        /**
         * Private default constructor.
         */
        private SingletonHolder() {
        }
    }
}