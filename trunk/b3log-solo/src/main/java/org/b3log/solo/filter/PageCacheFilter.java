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

import java.util.logging.Level;
import java.util.logging.Logger;
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
import static org.b3log.latke.action.AbstractCacheablePageAction.*;

/**
 * Page cache filter.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.3, Sep 6, 2010
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
        final HttpServletRequest httpServletRequest =
                (HttpServletRequest) request;
        final HttpServletResponse httpServletResponse =
                (HttpServletResponse) response;

        httpServletResponse.setHeader("Cache-Control", "no-cache");
        httpServletResponse.setHeader("Expires", "Fri, 25 Aug 1986 00:00:00 GMT");

        final String requestURI = httpServletRequest.getRequestURI();
        if (requestURI.equals("/json-rpc.do")
            || requestURI.equals("/captcha.do")
            || requestURI.equals("/tag-articles-feed.do")
            || requestURI.equals("/blog-articles-feed.do")
            || requestURI.equals("/admin-index.do")) {
            chain.doFilter(request, response);

            return;
        }


        String cachedPageKey = requestURI;
        final String queryString = httpServletRequest.getQueryString();
        if (!Strings.isEmptyOrNull(queryString)) {
            cachedPageKey += "?" + queryString;
        }

        LOGGER.log(Level.FINER, "Request[cachedPageKey={0}]", cachedPageKey);
        LOGGER.log(Level.FINEST, "Page cache[cachedCount={0}, maxCount={1}]",
                   new Object[]{PAGE_CACHE.getCachedCount(),
                                PAGE_CACHE.getMaxCount()});
        final Object cachedPageContentObject = PAGE_CACHE.get(cachedPageKey);
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

    @Override
    public void destroy() {
    }
}
