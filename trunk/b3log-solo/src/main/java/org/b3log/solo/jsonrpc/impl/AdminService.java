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
package org.b3log.solo.jsonrpc.impl;

import com.google.appengine.api.users.UserServiceFactory;
import org.apache.log4j.Logger;
import org.b3log.latke.client.action.AbstractCacheablePageAction;
import org.b3log.solo.jsonrpc.AbstractJSONRpcService;

/**
 * Administrator service for JavaScript client.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Aug 30, 2010
 */
public final class AdminService extends AbstractJSONRpcService {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(AdminService.class);
    /**
     * User service.
     */
    private com.google.appengine.api.users.UserService userService =
            UserServiceFactory.getUserService();

    /**
     * Determines whether the administrator is logged in.
     *
     * @return {@code true} if logged in, returns {@code false} otherwise
     */
    public boolean isAdminLoggedIn() {
        return userService.isUserLoggedIn() && userService.isUserAdmin();
    }

    /**
     * Clear all page cache.
     */
    public void clearPageCache() {
        AbstractCacheablePageAction.PAGE_CACHE.removeAll();
    }
}
