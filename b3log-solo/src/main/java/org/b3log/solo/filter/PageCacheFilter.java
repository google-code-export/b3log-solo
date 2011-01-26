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

import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.latke.cache.Cache;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.time.DateFormatUtils;
import org.b3log.latke.Keys;
import org.b3log.latke.action.AbstractCacheablePageAction;
import org.b3log.latke.action.util.PageCaches;
import org.b3log.latke.util.Strings;
import org.b3log.solo.model.Article;
import org.b3log.solo.repository.ArticleRepository;
import org.b3log.solo.repository.StatisticRepository;
import org.b3log.solo.repository.impl.ArticleGAERepository;
import org.b3log.solo.repository.impl.StatisticGAERepository;
import org.b3log.solo.util.Articles;
import org.b3log.solo.util.PageCacheKeys;
import org.b3log.solo.util.Statistics;
import org.json.JSONObject;

/**
 * Page cache filter.
 *
 * <p>
 * All request URI ends with ".ftl" will be redirected to "/".
 * </p>
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.2.9, Jan 26, 2011
 * @see org.b3log.latke.action.AbstractCacheablePageAction#afterDoFreeMarkerTemplateAction(
 * javax.servlet.http.HttpServletRequest,
 * javax.servlet.http.HttpServletResponse,
 * java.util.Map, freemarker.template.Template)
 * @see #shouldSkip(java.lang.String) 
 */
public final class PageCacheFilter implements Filter {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(PageCacheFilter.class.getName());
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
        "/admin-page.do",
        "/admin-others.do",
        "/admin-draft-list.do",
        "/admin-user-list.do",
        "/rm-all-data.do",
        "/init.do",
        "/clear-cache.do",
        "/tencent-microblog-oauth-authorize-token.do",
        "/tencent-microblog-oauth-callback.do"
    };
    /**
     * Page cache utilities.
     */
    private PageCacheKeys pageCacheKeys = PageCacheKeys.getInstance();
    /**
     * Statistic utilities.
     */
    private Statistics statistics = Statistics.getInstance();
    /**
     * Statistic repository.
     */
    private StatisticRepository statisticRepository =
            StatisticGAERepository.getInstance();
    /**
     * Article repository.
     */
    private ArticleRepository articleRepository =
            ArticleGAERepository.getInstance();
    /**
     * Article utilities.
     */
    private Articles articleUtils = Articles.getInstance();

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
    }

    /**
     * Writes response page content(cached/generated).
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
        final HttpServletRequest httpServletRequest =
                (HttpServletRequest) request;
        httpServletRequest.getContentType();
        final String contentType = httpServletRequest.getContentType();
        if (null != contentType
            && contentType.toLowerCase().contains("multipart/form-data")) {
            final RequestDispatcher requestDispatcher =
                    httpServletRequest.getRequestDispatcher(
                    "/datastore-file-access.do");
            requestDispatcher.forward(request, response);

            return;
        }

        final HttpServletResponse httpServletResponse =
                (HttpServletResponse) response;

        httpServletResponse.setHeader("Cache-Control", "no-cache");
        httpServletResponse.setHeader("Expires", "Mon, 25 Aug 1986 00:00:00 GMT");

        final String requestURI = httpServletRequest.getRequestURI();
        if (requestURI.endsWith(".ftl")) {
            httpServletResponse.sendRedirect("/");

            return;
        }

        if (shouldSkip(requestURI)) {
            LOGGER.log(Level.FINEST, "Skip filter request[URI={0}]", requestURI);
            chain.doFilter(request, response);

            return;
        }

        final String queryString = httpServletRequest.getQueryString();
        final String pageCacheKey =
                pageCacheKeys.getPageCacheKey(requestURI, queryString);
        final Cache<String, Object> cache = PageCaches.getCache();
        LOGGER.log(Level.FINER, "Request[pageCacheKey={0}]", pageCacheKey);
        LOGGER.log(Level.FINEST, "Page cache[cachedCount={0}, maxCount={1}]",
                   new Object[]{cache.getCachedCount(), cache.getMaxCount()});
        final JSONObject cachedPageContentObject =
                (JSONObject) cache.get(pageCacheKey);
        if (null == cachedPageContentObject) {
            httpServletRequest.setAttribute(Keys.PAGE_CACHE_KEY,
                                            pageCacheKey);
            chain.doFilter(request, response);

            final long endimeMillis = System.currentTimeMillis();
            final String dateString = DateFormatUtils.format(
                    endimeMillis, "yyyy/MM/dd HH:mm:ss");
            final PrintWriter writer = getResponseWriter(httpServletResponse);
            final String msg = String.format(
                    "<!-- Generated by B3log Solo(%1$d ms), %2$s -->",
                    endimeMillis - startTimeMillis, dateString);
            LOGGER.finer(msg);
            writer.write(msg);
            writer.flush();
            writer.close();
        } else {
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
                final String cachedType = cachedPageContentObject.optString(
                        AbstractCacheablePageAction.CACHED_TYPE);
                if (!Strings.isEmptyOrNull(cachedType)) {
                    final String oId = cachedPageContentObject.getString(
                            AbstractCacheablePageAction.CACHED_OID);
                    LOGGER.log(Level.FINEST,
                               "Cached value[key={0}, oId={1}, type={2}]",
                               new Object[]{pageCacheKey, oId, cachedType});
                    if (Strings.isEmptyOrNull(oId)) {
                        httpServletResponse.sendError(
                                HttpServletResponse.SC_NOT_FOUND);

                        return;
                    }

                    if (Article.ARTICLE.equals(cachedType)) {
                        statistics.incArticleViewCount(oId);
                    }
                }

                final long endimeMillis = System.currentTimeMillis();
                final String dateString = DateFormatUtils.format(
                        endimeMillis, "yyyy/MM/dd HH:mm:ss");
                final String msg = String.format(
                        "<!-- Cached by B3log Solo(%1$d ms), %2$s -->",
                        endimeMillis - startTimeMillis, dateString);
                LOGGER.finer(msg);
                cachedPageContent += msg;
                writer.write(cachedPageContent);
                writer.flush();
                writer.close();
            } catch (final Exception e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
        }

        statistics.incBlogViewCount();
    }

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
    // XXX: performance issue
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
               || equalAdminActions(requestURI)
               || requestURI.contains("/_ah/") // For local dev server
               || requestURI.contains("/datastore-file-access.do")
               || requestURI.contains("/skins")
               || requestURI.contains("/images")
               || requestURI.contains("/styles");
    }

    @Override
    public void destroy() {
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
     * Gets the writer of the specified http servlet response.
     * 
     * @param httpServletResponse the specified http servlet response
     * @return writer
     * @throws IOException io exception
     */
    private PrintWriter getResponseWriter(
            final HttpServletResponse httpServletResponse) throws IOException {
        PrintWriter ret = null;

        try {
            ret = httpServletResponse.getWriter();
        } catch (final IllegalStateException e) {
            ret = new PrintWriter(httpServletResponse.getOutputStream());
        }

        return ret;
    }
}
