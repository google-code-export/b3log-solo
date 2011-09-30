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
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.solo.model.Article;
import org.b3log.solo.repository.ArticleRepository;
import org.b3log.latke.Keys;
import org.b3log.latke.repository.AbstractRepository;
import org.b3log.latke.repository.FilterOperator;
import org.b3log.latke.repository.Query;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.SortDirection;
import org.b3log.latke.util.CollectionUtils;
import org.b3log.solo.model.BlogSync;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Article repository.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.3.1, Jul 10, 2011
 * @since 0.3.1
 */
public final class ArticleRepositoryImpl extends AbstractRepository
        implements ArticleRepository {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(ArticleRepositoryImpl.class.getName());

    @Override
    public JSONObject getByAuthorEmail(final String authorEmail,
                                       final int currentPageNum,
                                       final int pageSize)
            throws RepositoryException {
        final Query query = new Query();
        query.addFilter(Article.ARTICLE_AUTHOR_EMAIL,
                        FilterOperator.EQUAL, authorEmail);
        query.addSort(Article.ARTICLE_UPDATE_DATE,
                      SortDirection.DESCENDING);
        query.setCurrentPageNum(currentPageNum);
        query.setPageSize(pageSize);

        return get(query);
    }

    @Override
    public JSONObject getByPermalink(final String permalink) {
        final Query query = new Query();
        query.addFilter(Article.ARTICLE_PERMALINK,
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
        }

        return null;
    }

    @Override
    public List<JSONObject> getRecentArticles(final int fetchSize) {
        final Query query = new Query();
        query.addSort(Article.ARTICLE_UPDATE_DATE,
                      SortDirection.DESCENDING);
        query.setCurrentPageNum(1);
        query.setPageSize(fetchSize);

        try {
            final JSONObject result = get(query);
            final JSONArray array = result.getJSONArray(Keys.RESULTS);

            return CollectionUtils.jsonArrayToList(array);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

        return Collections.emptyList();
    }

    @Override
    public List<JSONObject> getMostCommentArticles(final int num) {
        final Query query = new Query();
        query.addSort(Article.ARTICLE_COMMENT_COUNT,
                      SortDirection.DESCENDING).
                addSort(Article.ARTICLE_UPDATE_DATE,
                        SortDirection.DESCENDING);
        query.addFilter(Article.ARTICLE_IS_PUBLISHED,
                        FilterOperator.EQUAL, true);
        query.setCurrentPageNum(1);
        query.setPageSize(num);

        try {
            final JSONObject result = get(query);
            final JSONArray array = result.getJSONArray(Keys.RESULTS);
            return CollectionUtils.jsonArrayToList(array);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

        return Collections.emptyList();
    }

    @Override
    public List<JSONObject> getMostViewCountArticles(final int num) {
        final Query query = new Query();
        query.addSort(Article.ARTICLE_VIEW_COUNT,
                      SortDirection.DESCENDING).
                addSort(Article.ARTICLE_UPDATE_DATE,
                        SortDirection.DESCENDING);
        query.addFilter(Article.ARTICLE_IS_PUBLISHED,
                        FilterOperator.EQUAL, true);
        query.setCurrentPageNum(1);
        query.setPageSize(num);

        try {
            final JSONObject result = get(query);
            final JSONArray array = result.getJSONArray(Keys.RESULTS);

            return CollectionUtils.jsonArrayToList(array);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

        return Collections.emptyList();
    }

    @Override
    public JSONObject getPreviousArticle(final String articleId)
            throws RepositoryException {
        try {
            final JSONObject current = get(articleId);
            final Date currentDate = (Date) current.get(
                    Article.ARTICLE_CREATE_DATE);

            final Query query = new Query();
            query.addFilter(Article.ARTICLE_CREATE_DATE,
                            FilterOperator.LESS_THAN, currentDate);
            query.addFilter(Article.ARTICLE_IS_PUBLISHED,
                            FilterOperator.EQUAL, true);
            query.addSort(Article.ARTICLE_CREATE_DATE,
                          SortDirection.DESCENDING);
            query.setCurrentPageNum(1);
            query.setPageSize(1);

            final JSONObject result = get(query);
            final JSONArray array = result.getJSONArray(Keys.RESULTS);

            if (1 != array.length()) {
                return null;
            }

            final JSONObject ret = new JSONObject();
            final JSONObject article = array.getJSONObject(0);
            ret.put(Article.ARTICLE_TITLE,
                    article.getString(Article.ARTICLE_TITLE));
            ret.put(Article.ARTICLE_PERMALINK,
                    article.getString(Article.ARTICLE_PERMALINK));

            return ret;
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new RepositoryException(e);
        }
    }

    @Override
    public JSONObject getNextArticle(final String articleId)
            throws RepositoryException {
        try {
            final JSONObject current = get(articleId);
            final Date currentDate = (Date) current.get(
                    Article.ARTICLE_CREATE_DATE);
            final Query query = new Query();
            query.addFilter(Article.ARTICLE_CREATE_DATE,
                            FilterOperator.GREATER_THAN, currentDate);
            query.addFilter(Article.ARTICLE_IS_PUBLISHED,
                            FilterOperator.EQUAL, true);
            query.addSort(Article.ARTICLE_CREATE_DATE,
                          SortDirection.ASCENDING);
            query.setCurrentPageNum(1);
            query.setPageSize(1);

            final JSONObject result = get(query);
            final JSONArray array = result.getJSONArray(Keys.RESULTS);

            if (1 != array.length()) {
                return null;
            }

            final JSONObject ret = new JSONObject();
            final JSONObject article = array.getJSONObject(0);
            ret.put(Article.ARTICLE_TITLE,
                    article.getString(Article.ARTICLE_TITLE));
            ret.put(Article.ARTICLE_PERMALINK,
                    article.getString(Article.ARTICLE_PERMALINK));

            return ret;
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new RepositoryException(e);
        }
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

        if (0 == count()) {
            return ret;
        }

        final double mid = Math.random();
        LOGGER.log(Level.FINEST, "Random mid[{0}]", mid);

        Query query = new Query();
        query.addFilter(Article.ARTICLE_RANDOM_DOUBLE,
                        FilterOperator.GREATER_THAN_OR_EQUAL, mid);
        query.addFilter(Article.ARTICLE_RANDOM_DOUBLE,
                        FilterOperator.LESS_THAN_OR_EQUAL, 1D);
        query.addFilter(Article.ARTICLE_IS_PUBLISHED,
                        FilterOperator.EQUAL, true);
        query.setCurrentPageNum(1);
        query.setPageSize(fetchSize);

        final JSONObject result1 = get(query);
        final JSONArray array1 = result1.optJSONArray(Keys.RESULTS);

        try {
            final List<JSONObject> list1 =
                    CollectionUtils.<JSONObject>jsonArrayToList(array1);
            ret.addAll(list1);

            final int reminingSize = fetchSize - array1.length();
            if (0 != reminingSize) { // Query for remains
                query = new Query();
                query.addFilter(Article.ARTICLE_RANDOM_DOUBLE,
                                FilterOperator.GREATER_THAN_OR_EQUAL, 0D);
                query.addFilter(Article.ARTICLE_RANDOM_DOUBLE,
                                FilterOperator.LESS_THAN_OR_EQUAL, mid);
                query.addFilter(Article.ARTICLE_IS_PUBLISHED,
                                FilterOperator.EQUAL, true);
                query.setCurrentPageNum(1);
                query.setPageSize(reminingSize);

                final JSONObject result2 = get(query);
                final JSONArray array2 = result2.optJSONArray(Keys.RESULTS);

                final List<JSONObject> list2 =
                        CollectionUtils.<JSONObject>jsonArrayToList(array2);
                ret.addAll(list2);
            }
        } catch (final JSONException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

        return ret;
    }

    /**
     * Gets the {@link ArticleGAERepository} singleton.
     *
     * @return the singleton
     */
    public static ArticleRepositoryImpl getInstance() {
        return SingletonHolder.SINGLETON;
    }

    /**
     * Private constructor.
     * 
     * @param name the specified name
     */
    private ArticleRepositoryImpl(final String name) {
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
        private static final ArticleRepositoryImpl SINGLETON =
                new ArticleRepositoryImpl(Article.ARTICLE);

        /**
         * Private default constructor.
         */
        private SingletonHolder() {
        }
    }
}
