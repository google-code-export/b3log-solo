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

import com.google.inject.Inject;
import java.util.logging.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.util.Strings;
import org.b3log.solo.action.impl.TagsAction;
import org.b3log.solo.repository.ArticleRepository;
import org.b3log.solo.repository.PageRepository;
import org.json.JSONObject;

/**
 * Page cache key utilities.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Nov 17, 2010
 */
public final class PageCacheKeys {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(PageCacheKeys.class.getName());
    /**
     * Page repository.
     */
    @Inject
    private PageRepository pageRepository;
    /**
     * Article repository.
     */
    @Inject
    private ArticleRepository articleRepository;

    /**
     * Gets page cache key by the specified URI and query string.
     *
     * @param uri the specified URI
     * @param queryString the specified query string
     * @return cache key
     */
    // XXX: Performance issue
    public String getPageCacheKey(final String uri,
                                  final String queryString) {
        String ret = null;

        try {
            if ("/tags.html".equals(uri) || "/tags.do".equals(uri)) {
                return TagsAction.CACHE_KEY;
            }

            final JSONObject page = pageRepository.getByPermalink(uri);
            if (null != page) {
                ret = "/page.do?oId=" + page.getString(Keys.OBJECT_ID);

                return ret;
            }

            final JSONObject article = articleRepository.getByPermalink(uri);
            if (null != article) {
                ret = "/article-detail.do?oId="
                      + article.getString(Keys.OBJECT_ID);

                return ret;
            }

            ret = uri;
            if (!Strings.isEmptyOrNull(queryString)) {
                ret += "?" + queryString;

                return ret;
            }
        } catch (final Exception e) {
            LOGGER.severe(e.getMessage());
        }

        return ret;
    }
}
