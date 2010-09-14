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
package org.b3log.solo.event.listener.buzz;

import com.google.api.client.googleapis.GoogleTransport;
import com.google.api.client.googleapis.json.JsonCParser;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.inject.Inject;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.latke.event.AbstractEventListener;
import org.b3log.latke.event.Event;
import org.b3log.latke.event.EventException;
import org.b3log.latke.event.EventManager;
import org.b3log.solo.event.EventTypes;
import org.b3log.solo.google.auth.OAuth;
import org.b3log.solo.google.buzz.BuzzActivity;
import org.b3log.solo.google.buzz.BuzzObject;
import org.b3log.solo.model.Preference;
import org.b3log.solo.servlet.SoloServletListener;
import org.json.JSONObject;

/**
 * This listener is responsible for creating a new activity to Google buzz while
 * adding an article.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Sep 13, 2010
 */
public final class ActivityCreator
        extends AbstractEventListener<JSONObject> {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(ActivityCreator.class.getName());

    /**
     * Constructs a {@link ActivityCreator} object with the specified event
     * manager.
     *
     * @param eventManager the specified event manager
     */
    @Inject
    public ActivityCreator(final EventManager eventManager) {
        super(eventManager);
    }

    @Override
    public void action(final Event<JSONObject> event) throws EventException {
        final JSONObject article = event.getData();
        LOGGER.log(Level.INFO,
                   "Processing an event[type={0}, data={1}] in listener[className={2}]",
                   new Object[]{event.getType(),
                                article,
                                ActivityCreator.class.getName()});

        try {
            final JSONObject preference =
                    SoloServletListener.getUserPreference();
            final String secret =
                    preference.getString(Preference.GOOLE_OAUTH_CONSUMER_SECRET);
            final boolean postToBuzzEnabled =
                    preference.getBoolean(Preference.GOOGLE_ENABLE_POST_TO_BUZZ);
            if (!postToBuzzEnabled) {
                return;
            }

            final HttpTransport httpTransport = GoogleTransport.create();
            httpTransport.addParser(new JsonCParser());
            try {
                OAuth.authorize(httpTransport);
                final BuzzActivity activity = addActivity(httpTransport);
                OAuth.revoke();
            } catch (final HttpResponseException e) {
                LOGGER.log(Level.SEVERE, e.response.parseAsString(), e);
                throw e;
            }
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new EventException(
                    "Send article creation buzz activity to Google Buzz error");
        }
    }

    /**
     * Adds a buzz activity with the specified http transport.
     *
     * @param httpTransport the specified http transport
     * @return the activity added
     * @throws IOException io exception
     */
    private BuzzActivity addActivity(final HttpTransport httpTransport)
            throws IOException {
        final BuzzActivity activity = new BuzzActivity();
        activity.setBuzzObject(new BuzzObject());
        activity.getBuzzObject().setContent("Posting using B3log Solo");
        final BuzzActivity result = activity.post(httpTransport);

        return result;
    }

    /**
     * Gets the event type {@linkplain EventTypes#ADD_COMMENT}.
     *
     * @return event type
     */
    @Override
    public String getEventType() {
        return EventTypes.ADD_ARTICLE;
    }
}
