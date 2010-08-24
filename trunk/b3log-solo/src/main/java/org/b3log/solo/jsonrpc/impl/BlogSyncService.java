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
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.client.action.ActionException;
import org.b3log.solo.jsonrpc.AbstractJSONRpcService;
import org.b3log.solo.util.ArticleUtils;
import org.b3log.solo.util.TagUtils;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.BlogSync;
import org.b3log.solo.repository.ArticleRepository;
import org.b3log.solo.repository.CSDNBlogArticleRepository;
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
 * @version 1.0.0.6, Aug 24, 2010
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
     * CSDN blog article retrieval count incremental.
     */
    public static final int CSDN_BLOG_ARTICLE_RETRIEVAL_COUNT_INCREMENTAL = 2;

    /**
     * Imports CSDN blog article by the specified request json object and http
     * servlet request.
     *
     * @param requestJSONObject the specified request json object, for example,
     * <pre>
     * {
     *     "blogSyncCSDNBlogUserName": "",
     *     "blogSyncCSDNBlogArticleIds": ["", "", ....]
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
            final String csdnBlogUserName =
                    requestJSONObject.getString(
                    BlogSync.BLOG_SYNC_CSDN_BLOG_USER_NAME);
            final JSONArray articleIds = requestJSONObject.getJSONArray(
                    BlogSync.BLOG_SYNC_CSDN_BLOG_ARTICLE_IDS);
            final List<String> importedIds = new ArrayList<String>();
            for (int i = 0; i < articleIds.length(); i++) {
                final String articleId = articleIds.getString(i);
                final JSONObject article = csdnBlogArticleRepository.get(
                        articleId);

                toSoloArticle(article);

                final String categoriesString =
                        article.getString(
                        BlogSync.BLOG_SYNC_CSDN_BLOG_ARTICLE_CATEGORIES);
                final String[] tagTitles = categoriesString.split(",");
                @SuppressWarnings(value = "unchecked")
                final JSONArray tags = tagUtils.tag(tagTitles, article);
                articleUtils.addTagArticleRelation(tags, article);

                articleRepository.importArticle(article);
                importedIds.add(articleId);

                statistics.incBlogArticleCount();

                archiveDateUtils.archiveDate(article);
            }

            ret.put(Keys.OBJECT_ID + "s", importedIds);
        } catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new ActionException(e);
        }

        return ret;
    }

    /**
     * To B3log Solo article(Key transformation) for the specified article.
     *
     * @param article the specified article
     * @throws Exception exception
     */
    private void toSoloArticle(final JSONObject article) throws Exception {
        article.put(Keys.OBJECT_ID,
                    article.getString(BlogSync.BLOG_SYNC_CSDN_BLOG_ARTICLE_ID));
        article.put(Article.ARTICLE_TITLE,
                    article.getString(BlogSync.BLOG_SYNC_CSDN_BLOG_ARTICLE_TITLE));
        article.put(Article.ARTICLE_ABSTRACT,
                    article.getString(
                BlogSync.BLOG_SYNC_CSDN_BLOG_ARTICLE_ABSTRACT));
        article.put(Article.ARTICLE_CONTENT,
                    article.getString(
                BlogSync.BLOG_SYNC_CSDN_BLOG_ARTICLE_CONTENT));
        article.put(Article.ARTICLE_CREATE_DATE,
                    article.get(BlogSync.BLOG_SYNC_CSDN_BLOG_ARTICLE_CREATE_DATE));

        article.put(Article.ARTICLE_VIEW_COUNT, 0);
        article.put(Article.ARTICLE_COMMENT_COUNT, 0);
    }

    /**
     * Gest CSDN blog articles by the specified request json object.
     *
     * @param requestJSONObject the specified request json object, for example,
     * <pre>
     * {
     *     "blogSyncCSDNBlogUserName": "",
     *     "blogSyncCSDNBlogArchiveDate": "2006/12"
     * }
     * </pre>
     * @param request the specified http servlet request
     * @param response the specified http servlet response
     * @return for example,
     * <pre>
     * {
     *     "blogSyncCSDNBlogArticles": [{
     *         "blogSyncCSDNBlogArticleId": "",
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

        final JSONObject ret = new JSONObject();

        try {
            final String csdnBlogUserName =
                    requestJSONObject.getString(
                    BlogSync.BLOG_SYNC_CSDN_BLOG_USER_NAME);
            final String archiveDate = requestJSONObject.getString(
                    BlogSync.BLOG_SYNC_CSDN_BLOG_ARCHIVE_DATE);
            final List<String> articleIds =
                    csdnBlog.getArticleIdsByArchiveDate(csdnBlogUserName,
                                                        archiveDate);
            LOGGER.debug("There are [" + articleIds.size()
                         + "] articles of CSDN"
                         + "blog user[userName=" + csdnBlogUserName + "]"
                         + " in [" + archiveDate + "]");
            final JSONArray articles = new JSONArray();
            ret.put(BlogSync.BLOG_SYNC_CSDN_BLOG_ARTICLES, articles);
            int retrievalCnt = 0;
            for (final String articleId : articleIds) {
                final boolean imported = articleRepository.has(articleId);
                final boolean csdnTmpImported =
                        csdnBlogArticleRepository.has(articleId);
                // assert imported == csdnTmpImported for consistency

                JSONObject article = null;
                if (csdnTmpImported) {
                    article = csdnBlogArticleRepository.get(articleId);
                } else { // Not retrieved yet, get the article from CSDN
                    final CSDNBlogArticle csdnBlogArticle =
                            csdnBlog.getArticleById(csdnBlogUserName,
                                                    articleId);
                    if (null != csdnBlogArticle) {
                        article = csdnBlogArticle.toJSONObject();
                        csdnBlogArticleRepository.add(article);
                    }

                    retrievalCnt++;
                    if (CSDN_BLOG_ARTICLE_RETRIEVAL_COUNT_INCREMENTAL
                        == retrievalCnt) {
                        break;
                    }
                }

                article.put(BlogSync.BLOG_SYNC_IMPORTED, imported);
                articles.put(article);
            }
        } catch (final Exception e) {
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
     *     "blogSyncCSDNBlogUserName": ""
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
                    BlogSync.BLOG_SYNC_CSDN_BLOG_USER_NAME);
            final List<String> archiveDates =
                    csdnBlog.getArchiveDates(csdnBlogUserName);

            ret.put(BlogSync.BLOG_SYNC_CSDN_BLOG_ARCHIVE_DATES, archiveDates);
        } catch (final JSONException e) {
            LOGGER.error(e.getMessage(), e);
            throw new ActionException(e);
        }

        return ret;
    }
}
