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

import com.google.inject.Inject;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.client.action.ActionException;
import org.b3log.latke.util.cache.Cache;
import org.b3log.latke.util.cache.qualifier.LruMemory;
import org.b3log.solo.action.StatusCodes;
import org.b3log.solo.jsonrpc.AbstractJSONRpcService;
import org.b3log.solo.util.Preferences;
import org.b3log.solo.model.Preference;
import org.b3log.solo.model.Skin;
import org.b3log.solo.repository.PreferenceRepository;
import org.b3log.solo.util.Skins;
import org.json.JSONObject;

/**
 * Preference service for JavaScript client.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Aug 21, 2010
 */
public final class PreferenceService extends AbstractJSONRpcService {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(
            PreferenceService.class);
    /**
     * Preference repository.
     */
    @Inject
    private PreferenceRepository preferenceRepository;
    /**
     * Cache.
     */
    @Inject
    @LruMemory
    private Cache<String, ?> cache;
    /**
     * Preference utilities.
     */
    @Inject
    private Preferences preferences;
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
     *         "skinName": "",
     *         "skinDirName": "",
     *         "skins: [{
     *             "skinName": "",
     *             "skinDirName": ""
     *         }, ....]
     *     }
     *     "sc": "GET_PREFERENCE_SUCC"
     * }
     * </pre>
     * @throws ActionException action exception
     */
    public JSONObject getPreference() throws ActionException {
        final JSONObject ret = new JSONObject();

        try {
            final JSONObject preference = preferences.getPreference();

            ret.put(Preference.PREFERENCE, preference);
            ret.put(Keys.STATUS_CODE, StatusCodes.GET_PREFERENCE_SUCC);

        } catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
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

        final JSONObject ret = new JSONObject();
        try {
            final JSONObject preference =
                    requestJSONObject.getJSONObject(Preference.PREFERENCE);

            final String skinDirName = preference.getString(Skin.SKIN_DIR_NAME);
            final String skinName = skins.getSkinName(skinDirName);
            preference.put(Skin.SKIN_NAME, skinName);

            preferenceRepository.update(Preference.PREFERENCE, preference);
            ((Cache<String, JSONObject>) cache).put(Preference.PREFERENCE,
                                                    preference);

            ret.put(Keys.STATUS_CODE, StatusCodes.UPDATE_PREFERENCE_SUCC);
        } catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new ActionException(e);
        }

        return ret;
    }
}
