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
 * This class defines all comment model relevant keys.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.9, Sep 14, 2010
 */
public final class Preference {

    /**
     * Preference.
     */
    public static final String PREFERENCE = "preference";
    /**
     * Blog title.
     */
    public static final String BLOG_TITLE = "blogTitle";
    /**
     * Blog subtitle.
     */
    public static final String BLOG_SUBTITLE = "blogSubtitle";
    /**
     * Recent article display count.
     */
    // XXX: recentArticleDisplayCount
    public static final String RECENT_ARTICLE_DISPLAY_CNT =
            "recentArticleDisplayCount";
    /**
     * Recent comment display count.
     */
    public static final String RECENT_COMMENT_DISPLAY_CNT =
            "recentCommentDisplayCount";
    /**
     * Most used tag display count.
     */
    public static final String MOST_USED_TAG_DISPLAY_CNT =
            "mostUsedTagDisplayCount";
    /**
     * Most comment article display count.
     */
    public static final String MOST_COMMENT_ARTICLE_DISPLAY_CNT =
            "mostCommentArticleDisplayCount";
    /**
     * Article list display count.
     */
    public static final String ARTICLE_LIST_DISPLAY_COUNT =
            "articleListDisplayCount";
    /**
     * Article list pagination window size.
     */
    public static final String ARTICLE_LIST_PAGINATION_WINDOW_SIZE =
            "articleListPaginationWindowSize";
    /**
     * Blog host.
     */
    public static final String BLOG_HOST = "blogHost";
    /**
     * Administrator's Gmail.
     */
    public static final String ADMIN_GMAIL = "adminGmail";
    /**
     * Locale string.
     */
    public static final String LOCALE_STRING = "localeString";
    /**
     * Notice board.
     */
    public static final String NOTICE_BOARD = "noticeBoard";
    /**
     * HTML head.
     */
    public static final String HTML_HEAD = "htmlHead";
    /**
     * Key of OAuth consumer secret.
     */
    public static final String GOOLE_OAUTH_CONSUMER_SECRET =
            "googleOAuthConsumerSecret";
    /**
     * Key of enable post to Buzz.
     */
    public static final String ENABLE_POST_TO_BUZZ =
            "enablePostToBuzz";

    /**
     * Private default constructor.
     */
    private Preference() {
    }
}
