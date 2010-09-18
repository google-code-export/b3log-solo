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
package org.b3log.solo.event.buzz;

import com.google.api.client.googleapis.json.JsonCContent;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpTransport;
import com.google.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.latke.event.AbstractEventListener;
import org.b3log.latke.event.Event;
import org.b3log.latke.event.EventException;
import org.b3log.latke.event.EventManager;
import org.b3log.solo.event.EventTypes;
import org.b3log.solo.google.buzz.BuzzUrl;
import org.b3log.solo.jsonrpc.impl.PreferenceService;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.Preference;
import org.b3log.solo.servlet.SoloServletListener;
import org.json.JSONObject;

/**
 * This listener is responsible for creating a new activity to Google buzz while
 * adding an article.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.2, Sep 18, 2010
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

            final HttpTransport httpTransport = preferenceService.
                    getHttpTransport();

            if (null == httpTransport) {
                LOGGER.log(Level.SEVERE, "Http transport is null");
                throw new Exception("Http transport is null");
            }

            post(httpTransport, article);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new EventException(
                    "Send article creation buzz activity to Google Buzz error");
        }
    }

    /**
     * Posts the specified article via the specified http transport.
     *
     * @param httpTransport the specified http transport
     * @param article the specified article
     * @throws Exception exception
     */
    public void post(final HttpTransport httpTransport,
                     final JSONObject article)
            throws Exception {
        final HttpRequest request = httpTransport.buildPostRequest();
        request.url = BuzzUrl.forMyActivityFeed();
        request.content = toContent(article);

        request.execute().parseAsString();
    }

    /**
     * Returns a new JSON-C content serializer for the specified article.
     *
     * @param article the specified article
     * @return JSON-C content
     * @throws Exception exception
     */
    private JsonCContent toContent(final JSONObject article) throws Exception {
        final JSONObject preference = SoloServletListener.getUserPreference();
        final JsonCContent ret = new JsonCContent();
        final Map<String, Object> data = new HashMap<String, Object>();
        data.put("title", preference.getString(Preference.BLOG_TITLE));
        final Map<String, Object> dataLinks = new HashMap<String, Object>();
        data.put("links", dataLinks);
        final List<Map<String, Object>> dataLinksAlts =
                new ArrayList<Map<String, Object>>();
        dataLinks.put("alternate", dataLinksAlts);
        final Map<String, Object> dataLinksAlt = new HashMap<String, Object>();
        dataLinksAlts.add(dataLinksAlt);
        dataLinksAlt.put("href", preference.getString(Preference.BLOG_HOST));
        dataLinksAlt.put("type", "text/html");

        final Map<String, Object> object = new HashMap<String, Object>();
        data.put("object", object);
        object.put("type", "note");
        object.put("content", article.getString(Article.ARTICLE_TITLE));
        final List<Map<String, Object>> objectAttachments =
                new ArrayList<Map<String, Object>>();
        object.put("attachments", objectAttachments);
        final Map<String, Object> objectAttachment =
                new HashMap<String, Object>();
        objectAttachments.add(objectAttachment);
        objectAttachment.put("type", "article");
        objectAttachment.put("title", article.getString(Article.ARTICLE_TITLE));
        objectAttachment.put("content", article.getString(
                Article.ARTICLE_CONTENT));
        final Map<String, Object> objectAttachmentsLinks =
                new HashMap<String, Object>();
        objectAttachment.put("links", objectAttachmentsLinks);
        final List<Map<String, Object>> objectLinksAlts =
                new ArrayList<Map<String, Object>>();
        objectAttachmentsLinks.put("alternate", objectLinksAlts);
        final Map<String, Object> objectLinksAlt = new HashMap<String, Object>();
        objectLinksAlts.add(objectLinksAlt);
        objectLinksAlt.put("href",
                           "http://"
                           + preference.getString(Preference.BLOG_HOST)
                           + article.getString(Article.ARTICLE_PERMALINK));
        objectLinksAlt.put("type", "text/html");

        ret.data = data;

        return ret;
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
