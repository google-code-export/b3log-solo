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
package org.b3log.solo.filter;

import org.apache.commons.lang.StringUtils;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.latke.cache.Cache;
import org.b3log.latke.util.Strings;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.latke.action.util.PageCaches;

/**
 * Page cache filter.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.6, Oct 27, 2010
 */
public final class PageCacheFilter implements Filter {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(PageCacheFilter.class.getName());

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
    }

    /**
     * Writes response cached page content if found from cache.
     *
     * @param request the specified request
     * @param response the specified response
     * @param chain filter chain
     * @throws IOException io exception
     * @throws ServletException servlet exception
     */
    @Override
    public void doFilter(final ServletRequest request,
                         final ServletResponse response,
                         final FilterChain chain) throws IOException,
                                                         ServletException {
        LOGGER.finer("Doing page cache filter....");
        final HttpServletRequest httpServletRequest =
                (HttpServletRequest) request;
        final HttpServletResponse httpServletResponse =
                (HttpServletResponse) response;

        httpServletResponse.setHeader("Cache-Control", "no-cache");
        httpServletResponse.setHeader("Expires", "Mon, 25 Aug 1986 00:00:00 GMT");

        final String requestURI = httpServletRequest.getRequestURI();
        if (requestURI.equals("/json-rpc.do")
            || requestURI.equals("/live.do")
            || requestURI.equals("/captcha.do")
            || requestURI.equals("/tag-articles-feed.do")
            || requestURI.equals("/blog-articles-feed.do")
            || requestURI.equals("/file-access.do")
            || requestURI.contains("/admin-")
            || requestURI.contains("/_ah/upload/")) {
            LOGGER.log(Level.FINER, "Skip filter request[URI={0}]", requestURI);
            chain.doFilter(request, response);

            return;
        }

        final String queryString = httpServletRequest.getQueryString();
        final String cachedPageKey = getPageCacheKey(requestURI, queryString);
        final Cache<String, Object> cache = PageCaches.getCache();
        LOGGER.log(Level.FINER, "Request[cachedPageKey={0}]", cachedPageKey);
        LOGGER.log(Level.FINEST, "Page cache[cachedCount={0}, maxCount={1}]",
                   new Object[]{cache.getCachedCount(),
                                cache.getMaxCount()});
        final Object cachedPageContentObject = cache.get(cachedPageKey);
        if (null == cachedPageContentObject) {
            chain.doFilter(request, response);

            return;
        } else {
            LOGGER.log(Level.FINEST,
                       "Writes resposne for page[cachedPageKey={0}] from cache",
                       cachedPageKey);
            response.setContentType("text/html");
            response.setCharacterEncoding("UTF-8");
            final PrintWriter writer = response.getWriter();
            final String cachedPageContent = (String) cachedPageContentObject;
            writer.write(cachedPageContent);
            writer.close();
        }
    }

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
                    uri,
                    uri.lastIndexOf("/") + 1, uri.lastIndexOf("."));
            ret = "/article-detail.do?oId=" + articleId;
        } else {
            ret = uri;
            if (!Strings.isEmptyOrNull(queryString)) {
                ret += "?" + queryString;
            }
        }

        return ret;
    }

    @Override
    public void destroy() {
    }
}
