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
 * @version 1.0.0.0, Aug 14, 2010
 */
public final class Preference {

    /**
     * Preference.
     */
    public static final String PREFERENCE = "preference";
    /**
     * Recent article display count.
     */
    public static final String RECENT_ARTICLE_DISPLAY_CNT = "recentArticleDisplayCount";
    /**
     * Most used tag display count.
     */
    public static final String MOST_USED_TAG_DISPLAY_CNT = "mostUsedTagDisplayCount";
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
     * Private default constructor.
     */
    private Preference() {
    }
}
