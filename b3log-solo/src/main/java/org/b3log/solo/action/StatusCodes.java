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

package org.b3log.solo.action;

/**
 * This enumeration defines all response status codes of actions.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.1.4, Nov 4, 2010
 */
public enum StatusCodes {

    /**
     * Indicates add an article successfully.
     */
    ADD_ARTICLE_SUCC,
    /**
     * Indicates get articles successfully.
     */
    GET_ARTICLES_SUCC,
    /**
     * Indicates get an article successfully.
     */
    GET_ARTICLE_SUCC,
    /**
     * Indicates remove an article successfully.
     */
    REMOVE_ARTICLE_SUCC,
    /**
     * Indicates put top an article successfully.
     */
    PUT_TOP_ARTICLE_SUCC,
    /**
     * Indicates put top an article fails.
     */
    PUT_TOP_ARTICLE_FAIL_,
    /**
     * Indicates cancel top an article successfully.
     */
    CANCEL_TOP_ARTICLE_SUCC,
    /**
     * Indicates cancel top an article fails.
     */
    CANCEL_TOP_ARTICLE_FAIL_,
    /**
     * Indicates update an article successfully.
     */
    UPDATE_ARTICLE_SUCC,
    /**
     * Indicates comment an article successfully.
     */
    COMMENT_ARTICLE_SUCC,
    /**
     * Indicates comment a page successfully.
     */
    COMMENT_PAGE_SUCC,
    /**
     * Indicates get comments successfully.
     */
    GET_COMMENTS_SUCC,
    /**
     * Indicates remove a comment successfully.
     */
    REMOVE_COMMENT_SUCC,
    /**
     * Indicates add a link successfully.
     */
    ADD_LINK_SUCC,
    /**
     * Indicates remove a link successfully.
     */
    REMOVE_LINK_SUCC,
    /**
     * Indicates update a link successfully.
     */
    UPDATE_LINK_SUCC,
    /**
     * Indicates get a link successfully.
     */
    GET_LINK_SUCC,
    /**
     * Indicates get links successfully.
     */
    GET_LINKS_SUCC,
    /**
     * Indicates update preference successfully.
     */
    UPDATE_PREFERENCE_SUCC,
    /**
     * Indicates update preference fails.
     */
    UPDATE_PREFERENCE_FAIL_,
    /**
     * Indicates get preference successfully.
     */
    GET_PREFERENCE_SUCC,
    /**
     * Indicates set blog sync management successfully.
     */
    SET_BLOG_SYNC_MGMT_SUCC,
    /**
     * Indicates captcha error.
     */
    CAPTCHA_ERROR,
    /**
     * Indicates add a page successfully.
     */
    ADD_PAGE_SUCC,
    /**
     * Indicates remove a page successfully.
     */
    REMOVE_PAGE_SUCC,
    /**
     * Indicates update a page successfully.
     */
    UPDATE_PAGE_SUCC,
    /**
     * Indicates get a page successfully.
     */
    GET_PAGE_SUCC,
    /**
     * Indicates get pages successfully.
     */
    GET_PAGES_SUCC,
    /**
     * Indicates remove unused tags successfully.
     */
    REMOVE_UNUSED_TAGS_SUCC,
    /**
     * Indicates remove unused tags fails.
     */
    REMOVE_UNUSED_TAGS_FAIL_,
    /**
     * Indicates get files successfully.
     */
    GET_FILES_SUCC,
    /**
     * Indicates upload a file successfully.
     */
    UPLOAD_FILES_SUCC,
    /**
     * Indicates remove a file successfully.
     */
    REMOVE_FILE_SUCC,
    /**
     * Indicates update a file successfully.
     */
    UPDATE_FILE_SUCC,
}
