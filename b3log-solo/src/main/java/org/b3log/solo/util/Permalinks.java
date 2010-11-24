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
import java.util.HashSet;
import java.util.Set;
import org.b3log.solo.repository.ArticleRepository;
import org.b3log.solo.repository.PageRepository;

/**
 * Permalink utilities.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Nov 24, 2010
 */
public final class Permalinks {

    /**
     * Article repository.
     */
    @Inject
    private ArticleRepository articleRepository;
    /**
     * Page repository.
     */
    @Inject
    private PageRepository pageRepository;
    /**
     * Reserved permalinks.
     */
    private static final Set<String> RESERVED_LINKS = new HashSet<String>();

    static {
        RESERVED_LINKS.add("/");
        RESERVED_LINKS.add("/admin-index.do");
        RESERVED_LINKS.add("/init.do");
        RESERVED_LINKS.add("/admin-index.do");
        RESERVED_LINKS.add("/index.do");
        RESERVED_LINKS.add("/article-detail.do");
        RESERVED_LINKS.add("/tag-articles.do");
        RESERVED_LINKS.add("/archive-date-articles.do");
        RESERVED_LINKS.add("/tags.do");
        RESERVED_LINKS.add("/tags.html");
        RESERVED_LINKS.add("/tags");
        RESERVED_LINKS.add("/page.do");
        RESERVED_LINKS.add("/admin-article.do");
        RESERVED_LINKS.add("/admin-article-list.do");
        RESERVED_LINKS.add("/admin-link-list.do");
        RESERVED_LINKS.add("/admin-preference.do");
        RESERVED_LINKS.add("/admin-article-sync.do");
        RESERVED_LINKS.add("/admin-file-list.do");
        RESERVED_LINKS.add("/admin-page.do");
        RESERVED_LINKS.add("/admin-others.do");
        RESERVED_LINKS.add("/blog-articles-feed.do");
        RESERVED_LINKS.add("/tag-articles-feed.do");
        RESERVED_LINKS.add("/captcha.do");
        RESERVED_LINKS.add("/captcha.do");
        RESERVED_LINKS.add("/error.do");
        RESERVED_LINKS.add("/file-access.do");
        RESERVED_LINKS.add("/datastore-file-access.do");
        RESERVED_LINKS.add("/live.do");
    }

    /**
     * Determines whether the specified permalink exists.
     *
     * @param permalink the specified permalink
     * @return {@code true} if exists, returns {@code false} otherwise
     */
    public boolean exist(final String permalink) {
        return isReserved(permalink)
               || null != articleRepository.getByPermalink(permalink)
               || null != pageRepository.getByPermalink(permalink);
    }

    /**
     * Determines whether the specified permalink reserved.
     *
     * @param permalink the specified permalink
     * @return {@code true} if reserved, returns {@code false} otherwise
     */
    private boolean isReserved(final String permalink) {
        for (final String reservedLink : RESERVED_LINKS) {
            if ("/".equals(reservedLink)) {
                continue;
            }

            if (permalink.contains(reservedLink)) {
                return true;
            }
        }

        return false;
    }
}
