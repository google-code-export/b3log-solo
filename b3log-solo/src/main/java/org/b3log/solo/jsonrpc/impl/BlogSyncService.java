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
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.client.action.AbstractCacheablePageAction;
import org.b3log.latke.client.action.ActionException;
import org.b3log.latke.repository.gae.AbstractGAERepository;
import org.b3log.solo.action.StatusCodes;
import org.b3log.solo.jsonrpc.AbstractJSONRpcService;
import org.b3log.solo.util.ArticleUtils;
import org.b3log.solo.util.TagUtils;
import org.b3log.solo.model.Article;
import static org.b3log.solo.model.BlogSync.*;
import org.b3log.solo.repository.ArticleRepository;
import org.b3log.solo.repository.BlogSyncManagementRepository;
import org.b3log.solo.repository.CSDNBlogArticleRepository;
import org.b3log.solo.repository.CSDNBlogArticleSoloArticleRepository;
import org.b3log.solo.servlet.SoloServletListener;
import org.b3log.solo.sync.csdn.blog.CSDNBlog;
import org.b3log.solo.sync.csdn.blog.CSDNBlogArticle;
import org.b3log.solo.util.ArchiveDateUtils;
import org.b3log.solo.util.Statistics;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Blog sync service for JavaScript client.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.8, Aug 27, 2010
 */
public final class BlogSyncService extends AbstractJSONRpcService {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(BlogSyncService.class);
    /**
     * Article repository.
     */
    @Inject
    private ArticleRepository articleRepository;
    /**
     * CSDN blog article repository.
     */
    @Inject
    private CSDNBlogArticleRepository csdnBlogArticleRepository;
    /**
     * CSDN blog.
     */
    @Inject
    private CSDNBlog csdnBlog;
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
     * CSDN blog article-Solo article repository.
     */
    @Inject
    private CSDNBlogArticleSoloArticleRepository csdnBlogArticleSoloArticleRepository;
    /**
     * Blog sync management repository.
     */
    @Inject
    private BlogSyncManagementRepository blogSyncManagementRepository;
    /**
     * CSDN blog article retrieval count incremental.
     */
    public static final int CSDN_BLOG_ARTICLE_RETRIEVAL_COUNT_INCREMENTAL = 2;

    /**
     * Gets blog sync management for CSDN blog with the specified http servlet
     * request and http servlet response.
     *
     * @param request the specified http servlet request
     * @param response the specified http servlet response
     * @return for example,
     * <pre>
     * {
     *     "blogSyncExternalBloggingSysUserName": "",
     *     "blogSyncExternalBloggingSysUserPassword": "",
     *     "blogSyncMgmtAddEnabled": boolean,
     *     "blogSyncMgmtUpdateEnabled": boolean,
     *     "blogSyncMgmtRemoveEnabled": boolean
     * }, returns {@code null} if not found
     * </pre>
     * @throws ActionException action exception
     * @throws IOException io exception
     */
    public JSONObject getBlogSyncMgmtForCSDNBlog(
            final HttpServletRequest request, final HttpServletResponse response)
            throws ActionException, IOException {
        checkAuthorized(request, response);

        return blogSyncManagementRepository.getByExternalBloggingSystem(
                BLOG_SYNC_CSDN_BLOG);
    }

