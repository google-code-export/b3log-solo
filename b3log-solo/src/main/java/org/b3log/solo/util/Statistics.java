/*
 * Copyright (c) 2009, 2010, B3log Team
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

import com.google.inject.Inject;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.solo.model.Statistic;
import org.b3log.solo.repository.StatisticRepository;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Statistic utilities.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Oct 26, 2010
 */
public final class Statistics {

    /**
     * Statistic repository.
     */
    @Inject
    private StatisticRepository statisticRepository;

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
        return statistic.getInt(
                Statistic.STATISTIC_BLOG_COMMENT_COUNT);
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
        statistic.put(Statistic.STATISTIC_BLOG_COMMENT_COUNT,
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
        statistic.put(Statistic.STATISTIC_BLOG_ARTICLE_COUNT,
                      count);
        statisticRepository.update(Statistic.STATISTIC, statistic);
    }

    /**
     * Blog statistic view count +1.
     *
     * @throws JSONException json exception
     * @throws RepositoryException repository exception
     */
    public void incBlogViewCount()
            throws JSONException, RepositoryException {
        final JSONObject statistic =
                statisticRepository.get(Statistic.STATISTIC);
        statistic.put(Statistic.STATISTIC_BLOG_VIEW_COUNT,
                      statistic.getInt(
                Statistic.STATISTIC_BLOG_VIEW_COUNT) + 1);
        statisticRepository.update(Statistic.STATISTIC, statistic);
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
        statistic.put(Statistic.STATISTIC_BLOG_ARTICLE_COUNT,
                      statistic.getInt(
                Statistic.STATISTIC_BLOG_ARTICLE_COUNT) + 1);
        statisticRepository.update(Statistic.STATISTIC, statistic);
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
        statistic.put(Statistic.STATISTIC_BLOG_ARTICLE_COUNT,
                      statistic.getInt(
                Statistic.STATISTIC_BLOG_ARTICLE_COUNT) - 1);
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
        statistic.put(Statistic.STATISTIC_BLOG_COMMENT_COUNT,
                      statistic.getInt(
                Statistic.STATISTIC_BLOG_COMMENT_COUNT) + 1);
        statisticRepository.update(Statistic.STATISTIC, statistic);
    }

    /**
     * Blog statistic article count -1.
     *
     * @throws JSONException json exception
     * @throws RepositoryException repository exception
     */
    public void decBlogCommentCount()
            throws JSONException, RepositoryException {
        final JSONObject statistic =
                statisticRepository.get(Statistic.STATISTIC);
        statistic.put(Statistic.STATISTIC_BLOG_COMMENT_COUNT,
                      statistic.getInt(
                Statistic.STATISTIC_BLOG_COMMENT_COUNT) - 1);
        statisticRepository.update(Statistic.STATISTIC, statistic);
    }
}
