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
package org.b3log.solo.sync.csdn.blog;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import org.b3log.latke.Keys;
import org.b3log.solo.model.BlogSync;
import org.b3log.solo.model.Preference;
import org.b3log.solo.servlet.SoloServletListener;
import org.b3log.solo.sync.AbstractMetaWeblogPost;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * CSDN blog article(post, entry, article, whatever).
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.7, Sep 4, 2010
 */
public final class CSDNBlogArticle extends AbstractMetaWeblogPost {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(CSDNBlogArticle.class.getName());

    /**
     * Constructs a {@link CSDNBlogArticle} object.
     */
    public CSDNBlogArticle() {
        super();
    }

    /**
     * Constructs a {@link CSDNBlogArticle} object from the specified json 
     * object.
     *
     * @param jsonObject the specified json object, for example,
     * <pre>
     * {
     *     "articleTitle": "",
     *     "articleCreateDate": java.util.Date,
     *     "articleContent": "",
     *     "articleTags": "" // splitted by ","
     * }
     * </pre>
     * @throws JSONException json exception
     */
    public CSDNBlogArticle(final JSONObject jsonObject)
            throws JSONException {
        super(jsonObject);
    }

    /**
     * Transforms this object to a json object.
     *
     * <p>
     *   <pre>
     *   {
     *       "oId": "",
     *       "blogSyncCSDNBlogArticleId": "",
     *       "blogSyncCSDNBlogArticleTitle": "",
     *       "blogSyncCSDNBlogArticleCreateDate": java.util.Date,
     *       "blogSyncCSDNBlogArticleCategories": "category1, category2, ....",
     *       "blogSyncCSDNBlogArticleContent": "",
     *       "blogSyncCSDNBlogArticleAbstract": ""
     *   }
     *   </pre>
     * </p>
     *
     * @return json object
     * @throws JSONException json exception
     */
    @Override
    public JSONObject toJSONObject() throws JSONException {
        final JSONObject ret = new JSONObject();

        ret.put(Keys.OBJECT_ID, String.valueOf(getCreateDate().getTime()));
        ret.put(BlogSync.BLOG_SYNC_CSDN_BLOG_ARTICLE_ID, getId());
        ret.put(BlogSync.BLOG_SYNC_CSDN_BLOG_ARTICLE_TITLE, getTitle());
        ret.put(BlogSync.BLOG_SYNC_CSDN_BLOG_ARTICLE_CREATE_DATE,
                getCreateDate());

        final StringBuilder categoriesStringBuilder = new StringBuilder();

        int i = 1;
        final int size = getCategories().size();
        for (final String category : getCategories()) {
            categoriesStringBuilder.append(category);

            if (i < size) {
                categoriesStringBuilder.append(",");
            }

            i++;
        }
        ret.put(BlogSync.BLOG_SYNC_CSDN_BLOG_ARTICLE_CATEGORIES,
                categoriesStringBuilder.toString());
        ret.put(BlogSync.BLOG_SYNC_CSDN_BLOG_ARTICLE_CONTENT, getContent());
        ret.put(BlogSync.BLOG_SYNC_CSDN_BLOG_ARTICLE_ABSTRACT, genAbstract(
                getContent()));

        return ret;
    }

    @Override
    public Map<String, Object> toPost() {
        final Map<String, Object> ret = new HashMap<String, Object>();

        try {
            ret.put("title", getTitle());
            final StringBuilder descriptionBuilder =
                    new StringBuilder(getContent());
            final JSONObject preference =
                    SoloServletListener.getUserPreference();
            final String blogTitle = preference.getString(Preference.BLOG_TITLE);
            descriptionBuilder.append("<p>");
            descriptionBuilder.append(
                    "本文是使用<a href='http://b3log-solo.googlecode.com/'>");
            descriptionBuilder.append("B3log Solo</a>从<a href='http://");
            descriptionBuilder.append(preference.getString(Preference.BLOG_HOST));
            descriptionBuilder.append("'>");
            descriptionBuilder.append(blogTitle);
            descriptionBuilder.append("</a>进行同步发布的。");
            descriptionBuilder.append("</p>");
            ret.put("description", descriptionBuilder.toString());
            ret.put("categories", getCategories().<String>toArray(new String[0]));

            // FIXME: CSDN blog created date bug(time zone)
            ret.put("dateCreated",
                    CSDNBlog.CST_DATE_FORMAT.parse(
                    CSDNBlog.UTC_DATE_FORMAT.format(getCreateDate())));
        } catch (final Exception e) {
            LOGGER.severe(e.getMessage());
        }

        return ret;
    }
}
