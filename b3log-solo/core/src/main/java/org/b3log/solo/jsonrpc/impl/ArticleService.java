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
package org.b3log.solo.jsonrpc.impl;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.solo.web.action.StatusCodes;
import org.b3log.solo.event.EventTypes;
import static org.b3log.solo.model.Article.*;
import org.b3log.solo.model.Tag;
import org.b3log.solo.repository.ArticleRepository;
import org.b3log.solo.repository.TagArticleRepository;
import org.b3log.solo.repository.TagRepository;
import org.b3log.latke.Keys;
import org.b3log.latke.action.ActionException;
import org.b3log.latke.action.util.Paginator;
import org.b3log.latke.event.Event;
import org.b3log.latke.event.EventException;
import org.b3log.latke.event.EventManager;
import org.b3log.latke.model.Pagination;
import org.b3log.latke.model.User;
import org.b3log.latke.repository.FilterOperator;
import org.b3log.latke.repository.Query;
import org.b3log.latke.repository.SortDirection;
import org.b3log.latke.repository.Transaction;
import org.b3log.solo.jsonrpc.AbstractGAEJSONRpcService;
import org.b3log.solo.model.Common;
import org.b3log.solo.model.Preference;
import org.b3log.solo.model.Sign;
import org.b3log.solo.repository.impl.ArticleRepositoryImpl;
import org.b3log.solo.repository.impl.TagArticleRepositoryImpl;
import org.b3log.solo.repository.impl.TagRepositoryImpl;
import org.b3log.solo.service.ArticleMgmtService;
import org.b3log.solo.service.CommentMgmtService;
import org.b3log.solo.service.PreferenceQueryService;
import org.b3log.solo.util.ArchiveDates;
import org.b3log.solo.util.Articles;
import org.b3log.solo.util.Statistics;
import org.b3log.solo.util.Tags;
import org.b3log.solo.util.Users;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Article service for JavaScript client.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.1.0.2, Oct 18, 2011
 * @since 0.3.1
 */
public final class ArticleService extends AbstractGAEJSONRpcService {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(ArticleService.class.getName());
    /**
     * Article management service.
     */
    private ArticleMgmtService articleMgmtService = ArticleMgmtService.
            getInstance();
    /**
     * Comment management service.
     */
    private CommentMgmtService commentMgmtService =
            CommentMgmtService.getInstance();
    /**
     * Article repository.
     */
    private static ArticleRepository articleRepository =
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
     * Event manager.
     */
    private static EventManager eventManager = EventManager.getInstance();
    /**
     * Tag utilities.
     */
    private static Tags tagUtils = Tags.getInstance();
    /**
     * Article utilities.
     */
    private static Articles articleUtils = Articles.getInstance();
    /**
     * Statistic utilities.
     */
    private static Statistics statistics = Statistics.getInstance();
    /**
     * Archive date utilities.
     */
    private static ArchiveDates archiveDateUtils = ArchiveDates.getInstance();
    /**
     * Preference query service.
     */
    private PreferenceQueryService preferenceQueryService =
            PreferenceQueryService.getInstance();
    /**
     * User utilities.
     */
    private static Users userUtils = Users.getInstance();

