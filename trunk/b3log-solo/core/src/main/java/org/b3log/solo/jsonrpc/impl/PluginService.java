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

import org.b3log.latke.plugin.AbstractPlugin;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.action.ActionException;
import org.b3log.latke.plugin.PluginManager;
import org.b3log.latke.plugin.PluginStatus;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.service.LangPropsService;
import org.b3log.solo.jsonrpc.AbstractGAEJSONRpcService;
import org.b3log.solo.repository.PluginRepository;
import org.b3log.solo.repository.impl.PluginRepositoryImpl;
import org.b3log.solo.util.Users;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Plugin service for JavaScript client.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Jul 22, 2011
 * @since 0.3.1
 */
public final class PluginService extends AbstractGAEJSONRpcService {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(AdminService.class.getName());
    /**
     * User utilities.
     */
    private Users userUtils = Users.getInstance();
    /**
     * Language service.
     */
    private LangPropsService langPropsService = LangPropsService.getInstance();
    /**
     * Plugin repository.
     */
    private PluginRepository pluginRepository =
            PluginRepositoryImpl.getInstance();

    /**
     * Sets a plugin's status with the specified plugin id, status.
     * 
     * @param pluginId the specified plugin id
     * @param status the specified status, see {@link PluginStatus}
     * @param request the specified http servlet request
     * @param response the specified http servlet response
     * @return for example,
     * <pre>
     * {
     *     "sc": boolean,
     *     "msg": "" 
     * }
     * </pre>
     * @throws ActionException action exception
     * @throws IOException io exception
     */
    public JSONObject setPluginStatus(final String pluginId, final String status,
                                      final HttpServletRequest request,
                                      final HttpServletResponse response)
            throws ActionException, IOException {
        final JSONObject ret = new JSONObject();

        if (!userUtils.isLoggedIn(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return ret;
        }

        final Map<String, String> langs =
                langPropsService.getAll(Latkes.getLocale());

        final PluginManager pluginManager = PluginManager.getInstance();
        final List<AbstractPlugin> plugins = pluginManager.getPlugins();

        for (final AbstractPlugin plugin : plugins) {
            if (plugin.getId().equals(pluginId)) {
                final Transaction transaction =
                        pluginRepository.beginTransaction();
                try {
                    plugin.setStatus(PluginStatus.valueOf(status));

                    pluginRepository.update(pluginId, plugin.toJSONObject());

                    transaction.commit();

                    pluginManager.update(plugin);

                    ret.put(Keys.STATUS_CODE, true);
                    ret.put(Keys.MSG, langs.get("setSuccLabel"));
                    return ret;
                } catch (final Exception e) {
                    if (transaction.isActive()) {
                        transaction.rollback();
                    }

                    LOGGER.log(Level.SEVERE, "Set plugin status error", e);

                    try {
                        ret.put(Keys.STATUS_CODE, false);
                        ret.put(Keys.MSG, langs.get("setFailLabel"));
                        return ret;
                    } catch (final JSONException ex) {
                        throw new ActionException(
                                "Set plugin status fatal error!");
                    }
                }
            }
        }



        try {
            ret.put(Keys.STATUS_CODE, false);
            ret.put(Keys.MSG, langs.get("refreshAndRetryLabel"));

            return ret;
        } catch (final JSONException ex) {
            throw new ActionException("Set plugin status fatal error!");
        }
    }

    /**
     * Gets the {@link PluginService} singleton.
     *
     * @return the singleton
     */
    public static PluginService getInstance() {
        return SingletonHolder.SINGLETON;


    }

    /**
     * Private default constructor.
     */
    private PluginService() {
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
        private static final PluginService SINGLETON = new PluginService();

        /**
         * Private default constructor.
         */
        private SingletonHolder() {
        }
    }
}
