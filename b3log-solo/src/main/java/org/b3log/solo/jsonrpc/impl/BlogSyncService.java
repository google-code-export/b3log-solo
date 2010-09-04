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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.latke.Keys;
import org.b3log.latke.action.AbstractCacheablePageAction;
import org.b3log.latke.action.ActionException;
import org.b3log.latke.repository.gae.AbstractGAERepository;
import org.b3log.solo.action.StatusCodes;
import org.b3log.solo.jsonrpc.AbstractGAEJSONRpcService;
import org.b3log.solo.util.ArticleUtils;
import org.b3log.solo.util.TagUtils;
import org.b3log.solo.model.Article;
import static org.b3log.solo.model.BlogSync.*;
import org.b3log.solo.repository.ArticleRepository;
import org.b3log.solo.repository.BlogSyncManagementRepository;
import org.b3log.solo.repository.ExternalArticleRepository;
import org.b3log.solo.repository.ExternalArticleSoloArticleRepository;
import org.b3log.solo.servlet.SoloServletListener;
import org.b3log.solo.sync.BlogFactory;
import org.b3log.solo.sync.MetaWeblog;
import org.b3log.solo.sync.Post;
import org.b3log.solo.util.ArchiveDateUtils;
import org.b3log.solo.util.Statistics;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Blog sync service for JavaScript client.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.9, Sep 4, 2010
 */
public final class BlogSyncService extends AbstractGAEJSONRpcService {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(BlogSyncService.class.getName());
    /**
     * Article repository.
     */
    @Inject
    private ArticleRepository articleRepository;
    /**
     * External blog article repository.
     */
    @Inject
    private ExternalArticleRepository externalArticleRepository;
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
     * External blog article-Solo article repository.
     */
    @Inject
    private ExternalArticleSoloArticleRepository externalArticleSoloArticleRepository;
    /**
     * Blog sync management repository.
     */
    @Inject
    private BlogSyncManagementRepository blogSyncManagementRepository;
    /**
     * External blog article retrieval count incremental.
     */
    public static final int EXTERNAL_ARTICLE_RETRIEVAL_COUNT_INCREMENTAL = 2;

