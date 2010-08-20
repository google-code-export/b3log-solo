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

import com.google.inject.Inject;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.b3log.solo.action.StatusCodes;
import org.b3log.solo.event.EventTypes;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.Tag;
import org.b3log.solo.repository.ArticleRepository;
import org.b3log.solo.repository.TagArticleRepository;
import org.b3log.solo.repository.TagRepository;
import org.b3log.latke.Keys;
import org.b3log.latke.client.action.ActionException;
import org.b3log.latke.client.action.util.Paginator;
import org.b3log.latke.client.remote.AbstractRemoteService;
import org.b3log.latke.event.Event;
import org.b3log.latke.event.EventManager;
import org.b3log.latke.model.Pagination;
import org.b3log.latke.repository.SortDirection;
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
 * @version 1.0.0.7, Aug 20, 2010
 */
public final class ArticleService extends AbstractRemoteService {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(ArticleService.class);
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
     * }, see {@link Article} for more details
     * </pre>
     * @param request the specified http servlet request
     * @param response the specified http servlet response
     * @return for example,
     * <pre>
     * {
     *     "oId": generatedArticleId
     *     "sc": "ADD_ARTICLE_SUCC"
     * }
     * </pre>
     * @throws ActionException action exception
     * @throws IOException io exception
     * @see Article
     */
    public JSONObject addArticle(final JSONObject requestJSONObject,
                                 final HttpServletRequest request,
                                 final HttpServletResponse response)
            throws ActionException, IOException {
        checkAuthorized(request, response);

        final JSONObject ret = new JSONObject();

        try {
            final JSONObject article =
                    requestJSONObject.getJSONObject(Article.ARTICLE);
            // Step 1: Add tags
            final String tagsString =
                    article.getString(Article.ARTICLE_TAGS_REF);
            final String[] tagTitles = tagsString.split(",");
            final JSONArray tags = tagUtils.tag(tagTitles, article);
            // Step 2; Set comment/view count to 0
            article.put(Article.ARTICLE_COMMENT_COUNT, 0);
            article.put(Article.ARTICLE_VIEW_COUNT, 0);
            // Step 3: Add article
            final String articleId = articleRepository.add(article);
            ret.put(Keys.OBJECT_ID, articleId);
            // Step 4: Add tag-article relations
            articleUtils.addTagArticleRelation(tags, article);
            // Step 5: Inc blog article count statictis
            statistics.incBlogArticleCount();
            // Step 6: Add archive date-article relations
            archiveDateUtils.archiveDate(article);

            // TODO: event handling: add article
            eventManager.fireEventSynchronously(
                    new Event<JSONObject>(EventTypes.ADD_ARTICLE, article));

            ret.put(Keys.STATUS_CODE, StatusCodes.ADD_ARTICLE_SUCC);
        } catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new ActionException(e);
        }

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
            ret.put(Article.ARTICLE, article);

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

            article.put(Article.ARTICLE_TAGS_REF, tags);
            ret.put(Keys.STATUS_CODE, StatusCodes.GET_ARTICLE_SUCC);

            LOGGER.debug("Got an article[oId=" + articleId + "]");
        } catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new ActionException(e);
        }

        return ret;
    }

    /**
     * Gets articles by the specified request json object.
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
     *         "articleContent": "",
     *         "articleCreateDate"; java.util.Date,
     *         "articleUpdateDate": java.util.Date
     *      }, ....]
     *     "sc": "GET_ARTICLES_SUCC"
     * }
     * </pre>
     * @throws ActionException action exception
     * @throws IOException io exception
     * @see Pagination
     * @see Article
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
                                          Article.ARTICLE_UPDATE_DATE,
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
            ret.put(Article.ARTICLES, articles);

            ret.put(Keys.STATUS_CODE, StatusCodes.GET_ARTICLES_SUCC);
        } catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
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
     *     "sc": "REMOVE_ARTICLE_SUCC"
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

        final JSONObject ret = new JSONObject();

        try {
            final String articleId = requestJSONObject.getString(Keys.OBJECT_ID);
            LOGGER.debug("Removing an article[oId=" + articleId + "]");
            // Step 1: Dec reference count of tag
            tagUtils.decTagRefCount(articleId);
            // Step 2: Remove tag-article relations
            articleUtils.removeTagArticleRelations(articleId);
            // Step 3: Remove article
            articleRepository.remove(articleId);
            // Step 4: Dec blog article count statictis
            statistics.decBlogArticleCount();
            // Step 5: Un-archive date-article relations
            archiveDateUtils.unArchiveDate(articleId);

            ret.put(Keys.STATUS_CODE, StatusCodes.REMOVE_ARTICLE_SUCC);

            LOGGER.debug("Removed an article[oId=" + articleId + "]");
        } catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new ActionException(e);
        }

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
     * }, see {@link Article} for more details
     * </pre>
     * @param request the specified http servlet request
     * @param response the specified http servlet response
     * @return for example,
     * <pre>
     * {
     *     "sc": "UPDATE_ARTICLE_SUCC"
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

        final JSONObject ret = new JSONObject();

        try {
            final JSONObject article =
                    requestJSONObject.getJSONObject(Article.ARTICLE);
            final String articleId = article.getString(Keys.OBJECT_ID);
            // Step 1: Dec reference count of tag
            tagUtils.decTagRefCount(articleId);
            // Step 2: Remove tag-article relations
            articleUtils.removeTagArticleRelations(articleId);
            // Step 3: Add tags
            final String tagsString =
                    article.getString(Article.ARTICLE_TAGS_REF);
            final String[] tagTitles = tagsString.split(",");
            final JSONArray tags = tagUtils.tag(tagTitles, article);
            // Step 4: Fill auto properties
            final JSONObject oldArticle = articleRepository.get(articleId);
            article.put(Article.ARTICLE_CREATE_DATE, oldArticle.get(
                    Article.ARTICLE_CREATE_DATE));
            article.put(Article.ARTICLE_COMMENT_COUNT,
                        oldArticle.getInt(Article.ARTICLE_COMMENT_COUNT));
            article.put(Article.ARTICLE_VIEW_COUNT,
                        oldArticle.getInt(Article.ARTICLE_VIEW_COUNT));
            // Step 5: Set updat date
            article.put(Article.ARTICLE_UPDATE_DATE, new Date());
            // Step 6: Update
            articleRepository.update(articleId, article);
            // Step 7: Add tag-article relations
            articleUtils.addTagArticleRelation(tags, article);
            // Step 8: Add archive date-article relations
            archiveDateUtils.archiveDate(article);

            ret.put(Keys.STATUS_CODE, StatusCodes.UPDATE_ARTICLE_SUCC);

            LOGGER.debug("Updated an article[oId=" + articleId + "]");
        } catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new ActionException(e);
        }

        return ret;
    }
}
