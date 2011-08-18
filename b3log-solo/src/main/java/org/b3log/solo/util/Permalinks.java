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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.b3log.latke.util.Strings;
import org.b3log.solo.filter.Skips;
import org.b3log.solo.repository.ArticleRepository;
import org.b3log.solo.repository.PageRepository;
import org.b3log.solo.repository.impl.ArticleGAERepository;
import org.b3log.solo.repository.impl.PageGAERepository;

/**
 * Permalink utilities.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.9, Aug 18, 2011
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
        RESERVED_LINKS.add("/article.do");
        RESERVED_LINKS.add("/tags.html");
        RESERVED_LINKS.add("/tags");
        RESERVED_LINKS.add("/page.do");
        RESERVED_LINKS.add("/blog-articles-feed.do");
        RESERVED_LINKS.add("/tag-articles-feed.do");
        RESERVED_LINKS.add("/captcha.do");
        RESERVED_LINKS.add("/error.do");
        RESERVED_LINKS.add("/file-access.do");
        RESERVED_LINKS.add("/datastore-file-access.do");
        RESERVED_LINKS.add("/kill-browser.html");
        RESERVED_LINKS.add("/check-login.do");
        RESERVED_LINKS.add("/add-article-comment.do");
        RESERVED_LINKS.add("/add-article-from-symphony-comment.do");
        RESERVED_LINKS.add("/add-page-comment.do");
        
        // XXX: I think we should update to the new Latke request dispatching ASAP

        RESERVED_LINKS.addAll(Arrays.asList(Skips.ADMIN_ACTIONS));
    }

    /**
     * Checks whether the specified article permalink matches the system 
     * generated format pattern ("/articles/yyyy/MM/dd/${articleId}.html").
     * 
     * @param permalink the specified permalink
     * @return {@code true} if matches, returns {@code false} otherwise
     */
    public static boolean matchDefaultArticlePermalinkFormat(
            final String permalink) {
        final Pattern pattern = Pattern.compile(
                "/articles/\\d{4}/\\d{2}/\\d{2}/\\d+\\.html");
        final Matcher matcher = pattern.matcher(permalink);

        return matcher.matches();
    }

    /**
     * Checks whether the specified page permalink matches the system generated 
     * format pattern ("/pages/${pageId}.html").
     * 
     * @param permalink the specified permalink
     * @return {@code true} if matches, returns {@code false} otherwise
     */
    public static boolean matchDefaultPagePermalinkFormat(
            final String permalink) {
        final Pattern pattern = Pattern.compile(
                "/pages/\\d+\\.html");
        final Matcher matcher = pattern.matcher(permalink);

        return matcher.matches();
    }

    /**
     * Checks whether the specified article permalink is invalid on format.
     * 
     * @param permalink the specified article permalink
     * @return {@code true} if invalid, returns {@code false} otherwise
     */
    public boolean invalidArticlePermalinkFormat(final String permalink) {
        if (Strings.isEmptyOrNull(permalink)) {
            return true;
        }

        if (matchDefaultArticlePermalinkFormat(permalink)) {
            return false;
        }

        return invalidUserDefinedPermalinkFormat(permalink);
    }

    /**
     * Checks whether the specified page permalink is invalid on format.
     * 
     * @param permalink the specified page permalink
     * @return {@code true} if invalid, returns {@code false} otherwise
     */
    public boolean invalidPagePermalinkFormat(final String permalink) {
        if (Strings.isEmptyOrNull(permalink)) {
            return true;
        }

        if (matchDefaultPagePermalinkFormat(permalink)) {
            return false;
        }

        return invalidUserDefinedPermalinkFormat(permalink);
    }

    /**
     * Checks whether the specified user-defined permalink is invalid on format.
     * 
     * @param permalink the specified user-defined permalink
     * @return {@code true} if invalid, returns {@code false} otherwise
     */
    private boolean invalidUserDefinedPermalinkFormat(final String permalink) {
        if (Strings.isEmptyOrNull(permalink)) {
            return true;
        }
        
        if (Strings.isNumeric(permalink)) {
            // See issue 120 (http://code.google.com/p/b3log-solo/issues/detail?id=120#c4)
            // for more details
            return true;
        }

        int slashCnt = 0;
        for (int i = 0; i < permalink.length(); i++) {
            if ('/' == permalink.charAt(i)) {
                slashCnt++;
            }

            if (slashCnt > 1) {
                return true;
            }
        }

        // FIXME: URL format check
        
        return false;
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
