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
import org.b3log.latke.event.AbstractEventListener;
import org.b3log.latke.event.Event;
import org.b3log.latke.event.EventManager;
import org.json.JSONObject;

/**
 * This listener is responsible for handling tag-article relation.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Aug 12, 2010
 */
public class TagArticleRelationProcessor extends AbstractEventListener<JSONObject> {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(TagArticleRelationProcessor.class);

    /**
     * Constructs a {@link TagArticleRelationProcessor} object with the specified
     * event manager.
     *
     * @param eventManager the specified event manager
     */
    @Inject
    public TagArticleRelationProcessor(final EventManager eventManager) {
        super(eventManager);
    }

    @Override
    public void action(final Event<JSONObject> event) {
        final JSONObject article = event.getData();
        LOGGER.trace("Processing an event[type=" + event.getType()
                     + ", data=" + article + "] in listener[className="
                     + TagArticleRelationProcessor.class.getName() + "]");

    }
}
