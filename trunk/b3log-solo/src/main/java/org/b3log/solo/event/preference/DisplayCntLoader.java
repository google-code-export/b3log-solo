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
package org.b3log.solo.event.preference;

import com.google.inject.Inject;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.latke.event.AbstractEventListener;
import org.b3log.latke.event.Event;
import org.b3log.latke.event.EventException;
import org.b3log.latke.event.EventManager;
import org.b3log.solo.event.EventTypes;
import org.b3log.solo.model.Preference;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This listener is responsible for preference 
 * {@linkplain Preference#RELEVANT_ARTICLES_DISPLAY_CNT relevant articles
 * display count},
 * {@linkplain Preference#RANDOM_ARTICLES_DISPLAY_CNT random articles display
 * count} and
 * {@linkplain Preference#EXTERNAL_RELEVANT_ARTICLES_DISPLAY_CNT random articles display
 * count} load process.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Oct 18, 2010
 */
public final class DisplayCntLoader
        extends AbstractEventListener<JSONObject> {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(DisplayCntLoader.class.getName());
    /**
     * Default relevant articles display count.
     */
    private static final int DEFAULT_RELEVANT_ARTICLES_DISPLAY_COUNT = 5;
    /**
     * Default random articles display count.
     */
    private static final int DEFAULT_RANDOM_ARTICLES_DISPLAY_COUNT = 5;
    /**
     * Default external relevant articles display count.
     */
    private static final int DEFAULT_EXTERNAL_RELEVANT_ARTICLES_DISPLAY_COUNT =
            5;

    /**
     * Constructs a {@link DisplayCntLoader} object with the specified event
     * manager.
     *
     * @param eventManager the specified event manager
     */
    @Inject
    public DisplayCntLoader(final EventManager eventManager) {
        super(eventManager);
    }

    @Override
    public void action(final Event<JSONObject> event) throws EventException {
        final JSONObject preference = event.getData();
        LOGGER.log(Level.FINER,
                   "Processing an event[type={0}, data={1}] in listener[className={2}]",
                   new Object[]{event.getType(),
                                preference,
                                DisplayCntLoader.class.getName()});
        try {
            if (!preference.has(Preference.RELEVANT_ARTICLES_DISPLAY_CNT)) {
                preference.put(Preference.RELEVANT_ARTICLES_DISPLAY_CNT,
                               DEFAULT_RELEVANT_ARTICLES_DISPLAY_COUNT);
            }
            if (!preference.has(Preference.RANDOM_ARTICLES_DISPLAY_CNT)) {
                preference.put(Preference.RANDOM_ARTICLES_DISPLAY_CNT,
                               DEFAULT_RANDOM_ARTICLES_DISPLAY_COUNT);
            }
            if (!preference.has(
                    Preference.EXTERNAL_RELEVANT_ARTICLES_DISPLAY_CNT)) {
                preference.put(Preference.EXTERNAL_RELEVANT_ARTICLES_DISPLAY_CNT,
                               DEFAULT_EXTERNAL_RELEVANT_ARTICLES_DISPLAY_COUNT);
            }
        } catch (final JSONException e) {
            LOGGER.severe(e.getMessage());
            throw new EventException("Load HTML head error!");
        }
    }

    /**
     * Gets the event type {@linkplain EventTypes#PREFERENCE_LOAD}.
     *
     * @return event type
     */
    @Override
    public String getEventType() {
        return EventTypes.PREFERENCE_LOAD;
    }
}
