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

import org.b3log.solo.web.processor.renderer.FrontFreeMarkerRenderer;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.service.LangPropsService;
import org.b3log.solo.model.Page;
import org.b3log.solo.util.Pages;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.latke.annotation.RequestProcessing;
import org.b3log.latke.annotation.RequestProcessor;
import org.b3log.solo.web.util.Filler;
import org.b3log.latke.servlet.AbstractFreeMarkerRenderer;
import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.HTTPRequestMethod;
import org.b3log.solo.model.PageTypes;
import org.b3log.solo.util.Preferences;
import org.b3log.solo.util.Skins;
import org.json.JSONObject;
import static org.b3log.latke.action.AbstractCacheablePageAction.*;

/**
 * Page processor.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.1.0.0, Sep 8, 2011
 * @since 0.3.1
 */
@RequestProcessor
public final class PageProcessor {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(PageProcessor.class.getName());
    /**
     * Language service.
     */
    private LangPropsService langPropsService = LangPropsService.getInstance();
    /**
     * Filler.
     */
    private Filler filler = Filler.getInstance();
    /**
     * Page utilities.
     */
    private Pages pageUtils = Pages.getInstance();
    /**
     * Preference utilities.
     */
    private Preferences preferenceUtils = Preferences.getInstance();
    /**
     * Skin utilities.
     */
    private Skins skins = Skins.getInstance();

    /**
     * Shows page with the specified context.
     * 
     * @param context the specified context
     */
    @RequestProcessing(value = {"/page"}, method = HTTPRequestMethod.GET)
    public void showPage(final HTTPRequestContext context) {
        final AbstractFreeMarkerRenderer renderer =
                new FrontFreeMarkerRenderer();
        context.setRenderer(renderer);

        renderer.setTemplateName("page.ftl");
        final Map<String, Object> dataModel = renderer.getDataModel();

        final HttpServletRequest request = context.getRequest();
        final HttpServletResponse response = context.getResponse();

        try {
            final JSONObject preference = preferenceUtils.getPreference();
            if (null == preference) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }

            skins.fillSkinLangs(preference, dataModel);
            final Map<String, String> langs =
                    langPropsService.getAll(Latkes.getLocale());
            request.setAttribute(CACHED_TYPE, langs.get(PageTypes.PAGE));

            final JSONObject page =
                    (JSONObject) request.getAttribute(Page.PAGE);
            if (null == page) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }

            final String pageId = page.getString(Keys.OBJECT_ID);
            request.setAttribute(CACHED_OID, pageId);
            request.setAttribute(CACHED_TITLE,
                                 page.getString(Page.PAGE_TITLE));
            request.setAttribute(CACHED_LINK,
                                 page.getString(Page.PAGE_PERMALINK));

            dataModel.put(Page.PAGE, page);
            final List<JSONObject> comments = pageUtils.getComments(pageId);
            dataModel.put(Page.PAGE_COMMENTS_REF, comments);

            filler.fillSide(dataModel, preference);
            filler.fillBlogHeader(dataModel, preference);
            filler.fillBlogFooter(dataModel, preference);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);

            try {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            } catch (final IOException ex) {
                LOGGER.severe(ex.getMessage());
            }
        }
    }
}
