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
package org.b3log.solo.event.rhythm;

import com.google.appengine.api.urlfetch.HTTPMethod;
import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import com.google.inject.Inject;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.latke.event.AbstractEventListener;
import org.b3log.latke.event.Event;
import org.b3log.latke.event.EventException;
import org.b3log.latke.event.EventManager;
import org.b3log.solo.SoloServletListener;
import org.b3log.solo.event.EventTypes;
import org.json.JSONObject;

/**
 * This listener is responsible for sending articles to B3log Rhythm.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Oct 16, 2010
 */
public final class ArticleSender
        extends AbstractEventListener<JSONObject> {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(ArticleSender.class.getName());
    /**
     * URL fetch service.
     */
    private final URLFetchService urlFetchService =
            URLFetchServiceFactory.getURLFetchService();
    /**
     * Timeout.
     */
    private static final long TIMEOUT = 5000;

    /**
     * Constructs a {@link ArticleSender} object with the specified event
     * manager.
     *
     * @param eventManager the specified event manager
     */
    @Inject
    public ArticleSender(final EventManager eventManager) {
        super(eventManager);
    }

    @Override
    public void action(final Event<JSONObject> event) throws EventException {
        final JSONObject article = event.getData();
        LOGGER.log(Level.FINER,
                   "Processing an event[type={0}, data={1}] in listener[className={2}]",
                   new Object[]{event.getType(),
                                article,
                                ArticleSender.class.getName()});
        try {
            final HTTPRequest httpRequest =
                    new HTTPRequest(SoloServletListener.B3LOG_RHYTHM_URL,
                                    HTTPMethod.POST);
            httpRequest.setPayload(article.toString().getBytes());
            final Future<HTTPResponse> futureResponse =
                    urlFetchService.fetchAsync(httpRequest);
            
            final HTTPResponse httpResponse =
                    futureResponse.get(TIMEOUT, TimeUnit.MILLISECONDS);
            final int statusCode = httpResponse.getResponseCode();
            LOGGER.log(Level.FINEST, "Response from Rhythm[statusCode={0}]",
                       statusCode);
        } catch (final Exception e) {
            LOGGER.severe(e.getMessage());
            throw new EventException("Sends article to Rhythm error!");
        }
    }

    /**
     * Gets the event type {@linkplain EventTypes#PREFERENCE_LOAD}.
     *
     * @return event type
     */
    @Override
    public String getEventType() {
        return EventTypes.ADD_ARTICLE;
    }
}
