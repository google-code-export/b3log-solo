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

import com.google.inject.Inject;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import oauth.signpost.OAuthConsumer;
import org.b3log.latke.event.AbstractEventListener;
import org.b3log.latke.event.Event;
import org.b3log.latke.event.EventException;
import org.b3log.latke.event.EventManager;
import org.b3log.solo.event.EventTypes;
import org.b3log.solo.jsonrpc.impl.PreferenceService;
import org.b3log.solo.model.Preference;
import org.b3log.solo.servlet.SoloServletListener;
import org.json.JSONObject;

/**
 * This listener is responsible for creating a new activity to Google buzz while
 * adding an article.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Sep 14, 2010
 */
public final class ActivityCreator
        extends AbstractEventListener<JSONObject> {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(ActivityCreator.class.getName());
    /**
     * Preference service.
     */
    @Inject
    private PreferenceService preferenceService;

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
            final boolean postToBuzzEnabled =
                    preference.getBoolean(Preference.ENABLE_POST_TO_BUZZ);
            if (!postToBuzzEnabled) {
                return;
            }

//            BuzzOAuth.authorize();
            final URL url =
                    new URL(
                    "https://www.googleapis.com/buzz/v1/activities/@me/@self?alt=json");

            final HttpURLConnection httpURLConnection =
                    (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);

            final JSONObject post = new JSONObject();
            final JSONObject data = new JSONObject();
            post.put("data", data);
            final JSONObject object = new JSONObject();
            data.put("object", object);
            object.put("type", "note");
            object.put("content", "测试 sync of B3log Solo 2 Google Buzz");

            final OAuthConsumer buzzOAuthConsumer =
                    preferenceService.getBuzzOAuthConsumer();
            buzzOAuthConsumer.sign(httpURLConnection);

            final OutputStream outputStream =
                    httpURLConnection.getOutputStream();
            outputStream.write(post.toString().getBytes());
            outputStream.close();

            LOGGER.log(Level.INFO, "Response: {0} {1}",
                       new Object[]{httpURLConnection.getResponseCode(),
                                    httpURLConnection.getResponseMessage()});
            httpURLConnection.disconnect();
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new EventException(
                    "Send article creation buzz activity to Google Buzz error");
        }
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