    /**
     * Gets blog sync management for external blogging system with the specified
     * http servlet request and http servlet response.
     *
     * @param requestJSONObject the specified request json object, for example,
     * <pre>
     * {
     *     ""blogSyncExternalBloggingSys": ""
     * }
     * </pre>
     * @param request the specified http servlet request
     * @param response the specified http servlet response
     * @return for example,
     * <pre>
     * {
     *     "blogSyncExternalBloggingSys": "",
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
    public JSONObject getBlogSyncMgmt(final JSONObject requestJSONObject,
                                      final HttpServletRequest request,
                                      final HttpServletResponse response)
            throws ActionException, IOException {
        checkAuthorized(request, response);

        String externalSys = null;
        try {
            externalSys = requestJSONObject.getString(
                    BLOG_SYNC_EXTERNAL_BLOGGING_SYS);
            return blogSyncManagementRepository.getByExternalBloggingSystem(
                    externalSys);
        } catch (final JSONException e) {
            LOGGER.severe(e.getMessage());
        }

        return null;
    }

    /**
     * Sets blog sync management for external blogging system with the specified
     * request json object, http servlet request and http servlet response.
     *
     * @param requestJSONObject the specified request json object, for example,
     * <pre>
     * {
     *     ""blogSyncExternalBloggingSys": "",
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
     *     "sc": SET_BLOG_SYNC_MGMT_SUCC
     * }
     * </pre>
     * @throws ActionException action exception
     * @throws IOException io exception
     */
    public JSONObject setBlogSyncMgmt(final JSONObject requestJSONObject,
                                      final HttpServletRequest request,
                                      final HttpServletResponse response)
            throws ActionException, IOException {
        checkAuthorized(request, response);
        final Transaction transaction =
                AbstractGAERepository.DATASTORE_SERVICE.beginTransaction();
        final JSONObject ret = new JSONObject();
        try {
            final String externalBloggingSys =
                    requestJSONObject.getString(
                    BLOG_SYNC_EXTERNAL_BLOGGING_SYS);
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

            JSONObject blogSyncMgmt = blogSyncManagementRepository.
                    getByExternalBloggingSystem(BLOG_SYNC_CSDN_BLOG);
            if (null == blogSyncMgmt) {
                blogSyncMgmt = new JSONObject();
            }

            blogSyncMgmt.put(BLOG_SYNC_EXTERNAL_BLOGGING_SYS,
                             externalBloggingSys);
            blogSyncMgmt.put(BLOG_SYNC_EXTERNAL_BLOGGING_SYS_USER_NAME,
                             userName);
            blogSyncMgmt.put(
                    BLOG_SYNC_EXTERNAL_BLOGGING_SYS_USER_PASSWORD, userPwd);
            blogSyncMgmt.put(BLOG_SYNC_MGMT_ADD_ENABLED, addEnabled);
            blogSyncMgmt.put(BLOG_SYNC_MGMT_UPDATE_ENABLED,
                             updateEnabled);
            blogSyncMgmt.put(BLOG_SYNC_MGMT_REMOVE_ENABLED,
                             removeEnabled);

            if (!blogSyncMgmt.has(Keys.OBJECT_ID)) {
                blogSyncManagementRepository.add(blogSyncMgmt);
                LOGGER.log(Level.FINER,
                           "Added blog sync management for [{0}] [{1}]",
                           new String[]{
                            externalBloggingSys,
                            blogSyncMgmt.toString(
                            SoloServletListener.JSON_PRINT_INDENT_FACTOR)
                        });

            } else {
                blogSyncManagementRepository.update(
                        blogSyncMgmt.getString(Keys.OBJECT_ID),
                        blogSyncMgmt);
                LOGGER.log(Level.FINER,
                           "Updated blog sync management for [{0}] [{1}]",
                           new String[]{
                            externalBloggingSys,
                            blogSyncMgmt.toString(
                            SoloServletListener.JSON_PRINT_INDENT_FACTOR)
                        });
            }

            transaction.commit();
            ret.put(Keys.STATUS_CODE,
                    StatusCodes.SET_BLOG_SYNC_MGMT_SUCC);
        } catch (final Exception e) {
            transaction.rollback();
            LOGGER.severe(e.getMessage());
            throw new ActionException(e);
        }

        return ret;
    }

