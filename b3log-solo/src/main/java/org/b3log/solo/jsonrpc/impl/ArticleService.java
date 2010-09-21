/*
 * Copyright (C) 2009, 2010, B3log Team
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

import com.google.appengine.api.datastore.Transaction;
import com.google.inject.Inject;
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
import org.b3log.latke.action.AbstractCacheablePageAction;
import org.b3log.latke.action.ActionException;
import org.b3log.latke.action.util.Paginator;
import org.b3log.latke.event.Event;
import org.b3log.latke.event.EventException;
import org.b3log.latke.event.EventManager;
import org.b3log.latke.model.Pagination;
import org.b3log.latke.repository.SortDirection;
import org.b3log.latke.repository.gae.AbstractGAERepository;
import org.b3log.solo.jsonrpc.AbstractGAEJSONRpcService;
import org.b3log.solo.repository.ArticleCommentRepository;
import org.b3log.solo.util.ArchiveDateUtils;
import org.b3log.solo.util.ArticleUtils;
import org.b3log.solo.util.Statistics;
import org.b3log.solo.util.TagUtils;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Article service for JavaScript client.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.1.2, Sep 12, 2010
 */
// FIXME: add/update/remove article event handle rollback
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
     * Article-Comment repository.
     */
    @Inject
    private ArticleCommentRepository articleCommentRepository;
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
     *         "articleTags": "tag1,tag2,tag3"
     *     },
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
        checkAuthorized(request, response);
        final JSONObject ret = new JSONObject();

        final Transaction transaction =
                AbstractGAERepository.DATASTORE_SERVICE.beginTransaction();
        try {
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
            // Step 4: Add article
            final String articleId = articleRepository.add(article);
            ret.put(Keys.OBJECT_ID, articleId);
            // Step 5: Add tag-article relations
            articleUtils.addTagArticleRelation(tags, article);
            // Step 6: Inc blog article count statictis
            statistics.incBlogArticleCount();
            // Step 7: Add archive date-article relations
            archiveDateUtils.archiveDate(article);
            // Step 8: Set permalink(/articles/yyyy/MM/dd/articleId.html)
            final String permalinkDate = PERMALINK_FORMAT.format(date);
            final String permalink = "/articles/" + permalinkDate + "/"
                                     + articleId + ".html";
            article.put(ARTICLE_PERMALINK, permalink);
            articleRepository.update(articleId, article); // XXX: Performance issue
            // Step 9: Fire add article event
            final JSONObject eventData = new JSONObject();
            eventData.put(ARTICLE, article);
            eventData.put(Keys.RESULTS, ret);
            eventManager.fireEventSynchronously(
                    new Event<JSONObject>(EventTypes.ADD_ARTICLE, eventData));

            transaction.commit();

            JSONObject status = ret.optJSONObject(Keys.STATUS);
            if (null == status) {
                status = new JSONObject();
            }

            status.put(Keys.CODE, StatusCodes.ADD_ARTICLE_SUCC);
            ret.put(Keys.STATUS, status);
        } catch (final Exception e) {
            transaction.rollback();
            LOGGER.severe(e.getMessage());
            throw new ActionException(e);
        }

        AbstractCacheablePageAction.PAGE_CACHE.removeAll();

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
        checkAuthorized(request, response);

        final JSONObject ret = new JSONObject();

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
            ret.put(Keys.STATUS_CODE, StatusCodes.GET_ARTICLE_SUCC);

            LOGGER.log(Level.FINER, "Got an article[oId={0}]", articleId);
        } catch (final Exception e) {
            LOGGER.severe(e.getMessage());
            throw new ActionException(e);
        }

        return ret;
    }

    /**
     * Gets articles(by crate date descending) by the specified request json
     * object.
     * 
     * @param requestJSONObject the specified request json object, for example,
     * <pre>
     * {
     *     "paginationCurrentPageNum": 1,
     *     "paginationPageSize": 20,
     *     "paginationWindowSize": 10
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
     *         "articleAbstract": "",
     *         "articleCommentCount": int,
     *         "articleCreateDate"; java.util.Date,
     *         "articleViewCount": int,
     *         "articleTags": "tag1, tag2, ...."
     *      }, ....]
     *     "sc": "GET_ARTICLES_SUCC"
     * }
     * </pre>
     * @throws ActionException action exception
     * @throws IOException io exception
     * @see Pagination
     */
    public JSONObject getArticles(final JSONObject requestJSONObject,
                                  final HttpServletRequest request,
                                  final HttpServletResponse response)
            throws ActionException, IOException {
        checkAuthorized(request, response);

        final JSONObject ret = new JSONObject();
        try {
            final int currentPageNum = requestJSONObject.getInt(
                    Pagination.PAGINATION_CURRENT_PAGE_NUM);
            final int pageSize = requestJSONObject.getInt(
                    Pagination.PAGINATION_PAGE_SIZE);
            final int windowSize = requestJSONObject.getInt(
                    Pagination.PAGINATION_WINDOW_SIZE);

            final JSONObject result =
                    articleRepository.get(currentPageNum, pageSize,
                                          ARTICLE_CREATE_DATE,
                                          SortDirection.DESCENDING);
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
            // Remove some details
            for (int i = 0; i < articles.length(); i++) {
                final JSONObject article = articles.getJSONObject(i);
                article.remove(ARTICLE_CONTENT);
                article.remove(ARTICLE_UPDATE_DATE);
            }
            ret.put(ARTICLES, articles);

            ret.put(Keys.STATUS_CODE, StatusCodes.GET_ARTICLES_SUCC);
        } catch (final Exception e) {
            LOGGER.severe(e.getMessage());
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
        checkAuthorized(request, response);
        final Transaction transaction =
                AbstractGAERepository.DATASTORE_SERVICE.beginTransaction();
        final JSONObject ret = new JSONObject();

        try {
            final String articleId = requestJSONObject.getString(Keys.OBJECT_ID);
            LOGGER.log(Level.FINER, "Removing an article[oId={0}]", articleId);
            // Step 1: Dec reference count of tag
            tagUtils.decTagRefCount(articleId);
            // Step 2: Remove tag-article relations
            articleUtils.removeTagArticleRelations(articleId);
            // Step 3: Remove related comments, article-comment relations
            articleUtils.removeArticleComments(articleId);
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
                LOGGER.severe(e.getMessage());
            }

            transaction.commit();

            JSONObject status = ret.optJSONObject(Keys.STATUS);
            if (null == status) {
                status = new JSONObject();
            }

            status.put(Keys.CODE, StatusCodes.REMOVE_ARTICLE_SUCC);
            ret.put(Keys.STATUS, status);
            LOGGER.log(Level.FINER, "Removed an article[oId={0}]", articleId);
        } catch (final Exception e) {
            transaction.rollback();
            LOGGER.severe(e.getMessage());
            throw new ActionException(e);
        }

        AbstractCacheablePageAction.PAGE_CACHE.removeAll();

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
     *         "articleTags": "tag1,tag2,tag3"
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
        checkAuthorized(request, response);
        final Transaction transaction =
                AbstractGAERepository.DATASTORE_SERVICE.beginTransaction();
        final JSONObject ret = new JSONObject();

        try {
            final JSONObject article =
                    requestJSONObject.getJSONObject(ARTICLE);
            final String articleId = article.getString(Keys.OBJECT_ID);
            // Step 1: Dec reference count of tag
            tagUtils.decTagRefCount(articleId);
            // Step 2: Un-archive date-article relations
            archiveDateUtils.unArchiveDate(articleId);
            // Step 3: Remove tag-article relations
            articleUtils.removeTagArticleRelations(articleId);
            // Step 4: Add tags
            final String tagsString =
                    article.getString(ARTICLE_TAGS_REF);
            final String[] tagTitles = tagsString.split(",");
            final JSONArray tags = tagUtils.tag(tagTitles, article);
            // Step 5: Fill auto properties
            final JSONObject oldArticle = articleRepository.get(articleId);
            article.put(ARTICLE_CREATE_DATE, oldArticle.get(
                    ARTICLE_CREATE_DATE));
            article.put(ARTICLE_COMMENT_COUNT,
                        oldArticle.getInt(ARTICLE_COMMENT_COUNT));
            article.put(ARTICLE_VIEW_COUNT,
                        oldArticle.getInt(ARTICLE_VIEW_COUNT));
            article.put(ARTICLE_PERMALINK,
                        oldArticle.getString(ARTICLE_PERMALINK));
            // Step 6: Set updat date
            article.put(ARTICLE_UPDATE_DATE, new Date());
            // Step 7: Update
            articleRepository.update(articleId, article);
            // Step 8: Add tag-article relations
            articleUtils.addTagArticleRelation(tags, article);
            // Step 9: Add archive date-article relations
            archiveDateUtils.archiveDate(article);
            // Step 10: Fire update article event
            final JSONObject eventData = new JSONObject();
            eventData.put(ARTICLE, article);
            eventData.put(Keys.RESULTS, ret);
            try {
                eventManager.fireEventSynchronously(
                        new Event<JSONObject>(EventTypes.UPDATE_ARTICLE,
                                              eventData));
            } catch (final EventException e) {
                LOGGER.severe(e.getMessage());
            }

            transaction.commit();
            JSONObject status = ret.optJSONObject(Keys.STATUS);
            if (null == status) {
                status = new JSONObject();
            }

            status.put(Keys.CODE, StatusCodes.UPDATE_ARTICLE_SUCC);
            ret.put(Keys.STATUS, status);
            LOGGER.log(Level.FINER, "Updated an article[oId={0}]", articleId);
        } catch (final Exception e) {
            transaction.rollback();
            LOGGER.severe(e.getMessage());
            throw new ActionException(e);
        }

        AbstractCacheablePageAction.PAGE_CACHE.removeAll();

        return ret;
    }
}
