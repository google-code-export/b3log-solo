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
package org.b3log.solo.service;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.repository.Transaction;
import org.b3log.solo.model.Article;
import org.b3log.solo.repository.ArticleRepository;
import org.b3log.solo.repository.impl.ArticleRepositoryImpl;
import org.b3log.solo.util.Statistics;
import org.json.JSONObject;

/**
 * Article management service.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Oct 18, 2011
 * @since 0.3.5
 */
public final class ArticleMgmtService {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(ArticleMgmtService.class.getName());
    /**
     * Article repository.
     */
    private ArticleRepository articleRepository =
            ArticleRepositoryImpl.getInstance();
    /**
     * Statistic utilities.
     */
    private Statistics statistics = Statistics.getInstance();

    /**
     * Updates the random values of articles fetched with the specified 
     * update count.
     * 
     * @param updateCnt the specified update count
     */
    public void updateArticlesRandomValue(final int updateCnt) {
        final Transaction transaction = articleRepository.beginTransaction();
        transaction.clearQueryCache(false);
        try {
            final List<JSONObject> randomArticles =
                    articleRepository.getRandomly(updateCnt);

            for (final JSONObject article : randomArticles) {
                article.put(Article.ARTICLE_RANDOM_DOUBLE, Math.random());

                articleRepository.update(article.getString(Keys.OBJECT_ID),
                                         article);
            }

            transaction.commit();
        } catch (final Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }

            LOGGER.log(Level.WARNING, "Updates article random value failed");
        }
    }

    /**
     * Gets the {@link ArticleMgmtService} singleton.
     *
     * @return the singleton
     */
    public static ArticleMgmtService getInstance() {
        return SingletonHolder.SINGLETON;
    }

    /**
     * Private constructor.
     */
    private ArticleMgmtService() {
    }

    /**
     * Singleton holder.
     *
     * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
     * @version 1.0.0.0, Oct 3, 2011
     */
    private static final class SingletonHolder {

        /**
         * Singleton.
         */
        private static final ArticleMgmtService SINGLETON =
                new ArticleMgmtService();

        /**
         * Private default constructor.
         */
        private SingletonHolder() {
        }
    }
}
