/*
 * Copyright (c) 2009, 2010, B3log Team
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

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.inject.Inject;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import org.b3log.solo.util.Users;

/**
 * Authentication filter.
 *
 * <p>
 * User not logged in was filtered by GAE, see the section &lt;security-constraint&gt;
 * in web.xml and GAE official document <a href="http://code.google.com/appengine/docs/java/config/
 * webxml.html#Security_and_Authentication">Security and Authentication</a>.
 * </p>
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Dec 7, 2010
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
    private UserService userService = UserServiceFactory.getUserService();
    /**
     * User utilities.
     */
    @Inject
    private Users users;

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
    }

    /**
     * If the specified request is NOT made by an authenticated user, sends error
     * {@value HttpServletResponse#SC_FORBIDDEN}.
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
        LOGGER.finer("Doing auth filter....");
        final HttpServletResponse httpServletResponse =
                (HttpServletResponse) response;

        try {
            final User currentUser = userService.getCurrentUser();
            if (null == currentUser) {
                LOGGER.warning("The request has been forbidden");
                httpServletResponse.sendError(HttpServletResponse.SC_FORBIDDEN);

                return;
            }

            final String currentUserEmail = currentUser.getEmail();
            LOGGER.log(Level.FINER, "Current user email[{0}]", currentUserEmail);
            if (users.isSoloUser(currentUserEmail)
                || users.isCollaborateAdmin()) {
                chain.doFilter(request, response);

                return;
            }

            LOGGER.warning("The request has been forbidden");
            httpServletResponse.sendError(HttpServletResponse.SC_FORBIDDEN);
        } catch (final Exception e) {
            httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    public void destroy() {
    }
}
