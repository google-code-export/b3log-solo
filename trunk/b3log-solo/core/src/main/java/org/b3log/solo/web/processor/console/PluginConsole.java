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
package org.b3log.solo.web.processor.console;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.latke.Keys;
import org.b3log.latke.annotation.RequestProcessing;
import org.b3log.latke.annotation.RequestProcessor;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.HTTPRequestMethod;
import org.b3log.latke.servlet.renderer.JSONRenderer;
import org.b3log.solo.service.PluginQueryService;
import org.b3log.solo.util.QueryResults;
import org.b3log.solo.util.Users;
import org.b3log.solo.web.util.Requests;
import org.json.JSONObject;

/**
 * Plugin console request processing.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Oct 27, 2011
 * @since 0.4.0
 */
@RequestProcessor
public final class PluginConsole {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(PluginConsole.class.getName());
    /**
     * User utilities.
     */
    private Users userUtils = Users.getInstance();
    /**
     * Plugin query service.
     */
    private PluginQueryService pluginQueryService = PluginQueryService.
            getInstance();
    /**
     * Get files request URI prefix.
     */
    private static final String PLUGINS_URI_PREFIX = "/console/plugins/";
    /**
     * Language service.
     */
    private LangPropsService langPropsService = LangPropsService.getInstance();

    /**
     * Gets plugins by the specified request json object.
     * 
     * <p>
     * The request URI contains the pagination arguments. For example, the 
     * request URI is /console/plugins/1/10/20, means the current page is 1, the 
     * page size is 10, and the window size is 20.
     * </p>
     * 
     * <p>
     * Renders the response with a json object, for example,
     * <pre>
     * {
     *     "sc": boolean,
     *     "pagination": {
     *         "paginationPageCount": 100,
     *         "paginationPageNums": [1, 2, 3, 4, 5]
     *     },
     *     "plugins": [{
     *         "name": "",
     *         "version": "",
     *         "author": "",
     *         "status": "", // Enumeration name of {@link org.b3log.latke.plugin.PluginStatus}
     *      }, ....]
     * }
     * </pre>
     * 
     * @param request the specified http servlet request
     * @param response the specified http servlet response
     * @param context the specified http request context
     * @throws Exception exception
     * @see Requests#PAGINATION_PATH_PATTERN
     */
    @RequestProcessing(value = PLUGINS_URI_PREFIX
                               + Requests.PAGINATION_PATH_PATTERN,
                       method = HTTPRequestMethod.GET)
    public void getPlugins(final HttpServletRequest request,
                           final HttpServletResponse response,
                           final HTTPRequestContext context)
            throws Exception {
        if (!userUtils.isLoggedIn(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        final JSONRenderer renderer = new JSONRenderer();
        context.setRenderer(renderer);

        try {
            final String requestURI = request.getRequestURI();
            final String path =
                    requestURI.substring(PLUGINS_URI_PREFIX.length());

            final JSONObject requestJSONObject =
                    Requests.buildPaginationRequest(path);

            final JSONObject result = pluginQueryService.getPlugins(
                    requestJSONObject);

            renderer.setJSONObject(result);

            result.put(Keys.STATUS_CODE, true);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);

            final JSONObject jsonObject = QueryResults.defaultResult();
            renderer.setJSONObject(jsonObject);
            jsonObject.put(Keys.MSG, langPropsService.get("getFailLabel"));
        }
    }
}
