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

package org.b3log.solo.model;

/**
 * This class defines all common model relevant keys.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.1.7, Dec 13, 2010
 */
public final class Common {

    /**
     * Most used tags.
     */
    public static final String MOST_USED_TAGS = "mostUsedTags";
    /**
     * Most comment count articles.
     */
    public static final String MOST_COMMENT_ARTICLES =
            "mostCommentArticles";
    /**
     * Most view count articles.
     */
    public static final String MOST_VIEW_COUNT_ARTICLES =
            "mostViewCountArticles";
    /**
     * Recent articles.
     */
    public static final String RECENT_ARTICLES = "recentArticles";
    /**
     * Recent comments.
     */
    public static final String RECENT_COMMENTS = "recentComments";
    /**
     * Previous article permalink.
     */
    public static final String PREVIOUS_ARTICLE_PERMALINK =
            "previousArticlePermalink";
    /**
     * Next article permalink.
     */
    public static final String NEXT_ARTICLE_PERMALINK = "nextArticlePermalink";
    /**
     * Previous article title.
     */
    public static final String PREVIOUS_ARTICLE_TITLE = "previousArticleTitle";
    /**
     * Next article title.
     */
    public static final String NEXT_ARTICLE_TITLE = "nextArticleTitle";
    /**
     * index.
     */
    public static final String INDEX = "index";
    /**
     * tag-articles.
     */
    public static final String TAG_ARTICLES = "tag-articles";
    /**
     * archive-date-articles.
     */
    public static final String ARCHIVED_DATE_ARTICLES = "archive-date-articles";
    /**
     * Action name.
     */
    public static final String ACTION_NAME = "actionName";
    /**
     * Version.
     */
    public static final String VERSION = "version";
    /**
     * Key of flag a comment is an reply or not.
     */
    public static final String IS_REPLY = "isReply";
    /**
     * Key of page navigations.
     */
    public static final String PAGE_NAVIGATIONS = "pageNavigations";
    /**
     * Key of relevant articles.
     */
    public static final String RELEVANT_ARTICLES = "relevantArticles";
    /**
     * Key of random articles.
     */
    public static final String RANDOM_ARTICLES = "randomArticles";
    /**
     * Key of has updated.
     */
    public static final String HAS_UPDATED = "hasUpdated";
    /**
     * Key of author is administrator.
     */
    // XXX: remove unused key?
    public static final String AUTHOR_IS_ADMIN = "authorIsAdmin";
    /**
     * Left part name(for skin {@value valentine}).
     */
    public static final String LEFT_PART_NAME = "_l";
    /**
     * Right part name(for skin {@value valentine}).
     */
    public static final String RIGHT_PART_NAME = "_r";
    /**
     * Author name.
     */
    public static final String AUTHOR_NAME = "authorName";
    /**
     * Author id.
     */
    public static final String AUTHOR_ID = "authorId";

    /**
     * Private default constructor.
     */
    private Common() {
    }
}
