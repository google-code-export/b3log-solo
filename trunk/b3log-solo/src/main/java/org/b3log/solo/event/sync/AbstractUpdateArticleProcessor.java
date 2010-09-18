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
import org.b3log.solo.model.Article;
import static org.b3log.solo.model.BlogSync.*;
import org.b3log.solo.repository.BlogSyncManagementRepository;
import org.b3log.solo.repository.ExternalArticleSoloArticleRepository;
import org.b3log.solo.servlet.SoloServletListener;
import org.b3log.solo.sync.BlogFactory;
import org.b3log.solo.sync.MetaWeblog;
import org.b3log.solo.sync.MetaWeblogPost;
import org.b3log.solo.sync.Post;
import org.json.JSONObject;

/**
 * This listener is responsible for blog sync update article to external blogging
 * system.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.2, Sep 4, 2010
 */
public abstract class AbstractUpdateArticleProcessor
        extends AbstractEventListener<JSONObject> {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(AbstractUpdateArticleProcessor.class.getName());
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
     * Constructs a {@link BlogSyncMgmtUpdateArticleProcessor} object with the
     * specified event manager.
     *
     * @param eventManager the specified event manager
     */
    @Inject
    public AbstractUpdateArticleProcessor(final EventManager eventManager) {
        super(eventManager);
    }

    @Override
    public void action(final Event<JSONObject> event) throws EventException {
        final JSONObject article = event.getData();
        LOGGER.log(Level.FINER,
                   "Processing an event[type={0}, data={1}] in listener[className={2}]",
                   new Object[]{event.getType(),
                                article,
                                AbstractUpdateArticleProcessor.class.getName()});

        final String externalBloggingSys = getExternalBloggingSys();
        try {
            final JSONObject blogSyncMgmt =
                    blogSyncManagementRepository.getByExternalBloggingSystem(
                    externalBloggingSys);
            if (null == blogSyncMgmt) {
                LOGGER.log(Level.FINER,
                           "Not found syn management settings for external blogging system[{0}]",
                           externalBloggingSys);
                return;
            }

            LOGGER.log(Level.FINER,
                       "Got a blog sync management setting[{0}]",
                       blogSyncMgmt.toString(
                    SoloServletListener.JSON_PRINT_INDENT_FACTOR));
            if (!blogSyncMgmt.getBoolean(
                    BLOG_SYNC_MGMT_UPDATE_ENABLED)) {
                LOGGER.log(Level.INFO,
                           "External blogging system[{0}] need NOT to syn update article",
                           externalBloggingSys);
            } else {
                final String userName = blogSyncMgmt.getString(
                        BLOG_SYNC_EXTERNAL_BLOGGING_SYS_USER_NAME);
                final String userPwd = blogSyncMgmt.getString(
                        BLOG_SYNC_EXTERNAL_BLOGGING_SYS_USER_PASSWORD);
                final Post externalArticle = new MetaWeblogPost(article);
                final String articleId = article.getString(Keys.OBJECT_ID);
                LOGGER.log(Level.FINEST,
                           "Getting External article-Solo article relation....");
                final JSONObject externalArticleSoloArticleRelation =
                        externalArticleSoloArticleRepository.getBySoloArticleId(
                        articleId, externalBloggingSys);
                final String postId = externalArticleSoloArticleRelation.
                        getString(BLOG_SYNC_EXTERNAL_ARTICLE_ID);
                final MetaWeblog metaWeblog =
                        BlogFactory.getMetaWeblog(externalBloggingSys);
                metaWeblog.setUserName(userName);
                metaWeblog.setUserPassword(userPwd);
                metaWeblog.editPost(postId, externalArticle);

                externalArticleSoloArticleRelation.put(
                        BLOG_SYNC_EXTERNAL_ARTICLE_ABSTRACT,
                        article.getString(Article.ARTICLE_ABSTRACT));
                externalArticleSoloArticleRelation.put(
                        BLOG_SYNC_EXTERNAL_ARTICLE_CATEGORIES,
                        article.getString(Article.ARTICLE_TAGS_REF));
                externalArticleSoloArticleRelation.put(
                        BLOG_SYNC_EXTERNAL_ARTICLE_CONTENT,
                        article.getString(Article.ARTICLE_CONTENT));
                externalArticleSoloArticleRelation.put(
                        BLOG_SYNC_EXTERNAL_ARTICLE_CREATE_DATE,
                        article.get(Article.ARTICLE_CREATE_DATE));
                externalArticleSoloArticleRelation.put(
                        BLOG_SYNC_EXTERNAL_ARTICLE_TITLE,
                        article.getString(Article.ARTICLE_TITLE));
                externalArticleSoloArticleRepository.update(
                        externalArticleSoloArticleRelation.getString(
                        Keys.OBJECT_ID), externalArticleSoloArticleRelation);
            }
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
    public final String getEventType() {
        return EventTypes.UPDATE_ARTICLE;
    }

    /**
     * Gets the external blogging system name.
     *
     * @return external blogging system name
     * @see org.b3log.solo.servlet.SoloServletListener#SUPPORTED_BLOG_SYNC_MGMT_EXTERNAL_BLOGGING_SYSTEMS
     */
    public abstract String getExternalBloggingSys();
}
