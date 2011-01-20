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

package org.b3log.solo.action.stat;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.latke.repository.Transaction;
import org.b3log.solo.model.Statistic;
import org.b3log.solo.repository.ArticleRepository;
import org.b3log.solo.repository.StatisticRepository;
import org.b3log.solo.repository.impl.ArticleGAERepository;
import org.b3log.solo.repository.impl.StatisticGAERepository;
import org.b3log.solo.util.Statistics;
import org.json.JSONObject;

/**
 * Flushes statistic from memcache to datastore.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Jan 20, 2011
 */
public final class FlushStatisticServlet extends HttpServlet {

    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(FlushStatisticServlet.class.getName());
    /**
     * Article repository.
     */
    private ArticleRepository articleRepository =
            ArticleGAERepository.getInstance();
    /**
     * Statistic repository.
     */
    private StatisticRepository statisticRepository =
            StatisticGAERepository.getInstance();

    @Override
    protected void doGet(final HttpServletRequest request,
                         final HttpServletResponse response)
            throws ServletException, IOException {
        final Integer blogViewCnt = (Integer) Statistics.CACHE.get(
                Statistic.STATISTIC_BLOG_VIEW_COUNT);
        if (null == blogViewCnt) {
            LOGGER.warning("Blog view count is null");
            return;
        }

        Transaction transaction = statisticRepository.beginTransaction();
        try {
            final JSONObject statistic =
                    statisticRepository.get(Statistic.STATISTIC);
            statistic.put(Statistic.STATISTIC_BLOG_VIEW_COUNT, blogViewCnt);
            statisticRepository.update(Statistic.STATISTIC, statistic);

            transaction.commit();
        } catch (final Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        LOGGER.log(Level.FINE, "Flushed the blog view count[{0}]",
                   blogViewCnt);

        transaction = statisticRepository.beginTransaction();
        try {
            @SuppressWarnings("unchecked")
            final Set<String> articleIds = (Set<String>) Statistics.CACHE.get(
                    Statistics.KEY_ARTICLE_NEED_TO_FLUSH);
            if (null != articleIds) {
                final Iterator<String> iterator = articleIds.iterator();
                while (iterator.hasNext()) {
                    final String articleId = iterator.next();
                    LOGGER.log(Level.FINER, "Article[oId={0}]", articleId);
                    final JSONObject articleStat =
                            (JSONObject) Statistics.CACHE.get(articleId);
                    if (null != articleStat) {
                        articleRepository.update(articleId, articleStat);
                        LOGGER.log(Level.FINE,
                                   "Flushing statistic of article[oId={0}]",
                                   articleId);
                    }

                    iterator.remove();
                    Statistics.CACHE.remove(articleId);
                }

                Statistics.CACHE.put(Statistics.KEY_ARTICLE_NEED_TO_FLUSH,
                                     articleIds);
                LOGGER.log(Level.FINE,
                           "Next time will flush [{0}] statistics",
                           articleIds.size());
            }
            transaction.commit();
        } catch (final Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        LOGGER.log(Level.FINE, "Flushed the articles view count");
    }
}
