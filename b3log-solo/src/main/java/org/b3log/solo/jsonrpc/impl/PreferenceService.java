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
import java.net.URLEncoder;
import java.util.Set;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.basic.DefaultOAuthProvider;
import org.b3log.latke.Keys;
import org.b3log.latke.action.AbstractCacheablePageAction;
import org.b3log.latke.action.ActionException;
import org.b3log.latke.repository.gae.AbstractGAERepository;
import org.b3log.latke.util.freemarker.Templates;
import org.b3log.solo.action.StatusCodes;
import org.b3log.solo.action.google.OAuthBuzzCallback;
import org.b3log.solo.google.auth.BuzzOAuth;
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
    public static final String BUZZ_SCOPE =
            "https://www.googleapis.com/auth/buzz";
    /**
     * Buzz OAuth consumer.
     */
    private OAuthConsumer buzzOAuthConsumer;

    /**
     * Gets Buzz OAuth consumer.
     *
     * @return Buzz OAuth consumer
     */
    public OAuthConsumer getBuzzOAuthConsumer() {
        return buzzOAuthConsumer;
    }

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
        try {
            final JSONObject preference =
                    SoloServletListener.getUserPreference();
            final String blogHost = preference.optString(Preference.BLOG_HOST);
            final String consumerKey = blogHost.split(":")[0];
            final String consumerSecret =
                    preference.getString(Preference.GOOLE_OAUTH_CONSUMER_SECRET);
            buzzOAuthConsumer = new DefaultOAuthConsumer(
                    consumerKey, consumerSecret);
            final OAuthProvider provider =
                    new DefaultOAuthProvider(
                    "https://www.google.com/accounts/OAuthGetRequestToken?scope="
                    + URLEncoder.encode(BUZZ_SCOPE, "UTF-8"),
                    "https://www.google.com/accounts/OAuthGetAccessToken",
                    "https://www.google.com/buzz/api/auth/OAuthAuthorizeToken?domain="
                    + consumerKey + "&scope=" + BUZZ_SCOPE + "&iconUrl="
                    + "http://" + blogHost + "/favicon.png");

            LOGGER.log(Level.INFO, "Fetching request token...");

            final String authUrl = provider.retrieveRequestToken(
                    buzzOAuthConsumer, "http://" + blogHost
                                       + BuzzOAuth.CALLBACK_URL);
            LOGGER.log(Level.INFO, "Authorization URL[{0}]", authUrl);
//            System.out.println("Request token: " + consumer.getToken());
//            System.out.println("Token secret: " + consumer.getTokenSecret());
            final String verifier =
                    OAuthBuzzCallback.getVerifier(buzzOAuthConsumer.getToken(),
                                                  -1);
            LOGGER.log(Level.INFO, "Fetching access token...");

            LOGGER.log(Level.INFO, "Verifier[{0}]", verifier);
            provider.retrieveAccessToken(buzzOAuthConsumer, verifier);

            System.out.println("Access token: " + buzzOAuthConsumer.getToken());
            System.out.println("Token secret: " + buzzOAuthConsumer.
                    getTokenSecret());
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
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
