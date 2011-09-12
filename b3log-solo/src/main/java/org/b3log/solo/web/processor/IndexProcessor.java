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

import org.b3log.latke.user.UserService;
import org.b3log.latke.user.UserServiceFactory;
import org.b3log.solo.util.Users;
import org.b3log.latke.model.Role;
import org.b3log.latke.model.User;
import org.b3log.latke.user.GeneralUser;
import org.b3log.latke.util.Strings;
import org.json.JSONException;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.util.Locales;
import org.b3log.solo.web.processor.renderer.FrontFreeMarkerRenderer;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.latke.annotation.RequestProcessing;
import org.b3log.latke.annotation.RequestProcessor;
import org.b3log.solo.web.action.util.Filler;
import org.b3log.latke.model.Pagination;
import org.b3log.latke.servlet.AbstractFreeMarkerRenderer;
import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.HTTPRequestMethod;
import org.b3log.latke.servlet.renderer.JSONRenderer;
import org.b3log.solo.web.action.util.Requests;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.Common;
import org.b3log.solo.model.PageTypes;
import org.b3log.solo.util.Preferences;
import org.b3log.solo.util.Skins;
import org.json.JSONObject;
import static org.b3log.latke.action.AbstractCacheablePageAction.*;

/**
 * Index processor.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.1.0.1, Sep 11, 2011
 * @since 0.3.1
 */
@RequestProcessor
public final class IndexProcessor {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(IndexProcessor.class.getName());
    /**
     * Filler.
     */
    private Filler filler = Filler.getInstance();
    /**
     * Preference utilities.
     */
    private Preferences preferenceUtils = Preferences.getInstance();
    /**
     * Skin utilities.
     */
    private Skins skins = Skins.getInstance();
    /**
     * Language service.
     */
    private LangPropsService langPropsService = LangPropsService.getInstance();
    /**
     * User utilities.
     */
    private Users userUtils = Users.getInstance();
    /**
     * User service.
     */
    private UserService userService = UserServiceFactory.getUserService();

    /**
     * Checks logged in with the specified context.
     * 
     * @param context the specified context
     */
    @RequestProcessing(value = {"/check-login.do"},
                       method = HTTPRequestMethod.POST)
    public void checkLoggedIn(final HTTPRequestContext context) {
        final JSONRenderer renderer = new JSONRenderer();
        context.setRenderer(renderer);

        final JSONObject currentUser = userUtils.getCurrentUser();
        final JSONObject jsonObjectToRender = new JSONObject();
        renderer.setJSONObject(jsonObjectToRender);

        try {
            jsonObjectToRender.put(Common.IS_LOGGED_IN, false);

            if (null == currentUser) {
                if (userService.isUserLoggedIn() && userService.isUserAdmin()) {
                    // Only should happen with the following cases:
                    // 1. Init Solo
                    //    Because of there is no any user in datastore before init Solo
                    //    although the administrator has been logged in for init
                    // 2. The collaborate administrator
                    jsonObjectToRender.put(Common.IS_LOGGED_IN, true);
                    jsonObjectToRender.put(Common.IS_ADMIN, true);
                    final GeneralUser admin = userService.getCurrentUser();
                    jsonObjectToRender.put(User.USER_NAME, admin.getNickname());

                    return;
                }

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

            String userName = currentUser.getString(User.USER_NAME);
            if (Strings.isEmptyOrNull(userName)) {
                // The administrators may be added via GAE Admin Console Permissions
                userName = userService.getCurrentUser().getNickname();
                jsonObjectToRender.put(Common.IS_ADMIN, true);
            }
            jsonObjectToRender.put(User.USER_NAME, userName);
        } catch (final JSONException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Shows index with the specified context.
     * 
     * @param context the specified context
     */
    @RequestProcessing(value = {"/"}, method = HTTPRequestMethod.GET)
    public void showIndex(final HTTPRequestContext context) {
        final AbstractFreeMarkerRenderer renderer =
                new FrontFreeMarkerRenderer();
        context.setRenderer(renderer);

        renderer.setTemplateName("index.ftl");
        final Map<String, Object> dataModel = renderer.getDataModel();

        final HttpServletRequest request = context.getRequest();
        final HttpServletResponse response = context.getResponse();
        final String requestURI = request.getRequestURI();
        try {
            final int currentPageNum = getCurrentPageNum(requestURI);
            if (-1 == currentPageNum) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }

            final JSONObject preference = preferenceUtils.getPreference();
            if (null == preference) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }

            skins.fillLanguage(preference, dataModel);

            request.setAttribute(CACHED_OID, "No id");
            request.setAttribute(CACHED_TITLE,
                                 dataModel.get(PageTypes.INDEX_ARTICLES)
                                 + "  [" + dataModel.get("pageNumLabel") + "="
                                 + currentPageNum + "]");
            request.setAttribute(CACHED_TYPE,
                                 dataModel.get(PageTypes.INDEX_ARTICLES));
            request.setAttribute(CACHED_LINK, requestURI);

            filler.fillIndexArticles(dataModel, currentPageNum, preference);

            @SuppressWarnings("unchecked")
            final List<JSONObject> articles =
                    (List<JSONObject>) dataModel.get(Article.ARTICLES);
            if (articles.isEmpty()) {
                try {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                } catch (final IOException ex) {
                    LOGGER.severe(ex.getMessage());
                }
            }

            filler.fillSide(dataModel, preference);
            filler.fillBlogHeader(dataModel, preference);
            filler.fillBlogFooter(dataModel, preference);

            dataModel.put(Pagination.PAGINATION_CURRENT_PAGE_NUM, currentPageNum);
            final String previousPageNum =
                    Integer.toString(currentPageNum > 1 ? currentPageNum - 1
                                     : 0);
            dataModel.put(Pagination.PAGINATION_PREVIOUS_PAGE_NUM,
                          "0".equals(previousPageNum) ? "" : previousPageNum);
            final Integer pageCount =
                    (Integer) dataModel.get(Pagination.PAGINATION_PAGE_COUNT);
            if (pageCount == currentPageNum + 1) { // The next page is the last page
                dataModel.put(Pagination.PAGINATION_NEXT_PAGE_NUM, "");
            } else {
                dataModel.put(Pagination.PAGINATION_NEXT_PAGE_NUM, currentPageNum
                                                                   + 1);
            }

            dataModel.put(Common.PATH, "");
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);

            try {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            } catch (final IOException ex) {
                LOGGER.severe(ex.getMessage());
            }
        }
    }