    /**
     * Gets an article by the specified request json object.
     *
     * @param requestJSONObject the specified request json object, for example,
     * <pre>
     * {
     *     "oId": ""
     * }
     * </pre>
     * @param request the specified http servlet request
     * @param response the specified http servlet response
     * @return for example,
     * <pre>
     * {
     *     "oId": "",
     *     "articleTitle": "",
     *     "articleAbstract": "",
     *     "articleContent": "",
     *     "articlePermalink": "",
     *     "articleHadBeenPublished": boolean,
     *     "articleTags": [{
     *         "oId": "",
     *         "tagTitle": ""
     *     }, ....],
     *     "articleSign_oId": "",
     *     "signs": [{
     *         "oId": "",
     *         "signHTML": ""
     *     }, ....]
     *     "sc": "GET_ARTICLE_SUCC"
     * }
     * </pre>
     * @throws ActionException action exception
     * @throws IOException io exception
     */
    public JSONObject getArticle(final JSONObject requestJSONObject,
                                 final HttpServletRequest request,
                                 final HttpServletResponse response)
            throws ActionException, IOException {
        final JSONObject ret = new JSONObject();
        if (!userUtils.isLoggedIn(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return ret;
        }

        try {
            final String articleId = requestJSONObject.getString(Keys.OBJECT_ID);
            final JSONObject article = articleRepository.get(articleId);
            ret.put(ARTICLE, article);

            final JSONArray tags = new JSONArray();
            final List<JSONObject> tagArticleRelations =
                    tagArticleRepository.getByArticleId(articleId);
            for (int i = 0; i < tagArticleRelations.size(); i++) {
                final JSONObject tagArticleRelation =
                        tagArticleRelations.get(i);
                final String tagId = tagArticleRelation.getString(
                        Tag.TAG + "_" + Keys.OBJECT_ID);
                final JSONObject tag = tagRepository.get(tagId);

                tags.put(tag);
            }
            article.put(ARTICLE_TAGS_REF, tags);

            final JSONObject preference = preferenceQueryService.getPreference();
            final String signId = articleUtils.getSign(
                    articleId, preference).getString(Keys.OBJECT_ID);
            article.put(ARTICLE_SIGN_REF + "_" + Keys.OBJECT_ID, signId);

            final JSONArray signs =
                    new JSONArray(preference.getString(Preference.SIGNS));
            article.put(Sign.SIGNS, signs);

            // Remove unused properties
            article.remove(ARTICLE_AUTHOR_EMAIL);
            article.remove(ARTICLE_COMMENT_COUNT);
            article.remove(ARTICLE_CREATE_DATE);
            article.remove(ARTICLE_IS_PUBLISHED);
            article.remove(ARTICLE_PUT_TOP);
            article.remove(ARTICLE_UPDATE_DATE);
            article.remove(ARTICLE_VIEW_COUNT);
            article.remove(ARTICLE_RANDOM_DOUBLE);

            ret.put(Keys.STATUS_CODE, StatusCodes.GET_ARTICLE_SUCC);

            LOGGER.log(Level.FINER, "Got an article[oId={0}]", articleId);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new ActionException(e);
        }

        return ret;
    }

    /**
     * Gets articles(by crate date descending) by the specified request json
     * object.
     *
     * <p>
     * If the property "articleIsPublished" of the specified request json object
     * is {@code true}, the returned articles all are unpublished, {@code false}
     * otherwise.
     * </p>
     * 
     * @param requestJSONObject the specified request json object, for example,
     * <pre>
     * {
     *     "paginationCurrentPageNum": 1,
     *     "paginationPageSize": 20,
     *     "paginationWindowSize": 10,
     *     "articleIsPublished": boolean
     * }, see {@link Pagination} for more details
     * </pre>
     * @param request the specified http servlet request
     * @param response the specified http servlet response
     * @return for example,
     * <pre>
     * {
     *     "pagination": {
     *         "paginationPageCount": 100,
     *         "paginationPageNums": [1, 2, 3, 4, 5]
     *     },
     *     "articles": [{
     *         "oId": "",
     *         "articleTitle": "",
     *         "articleCommentCount": int,
     *         "articleCreateDate"; java.util.Date,
     *         "articleViewCount": int,
     *         "articleTags": "tag1, tag2, ....",
     *         "articlePutTop": boolean,
     *         "articleIsPublished": boolean
     *      }, ....]
     *     "sc": "GET_ARTICLES_SUCC"
     * }
     * </pre>, order by article update date and sticky(put top).
     * @throws ActionException action exception
     * @throws IOException io exception
     * @see Pagination
     */
    public JSONObject getArticles(final JSONObject requestJSONObject,
                                  final HttpServletRequest request,
                                  final HttpServletResponse response)
            throws ActionException, IOException {
        final JSONObject ret = new JSONObject();
        if (!userUtils.isLoggedIn(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return ret;
        }

        try {
            final int currentPageNum = requestJSONObject.getInt(
                    Pagination.PAGINATION_CURRENT_PAGE_NUM);
            final int pageSize = requestJSONObject.getInt(
                    Pagination.PAGINATION_PAGE_SIZE);
            final int windowSize = requestJSONObject.getInt(
                    Pagination.PAGINATION_WINDOW_SIZE);
            final boolean articleIsPublished =
                    requestJSONObject.optBoolean(ARTICLE_IS_PUBLISHED, true);

            final Query query = new Query().setCurrentPageNum(currentPageNum).
                    setPageSize(pageSize).
                    addSort(ARTICLE_PUT_TOP, SortDirection.DESCENDING).
                    addSort(ARTICLE_CREATE_DATE, SortDirection.DESCENDING).
                    addFilter(ARTICLE_IS_PUBLISHED,
                              FilterOperator.EQUAL,
                              articleIsPublished);
            final JSONObject result = articleRepository.get(query);

            final int pageCount = result.getJSONObject(Pagination.PAGINATION).
                    getInt(Pagination.PAGINATION_PAGE_COUNT);

            final JSONObject pagination = new JSONObject();
            ret.put(Pagination.PAGINATION, pagination);
            final List<Integer> pageNums =
                    Paginator.paginate(currentPageNum, pageSize, pageCount,
                                       windowSize);
            pagination.put(Pagination.PAGINATION_PAGE_COUNT, pageCount);
            pagination.put(Pagination.PAGINATION_PAGE_NUMS, pageNums);

            final JSONArray articles = result.getJSONArray(Keys.RESULTS);

            for (int i = 0; i < articles.length(); i++) {
                final JSONObject article = articles.getJSONObject(i);
                final JSONObject author = articleUtils.getAuthor(article);
                final String authorName = author.getString(User.USER_NAME);
                article.put(Common.AUTHOR_NAME, authorName);

                // Remove unused properties
                article.remove(ARTICLE_CONTENT);
                article.remove(ARTICLE_ABSTRACT);
                article.remove(ARTICLE_UPDATE_DATE);
                article.remove(ARTICLE_AUTHOR_EMAIL);
                article.remove(ARTICLE_HAD_BEEN_PUBLISHED);
                article.remove(ARTICLE_IS_PUBLISHED);
                article.remove(ARTICLE_RANDOM_DOUBLE);
            }
            ret.put(ARTICLES, articles);

            ret.put(Keys.STATUS_CODE, StatusCodes.GET_ARTICLES_SUCC);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new ActionException(e);
        }

        return ret;
    }

    /**
     * Removes an article by the specified request json object.
     *
     * @param requestJSONObject the specified request json object, for example,
     * <pre>
     * {
     *     "oId": "",
     * }
     * </pre>
     * @param request the specified http servlet request
     * @param response the specified http servlet response
     * @return for example,
     * <pre>
     * {
     *     "status": {
     *         "code": "REMOVE_ARTICLE_SUCC",
     *         "events": { // optional
     *             "blogSyncCSDNBlog": {
     *                 "code": "",
     *                 "msg": "" // optional
     *             },
     *             ....
     *         }
     *     }
     * }
     * </pre>
     * @throws ActionException action exception
     * @throws IOException io exception
     */
    public JSONObject removeArticle(final JSONObject requestJSONObject,
                                    final HttpServletRequest request,
                                    final HttpServletResponse response)
            throws ActionException, IOException {
        final JSONObject ret = new JSONObject();
        if (!userUtils.isLoggedIn(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return ret;
        }

        final Transaction transaction = articleRepository.beginTransaction();
        try {
            final String articleId = requestJSONObject.getString(Keys.OBJECT_ID);
            if (!userUtils.canAccessArticle(articleId, request)) {
                ret.put(Keys.STATUS_CODE,
                        StatusCodes.REMOVE_ARTICLE_FAIL_FORBIDDEN);

                return ret;
            }

            articleMgmtService.removeArticle(articleId);

            final JSONObject eventData = new JSONObject();
            eventData.put(Keys.OBJECT_ID, articleId);
            try {
                eventManager.fireEventSynchronously(
                        new Event<JSONObject>(EventTypes.REMOVE_ARTICLE,
                                              eventData));
            } catch (final EventException e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }

            transaction.commit();

            ret.put(Keys.STATUS_CODE, StatusCodes.REMOVE_ARTICLE_SUCC);
            LOGGER.log(Level.FINER, "Removed an article[oId={0}]", articleId);
        } catch (final Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new ActionException(e);
        }

        return ret;
    }

    /**
     * Puts an article to top by the specified request json object.
     *
     * @param requestJSONObject the specified request json object, for example,
     * <pre>
     * {
     *     "oId": "",
     * }
     * </pre>
     * @param request the specified http servlet request
     * @param response the specified http servlet response
     * @return for example,
     * <pre>
     * {
     *     "sc": "PUT_TOP_ARTICLE_SUCC"
     * }
     * </pre>
     * @throws ActionException action exception
     * @throws IOException io exception
     */
    public JSONObject putTopArticle(final JSONObject requestJSONObject,
                                    final HttpServletRequest request,
                                    final HttpServletResponse response)
            throws ActionException, IOException {
        final JSONObject ret = new JSONObject();
        if (!userUtils.isAdminLoggedIn(request)) {
            try {
                ret.put(Keys.STATUS_CODE,
                        StatusCodes.PUT_TOP_ARTICLE_FAIL_FORBIDDEN);
            } catch (final JSONException e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }

            return ret;
        }
        final Transaction transaction =
                articleRepository.beginTransaction();
        String articleId = null;
        try {
            articleId = requestJSONObject.getString(Keys.OBJECT_ID);
            final JSONObject topArticle = articleRepository.get(articleId);
            topArticle.put(ARTICLE_PUT_TOP, true);
            articleRepository.update(articleId, topArticle);
            transaction.commit();

            ret.put(Keys.STATUS_CODE, StatusCodes.PUT_TOP_ARTICLE_SUCC);
        } catch (final Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            LOGGER.log(Level.SEVERE, "Can't put the article[oId{0}] to top",
                       articleId);
            try {
                ret.put(Keys.STATUS_CODE, StatusCodes.PUT_TOP_ARTICLE_FAIL_);
            } catch (final JSONException ex) {
                LOGGER.severe(ex.getMessage());
                throw new ActionException(e);
            }
        }

        return ret;
    }

    /**
     * Cancels an article from top by the specified request json object.
     *
     * @param requestJSONObject the specified request json object, for example,
     * <pre>
     * {
     *     "oId": "",
     * }
     * </pre>
     * @param request the specified http servlet request
     * @param response the specified http servlet response
     * @return for example,
     * <pre>
     * {
     *     "sc": "CANCEL_TOP_ARTICLE_SUCC"
     * }
     * </pre>
     * @throws ActionException action exception
     * @throws IOException io exception
     */
    public JSONObject cancelTopArticle(final JSONObject requestJSONObject,
                                       final HttpServletRequest request,
                                       final HttpServletResponse response)
            throws ActionException, IOException {
        final JSONObject ret = new JSONObject();
        if (!userUtils.isAdminLoggedIn(request)) {
            try {
                ret.put(Keys.STATUS_CODE,
                        StatusCodes.CANCEL_TOP_ARTICLE_FAIL_FORBIDDEN);
            } catch (final JSONException e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
            return ret;
        }
        final Transaction transaction = articleRepository.beginTransaction();
        String articleId = null;
        try {
            articleId = requestJSONObject.getString(Keys.OBJECT_ID);
            final JSONObject topArticle = articleRepository.get(articleId);
            topArticle.put(ARTICLE_PUT_TOP, false);
            articleRepository.update(articleId, topArticle);
            transaction.commit();

            ret.put(Keys.STATUS_CODE, StatusCodes.CANCEL_TOP_ARTICLE_SUCC);
        } catch (final Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            LOGGER.log(Level.SEVERE,
                       "Can't cancel the article[oId{0}] from top",
                       articleId);
            try {
                ret.put(Keys.STATUS_CODE, StatusCodes.CANCEL_TOP_ARTICLE_FAIL_);
            } catch (final JSONException ex) {
                LOGGER.severe(ex.getMessage());
                throw new ActionException(e);
            }
        }

        return ret;
    }

    /**
     * Cancels publish an article by the specified request json object.
     *
     * @param requestJSONObject the specified request json object, for example,
     * <pre>
     * {
     *     "oId": "",
     * }
     * </pre>
     * @param request the specified http servlet request
     * @param response the specified http servlet response
     * @return for example,
     * <pre>
     * {
     *   "sc": "CANCEL_PUBLISH_ARTICLE_SUCC"
     * }
     * </pre>
     * @throws ActionException action exception
     * @throws IOException io exception
     */
    public JSONObject cancelPublishArticle(final JSONObject requestJSONObject,
                                           final HttpServletRequest request,
                                           final HttpServletResponse response)
            throws ActionException, IOException {
        final JSONObject ret = new JSONObject();
        if (!userUtils.isLoggedIn(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return ret;
        }

        final Transaction transaction =
                articleRepository.beginTransaction();
        try {
            final String articleId =
                    requestJSONObject.getString(Keys.OBJECT_ID);

            if (!userUtils.canAccessArticle(articleId, request)) {
                ret.put(Keys.STATUS_CODE,
                        StatusCodes.CANCEL_PUBLISH_ARTICLE_FAIL_FORBIDDEN);

                return ret;
            }

            final JSONObject article = articleRepository.get(articleId);
            article.put(ARTICLE_IS_PUBLISHED, false);
            tagUtils.decTagPublishedRefCount(articleId);
            archiveDateUtils.decArchiveDatePublishedRefCount(articleId);
            articleRepository.update(articleId, article);
            statistics.decPublishedBlogArticleCount();
            final int blogCmtCnt =
                    statistics.getPublishedBlogCommentCount();
            final int articleCmtCnt =
                    article.getInt(ARTICLE_COMMENT_COUNT);
            statistics.setPublishedBlogCommentCount(
                    blogCmtCnt - articleCmtCnt);

            transaction.commit();

            ret.put(Keys.STATUS_CODE, StatusCodes.CANCEL_PUBLISH_ARTICLE_SUCC);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            if (transaction.isActive()) {
                transaction.rollback();
            }

            try {
                ret.put(Keys.STATUS_CODE,
                        StatusCodes.CANCEL_PUBLISH_ARTICLE_FAIL_);
            } catch (final JSONException ex) {
                LOGGER.severe(ex.getMessage());
                throw new ActionException(e);
            }

            return ret;
        }

        return ret;
    }

    /**
     * Gets the {@link ArticleService} singleton.
     *
     * @return the singleton
     */
    public static ArticleService getInstance() {
        return SingletonHolder.SINGLETON;
    }

    /**
     * Private default constructor.
     */
    private ArticleService() {
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
        private static final ArticleService SINGLETON = new ArticleService();

        /**
         * Private default constructor.
         */
        private SingletonHolder() {
        }
    }
}
