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
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.latke.service.ServiceException;
import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.HTTPRequestDispatcher;
import org.b3log.latke.util.StaticResources;
import org.b3log.solo.SoloServletListener;
import org.b3log.solo.service.PreferenceQueryService;
import org.json.JSONObject;

/**
 * Checks initialization filter.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.6, Dec 3, 2011
 * @since 0.3.1
 */
public final class InitCheckFilter implements Filter {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(InitCheckFilter.class.getName());
    /**
     * Preference query service.
     */
    private PreferenceQueryService preferenceQueryService = PreferenceQueryService.getInstance();

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
    }

    /**
     * If Solo has not been initialized, so redirects to /init.
     *
     * @param request the specified request
     * @param response the specified response
     * @param chain filter chain
     * @throws IOException io exception
     * @throws ServletException servlet exception
     */
    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
            throws IOException, ServletException {
        final HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        final String requestURI = httpServletRequest.getRequestURI();
        LOGGER.log(Level.FINEST, "Request[URI={0}]", requestURI);

        try {
            if (StaticResources.isStatic(requestURI)) {
                chain.doFilter(request, response);

                return;
            }

            if (SoloServletListener.isInited()) {
                chain.doFilter(request, response);

                return;
            }

            if ("POST".equalsIgnoreCase(httpServletRequest.getMethod())
                && "/init".equals(requestURI)) {
                // Do initailization
                chain.doFilter(request, response);

                return;
            }

            LOGGER.finer("Try to get preference to confirm whether the preference exixts");
            final JSONObject preference = preferenceQueryService.getPreference();
            if (null == preference) {
                LOGGER.log(Level.WARNING, "B3log Solo has not been initialized, so redirects to /init");

                final HTTPRequestContext context = new HTTPRequestContext();
                context.setRequest((HttpServletRequest) request);
                context.setResponse((HttpServletResponse) response);

                request.setAttribute("requestURI", "/init");
                request.setAttribute("method", "GET");

                HTTPRequestDispatcher.dispatch(context);

                return;
            } else {
                // XXX: Wrong state of SoloServletListener.isInited()
                chain.doFilter(request, response);
                return;
            }
        } catch (final ServiceException e) {
            ((HttpServletResponse) response).sendError(
                    HttpServletResponse.SC_NOT_FOUND);

            return;
        }
    }

    @Override
    public void destroy() {
    }
}