    /**
     * Imports external blogging system articles by the specified request json
     * object and http servlet request.
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
    public JSONObject importExternalArticles(final JSONObject requestJSONObject,
                                             final HttpServletRequest request,
                                             final HttpServletResponse response)
            throws ActionException, IOException {
        checkAuthorized(request, response);
        final JSONObject ret = new JSONObject();

        try {
            final JSONArray articleIds = requestJSONObject.getJSONArray(
                    Keys.OBJECT_IDS);
            final List<String> importedIds = new ArrayList<String>();
            for (int i = 0; i < articleIds.length(); i++) {
                final Transaction transaction =
                        AbstractGAERepository.DATASTORE_SERVICE.beginTransaction();
                try {

                    final String oId = articleIds.getString(i);

                    final JSONObject externalArticle =
                            externalArticleRepository.get(oId);
                    final JSONObject soloArticle =
                            toSoloArticle(externalArticle);

                    final String categoriesString = externalArticle.getString(
                            BLOG_SYNC_EXTERNAL_ARTICLE_CATEGORIES);
                    final String[] tagTitles = categoriesString.split(",");
                    @SuppressWarnings(value = "unchecked")
                    final JSONArray tags = tagUtils.tag(tagTitles, soloArticle);
                    articleUtils.addTagArticleRelation(tags, soloArticle);

                    articleRepository.importArticle(soloArticle);
                    importedIds.add(oId);

                    statistics.incBlogArticleCount();
                    archiveDateUtils.archiveDate(soloArticle);

                    transaction.commit();
                } catch (final Exception e) {
                    LOGGER.severe(e.getMessage());
                    transaction.rollback();
                }
            }

            // Clear page cache
            AbstractCacheablePageAction.PAGE_CACHE.removeAll();
            ret.put(Keys.OBJECT_IDS, importedIds);
        } catch (final JSONException e) {
            LOGGER.severe(e.getMessage());
            throw new ActionException(e);
        }

        return ret;
    }

    /**
     * Gest external blogging system articles by the specified request json
     * object.
     *
     * @param requestJSONObject the specified request json object, for example,
     * <pre>
     * {
     *     "blogSyncExternalBloggingSys": "",
     *     "blogSyncExternalBloggingSysUserName": "",
     *     "blogSyncExternalBloggingSysUserPassword": "",
     *     "blogSyncExternalArchiveDate": "2006/12"
     * }
     * </pre>
     * @param request the specified http servlet request
     * @param response the specified http servlet response
     * @return for example,
     * <pre>
     * {
     *     "blogSyncExternalArticles": [{
     *         "oId": "",
     *         "blogSyncExternalArticleTitle": "",
     *         "blogSyncExternalArticleCreateDate": java.util.Date,
     *         "blogSyncExternalArticleCategories": "category1, category2, ....",
     *         "blogSyncExternalArticleContent": "",
     *         "blogsyncExternalArticleAbstract": ""
     *     }, ....]
     * }
     * </pre>
     * @throws ActionException action exception
     * @throws IOException io exception
     */
    public JSONObject getExternalArticlesByArchiveDate(
            final JSONObject requestJSONObject,
            final HttpServletRequest request,
            final HttpServletResponse response) throws ActionException,
                                                       IOException {
        checkAuthorized(request, response);

        final Transaction transaction =
                AbstractGAERepository.DATASTORE_SERVICE.beginTransaction();
        final JSONObject ret = new JSONObject();

        try {
            final String externalSys = requestJSONObject.getString(
                    BLOG_SYNC_EXTERNAL_BLOGGING_SYS);
            final String userName =
                    requestJSONObject.getString(
                    BLOG_SYNC_EXTERNAL_BLOGGING_SYS_USER_NAME);
            final String userPwd =
                    requestJSONObject.getString(
                    BLOG_SYNC_EXTERNAL_BLOGGING_SYS_USER_PASSWORD);
            final String archiveDate = requestJSONObject.getString(
                    BLOG_SYNC_EXTERNAL_ARCHIVE_DATE);
            final MetaWeblog metaWeblog = BlogFactory.getMetaWeblog(externalSys);
            metaWeblog.setUserName(userName);
            metaWeblog.setUserPassword(userPwd);
            final List<String> externalArticleIds =
                    metaWeblog.getArticleIdsByArchiveDate(archiveDate);
            LOGGER.log(Level.FINER,
                       "There are [{0}] articles of [{1}] user[userName={2}] in [{3}]",
                       new Object[]{externalArticleIds.size(),
                                    externalSys,
                                    userName,
                                    archiveDate});
            final JSONArray articles = new JSONArray();
            ret.put(BLOG_SYNC_EXTERNAL_ARTICLES, articles);
            int retrievalCnt = 0;
            for (final String externalArticleId : externalArticleIds) {
                final String oId = externalArticleSoloArticleRepository.
                        getSoloArticleId(externalArticleId, externalSys);
                LOGGER.log(Level.FINEST,
                           "External[{0}] article[id={1}] Solo article[id={2}]",
                           new String[]{externalSys, externalArticleId, oId});
                final boolean imported = articleRepository.has(oId);
                final boolean tmpImported =
                        externalArticleRepository.has(oId);
                // assert imported == tmpImported for consistency

                JSONObject article = null;
                LOGGER.log(Level.FINER,
                           "External[{0}] blog article[oId={1}]'s status[tmpImported={2}, imported={3}]",
                           new String[]{externalSys,
                                        oId,
                                        Boolean.toString(tmpImported),
                                        Boolean.toString(imported)});
                if (tmpImported) {
                    article = externalArticleRepository.get(oId);
                } else { // Not retrieved yet, get the article from External blogging system
                    final Post externalPost =
                            metaWeblog.getPost(externalArticleId);
                    if (null != externalPost) {
                        article = externalPost.toJSONObject();
                        final String externalArticleImportedId =
                                externalArticleRepository.add(article);

                        final JSONObject externalArticleSoloArticleRelation =
                                new JSONObject();
                        externalArticleSoloArticleRelation.put(
                                BLOG_SYNC_EXTERNAL_ARTICLE_ID,
                                externalArticleId);
                        externalArticleSoloArticleRelation.put(
                                Article.ARTICLE + "_" + Keys.OBJECT_ID,
                                externalArticleImportedId);
                        externalArticleSoloArticleRelation.put(
                                BLOG_SYNC_EXTERNAL_BLOGGING_SYS,
                                externalSys);
                        externalArticleSoloArticleRepository.add(
                                externalArticleSoloArticleRelation);
                        LOGGER.log(Level.FINER,
                                   "Added an external[{0}] blog article-solo article relation[{1}]",
                                   new String[]{
                                    externalSys,
                                    externalArticleSoloArticleRelation.toString()});
                        retrievalCnt++;
                    } else {
                        LOGGER.log(Level.WARNING,
                                   "Retrieve article[postId={0}] from external blogging system[{1}]is null",
                                   new String[]{externalArticleId, externalSys});
                        continue;
                    }

                    article.put(BLOG_SYNC_IMPORTED, imported);
                    articles.put(article);

                    if (EXTERNAL_ARTICLE_RETRIEVAL_COUNT_INCREMENTAL
                        == retrievalCnt) {
                        break;
                    }
                }
            }

            transaction.commit();
        } catch (final Exception e) {
            transaction.rollback();
            LOGGER.severe(e.getMessage());
            throw new ActionException(e);
        }

        return ret;
    }

