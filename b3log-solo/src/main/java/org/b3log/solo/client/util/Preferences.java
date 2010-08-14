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
package org.b3log.solo.client.util;

import com.google.inject.Inject;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.util.cache.Cache;
import org.b3log.latke.util.cache.qualifier.LruMemory;
import org.b3log.solo.model.Preference;
import org.b3log.solo.repository.PreferenceRepository;
import org.json.JSONObject;

/**
 * Preference utilities.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Aug 14, 2010
 */
public final class Preferences {

    /**
     * Cache.
     */
    @Inject
    @LruMemory
    private Cache<String, ?> cache;
    /**
     * Preference repository.
     */
    @Inject
    private PreferenceRepository preferenceRepository;

    /**
     * Gets preference.
     *
     * @return preference
     * @throws RepositoryException repository exception
     */
    @SuppressWarnings("unchecked")
    public JSONObject getPreference() throws RepositoryException {
        JSONObject ret = (JSONObject) cache.get(Preference.PREFERENCE);
        if (null == ret) {
            ret = preferenceRepository.get(Preference.PREFERENCE);
            ((Cache<String, JSONObject>) cache).put(Preference.PREFERENCE,
                                                    ret);
        }

        return ret;
    }
}