    /**
     * Sets blog sync management for CSDN blog with the specified request json
     * object, http servlet request and http servlet response.
     *
     * @param requestJSONObject the specified request json object, for example,
     * <pre>
     * {
     *     "blogSyncExternalBloggingSysUserName": "",
     *     "blogSyncExternalBloggingSysUserPassword": "",
     *     "blogSyncMgmtAddEnabled": boolean,
     *     "blogSyncMgmtUpdateEnabled": boolean,
     *     "blogSyncMgmtRemoveEnabled": boolean
     * }
     * </pre>
     * @param request the specified http servlet request
     * @param response the specified http servlet response
     * @return for example,
     * <pre>
     * {
     *     "sc": SET_BLOG_SYNC_MGMT_FOR_CSDN_BLOG_SUCC
     * }
     * </pre>
     * @throws ActionException action exception
     * @throws IOException io exception
     */
    public JSONObject setBlogSyncMgmtForCSDNBlog(
            final JSONObject requestJSONObject,
            final HttpServletRequest request,
            final HttpServletResponse response)
            throws ActionException, IOException {
        checkAuthorized(request, response);
        final Transaction transaction =
                AbstractGAERepository.DATASTORE_SERVICE.beginTransaction();
        final JSONObject ret = new JSONObject();
        try {
            final String userName = requestJSONObject.getString(
                    BLOG_SYNC_EXTERNAL_BLOGGING_SYS_USER_NAME);
            final String userPwd = requestJSONObject.getString(
                    BLOG_SYNC_EXTERNAL_BLOGGING_SYS_USER_PASSWORD);
            final boolean addEnabled = requestJSONObject.getBoolean(
                    BLOG_SYNC_MGMT_ADD_ENABLED);
            final boolean updateEnabled = requestJSONObject.getBoolean(
                    BLOG_SYNC_MGMT_UPDATE_ENABLED);
            final boolean removeEnabled = requestJSONObject.getBoolean(
                    BLOG_SYNC_MGMT_REMOVE_ENABLED);

            JSONObject csdnBlogSyncMgmt = blogSyncManagementRepository.
                    getByExternalBloggingSystem(BLOG_SYNC_CSDN_BLOG);
            if (null == csdnBlogSyncMgmt) {
                csdnBlogSyncMgmt = new JSONObject();
            }

            csdnBlogSyncMgmt.put(BLOG_SYNC_EXTERNAL_BLOGGING_SYS,
                                 BLOG_SYNC_CSDN_BLOG);
            csdnBlogSyncMgmt.put(BLOG_SYNC_EXTERNAL_BLOGGING_SYS_USER_NAME,
                                 userName);
            csdnBlogSyncMgmt.put(
                    BLOG_SYNC_EXTERNAL_BLOGGING_SYS_USER_PASSWORD, userPwd);
            csdnBlogSyncMgmt.put(BLOG_SYNC_MGMT_ADD_ENABLED, addEnabled);
            csdnBlogSyncMgmt.put(BLOG_SYNC_MGMT_UPDATE_ENABLED,
                                 updateEnabled);
            csdnBlogSyncMgmt.put(BLOG_SYNC_MGMT_REMOVE_ENABLED,
                                 removeEnabled);

            if (!csdnBlogSyncMgmt.has(Keys.OBJECT_ID)) {
                blogSyncManagementRepository.add(csdnBlogSyncMgmt);
                LOGGER.debug("Added blog sync management for CSDN["
                             + csdnBlogSyncMgmt.toString(
                        SoloServletListener.JSON_PRINT_INDENT_FACTOR) + "]");

            } else {
                blogSyncManagementRepository.update(
                        csdnBlogSyncMgmt.getString(Keys.OBJECT_ID),
                        csdnBlogSyncMgmt);
                LOGGER.debug("Updated blog sync management for CSDN["
                             + csdnBlogSyncMgmt.toString(
                        SoloServletListener.JSON_PRINT_INDENT_FACTOR) + "]");

            }

            transaction.commit();
            ret.put(Keys.STATUS_CODE,
                    StatusCodes.SET_BLOG_SYNC_MGMT_FOR_CSDN_BLOG_SUCC);
        } catch (final Exception e) {
            transaction.rollback();
            LOGGER.error(e.getMessage(), e);
            throw new ActionException(e);
        }

        return ret;
    }

    /**
     * Imports CSDN blog articles by the specified request json object and http
     * servlet request.
     *
     * @param requestJSONObject the specified request json object, for example,
     * <pre>
     * {
     *     "oIds": ["", "", ....]
     * }
     * </pre>
     * @param request the specified http servlet request
     * @param response the specified http servlet response
     * @return imported article ids, for example,
     * <pre>
     * {
     *     "oIds": ["", "", ....]
     * }
     * </pre>
     * @throws ActionException action exception
     * @throws IOException io exception
     */
    public JSONObject importCSDNBlogArticles(final JSONObject requestJSONObject,
                                             final HttpServletRequest request,
                                             final HttpServletResponse response)
            throws ActionException, IOException {
        checkAuthorized(request, response);
        final Transaction transaction =
                AbstractGAERepository.DATASTORE_SERVICE.beginTransaction();
        final JSONObject ret = new JSONObject();

        try {
            final JSONArray articleIds = requestJSONObject.getJSONArray(
                    Keys.OBJECT_IDS);
            final List<String> importedIds = new ArrayList<String>();
            for (int i = 0; i < articleIds.length(); i++) {
                final String oId = articleIds.getString(i);

                final JSONObject csdnBlogArticle =
                        csdnBlogArticleRepository.get(oId);
                final JSONObject soloArticle = toSoloArticle(csdnBlogArticle);

                final String categoriesString = csdnBlogArticle.getString(
                        BLOG_SYNC_CSDN_BLOG_ARTICLE_CATEGORIES);
                final String[] tagTitles = categoriesString.split(",");
                @SuppressWarnings(value = "unchecked")
                final JSONArray tags = tagUtils.tag(tagTitles, soloArticle);
                articleUtils.addTagArticleRelation(tags, soloArticle);

                articleRepository.importArticle(soloArticle);
                importedIds.add(oId);

                statistics.incBlogArticleCount();
                archiveDateUtils.archiveDate(soloArticle);
            }

            // Clear page cache
            AbstractCacheablePageAction.PAGE_CACHE.removeAll();

            transaction.commit();
            ret.put(Keys.OBJECT_IDS, importedIds);
        } catch (final Exception e) {
            transaction.rollback();
            LOGGER.error(e.getMessage(), e);
            throw new ActionException(e);
        }

        return ret;
    }

