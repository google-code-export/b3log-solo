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
package org.b3log.solo.action.impl;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import org.b3log.latke.action.ActionException;
import com.google.inject.Inject;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.latke.Keys;
import org.b3log.latke.action.AbstractAction;
import org.b3log.latke.model.User;
import org.b3log.latke.util.Locales;
import org.b3log.solo.action.util.Filler;
import org.b3log.solo.model.Common;
import org.b3log.solo.model.Preference;
import org.b3log.solo.model.Skin;
import org.b3log.solo.SoloServletListener;
import org.json.JSONObject;

/**
 * Error action. error.ftl.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.2, Sep 12, 2010
 */
public final class ErrorAction extends AbstractAction {

    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(ErrorAction.class.getName());
    /**
     * User service.
     */
    private UserService userService = UserServiceFactory.getUserService();
    /**
     * Filler.
     */
    @Inject
    private Filler filler;
    /**
     * lang.properties.
     */
    private static ResourceBundle lang;

    @Override
    protected Map<?, ?> doFreeMarkerAction(
            final freemarker.template.Template template,
            final HttpServletRequest request,
            final HttpServletResponse response) throws ActionException {
        final Map<String, Object> ret = new HashMap<String, Object>();

        try {
            final JSONObject preference =
                    SoloServletListener.getUserPreference();
            final String blogHost = preference.getString(Preference.BLOG_HOST);
            final String localeString = preference.getString(
                    Preference.LOCALE_STRING);
            final Locale locale = new Locale(
                    Locales.getLanguage(localeString),
                    Locales.getCountry(localeString));
            lang = ResourceBundle.getBundle(Keys.LANGUAGE, locale);
            ret.put("allTagsLabel", lang.getString("allTagsLabel"));
            ret.put("adminLabel", lang.getString("adminLabel"));
            ret.put("clearAllCacheLabel", lang.getString("clearAllCacheLabel"));
            ret.put("clearCacheLabel", lang.getString("clearCacheLabel"));
            ret.put("logoutLabel", lang.getString("logoutLabel"));
            ret.put("loginLabel", lang.getString("loginLabel"));
            ret.put("viewCount1Label", lang.getString("viewCount1Label"));
            ret.put("articleCount1Label", lang.getString("articleCount1Label"));
            ret.put("commentCount1Label", lang.getString("commentCount1Label"));
            ret.put("atomLabel", lang.getString("atomLabel"));
            ret.put("sorryLabel", lang.getString("sorryLabel"));
            ret.put("returnTo1Label", lang.getString("returnTo1Label"));
            ret.put("notFoundLabel", lang.getString("notFoundLabel"));

            ret.put(User.USER_EMAIL,
                    preference.getString(Preference.ADMIN_GMAIL));
            ret.put(Preference.BLOG_TITLE,
                    preference.getString(Preference.BLOG_TITLE));
            ret.put(Preference.BLOG_HOST, blogHost);
            ret.put(Preference.BLOG_SUBTITLE,
                    preference.getString(Preference.BLOG_SUBTITLE));
            ret.put(Skin.SKIN_DIR_NAME, preference.getString(Skin.SKIN_DIR_NAME));
            ret.put(Common.VERSION, SoloServletListener.VERSION);
            ret.put(Preference.HTML_HEAD,
                    preference.getString(Preference.HTML_HEAD));
        } catch (final Exception e) {
            LOGGER.severe(e.getMessage());
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
