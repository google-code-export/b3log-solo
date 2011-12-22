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

import org.b3log.solo.processor.renderer.FrontFreeMarkerRenderer;
import org.b3log.solo.processor.util.Filler;
import org.b3log.latke.util.Requests;
import org.b3log.solo.processor.util.TopBars;
import org.b3log.latke.service.ServiceException;
import org.b3log.solo.service.PreferenceQueryService;
import org.b3log.latke.util.Locales;
import org.b3log.latke.servlet.renderer.freemarker.FreeMarkerRenderer;
import freemarker.template.Template;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.b3log.latke.Latkes;
import org.b3log.latke.service.LangPropsService;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.latke.annotation.RequestProcessing;
import org.b3log.latke.annotation.RequestProcessor;
import org.b3log.latke.model.Pagination;
import org.b3log.latke.servlet.renderer.freemarker.AbstractFreeMarkerRenderer;
import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.HTTPRequestMethod;
import org.b3log.latke.servlet.URIPatternMode;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.Common;
import org.b3log.solo.model.PageTypes;
import org.b3log.solo.util.Skins;
import org.json.JSONObject;
import static org.b3log.latke.action.AbstractCacheablePageAction.*;

/**
 * Index processor.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.1.0.6, Nov 7, 2011
 * @since 0.3.1
 */
@RequestProcessor
public final class IndexProcessor {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(IndexProcessor.class.getName());
    /**
     * Filler.
     */
    private Filler filler = Filler.getInstance();
    /**
     * Preference query service.
     */
    private PreferenceQueryService preferenceQueryService =
            PreferenceQueryService.getInstance();
    /**
     * Skin utilities.
     */
    private Skins skins = Skins.getInstance();
    /**
     * Language service.
     */
    private LangPropsService langPropsService = LangPropsService.getInstance();

    /**
     * Shows index with the specified context.
     * 
     * @param context the specified context
     */
    @RequestProcessing(value = {"/\\d*"}, uriPatternsMode = URIPatternMode.REGEX,
                       method = HTTPRequestMethod.GET)
    public void showIndex(final HTTPRequestContext context) {
        final AbstractFreeMarkerRenderer renderer =
                new FrontFreeMarkerRenderer();
        context.setRenderer(renderer);

        renderer.setTemplateName("index.ftl");
        final Map<String, Object> dataModel = renderer.getDataModel();

        final HttpServletRequest request = context.getRequest();
        final HttpServletResponse response = context.getResponse();
        final String requestURI = request.getRequestURI();
        try {
            final int currentPageNum = getCurrentPageNum(requestURI);
            final JSONObject preference = preferenceQueryService.getPreference();

            skins.fillSkinLangs(preference, dataModel);

            final Map<String, String> langs =
                    langPropsService.getAll(Latkes.getLocale());
            request.setAttribute(CACHED_OID, "No id");
            request.setAttribute(CACHED_TITLE,
                                 langs.get(PageTypes.INDEX_ARTICLES)
                                 + "  [" + langs.get("pageNumLabel") + "="
                                 + currentPageNum + "]");
            request.setAttribute(CACHED_TYPE,
                                 langs.get(PageTypes.INDEX_ARTICLES));
            request.setAttribute(CACHED_LINK, requestURI);

            filler.fillIndexArticles(dataModel, currentPageNum, preference);

            @SuppressWarnings("unchecked")
            final List<JSONObject> articles =
                    (List<JSONObject>) dataModel.get(Article.ARTICLES);
            if (articles.isEmpty()) {
                try {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);

                    return;
                } catch (final IOException ex) {
                    LOGGER.severe(ex.getMessage());
                }
            }

            filler.fillSide(request, dataModel, preference);
            filler.fillBlogHeader(request, dataModel, preference);
            filler.fillBlogFooter(dataModel, preference);

            dataModel.put(Pagination.PAGINATION_CURRENT_PAGE_NUM, currentPageNum);
            final String previousPageNum =
                    Integer.toString(currentPageNum > 1 ? currentPageNum - 1
                                     : 0);
            dataModel.put(Pagination.PAGINATION_PREVIOUS_PAGE_NUM,
                          "0".equals(previousPageNum) ? "" : previousPageNum);
            final Integer pageCount =
                    (Integer) dataModel.get(Pagination.PAGINATION_PAGE_COUNT);
            if (pageCount == currentPageNum + 1) { // The next page is the last page
                dataModel.put(Pagination.PAGINATION_NEXT_PAGE_NUM, "");
            } else {
                dataModel.put(Pagination.PAGINATION_NEXT_PAGE_NUM, currentPageNum
                                                                   + 1);
            }

            dataModel.put(Common.PATH, "");
        } catch (final ServiceException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);

