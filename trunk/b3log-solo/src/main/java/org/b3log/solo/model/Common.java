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
 * This class defines all common model relevant keys.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.1.1, Sep 11, 2010
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
     * Previous article id.
     */
    public static final String PREVIOUS_ARTICLE_ID = "previousArticleId";
    /**
     * Next article id.
     */
    public static final String NEXT_ARTICLE_ID = "nextArticleId";
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
     * Private default constructor.
     */
    private Common() {
    }
}
