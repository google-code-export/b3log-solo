/*
 * Copyright (c) 2009, 2010, 2011, B3log Team
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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.b3log.latke.Keys;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * This class defines all comment model relevant keys.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.2.2, Jun 19, 2011
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
     * Most view article display count.
     */
    public static final String MOST_VIEW_ARTICLE_DISPLAY_CNT =
            "mostViewArticleDisplayCount";
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
     * Administrator's email.
     */
    public static final String ADMIN_EMAIL = "adminEmail";
    /**
     * Locale string.
     */
    public static final String LOCALE_STRING = "localeString";
    /**
     * Time zone id.
     */
    public static final String TIME_ZONE_ID = "timeZoneId";
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
     * Key of article update hint flag.
     */
    public static final String ENABLE_ARTICLE_UPDATE_HINT =
            "enableArticleUpdateHint";
    /**
     * Key of current version number.
     */
    public static final String CURRENT_VERSION_NUMBER =
            "currentVersionNumber";
    /**
     * Key of signs.
     */
    public static final String SIGNS = "signs";
    /**
     * Key of tencent micro blog app key.
     */
    public static final String TENCENT_MICROBLOG_APP_KEY =
            "tencentMicroblogAppKey";
    /**
     * Key of tencent micro blog app secret.
     */
    public static final String TENCENT_MICROBLOG_APP_SECRET =
            "tencentMicroblogAppSecret";
    /**
     * Key of tencent micro blog token key.
     */
    public static final String TENCENT_MICROBLOG_TOKEN_KEY =
            "tencentMicroblogTokenKey";
    /**
     * Key of tencent micro blog token secret.
     */
    public static final String TENCENT_MICROBLOG_TOKEN_SECRET =
            "tencentMicroblogTokenSecret";
    /**
     * Key of enable post to tencent micro blog.
     */
    public static final String ENABLE_POST_TO_TENCENT_MICROBLOG =
            "enablePostToTencentMicroblog";
    /**
     * Key of key of Solo.
     */
    public static final String KEY_OF_SOLO = "keyOfSolo";
    /**
     * Key of page cache enabled.
     */
    public static final String PAGE_CACHE_ENABLED = "pageCacheEnabled";

    /**
     * Private default constructor.
     */
    private Preference() {
    }

    /**
     * Default preference.
     *
     * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
     * @version 1.0.0.8, Jan 2, 2011
     */
    public static final class Default {

        /**
         * Logger.
         */
        private static final Logger LOGGER =
                Logger.getLogger(Default.class.getName());
        /**
         * Default recent article display count.
         */
        public static final int DEFAULT_RECENT_ARTICLE_DISPLAY_COUNT = 10;
        /**
         * Default recent comment display count.
         */
        public static final int DEFAULT_RECENT_COMMENT_DISPLAY_COUNT = 10;
        /**
         * Default most used tag display count.
         */
        public static final int DEFAULT_MOST_USED_TAG_DISPLAY_COUNT = 20;
        /**
         * Default article list display count.
         */
        public static final int DEFAULT_ARTICLE_LIST_DISPLAY_COUNT = 20;
        /**
         * Default article list pagination window size.
         */
        public static final int DEFAULT_ARTICLE_LIST_PAGINATION_WINDOW_SIZE =
                15;
        /**
         * Default most comment article display count.
         */
        public static final int DEFAULT_MOST_COMMENT_ARTICLE_DISPLAY_COUNT = 5;
        /**
         * Default blog title.
         */
        public static final String DEFAULT_BLOG_TITLE = "Solo 示例";
        /**
         * Default blog subtitle.
         */
        public static final String DEFAULT_BLOG_SUBTITLE = "GAE 开源博客";
        /**
         * Default skin directory name.
         */
        public static final String DEFAULT_SKIN_DIR_NAME = "classic";
        /**
         * Default blog host.
         */
        public static final String DEFAULT_BLOG_HOST = "localhost:8080";
        /**
         * Default language.
         */
        public static final String DEFAULT_LANGUAGE = "zh_CN";
        /**
         * Default time zone.
         * 
         * @see java.util.TimeZone#getAvailableIDs() 
         */
        public static final String DEFAULT_TIME_ZONE = "Asia/Shanghai";
        /**
         * Default enable article update hint.
         */
        public static final boolean DEFAULT_ENABLE_ARTICLE_UPDATE_HINT = true;
        /**
         * Default notice board.
         */
        public static final String DEFAULT_NOTICE_BOARD =
                "Open Source, Open Mind, <br/>Open Sight, Open Future!";
        /**
         * Default meta keywords..
         */
        public static final String DEFAULT_META_KEYWORDS =
                "GAE 博客,GAE blog,b3log";
        /**
         * Default meta description..
         */
        public static final String DEFAULT_META_DESCRIPTION =
                "An open source blog based on GAE Java. GAE Java 开源博客";
        /**
         * Default HTML head to append.
         */
        public static final String DEFAULT_HTML_HEAD = "";
        /**
         * Default OAuth consumer secret for Google.
         */
        public static final String DEFAULT_GOOLE_OAUTH_CONSUMER_SECRET = "";
        /**
         * Default enable post to Buzz.
         */
        public static final boolean DEFAULT_ENABLE_POST_TO_BUZZ = false;
        /**
         * Default relevant articles display count.
         */
        public static final int DEFAULT_RELEVANT_ARTICLES_DISPLAY_COUNT = 5;
        /**
         * Default random articles display count.
         */
        public static final int DEFAULT_RANDOM_ARTICLES_DISPLAY_COUNT = 5;
        /**
         * Default external relevant articles display count.
         */
        public static final int DEFAULT_EXTERNAL_RELEVANT_ARTICLES_DISPLAY_COUNT =
                5;
        /**
         * Most view articles display count.
         */
        public static final int DEFAULT_MOST_VIEW_ARTICLES_DISPLAY_COUNT = 5;
        /**
         * Default signs.
         */
        public static final String DEFAULT_SIGNS;
        /**
         * Default page cache enabled.
         */
        public static final boolean DEFAULT_PAGE_CACHE_ENABLED = true;

        static {
            final JSONArray signs = new JSONArray();

            final int signLength = 4;
            try {
                for (int i = 0; i < signLength; i++) {
                    final JSONObject sign = new JSONObject();
                    sign.put(Keys.OBJECT_ID, i);
                    signs.put(sign);

                    sign.put(Sign.SIGN_HTML, "");
                }

                // Sign(id=0) is the 'empty' sign, used for article user needn't
                // a sign

                DEFAULT_SIGNS = signs.toString();
            } catch (final Exception e) {
                LOGGER.log(Level.SEVERE, "Creates sign error!", e);
                throw new IllegalStateException(e);
            }
        }

        /**
         * Private default constructor.
         */
        private Default() {
        }
    }
}