            try {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            } catch (final IOException ex) {
                LOGGER.severe(ex.getMessage());
            }
        }
    }

    /**
     * Handles errors with the specified context.
     * 
     * @param context the specified context
     * @param request the specified request
     * @param response the specified response 
     */
    @RequestProcessing(value = {"/error.do"}, method = HTTPRequestMethod.GET)
    public void handleErrors(final HTTPRequestContext context,
                             final HttpServletRequest request,
                             final HttpServletResponse response) {
        final AbstractFreeMarkerRenderer renderer = new FreeMarkerRenderer();
        context.setRenderer(renderer);

        renderer.setTemplateName("error.ftl");
        final Map<String, Object> dataModel = renderer.getDataModel();

        try {
            final JSONObject preference =
                    preferenceQueryService.getPreference();

            // Adds the top bar HTML content for output
            final String topBarHTML = TopBars.getTopBarHTML(request, response);
            dataModel.put(Common.TOP_BAR_REPLACEMENT_FLAG_KEY,
                          topBarHTML);

            skins.fillSkinLangs(preference, dataModel);

            filler.fillSide(request, dataModel, preference);
            filler.fillBlogHeader(request, dataModel, preference);
            filler.fillBlogFooter(dataModel, preference);
        } catch (final ServiceException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);

            try {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            } catch (final IOException ex) {
                LOGGER.severe(ex.getMessage());
            }
        }
    }

    /**
     * Shows kill browser page with the specified context.
     * 
     * @param context the specified context
     */
    @RequestProcessing(value = {"/kill-browser.html"},
                       method = HTTPRequestMethod.GET)
    public void showKillBrowser(final HTTPRequestContext context) {
        final AbstractFreeMarkerRenderer renderer = new KillBrowserRenderer();
        context.setRenderer(renderer);

        final Map<String, Object> dataModel = renderer.getDataModel();
        final HttpServletRequest request = context.getRequest();
        final HttpServletResponse response = context.getResponse();

        try {
            final Map<String, String> langs =
                    langPropsService.getAll(Locales.getLocale(request));
            dataModel.putAll(langs);
            final JSONObject preference = preferenceQueryService.getPreference();
            filler.fillBlogFooter(dataModel, preference);
            filler.fillMinified(dataModel);

            request.setAttribute(CACHED_OID, "No id");
            request.setAttribute(CACHED_TITLE, "Kill Browser Page");
            request.setAttribute(CACHED_TYPE,
                                 langs.get(PageTypes.KILL_BROWSER_PAGE));
            request.setAttribute(CACHED_LINK, request.getRequestURI());
        } catch (final ServiceException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);

            try {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            } catch (final IOException ex) {
                LOGGER.severe(ex.getMessage());
            }
        }
    }

    /**
     * Gets the request page number from the specified request URI.
     * 
     * @param requestURI the specified request URI
     * @return page number, returns {@code -1} if the specified request URI
     * can not convert to an number
     */
    private static int getCurrentPageNum(final String requestURI) {
        final String pageNumString = requestURI.substring("/".length());

        return Requests.getCurrentPageNum(pageNumString);
    }

    /**
     * Kill browser (kill-browser.ftl) HTTP response renderer.
     * 
     * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
     * @version 1.0.0.0, Sep 18, 2011
     * @see 0.3.1
     */
    private static final class KillBrowserRenderer extends AbstractFreeMarkerRenderer {

        /**
         * Logger.
         */
        private static final Logger LOGGER =
                Logger.getLogger(KillBrowserRenderer.class.getName());

        @Override
        public void render(final HTTPRequestContext context) {
            final HttpServletResponse response = context.getResponse();
            response.setContentType("text/html");
            response.setCharacterEncoding("UTF-8");

            try {
                final Template template =
                        InitProcessor.TEMPLATE_CFG.getTemplate(
                        "kill-browser.ftl");

                final PrintWriter writer = response.getWriter();

                final StringWriter stringWriter = new StringWriter();
                template.setOutputEncoding("UTF-8");
                template.process(getDataModel(), stringWriter);

                final String pageContent = stringWriter.toString();
                context.getRequest().setAttribute(CACHED_CONTENT, pageContent);

                writer.write(pageContent);
                writer.flush();
                writer.close();
            } catch (final Exception e) {
                try {
                    response.sendError(
                            HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    return;
                } catch (final IOException ex) {
                    LOGGER.log(Level.SEVERE, "Can not sned error 500!", ex);
                }
            }
        }

        @Override
        protected void afterRender(final HTTPRequestContext context)
                throws Exception {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        protected void beforeRender(final HTTPRequestContext context) throws
                Exception {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}
