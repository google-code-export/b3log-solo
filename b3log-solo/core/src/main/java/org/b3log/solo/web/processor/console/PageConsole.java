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
package org.b3log.solo.web.processor.console;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.latke.Keys;
import org.b3log.latke.action.AbstractAction;
import org.b3log.latke.annotation.RequestProcessing;
import org.b3log.latke.annotation.RequestProcessor;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.HTTPRequestMethod;
import org.b3log.latke.servlet.renderer.JSONRenderer;
import org.b3log.solo.model.Page;
import org.b3log.solo.service.PageMgmtService;
import org.b3log.solo.service.PageQueryService;
import org.b3log.solo.util.QueryResults;
import org.b3log.solo.util.Users;
import org.b3log.solo.web.util.Requests;
import org.json.JSONObject;

/**
 * Plugin console request processing.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Oct 27, 2011
 * @since 0.4.0
 */
@RequestProcessor
public final class PageConsole {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(PageConsole.class.getName());
    /**
     * User utilities.
     */
    private Users userUtils = Users.getInstance();
    /**
     * Page query service.
     */
    private PageQueryService pageQueryService = PageQueryService.getInstance();
    /**
     * Page management service.
     */
    private PageMgmtService pageMgmtService = PageMgmtService.getInstance();
    /**
     * Pages URI prefix.
     */
    private static final String PAGES_URI_PREFIX = "/console/pages/";
    /**
     * Page URI prefix.
     */
    private static final String PAGE_URI_PREFIX = "/console/page/";
    /**
     * Page order URI prefix.
     */
    private static final String PAGE_ORDER_URI_PREFIX = PAGE_URI_PREFIX
                                                        + "order/";
    /**
     * Language service.
     */
    private LangPropsService langPropsService = LangPropsService.getInstance();

    /**
     * Changes a page order by the specified page id and order.
     * 
     * <p>
     * Renders the response with a json object, for example,
     * <pre>
     * {
     *     "sc": boolean,
     *     "msg": ""
     * }
     * </pre>
     *
     * @param request the specified http servlet request, for example,
     * <pre>
     * {
     *     "oId": "",
     *     "pageOrder": int // the target order
     * }
     * </pre>
     * @param response the specified http servlet response
     * @param context the specified http request context
     * @throws Exception exception 
     */
    @RequestProcessing(value = PAGE_ORDER_URI_PREFIX,
                       method = HTTPRequestMethod.PUT)
    public void changeOrder(final HttpServletRequest request,
                            final HttpServletResponse response,
                            final HTTPRequestContext context)
            throws Exception {
        if (!userUtils.isAdminLoggedIn(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        final JSONRenderer renderer = new JSONRenderer();
        context.setRenderer(renderer);

        final JSONObject ret = new JSONObject();

        try {
            final JSONObject requestJSONObject =
                    AbstractAction.parseRequestJSONObject(request, response);
            final String linkId =
                    requestJSONObject.getString(Keys.OBJECT_ID);
            final int targetLinkOrder =
                    requestJSONObject.getInt(Page.PAGE_ORDER);

            pageMgmtService.changeOrder(linkId, targetLinkOrder);

            ret.put(Keys.STATUS_CODE, true);
            ret.put(Keys.MSG, langPropsService.get("updateSuccLabel"));

            renderer.setJSONObject(ret);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);

            final JSONObject jsonObject = QueryResults.defaultResult();
            renderer.setJSONObject(jsonObject);
            jsonObject.put(Keys.MSG, langPropsService.get("updateFailLabel"));
        }
    }

    /**
     * Gets a page by the specified request json object.
     * 
     * <p>
     * Renders the response with a json object, for example,
     * <pre>
     * {
     *     "sc": boolean
     *     "page": {
     *         "oId": "",
     *         "pageTitle": "",
     *         "pageContent": ""
     *         "pageOrder": int,
     *         "pagePermalink": "",
     *         "pageCommentCount": int,
     *     }
     * }
     * </pre>
     * </p>
     *
     * @param request the specified http servlet request
     * @param response the specified http servlet response
     * @param context the specified http request context
     * @throws Exception exception
     */
    @RequestProcessing(value = PAGE_URI_PREFIX + "*",
                       method = HTTPRequestMethod.GET)
    public void getPage(final HttpServletRequest request,
                        final HttpServletResponse response,
                        final HTTPRequestContext context)
            throws Exception {
        if (!userUtils.isLoggedIn(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        final JSONRenderer renderer = new JSONRenderer();
        context.setRenderer(renderer);

        try {
            final String requestURI = request.getRequestURI();
            final String pageId =
                    requestURI.substring(PAGE_URI_PREFIX.length());

            final JSONObject result = pageQueryService.getPage(pageId);

            if (null == result) {
                renderer.setJSONObject(QueryResults.defaultResult());

                return;
            }

            renderer.setJSONObject(result);
            result.put(Keys.STATUS_CODE, true);
            result.put(Keys.MSG, langPropsService.get("getSuccLabel"));
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);

            final JSONObject jsonObject = QueryResults.defaultResult();
            renderer.setJSONObject(jsonObject);
            jsonObject.put(Keys.MSG, langPropsService.get("getFailLabel"));
        }
    }

    /**
     * Gets pages by the specified request json object.
     * 
     * <p>
     * Renders the response with a json object, for example,
     * <pre>
     * {
     *     "pagination": {
     *         "paginationPageCount": 100,
     *         "paginationPageNums": [1, 2, 3, 4, 5]
     *     },
     *     "pages": [{
     *         "oId": "",
     *         "pageTitle": "",
     *         "pageCommentCount": int,
     *         "pageOrder": int,
     *         "pagePermalink": ""
     *      }, ....]
     *     "sc": "GET_PAGES_SUCC"
     * }
     * </pre>
     *
     * @param request the specified http servlet request
     * @param response the specified http servlet response
     * @param context the specified http request context
     * @throws Exception exception
     * @see Requests#PAGINATION_PATH_PATTERN
     */
    @RequestProcessing(value = PAGES_URI_PREFIX
                               + Requests.PAGINATION_PATH_PATTERN,
                       method = HTTPRequestMethod.GET)
    public void getPages(final HttpServletRequest request,
                         final HttpServletResponse response,
                         final HTTPRequestContext context)
            throws Exception {
        if (!userUtils.isLoggedIn(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        final JSONRenderer renderer = new JSONRenderer();
        context.setRenderer(renderer);

        try {
            final String requestURI = request.getRequestURI();
            final String path =
                    requestURI.substring(PAGES_URI_PREFIX.length());

            final JSONObject requestJSONObject =
                    Requests.buildPaginationRequest(path);

            final JSONObject result = pageQueryService.getPages(
                    requestJSONObject);
            result.put(Keys.STATUS_CODE, true);

            renderer.setJSONObject(result);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);

            final JSONObject jsonObject = QueryResults.defaultResult();
            renderer.setJSONObject(jsonObject);
            jsonObject.put(Keys.MSG, langPropsService.get("getFailLabel"));
        }
    }
}
