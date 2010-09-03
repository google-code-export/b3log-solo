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
import java.util.logging.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.util.CollectionUtils;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.BlogSync;
import org.b3log.solo.model.Preference;
import org.b3log.solo.servlet.SoloServletListener;
import org.b3log.solo.sync.MetaWeblogPost;
import org.b3log.solo.util.Htmls;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * CSDN blog article(post, entry, article, whatever).
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.6, Sep 3, 2010
 */
public final class CSDNBlogArticle implements MetaWeblogPost {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(CSDNBlogArticle.class.getName());
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
     * Maximum length of an article abstract.
     */
    private static final int MAX_ABSTRACT_LENGTH = 300;

    /**
     * Constructs a {@link CSDNBlogArticle} object.
     */
    public CSDNBlogArticle() {
        categories = new HashSet<String>();
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
     *     "articleTags": ""
     * }
     * </pre>
     * @throws JSONException json exception
     */
    @SuppressWarnings("unchecked")
    public CSDNBlogArticle(final JSONObject jsonObject) throws JSONException {
        title = jsonObject.getString(Article.ARTICLE_TITLE);
        createDate = (Date) jsonObject.get(Article.ARTICLE_CREATE_DATE);
        content = jsonObject.getString(Article.ARTICLE_CONTENT);
        final String tagsString = jsonObject.getString(Article.ARTICLE_TAGS_REF);
        final String[] tagStrings = tagsString.split(",");
        categories = CollectionUtils.<String>arrayToSet(tagStrings);
    }

    @Override
    public Date getCreateDate() {
        return createDate;
    }

    @Override
    public void setCreateDate(final Date createDate) {
        this.createDate = createDate;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(final String id) {
        this.id = id;
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public void setContent(final String content) {
        this.content = content;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(final String title) {
        this.title = title;
    }

    @Override
    public Set<String> getCategoris() {
        return Collections.unmodifiableSet(categories);
    }

    @Override
    public void addCategory(final String category) {
        categories.add(category);
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

        ret.put(Keys.OBJECT_ID, String.valueOf(createDate.getTime()));
        ret.put(BlogSync.BLOG_SYNC_CSDN_BLOG_ARTICLE_ID, id);
        ret.put(BlogSync.BLOG_SYNC_CSDN_BLOG_ARTICLE_TITLE, title);
        ret.put(BlogSync.BLOG_SYNC_CSDN_BLOG_ARTICLE_CREATE_DATE, createDate);

        final StringBuilder categoriesStringBuilder = new StringBuilder();

        int i = 1;
        for (final String category : categories) {
            categoriesStringBuilder.append(category);

            if (i < categories.size()) {
                categoriesStringBuilder.append(",");
            }

            i++;
        }
        ret.put(BlogSync.BLOG_SYNC_CSDN_BLOG_ARTICLE_CATEGORIES,
                categoriesStringBuilder.toString());
        ret.put(BlogSync.BLOG_SYNC_CSDN_BLOG_ARTICLE_CONTENT, content);
        ret.put(BlogSync.BLOG_SYNC_CSDN_BLOG_ARTICLE_ABSTRACT, genAbstract(
                content));

        return ret;
    }

    @Override
    public Map<String, Object> toPost() {
        final Map<String, Object> ret = new HashMap<String, Object>();

        try {
            ret.put("title", title);
            final StringBuilder descriptionBuilder = new StringBuilder(content);
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
            ret.put("categories", categories.<String>toArray(new String[0]));

            // FIXME: CSDN blog created date bug(time zone)
            ret.put("dateCreated",
                    CSDNBlog.CST_DATE_FORMAT.parse(
                    CSDNBlog.UTC_DATE_FORMAT.format(createDate)));
        } catch (final Exception e) {
            LOGGER.severe(e.getMessage());
        }

        return ret;
    }

    /**
     * Generates article abstract of the specified article content.
     *
     * @param content the specified article content
     * @return a string without html tags as article abstract, its length less
     * {@linkplain #MAX_ABSTRACT_LENGTH}
     */
    private String genAbstract(final String content) {
        final String contentWithoutTags = Htmls.removeHtmlTags(content);
        if (contentWithoutTags.length() >= MAX_ABSTRACT_LENGTH) {
            return contentWithoutTags.substring(0, MAX_ABSTRACT_LENGTH)
                   + "....";
        }

        return contentWithoutTags;
    }
}
