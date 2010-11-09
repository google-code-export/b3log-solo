/*
 * Copyright (c) 2009, 2010, B3log Team
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

package org.b3log.solo.sync;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.cache.Cache;
import org.b3log.latke.cache.CacheFactory;
import org.b3log.latke.util.CollectionUtils;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.BlogSync;
import org.b3log.solo.model.Preference;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Post(Article/Entry) of
 * <a href="http://www.xmlrpc.com/metaWeblogApi">MetaWeblog</a>.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.4, Oct 19, 2010
 */
public final class MetaWeblogPost implements Post {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(MetaWeblogPost.class.getName());
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
     * Permalink.
     */
    private String permalink;
    /**
     * Maximum length of an article abstract.
     */
    private static final int MAX_ABSTRACT_LENGTH = 300;

    /**
     * Constructs a {@link AbstractMetaWeblogPost} object.
     */
    public MetaWeblogPost() {
        categories = new HashSet<String>();
    }

    /**
     * Constructs a {@link AbstractMetaWeblogPost} object from the specified
     * json object.
     *
     * @param jsonObject the specified json object, for example,
     * <pre>
     * {
     *     "articleTitle": "",
     *     "articleCreateDate": java.util.Date,
     *     "articleContent": "",
     *     "articleTags": "" // splitted by ",",
     *     "articlePermalink": ""
     * }
     * </pre>
     * @throws JSONException json exception
     */
    public MetaWeblogPost(final JSONObject jsonObject)
            throws JSONException {
        title = jsonObject.getString(Article.ARTICLE_TITLE);
        createDate = (Date) jsonObject.get(Article.ARTICLE_CREATE_DATE);
        content = jsonObject.getString(Article.ARTICLE_CONTENT);
        final String tagsString = jsonObject.getString(Article.ARTICLE_TAGS_REF);
        final String[] tagStrings = tagsString.split(",");
        categories = CollectionUtils.<String>arrayToSet(tagStrings);
        permalink = jsonObject.getString(Article.ARTICLE_PERMALINK);
    }

    @Override
    public String getPermalink() {
        return permalink;
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
    public Set<String> getCategories() {
        return Collections.unmodifiableSet(categories);
    }

    @Override
    public void setCategories(final Set<String> categories) {
        this.categories = categories;
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
     *       "blogSyncExternalArticleId": "",
     *       "blogSyncExternalArticleTitle": "",
     *       "blogSyncExternalArticleCreateDate": java.util.Date,
     *       "blogSyncExternalArticleCategories": "category1, category2, ....",
     *       "blogSyncExternalArticleContent": "",
     *       "blogSyncExternalArticleAbstract": ""
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
        // XXX: the article_oId may be duplicated, its
        // dependens on article created date of external
        // blogging system. Such as CSDN, its article created
        // date ignored the second, so if an blogger publishes two articles in
        // one minute to CSDN blog, we will get the same article_oId....
        ret.put(Keys.OBJECT_ID, String.valueOf(getCreateDate().getTime()));
        ret.put(BlogSync.BLOG_SYNC_EXTERNAL_ARTICLE_ID, getId());
        ret.put(BlogSync.BLOG_SYNC_EXTERNAL_ARTICLE_TITLE, getTitle());
        ret.put(BlogSync.BLOG_SYNC_EXTERNAL_ARTICLE_CREATE_DATE,
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
        ret.put(BlogSync.BLOG_SYNC_EXTERNAL_ARTICLE_CATEGORIES,
                categoriesStringBuilder.toString());
        ret.put(BlogSync.BLOG_SYNC_EXTERNAL_ARTICLE_CONTENT, getContent());
        ret.put(BlogSync.BLOG_SYNC_EXTERNAL_ARTICLE_ABSTRACT, genAbstract(
                getContent()));

        return ret;
    }

    @Override
    public Map<String, Object> toMetaWeblogPost() {
        final Map<String, Object> ret = new HashMap<String, Object>();

        try {
            ret.put("title", getTitle());
            final StringBuilder descriptionBuilder =
                    new StringBuilder(getContent());
            final Cache<String, Object> userPreferenceCache = CacheFactory.
                    getCache(Preference.PREFERENCE);
            final Object preferenceString =
                    userPreferenceCache.get(Preference.PREFERENCE);
            // XXX: preference string may be null
            final JSONObject preference = new JSONObject(preferenceString.
                    toString());
            if (null != preference) { // Preference is null in test env
                final String blogTitle = preference.getString(
                        Preference.BLOG_TITLE);
                final String blogHost = preference.getString(
                        Preference.BLOG_HOST);
                final String blogDomain = blogHost.split(":")[0];
                // TODO: i18N
                descriptionBuilder.append("<br/>");
                descriptionBuilder.append(
                        "<div style='font: italic normal normal 11px Verdana'>");
                descriptionBuilder.append(
                        "本文是使用 <a href='http://b3log-solo.googlecode.com/'>");
                descriptionBuilder.append("B3log Solo</a> 从 <a href='http://");
                descriptionBuilder.append(blogHost);
                descriptionBuilder.append("'>");
                descriptionBuilder.append(blogTitle);
                descriptionBuilder.append("</a> 进行同步发布的</div>");
                descriptionBuilder.append(
                        "<div style='font: italic normal normal 11px Verdana'>");
                descriptionBuilder.append("原文地址：<a href='http://");
                descriptionBuilder.append(blogDomain);
                descriptionBuilder.append(getPermalink());
                descriptionBuilder.append("'>");
                descriptionBuilder.append("http://");
                descriptionBuilder.append(blogDomain);
                descriptionBuilder.append(getPermalink());
                descriptionBuilder.append("</a></div>");
            }
            ret.put("description", descriptionBuilder.toString());
            ret.put("categories", getCategories().<String>toArray(new String[0]));

            // FIXME: CSDN blog created date bug(time zone)
            ret.put("dateCreated",
                    AbstractMetaWeblog.CST_DATE_FORMAT.parse(
                    AbstractMetaWeblog.UTC_DATE_FORMAT.format(getCreateDate())));
        } catch (final Exception e) {
            LOGGER.severe(e.getMessage());
            throw new RuntimeException(e);
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
    protected String genAbstract(final String content) {
        final String contentWithoutTags = content.replaceAll(
                "\\&[a-zA-Z]{1,10};", "").replaceAll(
                "<[^>]*>", "").replaceAll("[(/>)<]", "");
        if (contentWithoutTags.length() >= MAX_ABSTRACT_LENGTH) {
            return contentWithoutTags.substring(0, MAX_ABSTRACT_LENGTH)
                   + "....";
        }

        return contentWithoutTags;
    }
}
