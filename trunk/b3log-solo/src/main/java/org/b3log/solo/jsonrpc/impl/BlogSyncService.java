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
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.client.action.ActionException;
import org.b3log.latke.client.remote.AbstractRemoteService;
import org.b3log.solo.util.ArticleUtils;
import org.b3log.solo.util.TagUtils;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.BlogSync;
import org.b3log.solo.repository.ArticleRepository;
import org.b3log.solo.repository.TagRepository;
import org.b3log.solo.sync.csdn.blog.CSDNBlog;
import org.b3log.solo.sync.csdn.blog.CSDNBlogArticle;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Blog sync service for JavaScript client.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Aug 17, 2010
 */
public final class BlogSyncService extends AbstractRemoteService {

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
     * Tag-Article repository.
     */
    @Inject
    private ArticleRepository tagArticleRepository;
    /**
     * Tag repository.
     */
    @Inject
    private TagRepository tagRepository;
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
     * Maximum length of an article abstract.
     */
    private static final int MAX_ABSTRACT_LENGTH = 500;

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
            ret.put(Keys.OBJECT_ID + "s", importedIds);
            for (int i = 0; i < articleIds.length(); i++) {
                final String articleId = articleIds.getString(i);
                final CSDNBlogArticle csdnBlogArticle = csdnBlog.getArticleById(
                        csdnBlogUserName, articleId);
                final String title = csdnBlogArticle.getTitle();
                final Set<String> categories = csdnBlogArticle.getCategoris();
                final Date createDate = csdnBlogArticle.getCreateDate();
                final String content = csdnBlogArticle.getContent();
                final int conentLength = content.length();
                final String summary = content.substring(
                        0, conentLength >= MAX_ABSTRACT_LENGTH
                        ? MAX_ABSTRACT_LENGTH : conentLength / 2);

                final JSONObject article = new JSONObject();
                article.put(Keys.OBJECT_ID, articleId);
                article.put(Article.ARTICLE_TITLE, title);
                article.put(Article.ARTICLE_ABSTRACT, summary);
                article.put(Article.ARTICLE_CONTENT, content);
                article.put(Article.ARTICLE_CREATE_DATE, createDate);
                final JSONArray tags = tagUtils.tag(categories.toArray(
                        new String[0]), article);

                article.put(Article.ARTICLE_COMMENT_COUNT, 0);
                articleUtils.addTagArticleRelation(tags, article);

                articleRepository.importArticle(article);
                importedIds.add(articleId);
            }

        } catch (final Exception e) {
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
     *         "blogSyncCSDNBlogArticleCategories": ["", "", ....],
     *         "blogSyncCSDNBlogArticleContent": ""
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
            final String archiveDate =
                    requestJSONObject.getString(
                    BlogSync.BLOG_SYNC_CSDN_BLOG_ARCHIVE_DATE);
            final List<String> articleIds =
                    csdnBlog.getArticleIdsByArchiveDate(csdnBlogUserName,
                                                        archiveDate);

            final JSONArray csdnBlogArticles = new JSONArray();
            ret.put(BlogSync.BLOG_SYNC_CSDN_BLOG_ARTICLES, csdnBlogArticles);
            for (final String articleId : articleIds) {
                final CSDNBlogArticle csdnBlogArticle =
                        csdnBlog.getArticleById(csdnBlogUserName,
                                                articleId);
                csdnBlogArticles.put(csdnBlogArticle.toJSONObject());
            }
        } catch (final JSONException e) {
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
