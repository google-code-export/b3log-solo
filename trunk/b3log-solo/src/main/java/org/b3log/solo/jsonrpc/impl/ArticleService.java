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

package org.b3log.solo.jsonrpc.impl;

import com.google.inject.Inject;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import org.b3log.latke.repository.Filter;
import org.b3log.latke.repository.FilterOperator;
import org.b3log.latke.repository.SortDirection;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.util.Strings;
import org.b3log.solo.jsonrpc.AbstractGAEJSONRpcService;
import org.b3log.solo.model.Preference;
import org.b3log.solo.util.ArchiveDateUtils;
import org.b3log.solo.util.ArticleUtils;
import org.b3log.solo.util.Permalinks;
import org.b3log.solo.util.PreferenceUtils;
import org.b3log.solo.util.Statistics;
import org.b3log.solo.util.TagUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Article service for JavaScript client.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.2.4, Dec 8, 2010
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
    @Inject
    private ArticleRepository articleRepository;
    /**
     * Tag repository.
     */
    @Inject
    private TagRepository tagRepository;
    /**
     * Tag-Article repository.
     */
    @Inject
    private TagArticleRepository tagArticleRepository;
    /**
     * Event manager.
     */
    @Inject
    private EventManager eventManager;
    /**
     * Tag utilities.
     */
    @Inject
    private TagUtils tagUtils;
    /**
     * Article utilities.
     */
    @Inject
    private ArticleUtils articleUtils;
    /**
     * Statistic utilities.
     */
    @Inject
    private Statistics statistics;
    /**
     * Archive date utilities.
     */
    @Inject
    private ArchiveDateUtils archiveDateUtils;
    /**
     * Preference utilities.
     */
    @Inject
    private PreferenceUtils preferenceUtils;
    /**
     * Permalink utilities.
     */
    @Inject
    private Permalinks permalinks;
    /**
     * Permalink date format(yyyy/MM/dd).
     */
    public static final DateFormat PERMALINK_FORMAT =
            new SimpleDateFormat("yyyy/MM/dd");

    /**
     * Gets the random articles.
     *
     * @return a list of articles, returns an empty list if not found
     * @throws ActionException action exception
     */
    public List<JSONObject> getRandomArticles() throws ActionException {
        try {
            final JSONObject preference = preferenceUtils.getPreference();
            if (null == preference) {
                throw new ActionException("Not found preference");
            }

            final int displayCnt =
                    preference.getInt(Preference.RANDOM_ARTICLES_DISPLAY_CNT);
            final List<JSONObject> ret =
                    articleRepository.getRandomly(displayCnt);
            for (final JSONObject article : ret) {
                article.remove(Keys.OBJECT_ID);
                article.remove(ARTICLE_ABSTRACT);
                article.remove(ARTICLE_COMMENT_COUNT);
                article.remove(ARTICLE_CONTENT);
                article.remove(ARTICLE_CREATE_DATE);
                article.remove(ARTICLE_TAGS_REF);
                article.remove(ARTICLE_UPDATE_DATE);
                article.remove(ARTICLE_VIEW_COUNT);
            }

            return ret;
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new ActionException(e);
        }
    }

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
     *         "articlePermalink": "",
     *         "articleIsPublished": boolean
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
        if (!isLoggedIn()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return ret;
        }

        final Transaction transaction = articleRepository.beginTransaction();
        try {
            final JSONObject status = new JSONObject();
            ret.put(Keys.STATUS, status);

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
            final Date date = new Date();
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
            // Step 10: Set had been published status
            article.put(ARTICLE_HAD_BEEN_PUBLISHED, false);
            if (article.getBoolean(ARTICLE_IS_PUBLISHED)) {
                // Publish it directly
                article.put(ARTICLE_HAD_BEEN_PUBLISHED, true);
            }
            // Step 11: Update article
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
            transaction.rollback();

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
     *     "articleIsPublished": boolean
     *     "articleTags": [{
     *         "oId": "",
     *         "tagTitle": ""
     *     }, ....],
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
        if (!isLoggedIn()) {
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
            // XXX: Remove unused properties
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
        if (!isLoggedIn()) {
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

            final Map<String, SortDirection> sorts =
                    new HashMap<String, SortDirection>();
            sorts.put(ARTICLE_CREATE_DATE, SortDirection.DESCENDING);
            sorts.put(ARTICLE_PUT_TOP, SortDirection.DESCENDING);
            final Set<Filter> filters = new HashSet<Filter>();
            filters.add(new Filter(ARTICLE_IS_PUBLISHED,
                                   FilterOperator.EQUAL,
                                   articleIsPublished));
            final JSONObject result =
                    articleRepository.get(currentPageNum, pageSize,
                                          sorts, filters);

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
                // Remove unused properties
                article.remove(ARTICLE_CONTENT);
                article.remove(ARTICLE_ABSTRACT);
                article.remove(ARTICLE_UPDATE_DATE);
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
        if (!isLoggedIn()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return ret;
        }

        // TODO: check the article whether is the current user's

        final Transaction transaction =
                articleRepository.beginTransaction();
        Transaction transaction2 = null;
        try {
            final JSONObject status = new JSONObject();
            ret.put(Keys.STATUS, status);

            final String articleId = requestJSONObject.getString(Keys.OBJECT_ID);
            LOGGER.log(Level.FINER, "Removing an article[oId={0}]", articleId);
            // Step 1: Dec reference count of tag
            tagUtils.decTagRefCount(articleId);
            // Step 2: Remove tag-article relations
            articleUtils.removeTagArticleRelations(articleId);
            // Step 3: Remove related comments, article-comment relations,
            // set article/blog comment statistic count
            articleUtils.removeArticleComments(articleId);
            // XXX: GAE transaction isolation
            // http://code.google.com/intl/en/appengine/docs/java/datastore/transactions.html#Isolation_and_Consistency
            transaction.commit();
            transaction2 =
                    articleRepository.beginTransaction();
            // Step 4: Remove article
            articleRepository.remove(articleId);
            // Step 5: Dec blog article count statictis
            statistics.decBlogArticleCount();
            // Step 6: Un-archive date-article relations
            archiveDateUtils.unArchiveDate(articleId);
            // Step 7: Fire remove article event
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

            transaction2.commit();

            status.put(Keys.CODE, StatusCodes.REMOVE_ARTICLE_SUCC);

            LOGGER.log(Level.FINER, "Removed an article[oId={0}]", articleId);
        } catch (final Exception e) {
            if (null != transaction2 && transaction2.isActive()) {
                transaction2.rollback();
            }

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
        if (!isAdminLoggedIn()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
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
            transaction.rollback();
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
        if (!isAdminLoggedIn()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
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
            transaction.rollback();
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
     *         "articlePermalink": "",
     *         "articleIsPublished": boolean
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
        if (!isLoggedIn()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return ret;
        }
        // TODO: check the article whether is the current user's
        final Transaction transaction =
                articleRepository.beginTransaction();
        Transaction transaction2 = null;

        String articleId = null;
        try {
            final JSONObject status = new JSONObject();
            ret.put(Keys.STATUS, status);

            final JSONObject article =
                    requestJSONObject.getJSONObject(ARTICLE);
            articleId = article.getString(Keys.OBJECT_ID);

            // Step 1: Set permalink
            final JSONObject oldArticle = articleRepository.get(articleId);
            final String permalink = getPermalinkForUpdateArticle(
                    oldArticle, article,
                    (Date) oldArticle.get(ARTICLE_CREATE_DATE), status);
            article.put(ARTICLE_PERMALINK, permalink);
            // Step 2: Dec reference count of tag
            tagUtils.decTagRefCount(articleId);
            // Step 3: Un-archive date-article relations
            archiveDateUtils.unArchiveDate(articleId);
            // Step 4: Remove tag-article relations
            articleUtils.removeTagArticleRelations(articleId);
            // XXX: GAE transaction isolation
            // http://code.google.com/intl/en/appengine/docs/java/datastore/transactions.html#Isolation_and_Consistency
            transaction.commit();
            transaction2 =
                    articleRepository.beginTransaction();
            // Step 5: Add tags
            final String tagsString =
                    article.getString(ARTICLE_TAGS_REF);
            final String[] tagTitles = tagsString.split(",");
            final JSONArray tags = tagUtils.tag(tagTitles, article);
            // Step 6: Fill auto properties
            final Date createDate = (Date) oldArticle.get(ARTICLE_CREATE_DATE);
            article.put(ARTICLE_CREATE_DATE, createDate);
            article.put(ARTICLE_COMMENT_COUNT,
                        oldArticle.getInt(ARTICLE_COMMENT_COUNT));
            article.put(ARTICLE_VIEW_COUNT,
                        oldArticle.getInt(ARTICLE_VIEW_COUNT));
            article.put(ARTICLE_PUT_TOP,
                        oldArticle.getBoolean(ARTICLE_PUT_TOP));
            article.put(ARTICLE_HAD_BEEN_PUBLISHED,
                        oldArticle.getBoolean(ARTICLE_HAD_BEEN_PUBLISHED));
            // Step 7: Set updat date
            article.put(ARTICLE_UPDATE_DATE, oldArticle.get(ARTICLE_UPDATE_DATE));
            if (article.getBoolean(ARTICLE_IS_PUBLISHED)) { // Publish it
                if (articleUtils.hadBeenPublished(oldArticle)) {
                    // Edit update date only for published article
                    article.put(ARTICLE_UPDATE_DATE, new Date());
                } else { // This article is a draft and this is the first time to publish it
                    final Date date = new Date();
                    article.put(ARTICLE_CREATE_DATE, date);
                    article.put(ARTICLE_UPDATE_DATE, date);
                    article.put(ARTICLE_HAD_BEEN_PUBLISHED, true);
                }
            } else { // Save as draft
                if (articleUtils.hadBeenPublished(oldArticle)) {
                    // Save update date only for published article
                    article.put(ARTICLE_UPDATE_DATE, new Date());
                } else {
                    // Reset create/update date to indicate this is an new draft
                    final Date date = new Date();
                    article.put(ARTICLE_CREATE_DATE, date);
                    article.put(ARTICLE_UPDATE_DATE, date);
                }
            }
            // Step 8: Update
            articleRepository.update(articleId, article);
            // Step 9: Add tag-article relations
            articleUtils.addTagArticleRelation(tags, article);
            // Step 10: Add archive date-article relations
            archiveDateUtils.archiveDate(article);

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

            transaction2.commit();

            status.put(Keys.CODE, StatusCodes.UPDATE_ARTICLE_SUCC);
            ret.put(Keys.STATUS, status);
            LOGGER.log(Level.FINER, "Updated an article[oId={0}]", articleId);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);

            if (null != transaction2 && transaction2.isActive()) {
                transaction2.rollback();
            }

            if (transaction.isActive()) {
                transaction.rollback();
            }

            return ret;
        }

        PageCaches.removeAll();

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
        if (!isLoggedIn()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return ret;
        }
// TODO: check the article whether is the current user's
        final Transaction transaction =
                articleRepository.beginTransaction();
        try {
            final String articleId =
                    requestJSONObject.getString(Keys.OBJECT_ID);
            final JSONObject article = articleRepository.get(articleId);
            article.put(ARTICLE_IS_PUBLISHED, false);
            tagUtils.decTagPublishedRefCount(articleId);
            archiveDateUtils.decArchiveDatePublishedRefCount(articleId);
            articleRepository.update(articleId, article);
            // TODO: dec statistic blog article/comment cnt
            transaction.commit();

            ret.put(Keys.STATUS_CODE, StatusCodes.CANCEL_PUBLISH_ARTICLE_SUCC);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            transaction.rollback();

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
}
