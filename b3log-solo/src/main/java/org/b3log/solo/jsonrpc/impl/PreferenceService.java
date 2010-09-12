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
 * @version 1.0.0.4, Sep 12, 2010
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
     *         "htmlHead": ""
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
     *         "htmlHead": ""
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
