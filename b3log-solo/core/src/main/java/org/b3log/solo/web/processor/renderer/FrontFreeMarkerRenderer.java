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
package org.b3log.solo.web.processor.renderer;

import freemarker.template.Template;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import org.b3log.latke.action.AbstractCacheablePageAction;
import org.b3log.latke.model.Role;
import org.b3log.latke.model.User;
import org.b3log.latke.repository.Repository;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.renderer.freemarker.CacheFreeMarkerRenderer;
import org.b3log.latke.user.GeneralUser;
import org.b3log.latke.user.UserService;
import org.b3log.latke.user.UserServiceFactory;
import org.b3log.latke.util.Strings;
import org.b3log.solo.model.Common;
import org.b3log.solo.repository.impl.StatisticRepositoryImpl;
import org.b3log.solo.util.Statistics;
import org.b3log.solo.util.Users;
import org.b3log.solo.web.action.impl.InitAction;
import org.b3log.solo.web.processor.LoginProcessor;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * <a href="http://freemarker.org">FreeMarker</a> HTTP response 
 * renderer.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.3, Sep 18, 2011
 * @since 0.3.1
 */
public final class FrontFreeMarkerRenderer extends CacheFreeMarkerRenderer {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(FrontFreeMarkerRenderer.class.getName());
    /**
     * Statistic utilities.
     */
    private Statistics statistics = Statistics.getInstance();
    /**
     * Statistic repository.
     */
    private Repository statisticRepository =
            StatisticRepositoryImpl.getInstance();
    /**
     * User utilities.
     */
    private Users userUtils = Users.getInstance();
    /**
     * User service.
     */
    private UserService userService = UserServiceFactory.getUserService();

    /**
     * {@inheritDoc}
     * 
     * <p>
     * Puts the top bar replacement flag into data model.
     * </p>
     */
    @Override
    protected void beforeRender(final HTTPRequestContext context)
            throws Exception {
        getDataModel().put(Common.TOP_BAR_REPLACEMENT_FLAG_KEY,
                           Common.TOP_BAR_REPLACEMENT_FLAG);
    }

    /**
     * {@inheritDoc}
     * 
     * <p>
     * Blog statistic view count +1.
     * </p>
     */
    @Override
    protected void afterRender(final HTTPRequestContext context)
            throws Exception {
        final HttpServletRequest request = context.getRequest();
        String pageContent =
                (String) request.getAttribute(
                AbstractCacheablePageAction.CACHED_CONTENT);
        if (null != pageContent) {
            final Template topBarTemplate =
                    InitAction.TEMPLATE_CFG.getTemplate("top-bar.ftl");
            final StringWriter stringWriter = new StringWriter();


            final Map<String, Object> topBarModel =
                    new HashMap<String, Object>();

            LoginProcessor.tryLogInWithCookie(request, context.getResponse());
            final JSONObject currentUser = userUtils.getCurrentUser(request);

            try {
                topBarModel.put(Common.IS_LOGGED_IN, false);

                if (null == currentUser) {
                    if (userService.isUserLoggedIn(request)
                        && userService.isUserAdmin(request)) {
                        // Only should happen with the following cases:
                        // 1. Init Solo
                        //    Because of there is no any user in datastore before init Solo
                        //    although the administrator has been logged in for init
                        // 2. The collaborate administrator
                        topBarModel.put(Common.IS_LOGGED_IN, true);
                        topBarModel.put(Common.IS_ADMIN, true);
                        final GeneralUser admin =
                                userService.getCurrentUser(request);
                        topBarModel.put(User.USER_NAME,
                                        admin.getNickname());

                        return;
                    }

                    topBarModel.put(Common.LOGIN_URL,
                                    userService.createLoginURL(
                            Common.ADMIN_INDEX_URI));
                    return;
                }

                topBarModel.put(Common.IS_LOGGED_IN, true);
                topBarModel.put(Common.LOGOUT_URL,
                                userService.createLogoutURL("/"));
                topBarModel.put(Common.IS_ADMIN,
                                Role.ADMIN_ROLE.equals(currentUser.getString(
                        User.USER_ROLE)));

                String userName = currentUser.getString(User.USER_NAME);
                if (Strings.isEmptyOrNull(userName)) {
                    // The administrators may be added via GAE Admin Console Permissions
                    userName = userService.getCurrentUser(request).getNickname();
                    topBarModel.put(Common.IS_ADMIN, true);
                }
                topBarModel.put(User.USER_NAME, userName);
            } catch (final JSONException e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }

            topBarTemplate.process(topBarModel, stringWriter);

            request.setAttribute(AbstractCacheablePageAction.CACHED_CONTENT,
                                 pageContent.replace(
                    Common.TOP_BAR_REPLACEMENT_FLAG, stringWriter.toString()));
        }

        super.afterRender(context);

        final Transaction transaction = statisticRepository.beginTransaction();
        transaction.clearQueryCache(false);
        try {
            statistics.incBlogViewCount();
            transaction.commit();
        } catch (final Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }

            LOGGER.log(Level.WARNING, "After render failed", e);
        }
    }
}
