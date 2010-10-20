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
package org.b3log.solo.event.upgrader;

import com.google.appengine.api.datastore.Transaction;
import com.google.inject.Inject;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.event.AbstractEventListener;
import org.b3log.latke.event.Event;
import org.b3log.latke.event.EventException;
import org.b3log.latke.event.EventManager;
import org.b3log.latke.repository.gae.AbstractGAERepository;
import org.b3log.solo.event.EventTypes;
import org.b3log.solo.model.Article;
import org.b3log.solo.repository.ArticleRepository;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * This listener is responsible for upgrade from v010.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Oct 20, 2010
 */
public final class V010Upgrader
        extends AbstractEventListener<JSONObject> {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(V010Upgrader.class.getName());
    /**
     * Article repository.
     */
    @Inject
    private ArticleRepository articleRepository;

    /**
     * Constructs a {@link V010Upgrader} object with the specified event
     * manager.
     *
     * @param eventManager the specified event manager
     */
    @Inject
    public V010Upgrader(final EventManager eventManager) {
        super(eventManager);
    }

    @Override
    public void action(final Event<JSONObject> event) throws EventException {
        final JSONObject data = event.getData();
        LOGGER.log(Level.FINER,
                   "Processing an event[type={0}, data={1}] in listener[className={2}]",
                   new Object[]{event.getType(),
                                data,
                                V010Upgrader.class.getName()});

        final Transaction transaction =
                AbstractGAERepository.DATASTORE_SERVICE.beginTransaction();
        try {
            final JSONObject result = articleRepository.get(1, 1);

            boolean articleHasPutTopProperty = false;
            if (result.has(Keys.RESULTS)) {
                final JSONArray articles = result.getJSONArray(Keys.RESULTS);
                final JSONObject article = articles.getJSONObject(0);
                if (article.has(Article.ARTICLE_PUT_TOP)) {
                    articleHasPutTopProperty = true;
                }
            }

            if (!articleHasPutTopProperty) {
                final long count = articleRepository.count();
                final List<JSONObject> articles =
                        articleRepository.getRecentArticles((int) count);

                for (final JSONObject article : articles) {
                    article.put(Article.ARTICLE_PUT_TOP, false);
                    final String articleId = article.getString(Keys.OBJECT_ID);
                    articleRepository.update(articleId, article);
                    LOGGER.log(Level.INFO, "Updated article[oId={0}]", articleId);
                }
            }

            transaction.commit();
        } catch (final Exception e) {
            transaction.rollback();
            LOGGER.severe(e.getMessage());
            throw new EventException("Upgrade fail from v010 to v011");
        }
    }

    /**
     * Gets the event type {@linkplain EventTypes#UPGRADE}.
     *
     * @return event type
     */
    @Override
    public String getEventType() {
        return EventTypes.UPGRADE;
    }
}
