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

import freemarker.template.Template;
import java.io.IOException;
import org.b3log.latke.action.ActionException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.util.Locales;
import org.b3log.solo.action.util.Filler;
import org.b3log.solo.model.PageTypes;
import org.b3log.solo.util.Preferences;
import org.json.JSONObject;

/**
 * Kill browser action. kill-browser.ftl.
 *
 * @author <a href="mailto:LLY2190@gmail.com">Liyuan Li</a>
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Aug 18, 2011
 */
public final class KillBrowserAction extends AbstractFrontPageAction {

    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(KillBrowserAction.class.getName());
    /**
     * Language service.
     */
    private LangPropsService langPropsService = LangPropsService.getInstance();
    /**
     * Filler.
     */
    private Filler filler = Filler.getInstance();
    /**
     * Preference utilities.
     */
    private Preferences preferenceUtils = Preferences.getInstance();

    @Override
    protected Map<?, ?> doFreeMarkerAction(
            final freemarker.template.Template template,
            final HttpServletRequest request,
            final HttpServletResponse response) throws ActionException {
        final Map<String, Object> ret = new HashMap<String, Object>();

        try {
            final Map<String, String> langs =
                    langPropsService.getAll(Locales.getLocale(request));
            ret.putAll(langs);
            final JSONObject preference = preferenceUtils.getPreference();
            filler.fillBlogFooter(ret, preference);
            filler.fillMinified(ret);

            request.setAttribute(CACHED_OID, "No id");
            request.setAttribute(CACHED_TITLE, "Kill Browser Page");
            request.setAttribute(CACHED_TYPE,
                                 langs.get(PageTypes.KILL_BROWSER_PAGE));
            request.setAttribute(CACHED_LINK, request.getRequestURI());
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new ActionException(e);
        }

        return ret;
    }

    @Override
    protected JSONObject doAjaxAction(final JSONObject data,
                                      final HttpServletRequest request,
                                      final HttpServletResponse response)
            throws ActionException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected Template getTemplate(final HttpServletRequest request) {
        try {
            return InitAction.TEMPLATE_CFG.getTemplate("kill-browser.ftl");
        } catch (final IOException e) {
            LOGGER.log(Level.SEVERE, "Can't find template by the specified request[URI="
                                     + request.getRequestURI() + "]",
                       e.getMessage());
            return null;
        }
    }
}
