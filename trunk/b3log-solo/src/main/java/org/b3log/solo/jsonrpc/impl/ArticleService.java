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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.solo.action.StatusCodes;
import org.b3log.solo.event.EventTypes;
import static org.b3log.solo.model.Article.*;
import org.b3log.solo.model.Tag;
import org.b3log.solo.repository.ArticleRepository;
import org.b3log.solo.repository.TagArticleRepository;
import org.b3log.solo.repository.TagRepository;
import org.b3log.latke.Keys;
import org.b3log.latke.action.ActionException;
import org.b3log.latke.action.util.PageCaches;
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
import org.b3log.latke.util.Strings;
import org.b3log.solo.jsonrpc.AbstractGAEJSONRpcService;
import org.b3log.solo.model.Common;
import org.b3log.solo.model.Preference;
import org.b3log.solo.model.Sign;
import org.b3log.solo.repository.ArticleSignRepository;
import org.b3log.solo.repository.impl.ArticleGAERepository;
import org.b3log.solo.repository.impl.ArticleSignGAERepository;
import org.b3log.solo.repository.impl.TagArticleGAERepository;
import org.b3log.solo.repository.impl.TagGAERepository;
import org.b3log.solo.util.ArchiveDates;
import org.b3log.solo.util.Articles;
import org.b3log.solo.util.Permalinks;
import org.b3log.solo.util.Preferences;
import org.b3log.solo.util.Statistics;
import org.b3log.solo.util.Tags;
import org.b3log.solo.util.TimeZones;
import org.b3log.solo.util.Users;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Article service for JavaScript client.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.3.4, Jan 20, 2011
 */
public final class ArticleService extends AbstractGAEJSONRpcService {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(ArticleService.class.getName());
    /**
     * Article repository.
     */
    private ArticleRepository articleRepository =
            ArticleGAERepository.getInstance();
    /**
     * Tag repository.
     */
    private TagRepository tagRepository = TagGAERepository.getInstance();
    /**
     * Tag-Article repository.
     */
    private TagArticleRepository tagArticleRepository =
            TagArticleGAERepository.getInstance();
    /**
     * Article-Sign repository.
     */
    private ArticleSignRepository articleSignRepository =
            ArticleSignGAERepository.getInstance();
    /**
     * Event manager.
     */
    private EventManager eventManager = EventManager.getInstance();
    /**
     * Tag utilities.
     */
    private Tags tagUtils = Tags.getInstance();
    /**
     * Article utilities.
     */
    private Articles articleUtils = Articles.getInstance();
    /**
     * Statistic utilities.
     */
    private Statistics statistics = Statistics.getInstance();
    /**
     * Archive date utilities.
     */
    private ArchiveDates archiveDateUtils = ArchiveDates.getInstance();
    /**
     * Preference utilities.
     */
    private Preferences preferenceUtils = Preferences.getInstance();
    /**
     * Permalink utilities.
     */
    private Permalinks permalinks = Permalinks.getInstance();
    /**
     * User utilities.
     */
    private Users userUtils = Users.getInstance();
    /**
     * Time zone utilities.
     */
    private TimeZones timeZoneUtils = TimeZones.getInstance();
    /**
     * Permalink date format(yyyy/MM/dd).
     */
    public static final DateFormat PERMALINK_FORMAT =
            new SimpleDateFormat("yyyy/MM/dd");

