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
package org.b3log.solo.event.listener.comment;

import com.google.appengine.api.mail.MailService;
import com.google.appengine.api.mail.MailService.Message;
import com.google.appengine.api.mail.MailServiceFactory;
import com.google.inject.Inject;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.latke.event.AbstractEventListener;
import org.b3log.latke.event.Event;
import org.b3log.latke.event.EventException;
import org.b3log.latke.event.EventManager;
import org.b3log.latke.util.Strings;
import org.b3log.solo.event.EventTypes;
import org.b3log.solo.model.Comment;
import org.b3log.solo.model.Preference;
import org.b3log.solo.repository.CommentRepository;
import org.b3log.solo.servlet.SoloServletListener;
import org.json.JSONObject;

/**
 * This listener is responsible for processing comment reply.
 *
 * <ol>
 *   <li>Extracts original comment committers</li>
 *   <li>Sends mails to these committers</li>
 * </ol>
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Sep 11, 2010
 */
public final class ReplyProcessor
        extends AbstractEventListener<JSONObject> {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(ReplyProcessor.class.getName());
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
     * Constructs a {@link ReplyProcessor} object with the specified event
     * manager.
     *
     * @param eventManager the specified event manager
     */
    @Inject
    public ReplyProcessor(final EventManager eventManager) {
        super(eventManager);
    }

    @Override
    public void action(final Event<JSONObject> event) throws EventException {
        final JSONObject comment = event.getData();
        final String originalCommentId =
                comment.optString(Comment.COMMENT_ORIGINAL_COMMENT_ID);
        if (Strings.isEmptyOrNull(originalCommentId)) {
            return;
        }

        LOGGER.log(Level.INFO,
                   "Processing an event[type={0}, data={1}] in listener[className={2}]",
                   new Object[]{event.getType(),
                                comment,
                                ReplyProcessor.class.getName()});
        try {
            final JSONObject preference =
                    SoloServletListener.getUserPreference();
            final JSONObject originalComment =
                    commentRepository.get(originalCommentId);
            final String originalCommentEmail =
                    originalComment.getString(Comment.COMMENT_EMAIL);
            final String commentEmail = comment.getString(Comment.COMMENT_EMAIL);
            final String commentContent =
                    comment.getString(Comment.COMMENT_CONTENT);
            final String commentSharpURL =
                    comment.getString(Comment.COMMENT_SHARP_URL);
            final Message message = new Message();
            message.setSender(commentEmail);
            final String mailSubject = "New reply of your comment from Solo "
                                       + "blog[" + preference.getString(
                    Preference.BLOG_TITLE) + "]";
            message.setSubject(mailSubject);
            final String mailBody = commentContent + "\r\n"
                                    + "See <a href=" + commentSharpURL
                                    + ">here</a> for more"
                                    + "for original post.";
            message.setHtmlBody(mailBody);
            LOGGER.log(Level.FINER,
                       "Sending a mail[mailSubject={0}, mailBody=[{1}] to [{2}]",
                       new Object[]{mailSubject, mailBody,
                                    originalCommentEmail});
            mailService.sendToAdmins(message);

        } catch (final Exception e) {
            LOGGER.severe(e.getMessage());
            throw new EventException("Load notice board error!");
        }
    }

    /**
     * Gets the event type {@linkplain EventTypes#ADD_COMMENT}.
     *
     * @return event type
     */
    @Override
    public String getEventType() {
        return EventTypes.ADD_COMMENT;
    }
}
