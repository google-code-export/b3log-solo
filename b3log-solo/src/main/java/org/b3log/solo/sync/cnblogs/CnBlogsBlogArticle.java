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
package org.b3log.solo.sync.cnblogs;

import java.util.logging.Logger;
import org.b3log.latke.Keys;
import org.b3log.solo.model.BlogSync;
import org.b3log.solo.sync.AbstractMetaWeblogPost;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * CnBlogs blog article.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Sep 4, 2010
 */
public final class CnBlogsBlogArticle extends AbstractMetaWeblogPost {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(CnBlogsBlogArticle.class.getName());

    /**
     * Constructs a {@link CnBlogsBlogArticle} object.
     */
    public CnBlogsBlogArticle() {
        super();
    }

    /**
     * Constructs a {@link CnBlogsBlogArticle} object from the specified json
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
    public CnBlogsBlogArticle(final JSONObject jsonObject)
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
    // TODO: toJSONObject
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
}