    /**
     * Gest CSDN blog articles by the specified request json object.
     *
     * @param requestJSONObject the specified request json object, for example,
     * <pre>
     * {
     *     "blogSyncExternalBloggingSysUserName": "",
     *     "blogSyncCSDNBlogArchiveDate": "2006/12"
     * }
     * </pre>
     * @param request the specified http servlet request
     * @param response the specified http servlet response
     * @return for example,
     * <pre>
     * {
     *     "blogSyncCSDNBlogArticles": [{
     *         "oId": "",
     *         "blogSyncCSDNBlogArticleTitle": "",
     *         "blogSyncCSDNBlogArticleCreateDate": java.util.Date,
     *         "blogSyncCSDNBlogArticleCategories": "category1, category2, ....",
     *         "blogSyncCSDNBlogArticleContent": "",
     *         "blogsyncCSDNBlogArticleAbstract": ""
     *     }, ....]
     * }
     * </pre>
     * @throws ActionException action exception
     * @throws IOException io exception
     */
    public JSONObject getCSDNBlogArticlesByArchiveDate(
            final JSONObject requestJSONObject,
            final HttpServletRequest request,
            final HttpServletResponse response) throws ActionException,
                                                       IOException {
        checkAuthorized(request, response);
        final Transaction transaction =
                AbstractGAERepository.DATASTORE_SERVICE.beginTransaction();
        final JSONObject ret = new JSONObject();

        try {
            final String csdnBlogUserName =
                    requestJSONObject.getString(
                    BLOG_SYNC_EXTERNAL_BLOGGING_SYS_USER_NAME);
            final String archiveDate = requestJSONObject.getString(
                    BLOG_SYNC_CSDN_BLOG_ARCHIVE_DATE);
            final List<String> csdnArticleIds =
                    csdnBlog.getArticleIdsByArchiveDate(csdnBlogUserName,
                                                        archiveDate);
            LOGGER.debug("There are [" + csdnArticleIds.size()
                         + "] articles of CSDN"
                         + "blog user[userName=" + csdnBlogUserName + "]"
                         + " in [" + archiveDate + "]");
            final JSONArray articles = new JSONArray();
            ret.put(BLOG_SYNC_CSDN_BLOG_ARTICLES, articles);
            int retrievalCnt = 0;
            for (final String csdnArticleId : csdnArticleIds) {
                final String oId = csdnBlogArticleSoloArticleRepository.
                        getSoloArticleId(csdnArticleId);
                LOGGER.trace("CSDN article[id=" + csdnArticleId + "] "
                             + "Solo article[id=" + oId + "]");
                final boolean imported = articleRepository.has(oId);
                final boolean csdnTmpImported =
                        csdnBlogArticleRepository.has(oId);
                // assert imported == csdnTmpImported for consistency

                JSONObject article = null;
                LOGGER.debug("CSDN blog article[oId=" + oId
                             + "]'s status["
                             + "csdnTmpImported=" + csdnTmpImported
                             + ", imported=" + imported + "]");
                if (csdnTmpImported) {
                    article = csdnBlogArticleRepository.get(oId);
                } else { // Not retrieved yet, get the article from CSDN
                    final CSDNBlogArticle csdnBlogArticle =
                            csdnBlog.getArticleById(csdnBlogUserName,
                                                    csdnArticleId);
                    if (null != csdnBlogArticle) {
                        article = csdnBlogArticle.toJSONObject();
                        final String csdnBlogArticleImportedId =
                                csdnBlogArticleRepository.add(article);

                        final JSONObject csdnArticleSoloArticleRelation =
                                new JSONObject();
                        csdnArticleSoloArticleRelation.put(
                                BLOG_SYNC_CSDN_BLOG_ARTICLE_ID, csdnArticleId);
                        csdnArticleSoloArticleRelation.put(
                                Article.ARTICLE + "_" + Keys.OBJECT_ID,
                                csdnBlogArticleImportedId);
                        csdnBlogArticleSoloArticleRepository.add(
                                csdnArticleSoloArticleRelation);
                        LOGGER.debug("Added CSDN blog article-solo article relation["
                                     + csdnArticleSoloArticleRelation.toString()
                                     + "]");

                        retrievalCnt++;
                    } else {
                        LOGGER.warn("Retrieve article[csdnArticleId="
                                    + csdnArticleId + "] from CSDN blog is null");

                        continue;
                    }
                }

                article.put(BLOG_SYNC_IMPORTED, imported);
                articles.put(article);

                if (CSDN_BLOG_ARTICLE_RETRIEVAL_COUNT_INCREMENTAL
                    == retrievalCnt) {
                    break;
                }
            }

//            LOGGER.debug("Got articles[" + ret.toString(
//                    SoloServletListener.JSON_PRINT_INDENT_FACTOR)
//                         + "] from CSDN blog");
            transaction.commit();
        } catch (final Exception e) {
            transaction.rollback();
            LOGGER.error(e.getMessage(), e);
            throw new ActionException(e);
        }

        return ret;
    }

