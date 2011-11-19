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
package org.b3log.solo.web.processor;

import freemarker.template.Template;
import java.io.IOException;
import java.util.Calendar;
import java.util.Map;
import java.util.logging.Level;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.solo.util.Users;
import java.util.logging.Logger;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.action.AbstractAction;
import org.b3log.latke.annotation.RequestProcessing;
import org.b3log.latke.annotation.RequestProcessor;
import org.b3log.latke.model.Role;
import org.b3log.latke.model.User;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.servlet.renderer.freemarker.AbstractFreeMarkerRenderer;
import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.HTTPRequestMethod;
import org.b3log.latke.servlet.renderer.JSONRenderer;
import org.b3log.latke.user.UserService;
import org.b3log.latke.user.UserServiceFactory;
import org.b3log.latke.util.MD5;
import org.b3log.latke.util.Sessions;
import org.b3log.latke.util.Strings;
import org.b3log.solo.SoloServletListener;
import org.b3log.solo.model.Common;
import org.b3log.solo.repository.UserRepository;
import org.b3log.solo.repository.impl.UserRepositoryImpl;
import org.b3log.solo.web.util.Filler;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Login/logout processor.
 * 
 * <p>Initializes administrator</p>.
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.1.0.4, Nov 19, 2011
 * @since 0.3.1
 */
@RequestProcessor
public final class LoginProcessor {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(LoginProcessor.class.getName());
    /**
     * User utilities.
     */
    private Users userUtils = Users.getInstance();
    /**
     * User service.
     */
    private UserService userService = UserServiceFactory.getUserService();
    /**
     * User repository.
     */
    private static UserRepository userRepository =
            UserRepositoryImpl.getInstance();
    /**
     * Language service.
     */
    private LangPropsService langPropsService = LangPropsService.getInstance();
    /**
     * Filler.
     */
    private Filler filler = Filler.getInstance();

