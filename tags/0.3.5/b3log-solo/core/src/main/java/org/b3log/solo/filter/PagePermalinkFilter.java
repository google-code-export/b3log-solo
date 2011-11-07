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
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.HTTPRequestDispatcher;
import org.b3log.solo.model.Page;
import org.b3log.solo.repository.PageRepository;
import org.b3log.solo.repository.impl.PageRepositoryImpl;
import org.json.JSONObject;

/**
 * Page permalink filter.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.6, Jul 11, 2011
 */
public final class PagePermalinkFilter implements Filter {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(PagePermalinkFilter.class.getName());
    /**
     * Page repository.
     */
    private PageRepository pageRepository = PageRepositoryImpl.getInstance();

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
    }

    /**
     * Tries to dispatch request to page processor.
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
        LOGGER.log(Level.FINER, "Request URI[{0}]", requestURI);
        if (Skips.shouldSkip(requestURI)) {
            LOGGER.log(Level.FINER, "Skip filter request[URI={0}]", requestURI);
            chain.doFilter(request, response);

            return;
        }

        final JSONObject page = pageRepository.getByPermalink(requestURI);
        if (null == page) {
            chain.doFilter(request, response);

            return;
        }

        try {
            final HTTPRequestContext context = new HTTPRequestContext();
            context.setRequest(httpServletRequest);
            context.setResponse((HttpServletResponse) response);

            httpServletRequest.setAttribute(Page.PAGE, page);

            httpServletRequest.setAttribute("requestURI", "/page");
            httpServletRequest.setAttribute("method", "GET");

            HTTPRequestDispatcher.dispatch(context);
        } catch (final Exception e) {
            ((HttpServletResponse) response).sendError(
                    HttpServletResponse.SC_NOT_FOUND);

            return;
        }
    }

    @Override
    public void destroy() {
    }
}