    /**
     * Handles errors with the specified context.
     * 
     * @param context the specified context
     */
    @RequestProcessing(value = {"/error.do"}, method = HTTPRequestMethod.GET)
    public void handleErrors(final HTTPRequestContext context) {
        final AbstractFreeMarkerRenderer renderer =
                new FrontFreeMarkerRenderer();
        context.setRenderer(renderer);

        renderer.setTemplateName("error.ftl");
        final Map<String, Object> dataModel = renderer.getDataModel();

        final HttpServletRequest request = context.getRequest();
        final HttpServletResponse response = context.getResponse();
        final String requestURI = request.getRequestURI();
        try {
            final JSONObject preference =
                    Preferences.getInstance().getPreference();
            if (null == preference) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }

            skins.fillLanguage(preference, dataModel);

            filler.fillSide(dataModel, preference);
            filler.fillBlogHeader(dataModel, preference);
            filler.fillBlogFooter(dataModel, preference);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);

            try {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            } catch (final IOException ex) {
                LOGGER.severe(ex.getMessage());
            }
        }
    }

    /**
     * Shows kill browser page with the specified context.
     * 
     * @param context the specified context
     */
    @RequestProcessing(value = {"/kill-browser.html"},
                       method = HTTPRequestMethod.GET)
    public void showKillBrowser(final HTTPRequestContext context) {
        final AbstractFreeMarkerRenderer renderer =
                new FrontFreeMarkerRenderer();
        context.setRenderer(renderer);

        renderer.setTemplateName("kill-browser.ftl");
        final Map<String, Object> dataModel = renderer.getDataModel();
        final HttpServletRequest request = context.getRequest();
        final HttpServletResponse response = context.getResponse();

        try {
            final Map<String, String> langs =
                    langPropsService.getAll(Locales.getLocale(request));
            dataModel.putAll(langs);
            final JSONObject preference = preferenceUtils.getPreference();
            filler.fillBlogFooter(dataModel, preference);
            filler.fillMinified(dataModel);

            request.setAttribute(CACHED_OID, "No id");
            request.setAttribute(CACHED_TITLE, "Kill Browser Page");
            request.setAttribute(CACHED_TYPE,
                                 langs.get(PageTypes.KILL_BROWSER_PAGE));
            request.setAttribute(CACHED_LINK, request.getRequestURI());
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);

            try {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            } catch (final IOException ex) {
                LOGGER.severe(ex.getMessage());
            }
        }
    }

    /**
     * Gets the request page number from the specified request URI.
     * 
     * @param requestURI the specified request URI
     * @return page number, returns {@code -1} if the specified request URI
     * can not convert to an number
     */
    private static int getCurrentPageNum(final String requestURI) {
        final String pageNumString = requestURI.substring("/".length());

        return Requests.getCurrentPageNum(pageNumString);
    }
}
