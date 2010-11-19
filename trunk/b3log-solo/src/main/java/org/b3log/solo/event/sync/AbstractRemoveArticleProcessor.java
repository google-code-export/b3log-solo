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
package org.b3log.solo.event.sync;

import com.google.inject.Inject;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.event.AbstractEventListener;
import org.b3log.latke.event.Event;
import org.b3log.latke.event.EventException;
import org.b3log.latke.event.EventManager;
import org.b3log.solo.event.EventTypes;
import static org.b3log.solo.model.BlogSync.*;
import org.b3log.solo.repository.BlogSyncManagementRepository;
import org.b3log.solo.repository.ExternalArticleSoloArticleRepository;
import org.b3log.solo.SoloServletListener;
import org.b3log.solo.sync.BlogFactory;
import org.b3log.solo.sync.MetaWeblog;
import org.b3log.solo.sync.SyncException;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This listener is responsible for blog sync remove article from external blogging
 * system.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.6, Nov 19, 2010
 */
public abstract class AbstractRemoveArticleProcessor
        extends AbstractEventListener<JSONObject> {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(AbstractRemoveArticleProcessor.class.getName());
    /**
     * Blog sync management repository.
     */
    @Inject
    private BlogSyncManagementRepository blogSyncManagementRepository;
    /**
     * External blog article-Solo article repository.
     */
    @Inject
    private ExternalArticleSoloArticleRepository externalArticleSoloArticleRepository;

    /**
     * Constructs a {@link BlogSyncMgmtRemoveArticleProcessor} object with the
     * specified event manager.
     *
     * @param eventManager the specified event manager
     */
    @Inject
    public AbstractRemoveArticleProcessor(final EventManager eventManager) {
        super(eventManager);
    }

    /**
     * Removes articles with the specified event.
     *
     * @param event the specified event
     * @return event result, for example,
     * <pre>
     * {
     *     "sc": "BLOG_SYNC_SUCC"
     * }
     * </pre>
     * @throws EventException event exception
     * @throws SyncException sync exception
     */
    protected final JSONObject removeArticle(final Event<JSONObject> event)
            throws EventException, SyncException {
        final JSONObject eventData = event.getData();

        String articleId = null;
        try {
            articleId = eventData.getString(Keys.OBJECT_ID);

            LOGGER.log(Level.FINER,
                       "Processing an event[type={0}, data={1}] in listener[className={2}]",
                       new Object[]{event.getType(),
                                    articleId,
                                    AbstractAddArticleProcessor.class.getName()});
        } catch (final JSONException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new EventException(e);
        }

        try {
            final JSONObject ret = new JSONObject();
            final String externalBloggingSys = getExternalBloggingSys();
            final JSONObject blogSyncMgmt =
                    blogSyncManagementRepository.getByExternalBloggingSystem(
                    externalBloggingSys);
            if (null == blogSyncMgmt) {
                LOGGER.log(Level.FINER,
                           "Not found syn management settings for external blogging system[{0}]",
                           externalBloggingSys);
                ret.put(Keys.STATUS_CODE,
                        BlogSyncStatusCodes.BLOG_SYNC_NO_NEED_TO_SYNC);

                return ret;
            }

            LOGGER.log(Level.FINER,
                       "Got a blog sync management setting[{0}]",
                       blogSyncMgmt.toString(
                    SoloServletListener.JSON_PRINT_INDENT_FACTOR));
            if (!blogSyncMgmt.getBoolean(
                    BLOG_SYNC_MGMT_REMOVE_ENABLED)) {
                LOGGER.log(Level.INFO,
                           "External blogging system[{0}] need NOT to syn remove article",
                           externalBloggingSys);
            } else {
                final String userName = blogSyncMgmt.getString(
                        BLOG_SYNC_EXTERNAL_BLOGGING_SYS_USER_NAME);
                final String userPwd = blogSyncMgmt.getString(
                        BLOG_SYNC_EXTERNAL_BLOGGING_SYS_USER_PASSWORD);
                final JSONObject externalArticleSoloArticleRelation =
                        externalArticleSoloArticleRepository.getBySoloArticleId(
                        articleId, externalBloggingSys);
                if (null == externalArticleSoloArticleRelation) {
                    // This article published without sync adding enabled.
                    // See issue 72 for details
                    ret.put(Keys.STATUS_CODE, BlogSyncStatusCodes.BLOG_SYNC_FAIL);

                    return ret;
                }

                final String externalArticleId =
                        externalArticleSoloArticleRelation.getString(
                        BLOG_SYNC_EXTERNAL_ARTICLE_ID);
                final MetaWeblog metaWeblog = BlogFactory.getMetaWeblog(
                        externalBloggingSys);
                metaWeblog.setUserName(userName);
                metaWeblog.setUserPassword(userPwd);
                metaWeblog.deletePost(externalArticleId);

                final String relationId =
                        externalArticleSoloArticleRelation.getString(
                        Keys.OBJECT_ID);
                externalArticleSoloArticleRepository.remove(relationId);
            }

            ret.put(Keys.STATUS_CODE, BlogSyncStatusCodes.BLOG_SYNC_SUCC);

            return ret;
        } catch (final SyncException e) {
            LOGGER.log(Level.WARNING,
                       "Can not remove article sync, error msg[{0}]",
                       e.getMessage());
            throw e;
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, "Can not handle event[{0}], error msg[{1}]",
                       new String[]{getEventType(), e.getMessage()});
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
    public final String getEventType() {
        return EventTypes.REMOVE_ARTICLE;
    }

    /**
     * Gets the external blogging system name.
     *
     * @return external blogging system name
     * @see org.b3log.solo.servlet.SoloServletListener#SUPPORTED_BLOG_SYNC_MGMT_EXTERNAL_BLOGGING_SYSTEMS
     */
    public abstract String getExternalBloggingSys();
}
