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
package org.b3log.solo.web.processor;

import java.io.IOException;
import java.util.logging.Level;
import org.b3log.solo.util.Users;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.latke.action.ActionException;
import org.b3log.latke.action.util.PageCaches;
import org.b3log.latke.annotation.RequestProcessing;
import org.b3log.latke.annotation.RequestProcessor;
import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.HTTPRequestMethod;
import org.b3log.latke.util.Strings;
import org.b3log.solo.model.Common;

/**
 * Cache processor.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.1.0.0, Sep 11, 2011
 * @since 0.3.1
 */
@RequestProcessor
public final class CacheProcessor {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(CacheProcessor.class.getName());
    /**
     * User utilities.
     */
    private Users userUtils = Users.getInstance();

    /**
     * Clears cache with the specified context.
     * 
     * @param context the specified context
     */
    @RequestProcessing(value = {"/clear-cache.do"},
                       method = HTTPRequestMethod.POST)
    public void clearCache(final HTTPRequestContext context) {
        final HttpServletRequest httpServletRequest = context.getRequest();
        final HttpServletResponse httpServletResponse = context.getResponse();

        final String all = httpServletRequest.getParameter("all");
        try {
            if (Strings.isEmptyOrNull(all)) { // Just clears single page cache
                final String uri = httpServletRequest.getParameter(Common.URI);
                clearPageCache(uri, httpServletRequest, httpServletResponse);
            } else { // Clears all page caches
                clearAllPageCache(httpServletRequest, httpServletResponse);
            }
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
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
    private void clearPageCache(final String uri,
                               final HttpServletRequest request,
                               final HttpServletResponse response)
            throws ActionException, IOException {
        if (!userUtils.isAdminLoggedIn()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        final String pageCacheKey = PageCaches.getPageCacheKey(uri, null);
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
    private void clearAllPageCache(final HttpServletRequest request,
                                  final HttpServletResponse response)
            throws ActionException, IOException {
        if (!userUtils.isAdminLoggedIn()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        PageCaches.removeAll();
    }
}
