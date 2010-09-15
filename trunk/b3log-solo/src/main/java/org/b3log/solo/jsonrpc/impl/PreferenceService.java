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

import com.google.appengine.api.datastore.Transaction;
import com.google.inject.Inject;
import java.io.IOException;
import java.util.Set;
import java.util.TimeZone;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.latke.Keys;
import org.b3log.latke.action.AbstractCacheablePageAction;
import org.b3log.latke.action.ActionException;
import org.b3log.latke.repository.gae.AbstractGAERepository;
import org.b3log.latke.util.freemarker.Templates;
import org.b3log.solo.action.StatusCodes;
import org.b3log.solo.jsonrpc.AbstractGAEJSONRpcService;
import org.b3log.solo.model.Preference;
import org.b3log.solo.model.Skin;
import org.b3log.solo.repository.PreferenceRepository;
import org.b3log.solo.servlet.SoloServletListener;
import org.b3log.solo.util.Skins;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Preference service for JavaScript client.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.6, Sep 15, 2010
 */
public final class PreferenceService extends AbstractGAEJSONRpcService {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(
            PreferenceService.class.getName());
    /**
     * Preference repository.
     */
    @Inject
    private PreferenceRepository preferenceRepository;
    /**
     * Skin utilities.
     */
    @Inject
    private Skins skins;
    /**
     * Buzz scope.
     */
    private static final String BUZZ_SCOPE =
            "https://www.googleapis.com/auth/buzz";

    /**
     * Enables Google Buzz sync by the specified request json object.
     *
     * @param request the specified http servlet request
     * @param response the specified http servlet response
     * @throws ActionException action exception
     * @throws IOException io exception
     */
    public void enableBuzzSync(final HttpServletRequest request,
                               final HttpServletResponse response)
            throws ActionException, IOException {
        final JSONObject preference = SoloServletListener.getUserPreference();
        final String blogHost = preference.optString(Preference.BLOG_HOST);

        final String base = "https://www.google.com/accounts/o8/id?";
        final StringBuilder params =
                new StringBuilder("openid.ax.mode=fetch_reuest");
        params.append(
                "&openid.ax.type.email=http://axschema.org/contact/email");
        params.append("&openid.ax.required=firstname,fullname,lastname,email");
        params.append(
                "&openid.ax.type.firstname=http://axschema.org/namePerson/first");
        params.append(
                "&openid.ax.type.fullname=http://axschema.org/namePerson");
        params.append(
                "&openid.ax.type.lastname=http://axschema.org/namePerson/last");
        params.append(
                "&openid.claimed_id=http://specs.openid.net/auth/2.0/identifier_select");
        params.append(
                "&openid.identity=http://specs.openid.net/auth/2.0/identifier_select");
        params.append("&openid.mode=checkid_setup");
        params.append("&openid.ns=http://specs.openid.net/auth/2.0");
        params.append("&openid.ns.ax=http://openid.net/srv/ax/1.0");
        params.append(
                "&openid.ns.oauth=http://specs.openid.net/extensions/oauth/1.0");
        params.append("&openid.oauth.consumer=");
        params.append(blogHost);
        params.append("&openid.oauth.scope=");
        params.append(BUZZ_SCOPE);
        params.append("&openid.realm=http://");
        params.append(blogHost);
        params.append("&openid.return_to=http://");
        params.append(blogHost);
        params.append("/admin-index.do");
        params.append("&openid.ns.ui=http://specs.openid.net/extensions/ui/1.0");
        params.append("&openid.ns.ext1=http://openid.net/srv/ax/1.0");


        LOGGER.info("Redirct to Google....");

        final String queryString =
                params.toString().replaceAll(":", "%3A").replaceAll("/", "%2F").
                replaceAll(",", "%2C");

        response.sendRedirect(base + queryString);
    }

    /**
     * Gets preference.
     *
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
     *         "adminGmail": "",
     *         "localeString": ""
     *         "skinName": "",
     *         "skinDirName": "",
     *         "skins: [{
     *             "skinName": "",
     *             "skinDirName": ""
     *         }, ....],
     *         "noticeBoard": "",
     *         "htmlHead": "",
     *         "googleOAuthConsumerSecret": "",
     *         "enablePostToBuzz": boolean
     *     }
     *     "sc": "GET_PREFERENCE_SUCC"
     * }
     * </pre>
     * @throws ActionException action exception
     */
    public JSONObject getPreference() throws ActionException {
        final JSONObject ret = new JSONObject();

        try {
            final JSONObject preference =
                    SoloServletListener.getUserPreference();

            ret.put(Preference.PREFERENCE, preference);
            ret.put(Keys.STATUS_CODE, StatusCodes.GET_PREFERENCE_SUCC);

        } catch (final Exception e) {
            LOGGER.severe(e.getMessage());
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
     *         "adminGmail": "",
     *         "localeString": "",
     *         "noticeBoard": "",
     *         "htmlHead": "",
     *         "googleOAuthConsumerSecret": "",
     *         "enablePostToBuzz": boolean
     *     }
     * }, see {@link Preference} for more details
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
    @SuppressWarnings("unchecked")
    public JSONObject updatePreference(final JSONObject requestJSONObject,
                                       final HttpServletRequest request,
                                       final HttpServletResponse response)
            throws ActionException, IOException {
        checkAuthorized(request, response);
        final Transaction transaction =
                AbstractGAERepository.DATASTORE_SERVICE.beginTransaction();
        final JSONObject ret = new JSONObject();
        try {
            final JSONObject preference =
                    requestJSONObject.getJSONObject(Preference.PREFERENCE);

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

            preference.put(Skin.SKINS, skinArray.toString());


            final String localeString = preference.getString(
                    Preference.LOCALE_STRING);
            if ("zh_CN".equals(localeString)) {
                Templates.CONFIGURATION.setTimeZone(
                        TimeZone.getTimeZone("Asia/Shanghai"));
            }

            preferenceRepository.update(Preference.PREFERENCE, preference);
            SoloServletListener.setUserPreference(preference);

            // Clear page cache
            AbstractCacheablePageAction.PAGE_CACHE.removeAll();

            transaction.commit();
            ret.put(Keys.STATUS_CODE, StatusCodes.UPDATE_PREFERENCE_SUCC);
        } catch (final Exception e) {
            transaction.rollback();
            LOGGER.severe(e.getMessage());
            throw new ActionException(e);
        }

        return ret;
    }
}
