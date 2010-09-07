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
package org.b3log.solo.event.listener.preference;

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
 * This listener is responsible for preference load process.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Sep 7, 2010
 */
public final class NoticeBoardLoader
        extends AbstractEventListener<JSONObject> {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(NoticeBoardLoader.class.getName());
    /**
     * Default notice board.
     */
    private static final String DEFAULT_NOTICE_BOARD =
            "Open Source, Open Mind, <br/>Open Sight, Open Future!";

    /**
     * Constructs a {@link NoticeBoardLoader} object with the specified event
     * manager.
     *
     * @param eventManager the specified event manager
     */
    @Inject
    public NoticeBoardLoader(final EventManager eventManager) {
        super(eventManager);
    }

    @Override
    public void action(final Event<JSONObject> event) throws EventException {
        final JSONObject preference = event.getData();
        LOGGER.log(Level.INFO,
                   "Processing an event[type={0}, data={1}] in listener[className={2}]",
                   new Object[]{event.getType(),
                                preference,
                                NoticeBoardLoader.class.getName()});
        try {
            if (!preference.has(Preference.NOTICE_BOARD)) {
                preference.put(Preference.NOTICE_BOARD, DEFAULT_NOTICE_BOARD);
            }
        } catch (final JSONException e) {
            LOGGER.severe(e.getMessage());
            throw new EventException("Load notice board error!");
        }
    }

    /**
     * Gets the event type {@linkplain EventTypes#ADD_ARTICLE}.
     *
     * @return event type
     */
    @Override
    public String getEventType() {
        return EventTypes.PREFERENCE_LOAD;
    }
}
