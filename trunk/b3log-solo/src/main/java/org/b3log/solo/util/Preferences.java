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

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.latke.Latkes;
import org.b3log.solo.repository.PreferenceRepository;
import org.json.JSONObject;
import org.b3log.latke.cache.Cache;
import org.b3log.latke.cache.CacheFactory;
import org.b3log.solo.repository.impl.PreferenceGAERepository;
import static org.b3log.solo.model.Preference.*;

/**
 * Preference utilities.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.6, Jul 12, 2011
 */
public final class Preferences {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(Preferences.class.getName());
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
     * Skin utilities.
     */
    private Skins skins = Skins.getInstance();

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
                    LOGGER.log(Level.WARNING,
                               "Can not load preference from datastore");
                    return null;
                }

                skins.loadSkins(ret);

                setPreference(ret);
            } else {
                ret = new JSONObject(preferenceString.toString());
            }
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new IllegalStateException(e);
        }

        return ret;
    }

    /**
     * Sets the user preference with the specified preference in cache and
     * repository.
     *
     * @param preference the specified preference
     * @throws Exception exception
     */
    public void setPreference(final JSONObject preference) throws Exception {
        @SuppressWarnings("unchecked")
        final Iterator<String> keys = preference.keys();
        while (keys.hasNext()) {
            final String key = keys.next();
            if (preference.isNull(key)) {
                throw new Exception("A value is null of preference[key=" + key
                                    + "]");
            }
        }

        preferenceRepository.update(PREFERENCE, preference);
        userPreferenceCache.put(PREFERENCE, preference.toString());

        if (preference.getBoolean(PAGE_CACHE_ENABLED)) {
            Latkes.enablePageCache();
        } else {
            Latkes.disablePageCache();
        }

        LOGGER.log(Level.FINER, "Set preference successfully");
    }

    /**
     * Gets the {@link Preferences} singleton.
     *
     * @return the singleton
     */
    public static Preferences getInstance() {
        return SingletonHolder.SINGLETON;
    }

    /**
     * Private default constructor.
     */
    private Preferences() {
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
        private static final Preferences SINGLETON = new Preferences();

        /**
         * Private default constructor.
         */
        private SingletonHolder() {
        }
    }
}
