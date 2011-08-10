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

package org.b3log.solo.event.ping;

import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import java.net.URL;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.StringUtils;
import org.b3log.latke.event.AbstractEventListener;
import org.b3log.latke.event.Event;
import org.b3log.latke.event.EventException;
import org.b3log.latke.event.EventManager;
import org.b3log.solo.event.EventTypes;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.Preference;
import org.b3log.solo.util.Preferences;
import org.json.JSONObject;

/**
 * This listener is responsible for pinging <a href="http://blogsearch.google.com">
 * Google Blog Search Service</a> asynchronously while updating an article.
 *
 * <p>
 *   <li>
 *     <a href="http://www.google.com/help/blogsearch/pinging_API.html">
 *     About Google Blog Search Pinging Service API</a>
 *   </li>
 * </p>
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Jan 12, 2011
 * @see AddArticleGoogleBlogSearchPinger
 */
public final class UpdateArticleGoogleBlogSearchPinger
        extends AbstractEventListener<JSONObject> {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(UpdateArticleGoogleBlogSearchPinger.class.getName());
    /**
     * URL fetch service.
     */
    private static final URLFetchService URL_FETCH_SERVICE =
            URLFetchServiceFactory.getURLFetchService();
    /**
     * Preference utilities.
     */
    private Preferences preferenceUtils = Preferences.getInstance();

    /**
     * Constructs a {@link UpdateArticleGoogleBlogSearchPinger} object with the
     * specified event manager.
     *
     * @param eventManager the specified event manager
     */
    public UpdateArticleGoogleBlogSearchPinger(final EventManager eventManager) {
        super(eventManager);
    }

    /**
     * Gets the event type {@linkplain EventTypes#UPDATE_ARTICLE}.
     *
     * @return event type
     */
    @Override
    public String getEventType() {
        return EventTypes.UPDATE_ARTICLE;
    }

    @Override
    public void action(final Event<JSONObject> event) throws EventException {
        final JSONObject eventData = event.getData();

        String articleTitle = null;
        try {
            final JSONObject article = eventData.getJSONObject(Article.ARTICLE);
            articleTitle = article.getString(Article.ARTICLE_TITLE);
            final JSONObject preference = preferenceUtils.getPreference();
            final String blogTitle = preference.getString(Preference.BLOG_TITLE);
            String blogHost = preference.getString(Preference.BLOG_HOST).
                    toLowerCase().trim();
            if ("localhost".equals(blogHost.split(":")[0].trim())) {
                LOGGER.log(Level.INFO,
                           "Blog Solo runs on local server, so should not ping "
                           + "Google Blog Search Service for the article[title={0}]",
                           new Object[]{article.getString(Article.ARTICLE_TITLE)});
                return;
            }
            blogHost = StringUtils.removeEnd("http://" + blogHost, "/");

            final String articlePermalink =
                    blogHost + article.getString(Article.ARTICLE_PERMALINK);
            final String spec =
                    "http://blogsearch.google.com/ping?name="
                    + URLEncoder.encode(blogTitle, "UTF-8")
                    + "&url=" + URLEncoder.encode(blogHost, "UTF-8")
                    + "&changesURL=" + URLEncoder.encode(articlePermalink,
                                                         "UTF-8");
            LOGGER.log(Level.FINER,
                       "Request Google Blog Search Service API[{0}] while updateing "
                       + "an article[title=" + articleTitle + "]", spec);
            URL_FETCH_SERVICE.fetchAsync(new URL(spec));
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE,
                       "Ping Google Blog Search Service fail while updating an "
                       + "article[title=" + articleTitle + "]", e);
        }
    }
}