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
package org.b3log.solo.event.comment;

import com.google.appengine.api.mail.MailService;
import com.google.appengine.api.mail.MailService.Message;
import com.google.appengine.api.mail.MailServiceFactory;
import com.google.inject.Inject;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.event.AbstractEventListener;
import org.b3log.latke.event.Event;
import org.b3log.latke.event.EventException;
import org.b3log.latke.event.EventManager;
import org.b3log.latke.util.Strings;
import org.b3log.solo.event.EventTypes;
import org.b3log.solo.model.Comment;
import org.b3log.solo.model.Preference;
import org.b3log.solo.repository.CommentRepository;
import org.b3log.solo.model.Page;
import org.b3log.solo.util.PreferenceUtils;
import org.json.JSONObject;

/**
 * This listener is responsible for processing page comment reply.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Oct 29, 2010
 */
public final class PageCommentReplyNotifier
        extends AbstractEventListener<JSONObject> {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(PageCommentReplyNotifier.class.getName());
    /**
     * Comment repository.
     */
    @Inject
    private CommentRepository commentRepository;
    /**
     * Mail service.
     */
    private MailService mailService =
            MailServiceFactory.getMailService();
    /**
     * Preference utilities.
     */
    @Inject
    private PreferenceUtils preferenceUtils;

    /**
     * Constructs a {@link ReplyProcessor} object with the specified event
     * manager.
     *
     * @param eventManager the specified event manager
     */
    @Inject
    public PageCommentReplyNotifier(final EventManager eventManager) {
        super(eventManager);
    }

    @Override
    public void action(final Event<JSONObject> event) throws EventException {
        final JSONObject eventData = event.getData();
        final JSONObject comment = eventData.optJSONObject(Comment.COMMENT);
        final JSONObject page = eventData.optJSONObject(Page.PAGE);
        LOGGER.log(Level.FINER,
                   "Processing an event[type={0}, data={1}] in listener[className={2}]",
                   new Object[]{event.getType(),
                                eventData,
                                PageCommentReplyNotifier.class.getName()});
        final String originalCommentId =
                comment.optString(Comment.COMMENT_ORIGINAL_COMMENT_ID);
        if (Strings.isEmptyOrNull(originalCommentId)) {
            LOGGER.log(Level.FINER, "This comment[id={0}] is not a reply",
                       comment.optString(Keys.OBJECT_ID));
            return;
        }

        try {
            final String commentEmail = comment.getString(Comment.COMMENT_EMAIL);
            final JSONObject originalComment =
                    commentRepository.get(originalCommentId);
            final String originalCommentEmail =
                    originalComment.getString(Comment.COMMENT_EMAIL);
            if (originalCommentEmail.equalsIgnoreCase(commentEmail)) {
                LOGGER.log(Level.FINE,
                           "Do not send reply notification mail to itself[{0}]",
                           originalCommentEmail);
                return;
            }

            final JSONObject preference = preferenceUtils.getPreference();
            final String blogTitle =
                    preference.getString(Preference.BLOG_TITLE);
            final String adminGmail =
                    preference.getString(Preference.ADMIN_GMAIL);

            final String commentContent =
                    comment.getString(Comment.COMMENT_CONTENT);
            final String commentSharpURL =
                    comment.getString(Comment.COMMENT_SHARP_URL);
            final Message message = new Message();
            message.setSender(adminGmail);
            message.setTo(originalCommentEmail);
            final String mailSubject = blogTitle + ": New reply of your comment";
            message.setSubject(mailSubject);
            final String pageTitle = page.getString(Page.PAGE_TITLE);
            final String blogHost = preference.getString(Preference.BLOG_HOST);
            final String pageId = page.getString(Keys.OBJECT_ID);
            final String pageLink = "http://" + blogHost + "/page.do?oId="
                                    + pageId;
            final String mailBody = "Your comment on page[<a href='"
                                    + pageLink + "'>" + pageTitle
                                    + "</a>] received an reply: <p>"
                                    + commentContent + "</p><p>"
                                    + "See <a href=" + commentSharpURL
                                    + ">here</a> for original post.</p>";
            message.setHtmlBody(mailBody);
            LOGGER.log(Level.FINER,
                       "Sending a mail[mailSubject={0}, mailBody=[{1}] to [{2}]",
                       new Object[]{mailSubject, mailBody,
                                    originalCommentEmail});
            mailService.send(message);
        } catch (final Exception e) {
            LOGGER.severe(e.getMessage());
            throw new EventException("Reply notifier error!");
        }
    }

    /**
     * Gets the event type {@linkplain EventTypes#ADD_COMMENT_TO_PAGE}.
     *
     * @return event type
     */
    @Override
    public String getEventType() {
        return EventTypes.ADD_COMMENT_TO_PAGE;
    }
}
