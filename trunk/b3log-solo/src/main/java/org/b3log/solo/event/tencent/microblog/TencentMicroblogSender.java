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

package org.b3log.solo.event.tencent.microblog;

import com.mime.qweibo.QWeiboSyncApi;
import com.mime.qweibo.QWeiboType.ResultType;
import java.util.logging.Level;
import java.util.logging.Logger;
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
 * This listener is responsible for sending article publish information to
 * author's <a href="http://t.qq.com">Tencent microblog</a>.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Jan 26, 2011
 */
public final class TencentMicroblogSender extends AbstractEventListener<JSONObject> {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(TencentMicroblogSender.class.getName());
    /**
     * Preference utilities.
     */
    private Preferences preferenceUtils = Preferences.getInstance();

    /**
     * Constructs a {@link TencentMicroblogSender} object with the specified
     * event manager.
     *
     * @param eventManager the specified event manager
     */
    public TencentMicroblogSender(final EventManager eventManager) {
        super(eventManager);
    }

    @Override
    public void action(final Event<JSONObject> event) throws EventException {
        final JSONObject preference = preferenceUtils.getPreference();
        if (null == preference) {
            throw new EventException("Not found preference");
        }

        final boolean enabled = preference.optBoolean(
                Preference.ENABLE_POST_TO_TENCENT_MICROBLOG);
        if (!enabled) {
            return;
        }

        final JSONObject data = event.getData();
        LOGGER.log(Level.FINER,
                   "Processing an event[type={0}, data={1}] in listener[className={2}]",
                   new Object[]{event.getType(),
                                data,
                                TencentMicroblogSender.class.getName()});
        try {
            final JSONObject article =
                    data.getJSONObject(Article.ARTICLE);

            final QWeiboSyncApi api = new QWeiboSyncApi();
            final String appKey = preference.getString(
                    Preference.TENCENT_MICROBLOG_APP_KEY);
            final String appSecret = preference.getString(
                    Preference.TENCENT_MICROBLOG_APP_SECRET);
            final String tokenKey = preference.getString(
                    Preference.TENCENT_MICROBLOG_TOKEN_KEY);
            final String tokenSecret = preference.getString(
                    Preference.TENCENT_MICROBLOG_TOKEN_SECRET);

            final String articleTitle = article.getString(Article.ARTICLE_TITLE);
            final String blogTitle = preference.getString(Preference.BLOG_TITLE);
            final String blogHost = preference.getString(Preference.BLOG_HOST);
            final String articlePermalink =
                    "http://" + blogHost + article.getString(
                    Article.ARTICLE_PERMALINK);

            final StringBuilder contentBuilder = new StringBuilder(articleTitle);
            contentBuilder.append(" - ").append(blogTitle).append(" ").append(
                    articlePermalink);

            api.publishMsg(appKey, appSecret, tokenKey, tokenSecret,
                           contentBuilder.toString(),
                           null, ResultType.ResultType_Json);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE,
                       "Sends article to Tencent microblog error: {0}",
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
