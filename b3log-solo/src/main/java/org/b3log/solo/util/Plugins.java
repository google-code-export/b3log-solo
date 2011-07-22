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

import java.util.List;
import org.b3log.latke.Keys;
import org.b3log.latke.plugin.AbstractPlugin;
import org.b3log.latke.plugin.PluginLoader;
import org.b3log.latke.repository.Query;
import org.b3log.latke.util.CollectionUtils;
import org.b3log.solo.repository.PluginRepository;
import org.b3log.solo.repository.impl.PluginGAERepository;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Plugin utilities.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Jul 21, 2011
 */
public final class Plugins {

    /**
     * Plugin repository.
     */
    private static final PluginRepository PLUGIN_REPOS =
            PluginGAERepository.getInstance();

    /**
     * Updates datastore plugin descriptions with the specified plugins and 
     * refreshes these plugins' id.
     * 
     * <p>
     * Invokes this method will clear all plugin descriptions in datastore and 
     * adds the specified plugins into datastore.
     * </p>
     * 
     * @param plugins the specified plugins
     * @throws Exception exception 
     */
    public static void refresh(final List<AbstractPlugin> plugins)
            throws Exception {
        final JSONObject result = PLUGIN_REPOS.get(new Query());
        final JSONArray pluginArray = result.getJSONArray(Keys.RESULTS);
        final List<JSONObject> persistedPlugins =
                CollectionUtils.jsonArrayToList(pluginArray);
        
        // Clears all plugin descriptions in datastore
        for (final JSONObject oldPluginDesc : persistedPlugins) {
            PLUGIN_REPOS.remove(oldPluginDesc.getString(Keys.OBJECT_ID));
        }
        
        // Adds all plugins into datastore and refreshes ids
        for (final AbstractPlugin plugin : plugins) {
            final JSONObject jsonObject = plugin.toJSONObject();
            final String id = PLUGIN_REPOS.add(jsonObject);
            
            plugin.setId(id); // Refreshes id
        }
        
        PluginLoader.set(plugins);
    }

    /**
     * Private default constructor.
     */
    private Plugins() {
    }
}
