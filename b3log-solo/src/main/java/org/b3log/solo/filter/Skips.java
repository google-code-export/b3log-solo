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

package org.b3log.solo.filter;

import java.util.logging.Logger;

/**
 * Skips for request filtering.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.3.4, Jul 11, 2011
 * @see #shouldSkip(java.lang.String) 
 */
public final class Skips {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(Skips.class.getName());
    /**
     * Administrator action serve URLs.
     */
    public static final String[] ADMIN_ACTIONS = new String[]{
        "/admin-index.do",
        "/admin-article.do",
        "/admin-article-list.do",
        "/admin-link-list.do",
        "/admin-preference.do",
        "/admin-article-sync.do",
        "/admin-file-list.do",
        "/admin-page-list.do",
        "/admin-others.do",
        "/admin-draft-list.do",
        "/admin-user-list.do",
        "/admin-plugin-list.do",
        "/admin-cache.do", // XXX: plugin conf hard coding....
        "/rm-all-data.do",
        "/init.do",
        "/clear-cache.do",
        "/tencent-microblog-oauth-authorize-token.do",
        "/tencent-microblog-oauth-callback.do"
    };

    /**
     * Determines whether the specified request URI should be skipped filter.
     *
     * <p>
     *   <b>Note</b>: This method SHOULD be invoked for all filters with pattern
     *   "/*".
     * </p>
     *
     * @param requestURI the specified request URI
     * @return {@code true} if should be skipped, {@code false} otherwise
     */
    // XXX: performance issue, super hard coding....
    // TODO: skips new urls....
    static boolean shouldSkip(final String requestURI) {
        return requestURI.equals("/json-rpc.do")
               || requestURI.equals("/captcha.do")
               || requestURI.equals("/tag-articles-feed.do")
               || requestURI.equals("/blog-articles-feed.do")
               || requestURI.equals("/file-access.do")
               || requestURI.equals("/check-login.do")
               || requestURI.equals("/add-article-comment.do")
               || requestURI.equals("/add-page-comment.do")
               || requestURI.equals("/get-random-articles.do")
               || requestURI.equals("/article-random-double-gen.do")
               || requestURI.equals("/flush-stat.do")
               || requestURI.contains("/_ah/") // For local dev server
               || requestURI.contains("/datastore-file-access.do")
               || requestURI.contains("/skins/")
               || requestURI.contains("/images/")
               || requestURI.contains("/styles/")
               || requestURI.contains("/tags/")
               || requestURI.contains("/archives/")
               || equalAdminActions(requestURI);
    }

    /**
     * Determines whether the specified request URI is equals to admin action
     * URI patterns.
     *
     * @param requestURI the specified request URI
     * @return {@code true} if it is equals to, {@code false} otherwise
     * @see #ADMIN_ACTIONS
     */
    private static boolean equalAdminActions(final String requestURI) {
        for (int i = 0; i < ADMIN_ACTIONS.length; i++) {
            if (ADMIN_ACTIONS[i].equals(requestURI)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Private default constructor.
     */
    private Skips() {
    }
}
