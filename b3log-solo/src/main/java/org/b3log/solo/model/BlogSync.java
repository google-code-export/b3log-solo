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
package org.b3log.solo.model;

/**
 * This class defines all blog sync model relevant keys.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.4, Sep 4, 2010
 */
public final class BlogSync {

    /**
     * Key of blog sync management.
     */
    public static final String BLOG_SYNC_MANAGEMENT = "blogSyncManagement";
    /**
     * Key of CSDN blog.
     */
    public static final String BLOG_SYNC_CSDN_BLOG = "blogSyncCSDNBlog";
    /**
     * Key of CnBlogs.
     */
    public static final String BLOG_SYNC_CNBLOGS = "blogSyncCnBlogs";
    /**
     * Key of blog sync external blogging system user name.
     */
    public static final String BLOG_SYNC_EXTERNAL_BLOGGING_SYS_USER_NAME =
            "blogSyncExternalBloggingSysUserName";
    /**
     * Key of external blogging system user password.
     */
    public static final String BLOG_SYNC_EXTERNAL_BLOGGING_SYS_USER_PASSWORD =
            "blogSyncExternalBloggingSysUserPassword";
    /**
     * Key of blog sync external blogging system.
     */
    public static final String BLOG_SYNC_EXTERNAL_BLOGGING_SYS =
            "blogSyncExternalBloggingSys";
    /**
     * Key of whether sync add external blogging system article.
     */
    public static final String BLOG_SYNC_MGMT_ADD_ENABLED =
            "blogSyncMgmtAddEnabled";
    /**
     * Key of whether sync update external blogging system article.
     */
    public static final String BLOG_SYNC_MGMT_UPDATE_ENABLED =
            "blogSyncMgmtUpdateEnabled";
    /**
     * Key of whether sync remove external blogging system article.
     */
    public static final String BLOG_SYNC_MGMT_REMOVE_ENABLED =
            "blogSyncMgmtRemoveEnabled";
    /**
     * Key of external blogging system blog archive date.
     */
    public static final String BLOG_SYNC_EXTERNAL_ARCHIVE_DATE =
            "blogSyncExternalArchiveDate";
    /**
     * Key of external blogging system blog archive dates.
     */
    public static final String BLOG_SYNC_EXTERNAL_ARCHIVE_DATES =
            "blogSyncExternalArchiveDates";
    /**
     * Key of external blogging system article.
     */
    public static final String BLOG_SYNC_EXTERNAL_ARTICLE =
            "blogSyncExternalArticle";
    /**
     * Key of external blogging system articles.
     */
    public static final String BLOG_SYNC_EXTERNAL_ARTICLES =
            "blogSyncExternalArticles";
    /**
     * Key of external blogging system article title.
     */
    public static final String BLOG_SYNC_EXTERNAL_ARTICLE_TITLE =
            "blogSyncExternalArticleTitle";
    /**
     *Key of external blogging system article create date.
     */
    public static final String BLOG_SYNC_EXTERNAL_ARTICLE_CREATE_DATE =
            "blogSyncExternalArticleCreateDate";
    /**
     * Key of external blogging system article categories.
     */
    public static final String BLOG_SYNC_EXTERNAL_ARTICLE_CATEGORIES =
            "blogSyncExternalArticleCategories";
    /**
     * Key ofexternal blogging system article content.
     */
    public static final String BLOG_SYNC_EXTERNAL_ARTICLE_CONTENT =
            "blogSyncExternalArticleContent";
    /**
     * Key ofexternal blogging system article abstract.
     */
    public static final String BLOG_SYNC_EXTERNAL_ARTICLE_ABSTRACT =
            "blogSyncExternalArticleAbstract";
    /**
     * Key of external blogging system article id.
     */
    public static final String BLOG_SYNC_EXTERNAL_ARTICLE_ID =
            "blogSyncExternalArticleId";
    /**
     * Key of imported flag.
     */
    public static final String BLOG_SYNC_IMPORTED =
            "blogSyncImported";

    /**
     * Private default constructor.
     */
    private BlogSync() {
    }
}
