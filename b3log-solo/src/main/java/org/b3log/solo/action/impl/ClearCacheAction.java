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

package org.b3log.solo.action.impl;

import com.google.inject.Inject;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.latke.Keys;
import org.b3log.latke.action.AbstractAction;
import org.b3log.latke.action.ActionException;
import org.b3log.latke.action.util.PageCaches;
import org.b3log.latke.util.Strings;
import org.b3log.solo.model.Common;
import org.b3log.solo.util.PageCacheKeys;
import org.b3log.solo.util.Users;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Cache clear action.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Jan 3, 2011
 */
public final class ClearCacheAction extends AbstractAction {

    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(ClearCacheAction.class.getName());
    /**
     * User utilities.
     */
    @Inject
    private Users userUtils;
    /**
     * Page cache utilities.
     */
    @Inject
    private PageCacheKeys pageCacheKeys;

    @Override
    protected Map<?, ?> doFreeMarkerAction(
            final freemarker.template.Template template,
            final HttpServletRequest request,
            final HttpServletResponse response) throws ActionException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected JSONObject doAjaxAction(final JSONObject data,
                                      final HttpServletRequest request,
                                      final HttpServletResponse response)
            throws ActionException {
        final JSONObject ret = new JSONObject();

        final String all = data.optString("all");
        try {
            if (Strings.isEmptyOrNull(all)) { // Just clears single page cache
                final String uri = data.getString(Common.URI);
                clearPageCache(uri, request, response);
            } else { // Clears all page caches
                clearAllPageCache(request, response);
            }

            ret.put(Keys.STATUS_CODE, true);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);

            try {
                ret.put(Keys.STATUS_CODE, false);
            } catch (final JSONException ex) {
                LOGGER.log(Level.SEVERE, ex.getMessage(), ex);

                throw new ActionException(ex);
            }
        }

        return ret;
    }

    /**
     * Clears a page cache specified by the given URI.
     *
     * @param uri the specified URI
     * @param request the specified http servlet request
     * @param response the specified http servlet response
     * @throws ActionException action exception
     * @throws IOException io exception
     */
    public void clearPageCache(final String uri,
                               final HttpServletRequest request,
                               final HttpServletResponse response)
            throws ActionException, IOException {
        if (!userUtils.isAdminLoggedIn()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String pageCacheKey = uri;
        pageCacheKey = pageCacheKeys.getPageCacheKey(uri, null);

        LOGGER.log(Level.INFO, "Clears page cache[pageCacheKey={0}]",
                   pageCacheKey);

        PageCaches.remove(pageCacheKey);
    }

    /**
     * Clears all page cache.
     *
     * @param request the specified http servlet request
     * @param response the specified http servlet response
     * @throws ActionException action exception
     * @throws IOException io exception
     */
    public void clearAllPageCache(final HttpServletRequest request,
                                  final HttpServletResponse response)
            throws ActionException, IOException {
        final JSONObject ret = new JSONObject();
        if (!userUtils.isAdminLoggedIn()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        PageCaches.removeAll();
    }
}
