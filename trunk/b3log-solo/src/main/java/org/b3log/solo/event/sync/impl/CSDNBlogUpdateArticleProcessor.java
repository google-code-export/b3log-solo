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
package org.b3log.solo.event.sync.impl;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.event.Event;
import org.b3log.latke.event.EventException;
import org.b3log.solo.event.sync.AbstractUpdateArticleProcessor;
import org.b3log.solo.event.sync.BlogSyncStatusCodes;
import org.b3log.solo.model.BlogSync;
import org.b3log.solo.sync.SyncException;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This listener is responsible for blog sync update article to CSDN blog.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.4, Jun 23, 2011
 */
public final class CSDNBlogUpdateArticleProcessor
        extends AbstractUpdateArticleProcessor {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(CSDNBlogUpdateArticleProcessor.class.getName());

    @Override
    public String getExternalBloggingSys() {
        return BlogSync.BLOG_SYNC_CSDN_BLOG;
    }

    @Override
    public void action(final Event<JSONObject> event) throws EventException {
        final JSONObject eventData = event.getData();
        JSONObject result = null;
        JSONObject events = null;
        JSONObject blogSyncCSDNBlog = null;
        try {
            result = eventData.getJSONObject(Keys.RESULTS);
            final JSONObject status = result.getJSONObject(Keys.STATUS);

            events = status.optJSONObject(Keys.EVENTS);
            if (null == events) {
                events = new JSONObject();
                status.put(Keys.EVENTS, events);
            }

            blogSyncCSDNBlog =
                    events.optJSONObject(BlogSync.BLOG_SYNC_CSDN_BLOG);
            if (null == blogSyncCSDNBlog) {
                blogSyncCSDNBlog = new JSONObject();
                events.put(BlogSync.BLOG_SYNC_CSDN_BLOG, blogSyncCSDNBlog);
            }
        } catch (final JSONException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new EventException(e);
        }

        try {
            final JSONObject rslt = updateArticle(event);
            try {
                blogSyncCSDNBlog.put(Keys.CODE, rslt.getString(Keys.STATUS_CODE));
            } catch (final JSONException ex) {
                LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
                throw new EventException(ex);
            }
        } catch (final SyncException e) {
            try {
                blogSyncCSDNBlog.put(Keys.CODE,
                                     BlogSyncStatusCodes.BLOG_SYNC_FAIL);
                blogSyncCSDNBlog.put(Keys.MSG, e.getMessage());
            } catch (final JSONException ex) {
                LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
                throw new EventException(ex);
            }
        } catch (final EventException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            try {
                blogSyncCSDNBlog.put(Keys.CODE,
                                     BlogSyncStatusCodes.BLOG_SYNC_FAIL);
                blogSyncCSDNBlog.put(Keys.MSG, "Unknown exception :-(");
            } catch (final JSONException ex) {
                LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
                throw new EventException(ex);
            }
        }
    }
}