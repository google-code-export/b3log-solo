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
package org.b3log.solo.util;

import java.util.logging.Level;
import java.util.logging.Logger;
import com.google.inject.Inject;
import org.b3log.solo.repository.PreferenceRepository;
import org.json.JSONException;
import org.json.JSONObject;
import org.b3log.latke.cache.Cache;
import org.b3log.latke.cache.CacheFactory;
import org.b3log.latke.repository.RepositoryException;
import static org.b3log.solo.model.Preference.*;

/**
 * Preference utilities.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Nov 8, 2010
 */
public final class PreferenceUtils {

    /**
     * Preference cache.
     */
    private static Cache<String, Object> userPreferenceCache;
    /**
     * Preference repository.
     */
    @Inject
    private PreferenceRepository preferenceRepository;
    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(PreferenceUtils.class.getName());

    static {
        userPreferenceCache = CacheFactory.getCache(PREFERENCE);
    }

    /**
     * Gets the user preference.
     *
     * @return user preference
     * @throws JSONException json exception
     */
    public JSONObject getPreference() throws JSONException {
        final Object preferenceString = userPreferenceCache.get(PREFERENCE);
        JSONObject ret = null;

        if (null == preferenceString) {
            // Loads from datastore
            try {
                ret = preferenceRepository.get(PREFERENCE);
                userPreferenceCache.put(PREFERENCE, ret.toString());
            } catch (final RepositoryException e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
                throw new RuntimeException(e);
            }
        } else {
            ret = new JSONObject(preferenceString.toString());
        }

        return ret;
    }

    /**
     * Sets the user preference with the specified preference.
     *
     * @param preference the specified preference
     * @throws JSONException json exception
     */
    public void setPreference(final JSONObject preference) throws JSONException {
        userPreferenceCache.put(PREFERENCE, preference.toString());
    }
}
