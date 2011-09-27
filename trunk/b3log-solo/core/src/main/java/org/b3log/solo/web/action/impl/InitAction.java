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

import freemarker.template.Configuration;
import freemarker.template.Template;
import java.io.File;
import java.io.IOException;
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
import org.b3log.latke.util.Locales;
import org.b3log.solo.SoloServletListener;
import org.b3log.solo.web.util.Filler;
import org.b3log.solo.model.Common;
import org.json.JSONObject;

/**
 * B3log Solo initialization action. init.ftl.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.9, Sep 27, 2011
 * @since 0.3.1
 */
public final class InitAction extends AbstractAction {

    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(InitAction.class.getName());
    /**
     * FreeMarker configuration.
     */
    public static final Configuration TEMPLATE_CFG;
    /**
     * Language service.
     */
    private LangPropsService langPropsService = LangPropsService.getInstance();
    /**
     * Filler.
     */
    private Filler filler = Filler.getInstance();

    static {
        TEMPLATE_CFG = new Configuration();
        TEMPLATE_CFG.setDefaultEncoding("UTF-8");
        try {
            final String webRootPath = SoloServletListener.getWebRoot();

            TEMPLATE_CFG.setDirectoryForTemplateLoading(new File(webRootPath));
        } catch (final IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @Override
    protected Map<?, ?> doFreeMarkerAction(
            final freemarker.template.Template template,
            final HttpServletRequest request,
            final HttpServletResponse response) throws ActionException {
        final Map<String, Object> ret = new HashMap<String, Object>();
        final Map<String, String> langs =
                langPropsService.getAll(Locales.getLocale(request));
        ret.putAll(langs);

        ret.put(Common.VERSION, SoloServletListener.VERSION);
        ret.put(Common.YEAR,
                String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
        filler.fillMinified(ret);

        try {
            if (SoloServletListener.isInited()) {
                response.sendRedirect("/init-admin");
                return ret;
            }
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
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
        final String pageName = getTemplateName(request.getRequestURI());

        try {
            return TEMPLATE_CFG.getTemplate(pageName);
        } catch (final IOException e) {
            LOGGER.log(Level.SEVERE, "Can't find template by the specified request[URI="
                                     + request.getRequestURI() + "]",
                       e.getMessage());
            return null;
        }
    }
}
