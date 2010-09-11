/*
 * Copyright (C) 2009, 2010, B3log Team
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
package org.b3log.solo.jsonrpc.impl;

import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.mail.MailService;
import com.google.appengine.api.mail.MailService.Message;
import com.google.appengine.api.mail.MailServiceFactory;
import com.google.appengine.api.urlfetch.HTTPHeader;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import com.google.inject.Inject;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang.StringEscapeUtils;
import org.b3log.solo.action.StatusCodes;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.Comment;
import org.b3log.solo.repository.ArticleCommentRepository;
import org.b3log.solo.repository.ArticleRepository;
import org.b3log.solo.repository.CommentRepository;
import org.b3log.latke.Keys;
import org.b3log.latke.action.AbstractCacheablePageAction;
import org.b3log.latke.action.ActionException;
import org.b3log.latke.event.Event;
import org.b3log.latke.event.EventManager;
import org.b3log.latke.repository.gae.AbstractGAERepository;
import org.b3log.latke.util.MD5;
import org.b3log.solo.action.captcha.CaptchaServlet;
import org.b3log.solo.event.EventTypes;
import org.b3log.solo.jsonrpc.AbstractGAEJSONRpcService;
import org.b3log.solo.model.Common;
import org.b3log.solo.model.Preference;
import org.b3log.solo.servlet.SoloServletListener;
import org.b3log.solo.util.ArticleUtils;
import org.b3log.solo.util.Buzzs;
import org.b3log.solo.util.Statistics;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Comment service for JavaScript client.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.1.2, Sep 11, 2010
 */
public final class CommentService extends AbstractGAEJSONRpcService {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(CommentService.class.getName());
    /**
     * Comment repository.
     */
    @Inject
    private CommentRepository commentRepository;
    /**
     * Event manager.
     */
    @Inject
    private EventManager eventManager;
    /**
     * Article-Comment repository.
     */
    @Inject
    private ArticleCommentRepository articleCommentRepository;
    /**
     * Article repository.
     */
    @Inject
    private ArticleRepository articleRepository;
    /**
     * Article utilities.
     */
    @Inject
    private ArticleUtils articleUtils;
    /**
     * Statistic utilities.
     */
    @Inject
    private Statistics statistics;
    /**
     * URL fetch service.
     */
    private URLFetchService urlFetchService =
            URLFetchServiceFactory.getURLFetchService();
    /**
     * Default user thumbnail.
     */
    private static final String DEFAULT_USER_THUMBNAIL =
            "default-user-thumbnail.png";
    /**
     * Mail service.
     */
    private MailService mailService =
            MailServiceFactory.getMailService();
    /**
     * Comment mail HTML body.
     */
    private static final String COMMENT_MAIL_HTML_BODY =
            "Article[<a href=\""
            + "{articleURL}\">" + "{articleTitle}</a>]"
            + " received a new comment[<a href=\"{commentSharpURL}\">"
            + "{commentContent}</a>]";

    /**
     * Gets recent comments with the specified http servlet request and response.
     *
     * @param request the specified http servlet request
     * @param response the specified http servlet response
     * @return for example:
     * <pre>
     * {
     *     "recentComments": [{
     *         "oId": "",
     *         "commentName": "",
     *         "thumbnailUrl": "",
     *         "commentURL": "",
     *         "commentContent": "",
     *         "commentDate": "",
     *      }, ....]
     *     "sc": "GET_COMMENTS_SUCC"
     * }
     * </pre>
     * @throws ActionException action exception
     * @throws IOException io exception
     */
    public JSONObject getRecentComments(final HttpServletRequest request,
                                        final HttpServletResponse response)
            throws ActionException, IOException {
        final JSONObject ret = new JSONObject();
        try {
            final String requestHost =
                    request.getServerName() + ":" + request.getServerPort();
            final JSONObject preference =
                    SoloServletListener.getUserPreference();
            final String configuredHost = preference.getString(
                    Preference.BLOG_HOST);
            LOGGER.log(Level.FINEST, "Request[host={0}], configured[host={1}]",
                       new Object[]{requestHost, configuredHost});
            if (!requestHost.equals(configuredHost)) {
                LOGGER.log(Level.WARNING, "Unauthorized request[host={0}]",
                           requestHost);
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return ret;
            }


            final int fetchSize = preference.getInt(
                    Preference.RECENT_COMMENT_DISPLAY_CNT);
            final List<JSONObject> recentComments =
                    commentRepository.getRecentComments(fetchSize);
            // Erase email for security reason
            for (final JSONObject comment : recentComments) {
                comment.remove(Comment.COMMENT_EMAIL);
            }

            ret.put(Common.RECENT_COMMENTS, recentComments);
            ret.put(Keys.STATUS_CODE, StatusCodes.GET_COMMENTS_SUCC);
        } catch (final Exception e) {
            LOGGER.severe(e.getMessage());
            throw new ActionException(e);
        }

        return ret;
    }

