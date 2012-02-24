/*
 * Copyright (c) 2009, 2010, 2011, 2012, B3log Team
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
import org.apache.commons.lang.time.DateFormatUtils;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.action.AbstractCacheablePageAction;
import org.b3log.latke.cache.PageCaches;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.service.ServiceException;
import org.b3log.latke.util.Strings;
import org.b3log.solo.model.Common;
import org.b3log.solo.processor.util.TopBars;
import org.b3log.solo.util.Statistics;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Page cache filter.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.8, Dec 12, 2011
 * @since 0.3.1
 */
public final class PageCacheFilter implements Filter {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(PageCacheFilter.class.getName());
    /**
     * Statistic utilities.
     */
    private Statistics statistics = Statistics.getInstance();

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
    }

    /**
     * Try to write response from cache.
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
        final long startTimeMillis = System.currentTimeMillis();
        request.setAttribute(AbstractCacheablePageAction.START_TIME_MILLIS,
                             startTimeMillis);

        final HttpServletRequest httpServletRequest =
                (HttpServletRequest) request;
        final String requestURI = httpServletRequest.getRequestURI();
        LOGGER.log(Level.FINER, "Request URI[{0}]", requestURI);

        if (!Latkes.isPageCacheEnabled()) {
            LOGGER.log(Level.FINEST, "Page cache is disabled");
            chain.doFilter(request, response);

            return;
        }

        final String skinDirName =
                (String) httpServletRequest.getAttribute(Keys.TEMAPLTE_DIR_NAME);
        if ("mobile".equals(skinDirName)) {
            // Mobile request, bypasses page caching
            chain.doFilter(request, response);

            return;
        }

        String pageCacheKey = null;
        final String queryString = httpServletRequest.getQueryString();
        pageCacheKey =
                (String) request.getAttribute(Keys.PAGE_CACHE_KEY);
        if (Strings.isEmptyOrNull(pageCacheKey)) {
            pageCacheKey = PageCaches.getPageCacheKey(requestURI,
                                                      queryString);
            request.setAttribute(Keys.PAGE_CACHE_KEY, pageCacheKey);
        }

        final JSONObject cachedPageContentObject =
                PageCaches.get(pageCacheKey, httpServletRequest);

        if (null == cachedPageContentObject) {
            LOGGER.log(Level.FINER, "Page cache miss for request URI[{0}]",
                       requestURI);
            chain.doFilter(request, response);

            return;
        }

        try {
            LOGGER.log(Level.FINEST,
                       "Writes resposne for page[pageCacheKey={0}] from cache",
                       pageCacheKey);
            response.setContentType("text/html");
            response.setCharacterEncoding("UTF-8");
            final PrintWriter writer = response.getWriter();
            String cachedPageContent =
                    cachedPageContentObject.getString(
                    AbstractCacheablePageAction.CACHED_CONTENT);
            final String topBarHTML =
                    TopBars.getTopBarHTML((HttpServletRequest) request,
                                          (HttpServletResponse) response);
            cachedPageContent = cachedPageContent.replace(
                    Common.TOP_BAR_REPLACEMENT_FLAG, topBarHTML);

            final String cachedType = cachedPageContentObject.optString(
                    AbstractCacheablePageAction.CACHED_TYPE);
            final String cachedTitle = cachedPageContentObject.getString(
                    AbstractCacheablePageAction.CACHED_TITLE);
            LOGGER.log(Level.FINEST,
                       "Cached value[key={0}, type={1}, title={2}]",
                       new Object[]{pageCacheKey, cachedType, cachedTitle});

            statistics.incBlogViewCount((HttpServletRequest) request);

            final long endimeMillis = System.currentTimeMillis();
            final String dateString = DateFormatUtils.format(
                    endimeMillis, "yyyy/MM/dd HH:mm:ss");
            final String msg = String.format(
                    "<!-- Cached by B3log Solo(%1$d ms), %2$s -->",
                    endimeMillis - startTimeMillis, dateString);
            LOGGER.finer(msg);
            cachedPageContent += Strings.LINE_SEPARATOR + msg;
            writer.write(cachedPageContent);
            writer.flush();
            writer.close();
        } catch (final JSONException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            chain.doFilter(request, response);

            return;
        } catch (final RepositoryException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            chain.doFilter(request, response);

            return;
        } catch (final ServiceException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            chain.doFilter(request, response);

            return;
        }
    }

    @Override
    public void destroy() {
    }
}
