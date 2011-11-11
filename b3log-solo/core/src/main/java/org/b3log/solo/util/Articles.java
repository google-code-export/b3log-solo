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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.solo.model.Article;
import org.b3log.latke.Keys;
import org.b3log.latke.repository.FilterOperator;
import org.b3log.latke.repository.Query;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.SortDirection;
import org.b3log.latke.service.ServiceException;
import org.b3log.latke.util.CollectionUtils;
import org.b3log.solo.model.Preference;
import org.b3log.solo.model.Sign;
import org.b3log.solo.repository.ArticleRepository;
import org.b3log.solo.repository.ArticleSignRepository;
import org.b3log.solo.repository.UserRepository;
import org.b3log.solo.repository.impl.ArticleRepositoryImpl;
import org.b3log.solo.repository.impl.ArticleSignRepositoryImpl;
import org.b3log.solo.repository.impl.UserRepositoryImpl;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Article utilities.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.2.5, Nov 5, 2011
 * @since 0.3.1
 */
public final class Articles {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(Articles.class.getName());
    /**
     * Article repository.
     */
    private ArticleRepository articleRepository =
            ArticleRepositoryImpl.getInstance();
    /**
     * Article-Sign repository.
     */
    private ArticleSignRepository articleSignRepository =
            ArticleSignRepositoryImpl.getInstance();
    /**
     * User repository.
     */
    private UserRepository userRepository = UserRepositoryImpl.getInstance();

    /**
     * Gets the specified article's author. 
     * 
     * <p>
     * The specified article has a property
     * {@value Article#ARTICLE_AUTHOR_EMAIL}, this method will use this property
     * to get a user from users.
     * </p>
     * 
     * <p>
     * If can't find the specified article's author (i.e. the author has been 
     * removed by administrator), returns administrator.
     * </p>
     *
     * @param article the specified article
     * @return user, {@code null} if not found
     * @throws ServiceException service exception
     */
    public JSONObject getAuthor(final JSONObject article)
            throws ServiceException {
        try {
            final String email = article.getString(Article.ARTICLE_AUTHOR_EMAIL);

            JSONObject ret = userRepository.getByEmail(email);

            if (null == ret) {
                LOGGER.log(Level.WARNING,
                           "Gets author of article failed, assumes the administrator is the author of this article[id={0}]",
                           article.getString(Keys.OBJECT_ID));
                // This author may be deleted by admin, use admin as the author
                // of this article
                ret = userRepository.getAdmin();
            }

            return ret;
        } catch (final RepositoryException e) {
            LOGGER.log(Level.SEVERE, "Gets author of article[id={0}] failed",
                       article.optString(Keys.OBJECT_ID));
            throw new ServiceException(e);
        } catch (final JSONException e) {
            LOGGER.log(Level.SEVERE, "Gets author of article[id={0}] failed",
                       article.optString(Keys.OBJECT_ID));
            throw new ServiceException(e);
        }
    }

    /**
     * Article comment count +1 for an article specified by the given article id.
     *
     * @param articleId the given article id
     * @throws JSONException json exception
     * @throws RepositoryException repository exception
     */
    public void incArticleCommentCount(final String articleId)
            throws JSONException, RepositoryException {
        final JSONObject article = articleRepository.get(articleId);
        final JSONObject newArticle =
                new JSONObject(article, JSONObject.getNames(article));
        final int commentCnt = article.getInt(Article.ARTICLE_COMMENT_COUNT);
        newArticle.put(Article.ARTICLE_COMMENT_COUNT, commentCnt + 1);
        articleRepository.update(articleId, newArticle);
    }

    /**
     * Article comment count -1 for an article specified by the given article id.
     *
     * @param articleId the given article id
     * @throws JSONException json exception
     * @throws RepositoryException repository exception
     */
    public void decArticleCommentCount(final String articleId)
            throws JSONException, RepositoryException {
        final JSONObject article = articleRepository.get(articleId);
        final JSONObject newArticle =
                new JSONObject(article, JSONObject.getNames(article));
        final int commentCnt = article.getInt(Article.ARTICLE_COMMENT_COUNT);
        newArticle.put(Article.ARTICLE_COMMENT_COUNT, commentCnt - 1);
        articleRepository.update(articleId, newArticle);
    }

    /**
     * Gets sign id of an article specified by the article id.
     *
     * @param articleId the specified article id
     * @param preference the specified preference
     * @return article sign, returns the default sign(which oId is "1") if not
     * found
     * @throws RepositoryException repository exception
     * @throws JSONException json exception
     */
    public JSONObject getSign(final String articleId,
                              final JSONObject preference)
            throws JSONException, RepositoryException {
        final JSONArray signs = new JSONArray(
                preference.getString(Preference.SIGNS));

        final JSONObject relation =
                articleSignRepository.getByArticleId(articleId);
        if (null == relation) {
            for (int i = 0; i < signs.length(); i++) {
                final JSONObject ret = signs.getJSONObject(i);
                if ("1".equals(ret.getString(Keys.OBJECT_ID))) {
                    LOGGER.log(Level.FINEST, "Used default article sign[{0}]",
                               ret);
                    return ret;
                }
            }
        }

        for (int i = 0; i < signs.length(); i++) {
            final JSONObject ret = signs.getJSONObject(i);
            if (relation.getString(Sign.SIGN + "_" + Keys.OBJECT_ID).
                    equals(ret.getString(Keys.OBJECT_ID))) {
                return ret;
            }
        }

        throw new IllegalStateException("Can't load article sign!");
    }

    /**
     * Determines the specified article has updated.
     *
     * @param article the specified article
     * @return {@code true} if it has updated, {@code false} otherwise
     * @throws JSONException json exception
     */
    public boolean hasUpdated(final JSONObject article)
            throws JSONException {
        final Date updateDate = (Date) article.get(Article.ARTICLE_UPDATE_DATE);
        final Date createDate = (Date) article.get(Article.ARTICLE_CREATE_DATE);

        return !createDate.equals(updateDate);
    }

    /**
     * Determines the specified article had been published.
     *
     * @param article the specified article
     * @return {@code true} if it had been published, {@code false} otherwise
     * @throws JSONException json exception
     */
    public boolean hadBeenPublished(final JSONObject article)
            throws JSONException {
        return article.getBoolean(Article.ARTICLE_HAD_BEEN_PUBLISHED);
    }

    /**
     * Gets all unpublished articles.
     *
     * @return articles all unpublished articles
     * @throws RepositoryException repository exception
     * @throws JSONException json exception
     */
    public List<JSONObject> getUnpublishedArticles()
            throws RepositoryException, JSONException {
        final Map<String, SortDirection> sorts =
                new HashMap<String, SortDirection>();
        sorts.put(Article.ARTICLE_CREATE_DATE, SortDirection.DESCENDING);
        sorts.put(Article.ARTICLE_PUT_TOP, SortDirection.DESCENDING);
        final Query query = new Query().addFilter(Article.ARTICLE_IS_PUBLISHED,
                                                  FilterOperator.EQUAL,
                                                  true);
        final JSONObject result = articleRepository.get(query);
        final JSONArray articles = result.getJSONArray(Keys.RESULTS);

        return CollectionUtils.jsonArrayToList(articles);
    }

    /**
     * Gets the {@link Articles} singleton.
     *
     * @return the singleton
     */
    public static Articles getInstance() {
        return SingletonHolder.SINGLETON;
    }

    /**
     * Private default constructor.
     */
    private Articles() {
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
        private static final Articles SINGLETON = new Articles();

        /**
         * Private default constructor.
         */
        private SingletonHolder() {
        }
    }
}
