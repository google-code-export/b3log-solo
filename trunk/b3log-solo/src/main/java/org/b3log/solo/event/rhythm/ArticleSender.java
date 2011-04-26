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
import java.util.Date;
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
import org.b3log.solo.model.Common;
import org.b3log.solo.model.Preference;
import org.b3log.solo.util.Preferences;
import org.json.JSONObject;

/**
 * This listener is responsible for sending article to B3log Rhythm.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.1.4, Apr 26, 2011
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
            final JSONObject originalArticle =
                    data.getJSONObject(Article.ARTICLE);
            if (!originalArticle.getBoolean(Common.POST_TO_COMMUNITY)) {
                LOGGER.log(Level.FINER,
                           "Ignores post article[title={0}] to Rhythm",
                           originalArticle.getString(Article.ARTICLE_TITLE));
                
                return;
            }

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
                           new Object[]{
                            originalArticle.getString(Keys.OBJECT_ID),
                            originalArticle.getString(
                            Article.ARTICLE_TITLE)});
                return;
            }

            final HTTPRequest httpRequest =
                    new HTTPRequest(ADD_ARTICLE_URL, HTTPMethod.POST);
            final JSONObject requestJSONObject = new JSONObject();
            final JSONObject article = new JSONObject();
            article.put(Keys.OBJECT_ID,
                        originalArticle.getString(Keys.OBJECT_ID));
            article.put(Article.ARTICLE_TITLE,
                        originalArticle.getString(Article.ARTICLE_TITLE));
            article.put(Article.ARTICLE_PERMALINK,
                        originalArticle.getString(Article.ARTICLE_PERMALINK));
            article.put(Article.ARTICLE_TAGS_REF,
                        originalArticle.getString(Article.ARTICLE_TAGS_REF));
            article.put(Article.ARTICLE_AUTHOR_EMAIL,
                        originalArticle.getString(Article.ARTICLE_AUTHOR_EMAIL));
            article.put(Article.ARTICLE_CONTENT,
                        originalArticle.getString(Article.ARTICLE_CONTENT));
            article.put(Article.ARTICLE_CREATE_DATE,
                        ((Date) originalArticle.get(Article.ARTICLE_CREATE_DATE)).
                    getTime());

            requestJSONObject.put(Article.ARTICLE, article);
            requestJSONObject.put(Common.BLOG_VERSION,
                                  SoloServletListener.VERSION);
            requestJSONObject.put(Common.BLOG, "B3log Solo");
            requestJSONObject.put(Preference.BLOG_TITLE,
                                  preference.getString(Preference.BLOG_TITLE));
            requestJSONObject.put(Preference.BLOG_HOST, blogHost);
            httpRequest.setPayload(
                    requestJSONObject.toString().getBytes("UTF-8"));

            urlFetchService.fetchAsync(httpRequest);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, "Sends article to Rhythm error: {0}",
                       e.getMessage());
        }
    }

    /**
     * Gets the event type {@linkplain EventTypes#ADD_ARTICLE}.
     *
     * @return event type
     */
    @Override
    public String getEventType() {
        return EventTypes.ADD_ARTICLE;
    }
}
