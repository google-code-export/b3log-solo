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
package org.b3log.solo.event.article;

import com.google.inject.Inject;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.event.AbstractEventListener;
import org.b3log.latke.event.Event;
import org.b3log.latke.event.EventException;
import org.b3log.latke.event.EventManager;
import org.b3log.solo.event.EventTypes;
import org.b3log.solo.repository.TopArticleRepository;
import org.json.JSONObject;

/**
 * This listener is responsible for processing article remove.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Oct 20, 2010
 */
public final class TopArticleRemover
        extends AbstractEventListener<JSONObject> {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(TopArticleRemover.class.getName());
    /**
     * Top article repository.
     */
    @Inject
    private TopArticleRepository topArticleRepository;

    /**
     * Constructs a {@link TopArticleRemover} object with the specified event
     * manager.
     *
     * @param eventManager the specified event manager
     */
    @Inject
    public TopArticleRemover(final EventManager eventManager) {
        super(eventManager);
    }

    @Override
    public void action(final Event<JSONObject> event) throws EventException {
        final JSONObject eventData = event.getData();
        LOGGER.log(Level.FINER,
                   "Processing an event[type={0}, data={1}] in listener[className={2}]",
                   new Object[]{event.getType(),
                                eventData,
                                TopArticleRemover.class.getName()});
        try {
            final String articleId = eventData.getString(Keys.OBJECT_ID);
            topArticleRepository.remove(articleId);
        } catch (final Exception e) {
            LOGGER.severe(e.getMessage());
            throw new EventException("Remove top article error!");
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
