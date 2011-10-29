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
import org.b3log.latke.service.ServiceException;
import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.HTTPRequestMethod;
import org.b3log.latke.servlet.renderer.JSONRenderer;
import org.b3log.solo.model.Article;
import org.b3log.solo.service.ArticleMgmtService;
import org.b3log.solo.util.QueryResults;
import org.b3log.solo.util.Users;
import org.json.JSONObject;

/**
 * Article console request processing.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Oct 28, 2011
 * @since 0.4.0
 */
@RequestProcessor
public final class ArticleConsole {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(ArticleConsole.class.getName());
    /**
     * Article management service.
     */
    private ArticleMgmtService articleMgmtService =
            ArticleMgmtService.getInstance();
    /**
     * Article URI prefix.
     */
    private static final String ARTICLE_URI_PREFIX = "/console/article/";
    /**
     * Articles URI prefix.
     */
    private static final String ARTICLES_URI_PREFIX = "/console/articles/";
    /**
     * User utilities.
     */
    private Users userUtils = Users.getInstance();
    /**
     * Language service.
     */
    private LangPropsService langPropsService = LangPropsService.getInstance();

    /**
     * Cancels publish an article by the specified request json object.
     *
     * <p>
     * Renders the response with a json object, for example,
     * <pre>
     * {
     *     "sc": boolean,
     *     "msg": ""
     * }
     * </pre>
     * </p>
     *
     * @param context the specified http request context
     * @param request the specified http servlet request
     * @param response the specified http servlet response
     * @throws Exception exception
     */
    @RequestProcessing(value = ARTICLE_URI_PREFIX + "unpublish/*",
                       method = HTTPRequestMethod.PUT)
    public void cancelPublishArticle(final HTTPRequestContext context,
                                     final HttpServletRequest request,
                                     final HttpServletResponse response)
            throws Exception {
        if (!userUtils.isLoggedIn(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        final JSONRenderer renderer = new JSONRenderer();
        context.setRenderer(renderer);

        final JSONObject ret = new JSONObject();
        renderer.setJSONObject(ret);

        try {
            final String articleId =
                    request.getRequestURI().substring((ARTICLE_URI_PREFIX
                                                       + "canceltop/").length());

            if (!userUtils.canAccessArticle(articleId, request)) {
                ret.put(Keys.STATUS_CODE, false);
                ret.put(Keys.MSG, langPropsService.get("forbiddenLabel"));

                return;
            }

            articleMgmtService.cancelPublishArticle(articleId);

            ret.put(Keys.STATUS_CODE, true);
            ret.put(Keys.MSG, langPropsService.get("unPulbishSuccLabel"));
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);

            final JSONObject jsonObject = new JSONObject();
            renderer.setJSONObject(jsonObject);
            jsonObject.put(Keys.STATUS_CODE, false);
            jsonObject.put(Keys.MSG, langPropsService.get("unPulbishFailLabel"));
        }
    }

    /**
     * Cancels an top article by the specified request.
     * 
     * <p>
     * Renders the response with a json object, for example,
     * <pre>
     * {
     *     "sc": boolean,
     *     "msg": ""
     * }
     * </pre>
     * </p>
     *
     * @param context the specified http request context
     * @param request the specified http servlet request
     * @param response the specified http servlet response
     * @throws Exception exception
     */
    @RequestProcessing(value = ARTICLE_URI_PREFIX + "canceltop/*",
                       method = HTTPRequestMethod.PUT)
    public void cancelTopArticle(final HTTPRequestContext context,
                                 final HttpServletRequest request,
                                 final HttpServletResponse response)
            throws Exception {
        if (!userUtils.isLoggedIn(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        final JSONRenderer renderer = new JSONRenderer();
        context.setRenderer(renderer);

        final JSONObject ret = new JSONObject();
        renderer.setJSONObject(ret);

        if (!userUtils.isAdminLoggedIn(request)) {
            ret.put(Keys.MSG, langPropsService.get("forbiddenLabel"));
            ret.put(Keys.STATUS_CODE, false);

            return;
        }

        try {
            final String articleId =
                    request.getRequestURI().substring((ARTICLE_URI_PREFIX
                                                       + "canceltop/").length());
            articleMgmtService.topArticle(articleId, false);

            ret.put(Keys.STATUS_CODE, true);
            ret.put(Keys.MSG, langPropsService.get("cancelTopSuccLabel"));
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);

            final JSONObject jsonObject = new JSONObject();
            renderer.setJSONObject(jsonObject);
            jsonObject.put(Keys.STATUS_CODE, false);
            jsonObject.put(Keys.MSG, langPropsService.get("cancelTopFailLabel"));
        }
    }

    /**
     * Puts an article to top by the specified request.
     * 
     * <p>
     * Renders the response with a json object, for example,
     * <pre>
     * {
     *     "sc": boolean,
     *     "msg": ""
     * }
     * </pre>
     * </p>
     *
     * @param context the specified http request context
     * @param request the specified http servlet request
     * @param response the specified http servlet response
     * @throws Exception exception
     */
    @RequestProcessing(value = ARTICLE_URI_PREFIX + "puttop/*",
                       method = HTTPRequestMethod.PUT)
    public void putTopArticle(final HTTPRequestContext context,
                              final HttpServletRequest request,
                              final HttpServletResponse response)
            throws Exception {
        if (!userUtils.isLoggedIn(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        final JSONRenderer renderer = new JSONRenderer();
        context.setRenderer(renderer);

        final JSONObject ret = new JSONObject();
        renderer.setJSONObject(ret);

        if (!userUtils.isAdminLoggedIn(request)) {
            ret.put(Keys.MSG, langPropsService.get("forbiddenLabel"));
            ret.put(Keys.STATUS_CODE, false);

            return;
        }

        try {
            final String articleId =
                    request.getRequestURI().substring((ARTICLE_URI_PREFIX
                                                       + "puttop/").length());
            articleMgmtService.topArticle(articleId, true);

            ret.put(Keys.STATUS_CODE, true);
            ret.put(Keys.MSG, langPropsService.get("putTopSuccLabel"));
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);

            final JSONObject jsonObject = new JSONObject();
            renderer.setJSONObject(jsonObject);
            jsonObject.put(Keys.STATUS_CODE, false);
            jsonObject.put(Keys.MSG, langPropsService.get("putTopFailLabel"));
        }
    }

    /** 
     * Updates an article by the specified request json object.
     * 
     * <p>
     * Renders the response with a json object, for example,
     * <pre>
     * {
     *     "sc": boolean,
     *     "msg": ""
     * }
     * </pre>
     * </p>
     *
     * @param context the specified http request context
     * @param request the specified http servlet request, for example,
     * <pre>
     * {
     *     "article": {
     *         "oId": "",
     *         "articleTitle": "",
     *         "articleAbstract": "",
     *         "articleContent": "",
     *         "articleTags": "tag1,tag2,tag3",
     *         "articlePermalink": "", // optional
     *         "articleIsPublished": boolean,
     *         "articleSign_oId": "" // optional
     *     }
     * }
     * </pre>
     * @param response the specified http servlet response
     * @throws Exception exception
     */
    @RequestProcessing(value = ARTICLE_URI_PREFIX,
                       method = HTTPRequestMethod.PUT)
    public void updateArticle(final HTTPRequestContext context,
                              final HttpServletRequest request,
                              final HttpServletResponse response)
            throws Exception {
        if (!userUtils.isLoggedIn(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        final JSONRenderer renderer = new JSONRenderer();
        context.setRenderer(renderer);

        final JSONObject ret = new JSONObject();

        try {
            final JSONObject requestJSONObject =
                    AbstractAction.parseRequestJSONObject(request, response);

            final JSONObject article =
                    requestJSONObject.getJSONObject(Article.ARTICLE);
            final String articleId = article.getString(Keys.OBJECT_ID);

            renderer.setJSONObject(ret);

            if (!userUtils.canAccessArticle(articleId, request)) {
                ret.put(Keys.MSG, langPropsService.get("forbiddenLabel"));
                ret.put(Keys.STATUS_CODE, false);

                return;
            }

            articleMgmtService.updateArticle(requestJSONObject, request);

            ret.put(Keys.MSG, langPropsService.get("updateSuccLabel"));
            ret.put(Keys.STATUS_CODE, true);
        } catch (final ServiceException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);

            final JSONObject jsonObject = QueryResults.defaultResult();
            renderer.setJSONObject(jsonObject);
            jsonObject.put(Keys.MSG, e.getMessage());
        }
    }

    /**
     * Adds an article with the specified request.
     * 
     * <p>
     * Renders the response with a json object, for example,
     * <pre>
     * {
     *     "sc": boolean,
     *     "oId": "", // Generated article id
     *     "msg": ""
     * }
     * </pre>
     * </p>
     * 
     * @param request the specified http servlet request, for example,
     * <pre>
     * {
     *     "article": {
     *         "articleTitle": "",
     *         "articleAbstract": "",
     *         "articleContent": "",
     *         "articleTags": "tag1,tag2,tag3",
     *         "articlePermalink": "", // optional
     *         "articleIsPublished": boolean,
     *         "postToCommunity": boolean,
     *         "articleSign_oId": "" // optional
     *     }
     * }
     * </pre>
     * @param response the specified http servlet response
     * @param context the specified http request context
     * @throws Exception exception
     */
    @RequestProcessing(value = ARTICLE_URI_PREFIX,
                       method = HTTPRequestMethod.POST)
    public void addArticle(final HttpServletRequest request,
                           final HttpServletResponse response,
                           final HTTPRequestContext context)
            throws Exception {
        if (!userUtils.isLoggedIn(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        final JSONRenderer renderer = new JSONRenderer();
        context.setRenderer(renderer);

        final JSONObject ret = new JSONObject();

        try {
            final JSONObject requestJSONObject =
                    AbstractAction.parseRequestJSONObject(request, response);

            final String articleId = articleMgmtService.addArticle(
                    requestJSONObject, request);

            ret.put(Keys.OBJECT_ID, articleId);
            ret.put(Keys.MSG, langPropsService.get("addSuccLabel"));
            ret.put(Keys.STATUS_CODE, true);

            renderer.setJSONObject(ret);
        } catch (final ServiceException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);

            final JSONObject jsonObject = QueryResults.defaultResult();
            renderer.setJSONObject(jsonObject);
            jsonObject.put(Keys.MSG, e.getMessage());
        }
    }
}
