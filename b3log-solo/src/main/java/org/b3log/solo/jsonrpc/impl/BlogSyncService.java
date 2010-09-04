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
     * CSDN blog article repository.
     */
    @Inject
    private CSDNBlogArticleRepository csdnBlogArticleRepository;
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

            JSONObject csdnBlogSyncMgmt = blogSyncManagementRepository.
                    getByExternalBloggingSystem(BLOG_SYNC_CSDN_BLOG);
            if (null == csdnBlogSyncMgmt) {
                csdnBlogSyncMgmt = new JSONObject();
            }

            csdnBlogSyncMgmt.put(BLOG_SYNC_EXTERNAL_BLOGGING_SYS,
                                 externalBloggingSys);
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
                LOGGER.log(Level.FINER,
                           "Added blog sync management for [{0}] [{1}]",
                           new String[]{
                            externalBloggingSys,
                            csdnBlogSyncMgmt.toString(
                            SoloServletListener.JSON_PRINT_INDENT_FACTOR)
                        });

            } else {
                blogSyncManagementRepository.update(
                        csdnBlogSyncMgmt.getString(Keys.OBJECT_ID),
                        csdnBlogSyncMgmt);
                LOGGER.log(Level.FINER,
                           "Updated blog sync management for [{0}] [{1}]",
                           new String[]{
                            externalBloggingSys,
                            csdnBlogSyncMgmt.toString(
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

                    final JSONObject csdnBlogArticle =
                            csdnBlogArticleRepository.get(oId);
                    final JSONObject soloArticle =
                            toSoloArticle(csdnBlogArticle);

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
            final CSDNBlog csdnBlog = new CSDNBlog();
            csdnBlog.setUserName(csdnBlogUserName);
            csdnBlog.setUserPassword("ignored");
            final List<String> csdnArticleIds =
                    csdnBlog.getArticleIdsByArchiveDate(archiveDate);
            LOGGER.log(Level.FINER,
                       "There are [{0}] articles of CSDN blog user[userName={1}] in [{2}]",
                       new Object[]{csdnArticleIds.size(),
                                    csdnBlogUserName,
                                    archiveDate});
            final JSONArray articles = new JSONArray();
            ret.put(BLOG_SYNC_CSDN_BLOG_ARTICLES, articles);
            int retrievalCnt = 0;
            for (final String csdnArticleId : csdnArticleIds) {
                final String oId = csdnBlogArticleSoloArticleRepository.
                        getSoloArticleId(csdnArticleId);
                LOGGER.log(Level.FINEST,
                           "CSDN article[id={0}] Solo article[id={1}]",
                           new Object[]{csdnArticleId, oId});
                final boolean imported = articleRepository.has(oId);
                final boolean csdnTmpImported =
                        csdnBlogArticleRepository.has(oId);
                // assert imported == csdnTmpImported for consistency

                JSONObject article = null;
                LOGGER.log(Level.FINER,
                           "CSDN blog article[oId={0}]'s status[csdnTmpImported={1}, imported={2}]",
                           new Object[]{oId, csdnTmpImported, imported});
                if (csdnTmpImported) {
                    article = csdnBlogArticleRepository.get(oId);
                } else { // Not retrieved yet, get the article from CSDN
                    final CSDNBlogArticle csdnBlogArticle = null;
                    // TODO: csdnBlog.getArticleById(csdnArticleId);
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
                        LOGGER.log(Level.FINER,
                                   "Added CSDN blog article-solo article relation[{0}]",
                                   csdnArticleSoloArticleRelation.toString());

                        retrievalCnt++;
                    } else {
                        LOGGER.log(Level.WARNING,
                                   "Retrieve article[csdnArticleId={0}] from CSDN blog is null",
                                   csdnArticleId);

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
            LOGGER.severe(e.getMessage());
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
            final String csdnBlogUserPwd = requestJSONObject.getString(
                    BLOG_SYNC_EXTERNAL_BLOGGING_SYS_USER_PASSWORD);
            final CSDNBlog csdnBlog = new CSDNBlog();
            csdnBlog.setUserName(csdnBlogUserName);
            csdnBlog.setUserPassword(csdnBlogUserPwd);
            final List<String> archiveDates = csdnBlog.getArchiveDates();

            ret.put(BLOG_SYNC_CSDN_BLOG_ARCHIVE_DATES, archiveDates);
        } catch (final JSONException e) {
            LOGGER.severe(e.getMessage());
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
