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
 * This class defines all comment model relevant keys.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.1.3, Oct 26, 2010
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
     * Relevant articles display count.
     */
    public static final String RELEVANT_ARTICLES_DISPLAY_CNT =
            "relevantArticlesDisplayCount";
    /**
     * Random articles display count.
     */
    public static final String RANDOM_ARTICLES_DISPLAY_CNT =
            "randomArticlesDisplayCount";
    /**
     * External relevant articles display count.
     */
    public static final String EXTERNAL_RELEVANT_ARTICLES_DISPLAY_CNT =
            "externalRelevantArticlesDisplayCount";
    /**
     * Recent article display count.
     */
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
    public static final String GOOGLE_OAUTH_CONSUMER_SECRET =
            "googleOAuthConsumerSecret";
    /**
     * Key of Google Buzz token.
     */
    public static final String GOOGLE_BUZZ_TOKEN =
            "googleBuzzToken";
    /**
     * Key of Google Buzz verifier.
     */
    public static final String GOOGLE_BUZZ_VERIFIER =
            "googleBuzzVerifier";
    /**
     * Key of enable post to Buzz.
     */
    public static final String ENABLE_POST_TO_BUZZ =
            "enablePostToBuzz";
    /**
     * Key of meta keywords.
     */
    public static final String META_KEYWORDS =
            "metaKeywords";
    /**
     * Key of meta description.
     */
    public static final String META_DESCRIPTION =
            "metaDescription";

    /**
     * Private default constructor.
     */
    private Preference() {
    }
}
