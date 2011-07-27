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
import com.google.appengine.api.datastore.QueryResultIterable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.RuntimeEnv;
import org.b3log.latke.cache.Cache;
import org.b3log.latke.cache.CacheFactory;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.solo.model.Comment;
import org.b3log.solo.repository.CommentRepository;
import org.b3log.latke.repository.gae.AbstractGAERepository;
import org.b3log.solo.model.Article;
import org.b3log.solo.repository.ArticleCommentRepository;
import org.b3log.solo.repository.ArticleRepository;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Comment Google App Engine repository.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.5, Jan 17, 2011
 */
public final class CommentGAERepository extends AbstractGAERepository
        implements CommentRepository {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(CommentGAERepository.class.getName());
    /**
     * Cache.
     */
    private static final Cache<String, Object> CACHE;
    /**
     * Key of the recent comments cache count.
     */
    private static final String KEY_RECENT_COMMENTS_CACHE_CNT =
            "mostRecentCommentsCacheCnt";
    /**
     * Article-Comment repository.
     */
    private ArticleCommentRepository articleCommentRepository =
            ArticleCommentGAERepository.getInstance();
    /**
     * Article repository.
     */
    private ArticleRepository articleRepository =
            ArticleGAERepository.getInstance();

    static {
        final RuntimeEnv runtimeEnv = Latkes.getRuntimeEnv();
        if (!runtimeEnv.equals(RuntimeEnv.GAE)) {
            throw new IllegalStateException(
                    "GAE repository can only runs on Google App Engine, please "
                    + "check your configuration and make sure "
                    + "Latkes.setRuntimeEnv(RuntimeEnv.GAE) was invoked before "
                    + "using GAE repository.");
        }

        CACHE = CacheFactory.getCache("CommentGAERepositoryCache");
    }

    @Override
    public String getName() {
        return Comment.COMMENT;
    }

    @Override
    public List<JSONObject> getRecentComments(final int num)
            throws RepositoryException {
        final String cacheKey = KEY_RECENT_COMMENTS_CACHE_CNT + "["
                                + num + "]";
        @SuppressWarnings("unchecked")
        List<JSONObject> ret = (List<JSONObject>) CACHE.get(cacheKey);
        if (null != ret) {
            LOGGER.log(Level.FINEST, "Got the recent comments from cache");
        } else {
            ret = new ArrayList<JSONObject>();
            final Query query = new Query(getName());
            query.addSort(Keys.OBJECT_ID,
                          Query.SortDirection.DESCENDING);
            final PreparedQuery preparedQuery = getDatastoreService().prepare(
                    query);
            final QueryResultIterable<Entity> queryResultIterable =
                    preparedQuery.asQueryResultIterable(FetchOptions.Builder.
                    withLimit(num));

            for (final Entity entity : queryResultIterable) {
                final JSONObject comment = entity2JSONObject(entity);
                ret.add(comment);
            }

            try {
                removeForUnpublishedArticles(ret);
            } catch (final JSONException e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }

            CACHE.put(cacheKey, ret);

            LOGGER.log(Level.FINEST,
                       "Got the recent comments, then put it into cache");
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
            final String commentId = comment.getString(Keys.OBJECT_ID);
            final JSONObject articleCommentRelation =
                    articleCommentRepository.getByCommentId(commentId);
            if (null == articleCommentRelation) {
                continue; // This comment is a page comment or comment has been removed just
            }
            final String articleId = articleCommentRelation.getString(
                    Article.ARTICLE + "_" + Keys.OBJECT_ID);
            if (!articleRepository.isPublished(articleId)) {
                iterator.remove();
            }
        }

        LOGGER.finer("Removed unpublished articles' comments....");
    }

    /**
     * Gets the {@link CommentGAERepository} singleton.
     *
     * @return the singleton
     */
    public static CommentGAERepository getInstance() {
        return SingletonHolder.SINGLETON;
    }

    /**
     * Private default constructor.
     */
    private CommentGAERepository() {
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
        private static final CommentGAERepository SINGLETON =
                new CommentGAERepository();

        /**
         * Private default constructor.
         */
        private SingletonHolder() {
        }
    }
}