    /**
     * Gets comments of an article specified by the article id for administrator.
     *
     * @param requestJSONObject the specified request json object, for example,
     * <pre>
     * {
     *     "oId": articleId
     * }
     * </pre>
     * @param request the specified http servlet request
     * @param response the specified http servlet response
     * @return for example,
     * <pre>
     * {
     *     "comments": [{
     *         "oId": "",
     *         "commentName": "",
     *         "commentEmail": "",
     *         "thumbnailUrl": "",
     *         "commentURL": "",
     *         "commentContent": "",
     *         "commentDate": "",
     *         "commentSharpURL": ""
     *      }, ....]
     *     "sc": "GET_COMMENTS_SUCC"
     * }
     * </pre>
     * @throws ActionException action exception
     * @throws IOException io exception
     */
    public JSONObject getComments(final JSONObject requestJSONObject,
                                  final HttpServletRequest request,
                                  final HttpServletResponse response)
            throws ActionException, IOException {
        checkAuthorized(request, response);
        final JSONObject ret = new JSONObject();

        try {
            final String articleId = requestJSONObject.getString(Keys.OBJECT_ID);
            // Step 1: Get article-comment relations
            final List<JSONObject> articleCommentRelations =
                    articleCommentRepository.getByArticleId(articleId);
            // Step 2: Get comments
            final List<JSONObject> comments = new ArrayList<JSONObject>();
            for (int i = 0; i < articleCommentRelations.size(); i++) {
                final JSONObject articleCommentRelation =
                        articleCommentRelations.get(i);
                final String commentId =
                        articleCommentRelation.getString(Comment.COMMENT + "_"
                                                         + Keys.OBJECT_ID);

                final JSONObject comment = commentRepository.get(commentId);
                comments.add(comment);
            }

            ret.put(Comment.COMMENTS, comments);
            ret.put(Keys.STATUS_CODE, StatusCodes.GET_COMMENTS_SUCC);
        } catch (final Exception e) {
            LOGGER.severe(e.getMessage());
            throw new ActionException(e);
        }

        return ret;
    }

    /**
     * Adds a comment to an article.
     *
     * @param requestJSONObject the specified request json object, for example,
     * <pre>
     * {
     *     "captcha": "",
     *     "oId": articleId,
     *     "commentName": "",
     *     "commentEmail": "",
     *     "commentURL": "",
     *     "commentContent": "",
     *     "originalCommentId": "" // optional, if exists this key, the comment
     *                             // is an reply comment
     * }
     * </pre>
     * @param request the specified http servlet request
     * @param response the specified http servlet response
     * @return for example,
     * <pre>
     * {
     *     "oId": generatedCommentId
     *     "sc": "COMMENT_ARTICLE_SUCC"
     * }
     * </pre>
     * @throws ActionException action exception
     * @throws IOException io exception
     */
    public JSONObject addComment(final JSONObject requestJSONObject,
                                 final HttpServletRequest request,
                                 final HttpServletResponse response)
            throws ActionException, IOException {
        final JSONObject ret = new JSONObject();
        final Transaction transaction =
                AbstractGAERepository.DATASTORE_SERVICE.beginTransaction();

        String articleId, commentId;
        try {
            final String captcha = requestJSONObject.getString(
                    CaptchaServlet.CAPTCHA);
            final HttpSession session = request.getSession();
            final String storedCaptcha = (String) session.getAttribute(
                    CaptchaServlet.CAPTCHA);
            if (!storedCaptcha.equals(captcha)) {
                ret.put(Keys.STATUS_CODE, StatusCodes.CAPTCHA_ERROR);

                return ret;
            }

            articleId = requestJSONObject.getString(Keys.OBJECT_ID);
            final JSONObject article = articleRepository.get(articleId);
            final String commentName =
                    requestJSONObject.getString(Comment.COMMENT_NAME);
            final String commentEmail =
                    requestJSONObject.getString(Comment.COMMENT_EMAIL);
            final String commentURL =
                    requestJSONObject.optString(Comment.COMMENT_URL);
            String commentContent =
                    requestJSONObject.getString(Comment.COMMENT_CONTENT);
            commentContent = StringEscapeUtils.escapeHtml(commentContent);
            // Step 1: Add comment
            final JSONObject comment = new JSONObject();
            comment.put(Comment.COMMENT_NAME, commentName);
            comment.put(Comment.COMMENT_EMAIL, commentEmail);
            comment.put(Comment.COMMENT_URL, commentURL);
            comment.put(Comment.COMMENT_CONTENT, commentContent);
            comment.put(Comment.COMMENT_DATE, new Date());
            setCommentThumbnailURL(comment);
            commentId = commentRepository.add(comment);
            // Save comment sharp URL
            final String commentSharpURL = getCommentSharpURL(request,
                                                              articleId,
                                                              commentId);
            comment.put(Comment.COMMENT_SHARP_URL, commentSharpURL);
            comment.put(Keys.OBJECT_ID, commentId);
            commentRepository.update(commentId, comment);
            // Step 2: Add article-comment relation
            final JSONObject articleCommentRelation = new JSONObject();
            articleCommentRelation.put(Article.ARTICLE + "_" + Keys.OBJECT_ID,
                                       articleId);
            articleCommentRelation.put(Comment.COMMENT + "_" + Keys.OBJECT_ID,
                                       commentId);
            articleCommentRepository.add(articleCommentRelation);
            // Step 3: Update article comment count
            articleUtils.incArticleCommentCount(articleId);
            // Step 4: Update blog statistic comment count
            statistics.incBlogCommentCount();
            // Step 5: Send an email to admin
            sendNotificationMail(article, commentId, commentContent, request);
            // Step 6: Fire add comment event
            eventManager.fireEventSynchronously(
                    new Event<JSONObject>(EventTypes.ADD_COMMENT, comment));

            transaction.commit();
            ret.put(Keys.STATUS_CODE, StatusCodes.COMMENT_ARTICLE_SUCC);
            ret.put(Keys.OBJECT_ID, commentId);
        } catch (final Exception e) {
            transaction.rollback();
            LOGGER.severe(e.getMessage());
            throw new ActionException(e);
        }

        AbstractCacheablePageAction.PAGE_CACHE.remove(
                "/article-detail.do?oId=" + articleId);
        AbstractCacheablePageAction.PAGE_CACHE.remove(
                "/article-detail.do?oId=" + articleId + "#" + commentId);

        return ret;
    }

