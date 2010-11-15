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
 * This class defines all page model relevant keys.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.3, Nov 15, 2010
 */
public final class Page {

    /**
     * Page.
     */
    public static final String PAGE = "page";
    /**
     * Pages.
     */
    public static final String PAGES = "pages";
    /**
     * Key of title.
     */
    public static final String PAGE_TITLE = "pageTitle";
    /**
     * Key of content.
     */
    public static final String PAGE_CONTENT = "pageContent";
    /**
     * Key of order.
     */
    public static final String PAGE_ORDER = "pageOrder";
    /**
     * Key of comment count.
     */
    public static final String PAGE_COMMENT_COUNT = "pageCommentCount";
    /**
     * Key of permalink.
     */
    public static final String PAGE_PERMALINK = "pagePermalink";
    /**
     * Key of comments.
     */
    public static final String PAGE_COMMENTS_REF = "pageComments";

    /**
     * Private default constructor.
     */
    private Page() {
    }
}
