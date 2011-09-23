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
package org.b3log.solo.jsonrpc.impl;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.RuntimeMode;
import org.b3log.latke.action.ActionException;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.util.Locales;
import org.b3log.latke.util.Strings;
import org.b3log.latke.util.freemarker.Templates;
import org.b3log.solo.web.action.StatusCodes;
import org.b3log.solo.jsonrpc.AbstractGAEJSONRpcService;
import static org.b3log.solo.model.Preference.*;
import org.b3log.solo.model.Skin;
import org.b3log.solo.SoloServletListener;
import org.b3log.solo.model.Preference;
import org.b3log.solo.repository.PreferenceRepository;
import org.b3log.solo.repository.impl.PreferenceRepositoryImpl;
import org.b3log.solo.util.Preferences;
import org.b3log.solo.util.Skins;
import org.b3log.solo.util.TimeZones;
import org.b3log.solo.util.Users;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Preference service for JavaScript client.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.3.0, Sep 1, 2011
 */
public final class PreferenceService extends AbstractGAEJSONRpcService {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(
            PreferenceService.class.getName());
    /**
     * Skin utilities.
     */
    private Skins skins = Skins.getInstance();
    /**
     * Preference utilities.
     */
    private Preferences preferenceUtils = Preferences.getInstance();
    /**
     * Preference repository.
     */
    private PreferenceRepository preferenceRepository =
            PreferenceRepositoryImpl.getInstance();
    /**
     * User utilities.
     */
    private Users userUtils = Users.getInstance();
    /**
     * Time zone utilities.
     */
    private TimeZones timeZoneUtils = TimeZones.getInstance();

    /**
     * Gets signs.
     *
     * @param response the specified http servlet response
     * @return for example,
     * <pre>
     * [{
     *     "oId": "",
     *     "signHTML": ""
     *  }, ...]
     * </pre>
     * @throws ActionException action exception
     */
    public JSONArray getSigns(
            final HttpServletResponse response) throws ActionException {
        final JSONArray ret = new JSONArray();

        try {
            if (!userUtils.isLoggedIn()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return ret;
            }

            final JSONObject preference = preferenceUtils.getPreference();
            final JSONArray allSigns = // includes the empty sign(id=0)
                    new JSONArray(preference.getString(Preference.SIGNS));
            for (int i = 1; i < allSigns.length(); i++) { // excludes the empty sign
                ret.put(allSigns.getJSONObject(i));
            }
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new ActionException(e);
        }

        return ret;
    }

    /**
     * Gets preference.
     *
     * @param response the specified http servlet response
     * @return for example,
     * <pre>
     * {
     *     "preference": {
     *         "recentArticleDisplayCount": int,
     *         "mostUsedTagDisplayCount": int,
     *         "articleListDisplayCount": int,
     *         "articleListPaginationWindowSize": int,
     *         "blogTitle": "",
     *         "blogSubtitle": "",
     *         "mostCommentArticleDisplayCount": int,
     *         "blogHost": "",
     *         "localeString": "",
     *         "timeZoneId": "",
     *         "skinName": "",
     *         "skinDirName": "",
     *         "skins": "[{
     *             "skinName": "",
     *             "skinDirName": ""
     *         }, ....]",
     *         "noticeBoard": "",
     *         "htmlHead": "",
     *         "googleOAuthConsumerSecret": "",
     *         "externalRelevantArticlesDisplayCount": int,
     *         "relevantArticlesDisplayCount": int,
     *         "randomArticlesDisplayCount": int,
     *         "adminEmail": "",
     *         "metaKeywords": "",
     *         "metaDescription": "",
     *         "enableArticleUpdateHint": boolean,
     *         "signs": "[{
     *             "oId": "",
     *             "signHTML": ""
     *         }, ...]",
     *         "enablePostToTencentMicroblog": boolean,
     *         "allowVisitDraftViaPermalink": boolean,
     *         "version": ""
     *     }
     *     "sc": "GET_PREFERENCE_SUCC"
     * }
     * </pre>
     * @throws ActionException action exception
     */
    public JSONObject getPreference(
            final HttpServletResponse response) throws ActionException {
        final JSONObject ret = new JSONObject();

        try {
            if (!userUtils.isAdminLoggedIn()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return ret;
            }

            final JSONObject preference = preferenceUtils.getPreference();

            ret.put(PREFERENCE, preference);
            ret.put(Keys.STATUS_CODE, StatusCodes.GET_PREFERENCE_SUCC);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new ActionException(e);
        }

        return ret;
    }

