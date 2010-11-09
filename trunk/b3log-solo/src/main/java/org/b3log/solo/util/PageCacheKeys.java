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

package org.b3log.solo.util;

import org.apache.commons.lang.StringUtils;
import org.b3log.latke.util.Strings;

/**
 * Page cache key utilities.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Oct 27, 2010
 */
public final class PageCacheKeys {

    /**
     * Gets page cache key by the specified URI and query string.
     *
     * @param uri the specified URI
     * @param queryString the specified query string
     * @return cache key
     */
    // XXX: more generally?
    public static String getPageCacheKey(final String uri,
                                         final String queryString) {
        String ret = null;
        if (uri.endsWith(".html")) { // article permalink
            final String articleId = StringUtils.substring(
                    uri, uri.lastIndexOf("/") + 1, uri.lastIndexOf("."));
            ret = "/article-detail.do?oId=" + articleId;
        } else {
            ret = uri;
            if (!Strings.isEmptyOrNull(queryString)) {
                ret += "?" + queryString;
            }
        }

        return ret;
    }

    /**
     * Private default constructor.
     */
    private PageCacheKeys() {
    }
}
