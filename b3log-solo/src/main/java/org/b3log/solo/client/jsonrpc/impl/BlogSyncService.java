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
package org.b3log.solo.client.jsonrpc.impl;

import com.google.inject.Inject;
import java.util.List;
import org.apache.log4j.Logger;
import org.b3log.latke.client.action.ActionException;
import org.b3log.latke.client.remote.AbstractRemoteService;
import org.b3log.solo.model.BlogSync;
import org.b3log.solo.repository.ArticleRepository;
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
     * CSDN blog.
     */
    @Inject
    private CSDNBlog csdnBlog;

    /**
     *
     * @param requestJSONObject the specified request json object, for example,
     * <pre>
     * {
     *     "blogSyncCSDNBlogUserName": "",
     *     "blogSyncCSDNBlogArchiveDate": "2006/12"
     * }
     * </pre>
     * @return for example,
     * <pre>
     * {
     *     "blogSyncCSDNBlogArticles": [{
     *         "blogSyncCSDNBlogArchiveTitle": "",
     *         "blogSyncCSDNBlogArchiveCreateDate": java.util.Date,
     *         "blogSyncCSDNBlogArchiveCategories": ["", "", ....],
     *         "blogSyncCSDNBlogArchiveContent": ""
     *     }, ....]
     * }
     * </pre>
     * @throws ActionException action exception
     */
    public JSONObject getCSDNBlogArticlesByArchiveDate(
            final JSONObject requestJSONObject) throws ActionException {
        final JSONObject ret = new JSONObject();

        try {
            final String csdnBlogUserName =
                    requestJSONObject.getString(
                    BlogSync.BLOG_SYNC_CSDN_BLOG_USER_NAME);
            final String archiveDate =
                    requestJSONObject.getString(
                    BlogSync.BLOG_SYNC_CSDN_BLOG_ARTICLE_CREATE_DATE);
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
     * @return for example,
     * <pre>
     * {
     *     "blogSyncCSDNBlogArchiveDates": ["2006/12", "2007/01", ...]
     * }
     * </pre>
     * @throws ActionException action exception
     */
    public JSONObject getCSDNBlogArticleArchiveDate(
            final JSONObject requestJSONObject)
            throws ActionException {
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
