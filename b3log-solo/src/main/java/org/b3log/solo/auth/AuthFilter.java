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

package org.b3log.solo.auth;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import java.util.logging.Logger;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Authentication filter.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Aug 21, 2010
 */
public final class AuthFilter implements Filter {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(AuthFilter.class.getName());
    /**
     * User service.
     */
    private static final UserService USER_SERVICE =
            UserServiceFactory.getUserService();

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
    }

    /**
     * Filters the has not logged in user for the specified request. Send error
     * {@linkplain HttpServletResponse#SC_FORBIDDEN}.
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


        if (!USER_SERVICE.isUserLoggedIn() || !USER_SERVICE.isUserAdmin()) {
            LOGGER.warning("Authenticate fail");

            httpServletResponse.sendError(HttpServletResponse.SC_FORBIDDEN);
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
