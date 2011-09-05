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
package org.b3log.solo.action.impl;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.latke.action.AbstractAction;
import org.b3log.latke.action.ActionException;
import org.b3log.latke.model.Role;
import org.b3log.latke.model.User;
import org.b3log.latke.user.GeneralUser;
import org.b3log.latke.user.UserService;
import org.b3log.latke.user.UserServiceFactory;
import org.b3log.latke.util.Strings;
import org.b3log.solo.model.Common;
import org.b3log.solo.util.Users;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Checks whether if a user logged in.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.2, Aug 9, 2011
 */
public final class CheckLoggedInAction extends AbstractAction {

    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(CheckLoggedInAction.class.getName());
    /**
     * User utilities.
     */
    private Users userUtils = Users.getInstance();
    /**
     * User service.
     */
    private UserService userService = UserServiceFactory.getUserService();

    @Override
    protected Map<?, ?> doFreeMarkerAction(
            final freemarker.template.Template template,
            final HttpServletRequest request,
            final HttpServletResponse response) throws ActionException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected JSONObject doAjaxAction(final JSONObject data,
                                      final HttpServletRequest request,
                                      final HttpServletResponse response)
            throws ActionException {
        final JSONObject currentUser = userUtils.getCurrentUser();
        final JSONObject ret = new JSONObject();

        try {
            ret.put(Common.IS_LOGGED_IN, false);

            if (null == currentUser) {
                if (userService.isUserLoggedIn()
                    && userService.isUserAdmin()) {
                    // Only should happen with the following cases:
                    // 1. Init Solo
                    //    Because of there is no any user in datastore before init Solo
                    //    although the administrator has been logged in for init
                    // 2. The collaborate administrator
                    ret.put(Common.IS_LOGGED_IN, true);
                    ret.put(Common.IS_ADMIN, true);
                    final GeneralUser admin = userService.getCurrentUser();
                    ret.put(User.USER_NAME, admin.getNickname());
                }

                ret.put(Common.LOGIN_URL,
                        userService.createLoginURL(Common.ADMIN_INDEX_URI));

                return ret;
            }

            ret.put(Common.IS_LOGGED_IN, true);
            ret.put(Common.LOGOUT_URL, userService.createLogoutURL("/"));
            ret.put(Common.IS_ADMIN,
                    Role.ADMIN_ROLE.equals(currentUser.getString(User.USER_ROLE)));

            String userName = currentUser.getString(User.USER_NAME);
            if (Strings.isEmptyOrNull(userName)) {
                // The administrators may be added via GAE Admin Console Permissions
                userName = userService.getCurrentUser().getNickname();
                ret.put(Common.IS_ADMIN, true);
            }
            ret.put(User.USER_NAME, userName);

            return ret;
        } catch (final JSONException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);

            return ret;
        }
    }
}
