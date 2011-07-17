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

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.util.logging.Level;
import org.b3log.latke.action.ActionException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.latke.model.Role;
import org.b3log.latke.model.User;
import org.b3log.solo.action.util.Filler;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.util.Locales;
import org.b3log.solo.model.Preference;
import org.b3log.solo.util.Preferences;
import org.b3log.solo.util.Users;
import org.json.JSONObject;

/**
 * Admin index action. admin-index.ftl.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.7, Jul 10, 2011
 */
public final class AdminIndexAction extends AbstractAdminAction {

    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(AdminIndexAction.class.getName());
    /**
     * Language service.
     */
    private LangPropsService langPropsService = LangPropsService.getInstance();
    /**
     * Filler.
     */
    private Filler filler = Filler.getInstance();
    /**
     * Preference utilities.
     */
    private Preferences preferenceUtils = Preferences.getInstance();
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
        final Map<String, Object> ret = new HashMap<String, Object>();

        try {
            final JSONObject preference = preferenceUtils.getPreference();
            if (null == preference) {
                LOGGER.log(Level.WARNING,
                           "B3log Solo has not been initialized, so redirects to /init.do");
                final RequestDispatcher requestDispatcher =
                        request.getRequestDispatcher("/init.do");
                requestDispatcher.forward(request, response);

                return ret;
            }

            final String localeString = preference.getString(
                    Preference.LOCALE_STRING);
            final Locale locale = new Locale(
                    Locales.getLanguage(localeString),
                    Locales.getCountry(localeString));

            final Map<String, String> langs = langPropsService.getAll(locale);
            ret.putAll(langs);

            final JSONObject currentUser = userUtils.getCurrentUser();
            String userName = null;
            String roleName = null;
            if (null == currentUser) {
                // The administrators may be added via GAE Admin Console Permissions
                final com.google.appengine.api.users.User collaborateAdmin =
                        userService.getCurrentUser();
                userName = collaborateAdmin.getNickname();
                roleName = Role.ADMIN_ROLE;
            } else {
                userName = currentUser.getString(User.USER_NAME);
                roleName = currentUser.getString(User.USER_ROLE);
            }

            ret.put(User.USER_NAME, userName);
            ret.put(User.USER_ROLE, roleName);

            filler.fillBlogHeader(ret, preference);
            filler.fillBlogFooter(ret, preference);

            // Comments the template variable for issue 225 (http://code.google.com/p/b3log-solo/issues/detail?id=225)
            // final boolean hasMultipleUsers = userUtils.hasMultipleUsers();
            // ret.put(Common.ENABLED_MULTIPLE_USER_SUPPORT, hasMultipleUsers);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new ActionException(e);
        }

        return ret;
    }

    @Override
    protected JSONObject doAjaxAction(final JSONObject data,
                                      final HttpServletRequest request,
                                      final HttpServletResponse response)
            throws ActionException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
