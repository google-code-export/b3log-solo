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
import org.b3log.latke.action.ActionException;
import org.b3log.latke.action.util.PageCaches;
import org.b3log.latke.event.Event;
import org.b3log.latke.event.EventManager;
import org.b3log.latke.repository.gae.AbstractGAERepository;
import org.b3log.latke.util.MD5;
import org.b3log.latke.util.Strings;
import org.b3log.solo.action.captcha.CaptchaServlet;
import org.b3log.solo.event.EventTypes;
import org.b3log.solo.jsonrpc.AbstractGAEJSONRpcService;
import org.b3log.solo.model.Common;
import org.b3log.solo.model.Preference;
import org.b3log.solo.model.Google;
import org.b3log.solo.model.Page;
import org.b3log.solo.repository.PageCommentRepository;
import org.b3log.solo.repository.PageRepository;
import org.b3log.solo.util.ArticleUtils;
import org.b3log.solo.util.PageUtils;
import org.b3log.solo.util.PreferenceUtils;
import org.b3log.solo.util.Statistics;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Comment service for JavaScript client.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.2.7, Nov 15, 2010
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
     * Page-Comment repository.
     */
    @Inject
    private PageCommentRepository pageCommentRepository;
    /**
     * Article repository.
     */
    @Inject
    private ArticleRepository articleRepository;
    /**
     * Page repository.
     */
    @Inject
    private PageRepository pageRepository;
    /**
     * Article utilities.
     */
    @Inject
    private ArticleUtils articleUtils;
    /**
     * Page utilities.
     */
    @Inject
    private PageUtils pageUtils;
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
            "{articleOrPage} [<a href=\""
            + "{articleOrPageURL}\">" + "{title}</a>]"
            + " received a new comment [<a href=\"http://{commentSharpURL}\">"
            + "{commentContent}</a>]";
    /**
     * Preference utilities.
     */
    @Inject
    private PreferenceUtils preferenceUtils;

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
            final JSONObject preference = preferenceUtils.getPreference();
            if (null == preference) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
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
    public JSONObject getCommentsOfArticle(final JSONObject requestJSONObject,
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
     * Gets comments of a page specified by the page id for administrator.
     *
     * @param requestJSONObject the specified request json object, for example,
     * <pre>
     * {
     *     "oId": pageId
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
    public JSONObject getCommentsOfPage(final JSONObject requestJSONObject,
                                        final HttpServletRequest request,
                                        final HttpServletResponse response)
            throws ActionException, IOException {
        checkAuthorized(request, response);
        final JSONObject ret = new JSONObject();

        try {
            final String pageId = requestJSONObject.getString(Keys.OBJECT_ID);
            // Step 1: Get page-comment relations
            final List<JSONObject> pageCommentRelations =
                    pageCommentRepository.getByPageId(pageId);
            // Step 2: Get comments
            final List<JSONObject> comments = new ArrayList<JSONObject>();
            for (int i = 0; i < pageCommentRelations.size(); i++) {
                final JSONObject pageCommentRelation =
                        pageCommentRelations.get(i);
                final String commentId =
                        pageCommentRelation.getString(Comment.COMMENT + "_"
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
     *     "commentOriginalCommentId": "" // optional, if exists this key, the comment
     *                                    // is an reply comment
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
    public JSONObject addCommentToArticle(
            final JSONObject requestJSONObject,
            final HttpServletRequest request,
            final HttpServletResponse response) throws ActionException,
                                                       IOException {
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
            if (null == storedCaptcha || !storedCaptcha.equals(captcha)) {
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
            final String originalCommentId = requestJSONObject.optString(
                    Comment.COMMENT_ORIGINAL_COMMENT_ID);
            // Step 1: Add comment
            final JSONObject comment = new JSONObject();
            JSONObject originalComment = null;
            comment.put(Comment.COMMENT_NAME, commentName);
            comment.put(Comment.COMMENT_EMAIL, commentEmail);
            comment.put(Comment.COMMENT_URL, commentURL);
            comment.put(Comment.COMMENT_CONTENT, commentContent);
            comment.put(Comment.COMMENT_DATE, new Date());
            if (!Strings.isEmptyOrNull(originalCommentId)) {
                originalComment =
                        commentRepository.get(originalCommentId);
                if (null != originalComment) {
                    comment.put(Comment.COMMENT_ORIGINAL_COMMENT_ID,
                                originalCommentId);
                    comment.put(Comment.COMMENT_ORIGINAL_COMMENT_NAME,
                                originalComment.getString(Comment.COMMENT_NAME));
                } else {
                    LOGGER.log(Level.WARNING,
                               "Not found orginal comment[id={0}] of reply[name={1}, content={2}]",
                               new String[]{originalCommentId, commentName,
                                            commentContent});
                }
            }
            setCommentThumbnailURL(comment);
            commentId = commentRepository.add(comment);
            // Save comment sharp URL
            final String commentSharpURL =
                    getCommentSharpURLForArticle(article,
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
            sendNotificationMail(article, comment, originalComment);
            // Step 6: Fire add comment event
            final JSONObject eventData = new JSONObject();
            eventData.put(Comment.COMMENT, comment);
            eventData.put(Article.ARTICLE, article);
            eventManager.fireEventSynchronously(
                    new Event<JSONObject>(EventTypes.ADD_COMMENT_TO_ARTICLE,
                                          eventData));

            PageCaches.removeAll();

            transaction.commit();
            ret.put(Keys.STATUS_CODE, StatusCodes.COMMENT_ARTICLE_SUCC);
            ret.put(Keys.OBJECT_ID, commentId);
        } catch (final Exception e) {
            transaction.rollback();
            LOGGER.severe(e.getMessage());
            throw new ActionException(e);
        }

        return ret;
    }

    /**
     * Adds a comment to a page.
     *
     * @param requestJSONObject the specified request json object, for example,
     * <pre>
     * {
     *     "captcha": "",
     *     "oId": pageId,
     *     "commentName": "",
     *     "commentEmail": "",
     *     "commentURL": "",
     *     "commentContent": "",
     *     "commentOriginalCommentId": "" // optional, if exists this key, the comment
     *                                    // is an reply comment
     * }
     * </pre>
     * @param request the specified http servlet request
     * @param response the specified http servlet response
     * @return for example,
     * <pre>
     * {
     *     "oId": generatedCommentId
     *     "sc": "COMMENT_PAGE_SUCC"
     * }
     * </pre>
     * @throws ActionException action exception
     * @throws IOException io exception
     */
    public JSONObject addCommentToPage(
            final JSONObject requestJSONObject,
            final HttpServletRequest request,
            final HttpServletResponse response) throws ActionException,
                                                       IOException {
        final JSONObject ret = new JSONObject();
        final Transaction transaction =
                AbstractGAERepository.DATASTORE_SERVICE.beginTransaction();

        String pageId, commentId;
        try {
            final String captcha = requestJSONObject.getString(
                    CaptchaServlet.CAPTCHA);
            final HttpSession session = request.getSession();
            final String storedCaptcha = (String) session.getAttribute(
                    CaptchaServlet.CAPTCHA);
            if (null == storedCaptcha || !storedCaptcha.equals(captcha)) {
                ret.put(Keys.STATUS_CODE, StatusCodes.CAPTCHA_ERROR);

                return ret;
            }

            pageId = requestJSONObject.getString(Keys.OBJECT_ID);
            final JSONObject page = pageRepository.get(pageId);
            final String commentName =
                    requestJSONObject.getString(Comment.COMMENT_NAME);
            final String commentEmail =
                    requestJSONObject.getString(Comment.COMMENT_EMAIL);
            final String commentURL =
                    requestJSONObject.optString(Comment.COMMENT_URL);
            String commentContent =
                    requestJSONObject.getString(Comment.COMMENT_CONTENT);
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
            comment.put(Comment.COMMENT_DATE, new Date());
            if (!Strings.isEmptyOrNull(originalCommentId)) {
                originalComment =
                        commentRepository.get(originalCommentId);
                if (null != originalComment) {
                    comment.put(Comment.COMMENT_ORIGINAL_COMMENT_ID,
                                originalCommentId);
                    comment.put(Comment.COMMENT_ORIGINAL_COMMENT_NAME,
                                originalComment.getString(Comment.COMMENT_NAME));
                } else {
                    LOGGER.log(Level.WARNING,
                               "Not found orginal comment[id={0}] of reply[name={1}, content={2}]",
                               new String[]{originalCommentId, commentName,
                                            commentContent});
                }
            }
            setCommentThumbnailURL(comment);
            commentId = commentRepository.add(comment);
            // Save comment sharp URL
            final String commentSharpURL = getCommentSharpURLForPage(page,
                                                                     commentId);
            comment.put(Comment.COMMENT_SHARP_URL, commentSharpURL);
            comment.put(Keys.OBJECT_ID, commentId);
            commentRepository.update(commentId, comment);
            // Step 2: Add page-comment relation
            final JSONObject pageCommentRelation = new JSONObject();
            pageCommentRelation.put(Page.PAGE + "_" + Keys.OBJECT_ID,
                                    pageId);
            pageCommentRelation.put(Comment.COMMENT + "_" + Keys.OBJECT_ID,
                                    commentId);
            pageCommentRepository.add(pageCommentRelation);
            // Step 3: Update page comment count
            pageUtils.incPageCommentCount(pageId);
            // Step 4: Update blog statistic comment count
            statistics.incBlogCommentCount();
            // Step 5: Send an email to admin
            sendNotificationMail(page, comment, originalComment);
            // Step 6: Fire add comment event
            final JSONObject eventData = new JSONObject();
            eventData.put(Comment.COMMENT, comment);
            eventData.put(Page.PAGE, page);
            eventManager.fireEventSynchronously(
                    new Event<JSONObject>(EventTypes.ADD_COMMENT_TO_PAGE,
                                          eventData));

            PageCaches.removeAll();

            transaction.commit();
            ret.put(Keys.STATUS_CODE, StatusCodes.COMMENT_PAGE_SUCC);
            ret.put(Keys.OBJECT_ID, commentId);
        } catch (final Exception e) {
            transaction.rollback();
            LOGGER.severe(e.getMessage());
            throw new ActionException(e);
        }

        return ret;
    }

    /**
     * Sends a notification mail to administrator for notifying the specified
     * article or page received the specified comment.
     *
     * @param articleOrPage the specified article or page
     * @param comment the specified comment
     * @param originalComment original comment, if not exists, set it as
     * {@code null}
     * @throws IOException io exception
     * @throws JSONException json exception
     */
    private void sendNotificationMail(final JSONObject articleOrPage,
                                      final JSONObject comment,
                                      final JSONObject originalComment)
            throws IOException, JSONException {
        final String commentEmail = comment.getString(Comment.COMMENT_EMAIL);
        final String commentId = comment.getString(Keys.OBJECT_ID);
        final String commentContent = comment.getString(Comment.COMMENT_CONTENT);
        final JSONObject preference = preferenceUtils.getPreference();
        if (null == preference) {
            throw new IOException("Not found preference");
        }

        final String adminEmail = preference.getString(Preference.ADMIN_GMAIL);
        if (adminEmail.equalsIgnoreCase(commentEmail)) {
            LOGGER.log(Level.FINE,
                       "Do not send comment notification mail to admin itself[{0}]",
                       adminEmail);
            return;
        }

        if (comment.has(Comment.COMMENT_ORIGINAL_COMMENT_ID)) {
            final String originalEmail =
                    originalComment.getString(Comment.COMMENT_EMAIL);
            if (originalEmail.equalsIgnoreCase(adminEmail)) {
                LOGGER.log(Level.FINE,
                           "Do not send comment notification mail to admin while the specified comment[{0}] is an reply",
                           commentId);
                return;
            }
        }

        final String blogTitle =
                preference.getString(Preference.BLOG_TITLE);
        final String blogHost =
                preference.getString(Preference.BLOG_HOST);
        boolean isArticle = true;
        String title =
                articleOrPage.optString(Article.ARTICLE_TITLE);
        if (Strings.isEmptyOrNull(title)) {
            title = articleOrPage.getString(Page.PAGE_TITLE);
            isArticle = false;
        }

        final String commentSharpURL =
                comment.getString(Comment.COMMENT_SHARP_URL);
        final Message message = new Message();
        message.setSender(adminEmail);
        String mailSubject = null;
        String articleOrPageURL = null;
        String mailBody = null;
        if (isArticle) {
            mailSubject = blogTitle + ": New comment about article ["
                          + title + "]";
            articleOrPageURL = "http://" + blogHost + articleOrPage.getString(
                    Article.ARTICLE_PERMALINK);
            mailBody = COMMENT_MAIL_HTML_BODY.replace("{articleOrPage}",
                                                      "Article");
        } else {
            mailSubject = blogTitle + ": New comment about page ["
                          + title + "]";
            articleOrPageURL = "http://" + blogHost + "/page.do?oId="
                               + articleOrPage.getString(Keys.OBJECT_ID);
            mailBody = COMMENT_MAIL_HTML_BODY.replace("{articleOrPage}", "Page");
        }

        message.setSubject(mailSubject);
        mailBody = mailBody.replace(
                "{articleOrPageURL}", articleOrPageURL).
                replace("{title}", title).
                replace("{commentContent}", commentContent).
                replace("{commentSharpURL}", blogHost + commentSharpURL);
        message.setHtmlBody(mailBody);
        LOGGER.log(Level.FINER,
                   "Sending a mail[mailSubject={0}, mailBody=[{1}] to admins",
                   new Object[]{mailSubject, mailBody});
        mailService.sendToAdmins(message);
    }

    /**
     * Removes a comment of an article by the specified request json object.
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
    public JSONObject removeCommentOfArticle(final JSONObject requestJSONObject,
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

            PageCaches.removeAll();

            transaction.commit();
            ret.put(Keys.STATUS_CODE, StatusCodes.REMOVE_COMMENT_SUCC);

            LOGGER.log(Level.FINER, "Removed comment[oId={0}]", commentId);
        } catch (final Exception e) {
            transaction.rollback();
            LOGGER.severe(e.getMessage());
            throw new ActionException(e);
        }

        return ret;
    }

    /**
     * Removes a comment of a page by the specified request json object.
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
    public JSONObject removeCommentOfPage(final JSONObject requestJSONObject,
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

            // Step 1: Remove page-comment relation
            final JSONObject pageCommentRelation =
                    pageCommentRepository.getByCommentId(commentId);
            final String pageCommentRelationId =
                    pageCommentRelation.getString(Keys.OBJECT_ID);
            pageCommentRepository.remove(pageCommentRelationId);

            final String pageId = pageCommentRelation.getString(
                    Page.PAGE + "_" + Keys.OBJECT_ID);
            // Step 2: Remove comment
            commentRepository.remove(commentId);
            // Step 3: Update page comment count
            pageUtils.decPageCommentCount(pageId);
            // Step 4: Update blog statistic comment count
            statistics.decBlogCommentCount();
            // Step 5: Fire remove comment event
            eventManager.fireEventSynchronously(
                    new Event<String>(EventTypes.REMOVE_COMMENT, pageId));

            PageCaches.removeAll();

            transaction.commit();
            ret.put(Keys.STATUS_CODE, StatusCodes.REMOVE_COMMENT_SUCC);

            LOGGER.log(Level.FINER,
                       "Removed comment[oId={0}] of page[{oId={1}}]",
                       new String[]{commentId, pageId});
        } catch (final Exception e) {
            transaction.rollback();
            LOGGER.severe(e.getMessage());
            throw new ActionException(e);
        }

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
                    new URL(Google.GOOGLE_PROFILE_RETRIEVAL.replace("{userId}",
                                                                    id));
            final HTTPResponse response =
                    urlFetchService.fetch(googleProfileURL);
            final int statusCode = response.getResponseCode();

            if (HttpServletResponse.SC_OK == statusCode) {
                final byte[] content = response.getContent();
                final String profileJSONString = new String(content, "UTF-8");
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

    /**
     * Gets comment sharp URL with the specified article and comment id.
     *
     * @param article the specified article
     * @param commentId the specified comment id
     * @return comment sharp URL
     * @throws JSONException json exception
     */
    private String getCommentSharpURLForArticle(final JSONObject article,
                                                final String commentId)
            throws JSONException {
        final String articleLink = article.getString(Article.ARTICLE_PERMALINK);

        return articleLink + "#" + commentId;
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
        final String pageId = page.getString(Keys.OBJECT_ID);

        return "/page.do?oId=" + pageId + "#" + commentId;
    }
}
