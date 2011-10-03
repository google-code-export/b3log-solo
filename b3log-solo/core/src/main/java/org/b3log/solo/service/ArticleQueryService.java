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

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.latke.repository.Repository;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.util.Strings;
import org.b3log.solo.model.Article;
import org.b3log.solo.repository.ArticleRepository;
import org.b3log.solo.repository.impl.ArticleRepositoryImpl;
import org.b3log.solo.repository.impl.StatisticRepositoryImpl;
import org.b3log.solo.service.util.QueryResults;
import org.b3log.solo.util.Statistics;
import org.json.JSONObject;

/**
 * Article query service.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Oct 3, 2011
 * @since 0.3.5
 */
public final class ArticleQueryService {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(ArticleQueryService.class.getName());
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
     * Gets a list of aticles randomly with the specified fetch size.
     *
     * @param fetchSize the specified fetch size
     * @return a list of json objects, its size less or equal to the specified
     * fetch size, returns an empty list if not found
     */
    public List<JSONObject> getArticlesRandomly(final int fetchSize) {
        try {
            return articleRepository.getRandomly(fetchSize);
        } catch (final RepositoryException e) {
            LOGGER.log(Level.SEVERE, "Gets articles randomly failed[fetchSize="
                                     + fetchSize + "]", e);
            return Collections.emptyList();
        }
    }

    /**
     * Determines an article specified by the given article id is published.
     * 
     * @param articleId the given article id
     * @return {@code true} if it is published, {@code false} otherwise
     */
    public boolean isArticlePublished(final String articleId) {
        try {
            return articleRepository.isPublished(articleId);
        } catch (final RepositoryException e) {
            LOGGER.log(Level.SEVERE, "Determines the article publish status failed[articleId="
                                     + articleId + "]", e);
            return false;
        }
    }

    /**
     * Gets the next article(by create date) by the specified article
     * id.
     *
     * @param articleId the specified article id
     * @return the previous article,
     * <pre>
     * {
     *     "articleTitle": "",
     *     "articlePermalink": ""
     * }
     * </pre>
     * returns {@code null} if not found
     */
    public JSONObject getNextArticle(final String articleId) {
        try {
            return articleRepository.getNextArticle(articleId);
        } catch (final RepositoryException e) {
            LOGGER.log(Level.SEVERE, "Gets the next article failed[articleId="
                                     + articleId + "]", e);
            return null;
        }
    }

    /**
     * Gets the previous article(by create date) by the specified article
     * id.
     *
     * @param articleId the specified article id
     * @return the previous article,
     * <pre>
     * {
     *     "articleTitle": "",
     *     "articlePermalink": ""
     * }
     * </pre>
     * returns {@code null} if not found
     */
    public JSONObject getPreviousArticle(final String articleId) {
        try {
            return articleRepository.getPreviousArticle(articleId);
        } catch (final RepositoryException e) {
            LOGGER.log(Level.SEVERE, "Gets the previous article failed[articleId="
                                     + articleId + "]", e);
            return null;
        }
    }

    /**
     * Gets an article by the specified article id.
     * 
     * @param articleId the specified article id
     * @return an article, returns {@code null} if not found
     */
    public JSONObject getArticleById(final String articleId) {
        try {
            return articleRepository.get(articleId);
        } catch (final RepositoryException e) {
            LOGGER.log(Level.SEVERE, "Gets an article[articleId=" + articleId
                                     + "] failed", e);
            return null;
        }
    }

    /**
     * Gets <em>published</em> articles by the specified author email, current page 
     * number and page size.
     * 
     * @param authorEmail the specified author email
     * @param currentPageNum the specified current page number
     * @param pageSize the specified page size
     * @return query result, for example
     * <pre>
     * {
     *     "pagination": {
     *       "paginationPageCount": 88250
     *     },
     *     "rslts": [{
     *         // article keys....
     *     }, ....]
     * }
     * </pre>
     */
    public JSONObject getArticlesByAuthorEmail(final String authorEmail,
                                               final int currentPageNum,
                                               final int pageSize) {
        try {
            return articleRepository.getByAuthorEmail(authorEmail,
                                                      currentPageNum,
                                                      pageSize);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, "Gets articles by author email failed[authorEmail="
                                     + authorEmail + ", currentPageNum="
                                     + currentPageNum + ", pageSize="
                                     + pageSize + "]", e);

            return QueryResults.defaultResult();
        }
    }

    /**
     * Gets article contents with the specified article id.
     * 
     * <p>
     * If gets article content successfully, increments article view count.
     * </p>
     * 
     * @param articleId the specified article id
     * @return article contents, returns {@code null} if not found
     */
    public String getArticleContent(final String articleId) {
        if (Strings.isEmptyOrNull(articleId)) {
            return null;
        }

        try {
            final JSONObject article = articleRepository.get(articleId);
            if (null == article) {
                return null;
            }

            final String ret = article.getString(Article.ARTICLE_CONTENT);

            final Repository statisticRepository =
                    StatisticRepositoryImpl.getInstance();
            final Transaction transaction =
                    statisticRepository.beginTransaction();
            transaction.clearQueryCache(false);
            try {
                statistics.incArticleViewCount(articleId);
                transaction.commit();
            } catch (final Exception e) {
                if (transaction.isActive()) {
                    transaction.rollback();
                }

                LOGGER.log(Level.WARNING, "Inc article view count failed[articleId="
                                          + articleId + "]", e);
            }

            return ret;
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, "Gets article content failed[articleId="
                                     + articleId + "]", e);

            return null;
        }
    }

    /**
     * Gets the {@link ArticleQueryService} singleton.
     *
     * @return the singleton
     */
    public static ArticleQueryService getInstance() {
        return SingletonHolder.SINGLETON;
    }

    /**
     * Private constructor.
     */
    private ArticleQueryService() {
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
        private static final ArticleQueryService SINGLETON =
                new ArticleQueryService();

        /**
         * Private default constructor.
         */
        private SingletonHolder() {
        }
    }
}
