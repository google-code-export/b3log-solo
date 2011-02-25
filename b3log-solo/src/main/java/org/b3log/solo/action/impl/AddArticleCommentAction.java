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

package org.b3log.solo.action.impl;

import com.google.appengine.api.mail.MailService;
import com.google.appengine.api.mail.MailService.Message;
import com.google.appengine.api.mail.MailServiceFactory;
import com.google.appengine.api.urlfetch.HTTPHeader;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang.StringEscapeUtils;
import org.b3log.latke.Keys;
import org.b3log.latke.action.AbstractAction;
import org.b3log.latke.action.ActionException;
import org.b3log.latke.action.util.PageCaches;
import org.b3log.latke.event.Event;
import org.b3log.latke.event.EventManager;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.util.MD5;
import org.b3log.latke.util.Strings;
import org.b3log.solo.SoloServletListener;
import org.b3log.solo.action.StatusCodes;
import org.b3log.solo.action.captcha.CaptchaServlet;
import org.b3log.solo.event.EventTypes;
import org.b3log.solo.jsonrpc.impl.CommentService;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.Comment;
import org.b3log.solo.model.Google;
import org.b3log.solo.model.Page;
import org.b3log.solo.model.Preference;
import org.b3log.solo.repository.ArticleCommentRepository;
import org.b3log.solo.repository.ArticleRepository;
import org.b3log.solo.repository.CommentRepository;
import org.b3log.solo.repository.impl.ArticleCommentGAERepository;
import org.b3log.solo.repository.impl.ArticleGAERepository;
import org.b3log.solo.repository.impl.CommentGAERepository;
import org.b3log.solo.util.Articles;
import org.b3log.solo.util.Preferences;
import org.b3log.solo.util.Statistics;
import org.b3log.solo.util.TimeZones;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Adds article comment action.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.3, Jan 12, 2011
 */
public final class AddArticleCommentAction extends AbstractAction {

    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(AddArticleCommentAction.class.getName());
    /**
     * Comment repository.
     */
    private static CommentRepository commentRepository =
            CommentGAERepository.getInstance();
    /**
     * Article utilities.
     */
    private static Articles articleUtils = Articles.getInstance();
    /**
     * Preference utilities.
     */
    private static Preferences preferenceUtils = Preferences.getInstance();
    /**
     * Time zone utilities.
     */
    private static TimeZones timeZoneUtils = TimeZones.getInstance();
    /**
     * Article repository.
     */
    private static ArticleRepository articleRepository =
            ArticleGAERepository.getInstance();
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
     * Mail service.
     */
    private static MailService mailService =
            MailServiceFactory.getMailService();
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
     * Article-Comment repository.
     */
    private static ArticleCommentRepository articleCommentRepository =
            ArticleCommentGAERepository.getInstance();
    /**
     * Comment mail HTML body.
     */
    public static final String COMMENT_MAIL_HTML_BODY =
            "<p>{articleOrPage} [<a href=\"" + "{articleOrPageURL}\">"
            + "{title}</a>]" + " received a new comment:</p>"
            + "{commenter}: <span><a href=\"http://{commentSharpURL}\">"
            + "{commentContent}</a></span>";