    /**
     * Sends a notification mail to administrator for notifying the specified
     * article received a comment which specified by the given comment id and
     * comment content.
     *
     * @param article the specified article
     * @param commentId the given comment id
     * @param commentContent the given comment content
     * @param request the specified http servlet request
     * @throws IOException io exception
     * @throws JSONException json exception
     */
    private void sendNotificationMail(final JSONObject article,
                                      final String commentId,
                                      final String commentContent,
                                      final HttpServletRequest request)
            throws IOException, JSONException {
        final String articleId = article.getString(Keys.OBJECT_ID);
        final JSONObject preference =
                SoloServletListener.getUserPreference();
        final String blogTitle =
                preference.getString(Preference.BLOG_TITLE);
        final String articleTitle =
                article.getString(Article.ARTICLE_TITLE);
        final String commentSharpURL = getCommentSharpURL(request,
                                                          articleId,
                                                          commentId);
        final Message message = new Message();
        message.setSender(preference.getString(Preference.ADMIN_GMAIL));
        final String mailSubject = blogTitle + ": New comment on "
                                   + articleTitle;
        message.setSubject(mailSubject);
        final String articleURL = getArticleURL(request, articleId);
        final String mailBody =
                COMMENT_MAIL_HTML_BODY.replace("{articleURL}", articleURL).
                replace("{articleTitle}", articleTitle).
                replace("{commentContent}", commentContent).
                replace("{commentSharpURL}", commentSharpURL);
        message.setHtmlBody(mailBody);
        LOGGER.log(Level.FINER,
                   "Sending a mail[mailSubject={0}, mailBody=[{1}] to admins",
                   new Object[]{mailSubject, mailBody});
        mailService.sendToAdmins(message);
    }

