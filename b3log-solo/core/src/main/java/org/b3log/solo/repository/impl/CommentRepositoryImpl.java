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
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.cache.Cache;
import org.b3log.latke.repository.AbstractRepository;
import org.b3log.latke.repository.FilterOperator;
import org.b3log.latke.repository.Query;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.SortDirection;
import org.b3log.solo.model.Comment;
import org.b3log.solo.repository.CommentRepository;
import org.b3log.latke.util.CollectionUtils;
import org.b3log.solo.model.Article;
import org.b3log.solo.repository.ArticleRepository;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Comment repository.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.8, Oct 18, 2011
 * @since 0.3.1
 */
public final class CommentRepositoryImpl extends AbstractRepository
        implements CommentRepository {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(CommentRepositoryImpl.class.getName());
    /**
     * Article repository.
     */
    private ArticleRepository articleRepository =
            ArticleRepositoryImpl.getInstance();
    /**
     * Recent comments query results cache key.
     */
    public static final String RECENT_CMTS_CACHE_KEY = "recentCMTs";

    @Override
    public int removeComments(final String onId) throws RepositoryException {
        final List<JSONObject> comments =
                getComments(onId, 1, Integer.MAX_VALUE);

        try {
            for (final JSONObject comment : comments) {
                final String commentId = comment.getString(Keys.OBJECT_ID);
                remove(commentId);
            }
        } catch (final JSONException e) {
            LOGGER.log(Level.SEVERE, "Remove comments[onId=" + onId + "] error",
                       e);

            throw new RepositoryException(e);
        }

        LOGGER.log(Level.FINER, "Removed comments[onId={0}, removedCnt={1}]",
                   new Object[]{onId, comments.size()});

        return comments.size();
    }

    @Override
    public List<JSONObject> getComments(final String onId,
                                        final int currentPageNum,
                                        final int pageSize) {
        final Query query = new Query();
        query.addSort(Keys.OBJECT_ID, SortDirection.DESCENDING);
        query.addFilter(Comment.COMMENT_ON_ID, FilterOperator.EQUAL, onId);
        query.setCurrentPageNum(currentPageNum);
        query.setPageSize(pageSize);

        List<JSONObject> ret = new ArrayList<JSONObject>();
        try {
            final JSONObject result = get(query);

            final JSONArray array = result.getJSONArray(Keys.RESULTS);

            ret = CollectionUtils.jsonArrayToList(array);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

        return ret;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<JSONObject> getRecentComments(final int num) {
        if (isCacheEnabled()) {
            final Cache<String, Object> cache = getCache();
            final Object ret = cache.get(RECENT_CMTS_CACHE_KEY);
            if (null != ret) {
                return (List<JSONObject>) ret;
            }
        }

        final Query query = new Query().addSort(Keys.OBJECT_ID,
                                                SortDirection.DESCENDING).
                setCurrentPageNum(1).
                setPageSize(num).setPageCount(1);

        List<JSONObject> ret = new ArrayList<JSONObject>();
        try {
            final JSONObject result = get(query);

            final JSONArray array = result.getJSONArray(Keys.RESULTS);

            ret = CollectionUtils.jsonArrayToList(array);

            // Removes unpublished article related comments
            removeForUnpublishedArticles(ret);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return ret;
        }

        if (isCacheEnabled()) {
            final Cache<String, Object> cache = getCache();
            cache.put(RECENT_CMTS_CACHE_KEY, (Object) ret);
        }

        return ret;
    }

    /**
     * Removes comments of unpublished articles for the specified comments.
     *
     * @param comments the specified comments
     * @throws JSONException json exception
     * @throws RepositoryException repository exception
     */
    private void removeForUnpublishedArticles(
            final List<JSONObject> comments) throws JSONException,
                                                    RepositoryException {
        LOGGER.finer("Removing unpublished articles' comments....");
        final Iterator<JSONObject> iterator = comments.iterator();
        while (iterator.hasNext()) {
            final JSONObject comment = iterator.next();
            final String commentOnType =
                    comment.getString(Comment.COMMENT_ON_TYPE);
            if (Article.ARTICLE.equals(commentOnType)) {
                final String articleId =
                        comment.getString(Comment.COMMENT_ON_ID);

                if (!articleRepository.isPublished(articleId)) {
                    iterator.remove();
                }
            }
        }

        LOGGER.finer("Removed unpublished articles' comments....");
    }

    /**
     * Gets the {@link CommentRepositoryImpl} singleton.
     *
     * @return the singleton
     */
    public static CommentRepositoryImpl getInstance() {
        return SingletonHolder.SINGLETON;
    }

    /**
     * Private constructor.
     * 
     * @param name the specified name
     */
    private CommentRepositoryImpl(final String name) {
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
        private static final CommentRepositoryImpl SINGLETON =
                new CommentRepositoryImpl(Comment.COMMENT);

        /**
         * Private default constructor.
         */
        private SingletonHolder() {
        }
    }
}
