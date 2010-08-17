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

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.BlogSync;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * CSDN blog article(post, entry, article, whatever).
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Aug 17, 2010
 */
public final class CSDNBlogArticle {

    /**
     * Id.
     */
    private String id;
    /**
     * Create date.
     */
    private Date createDate;
    /**
     * Title.
     */
    private String title;
    /**
     * Content.
     */
    private String content;
    /**
     * Categories.
     */
    private Set<String> categories;

    /**
     * Constructs a {@link CSDNBlogArticle} object.
     */
    public CSDNBlogArticle() {
        categories = new HashSet<String>();
    }

    /**
     * Constructs a {@link CSDNBlogArticle} object from the specified json
     * object.
     * @param jsonObject the specified json object, for example,
     * <pre>
     * {
     *     "articleTitle": "",
     *     "articleCreateDate": java.util.Date,
     *     "articleContent": "",
     *     "articleTags": java.util.Set&lt;String&gt;
     * }
     * </pre>
     * @throws JSONException json exception
     */
    @SuppressWarnings("unchecked")
    public CSDNBlogArticle(final JSONObject jsonObject) throws JSONException {
        title = jsonObject.getString(Article.ARTICLE_TITLE);
        createDate = (Date) jsonObject.get(Article.ARTICLE_CREATE_DATE);
        content = jsonObject.getString(Article.ARTICLE_CONTENT);
        categories = (Set<String>) jsonObject.get(Article.ARTICLE_TAGS_REF);
    }

    /**
     * Gets the create date.
     *
     * @return create date
     */
    public Date getCreateDate() {
        return createDate;
    }

    /**
     * Sets the create date with the specified create date.
     *
     * @param createDate the specified create date
     */
    public void setCreateDate(final Date createDate) {
        this.createDate = createDate;
    }

    /**
     * Gets the id.
     *
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the id with the specified id.
     *
     * @param id the specified id
     */
    public void setId(final String id) {
        this.id = id;
    }

    /**
     * Gets the content.
     *
     * @return content
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets the content with the specified content.
     *
     * @param content the specified content
     */
    public void setContent(final String content) {
        this.content = content;
    }

    /**
     * Gets the title.
     *
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title with the specified title.
     *
     * @param title the specified title
     */
    public void setTitle(final String title) {
        this.title = title;
    }

    /**
     * Gets the categories.
     *
     * @return categories
     */
    public Set<String> getCategoris() {
        return Collections.unmodifiableSet(categories);
    }

    /**
     * Adds the specified category.
     *
     * @param category the specified category
     */
    public void addCategory(final String category) {
        categories.add(category);
    }

    /**
     * Transforms this object to a json object.
     *
     * <p>
     *   <pre>
     *   {
     *       "blogSyncCSDNBlogArticleId": "",
     *       "blogSyncCSDNBlogArticleTitle": "",
     *       "blogSyncCSDNBlogArticleCreateDate": java.util.Date,
     *       "blogSyncCSDNBlogArticleCategories": ["", "", ....],
     *       "blogSyncCSDNBlogArticleContent": ""
     *   }
     *   </pre>
     * </p>
     *
     * @return json object
     * @throws JSONException json exception
     */
    public JSONObject toJSONObject() throws JSONException {
        final JSONObject ret = new JSONObject();

        ret.put(BlogSync.BLOG_SYNC_CSDN_BLOG_ARTICLE_ID, id);
        ret.put(BlogSync.BLOG_SYNC_CSDN_BLOG_ARTICLE_TITLE, title);
        ret.put(BlogSync.BLOG_SYNC_CSDN_BLOG_ARCHIVE_DATE, createDate);
        ret.put(BlogSync.BLOG_SYNC_CSDN_BLOG_ARTICLE_CATEGORIES, categories);
        ret.put(BlogSync.BLOG_SYNC_CSDN_BLOG_ARTICLE_CONTENT, content);

        return ret;
    }

    /**
     * Transforms this object to a MetaWeblog post structure.
     *
     * @return MetaWeblog post structure
     */
    Map<String, Object> toPost() {
        final Map<String, Object> ret = new HashMap<String, Object>();
        ret.put("title", title);
        ret.put("description", content);
        ret.put("categories", categories.<String>toArray(new String[0]));
        ret.put("dateCreated", createDate);

        return ret;
    }
}
