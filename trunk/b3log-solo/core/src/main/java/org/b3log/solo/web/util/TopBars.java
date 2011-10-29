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
package org.b3log.solo.web.util;

import freemarker.template.Template;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.latke.model.Role;
import org.b3log.latke.model.User;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.user.UserService;
import org.b3log.latke.user.UserServiceFactory;
import org.b3log.solo.model.Common;
import org.b3log.solo.util.Users;
import org.b3log.solo.web.processor.InitProcessor;
import org.b3log.solo.web.processor.LoginProcessor;
import org.json.JSONObject;

/**
 * Top bar utilities.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Oct 2, 2011
 * @since 0.3.5
 */
public final class TopBars {

    /**
     * User utilities.
     */
    private static Users userUtils = Users.getInstance();
    /**
     * User service.
     */
    private static UserService userService = UserServiceFactory.getUserService();
    /**
     * Language service.
     */
    private static LangPropsService langPropsService = LangPropsService.
            getInstance();

    /**
     * Generates top bar HTML.
     * 
     * @param request the specified request
     * @param response the specified response
     * @return top bar HTML
     * @throws Exception exception
     */
    public static String getTopBarHTML(final HttpServletRequest request,
                                       final HttpServletResponse response)
            throws Exception {
        final Template topBarTemplate =
                InitProcessor.TEMPLATE_CFG.getTemplate("top-bar.ftl");
        final StringWriter stringWriter = new StringWriter();


        final Map<String, Object> topBarModel =
                new HashMap<String, Object>();

        LoginProcessor.tryLogInWithCookie(request, response);
        final JSONObject currentUser = userUtils.getCurrentUser(request);

        topBarModel.put(Common.IS_LOGGED_IN, false);

        if (null == currentUser) {
            topBarModel.put(Common.LOGIN_URL,
                            userService.createLoginURL(Common.ADMIN_INDEX_URI));
            topBarModel.put("loginLabel", langPropsService.get("loginLabel"));
            
            topBarTemplate.process(topBarModel, stringWriter);

            return stringWriter.toString();
        }

        topBarModel.put(Common.IS_LOGGED_IN, true);
        topBarModel.put(Common.LOGOUT_URL,
                        userService.createLogoutURL("/"));
        topBarModel.put(Common.IS_ADMIN,
                        Role.ADMIN_ROLE.equals(currentUser.getString(
                User.USER_ROLE)));

        topBarModel.put("clearAllCacheLabel",
                        langPropsService.get("clearAllCacheLabel"));
        topBarModel.put("clearCacheLabel",
                        langPropsService.get("clearCacheLabel"));
        topBarModel.put("adminLabel", langPropsService.get("adminLabel"));
        topBarModel.put("logoutLabel", langPropsService.get("logoutLabel"));

        final String userName = currentUser.getString(User.USER_NAME);
        topBarModel.put(User.USER_NAME, userName);

        topBarTemplate.process(topBarModel, stringWriter);

        return stringWriter.toString();
    }

    /**
     * Private default constructor.
     */
    private TopBars() {
    }
}
