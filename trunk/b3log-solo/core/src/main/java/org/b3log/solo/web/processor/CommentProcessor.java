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

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import org.b3log.latke.urlfetch.URLFetchService;
import org.b3log.latke.urlfetch.URLFetchServiceFactory;
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
import org.b3log.latke.urlfetch.HTTPHeader;
import org.b3log.latke.urlfetch.HTTPRequest;
import org.b3log.latke.urlfetch.HTTPResponse;
import org.b3log.latke.util.Ids;
import org.b3log.latke.util.MD5;
import org.b3log.latke.util.Strings;
import org.b3log.solo.SoloServletListener;
import org.b3log.solo.event.EventTypes;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.Comment;
import org.b3log.solo.model.Page;
import org.b3log.solo.model.Preference;
import org.b3log.solo.repository.ArticleRepository;
import org.b3log.solo.repository.impl.ArticleRepositoryImpl;
import org.b3log.solo.service.CommentMgmtService;
import org.b3log.solo.service.PreferenceQueryService;
import org.b3log.solo.util.Articles;
import org.b3log.solo.util.Comments;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Comment processor.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.1.0.3, Nov 10, 2011
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
     * Time zone utilities.
     */
    private static TimeZones timeZoneUtils = TimeZones.getInstance();
    /**
     * Article utilities.
     */
    private static Articles articleUtils = Articles.getInstance();
    /**
     * Statistic utilities.
     */
    private static Statistics statistics = Statistics.getInstance();
    /**
     * Default user thumbnail.
     */
    private static final String DEFAULT_USER_THUMBNAIL =
            "default-user-thumbnail.png";
    /**
     * URL fetch service.
     */
    private static URLFetchService urlFetchService =
            URLFetchServiceFactory.getURLFetchService();
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
            LOGGER.log(Level.WARNING, "Can''t add comment[msg={0}]", jsonObject.
                    optString(Keys.MSG));
            return;
        }

        final Transaction transaction = commentRepository.beginTransaction();

        String pageId, commentId;
        try {
            final String captcha = requestJSONObject.getString(
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

            pageId = requestJSONObject.getString(Keys.OBJECT_ID);
            final JSONObject page = pageRepository.get(pageId);
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
            JSONObject originalComment = null;
            comment.put(Comment.COMMENT_NAME, commentName);
            comment.put(Comment.COMMENT_EMAIL, commentEmail);
            comment.put(Comment.COMMENT_URL, commentURL);
            comment.put(Comment.COMMENT_CONTENT, commentContent);
            final JSONObject preference = preferenceQueryService.getPreference();
            final String timeZoneId =
                    preference.getString(Preference.TIME_ZONE_ID);
            final Date date = timeZoneUtils.getTime(timeZoneId);
            comment.put(Comment.COMMENT_DATE, date);
            jsonObject.put(Comment.COMMENT_DATE,
                           Comment.DATE_FORMAT.format(date));
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
                    jsonObject.put(Comment.COMMENT_ORIGINAL_COMMENT_NAME,
                                   originalCommentName);
                } else {
                    LOGGER.log(Level.WARNING,
                               "Not found orginal comment[id={0}] of reply[name={1}, content={2}]",
                               new String[]{originalCommentId, commentName,
                                            commentContent});
                }
            }
            setCommentThumbnailURL(comment);
            jsonObject.put(Comment.COMMENT_THUMBNAIL_URL,
                           comment.getString(Comment.COMMENT_THUMBNAIL_URL));
            // Sets comment on page....
            comment.put(Comment.COMMENT_ON_ID, pageId);
            comment.put(Comment.COMMENT_ON_TYPE, Page.PAGE);
            commentId = commentRepository.add(comment);
            // Save comment sharp URL
            final String commentSharpURL = getCommentSharpURLForPage(page,
                                                                     commentId);
            jsonObject.put(Comment.COMMENT_SHARP_URL, commentSharpURL);
            comment.put(Comment.COMMENT_SHARP_URL, commentSharpURL);
            comment.put(Keys.OBJECT_ID, commentId);
            commentRepository.update(commentId, comment);
            // Step 2: Update page comment count
            commentMgmtService.incPageCommentCount(pageId);
            // Step 3: Update blog statistic comment count
            statistics.incBlogCommentCount();
            statistics.incPublishedBlogCommentCount();
            // Step 4: Send an email to admin
            try {
                Comments.sendNotificationMail(page, comment, originalComment,
                                              preference);
            } catch (final Exception e) {
                LOGGER.log(Level.WARNING, "Send mail failed", e);
            }
            // Step 5: Fire add comment event
            final JSONObject eventData = new JSONObject();
            eventData.put(Comment.COMMENT, comment);
            eventData.put(Page.PAGE, page);
            eventManager.fireEventSynchronously(
                    new Event<JSONObject>(EventTypes.ADD_COMMENT_TO_PAGE,
                                          eventData));

            transaction.commit();
            jsonObject.put(Keys.STATUS_CODE, true);
            jsonObject.put(Keys.OBJECT_ID, commentId);
        } catch (final Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }

            LOGGER.log(Level.SEVERE, "Can not add comment on page", e);

            try {
                jsonObject.put(Keys.STATUS_CODE, false);
                jsonObject.put(Keys.MSG, langPropsService.get("addFailLabel"));
            } catch (final JSONException ex) {
                throw new RuntimeException(ex);
            }
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
            LOGGER.log(Level.WARNING, "Can''t add comment[msg={0}]", jsonObject.
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
            try {
                jsonObject.put(Keys.STATUS_CODE, false);
                jsonObject.put(Keys.MSG, langPropsService.get("addFailLabel"));
            } catch (final JSONException ex) {
                throw new RuntimeException(ex);
            }
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
        final Date date = timeZoneUtils.getTime(timeZoneId);
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
        setCommentThumbnailURL(comment);
        ret.put(Comment.COMMENT_THUMBNAIL_URL,
                comment.getString(Comment.COMMENT_THUMBNAIL_URL));
        // Sets comment on article....
        comment.put(Comment.COMMENT_ON_ID, articleId);
        comment.put(Comment.COMMENT_ON_TYPE, Article.ARTICLE);
        final String commentId = Ids.genTimeMillisId();
        comment.put(Keys.OBJECT_ID, commentId);

        final String commentSharpURL =
                getCommentSharpURLForArticle(article,
                                             commentId);
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

    /**
     * Gets comment sharp URL with the specified page and comment id.
     *
     * @param page the specified page
     * @param commentId the specified comment id
     * @return comment sharp URL
     * @throws JSONException json exception
     */
    private String getCommentSharpURLForPage(final JSONObject page,
                                             final String commentId)
            throws JSONException {
        return page.getString(Page.PAGE_PERMALINK) + "#" + commentId;
    }

    /**
     * Gets comment sharp URL with the specified article and comment id.
     *
     * @param article the specified article
     * @param commentId the specified comment id
     * @return comment sharp URL
     * @throws JSONException json exception
     */
    private static String getCommentSharpURLForArticle(final JSONObject article,
                                                       final String commentId)
            throws JSONException {
        final String articleLink = article.getString(Article.ARTICLE_PERMALINK);

        return articleLink + "#" + commentId;
    }

    /**
     * Sets commenter thumbnail URL for the specified comment.
     *
     * @param comment the specified comment
     * @throws Exception exception
     */
    private static void setCommentThumbnailURL(final JSONObject comment)
            throws Exception {
        final String commentEmail = comment.getString(Comment.COMMENT_EMAIL);
        final String id = commentEmail.split("@")[0];
        final String domain = commentEmail.split("@")[1];
        String thumbnailURL = null;

        // Try to set thumbnail URL using Gravatar service
        final String hashedEmail = MD5.hash(commentEmail.toLowerCase());
        final int size = 60;
        final URL gravatarURL =
                new URL("http://www.gravatar.com/avatar/" + hashedEmail + "?s="
                        + size + "&r=G");

        try {
            final HTTPRequest request = new HTTPRequest();
            request.setURL(gravatarURL);
            final HTTPResponse response = urlFetchService.fetch(request);
            final int statusCode = response.getResponseCode();

            if (HttpServletResponse.SC_OK == statusCode) {
                final List<HTTPHeader> headers = response.getHeaders();
                boolean defaultFileLengthMatched = false;
                for (final HTTPHeader httpHeader : headers) {
                    if ("Content-Length".equalsIgnoreCase(httpHeader.getName())) {
                        if (httpHeader.getValue().equals("2147")) {
                            defaultFileLengthMatched = true;
                        }
                    }
                }

                if (!defaultFileLengthMatched) {
                    thumbnailURL = "http://www.gravatar.com/avatar/"
                                   + hashedEmail + "?s=" + size + "&r=G";
                    comment.put(Comment.COMMENT_THUMBNAIL_URL, thumbnailURL);
                    LOGGER.log(Level.FINEST, "Comment thumbnail[URL={0}]",
                               thumbnailURL);

                    return;
                }
            } else {
                LOGGER.log(Level.WARNING,
                           "Can not fetch thumbnail from Gravatar[commentEmail={0}, statusCode={1}]",
                           new Object[]{commentEmail, statusCode});
            }
        } catch (final IOException e) {
            LOGGER.warning(e.getMessage());
            LOGGER.log(Level.WARNING,
                       "Can not fetch thumbnail from Gravatar[commentEmail={0}]",
                       commentEmail);
        }

        if (null == thumbnailURL) {
            LOGGER.log(Level.WARNING,
                       "Not supported yet for comment thumbnail for email[{0}]",
                       commentEmail);
            thumbnailURL = "/images/" + DEFAULT_USER_THUMBNAIL;
            comment.put(Comment.COMMENT_THUMBNAIL_URL, thumbnailURL);
        }
    }
}
