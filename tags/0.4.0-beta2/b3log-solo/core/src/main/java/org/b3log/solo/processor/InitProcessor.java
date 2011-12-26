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
package org.b3log.solo.processor;

import freemarker.template.Configuration;
import freemarker.template.Template;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.action.AbstractAction;
import org.b3log.latke.annotation.RequestProcessing;
import org.b3log.latke.annotation.RequestProcessor;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.HTTPRequestMethod;
import org.b3log.latke.servlet.renderer.JSONRenderer;
import org.b3log.latke.servlet.renderer.freemarker.AbstractFreeMarkerRenderer;
import org.b3log.latke.util.Locales;
import org.b3log.solo.SoloServletListener;
import org.b3log.solo.model.Common;
import org.b3log.solo.processor.util.Filler;
import org.b3log.solo.service.InitService;
import org.b3log.solo.util.QueryResults;
import org.json.JSONObject;

/**
 * B3log Solo initialization service.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Nov 18, 2011
 * @since 0.4.0
 */
@RequestProcessor
public final class InitProcessor {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(InitProcessor.class.getName());
    /**
     * Initialization service.
     */
    private InitService initService = InitService.getInstance();
    /**
     * FreeMarker configuration.
     */
    public static final Configuration TEMPLATE_CFG;
    /**
     * Filler.
     */
    private Filler filler = Filler.getInstance();
    /**
     * Language service.
     */
    private LangPropsService langPropsService = LangPropsService.getInstance();

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

    /**
     * Shows initialization page.
     * 
     * @param context the specified http request context
     * @param request the specified http servlet request
     * @param response the specified http servlet response
     * @throws Exception exception 
     */
    @RequestProcessing(value = "/init", method = HTTPRequestMethod.GET)
    public void showInit(final HTTPRequestContext context,
                         final HttpServletRequest request,
                         final HttpServletResponse response)
            throws Exception {
        if (SoloServletListener.isInited()) {
            response.sendRedirect("/");

            return;
        }

        final AbstractFreeMarkerRenderer renderer =
                new AbstractFreeMarkerRenderer() {

                    @Override
                    protected Template getTemplate(final String templateDirName,
                                                   final String templateName)
                            throws IOException {
                        return TEMPLATE_CFG.getTemplate(templateName);
                    }

                    @Override
                    protected void beforeRender(final HTTPRequestContext context)
                            throws Exception {
                    }

                    @Override
                    protected void afterRender(final HTTPRequestContext context)
                            throws Exception {
                    }
                };

        renderer.setTemplateName("init.ftl");
        context.setRenderer(renderer);

        final Map<String, Object> dataModel = renderer.getDataModel();

        final Map<String, String> langs =
                langPropsService.getAll(Locales.getLocale(request));
        dataModel.putAll(langs);

        dataModel.put(Common.VERSION, SoloServletListener.VERSION);
        dataModel.put(Common.STATIC_RESOURCE_VERSION,
                      Latkes.getStaticResourceVersion());
        dataModel.put(Common.YEAR,
                      String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));

        filler.fillMinified(dataModel);
    }

    /**
     * Initializes B3log Solo.
     * 
     * @param context the specified http request context
     * @param request the specified http servlet request, for example,
     * <pre>
     * {
     *     "userName": "",
     *     "userEmail": "",
     *     "userPassword": ""
     * }
     * </pre>
     * @param response the specified http servlet response
     * @throws Exception exception 
     */
    @RequestProcessing(value = "/init", method = HTTPRequestMethod.POST)
    public void initB3logSolo(final HTTPRequestContext context,
                              final HttpServletRequest request,
                              final HttpServletResponse response)
            throws Exception {
        if (SoloServletListener.isInited()) {
            response.sendRedirect("/");

            return;
        }

        final JSONRenderer renderer = new JSONRenderer();
        context.setRenderer(renderer);

        final JSONObject ret = new JSONObject();

        try {
            final JSONObject requestJSONObject =
                    AbstractAction.parseRequestJSONObject(request, response);

            initService.init(requestJSONObject, request, response);

            ret.put(Keys.STATUS_CODE, true);

            renderer.setJSONObject(ret);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);

            final JSONObject jsonObject = QueryResults.defaultResult();
            renderer.setJSONObject(jsonObject);
            jsonObject.put(Keys.MSG, e.getMessage());
        }
    }
}
