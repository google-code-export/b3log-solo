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

package org.b3log.solo.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.b3log.solo.filter.PageCacheFilter;
import org.b3log.solo.repository.ArticleRepository;
import org.b3log.solo.repository.PageRepository;
import org.b3log.solo.repository.impl.ArticleGAERepository;
import org.b3log.solo.repository.impl.PageGAERepository;

/**
 * Permalink utilities.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.6, Jan 26, 2011
 */
public final class Permalinks {

    /**
     * Article repository.
     */
    private ArticleRepository articleRepository =
            ArticleGAERepository.getInstance();
    /**
     * Page repository.
     */
    private PageRepository pageRepository =
            PageGAERepository.getInstance();
    /**
     * Reserved permalinks.
     */
    private static final Set<String> RESERVED_LINKS = new HashSet<String>();

    static {
        RESERVED_LINKS.add("/");
        RESERVED_LINKS.add("/index.do");
        RESERVED_LINKS.add("/article-detail.do");
        RESERVED_LINKS.add("/tag-articles.do");
        RESERVED_LINKS.add("/archive-date-articles.do");
        RESERVED_LINKS.add("/tags.do");
        RESERVED_LINKS.add("/tags.html");
        RESERVED_LINKS.add("/tags");
        RESERVED_LINKS.add("/page.do");
        RESERVED_LINKS.add("/blog-articles-feed.do");
        RESERVED_LINKS.add("/tag-articles-feed.do");
        RESERVED_LINKS.add("/captcha.do");
        RESERVED_LINKS.add("/error.do");
        RESERVED_LINKS.add("/file-access.do");
        RESERVED_LINKS.add("/datastore-file-access.do");

        RESERVED_LINKS.addAll(Arrays.asList(PageCacheFilter.ADMIN_ACTIONS));
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
               || null != pageRepository.getByPermalink(permalink)
               || permalink.endsWith(".ftl");
    }

    /**
     * Determines whether the specified permalink reserved.
     *
     * @param permalink the specified permalink
     * @return {@code true} if reserved, returns {@code false} otherwise
     */
    private boolean isReserved(final String permalink) {
        if ("/".equals(permalink)) {
            return true;
        }

        for (final String reservedLink : RESERVED_LINKS) {
            if ("/".equals(reservedLink)) {
                continue;
            }

            if (permalink.startsWith(reservedLink)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Gets the {@link Permalinks} singleton.
     *
     * @return the singleton
     */
    public static Permalinks getInstance() {
        return SingletonHolder.SINGLETON;
    }

    /**
     * Private default constructor.
     */
    private Permalinks() {
    }

    /**
     * Singleton holder.
     *
     * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
     * @version 1.0.0.0, Jan 12, 2011
     */
    private static final class SingletonHolder {

        /**
         * Singleton.
         */
        private static final Permalinks SINGLETON = new Permalinks();

        /**
         * Private default constructor.
         */
        private SingletonHolder() {
        }
    }
}