    /**
     * Gets external blogging system article archive dates by the specified
     * request json object.
     *
     * @param requestJSONObject the specified request json object, for example,
     * <pre>
     * {
     *     "blogSyncExternalBloggingSys": "",
     *     "blogSyncExternalBloggingSysUserName": ""
     * }
     * </pre>
     * @param request the specified http servlet request
     * @param response the specified http servlet response
     * @return for example,
     * <pre>
     * {
     *     "blogSyncExternalArchiveDates": ["2006/12", "2007/01", ...]
     * }
     * </pre>
     * @throws ActionException action exception
     * @throws IOException io exception
     */
    public JSONObject getExternalArticleArchiveDate(
            final JSONObject requestJSONObject,
            final HttpServletRequest request,
            final HttpServletResponse response) throws ActionException,
                                                       IOException {
        checkAuthorized(request, response);

        final JSONObject ret = new JSONObject();

        try {
            final String externalSys = requestJSONObject.getString(
                    BLOG_SYNC_EXTERNAL_BLOGGING_SYS);
            final String userName = requestJSONObject.getString(
                    BLOG_SYNC_EXTERNAL_BLOGGING_SYS_USER_NAME);
            final MetaWeblog metaWeblog = BlogFactory.getMetaWeblog(externalSys);
            metaWeblog.setUserName(userName);
            metaWeblog.setUserPassword("ignored");
            final List<String> archiveDates = metaWeblog.getArchiveDates();

            ret.put(BLOG_SYNC_EXTERNAL_ARCHIVE_DATES, archiveDates);
        } catch (final JSONException e) {
            LOGGER.severe(e.getMessage());
            throw new ActionException(e);
        }

        return ret;
    }

    /**
     * To B3log Solo article(Key transformation) for the specified external blog
     * article.
     *
     * @param externalArticle the specified external Blog article
     * @return Solo article
     * @throws Exception exception
     */
    private JSONObject toSoloArticle(final JSONObject externalArticle)
            throws Exception {
        final JSONObject ret = new JSONObject();

        ret.put(Keys.OBJECT_ID, externalArticle.getString(Keys.OBJECT_ID));
        ret.put(Article.ARTICLE_TITLE,
                externalArticle.getString(BLOG_SYNC_EXTERNAL_ARTICLE_TITLE));
        ret.put(Article.ARTICLE_ABSTRACT,
                externalArticle.getString(BLOG_SYNC_EXTERNAL_ARTICLE_ABSTRACT));
        ret.put(Article.ARTICLE_CONTENT,
                externalArticle.getString(BLOG_SYNC_EXTERNAL_ARTICLE_CONTENT));
        ret.put(Article.ARTICLE_CREATE_DATE,
                externalArticle.get(BLOG_SYNC_EXTERNAL_ARTICLE_CREATE_DATE));
        ret.put(Article.ARTICLE_TAGS_REF,
                externalArticle.getString(BLOG_SYNC_EXTERNAL_ARTICLE_CATEGORIES));

        ret.put(Article.ARTICLE_VIEW_COUNT, 0);
        ret.put(Article.ARTICLE_COMMENT_COUNT, 0);

        return ret;
    }
}
