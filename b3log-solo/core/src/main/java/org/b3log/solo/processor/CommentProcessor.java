/*
 * Copyright (c) 2009, 2010, 2011, 2012, B3log Team
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

import java.io.IOException;
import java.util.logging.Level;
import org.b3log.solo.repository.CommentRepository;
import org.b3log.solo.repository.PageRepository;
import org.b3log.solo.repository.impl.CommentRepositoryImpl;
import org.b3log.solo.repository.impl.PageRepositoryImpl;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.b3log.latke.Keys;
import org.b3log.latke.action.AbstractAction;
import org.b3log.latke.annotation.RequestProcessing;
import org.b3log.latke.annotation.RequestProcessor;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.HTTPRequestMethod;
import org.b3log.latke.servlet.renderer.JSONRenderer;
import org.b3log.solo.repository.ArticleRepository;
import org.b3log.solo.repository.impl.ArticleRepositoryImpl;
import org.b3log.solo.service.CommentMgmtService;
import org.b3log.solo.service.PreferenceQueryService;
import org.b3log.solo.util.Comments;
import org.json.JSONObject;

/**
 * Comment processor.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.1.0.5, Feb 3, 2012
 * @since 0.3.1
 */
@RequestProcessor
public final class CommentProcessor {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(CommentProcessor.class.getName());
    /**
     * Language service.
     */
    private static LangPropsService langPropsService =
            LangPropsService.getInstance();
    /**
     * Page repository.
     */
    private PageRepository pageRepository = PageRepositoryImpl.getInstance();
    /**
     * Article repository.
     */
    private static ArticleRepository articleRepository =
            ArticleRepositoryImpl.getInstance();
    /**
     * Comment repository.
     */
    private static CommentRepository commentRepository =
            CommentRepositoryImpl.getInstance();
    /**
     * Preference query service.
     */
    private static PreferenceQueryService preferenceQueryService =
            PreferenceQueryService.getInstance();
    /**
     * Comment management service.
     */
    private CommentMgmtService commentMgmtService =
            CommentMgmtService.getInstance();

