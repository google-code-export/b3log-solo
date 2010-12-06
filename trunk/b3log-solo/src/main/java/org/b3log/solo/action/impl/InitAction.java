/*
 * Copyright (c) 2009, 2010, B3log Team
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
import freemarker.template.Configuration;
import freemarker.template.Template;
import java.io.File;
import java.io.IOException;
import org.b3log.latke.action.ActionException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.latke.action.AbstractAction;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.service.ServiceException;
import org.b3log.latke.util.Locales;
import org.b3log.solo.SoloServletListener;
import org.json.JSONObject;

/**
 * B3log Solo initialization action. init.ftl.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.2, Dec 6, 2010
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
    private Configuration configuration;
    /**
     * Language service.
     */
    @Inject
    private LangPropsService langPropsService;

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

        try {
            if (SoloServletListener.isInited()) {
                final RequestDispatcher requestDispatcher =
                        request.getRequestDispatcher("/");
                requestDispatcher.forward(request, response);
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
    public void init() throws ServletException {
        configuration = new Configuration();
        configuration.setDefaultEncoding("UTF-8");
        try {
            final String webRootPath = SoloServletListener.getWebRoot();

            configuration.setDirectoryForTemplateLoading(new File(webRootPath));
        } catch (final IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    @Override
    protected Template beforeDoFreeMarkerAction(
            final HttpServletRequest request, final HttpServletResponse response)
            throws ActionException {
        final String pageName = getPageName(request.getRequestURI());

        try {
            return configuration.getTemplate(pageName);
        } catch (final IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new ActionException(e);
        }
    }
}