    /**
     * Adds an article from the specified request json object and http servlet
     * request.
     *
     * @param requestJSONObject the specified request json object, for example,
     * <pre>
     * {
     *     "article": {
     *         "articleTitle": "",
     *         "articleAbstract": "",
     *         "articleContent": "",
     *         "articleTags": "tag1,tag2,tag3",
     *         "articlePermalink": "", // optional
     *         "articleIsPublished": boolean,
     *         "articleSign_oId": "" // optional
     *     }
     * }
     * </pre>
     * @param request the specified http servlet request
     * @param response the specified http servlet response
     * @return for example,
     * <pre>
     * {
     *     "oId": generatedArticleId
     *     "status": {
     *         "code": "ADD_ARTICLE_SUCC",
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
    public JSONObject addArticle(final JSONObject requestJSONObject,
                                 final HttpServletRequest request,
                                 final HttpServletResponse response)
            throws ActionException, IOException {
        final JSONObject ret = new JSONObject();

        if (!userUtils.isLoggedIn()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return ret;
        }

        final Transaction transaction = articleRepository.beginTransaction();
        final JSONObject status = new JSONObject();
        try {
            ret.put(Keys.STATUS, status);
            if (userUtils.isCollaborateAdmin()) {
                status.put(Keys.CODE, StatusCodes.UPDATE_ARTICLE_FAIL_FORBIDDEN);

                return ret;
            }

            final JSONObject article =
                    requestJSONObject.getJSONObject(ARTICLE);
            // Step 1: Add tags
            final String tagsString =
                    article.getString(ARTICLE_TAGS_REF);
            final String[] tagTitles = tagsString.split(",");
            final JSONArray tags = tagUtils.tag(tagTitles, article);
            // Step 2; Set comment/view count to 0
            article.put(ARTICLE_COMMENT_COUNT, 0);
            article.put(ARTICLE_VIEW_COUNT, 0);
            // Step 3: Set create/updat date
            final JSONObject preference = preferenceUtils.getPreference();
            final String timeZoneId =
                    preference.getString(Preference.TIME_ZONE_ID);
            final Date date = timeZoneUtils.getTime(timeZoneId);
            article.put(ARTICLE_UPDATE_DATE, date);
            article.put(ARTICLE_CREATE_DATE, date);
            // Step 4: Set put top to false
            article.put(ARTICLE_PUT_TOP, false);
            // Step 5: Add article
            final String articleId = articleRepository.add(article);
            ret.put(Keys.OBJECT_ID, articleId);
            // Step 6: Add tag-article relations
            articleUtils.addTagArticleRelation(tags, article);
            // Step 7: Inc blog article count statictis
            statistics.incBlogArticleCount();
            if (article.getBoolean(ARTICLE_IS_PUBLISHED)) {
                statistics.incPublishedBlogArticleCount();
            }
            // Step 8: Add archive date-article relations
            archiveDateUtils.archiveDate(article);
            // Step 9: Set permalink
            String permalink = article.optString(ARTICLE_PERMALINK);
            if (Strings.isEmptyOrNull(permalink)) {
                permalink = "/articles/" + PERMALINK_FORMAT.format(date) + "/"
                            + articleId + ".html";
            }

            if (!permalink.startsWith("/")) {
                permalink = "/" + permalink;
            }

            if (permalinks.exist(permalink)) {
                status.put(Keys.CODE,
                           StatusCodes.ADD_ARTICLE_FAIL_DUPLICATED_PERMALINK);

                throw new Exception("Add article fail, caused by duplicated permalink["
                                    + permalink + "]");
            }
            article.put(ARTICLE_PERMALINK, permalink);
            // Step 10: Add article-sign relation
            final String signId =
                    article.getString(ARTICLE_SIGN_REF + "_" + Keys.OBJECT_ID);
            articleUtils.addArticleSignRelation(signId, articleId);
            article.remove(ARTICLE_SIGN_REF + "_" + Keys.OBJECT_ID);
            // Step 11: Set had been published status
            article.put(ARTICLE_HAD_BEEN_PUBLISHED, false);
            if (article.getBoolean(ARTICLE_IS_PUBLISHED)) {
                // Publish it directly
                article.put(ARTICLE_HAD_BEEN_PUBLISHED, true);
            }
            // Step 12: Set author email
            final String authorEmail =
                    userUtils.getCurrentUser().getString(User.USER_EMAIL);
            article.put(ARTICLE_AUTHOR_EMAIL, authorEmail);
            // Step 13: Set random double
            article.put(ARTICLE_RANDOM_DOUBLE, Math.random());
            // Step 14: Update article
            articleRepository.update(articleId, article);

            if (article.getBoolean(ARTICLE_IS_PUBLISHED)) {
                // Fire add article event
                final JSONObject eventData = new JSONObject();
                eventData.put(ARTICLE, article);
                eventData.put(Keys.RESULTS, ret);
                eventManager.fireEventSynchronously(
                        new Event<JSONObject>(EventTypes.ADD_ARTICLE, eventData));
            }

            transaction.commit();

            status.put(Keys.CODE, StatusCodes.ADD_ARTICLE_SUCC);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            if (transaction.isActive()) {
                transaction.rollback();
            }

            return ret;
        }

        PageCaches.removeAll();

        return ret;
    }

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
     *     "articleIsPublished": boolean
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
        if (!userUtils.isLoggedIn()) {
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

            final JSONObject preference = preferenceUtils.getPreference();
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
            article.remove(ARTICLE_HAD_BEEN_PUBLISHED);
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
        if (!userUtils.isLoggedIn()) {
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
                    addSort(ARTICLE_CREATE_DATE, SortDirection.DESCENDING).
                    addSort(ARTICLE_PUT_TOP, SortDirection.DESCENDING).
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
        if (!userUtils.isLoggedIn()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return ret;
        }

        final Transaction transaction = articleRepository.beginTransaction();
        try {
            final JSONObject status = new JSONObject();
            ret.put(Keys.STATUS, status);

            final String articleId = requestJSONObject.getString(Keys.OBJECT_ID);
            if (!userUtils.canAccessArticle(articleId)) {
                status.put(Keys.CODE, StatusCodes.REMOVE_ARTICLE_FAIL_FORBIDDEN);

                return ret;
            }

            LOGGER.log(Level.FINER, "Removing an article[oId={0}]", articleId);
            tagUtils.decTagRefCount(articleId);
            archiveDateUtils.unArchiveDate(articleId);
            articleUtils.removeTagArticleRelations(articleId);
            articleUtils.removeArticleComments(articleId);
            final JSONObject article = articleRepository.get(articleId);
            articleRepository.remove(articleId);
            statistics.decBlogArticleCount();
            if (article.getBoolean(ARTICLE_IS_PUBLISHED)) {
                statistics.decPublishedBlogArticleCount();
            }
            archiveDateUtils.archiveDate(article);
            final JSONObject eventData = new JSONObject();
            eventData.put(Keys.OBJECT_ID, articleId);
            eventData.put(Keys.RESULTS, ret);
            try {
                eventManager.fireEventSynchronously(
                        new Event<JSONObject>(EventTypes.REMOVE_ARTICLE,
                                              eventData));
            } catch (final EventException e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }

            transaction.commit();

            status.put(Keys.CODE, StatusCodes.REMOVE_ARTICLE_SUCC);
            LOGGER.log(Level.FINER, "Removed an article[oId={0}]", articleId);
        } catch (final Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new ActionException(e);
        }

        PageCaches.removeAll();

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
        if (!userUtils.isAdminLoggedIn()) {
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

        PageCaches.removeAll();

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
        if (!userUtils.isAdminLoggedIn()) {
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

        PageCaches.removeAll();

        return ret;
    }

    /**
     * Updates an article by the specified request json object.
     *
     * @param requestJSONObject the specified request json object, for example,
     * <pre>
     * {
     *     "article": {
     *         "oId": "",
     *         "articleTitle": "",
     *         "articleAbstract": "",
     *         "articleContent": "",
     *         "articleTags": "tag1,tag2,tag3",
     *         "articlePermalink": "", // optional
     *         "articleIsPublished": boolean,
     *         "articleSign_oId": "" // optional
     *     }
     * }
     * </pre>
     * @param request the specified http servlet request
     * @param response the specified http servlet response
     * @return for example,
     * <pre>
     * {
     *     "status": {
     *         "code": "UPDATE_ARTICLE_SUCC",
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
    public JSONObject updateArticle(final JSONObject requestJSONObject,
                                    final HttpServletRequest request,
                                    final HttpServletResponse response)
            throws ActionException, IOException {
        final JSONObject ret = new JSONObject();

        if (!userUtils.isLoggedIn()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return ret;
        }

        final Transaction transaction = articleRepository.beginTransaction();

        final JSONObject status = new JSONObject();
        try {
            ret.put(Keys.STATUS, status);

            if (userUtils.isCollaborateAdmin()) {
                status.put(Keys.CODE, StatusCodes.UPDATE_ARTICLE_FAIL_FORBIDDEN);

                return ret;
            }

            final JSONObject article = requestJSONObject.getJSONObject(ARTICLE);
            final String articleId = article.getString(Keys.OBJECT_ID);
            if (!userUtils.canAccessArticle(articleId)) {
                status.put(Keys.CODE, StatusCodes.UPDATE_ARTICLE_FAIL_FORBIDDEN);

                return ret;
            }
            // Set permalink
            final JSONObject oldArticle = articleRepository.get(articleId);
            final String permalink = getPermalinkForUpdateArticle(
                    oldArticle, article,
                    (Date) oldArticle.get(ARTICLE_CREATE_DATE), status);
            article.put(ARTICLE_PERMALINK, permalink);
            // Process tag
            tagUtils.processTagsForArticleUpdate(oldArticle, article);
            // Fill auto properties
            fillAutoProperties(oldArticle, article);
            // Set date
            article.put(ARTICLE_UPDATE_DATE, oldArticle.get(ARTICLE_UPDATE_DATE));
            final JSONObject preference = preferenceUtils.getPreference();
            final String timeZoneId =
                    preference.getString(Preference.TIME_ZONE_ID);
            final Date date = timeZoneUtils.getTime(timeZoneId);
            if (article.getBoolean(ARTICLE_IS_PUBLISHED)) { // Publish it
                if (articleUtils.hadBeenPublished(oldArticle)) {
                    // Edit update date only for published article
                    article.put(ARTICLE_UPDATE_DATE, date);
                } else { // This article is a draft and this is the first time to publish it
                    article.put(ARTICLE_CREATE_DATE, date);
                    article.put(ARTICLE_UPDATE_DATE, date);
                    article.put(ARTICLE_HAD_BEEN_PUBLISHED, true);
                }
            } else { // Save as draft
                if (articleUtils.hadBeenPublished(oldArticle)) {
                    // Save update date only for published article
                    article.put(ARTICLE_UPDATE_DATE, date);
                } else {
                    // Reset create/update date to indicate this is an new draft
                    article.put(ARTICLE_CREATE_DATE, date);
                    article.put(ARTICLE_UPDATE_DATE, date);
                }
            }
            // Set statistic
            if (article.getBoolean(ARTICLE_IS_PUBLISHED)) {
                if (!oldArticle.getBoolean(ARTICLE_IS_PUBLISHED)) {
                    // This article is updated from unpublished to published
                    statistics.incPublishedBlogArticleCount();
                    final int blogCmtCnt =
                            statistics.getPublishedBlogCommentCount();
                    final int articleCmtCnt =
                            article.getInt(ARTICLE_COMMENT_COUNT);
                    statistics.setPublishedBlogCommentCount(
                            blogCmtCnt + articleCmtCnt);
                }
            }
            // Add article-sign relation
            final String signId =
                    article.getString(ARTICLE_SIGN_REF + "_" + Keys.OBJECT_ID);
            final JSONObject articleSignRelation =
                    articleSignRepository.getByArticleId(articleId);
            if (null != articleSignRelation) {
                articleSignRepository.remove(
                        articleSignRelation.getString(Keys.OBJECT_ID));
            }
            articleUtils.addArticleSignRelation(signId, articleId);
            article.remove(ARTICLE_SIGN_REF + "_" + Keys.OBJECT_ID);
            if (!oldArticle.getBoolean(ARTICLE_IS_PUBLISHED)
                && article.getBoolean(ARTICLE_IS_PUBLISHED)) {
                archiveDateUtils.incArchiveDatePublishedRefCount(articleId);
            }
            // Update
            articleRepository.update(articleId, article);
            if (article.getBoolean(ARTICLE_IS_PUBLISHED)) {
                // Fire update article event
                final JSONObject eventData = new JSONObject();
                eventData.put(ARTICLE, article);
                eventData.put(Keys.RESULTS, ret);
                try {
                    eventManager.fireEventSynchronously(
                            new Event<JSONObject>(EventTypes.UPDATE_ARTICLE,
                                                  eventData));
                } catch (final EventException e) {
                    LOGGER.log(Level.SEVERE, e.getMessage(), e);
                }
            }
            if (!articleUtils.hadBeenPublished(article)) {
                // Fire add article event
                final JSONObject eventData = new JSONObject();
                eventData.put(ARTICLE, article);
                eventData.put(Keys.RESULTS, ret);
                try {
                    eventManager.fireEventSynchronously(
                            new Event<JSONObject>(EventTypes.ADD_ARTICLE,
                                                  eventData));
                } catch (final EventException e) {
                    LOGGER.log(Level.SEVERE, e.getMessage(), e);
                }
            }

            transaction.commit();

            status.put(Keys.CODE, StatusCodes.UPDATE_ARTICLE_SUCC);
            ret.put(Keys.STATUS, status);
            LOGGER.log(Level.FINER, "Updated an article[oId={0}]", articleId);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);

            if (transaction.isActive()) {
                transaction.rollback();
            }

            return ret;
        }

        PageCaches.removeAll();

        return ret;
    }

    /**
     * Fills 'auto' properties for the specified article and old article.
     *
     * <p>
     * Some properties of an article are not been changed while article updating,
     * these properties are called 'auto' properties.
     * </p>
     *
     * <p>
     * The property(named {@value org.b3log.solo.model.Article#ARTICLE_RANDOM_DOUBLE})
     * of the specified article will be regenerated.
     * </p>
     *
     * @param oldArticle the specified old article
     * @param article the specified article
     * @throws JSONException json exception
     */
    private void fillAutoProperties(final JSONObject oldArticle,
                                    final JSONObject article) throws
            JSONException {

        final Date createDate =
                (Date) oldArticle.get(ARTICLE_CREATE_DATE);
        article.put(ARTICLE_CREATE_DATE, createDate);
        article.put(ARTICLE_COMMENT_COUNT,
                    oldArticle.getInt(ARTICLE_COMMENT_COUNT));
        article.put(ARTICLE_VIEW_COUNT, oldArticle.getInt(ARTICLE_VIEW_COUNT));
        article.put(ARTICLE_PUT_TOP, oldArticle.getBoolean(ARTICLE_PUT_TOP));
        article.put(ARTICLE_HAD_BEEN_PUBLISHED,
                    oldArticle.getBoolean(ARTICLE_HAD_BEEN_PUBLISHED));
        article.put(ARTICLE_AUTHOR_EMAIL,
                    oldArticle.getString(ARTICLE_AUTHOR_EMAIL));
        article.put(ARTICLE_RANDOM_DOUBLE, Math.random());
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
        if (!userUtils.isLoggedIn()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return ret;
        }

        final Transaction transaction =
                articleRepository.beginTransaction();
        try {
            final String articleId =
                    requestJSONObject.getString(Keys.OBJECT_ID);

            if (!userUtils.canAccessArticle(articleId)) {
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

        PageCaches.removeAll();

        return ret;
    }

    /**
     * Gets article permalink for updating article with the specified old article,
     * article, create date and status.
     *
     * @param oldArticle the specified old article
     * @param article the specified article
     * @param createDate the specified create date
     * @param status the specified status
     * @return permalink
     * @throws Exception if duplicated permalink occurs
     */
    private String getPermalinkForUpdateArticle(final JSONObject oldArticle,
                                                final JSONObject article,
                                                final Date createDate,
                                                final JSONObject status)
            throws Exception {
        final String articleId = article.getString(Keys.OBJECT_ID);
        String ret = article.optString(ARTICLE_PERMALINK).trim();
        final String oldPermalink = oldArticle.getString(ARTICLE_PERMALINK);
        if (!oldPermalink.equals(ret)) {
            if (Strings.isEmptyOrNull(ret)) {
                ret = "/articles/" + PERMALINK_FORMAT.format(
                        createDate) + "/" + articleId + ".html";
            }

            if (!ret.startsWith("/")) {
                ret = "/" + ret;
            }

            if (!oldPermalink.equals(ret)
                && permalinks.exist(ret)) {
                status.put(Keys.CODE,
                           StatusCodes.UPDATE_ARTICLE_FAIL_DUPLICATED_PERMALINK);

                throw new Exception("Update article fail, caused by duplicated permalink["
                                    + ret + "]");
            }
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
