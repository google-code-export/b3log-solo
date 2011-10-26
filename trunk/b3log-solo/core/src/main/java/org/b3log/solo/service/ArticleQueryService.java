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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.repository.Repository;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.service.ServiceException;
import org.b3log.latke.util.CollectionUtils;
import org.b3log.latke.util.Strings;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.Preference;
import org.b3log.solo.repository.ArticleRepository;
import org.b3log.solo.repository.TagArticleRepository;
import org.b3log.solo.repository.TagRepository;
import org.b3log.solo.repository.impl.ArticleRepositoryImpl;
import org.b3log.solo.repository.impl.StatisticRepositoryImpl;
import org.b3log.solo.repository.impl.TagArticleRepositoryImpl;
import org.b3log.solo.repository.impl.TagRepositoryImpl;
import org.b3log.solo.util.Statistics;
import org.b3log.solo.util.comparator.Comparators;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Article query service.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Oct 17, 2011
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
     * Tag repository.
     */
    private TagRepository tagRepository = TagRepositoryImpl.getInstance();
    /**
     * Tag-Article repository.
     */
    private TagArticleRepository tagArticleRepository =
            TagArticleRepositoryImpl.getInstance();
    /**
     * Statistic utilities.
     */
    private Statistics statistics = Statistics.getInstance();

    /**
     * Gets a list of articles randomly with the specified fetch size.
     *
     * @param fetchSize the specified fetch size
     * @return a list of json objects, its size less or equal to the specified
     * fetch size
     * @throws ServiceException service exception 
     */
    public List<JSONObject> getArticlesRandomly(final int fetchSize)
            throws ServiceException {
        try {
            final List<JSONObject> ret =
                    articleRepository.getRandomly(fetchSize);

            removeUnusedProperties(ret);

            return ret;
        } catch (final RepositoryException e) {
            LOGGER.log(Level.SEVERE, "Gets articles randomly failed[fetchSize="
                                     + fetchSize + "]", e);
            throw new ServiceException(e);
        }
    }

    /**
     * Gets the relevant published articles of the specified article.
     *
     * @param article the specified article
     * @param preference the specified preference
     * @return a list of articles, returns an empty list if not found
     * @throws ServiceException service exception
     */
    public List<JSONObject> getRelevantArticles(
            final JSONObject article, final JSONObject preference)
            throws ServiceException {
        try {
            final int displayCnt =
                    preference.getInt(Preference.RELEVANT_ARTICLES_DISPLAY_CNT);
            final String[] tagTitles =
                    article.getString(Article.ARTICLE_TAGS_REF).split(",");
            final int maxTagCnt = displayCnt > tagTitles.length
                                  ? tagTitles.length : displayCnt;
            final String articleId = article.getString(Keys.OBJECT_ID);

            final List<JSONObject> articles = new ArrayList<JSONObject>();
            for (int i = 0; i < maxTagCnt; i++) {  // XXX: should average by tag?
                final String tagTitle = tagTitles[i];
                final JSONObject tag = tagRepository.getByTitle(tagTitle);
                final String tagId = tag.getString(Keys.OBJECT_ID);
                final JSONObject result =
                        tagArticleRepository.getByTagId(tagId, 1, displayCnt);
                final JSONArray tagArticleRelations =
                        result.getJSONArray(Keys.RESULTS);

                final int relationSize = displayCnt
                                         < tagArticleRelations.length()
                                         ? displayCnt : tagArticleRelations.
                        length();
                for (int j = 0; j < relationSize; j++) {
                    final JSONObject tagArticleRelation =
                            tagArticleRelations.getJSONObject(j);
                    final String relatedArticleId =
                            tagArticleRelation.getString(Article.ARTICLE + "_"
                                                         + Keys.OBJECT_ID);
                    if (articleId.equals(relatedArticleId)) {
                        continue;
                    }

                    final JSONObject relevant =
                            articleRepository.get(relatedArticleId);
                    if (!relevant.getBoolean(Article.ARTICLE_IS_PUBLISHED)) {
                        continue;
                    }

                    boolean existed = false;
                    for (final JSONObject relevantArticle : articles) {
                        if (relevantArticle.getString(Keys.OBJECT_ID).
                                equals(relevant.getString(Keys.OBJECT_ID))) {
                            existed = true;
                        }
                    }

                    if (!existed) {
                        articles.add(relevant);
                    }
                }
            }

            Collections.sort(articles,
                             Comparators.ARTICLE_UPDATE_DATE_COMPARATOR);
            removeUnusedProperties(articles);

            if (displayCnt > articles.size()) {
                return articles;
            }

            final List<Integer> randomIntegers =
                    CollectionUtils.getRandomIntegers(0,
                                                      articles.size() - 1,
                                                      displayCnt);
            final List<JSONObject> ret = new ArrayList<JSONObject>();
            for (final int index : randomIntegers) {
                ret.add(articles.get(index));
            }

            return ret;
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, "Gets relevant articles failed", e);

            throw new ServiceException(e);
        }
    }

    /**
     * Determines an article specified by the given article id is published.
     * 
     * @param articleId the given article id
     * @return {@code true} if it is published
     * @throws ServiceException service exception 
     */
    public boolean isArticlePublished(final String articleId)
            throws ServiceException {
        try {
            return articleRepository.isPublished(articleId);
        } catch (final RepositoryException e) {
            LOGGER.log(Level.SEVERE, "Determines the article publish status failed[articleId="
                                     + articleId + "]", e);
            throw new ServiceException(e);
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
     * @throws ServiceException service exception 
     */
    public JSONObject getNextArticle(final String articleId)
            throws ServiceException {
        try {
            return articleRepository.getNextArticle(articleId);
        } catch (final RepositoryException e) {
            LOGGER.log(Level.SEVERE, "Gets the next article failed[articleId="
                                     + articleId + "]", e);
            throw new ServiceException(e);
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
     * @throws ServiceException service exception 
     */
    public JSONObject getPreviousArticle(final String articleId)
            throws ServiceException {
        try {
            return articleRepository.getPreviousArticle(articleId);
        } catch (final RepositoryException e) {
            LOGGER.log(Level.SEVERE, "Gets the previous article failed[articleId="
                                     + articleId + "]", e);
            throw new ServiceException(e);
        }
    }

    /**
     * Gets an article by the specified article id.
     * 
     * @param articleId the specified article id
     * @return an article, returns {@code null} if not found
     * @throws ServiceException service exception 
     */
    public JSONObject getArticleById(final String articleId)
            throws ServiceException {
        try {
            return articleRepository.get(articleId);
        } catch (final RepositoryException e) {
            LOGGER.log(Level.SEVERE, "Gets an article[articleId=" + articleId
                                     + "] failed", e);
            throw new ServiceException(e);
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
     * @throws ServiceException service exception 
     */
    public JSONObject getArticlesByAuthorEmail(final String authorEmail,
                                               final int currentPageNum,
                                               final int pageSize)
            throws ServiceException {
        try {
            return articleRepository.getByAuthorEmail(authorEmail,
                                                      currentPageNum,
                                                      pageSize);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, "Gets articles by author email failed[authorEmail="
                                     + authorEmail + ", currentPageNum="
                                     + currentPageNum + ", pageSize="
                                     + pageSize + "]", e);

            throw new ServiceException(e);
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
     * @throws ServiceException service exception 
     */
    public String getArticleContent(final String articleId)
            throws ServiceException {
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

            throw new ServiceException(e);
        }
    }

    /**
     * Removes unused properties of each article in the specified articles.
     * 
     * <p>
     * Remains the following properties:
     * <ul>
     *   <li>{@link Article#ARTICLE_TITLE article title}</li>
     *   <li>{@link Article#ARTICLE_PERMALINK article permalink}</li>
     * </ul>
     * </p>
     * 
     * <p>
     * The batch version of method {@link #removeUnusedProperties(org.json.JSONObject)}.
     * </p>
     * 
     * @param articles the specified articles
     * @see #removeUnusedProperties(org.json.JSONObject) 
     */
    public void removeUnusedProperties(final List<JSONObject> articles) {
        for (final JSONObject article : articles) {
            removeUnusedProperties(article);
        }
    }

    /**
     * Removes unused properties of the specified article.
     * 
     * <p>
     * Remains the following properties:
     * <ul>
     *   <li>{@link Article#ARTICLE_TITLE article title}</li>
     *   <li>{@link Article#ARTICLE_PERMALINK article permalink}</li>
     * </ul>
     * </p>
     * 
     * @param article the specified article
     * @see #removeUnusedProperties(java.util.List) 
     */
    public void removeUnusedProperties(final JSONObject article) {
        article.remove(Keys.OBJECT_ID);
        article.remove(Article.ARTICLE_AUTHOR_EMAIL);
        article.remove(Article.ARTICLE_ABSTRACT);
        article.remove(Article.ARTICLE_COMMENT_COUNT);
        article.remove(Article.ARTICLE_CONTENT);
        article.remove(Article.ARTICLE_CREATE_DATE);
        article.remove(Article.ARTICLE_TAGS_REF);
        article.remove(Article.ARTICLE_UPDATE_DATE);
        article.remove(Article.ARTICLE_VIEW_COUNT);
        article.remove(Article.ARTICLE_RANDOM_DOUBLE);
        article.remove(Article.ARTICLE_IS_PUBLISHED);
        article.remove(Article.ARTICLE_PUT_TOP);
        article.remove(Article.ARTICLE_HAD_BEEN_PUBLISHED);
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
