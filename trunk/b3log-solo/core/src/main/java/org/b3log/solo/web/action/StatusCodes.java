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
package org.b3log.solo.web.action;

/**
 * This enumeration defines all response status codes of actions.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.2.5, Oct 18, 2011
 */
public enum StatusCodes {

    /**
     * Indicates add an article successfully.
     */
    ADD_ARTICLE_SUCC,
    /**
     * Indicates add an article fails, caused by duplicated permalink.
     */
    ADD_ARTICLE_FAIL_DUPLICATED_PERMALINK,
    /**
     * Indicates add an article fails, caused by invalid permalink format.
     */
    ADD_ARTICLE_FAIL_INVALID_PERMALINK_FORMAT,
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
     * Indicates remove an article fails.
     */
    REMOVE_ARTICLE_FAIL_,
    /**
     * Indicates remove an article fails caused by forbidden.
     */
    REMOVE_ARTICLE_FAIL_FORBIDDEN,
    /**
     * Indicates put top an article successfully.
     */
    PUT_TOP_ARTICLE_SUCC,
    /**
     * Indicates put top an article fails.
     */
    PUT_TOP_ARTICLE_FAIL_,
    /**
     * Indicates put top an article fails caused by forbidden.
     */
    PUT_TOP_ARTICLE_FAIL_FORBIDDEN,
    /**
     * Indicates cancel top an article successfully.
     */
    CANCEL_TOP_ARTICLE_SUCC,
    /**
     * Indicates cancel top an article fails.
     */
    CANCEL_TOP_ARTICLE_FAIL_,
    /**
     * Indicates cancel top an article fails caused by forbidden.
     */
    CANCEL_TOP_ARTICLE_FAIL_FORBIDDEN,
    /**
     * Indicates update an article successfully.
     */
    UPDATE_ARTICLE_SUCC,
    /**
     * Indicates update an article fails, caused by duplicated permalink.
     */
    UPDATE_ARTICLE_FAIL_DUPLICATED_PERMALINK,
    /**
     * Indicates update an article fails, caused by invalid permalink format.
     */
    UPDATE_ARTICLE_FAIL_INVALID_PERMALINK_FORMAT,
    /**
     * Indicates update an article fails caused by forbidden.
     */
    UPDATE_ARTICLE_FAIL_FORBIDDEN,
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
     * Indicates remove a comment fails caused by forbidden.
     */
    REMOVE_COMMENT_FAIL_FORBIDDEN,
    /**
     * Indicates add a link successfully.
     */
    ADD_LINK_SUCC,
    /**
     * Indicates remove a link successfully.
     */
    REMOVE_LINK_SUCC,
    /**
     * Indicates remove a link fails.
     */
    REMOVE_LINK_FAIL_,
    /**
     * Indicates update a link successfully.
     */
    UPDATE_LINK_SUCC,
    /**
     * Indicates update a link fails.
     */
    UPDATE_LINK_FAIL_,
    /**
     * Indicates update preference successfully.
     */
    UPDATE_PREFERENCE_SUCC,
    /**
     * Indicates update preference fails.
     */
    UPDATE_PREFERENCE_FAIL_,
    /**
     * Indicates update preference fails, caused by cannot set blog host as
     * "localhost" on production.
     */
    UPDATE_PREFERENCE_FAIL_CANNT_BE_LOCALHOST,
    /**
     * Indicates get preference successfully.
     */
    GET_PREFERENCE_SUCC,
    /**
     * Indicates captcha error.
     */
    CAPTCHA_ERROR,
    /**
     * Indicates add a page successfully.
     */
    ADD_PAGE_SUCC,
    /**
     * Indicates add a page fails, caused by duplicated permalink.
     */
    ADD_PAGE_FAIL_DUPLICATED_PERMALINK,
    /**
     * Indicates add an page fails, caused by invalid permalink format.
     */
    ADD_PAGE_FAIL_INVALID_PERMALINK_FORMAT,
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
     * Indicates update a page fails, caused by duplicated permalink.
     */
    UPDATE_PAGE_FAIL_DUPLICATED_PERMALINK,
    /**
     * Indicates update an page fails, caused by invalid permalink format.
     */
    UPDATE_PAGE_FAIL_INVALID_PERMALINK_FORMAT,
    /**
     * Indicates get pages successfully.
     */
    GET_PAGES_SUCC,
    /**
     * Indicates upload a file successfully.
     */
    UPLOAD_FILES_SUCC,
    /**
     * Indicates initializes B3log Solo successfully.
     */
    INIT_B3LOG_SOLO_SUCC,
    /**
     * Indicates cancels a published article successfully.
     */
    CANCEL_PUBLISH_ARTICLE_SUCC,
    /**
     * Indicates cancels a published article fails.
     */
    CANCEL_PUBLISH_ARTICLE_FAIL_,
    /**
     * Indicates cancels a published article fails caused by forbidden.
     */
    CANCEL_PUBLISH_ARTICLE_FAIL_FORBIDDEN,
    /**
     * Indicates adds a user successfully.
     */
    ADD_USER_SUCC,
    /**
     * Indicates adds a user fails, caused by duplicated user email.
     */
    ADD_USER_FAIL_DUPLICATED_EMAIL,
    /**
     * Indicates removes a user successfully.
     */
    REMOVE_USER_SUCC,
    /**
     * Indicates updates a user successfully.
     */
    UPDATE_USER_SUCC,
    /**
     * Indicates updates a user fails.
     */
    UPDATE_USER_FAIL_,
    /**
     * Indicates get users successfully.
     */
    GET_USERS_SUCC,
    /**
     * Indicates get user successfully.
     */
    GET_USER_SUCC,
    /**
     * Indicates get plugins successfully.
     */
    GET_PLUGINS_SUCC
}
