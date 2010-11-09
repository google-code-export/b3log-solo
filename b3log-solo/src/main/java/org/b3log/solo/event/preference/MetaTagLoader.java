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
 * This listener is responsible for preference HTML head meta tags load process.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Oct 26, 2010
 */
public final class MetaTagLoader
        extends AbstractEventListener<JSONObject> {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(MetaTagLoader.class.getName());
    /**
     * Default meta keywords..
     */
    private static final String DEFAULT_META_KEYWORDS = 
            "GAE 博客,GAE blog,b3log solo,b3log rhythm,b3log";
    /**
     * Default meta description..
     */
    private static final String DEFAULT_META_DESCRIPTION = 
            "An open source blog based on GAE. 基于 GAE 的开源博客。";

    /**
     * Constructs a {@link MetaTagLoader} object with the specified event
     * manager.
     *
     * @param eventManager the specified event manager
     */
    @Inject
    public MetaTagLoader(final EventManager eventManager) {
        super(eventManager);
    }

    @Override
    public void action(final Event<JSONObject> event) throws EventException {
        final JSONObject preference = event.getData();
        LOGGER.log(Level.FINER,
                   "Processing an event[type={0}, data={1}] in listener[className={2}]",
                   new Object[]{event.getType(),
                                preference,
                                MetaTagLoader.class.getName()});
        try {
            if (!preference.has(Preference.META_KEYWORDS)) {
                preference.put(Preference.META_KEYWORDS, DEFAULT_META_KEYWORDS);
            }

            if (!preference.has(Preference.META_DESCRIPTION)) {
                preference.put(Preference.META_DESCRIPTION,
                               DEFAULT_META_DESCRIPTION);
            }
        } catch (final JSONException e) {
            LOGGER.severe(e.getMessage());
            throw new EventException("Load meta tags error!");
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
