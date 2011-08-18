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

import freemarker.template.Configuration;
import java.util.Calendar;
import org.b3log.latke.action.ActionException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.latke.action.AbstractAction;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.service.ServiceException;
import org.b3log.latke.util.Locales;
import org.b3log.solo.SoloServletListener;
import org.b3log.solo.model.Common;
import org.json.JSONObject;

/**
 * B3log Solo initialization action. kill-browser.ftl.
 *
 * @author <a href="mailto:LLY2190@gmail.com">Liyuan Li</a>
 * @version 1.0.0.1, Aug 18, 2011
 */
public final class KillBrowserAction extends AbstractAction {

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
     * FreeMarker configuration.
     */
    private Configuration configuration;
    /**
     * Language service.
     */
    private LangPropsService langPropsService = LangPropsService.getInstance();

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
        } catch (final ServiceException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

        ret.put(Common.VERSION, SoloServletListener.VERSION);
        ret.put(Common.YEAR,
                String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));

        return ret;
    }

    @Override
    protected JSONObject doAjaxAction(final JSONObject data,
            final HttpServletRequest request,
            final HttpServletResponse response)
            throws ActionException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    /**
     * Returns "kill-browser.ftl".
     * 
     * <p>
     * Ignores the specified request URI
     * </p>
     * 
     * @param requestURI the specified request URI
     * @return "kill-browser.ftl"
     */
    @Override
    protected String getTemplateName(final String requestURI) {
        return "kill-browser.ftl";
    }
}
