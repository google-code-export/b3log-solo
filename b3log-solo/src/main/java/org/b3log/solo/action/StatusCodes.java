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
package org.b3log.solo.action;

/**
 * This enumeration defines all response status codes of actions.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.9, Sep 28, 2010
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
     * Indicates update an article successfully.
     */
    UPDATE_ARTICLE_SUCC,
    /**
     * Indicates comment an article successfully.
     */
    COMMENT_ARTICLE_SUCC,
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
}