    /**
     * Adds a comment to a page.
     * 
     * <p>
     * Renders the response with a json object, for example,
     * <pre>
     * {
     *     "oId": generatedCommentId,
     *     "sc": "COMMENT_PAGE_SUCC"
     *     "commentDate": "", // yyyy/MM/dd hh:mm:ss
     *     "commentSharpURL": "",
     *     "commentThumbnailURL": "",
     *     "commentOriginalCommentName": "" // if exists this key, the comment is an reply
     * }
     * </pre>
     * </p>
     *
     * @param context the specified context, 
     * including a request json object, for example,
     * <pre>
     * {
     *     "captcha": "",
     *     "oId": pageId,
     *     "commentName": "",
     *     "commentEmail": "",
     *     "commentURL": "",
     *     "commentContent": "",
     *     "commentOriginalCommentId": "" // optional, if exists this key, the comment
     *                                    // is an reply
     * }
     * </pre>
     * @throws ServletException servlet exception
     * @throws IOException io exception
     */
    @RequestProcessing(value = {"/add-page-comment.do"},
                       method = HTTPRequestMethod.POST)
    public void addPageComment(final HTTPRequestContext context)
            throws ServletException, IOException {
        final HttpServletRequest httpServletRequest = context.getRequest();
        final HttpServletResponse httpServletResponse = context.getResponse();

        final JSONObject requestJSONObject =
                AbstractAction.parseRequestJSONObject(httpServletRequest,
                                                      httpServletResponse);

        final JSONObject jsonObject =
                Comments.checkAddCommentRequest(requestJSONObject);

        final JSONRenderer renderer = new JSONRenderer();
        context.setRenderer(renderer);
        renderer.setJSONObject(jsonObject);

        if (!jsonObject.optBoolean(Keys.STATUS_CODE)) {
            LOGGER.log(Level.WARNING, "Can't add comment[msg={0}]", jsonObject.
                    optString(Keys.MSG));
            return;
        }

        final String captcha = requestJSONObject.optString(
                CaptchaProcessor.CAPTCHA);
        final HttpSession session = httpServletRequest.getSession();
        final String storedCaptcha = (String) session.getAttribute(
                CaptchaProcessor.CAPTCHA);
        if (null == storedCaptcha || !storedCaptcha.equals(captcha)) {
            jsonObject.put(Keys.STATUS_CODE, false);
            jsonObject.put(Keys.MSG, langPropsService.get(
                    "captchaErrorLabel"));

            return;
        }

        session.removeAttribute(CaptchaProcessor.CAPTCHA);

        try {
            final JSONObject addResult =
                    commentMgmtService.addPageComment(requestJSONObject);
            addResult.put(Keys.STATUS_CODE, true);

            renderer.setJSONObject(addResult);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, "Can not add comment on page", e);

            jsonObject.put(Keys.STATUS_CODE, false);
            jsonObject.put(Keys.MSG, langPropsService.get("addFailLabel"));
        }
    }

    /**
     * Adds a comment to an article.
     *
     * 
     * <p>
     * Renders the response with a json object, for example,
     * <pre>
     * {
     *     "oId": generatedCommentId,
     *     "sc": "COMMENT_ARTICLE_SUCC",
     *     "commentDate": "", // yyyy/MM/dd hh:mm:ss
     *     "commentSharpURL": "",
     *     "commentThumbnailURL": "",
     *     "commentOriginalCommentName": "" // if exists this key, the comment is an reply
     * }
     * </pre>
     * 
     * @param context the specified context, 
     * including a request json object, for example,
     * <pre>
     * {
     *     "captcha": "",
     *     "oId": articleId,
     *     "commentName": "",
     *     "commentEmail": "",
     *     "commentURL": "",
     *     "commentContent": "",
     *     "commentOriginalCommentId": "" // optional, if exists this key, the comment
     *                                    // is an reply
     * }
     * </pre>
     * @throws ServletException servlet exception
     * @throws IOException io exception 
     */
    @RequestProcessing(value = {"/add-article-comment.do"},
                       method = HTTPRequestMethod.POST)
    public void addArticleComment(final HTTPRequestContext context)
            throws ServletException, IOException {
        final HttpServletRequest httpServletRequest = context.getRequest();
        final HttpServletResponse httpServletResponse = context.getResponse();

        final JSONObject requestJSONObject =
                AbstractAction.parseRequestJSONObject(httpServletRequest,
                                                      httpServletResponse);

        final JSONObject jsonObject =
                Comments.checkAddCommentRequest(requestJSONObject);

        final JSONRenderer renderer = new JSONRenderer();
        context.setRenderer(renderer);
        renderer.setJSONObject(jsonObject);

        if (!jsonObject.optBoolean(Keys.STATUS_CODE)) {
            LOGGER.log(Level.WARNING, "Can't add comment[msg={0}]", jsonObject.
                    optString(Keys.MSG));
            return;
        }

        final String captcha =
                requestJSONObject.optString(CaptchaProcessor.CAPTCHA);
        final HttpSession session = httpServletRequest.getSession();
        final String storedCaptcha =
                (String) session.getAttribute(CaptchaProcessor.CAPTCHA);
        if (null == storedCaptcha || !storedCaptcha.equals(captcha)) {
            jsonObject.put(Keys.STATUS_CODE, false);
            jsonObject.put(Keys.MSG, langPropsService.get(
                    "captchaErrorLabel"));

            return;
        }

        session.removeAttribute(CaptchaProcessor.CAPTCHA);

        try {
            final JSONObject addResult =
                    commentMgmtService.addArticleComment(requestJSONObject);

            addResult.put(Keys.STATUS_CODE, true);
            renderer.setJSONObject(addResult);
        } catch (final Exception e) {

            LOGGER.log(Level.SEVERE, "Can not add comment on article", e);
            jsonObject.put(Keys.STATUS_CODE, false);
            jsonObject.put(Keys.MSG, langPropsService.get("addFailLabel"));
        }
    }
}
