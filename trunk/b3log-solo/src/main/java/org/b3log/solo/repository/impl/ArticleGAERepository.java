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

import com.google.appengine.api.datastore.QueryResultIterable;
import java.util.ArrayList;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.QueryResultList;
import static com.google.appengine.api.datastore.FetchOptions.Builder.*;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.solo.model.Article;
import org.b3log.solo.repository.ArticleRepository;
import org.b3log.latke.Keys;
import org.b3log.latke.model.Pagination;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.gae.AbstractGAERepository;
import org.b3log.latke.util.CollectionUtils;
import org.b3log.solo.model.BlogSync;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Article Google App Engine repository.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.2.2, Dec 22, 2010
 */
public final class ArticleGAERepository extends AbstractGAERepository
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
    public JSONObject getByAuthorEmail(final String authorEmail,
                                       final int currentPageNum,
                                       final int pageSize)
            throws RepositoryException {
        final Query query = new Query(getName());
        query.addFilter(Article.ARTICLE_AUTHOR_EMAIL,
                        Query.FilterOperator.EQUAL, authorEmail);
        query.addSort(Article.ARTICLE_UPDATE_DATE,
                      Query.SortDirection.ASCENDING);

        final PreparedQuery preparedQuery = getDatastoreService().prepare(query);
        final int count = preparedQuery.countEntities(
                FetchOptions.Builder.withDefaults());
        final int pageCount =
                (int) Math.ceil((double) count / (double) pageSize);

        final JSONObject ret = new JSONObject();
        final JSONObject pagination = new JSONObject();
        try {
            ret.put(Pagination.PAGINATION, pagination);
            pagination.put(Pagination.PAGINATION_PAGE_COUNT, pageCount);
            final int offset = pageSize * (currentPageNum - 1);
            final QueryResultList<Entity> queryResultList =
                    preparedQuery.asQueryResultList(
                    withOffset(offset).limit(pageSize));
            final JSONArray results = new JSONArray();
            ret.put(Keys.RESULTS, results);
            for (final Entity entity : queryResultList) {
                final JSONObject jsonObject = entity2JSONObject(entity);

                results.put(jsonObject);
            }
        } catch (final JSONException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new RepositoryException(e);
        }

        return ret;
    }

    @Override
    public JSONObject getByPermalink(final String permalink) {
        final Query query = new Query(getName());
        query.addFilter(Article.ARTICLE_PERMALINK,
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
    public List<JSONObject> getRecentArticles(final int fetchSize) {
        final Query query = new Query(getName());
        query.addSort(Article.ARTICLE_UPDATE_DATE,
                      Query.SortDirection.DESCENDING);
        final PreparedQuery preparedQuery = getDatastoreService().prepare(query);
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
        query.addFilter(Article.ARTICLE_IS_PUBLISHED,
                        Query.FilterOperator.EQUAL, true);
        final PreparedQuery preparedQuery = getDatastoreService().prepare(query);
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
        query.addFilter(Article.ARTICLE_IS_PUBLISHED,
                        Query.FilterOperator.EQUAL, true);
        final PreparedQuery preparedQuery = getDatastoreService().prepare(query);
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
        final PreparedQuery preparedQuery = getDatastoreService().prepare(query);
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
        final PreparedQuery preparedQuery = getDatastoreService().prepare(query);
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
        final PreparedQuery preparedQuery = getDatastoreService().prepare(query);
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
        final PreparedQuery preparedQuery = getDatastoreService().prepare(query);
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

            // Remove external attributes, such as "blogSyncExternal...."
            article.remove(BlogSync.BLOG_SYNC_EXTERNAL_ARTICLE_ABSTRACT);
            article.remove(BlogSync.BLOG_SYNC_EXTERNAL_ARTICLE_CATEGORIES);
            article.remove(BlogSync.BLOG_SYNC_EXTERNAL_ARTICLE_CONTENT);
            article.remove(BlogSync.BLOG_SYNC_EXTERNAL_ARTICLE_CREATE_DATE);
            article.remove(BlogSync.BLOG_SYNC_EXTERNAL_ARTICLE_ID);
            article.remove(BlogSync.BLOG_SYNC_EXTERNAL_ARTICLE_TITLE);

            super.add(article);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new RepositoryException(e);
        }

        LOGGER.log(Level.FINER, "Imported an article[oId={0}]", articleId);
    }

    @Override
    public boolean isPublished(final String articleId)
            throws RepositoryException {
        final JSONObject article = get(articleId);
        if (null == article) {
            return false;
        } else {
            try {
                return article.getBoolean(Article.ARTICLE_IS_PUBLISHED);
            } catch (final JSONException e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);

                throw new RepositoryException(e);
            }
        }
    }

    @Override
    public List<JSONObject> getRandomly(final int fetchSize)
            throws RepositoryException {
        final List<JSONObject> ret = new ArrayList<JSONObject>();
        final Query query = new Query(getName());
        final PreparedQuery preparedQuery = getDatastoreService().prepare(query);
        final int count = preparedQuery.countEntities(
                FetchOptions.Builder.withDefaults());

        if (0 == count) {
            return ret;
        }

        final Iterable<Entity> entities = preparedQuery.asIterable();
        List<Integer> fetchIndexes = Collections.emptyList();
        try {
            fetchIndexes = CollectionUtils.getRandomIntegers(0, count - 1,
                                                             fetchSize);
        } catch (final IllegalArgumentException e) {
            LOGGER.warning(e.getMessage());
        }

        int index = 0;
        for (final Entity entity : entities) { // XXX: performance issue
            if (fetchIndexes.contains(index)) {
                if ((Boolean) entity.getProperty(Article.ARTICLE_IS_PUBLISHED)) {
                    final JSONObject jsonObject = entity2JSONObject(entity);
                    ret.add(jsonObject);
                }
            }

            index++;
        }

        return ret;
    }
}