    /**
     * Shows login page.
     * 
     * @param context the specified context
     */
    @RequestProcessing(value = {"/login"}, method = HTTPRequestMethod.GET)
    public void showLogin(final HTTPRequestContext context) {
        final HttpServletRequest request = context.getRequest();

        String destinationURL = request.getParameter("goto");
        if (Strings.isEmptyOrNull(destinationURL)) {
            destinationURL = "/";
        }

        final AbstractFreeMarkerRenderer renderer =
                new AbstractFreeMarkerRenderer() {

                    @Override
                    protected Template getTemplate(final String templateName)
                            throws IOException {
                        return InitProcessor.TEMPLATE_CFG.getTemplate(
                                templateName);
                    }

                    @Override
                    protected void afterRender(final HTTPRequestContext context)
                            throws Exception {
                    }

                    @Override
                    protected void beforeRender(final HTTPRequestContext context)
                            throws Exception {
                    }
                };

        renderer.setTemplateName("login.ftl");
        context.setRenderer(renderer);

        final Map<String, Object> dataModel = renderer.getDataModel();
        final Map<String, String> langs =
                langPropsService.getAll(Latkes.getLocale());
        dataModel.putAll(langs);
        dataModel.put("goto", destinationURL);
        dataModel.put(Common.YEAR,
                      String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
        dataModel.put(Common.VERSION, SoloServletListener.VERSION);
        dataModel.put(Common.STATIC_RESOURCE_VERSION, 
                      Latkes.getStaticResourceVersion());

        filler.fillMinified(dataModel);
    }

    /**
     * Logins.
     * 
     * <p>
     * Renders the response with a json object, for example,
     * <pre>
     * {
     *     "isLoggedIn": boolean,
     *     "msg": "" // optional, exists if isLoggedIn equals to false
     * }
     * </pre>
     * </p>
     * 
     * @param context the specified context
     */
    @RequestProcessing(value = {"/login"}, method = HTTPRequestMethod.POST)
    public void login(final HTTPRequestContext context) {
        final HttpServletRequest request = context.getRequest();

        final JSONRenderer renderer = new JSONRenderer();
        context.setRenderer(renderer);
        final JSONObject jsonObject = new JSONObject();
        renderer.setJSONObject(jsonObject);


        try {
            jsonObject.put(Common.IS_LOGGED_IN, false);
            final String loginFailLabel = langPropsService.get("loginFailLabel");
            jsonObject.put(Keys.MSG, loginFailLabel);

            final JSONObject requestJSONObject =
                    AbstractAction.parseRequestJSONObject(request,
                                                          context.getResponse());
            final String userEmail =
                    requestJSONObject.getString(User.USER_EMAIL);
            final String userPwd =
                    requestJSONObject.getString(User.USER_PASSWORD);

            if (Strings.isEmptyOrNull(userEmail)
                || Strings.isEmptyOrNull(userPwd)) {
                return;
            }

            LOGGER.log(Level.INFO, "Login[email={0}]", userEmail);

            final JSONObject user = userRepository.getByEmail(userEmail);
            if (null == user) {
                LOGGER.log(Level.WARNING, "Not found user[email={0}", userEmail);
                return;
            }

            if (userPwd.equals(user.getString(User.USER_PASSWORD))) {
                Sessions.login(request, context.getResponse(), user);

                LOGGER.log(Level.INFO, "Logged in[email={0}]", userEmail);

                jsonObject.put(Common.IS_LOGGED_IN, true);
                jsonObject.remove(Keys.MSG);

                return;
            }

            LOGGER.log(Level.WARNING, "Wrong password[{0}]", userPwd);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Logout.
     * 
     * @param context the specified context
     * @throws IOException io exception 
     */
    @RequestProcessing(value = {"/logout"}, method = HTTPRequestMethod.GET)
    public void logout(final HTTPRequestContext context) throws IOException {
        final HttpServletRequest httpServletRequest = context.getRequest();

        Sessions.logout(httpServletRequest, context.getResponse());

        String destinationURL = httpServletRequest.getParameter("goto");
        if (Strings.isEmptyOrNull(destinationURL)) {
            destinationURL = "/";
        }

        context.getResponse().sendRedirect(destinationURL);
    }

    /**
     * Checks logged in with the specified context.
     * 
     * @param context the specified context
     * @throws Exception exception 
     */
    @RequestProcessing(value = {"/check-login.do"},
                       method = HTTPRequestMethod.POST)
    public void checkLoggedIn(final HTTPRequestContext context)
            throws Exception {

        final JSONRenderer renderer = new JSONRenderer();
        context.setRenderer(renderer);

        final HttpServletRequest request = context.getRequest();
        tryLogInWithCookie(request, context.getResponse());

        final JSONObject currentUser = userUtils.getCurrentUser(request);
        final JSONObject jsonObjectToRender = new JSONObject();
        renderer.setJSONObject(jsonObjectToRender);

        try {
            jsonObjectToRender.put(Common.IS_LOGGED_IN, false);

            if (null == currentUser) {
                jsonObjectToRender.put(Common.LOGIN_URL,
                                       userService.createLoginURL(
                        Common.ADMIN_INDEX_URI));
                return;
            }

            jsonObjectToRender.put(Common.IS_LOGGED_IN, true);
            jsonObjectToRender.put(Common.LOGOUT_URL, userService.
                    createLogoutURL("/"));
            jsonObjectToRender.put(Common.IS_ADMIN,
                                   Role.ADMIN_ROLE.equals(currentUser.getString(
                    User.USER_ROLE)));

            final String userName = currentUser.getString(User.USER_NAME);
            jsonObjectToRender.put(User.USER_NAME, userName);
        } catch (final JSONException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Tries to login with cookie.
     * 
     * @param request the specified request
     * @param response the specified response
     * @throws JSONException json exception
     * @throws RepositoryException repository exception 
     */
    public static void tryLogInWithCookie(final HttpServletRequest request,
                                          final HttpServletResponse response)
            throws JSONException, RepositoryException {
        final Cookie[] cookies = request.getCookies();
        if (null == cookies) {
            return;
        }

        for (int i = 0; i < cookies.length; i++) {
            final Cookie cookie = cookies[i];
            if ("b3log-latke".equals(cookie.getName())) {
                final JSONObject cookieJSONObject =
                        new JSONObject(cookie.getValue());

                final String userEmail =
                        cookieJSONObject.getString(User.USER_EMAIL);
                if (Strings.isEmptyOrNull(userEmail)) {
                    break;
                }

                final JSONObject user = userRepository.getByEmail(
                        userEmail.toLowerCase().trim());
                if (null == user) {
                    break;
                }

                final String userPassword =
                        user.getString(User.USER_PASSWORD);
                final String hashPassword =
                        cookieJSONObject.getString(User.USER_PASSWORD);
                if (MD5.hash(userPassword).equals(hashPassword)) {
                    Sessions.login(request, response, user);
                }
            }
        }
    }
}
