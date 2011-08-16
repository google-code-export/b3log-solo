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
package org.b3log.solo.util;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.mail.MailService;
import org.b3log.latke.mail.MailService.Message;
import org.b3log.latke.mail.MailServiceFactory;
import org.b3log.latke.util.Strings;
import org.b3log.solo.SoloServletListener;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.Comment;
import org.b3log.solo.model.Page;
import org.b3log.solo.model.Preference;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Comment utilities.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.5, Aug 16, 2011
 */
public final class Comments {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(Comments.class.getName());
    /**
     * Mail service.
     */
    private static final MailService MAIL_SVC =
            MailServiceFactory.getMailService();
    /**
     * Comment mail HTML body.
     */
    public static final String COMMENT_MAIL_HTML_BODY =
            "<p>{articleOrPage} [<a href=\"" + "{articleOrPageURL}\">"
            + "{title}</a>]" + " received a new comment:</p>"
            + "{commenter}: <span><a href=\"http://{commentSharpURL}\">"
            + "{commentContent}</a></span>";

    /**
     * Sends a notification mail to administrator for notifying the specified
     * article or page received the specified comment and original comment.
     *
     * @param articleOrPage the specified article or page
     * @param comment the specified comment
     * @param originalComment original comment, if not exists, set it as
     * {@code null}
     * @param preference the specified preference
     * @throws IOException io exception
     * @throws JSONException json exception
     */
    public static void sendNotificationMail(final JSONObject articleOrPage,
                                            final JSONObject comment,
                                            final JSONObject originalComment,
                                            final JSONObject preference)
            throws IOException, JSONException {
        final String commentEmail = comment.getString(Comment.COMMENT_EMAIL);
        final String commentId = comment.getString(Keys.OBJECT_ID);
        final String commentContent = comment.getString(Comment.COMMENT_CONTENT).
                replaceAll(SoloServletListener.ENTER_ESC, "<br/>");

        final String adminEmail = preference.getString(Preference.ADMIN_EMAIL);
        if (adminEmail.equalsIgnoreCase(commentEmail)) {
            LOGGER.log(Level.FINER,
                       "Do not send comment notification mail to admin itself[{0}]",
                       adminEmail);
            return;
        }

        if (comment.has(Comment.COMMENT_ORIGINAL_COMMENT_ID)) {
            final String originalEmail =
                    originalComment.getString(Comment.COMMENT_EMAIL);
            if (originalEmail.equalsIgnoreCase(adminEmail)) {
                LOGGER.log(Level.FINER,
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
        message.setFrom(adminEmail);
        message.addRecipient(adminEmail);
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
            articleOrPageURL = "http://" + blogHost + articleOrPage.getString(
                    Page.PAGE_PERMALINK);
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
                   "Sending a mail[mailSubject={0}, mailBody=[{1}] to admin[email={2}]",
                   new Object[]{mailSubject, mailBody, adminEmail});
        MAIL_SVC.send(message);
    }

    // XXX: remove this unused class?
    /**
     * Gets the {@link Comments} singleton.
     *
     * @return the singleton
     */
    public static Comments getInstance() {
        return SingletonHolder.SINGLETON;
    }

    /**
     * Private default constructor.
     */
    private Comments() {
    }

    /**
     * Singleton holder.
     *
     * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
     * @version 1.0.0.0, Jan 12, 2011
     */
    private static final class SingletonHolder {

        /**
         * Singleton.
         */
        private static final Comments SINGLETON = new Comments();

        /**
         * Private default constructor.
         */
        private SingletonHolder() {
        }
    }
}
