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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.event.AbstractEventListener;
import org.b3log.latke.event.Event;
import org.b3log.latke.event.EventException;
import org.b3log.latke.event.EventManager;
import org.b3log.solo.event.EventTypes;
import org.b3log.solo.model.Article;
import static org.b3log.solo.model.BlogSync.*;
import org.b3log.solo.repository.BlogSyncManagementRepository;
import org.b3log.solo.repository.CSDNBlogArticleSoloArticleRepository;
import org.b3log.solo.servlet.SoloServletListener;
import org.b3log.solo.sync.csdn.blog.CSDNBlog;
import org.b3log.solo.sync.csdn.blog.CSDNBlogArticle;
import org.json.JSONObject;

/**
 * This listener is responsible for blog sync add article to external blogging
 * system.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.2, Sep 2, 2010
 */
public final class BlogSyncMgmtAddArticleProcessor
        extends AbstractEventListener<JSONObject> {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(BlogSyncMgmtAddArticleProcessor.class.getName());
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
     * Constructs a {@link BlogSyncMgmtAddArticleProcessor} object with the
     * specified event manager.
     *
     * @param eventManager the specified event manager
     */
    @Inject
    public BlogSyncMgmtAddArticleProcessor(final EventManager eventManager) {
        super(eventManager);
    }

    @Override
    public void action(final Event<JSONObject> event) throws EventException {
        final JSONObject article = event.getData();
        LOGGER.log(Level.FINEST,
                   "Processing an event[type={0}, data={1}] in listener[className={2}]",
                   new Object[]{event.getType(),
                                article,
                                BlogSyncMgmtAddArticleProcessor.class.getName()});

        final String[] knownExternalBloggingSystems =
                SoloServletListener.SUPPORTED_BLOG_SYNC_MGMT_EXTERNAL_BLOGGING_SYSTEMS;
        String csdnArticleId = null;

        String articleId = null;
        try {
            articleId = article.getString(Keys.OBJECT_ID);

            for (int i = 0; i < knownExternalBloggingSystems.length; i++) {
                final String knownExternalBloggingSys =
                        knownExternalBloggingSystems[i];
                final JSONObject blogSyncMgmt =
                        blogSyncManagementRepository.getByExternalBloggingSystem(
                        knownExternalBloggingSys);
                if (null == blogSyncMgmt) {
                    LOGGER.log(Level.FINER,
                               "Not found syn management settings for external blogging system[{0}]",
                               knownExternalBloggingSys);
                    continue;
                }

                LOGGER.log(Level.FINER,
                           "Got a blog sync management setting[{0}]",
                           blogSyncMgmt.toString(
                        SoloServletListener.JSON_PRINT_INDENT_FACTOR));
                if (!blogSyncMgmt.getBoolean(
                        BLOG_SYNC_MGMT_ADD_ENABLED)) {
                    LOGGER.log(Level.INFO,
                               "External blogging system[{0}] need NOT to syn add article",
                               knownExternalBloggingSys);
                } else {
                    // XXX: Design external blogging system interface
                    final String userName = blogSyncMgmt.getString(
                            BLOG_SYNC_EXTERNAL_BLOGGING_SYS_USER_NAME);
                    final String userPwd = blogSyncMgmt.getString(
                            BLOG_SYNC_EXTERNAL_BLOGGING_SYS_USER_PASSWORD);
                    final CSDNBlogArticle csdnBlogArticle =
                            new CSDNBlogArticle(article);
                    csdnArticleId =
                            csdnBlog.newPost(userName, userPwd, csdnBlogArticle);
                }
            }
        } catch (final Exception e) {
            LOGGER.severe(e.getMessage());

            throw new EventException("Can not handle event[" + getEventType()
                                     + "]");
        }

        try {
            final JSONObject csdnArticleSoloArticleRelation = new JSONObject();
            csdnArticleSoloArticleRelation.put(
                    BLOG_SYNC_CSDN_BLOG_ARTICLE_ID, csdnArticleId);
            csdnArticleSoloArticleRelation.put(Article.ARTICLE + "_"
                                               + Keys.OBJECT_ID, articleId);
            csdnBlogArticleSoloArticleRepository.add(
                    csdnArticleSoloArticleRelation);
            LOGGER.log(Level.FINER,
                       "Added CSDN blog article-solo article relation[{0}]",
                       csdnArticleSoloArticleRelation.toString());
        } catch (final Exception e) {
            LOGGER.severe(e.getMessage());
            throw new EventException("Can not handle event[" + getEventType()
                                     + "]");
        }
    }

    /**
     * Gets the event type {@linkplain EventTypes#ADD_ARTICLE}.
     *
     * @return event type
     */
    @Override
    public String getEventType() {
        return EventTypes.ADD_ARTICLE;
    }
}
