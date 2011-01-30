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

package org.b3log.solo.util;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.latke.Latkes;
import org.b3log.latke.RunsOnEnv;
import org.b3log.latke.cache.Cache;
import org.b3log.latke.cache.CacheFactory;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.Statistic;
import org.b3log.solo.repository.ArticleRepository;
import org.b3log.solo.repository.StatisticRepository;
import org.b3log.solo.repository.impl.ArticleGAERepository;
import org.b3log.solo.repository.impl.StatisticGAERepository;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Statistic utilities.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.5, Jan 30, 2011
 */
public final class Statistics {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(Statistics.class.getName());
    /**
     * Statistic repository.
     */
    private StatisticRepository statisticRepository =
            StatisticGAERepository.getInstance();
    /**
     * Article repository.
     */
    private ArticleRepository articleRepository =
            ArticleGAERepository.getInstance();
    /**
     * Statistic cache.
     */
    public static final Cache<String, Object> CACHE;
    /**
     * Statistic cache name.
     */
    public static final String STATISTIC_CACHE_NAME = "statisticCache";

    /**
     * Initializes cache.
     */
    static {
        final RunsOnEnv runsOnEnv = Latkes.getRunsOnEnv();
        if (!runsOnEnv.equals(RunsOnEnv.GAE)) {
            throw new RuntimeException(
                    "GAE cache can only runs on Google App Engine, please "
                    + "check your configuration and make sure "
                    + "Latkes.setRunsOnEnv(RunsOnEnv.GAE) was invoked before "
                    + "using GAE cache.");
        }

        CACHE = CacheFactory.getCache(STATISTIC_CACHE_NAME);
    }

    /**
     * Get blog comment count.
     *
     * @return blog comment count
     * @throws JSONException json exception
     * @throws RepositoryException repository exception
     */
    public int getBlogCommentCount()
            throws JSONException, RepositoryException {
        final JSONObject statistic =
                statisticRepository.get(Statistic.STATISTIC);
        if (null == statistic) {
            throw new RepositoryException("Not found statistic");
        }

        return statistic.getInt(
                Statistic.STATISTIC_BLOG_COMMENT_COUNT);
    }

    /**
     * Get blog comment(published article) count.
     *
     * @return blog comment count
     * @throws JSONException json exception
     * @throws RepositoryException repository exception
     */
    public int getPublishedBlogCommentCount()
            throws JSONException, RepositoryException {
        final JSONObject statistic =
                statisticRepository.get(Statistic.STATISTIC);
        if (null == statistic) {
            throw new RepositoryException("Not found statistic");
        }

        return statistic.getInt(
                Statistic.STATISTIC_PUBLISHED_BLOG_COMMENT_COUNT);
    }

    /**
     * Sets blog comment count with the specified count.
     *
     * @param count the specified count
     * @throws JSONException json exception
     * @throws RepositoryException repository exception
     */
    public void setBlogCommentCount(final int count)
            throws JSONException, RepositoryException {
        final JSONObject statistic =
                statisticRepository.get(Statistic.STATISTIC);
        if (null == statistic) {
            throw new RepositoryException("Not found statistic");
        }

        statistic.put(Statistic.STATISTIC_BLOG_COMMENT_COUNT,
                      count);
        statisticRepository.update(Statistic.STATISTIC, statistic);
    }

    /**
     * Sets blog comment(published article) count with the specified count.
     *
     * @param count the specified count
     * @throws JSONException json exception
     * @throws RepositoryException repository exception
     */
    public void setPublishedBlogCommentCount(final int count)
            throws JSONException, RepositoryException {
        final JSONObject statistic =
                statisticRepository.get(Statistic.STATISTIC);
        if (null == statistic) {
            throw new RepositoryException("Not found statistic");
        }

        statistic.put(Statistic.STATISTIC_PUBLISHED_BLOG_COMMENT_COUNT,
                      count);
        statisticRepository.update(Statistic.STATISTIC, statistic);
    }

    /**
     * Gets blog statistic article count.
     *
     * @return blog article count
     * @throws JSONException json exception
     * @throws RepositoryException repository exception
     */
    public int getBlogArticleCount()
            throws JSONException, RepositoryException {
        final JSONObject statistic =
                statisticRepository.get(Statistic.STATISTIC);
        if (null == statistic) {
            throw new RepositoryException("Not found statistic");
        }

        return statistic.getInt(Statistic.STATISTIC_BLOG_ARTICLE_COUNT);
    }

