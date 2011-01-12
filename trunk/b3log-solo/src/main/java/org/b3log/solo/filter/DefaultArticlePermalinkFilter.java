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

import org.apache.commons.lang.StringUtils;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.b3log.latke.Keys;

/**
 * Default article permalink filter.
 *
 * <p>
 *   Filters URL pattern {@literal /articles/\\d{4}/\\d{2}/\\d{2}/\\d+.html}.
 * </p>
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.6, Jan 12, 2011
 */
public final class DefaultArticlePermalinkFilter implements Filter {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(DefaultArticlePermalinkFilter.class.getName());

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
    }

    /**
     * Redirects request URI {@code /articles/yyyy/MM/dd/articleId.html} to
     * {@link org.b3log.solo.action.impl.ArticleAction}.
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
        final String requestURI = httpServletRequest.getRequestURI();

        if (!requestURI.matches("/articles/\\d{4}/\\d{2}/\\d{2}/\\d+.html")) {
            chain.doFilter(request, response);
            
            return;
        }

        final String articleId = StringUtils.substring(
                requestURI,
                requestURI.lastIndexOf("/") + 1, requestURI.lastIndexOf("."));
        LOGGER.log(Level.FINER,
                   "Article permalink request[URI={0}, oId={1}]",
                   new String[]{requestURI, articleId});

        final RequestDispatcher requestDispatcher =
                httpServletRequest.getRequestDispatcher("/article-detail.do");
        request.setAttribute(Keys.OBJECT_ID, articleId);
        requestDispatcher.forward(request, response);
    }

    @Override
    public void destroy() {
    }
}
