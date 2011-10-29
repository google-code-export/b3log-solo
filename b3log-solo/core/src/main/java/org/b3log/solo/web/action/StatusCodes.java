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
     * Indicates get articles successfully.
     */
    GET_ARTICLES_SUCC,
    /**
     * Indicates get an article successfully.
     */
    GET_ARTICLE_SUCC,
    /**
     * Indicates comment an article successfully.
     */
    COMMENT_ARTICLE_SUCC,
    /**
     * Indicates comment a page successfully.
     */
    COMMENT_PAGE_SUCC,
    /**
     * Indicates captcha error.
     */
    CAPTCHA_ERROR,
    /**
     * Indicates upload a file successfully.
     */
    UPLOAD_FILES_SUCC,
}
