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

import java.io.IOException;
import org.b3log.latke.action.ActionException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.latke.util.Strings;
import org.b3log.solo.model.ErrorPage;
import org.json.JSONObject;

/**
 * Admin console error action. admin-error.ftl.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Dec 4, 2010
 */
public final class AdminErrorAction extends AbstractAdminAction {

    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(AdminErrorAction.class.getName());

    @Override
    protected Map<?, ?> doFreeMarkerAction(
            final freemarker.template.Template template,
            final HttpServletRequest request,
            final HttpServletResponse response) throws ActionException {
        final Map<String, Object> ret = new HashMap<String, Object>();

        try {
            String requestURI =
                    request.getParameter(ErrorPage.ERROR_PAGE_REQUEST_URI);
            if (Strings.isEmptyOrNull(requestURI)) {
                requestURI = (String) request.getAttribute(
                        ErrorPage.ERROR_PAGE_REQUEST_URI);
                if (Strings.isEmptyOrNull(requestURI)) {
                    requestURI = "Unknown";
                }
            }
            ret.put(ErrorPage.ERROR_PAGE_REQUEST_URI, requestURI);

            String cause = request.getParameter(ErrorPage.ERROR_PAGE_CAUSE);
            if (Strings.isEmptyOrNull(cause)) {
                cause = (String) request.getAttribute(
                        ErrorPage.ERROR_PAGE_CAUSE);
                if (Strings.isEmptyOrNull(cause)) {
                    cause = "Unknown";
                }
            }
            ret.put(ErrorPage.ERROR_PAGE_CAUSE, cause);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new ActionException(e);
        }

        return ret;
    }

    @Override
    protected void doPost(final HttpServletRequest request,
                          final HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    @Override
    protected JSONObject doAjaxAction(final JSONObject data,
                                      final HttpServletRequest request,
                                      final HttpServletResponse response)
            throws ActionException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