    /**
     * Updates the preference by the specified request json object.
     *
     * @param requestJSONObject the specified request json object, for example,
     * <pre>
     * {
     *     "preference": {
     *         "recentArticleDisplayCount": int,
     *         "mostUsedTagDisplayCount": int,
     *         "articleListDisplayCount": int,
     *         "articleListPaginationWindowSize": int
     *         "blogTitle": "",
     *         "blogSubtitle": "",
     *         "mostCommentArticleDisplayCount": int,
     *         "skinDirName": "",
     *         "blogHost": "",
     *         "localeString": "",
     *         "timeZoneId": "",
     *         "noticeBoard": "",
     *         "htmlHead": "",
     *         "googleOAuthConsumerSecret": "",
     *         "externalRelevantArticlesDisplayCount": int,
     *         "relevantArticlesDisplayCount": int,
     *         "randomArticlesDisplayCount": int,
     *         "metaKeywords": "",
     *         "metaDescription": "",
     *         "enableArticleUpdateHint": boolean,
     *         "signs": [{
     *             "oId": "",
     *             "signHTML": ""
     *         }, ...],
     *         "enablePostToTencentMicroblog": boolean,
     *         "allowVisitDraftViaPermalink": boolean
     *     }
     * }, see {@link org.b3log.solo.model.Preference} for more details
     * </pre>
     * @param request the specified http servlet request
     * @param response the specified http servlet response
     * @return for example,
     * <pre>
     * {
     *     "sc": "UPDATE_PREFERENCE_SUCC"
     * }
     * </pre>
     * @throws ActionException action exception
     * @throws IOException io exception
     */
    public JSONObject updatePreference(final JSONObject requestJSONObject,
                                       final HttpServletRequest request,
                                       final HttpServletResponse response)
            throws ActionException, IOException {
        final JSONObject ret = new JSONObject();
        if (!userUtils.isAdminLoggedIn()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return ret;
        }
        final Transaction transaction = preferenceRepository.beginTransaction();
        try {
            final JSONObject preference =
                    requestJSONObject.getJSONObject(PREFERENCE);
            String blogHost = preference.getString(BLOG_HOST).
                    toLowerCase().trim();
            if (StringUtils.startsWithIgnoreCase(blogHost, "http://")) {
                blogHost = blogHost.substring("http://".length());
            }
            if (blogHost.endsWith("/")) {
                blogHost = blogHost.substring(0, blogHost.length() - 1);
            }

            LOGGER.log(Level.FINER, "Blog Host[{0}]", blogHost);

            String domain = null;
            final boolean hasPort = blogHost.contains(":");
            if (hasPort) {
                domain = blogHost.split(":")[0].trim();
            } else {
                domain = blogHost;
            }

            if (RuntimeMode.PRODUCTION == Latkes.getRuntimeMode()) {
                if ("localhost".equals(domain)) {
                    ret.put(Keys.STATUS_CODE,
                            StatusCodes.UPDATE_PREFERENCE_FAIL_CANNT_BE_LOCALHOST);
                    if (transaction.isActive()) {
                        transaction.rollback();
                    }

                    return ret;
                }
            }

            String port = "80";
            if (hasPort) {
                port = blogHost.split(":")[1].trim();
            }

            if (!"localhost".equals(domain) && !"80".equals(port)) {
                ret.put(Keys.STATUS_CODE, StatusCodes.UPDATE_PREFERENCE_FAIL_);
                if (transaction.isActive()) {
                    transaction.rollback();
                }

                return ret;
            }

            preference.put(BLOG_HOST, blogHost);

            final String skinDirName = preference.getString(Skin.SKIN_DIR_NAME);
            final String skinName = skins.getSkinName(skinDirName);
            preference.put(Skin.SKIN_NAME, skinName);
            final Set<String> skinDirNames = skins.getSkinDirNames();
            final JSONArray skinArray = new JSONArray();
            for (final String dirName : skinDirNames) {
                final JSONObject skin = new JSONObject();
                skinArray.put(skin);

                final String name = skins.getSkinName(dirName);
                skin.put(Skin.SKIN_NAME, name);
                skin.put(Skin.SKIN_DIR_NAME, dirName);
            }
            final String webRootPath = SoloServletListener.getWebRoot();
            final String skinPath = webRootPath + Skin.SKINS + "/" + skinDirName;
            LOGGER.log(Level.FINER, "Skin path[{0}]", skinPath);
            Templates.CACHE.clear();

            preference.put(Skin.SKINS, skinArray.toString());

            final String timeZoneId = preference.getString(TIME_ZONE_ID);
            timeZoneUtils.setTimeZone(timeZoneId);

            preference.put(Preference.SIGNS,
                           preference.getJSONArray(Preference.SIGNS).toString());

            final JSONObject oldPreference = preferenceUtils.getPreference();
            final String adminEmail = oldPreference.getString(ADMIN_EMAIL);
            preference.put(ADMIN_EMAIL, adminEmail);

            final boolean pageCacheEnabled =
                    oldPreference.getBoolean(PAGE_CACHE_ENABLED);
            preference.put(PAGE_CACHE_ENABLED, pageCacheEnabled);

            final String version = oldPreference.optString(VERSION);
            if (!Strings.isEmptyOrNull(version)) {
                preference.put(VERSION, version);
            }

            final String localeString = preference.getString(
                    Preference.LOCALE_STRING);
            LOGGER.log(Level.FINER, "Current locale[string={0}]", localeString);
            Latkes.setLocale(new Locale(
                    Locales.getLanguage(localeString),
                    Locales.getCountry(localeString)));

            preferenceUtils.setPreference(preference);

            transaction.commit();
            ret.put(Keys.STATUS_CODE, StatusCodes.UPDATE_PREFERENCE_SUCC);

            Templates.CONFIGURATION.setDirectoryForTemplateLoading(
                    new File(skinPath));
        } catch (final Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new ActionException(e);
        }

        return ret;
    }

    /**
     * Gets the {@link PreferenceService} singleton.
     *
     * @return the singleton
     */
    public static PreferenceService getInstance() {
        return SingletonHolder.SINGLETON;
    }

    /**
     * Private default constructor.
     */
    private PreferenceService() {
    }

    /**
     * Singleton holder.
     *
     * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
     * @version 1.0.0.0, Jan 12, 2011
     */
    private static final class SingletonHolder {

        /**
         * Singleton.
         */
        private static final PreferenceService SINGLETON =
                new PreferenceService();

        /**
         * Private default constructor.
         */
        private SingletonHolder() {
        }
    }
}
