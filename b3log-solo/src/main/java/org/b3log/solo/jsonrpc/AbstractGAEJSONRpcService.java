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

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.inject.Inject;
import org.b3log.latke.jsonrpc.AbstractJSONRpcService;
import org.b3log.solo.util.Users;

/**
 * Abstract json RPC service on GAE.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.2, Dec 7, 2010
 */
public abstract class AbstractGAEJSONRpcService extends AbstractJSONRpcService {

    /**
     * User service.
     */
    private UserService userService = UserServiceFactory.getUserService();
   /**
     * User utilities.
     */
    @Inject
    private Users users;

    /**
     * Checks whether the current request is made by logged in user(including
     * default user and administrator lists in <i>users</i>).
     *
     * @return {@code true} if the current request is made by logged in user,
     * returns {@code false} otherwise
     */
    public boolean isLoggedIn() {
        final User currentUser = userService.getCurrentUser();
        if (null == currentUser) {
            return false;
        }

        return users.isSoloUser(currentUser.getEmail());
    }

    /**
     * Checks whether the current request is made by logged in administrator.
     *
     * @return {@code true} if the current request is made by logged in
     * administrator, returns {@code false} otherwise
     */
    public boolean isAdminLoggedIn() {
        return userService.isUserLoggedIn() && userService.isUserAdmin();
    }
}
