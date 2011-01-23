/*
 * Copyright (c) 2009, 2010, 2011, B3log Team
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
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.event.AbstractEventListener;
import org.b3log.latke.event.Event;
import org.b3log.latke.event.EventException;
import org.b3log.latke.event.EventManager;
import org.b3log.solo.SoloServletListener;
import org.b3log.solo.event.EventTypes;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.Preference;
import org.b3log.solo.util.Preferences;
import org.json.JSONObject;

/**
 * This listener is responsible for sending article to B3log Rhythm.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.1.0, Jan 23, 2011
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
     * Preference utilities.
     */
    private Preferences preferenceUtils = Preferences.getInstance();
    /**
     * URL of adding article to Rhythm.
     */
    private static final URL ADD_ARTICLE_URL;
    /**
     * Key of version.
     */
    private static final String VER = "soloVersion";

    static {
        try {
            ADD_ARTICLE_URL =
                    new URL(SoloServletListener.B3LOG_RHYTHM_ADDRESS
                            + "/add-article.do");
        } catch (final MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Constructs a {@link ArticleSender} object with the specified event
     * manager.
     *
     * @param eventManager the specified event manager
     */
    public ArticleSender(final EventManager eventManager) {
        super(eventManager);
    }

    @Override
    public void action(final Event<JSONObject> event) throws EventException {
        final JSONObject data = event.getData();
        LOGGER.log(Level.FINER,
                   "Processing an event[type={0}, data={1}] in listener[className={2}]",
                   new Object[]{event.getType(),
                                data,
                                ArticleSender.class.getName()});
        try {
            final JSONObject article =
                    data.getJSONObject(Article.ARTICLE);
            final JSONObject preference = preferenceUtils.getPreference();
            if (null == preference) {
                throw new EventException("Not found preference");
            }

            final String blogHost =
                    preference.getString(Preference.BLOG_HOST).toLowerCase();
            if (Preference.Default.DEFAULT_BLOG_HOST.equals(blogHost)
                || "localhost".equals(blogHost.split(":")[0].trim())) {
                LOGGER.log(Level.INFO,
                           "Blog Solo runs on local server, so should not send "
                           + "this article[oId={0}, title={1}] to Rhythm",
                           new Object[]{article.getString(Keys.OBJECT_ID),
                                        article.getString(Article.ARTICLE_TITLE)});
                return;
            }

            final HTTPRequest httpRequest =
                    new HTTPRequest(ADD_ARTICLE_URL, HTTPMethod.POST);
            final JSONObject requestJSONObject = new JSONObject();
            requestJSONObject.put(VER, SoloServletListener.VERSION);
            requestJSONObject.put(Article.ARTICLE, article);
            requestJSONObject.put(Preference.BLOG_HOST, blogHost);
            httpRequest.setPayload(
                    requestJSONObject.toString().getBytes("UTF-8"));
//            final Future<HTTPResponse> futureResponse =
            urlFetchService.fetchAsync(httpRequest);
//            final HTTPResponse httpResponse =
//                    futureResponse.get(TIMEOUT, TimeUnit.MILLISECONDS);
//            final int statusCode = httpResponse.getResponseCode();
//            LOGGER.log(Level.FINEST, "Response from Rhythm[statusCode={0}]",
//                       statusCode);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, "Sends article to Rhythm error: {0}",
                       e.getMessage());
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