    /**
     * Gets CSDN blog archive dates by the specified request json object.
     *
     * @param requestJSONObject the specified request json object, for example,
     * <pre>
     * {
     *     "blogSyncExternalBloggingSysUserName": ""
     * }
     * </pre>
     * @param request the specified http servlet request
     * @param response the specified http servlet response
     * @return for example,
     * <pre>
     * {
     *     "blogSyncCSDNBlogArchiveDates": ["2006/12", "2007/01", ...]
     * }
     * </pre>
     * @throws ActionException action exception
     * @throws IOException io exception
     */
    public JSONObject getCSDNBlogArticleArchiveDate(
            final JSONObject requestJSONObject,
            final HttpServletRequest request,
            final HttpServletResponse response) throws ActionException,
                                                       IOException {
        checkAuthorized(request, response);

        final JSONObject ret = new JSONObject();

        try {
            final String csdnBlogUserName = requestJSONObject.getString(
                    BLOG_SYNC_EXTERNAL_BLOGGING_SYS_USER_NAME);
            final List<String> archiveDates =
                    csdnBlog.getArchiveDates(csdnBlogUserName);

            ret.put(BLOG_SYNC_CSDN_BLOG_ARCHIVE_DATES, archiveDates);
        } catch (final JSONException e) {
            LOGGER.error(e.getMessage(), e);
            throw new ActionException(e);
        }

        return ret;
    }

    /**
     * To B3log Solo article(Key transformation) for the specified CSDN blog
     * article.
     *
     * @param csdnBlogArticle the specified CSDN Blog article
     * @return Solo article
     * @throws Exception exception
     */
    private JSONObject toSoloArticle(final JSONObject csdnBlogArticle)
            throws Exception {
        final JSONObject ret = new JSONObject();

        ret.put(Keys.OBJECT_ID, csdnBlogArticle.getString(Keys.OBJECT_ID));
        ret.put(Article.ARTICLE_TITLE,
                csdnBlogArticle.getString(BLOG_SYNC_CSDN_BLOG_ARTICLE_TITLE));
        ret.put(Article.ARTICLE_ABSTRACT,
                csdnBlogArticle.getString(BLOG_SYNC_CSDN_BLOG_ARTICLE_ABSTRACT));
        ret.put(Article.ARTICLE_CONTENT,
                csdnBlogArticle.getString(BLOG_SYNC_CSDN_BLOG_ARTICLE_CONTENT));
        ret.put(Article.ARTICLE_CREATE_DATE,
                csdnBlogArticle.get(BLOG_SYNC_CSDN_BLOG_ARTICLE_CREATE_DATE));
        ret.put(Article.ARTICLE_TAGS_REF,
                csdnBlogArticle.getString(BLOG_SYNC_CSDN_BLOG_ARTICLE_CATEGORIES));

        ret.put(Article.ARTICLE_VIEW_COUNT, 0);
        ret.put(Article.ARTICLE_COMMENT_COUNT, 0);

        return ret;
    }
}
