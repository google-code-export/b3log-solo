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
import java.util.Date;
import java.util.logging.Level;
import org.b3log.solo.repository.CommentRepository;
import org.b3log.solo.repository.PageRepository;
import org.b3log.solo.repository.impl.CommentRepositoryImpl;
import org.b3log.solo.repository.impl.PageRepositoryImpl;
import org.b3log.solo.util.Statistics;
import org.b3log.solo.util.TimeZones;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang.StringEscapeUtils;
import org.b3log.latke.Keys;
import org.b3log.latke.action.AbstractAction;
import org.b3log.latke.annotation.RequestProcessing;
import org.b3log.latke.annotation.RequestProcessor;
import org.b3log.latke.event.Event;
import org.b3log.latke.event.EventManager;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.HTTPRequestMethod;
import org.b3log.latke.servlet.renderer.JSONRenderer;
import org.b3log.latke.util.Ids;
import org.b3log.latke.util.Strings;
import org.b3log.solo.SoloServletListener;
import org.b3log.solo.event.EventTypes;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.Comment;
import org.b3log.solo.model.Preference;
import org.b3log.solo.repository.ArticleRepository;
import org.b3log.solo.repository.impl.ArticleRepositoryImpl;
import org.b3log.solo.service.CommentMgmtService;
import org.b3log.solo.service.PreferenceQueryService;
import org.b3log.solo.util.Articles;
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
     * Article utilities.
     */
    private static Articles articleUtils = Articles.getInstance();
    /**
     * Statistic utilities.
     */
    private static Statistics statistics = Statistics.getInstance();
    /**
     * Event manager.
     */
    private static EventManager eventManager = EventManager.getInstance();

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
    // TODO: encap txn
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
            final JSONObject ret =
                    commentMgmtService.addPageComment(requestJSONObject);

            final String commentId = ret.getString(Keys.OBJECT_ID);
            jsonObject.put(Keys.OBJECT_ID, commentId);
            
            final String commentDate = ret.getString(Comment.COMMENT_DATE);
            jsonObject.put(Comment.COMMENT_DATE, commentDate);
            
            final String commentOriginalCommentName = 
                    ret.optString(Comment.COMMENT_ORIGINAL_COMMENT_NAME);
            if (!Strings.isEmptyOrNull(commentOriginalCommentName)) {
                jsonObject.put(Comment.COMMENT_ORIGINAL_COMMENT_NAME, 
                        commentOriginalCommentName);
            }
            
            final String commentThumbnailURL = 
                    ret.getString(Comment.COMMENT_THUMBNAIL_URL);
            jsonObject.put(Comment.COMMENT_THUMBNAIL_URL, commentThumbnailURL);
            
            final String commentSharpURL = 
                    ret.getString(Comment.COMMENT_SHARP_URL);
            jsonObject.put(Comment.COMMENT_SHARP_URL, commentSharpURL);

            jsonObject.put(Keys.STATUS_CODE, true);
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
        // TODO: add article comment args check
        final HttpServletRequest httpServletRequest = context.getRequest();
        final HttpServletResponse httpServletResponse = context.getResponse();

        final JSONObject requestJSONObject =
                AbstractAction.parseRequestJSONObject(httpServletRequest,
                                                      httpServletResponse);

        JSONObject jsonObject =
                Comments.checkAddCommentRequest(requestJSONObject);

        final JSONRenderer renderer = new JSONRenderer();
        context.setRenderer(renderer);
        renderer.setJSONObject(jsonObject);

        if (!jsonObject.optBoolean(Keys.STATUS_CODE)) {
            LOGGER.log(Level.WARNING, "Can't add comment[msg={0}]", jsonObject.
                    optString(Keys.MSG));
            return;
        }

        final Transaction transaction = commentRepository.beginTransaction();
        try {
            jsonObject =
                    addArticleCommentInternal(requestJSONObject,
                                              httpServletRequest);

            transaction.commit();

            context.setRenderer(renderer);
            renderer.setJSONObject(jsonObject);
        } catch (final Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }

            LOGGER.log(Level.SEVERE, "Can not add comment on article", e);
            jsonObject.put(Keys.STATUS_CODE, false);
            jsonObject.put(Keys.MSG, langPropsService.get("addFailLabel"));
        }
    }

    /**
     * Adds an article comment for internal calls.
     * 
     * @param requestJSONObject the specified request json object
     * @param request the specified request
     * @return result
     * @throws Exception exception
     */
    public static JSONObject addArticleCommentInternal(
            final JSONObject requestJSONObject,
            final HttpServletRequest request) throws Exception {
        final JSONObject ret = new JSONObject();

        String articleId;

        final String captcha =
                requestJSONObject.getString(CaptchaProcessor.CAPTCHA);
        final HttpSession session = request.getSession();
        final String storedCaptcha =
                (String) session.getAttribute(CaptchaProcessor.CAPTCHA);
        if (null == storedCaptcha || !storedCaptcha.equals(captcha)) {
            ret.put(Keys.STATUS_CODE, false);
            ret.put(Keys.MSG, langPropsService.get(
                    "captchaErrorLabel"));

            return ret;
        }

        session.removeAttribute(CaptchaProcessor.CAPTCHA);

        articleId = requestJSONObject.getString(Keys.OBJECT_ID);
        final JSONObject article = articleRepository.get(articleId);
        final String commentName =
                requestJSONObject.getString(Comment.COMMENT_NAME);
        final String commentEmail =
                requestJSONObject.getString(Comment.COMMENT_EMAIL).trim().
                toLowerCase();
        final String commentURL =
                requestJSONObject.optString(Comment.COMMENT_URL);
        String commentContent =
                requestJSONObject.getString(Comment.COMMENT_CONTENT).
                replaceAll("\\n", SoloServletListener.ENTER_ESC);
        commentContent = StringEscapeUtils.escapeHtml(commentContent);
        final String originalCommentId = requestJSONObject.optString(
                Comment.COMMENT_ORIGINAL_COMMENT_ID);
        // Step 1: Add comment
        final JSONObject comment = new JSONObject();
        comment.put(Comment.COMMENT_ORIGINAL_COMMENT_ID, "");
        comment.put(Comment.COMMENT_ORIGINAL_COMMENT_NAME, "");

        JSONObject originalComment = null;
        comment.put(Comment.COMMENT_NAME, commentName);
        comment.put(Comment.COMMENT_EMAIL, commentEmail);
        comment.put(Comment.COMMENT_URL, commentURL);
        comment.put(Comment.COMMENT_CONTENT, commentContent);
        comment.put(Comment.COMMENT_ORIGINAL_COMMENT_ID,
                    requestJSONObject.optString(
                Comment.COMMENT_ORIGINAL_COMMENT_ID));
        comment.put(Comment.COMMENT_ORIGINAL_COMMENT_NAME,
                    requestJSONObject.optString(
                Comment.COMMENT_ORIGINAL_COMMENT_NAME));
        final JSONObject preference = preferenceQueryService.getPreference();
        final String timeZoneId =
                preference.getString(Preference.TIME_ZONE_ID);
        final Date date = TimeZones.getTime(timeZoneId);
        comment.put(Comment.COMMENT_DATE, date);
        ret.put(Comment.COMMENT_DATE, Comment.DATE_FORMAT.format(date));

        if (!Strings.isEmptyOrNull(originalCommentId)) {
            originalComment =
                    commentRepository.get(originalCommentId);
            if (null != originalComment) {
                comment.put(Comment.COMMENT_ORIGINAL_COMMENT_ID,
                            originalCommentId);
                final String originalCommentName =
                        originalComment.getString(Comment.COMMENT_NAME);
                comment.put(Comment.COMMENT_ORIGINAL_COMMENT_NAME,
                            originalCommentName);
                ret.put(Comment.COMMENT_ORIGINAL_COMMENT_NAME,
                        originalCommentName);
            } else {
                LOGGER.log(Level.WARNING,
                           "Not found orginal comment[id={0}] of reply[name={1}, content={2}]",
                           new String[]{originalCommentId, commentName,
                                        commentContent});
            }
        }
        CommentMgmtService.setCommentThumbnailURL(comment);
        ret.put(Comment.COMMENT_THUMBNAIL_URL,
                comment.getString(Comment.COMMENT_THUMBNAIL_URL));
        // Sets comment on article....
        comment.put(Comment.COMMENT_ON_ID, articleId);
        comment.put(Comment.COMMENT_ON_TYPE, Article.ARTICLE);
        final String commentId = Ids.genTimeMillisId();
        comment.put(Keys.OBJECT_ID, commentId);

        final String commentSharpURL =
                Comments.getCommentSharpURLForArticle(article, commentId);
        comment.put(Comment.COMMENT_SHARP_URL, commentSharpURL);
        ret.put(Comment.COMMENT_SHARP_URL, commentSharpURL);

        commentRepository.add(comment);
        // Step 2: Update article comment count
        articleUtils.incArticleCommentCount(articleId);
        // Step 3: Update blog statistic comment count
        statistics.incBlogCommentCount();
        statistics.incPublishedBlogCommentCount();
        // Step 4: Send an email to admin
        try {
            Comments.sendNotificationMail(article, comment, originalComment,
                                          preference);
        } catch (final Exception e) {
            LOGGER.log(Level.WARNING, "Send mail failed", e);
        }
        // Step 5: Fire add comment event
        final JSONObject eventData = new JSONObject();
        eventData.put(Comment.COMMENT, comment);
        eventData.put(Article.ARTICLE, article);
        eventManager.fireEventSynchronously(
                new Event<JSONObject>(EventTypes.ADD_COMMENT_TO_ARTICLE,
                                      eventData));

        ret.put(Keys.STATUS_CODE, true);
        ret.put(Keys.OBJECT_ID, commentId);

        return ret;
    }
}
