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

package org.b3log.solo.util;

import org.b3log.latke.event.EventManager;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.google.inject.Inject;
import org.b3log.solo.repository.PreferenceRepository;
import org.json.JSONException;
import org.json.JSONObject;
import org.b3log.latke.cache.Cache;
import org.b3log.latke.cache.CacheFactory;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.solo.repository.impl.PreferenceGAERepository;
import static org.b3log.solo.model.Preference.*;

/**
 * Preference utilities.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.4, Jan 12, 2011
 */
public final class Preferences {

    /**
     * Preference cache.
     */
    private static Cache<String, Object> userPreferenceCache;
    /**
     * Preference repository.
     */
    private PreferenceRepository preferenceRepository =
            PreferenceGAERepository.getInstance();
    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(Preferences.class.getName());
    /**
     * Event manager.
     */
    private EventManager eventManager = EventManager.getInstance();
    /**
     * Skin utilities.
     */
    @Inject
    private Skins skins;

    static {
        userPreferenceCache = CacheFactory.getCache(PREFERENCE);
    }

    /**
     * Gets the user preference.
     *
     * @return user preference, returns {@code null} if not found
     */
    public JSONObject getPreference() {
        final Object preferenceString = userPreferenceCache.get(PREFERENCE);
        JSONObject ret = null;
        try {
            if (null == preferenceString) {
                LOGGER.info("Load preference from datastore");
                ret = preferenceRepository.get(PREFERENCE);

                if (null == ret) {
                    return null;
                }

                skins.loadSkins(ret);

                setPreference(ret);
            } else {
                ret = new JSONObject(preferenceString.toString());
            }
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new RuntimeException(e);
        }

        return ret;
    }

    /**
     * Sets the user preference with the specified preference in cache and
     * repository.
     *
     * @param preference the specified preference
     * @throws JSONException json exception
     * @throws RepositoryException repository exception
     */
    public void setPreference(final JSONObject preference)
            throws JSONException, RepositoryException {
        userPreferenceCache.put(PREFERENCE, preference.toString());
        preferenceRepository.update(PREFERENCE, preference);
    }
}
