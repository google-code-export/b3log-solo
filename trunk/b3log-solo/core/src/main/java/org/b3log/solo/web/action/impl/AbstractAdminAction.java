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
package org.b3log.solo.web.action.impl;

import freemarker.template.Template;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.latke.Latkes;
import org.b3log.latke.action.AbstractAction;
import org.b3log.latke.action.ActionException;
import org.b3log.latke.service.LangPropsService;
import org.b3log.solo.model.Preference;
import org.b3log.solo.service.PreferenceQueryService;
import org.b3log.solo.web.processor.InitProcessor;
import org.json.JSONObject;

/**
 * Abstract admin action.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.7, Aug 18, 2011
 */
public abstract class AbstractAdminAction extends AbstractAction {

    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(AbstractAdminAction.class.getName());
    /**
     * Language service.
     */
    private LangPropsService langPropsService = LangPropsService.getInstance();
    /**
     * Preference query service.
     */
    private PreferenceQueryService preferenceQueryService =
            PreferenceQueryService.getInstance();

    @Override
    protected Map<?, ?> doFreeMarkerAction(
            final freemarker.template.Template template,
            final HttpServletRequest request,
            final HttpServletResponse response) throws ActionException {
        final Map<String, Object> ret = new HashMap<String, Object>();

        try {
            final JSONObject preference = preferenceQueryService.getPreference();
            if (null == preference) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return ret;
            }

            final Locale locale = Latkes.getLocale();
            final Map<String, String> langs =
                    langPropsService.getAll(locale);
            ret.putAll(langs);

            ret.put(Preference.LOCALE_STRING, locale.toString());

            // For admin-preference.ftl only
            final StringBuilder timeZoneIdOptions = new StringBuilder();
            final String[] availableIDs = TimeZone.getAvailableIDs();
            for (int i = 0; i < availableIDs.length; i++) {
                final String id = availableIDs[i];
                String option = null;
                if (id.equals(preference.getString(Preference.TIME_ZONE_ID))) {
                    option = "<option value=\"" + id + "\" selected=\"true\">"
                             + id + "</option>";
                } else {
                    option = "<option value=\"" + id + "\">" + id + "</option>";
                }
                timeZoneIdOptions.append(option);
            }
            ret.put("timeZoneIdOptions", timeZoneIdOptions.toString());
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new ActionException("Language model fill error");
        }

        return ret;
    }

    @Override
    protected JSONObject doAjaxAction(final JSONObject data,
                                      final HttpServletRequest request,
                                      final HttpServletResponse response)
            throws ActionException {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Template getTemplate(final HttpServletRequest request) {
        final String pageName = getTemplateName(request.getRequestURI());

        try {
            return InitProcessor.TEMPLATE_CFG.getTemplate(pageName);
        } catch (final IOException e) {
            LOGGER.log(Level.SEVERE, "Can't find template by the specified request[URI="
                                     + request.getRequestURI() + "]",
                       e.getMessage());
            return null;
        }
    }
}
