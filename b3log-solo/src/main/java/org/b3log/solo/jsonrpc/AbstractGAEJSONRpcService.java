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

package org.b3log.solo.jsonrpc;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.latke.jsonrpc.AbstractJSONRpcService;

/**
 * Abstract json RPC service on GAE.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Sep 2, 2010
 */
public abstract class AbstractGAEJSONRpcService extends AbstractJSONRpcService {

    /**
     * User service.
     */
    private UserService userService = UserServiceFactory.getUserService();

    /**
     * Checks the specified request authorized or not(Http Status Code:
     * Forbidden 403).
     * <p>
     * If the specified request is not send from the logged in administrator,
     * sends an error with status code 403.
     * </p>
     *
     * @param request the specified http servlet request
     * @param response the specified http servlet response
     * @throws IOException io exception
     */
    @Override
    public final void checkAuthorized(final HttpServletRequest request,
                                      final HttpServletResponse response)
            throws IOException {
        if (!userService.isUserLoggedIn() || !userService.isUserAdmin()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
        }
    }
}