    /**
     * Removes a comment by the specified request json object.
     *
     * @param requestJSONObject the specified request json object, for example,
     * <pre>
     * {
     *     "oId": commentId,
     * }
     * </pre>
     * @param request the specified http servlet request
     * @param response the specified http servlet response
     * @return for example,
     * <pre>
     * {
     *     "sc": "REMOVE_COMMENT_SUCC"
     * }
     * </pre>
     * @throws ActionException action exception
     * @throws IOException io exception
     */
    public JSONObject removeComment(final JSONObject requestJSONObject,
                                    final HttpServletRequest request,
                                    final HttpServletResponse response)
            throws ActionException, IOException {
        checkAuthorized(request, response);

        final JSONObject ret = new JSONObject();
        final Transaction transaction =
                AbstractGAERepository.DATASTORE_SERVICE.beginTransaction();
        try {
            final String commentId = requestJSONObject.getString(Keys.OBJECT_ID);
            LOGGER.log(Level.FINER, "Removing comment[oId={0}]", commentId);

            // Step 1: Remove article-comment relation
            final JSONObject articleCommentRelation =
                    articleCommentRepository.getByCommentId(commentId);
            final String articleCommentRelationId =
                    articleCommentRelation.getString(Keys.OBJECT_ID);
            articleCommentRepository.remove(articleCommentRelationId);

            final String articleId = articleCommentRelation.getString(
                    Article.ARTICLE + "_" + Keys.OBJECT_ID);
            // Step 2: Remove comment
            commentRepository.remove(commentId);
            // Step 3: Update article comment count
            articleUtils.decArticleCommentCount(articleId);
            // Step 4: Update blog statistic comment count
            statistics.decBlogCommentCount();
            // Step 5: Fire remove comment event
            eventManager.fireEventSynchronously(
                    new Event<String>(EventTypes.REMOVE_COMMENT, articleId));

            transaction.commit();
            ret.put(Keys.STATUS_CODE, StatusCodes.REMOVE_COMMENT_SUCC);

            LOGGER.log(Level.FINER, "Removed comment[oId={0}]", commentId);
        } catch (final Exception e) {
            transaction.rollback();
            LOGGER.severe(e.getMessage());
            throw new ActionException(e);
        }

        AbstractCacheablePageAction.PAGE_CACHE.removeAll();

        return ret;
    }

    /**
     * Sets commenter thumbnail URL for the specified comment.
     *
     * @param comment the specified comment
     * @throws Exception exception
     */
    private void setCommentThumbnailURL(final JSONObject comment)
            throws Exception {
        final String commentEmail = comment.getString(Comment.COMMENT_EMAIL);
        final String id = commentEmail.split("@")[0];
        final String domain = commentEmail.split("@")[1];
        String thumbnailURL = null;

        // Try to set thumbnail URL using Google Buzz API
        if ("gmail.com".equals(domain.toLowerCase())) {
            final URL googleProfileURL =
                    new URL(Buzzs.GOOGLE_PROFILE_RETRIEVAL.replace("{userId}",
                                                                   id));
            // XXX: use async url fetch instead????
            final HTTPResponse response =
                    urlFetchService.fetch(googleProfileURL);
            final int statusCode = response.getResponseCode();

            if (HttpServletResponse.SC_OK == statusCode) {
                final byte[] content = response.getContent();
                final String profileJSONString = new String(content);
                LOGGER.log(Level.FINEST, "Google profile[jsonString={0}]",
                           profileJSONString);
                final JSONObject profile = new JSONObject(profileJSONString);
                final JSONObject profileData = profile.getJSONObject("data");
                thumbnailURL = profileData.getString("thumbnailUrl");
                comment.put(Comment.COMMENT_THUMBNAIL_URL, thumbnailURL);
                LOGGER.log(Level.FINEST, "Comment thumbnail[URL={0}]",
                           thumbnailURL);

                return;
            } else {
                LOGGER.log(Level.WARNING,
                           "Can not fetch google profile[userId={0}, statusCode={1}]",
                           new Object[]{id, statusCode});
            }
        }

        // Try to set thumbnail URL using Gravatar service
        final String hashedEmail = MD5.hash(commentEmail.toLowerCase());
        final int size = 60;
        final URL gravatarURL =
                new URL("http://www.gravatar.com/avatar/" + hashedEmail + "?s="
                        + size + "&r=G");
        // XXX: use async url fetch instead????
        try {
            final HTTPResponse response = urlFetchService.fetch(gravatarURL);
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
                                   + hashedEmail
                                   + "?s="
                                   + size + "&r=G";
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
            thumbnailURL = "/images/" + DEFAULT_USER_THUMBNAIL;
            LOGGER.log(Level.WARNING,
                       "Not supported yet for comment thumbnail for email[{0}]",
                       commentEmail);
            comment.put(Comment.COMMENT_THUMBNAIL_URL, thumbnailURL);
        }
    }

    /**
     * Gets comment sharp URL with the specified http servlet request, article
     * id and comment id.
     *
     * @param request the specified http servlet request
     * @param articleId the specified article id
     * @param commentId the specified comment id
     * @return comment sharp URL
     */
    private String getCommentSharpURL(final HttpServletRequest request,
                                      final String articleId,
                                      final String commentId) {
        return getArticleURL(request, articleId) + "#" + commentId;
    }

    /**
     * Gets article URL with the specified http servlet request and article id.
     *
     * @param request the specified http servlet request
     * @param articleId the specified article id
     * @return article URL
     */
    private String getArticleURL(final HttpServletRequest request,
                                 final String articleId) {
        return request.getScheme() + "://" + request.getServerName() + ":"
               + request.getServerPort() + "/article-detail.do?oId="
               + articleId;
    }
}
