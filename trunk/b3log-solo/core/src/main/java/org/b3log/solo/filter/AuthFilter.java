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
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.latke.model.User;
import org.b3log.latke.user.GeneralUser;
import org.b3log.latke.user.UserService;
import org.b3log.latke.user.UserServiceFactory;
import org.b3log.latke.util.MD5;
import org.b3log.latke.util.Sessions;
import org.b3log.latke.util.Strings;
import org.b3log.solo.repository.UserRepository;
import org.b3log.solo.repository.impl.UserRepositoryImpl;
import org.b3log.solo.util.Users;
import org.json.JSONObject;

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
 * @version 1.0.0.2, Sep 30, 2011
 * @since 0.3.1
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
    private Users users = Users.getInstance();
    /**
     * User repository.
     */
    private UserRepository userRepository = UserRepositoryImpl.getInstance();

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
        final HttpServletResponse httpServletResponse =
                (HttpServletResponse) response;
        final HttpServletRequest httpServletRequest =
                (HttpServletRequest) request;

        try {
            final Cookie[] cookies = httpServletRequest.getCookies();
            for (int i = 0; i < cookies.length; i++) {
                final Cookie cookie = cookies[i];
                if ("b3log-solo".equals(cookie.getName())) {
                    final JSONObject cookieJSONObject =
                            new JSONObject(cookie.getValue());

                    final String userEmail =
                            cookieJSONObject.getString(User.USER_EMAIL);
                    if (Strings.isEmptyOrNull(userEmail)) {
                        break;
                    }

                    final JSONObject user =
                            userRepository.getByEmail(
                            userEmail.toLowerCase().trim());
                    if (null == user) {
                        break;
                    }

                    final String userPassword =
                            user.getString(User.USER_PASSWORD);
                    final String hashPassword =
                            cookieJSONObject.getString(User.USER_PASSWORD);
                    if (MD5.hash(userPassword).equals(hashPassword)) {
                        Sessions.login(httpServletRequest, httpServletResponse,
                                       user);
                    }
                }
            }

            final GeneralUser currentUser =
                    userService.getCurrentUser(httpServletRequest);
            if (null == currentUser) {
                LOGGER.warning("The request has been forbidden");
                httpServletResponse.sendError(HttpServletResponse.SC_FORBIDDEN);

                return;
            }

            final String currentUserEmail = currentUser.getEmail();
            LOGGER.log(Level.FINER, "Current user email[{0}]", currentUserEmail);
            if (users.isSoloUser(currentUserEmail)
                || users.isCollaborateAdmin((HttpServletRequest) request)) {
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
