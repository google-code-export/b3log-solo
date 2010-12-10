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
import com.google.inject.Inject;
import org.b3log.latke.jsonrpc.AbstractJSONRpcService;
import org.b3log.solo.util.Users;

/**
 * Abstract json RPC service on GAE.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.3, Dec 10, 2010
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
}
