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
package org.b3log.solo.repository.impl;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.QueryResultIterable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.solo.model.Article;
import org.b3log.solo.repository.ArticleRepository;
import org.b3log.latke.Keys;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.gae.AbstractGAERepository;
import org.b3log.solo.model.BlogSync;
import org.json.JSONObject;

/**
 * Article Google App Engine repository.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.1.3, Oct 11, 2010
 */
public class ArticleGAERepository extends AbstractGAERepository
        implements ArticleRepository {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(ArticleGAERepository.class.getName());

    @Override
    public String getName() {
        return Article.ARTICLE;
    }

    @Override
    public List<JSONObject> getRecentArticles(final int fetchSize) {
        final Query query = new Query(getName());
        query.addSort(Article.ARTICLE_UPDATE_DATE,
                      Query.SortDirection.DESCENDING);
        final PreparedQuery preparedQuery = DATASTORE_SERVICE.prepare(query);
        final QueryResultIterable<Entity> queryResultIterable =
                preparedQuery.asQueryResultIterable(FetchOptions.Builder.
                withLimit(fetchSize));

        final List<JSONObject> ret = new ArrayList<JSONObject>();
        for (final Entity entity : queryResultIterable) {
            final JSONObject article = entity2JSONObject(entity);
            ret.add(article);
        }

        return ret;
    }

    @Override
    public List<JSONObject> getMostCommentArticles(final int num) {
        final Query query = new Query(getName());
        query.addSort(Article.ARTICLE_COMMENT_COUNT,
                      Query.SortDirection.DESCENDING).
                addSort(Article.ARTICLE_UPDATE_DATE,
                        Query.SortDirection.DESCENDING);
        final PreparedQuery preparedQuery = DATASTORE_SERVICE.prepare(query);
        final QueryResultIterable<Entity> queryResultIterable =
                preparedQuery.asQueryResultIterable(FetchOptions.Builder.
                withLimit(num));

        final List<JSONObject> ret = new ArrayList<JSONObject>();
        for (final Entity entity : queryResultIterable) {
            final JSONObject article = entity2JSONObject(entity);
            ret.add(article);
        }

        return ret;
    }

    @Override
    public List<JSONObject> getMostViewCountArticles(final int num) {
        final Query query = new Query(getName());
        query.addSort(Article.ARTICLE_VIEW_COUNT,
                      Query.SortDirection.DESCENDING).
                addSort(Article.ARTICLE_UPDATE_DATE,
                        Query.SortDirection.DESCENDING);
        final PreparedQuery preparedQuery = DATASTORE_SERVICE.prepare(query);
        final QueryResultIterable<Entity> queryResultIterable =
                preparedQuery.asQueryResultIterable(FetchOptions.Builder.
                withLimit(num));

        final List<JSONObject> ret = new ArrayList<JSONObject>();
        for (final Entity entity : queryResultIterable) {
            final JSONObject article = entity2JSONObject(entity);
            ret.add(article);
        }

        return ret;
    }

    @Override
    public String getPreviousArticleId(final String articleId) {
        final Query query = new Query(getName());
        query.addFilter(Keys.OBJECT_ID,
                        Query.FilterOperator.LESS_THAN, articleId);
        query.addSort(Keys.OBJECT_ID, Query.SortDirection.DESCENDING);
        final PreparedQuery preparedQuery = DATASTORE_SERVICE.prepare(query);
        final List<Entity> result =
                preparedQuery.asList(FetchOptions.Builder.withLimit(1));

        if (1 == result.size()) {
            final JSONObject previous = entity2JSONObject(result.get(0));
            return previous.optString(Keys.OBJECT_ID);
        }

        return null;
    }

    @Override
    public String getNextArticleId(final String articleId) {
        final Query query = new Query(getName());
        query.addFilter(Keys.OBJECT_ID,
                        Query.FilterOperator.GREATER_THAN, articleId);
        final PreparedQuery preparedQuery = DATASTORE_SERVICE.prepare(query);
        final List<Entity> result =
                preparedQuery.asList(FetchOptions.Builder.withLimit(1));

        if (1 == result.size()) {
            final JSONObject previous = entity2JSONObject(result.get(0));
            return previous.optString(Keys.OBJECT_ID);
        }

        return null;
    }

    @Override
    public JSONObject getPreviousArticle(final String articleId) {
        final Query query = new Query(getName());
        query.addFilter(Keys.OBJECT_ID,
                        Query.FilterOperator.LESS_THAN, articleId);
        query.addSort(Keys.OBJECT_ID, Query.SortDirection.DESCENDING);
        final PreparedQuery preparedQuery = DATASTORE_SERVICE.prepare(query);
        final List<Entity> result =
                preparedQuery.asList(FetchOptions.Builder.withLimit(1));

        if (1 == result.size()) {
            return entity2JSONObject(result.get(0));
        }

        return null;
    }

    @Override
    public JSONObject getNextArticle(final String articleId) {
        final Query query = new Query(getName());
        query.addFilter(Keys.OBJECT_ID,
                        Query.FilterOperator.GREATER_THAN, articleId);
        final PreparedQuery preparedQuery = DATASTORE_SERVICE.prepare(query);
        final List<Entity> result =
                preparedQuery.asList(FetchOptions.Builder.withLimit(1));

        if (1 == result.size()) {
            return entity2JSONObject(result.get(0));
        }

        return null;
    }

    @Override
    public void importArticle(final JSONObject article)
            throws RepositoryException {
        String articleId = null;
        try {
            if (!article.has(Keys.OBJECT_ID)) {
                throw new RepositoryException("The article to import MUST exist "
                                              + "id");
            }
            articleId = article.getString(Keys.OBJECT_ID);

            if (!article.has(Article.ARTICLE_CREATE_DATE)) {
                throw new RepositoryException("The article to import MUST exist "
                                              + "create date");
            }

            // XXX:  check other params

            if (!article.has(Article.ARTICLE_UPDATE_DATE)
                || null == article.get(Article.ARTICLE_UPDATE_DATE)) {
                article.put(Article.ARTICLE_UPDATE_DATE,
                            article.get(Article.ARTICLE_CREATE_DATE));
            }

            // Remove external attributes, such as "blogSyncExternal...."
            article.remove(BlogSync.BLOG_SYNC_EXTERNAL_ARTICLE_ABSTRACT);
            article.remove(BlogSync.BLOG_SYNC_EXTERNAL_ARTICLE_CATEGORIES);
            article.remove(BlogSync.BLOG_SYNC_EXTERNAL_ARTICLE_CONTENT);
            article.remove(BlogSync.BLOG_SYNC_EXTERNAL_ARTICLE_CREATE_DATE);
            article.remove(BlogSync.BLOG_SYNC_EXTERNAL_ARTICLE_ID);
            article.remove(BlogSync.BLOG_SYNC_EXTERNAL_ARTICLE_TITLE);

            super.add(article);
        } catch (final Exception e) {
            LOGGER.severe(e.getMessage());
            throw new RepositoryException(e);
        }

        LOGGER.log(Level.FINER, "Imported an article[oId={0}]", articleId);
    }
}