    @Override
    protected Map<?, ?> doFreeMarkerAction(
            final freemarker.template.Template template,
            final HttpServletRequest request,
            final HttpServletResponse response) throws ActionException {
        throw new UnsupportedOperationException("Not supported yet.");
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
     *                                    // is an reply
     * }
     * </pre>
     * @param request the specified http servlet request
     * @param response the specified http servlet response
     * @return for example,
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
     * @throws ActionException action exception
     */
    @Override
    public JSONObject doAjaxAction(final JSONObject requestJSONObject,
                                   final HttpServletRequest request,
                                   final HttpServletResponse response)
            throws ActionException {
        final JSONObject ret = new JSONObject();
        final Transaction transaction = commentRepository.beginTransaction();

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

            synchronized (CommentService.class) {
                session.removeAttribute(CaptchaServlet.CAPTCHA);
            }

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
            JSONObject originalComment = null;
            comment.put(Comment.COMMENT_NAME, commentName);
            comment.put(Comment.COMMENT_EMAIL, commentEmail);
            comment.put(Comment.COMMENT_URL, commentURL);
            comment.put(Comment.COMMENT_CONTENT, commentContent);
            final JSONObject preference = preferenceUtils.getPreference();
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
            commentId = commentRepository.add(comment);
            // Save comment sharp URL
            final String commentSharpURL =
                    getCommentSharpURLForArticle(article,
                                                 commentId);
            comment.put(Comment.COMMENT_SHARP_URL, commentSharpURL);
            ret.put(Comment.COMMENT_SHARP_URL, commentSharpURL);
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
            statistics.incPublishedBlogCommentCount();
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
            if (transaction.isActive()) {
                transaction.rollback();
            }
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
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
    private static void sendNotificationMail(final JSONObject articleOrPage,
                                             final JSONObject comment,
                                             final JSONObject originalComment)
            throws IOException, JSONException {
        final String commentEmail = comment.getString(Comment.COMMENT_EMAIL);
        final String commentId = comment.getString(Keys.OBJECT_ID);
        final String commentContent = comment.getString(Comment.COMMENT_CONTENT).
                replaceAll(SoloServletListener.ENTER_ESC, "<br/>");
        final JSONObject preference = preferenceUtils.getPreference();
        if (null == preference) {
            throw new IOException("Not found preference");
        }

        final String adminEmail = preference.getString(Preference.ADMIN_EMAIL);
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
            mailSubject = blogTitle + ": New comment on article ["
                          + title + "]";
            articleOrPageURL = "http://" + blogHost + articleOrPage.getString(
                    Article.ARTICLE_PERMALINK);
            mailBody = COMMENT_MAIL_HTML_BODY.replace("{articleOrPage}",
                                                      "Article");
        } else {
            mailSubject = blogTitle + ": New comment on page ["
                          + title + "]";
            articleOrPageURL = "http://" + blogHost + "/page.do?oId="
                               + articleOrPage.getString(Keys.OBJECT_ID);
            mailBody = COMMENT_MAIL_HTML_BODY.replace("{articleOrPage}", "Page");
        }

        message.setSubject(mailSubject);
        final String commentName = comment.getString(Comment.COMMENT_NAME);
        final String commentURL = comment.getString(Comment.COMMENT_URL);
        String commenter = null;
        if (!"http://".equals(commentURL)) {
            commenter = "<a target=\"_blank\" " + "href=\"" + commentURL
                        + "\">" + commentName + "</a>";
        } else {
            commenter = commentName;
        }

        mailBody = mailBody.replace(
                "{articleOrPageURL}", articleOrPageURL).
                replace("{title}", title).
                replace("{commentContent}", commentContent).
                replace("{commentSharpURL}", blogHost + commentSharpURL).
                replace("{commenter}", commenter);
        message.setHtmlBody(mailBody);
        LOGGER.log(Level.FINER,
                   "Sending a mail[mailSubject={0}, mailBody=[{1}] to admins",
                   new Object[]{mailSubject, mailBody});
        mailService.sendToAdmins(message);
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

        // Try to set thumbnail URL using Google Buzz API
        if ("gmail.com".equals(domain.toLowerCase())) {
            final URL googleProfileURL =
                    new URL(Google.GOOGLE_PROFILE_RETRIEVAL.replace("{userId}",
                                                                    id));
            try {
                final HTTPResponse response =
                        urlFetchService.fetch(googleProfileURL);
                final int statusCode = response.getResponseCode();

                if (HttpServletResponse.SC_OK == statusCode) {
                    final byte[] content = response.getContent();
                    final String profileJSONString =
                            new String(content, "UTF-8");
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
            } catch (final Exception e) {
                LOGGER.log(Level.WARNING,
                           "Can not fetch google profile[userId=" + id + "", e);
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
}