    /**
     * Sets blog statistic article count with the specified count.
     *
     * @param count the specified blog article count
     * @throws JSONException json exception
     * @throws RepositoryException repository exception
     */
    public void setBlogArticleCount(final int count)
            throws JSONException, RepositoryException {
        final JSONObject statistic =
                statisticRepository.get(Statistic.STATISTIC);
        if (null == statistic) {
            throw new RepositoryException("Not found statistic");
        }

        statistic.put(Statistic.STATISTIC_BLOG_ARTICLE_COUNT,
                      count);
        statisticRepository.updateAsync(Statistic.STATISTIC, statistic);
    }

    /**
     * Blog statistic view count +1.
     * 
     * <p>
     * There is a cron job to flush the blog view count from memcache to 
     * datastore.
     * </p>
     */
    public void incBlogViewCount() {
        try {
            final JSONObject statistic =
                    statisticRepository.get(Statistic.STATISTIC);
            long blogViewCnt =
                    statistic.getLong(Statistic.STATISTIC_BLOG_VIEW_COUNT);
            ++blogViewCnt;
            statistic.put(Statistic.STATISTIC_BLOG_VIEW_COUNT, blogViewCnt);
            statisticRepository.updateAsync(Statistic.STATISTIC, statistic);
            LOGGER.log(Level.FINE, "Current blog view count[{0}]", blogViewCnt);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Article view count +1 in memcache for an article specified by the given
     * article id.
     *
     * <p>
     * The property(named {@value Article#ARTICLE_RANDOM_DOUBLE}) of the
     * specified article will be regenerated.
     * </p>
     *
     * @param articleId the given article id
     * @throws JSONException json exception
     * @throws RepositoryException repository exception
     */
    public void incArticleViewCount(final String articleId)
            throws JSONException, RepositoryException {
        try {
            final JSONObject article = articleRepository.get(articleId);
            if (null == article) {
                return;
            }

            final int viewCnt =
                    article.getInt(Article.ARTICLE_VIEW_COUNT) + 1;
            article.put(Article.ARTICLE_VIEW_COUNT, viewCnt);
            article.put(Article.ARTICLE_RANDOM_DOUBLE, Math.random());

            LOGGER.finer("Incing article view count async....");
            articleRepository.updateAsync(articleId, article);
            LOGGER.finer("Inced article view count");
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Blog statistic article count +1.
     *
     * @throws JSONException json exception
     * @throws RepositoryException repository exception
     */
    public void incBlogArticleCount()
            throws JSONException, RepositoryException {
        final JSONObject statistic =
                statisticRepository.get(Statistic.STATISTIC);
        if (null == statistic) {
            throw new RepositoryException("Not found statistic");
        }

        statistic.put(Statistic.STATISTIC_BLOG_ARTICLE_COUNT,
                      statistic.getInt(
                Statistic.STATISTIC_BLOG_ARTICLE_COUNT) + 1);
        statisticRepository.updateAsync(Statistic.STATISTIC, statistic);
    }

    /**
     * Blog statistic published article count +1.
     *
     * @throws JSONException json exception
     * @throws RepositoryException repository exception
     */
    public void incPublishedBlogArticleCount()
            throws JSONException, RepositoryException {
        final JSONObject statistic =
                statisticRepository.get(Statistic.STATISTIC);
        if (null == statistic) {
            throw new RepositoryException("Not found statistic");
        }

        statistic.put(Statistic.STATISTIC_PUBLISHED_ARTICLE_COUNT,
                      statistic.getInt(
                Statistic.STATISTIC_PUBLISHED_ARTICLE_COUNT) + 1);
        statisticRepository.updateAsync(Statistic.STATISTIC, statistic);
    }

    /**
     * Blog statistic article count -1.
     *
     * @throws JSONException json exception
     * @throws RepositoryException repository exception
     */
    public void decBlogArticleCount()
            throws JSONException, RepositoryException {
        final JSONObject statistic =
                statisticRepository.get(Statistic.STATISTIC);
        if (null == statistic) {
            throw new RepositoryException("Not found statistic");
        }

        statistic.put(Statistic.STATISTIC_BLOG_ARTICLE_COUNT,
                      statistic.getInt(
                Statistic.STATISTIC_BLOG_ARTICLE_COUNT) - 1);
        statisticRepository.update(Statistic.STATISTIC, statistic);
    }

    /**
     * Blog statistic published article count -1.
     *
     * @throws JSONException json exception
     * @throws RepositoryException repository exception
     */
    public void decPublishedBlogArticleCount()
            throws JSONException, RepositoryException {
        final JSONObject statistic =
                statisticRepository.get(Statistic.STATISTIC);
        if (null == statistic) {
            throw new RepositoryException("Not found statistic");
        }

        statistic.put(Statistic.STATISTIC_PUBLISHED_ARTICLE_COUNT,
                      statistic.getInt(
                Statistic.STATISTIC_PUBLISHED_ARTICLE_COUNT) - 1);
        statisticRepository.update(Statistic.STATISTIC, statistic);
    }

    /**
     * Blog statistic comment count +1.
     *
     * @throws JSONException json exception
     * @throws RepositoryException repository exception
     */
    public void incBlogCommentCount()
            throws JSONException, RepositoryException {
        final JSONObject statistic =
                statisticRepository.get(Statistic.STATISTIC);
        if (null == statistic) {
            throw new RepositoryException("Not found statistic");
        }
        statistic.put(Statistic.STATISTIC_BLOG_COMMENT_COUNT,
                      statistic.getInt(
                Statistic.STATISTIC_BLOG_COMMENT_COUNT) + 1);
        statisticRepository.updateAsync(Statistic.STATISTIC, statistic);
    }

    /**
     * Blog statistic comment(published article) count +1.
     *
     * @throws JSONException json exception
     * @throws RepositoryException repository exception
     */
    public void incPublishedBlogCommentCount()
            throws JSONException, RepositoryException {
        final JSONObject statistic =
                statisticRepository.get(Statistic.STATISTIC);
        if (null == statistic) {
            throw new RepositoryException("Not found statistic");
        }
        statistic.put(Statistic.STATISTIC_PUBLISHED_BLOG_COMMENT_COUNT,
                      statistic.getInt(
                Statistic.STATISTIC_PUBLISHED_BLOG_COMMENT_COUNT) + 1);
        statisticRepository.updateAsync(Statistic.STATISTIC, statistic);
    }

    /**
     * Blog statistic comment count -1.
     *
     * @throws JSONException json exception
     * @throws RepositoryException repository exception
     */
    public void decBlogCommentCount()
            throws JSONException, RepositoryException {
        final JSONObject statistic =
                statisticRepository.get(Statistic.STATISTIC);
        if (null == statistic) {
            throw new RepositoryException("Not found statistic");
        }

        statistic.put(Statistic.STATISTIC_BLOG_COMMENT_COUNT,
                      statistic.getInt(
                Statistic.STATISTIC_BLOG_COMMENT_COUNT) - 1);
        statisticRepository.update(Statistic.STATISTIC, statistic);
    }

    /**
     * Blog statistic comment(published article) count -1.
     *
     * @throws JSONException json exception
     * @throws RepositoryException repository exception
     */
    public void decPublishedBlogCommentCount()
            throws JSONException, RepositoryException {
        final JSONObject statistic =
                statisticRepository.get(Statistic.STATISTIC);
        if (null == statistic) {
            throw new RepositoryException("Not found statistic");
        }

        statistic.put(Statistic.STATISTIC_PUBLISHED_BLOG_COMMENT_COUNT,
                      statistic.getInt(
                Statistic.STATISTIC_PUBLISHED_BLOG_COMMENT_COUNT) - 1);
        statisticRepository.update(Statistic.STATISTIC, statistic);
    }

    /**
     * Gets the {@link Statistics} singleton.
     *
     * @return the singleton
     */
    public static Statistics getInstance() {
        return SingletonHolder.SINGLETON;
    }

    /**
     * Private default constructor.
     */
    private Statistics() {
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
        private static final Statistics SINGLETON = new Statistics();

        /**
         * Private default constructor.
         */
        private SingletonHolder() {
        }
    }
}
