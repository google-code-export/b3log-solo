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
package org.b3log.solo.event.listener.impl;

import com.google.inject.Inject;
import org.apache.log4j.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.event.AbstractEventListener;
import org.b3log.latke.event.Event;
import org.b3log.latke.event.EventException;
import org.b3log.latke.event.EventManager;
import org.b3log.solo.event.EventTypes;
import static org.b3log.solo.model.BlogSync.*;
import org.b3log.solo.repository.BlogSyncManagementRepository;
import org.b3log.solo.repository.CSDNBlogArticleSoloArticleRepository;
import org.b3log.solo.servlet.SoloServletListener;
import org.b3log.solo.sync.csdn.blog.CSDNBlog;
import org.json.JSONObject;

/**
 * This listener is responsible for blog sync remove article to external blogging
 * system.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Aug 27, 2010
 */
public final class BlogSyncMgmtRemoveArticleProcessor
        extends AbstractEventListener<String> {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(BlogSyncMgmtRemoveArticleProcessor.class);
    /**
     * Blog sync management repository.
     */
    @Inject
    private BlogSyncManagementRepository blogSyncManagementRepository;
    /**
     * CSDN blog.
     */
    @Inject
    private CSDNBlog csdnBlog;
    /**
     * CSDN blog article-Solo article repository.
     */
    @Inject
    private CSDNBlogArticleSoloArticleRepository csdnBlogArticleSoloArticleRepository;

    /**
     * Constructs a {@link BlogSyncMgmtRemoveArticleProcessor} object with the
     * specified event manager.
     *
     * @param eventManager the specified event manager
     */
    @Inject
    public BlogSyncMgmtRemoveArticleProcessor(final EventManager eventManager) {
        super(EventTypes.REMOVE_ARTICLE, eventManager);
    }

    @Override
    public void action(final Event<String> event) throws EventException {
        final String articleId = event.getData();
        LOGGER.trace("Processing an event[type=" + event.getType()
                     + ", data=" + articleId + "] in listener[className="
                     + BlogSyncMgmtRemoveArticleProcessor.class.getName() + "]");

        final String[] knownExternalBloggingSystems =
                SoloServletListener.SUPPORTED_BLOG_SYNC_MGMT_EXTERNAL_BLOGGING_SYSTEMS;
        try {
            for (int i = 0; i < knownExternalBloggingSystems.length; i++) {
                final String knownExternalBloggingSys =
                        knownExternalBloggingSystems[i];
                final JSONObject blogSyncMgmt =
                        blogSyncManagementRepository.getByExternalBloggingSystem(
                        knownExternalBloggingSys);
                if (null == blogSyncMgmt) {
                    LOGGER.debug("Not found syn management settings for external "
                                 + "blogging system[" + knownExternalBloggingSys
                                 + "]");
                    continue;
                }

                LOGGER.debug("Got a blog sync management setting["
                             + blogSyncMgmt.toString(
                        SoloServletListener.JSON_PRINT_INDENT_FACTOR) + "]");
                if (!blogSyncMgmt.getBoolean(
                        BLOG_SYNC_MGMT_REMOVE_ENABLED)) {
                    LOGGER.info("External blogging system["
                                + knownExternalBloggingSys
                                + "] need NOT to syn remove article");
                } else {
                    // XXX: Design external blogging system interface
                    final String userName = blogSyncMgmt.getString(
                            BLOG_SYNC_EXTERNAL_BLOGGING_SYS_USER_NAME);
                    final String userPwd = blogSyncMgmt.getString(
                            BLOG_SYNC_EXTERNAL_BLOGGING_SYS_USER_PASSWORD);
                    final JSONObject csdnArticleSoloArticleRelation =
                            csdnBlogArticleSoloArticleRepository.
                            getBySoloArticleId(articleId);
                    if (null != csdnArticleSoloArticleRelation) {
                        final String csdnBlogArticleId =
                                csdnArticleSoloArticleRelation.getString(
                                BLOG_SYNC_CSDN_BLOG_ARTICLE_ID);

                        csdnBlog.deletePost(userName, userPwd, csdnBlogArticleId);

                        final String relationId =
                                csdnArticleSoloArticleRelation.getString(
                                Keys.OBJECT_ID);
                        csdnBlogArticleSoloArticleRepository.remove(relationId);
                    }
                }
            }
        } catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);

            throw new EventException("Can not handle event[" + getEventType()
                                     + "]");
        }
    }

    /**
     * Gets the event type {@linkplain EventTypes#REMOVE_ARTICLE}.
     *
     * @return event type
     */
    @Override
    public String getEventType() {
        return EventTypes.REMOVE_ARTICLE;
    }
}
