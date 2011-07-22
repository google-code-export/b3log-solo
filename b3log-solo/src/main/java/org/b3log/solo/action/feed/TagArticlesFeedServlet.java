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
package org.b3log.solo.action.feed;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringEscapeUtils;
import org.b3log.latke.Keys;
import org.b3log.latke.model.User;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.Preference;
import org.b3log.solo.model.Tag;
import org.b3log.solo.repository.ArticleRepository;
import org.b3log.solo.repository.TagArticleRepository;
import org.b3log.solo.repository.TagRepository;
import org.b3log.solo.repository.impl.ArticleGAERepository;
import org.b3log.solo.repository.impl.TagArticleGAERepository;
import org.b3log.solo.repository.impl.TagGAERepository;
import org.b3log.solo.util.Articles;
import org.b3log.solo.util.Preferences;
import org.b3log.solo.util.TimeZones;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Tag articles feed.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.1.5, Jul 11, 2011
 */
public final class TagArticlesFeedServlet extends HttpServlet {

    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(TagArticlesFeedServlet.class.getName());
    /**
     * Article repository.
     */
    private ArticleRepository articleRepository =
            ArticleGAERepository.getInstance();
    /**
     * Tag repository.
     */
    private TagRepository tagRepository =
            TagGAERepository.getInstance();
    /**
     * Tag-Article repository.
     */
    private TagArticleRepository tagArticleRepository =
            TagArticleGAERepository.getInstance();
    /**
     * Preference utilities.
     */
    private Preferences preferenceUtils = Preferences.getInstance();
    /**
     * Count of output entry.
     */
    public static final int ENTRY_OUTPUT_CNT = 10;
    /**
     * Article utilities.
     */
    private Articles articleUtils = Articles.getInstance();
    /**
     * Time zone utilities.
     */
    private TimeZones timeZoneUtils = TimeZones.getInstance();

    @Override
    protected void doGet(final HttpServletRequest request,
                         final HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/atom+xml");
        response.setCharacterEncoding("UTF-8");

        final String queryString = request.getQueryString();
        final String oIdMap = queryString.split("&")[0];
        final String tagId = oIdMap.split("=")[1];

        final Feed feed = new Feed();
        try {
            final JSONObject tagArticleResult =
                    tagArticleRepository.getByTagId(tagId, 1, ENTRY_OUTPUT_CNT);
            final JSONArray tagArticleRelations =
                    tagArticleResult.getJSONArray(Keys.RESULTS);
            if (0 == tagArticleRelations.length()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            
            final List<JSONObject> articles = new ArrayList<JSONObject>();
            for (int i = 0; i < tagArticleRelations.length(); i++) {
                final JSONObject tagArticleRelation =
                        tagArticleRelations.getJSONObject(i);
                final String articleId =
                        tagArticleRelation.getString(Article.ARTICLE + "_"
                                                     + Keys.OBJECT_ID);
                final JSONObject article = articleRepository.get(articleId);
                if (article.getBoolean(Article.ARTICLE_IS_PUBLISHED)) {  // Skips the unpublished article
                    articles.add(article);
                }
            }

            final String tagTitle =
                    tagRepository.get(tagId).getString(Tag.TAG_TITLE);

            final JSONObject preference = preferenceUtils.getPreference();
            if (null == preference) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            final String blogTitle = preference.getString(Preference.BLOG_TITLE);
            final String blogSubtitle = preference.getString(
                    Preference.BLOG_SUBTITLE) + ", " + tagTitle;
            final String blogHost = preference.getString(Preference.BLOG_HOST);

            feed.setTitle(StringEscapeUtils.escapeXml(blogTitle));
            feed.setSubtitle(StringEscapeUtils.escapeXml(blogSubtitle));
            feed.setUpdated(timeZoneUtils.getTime(
                    preference.getString(Preference.TIME_ZONE_ID)));
            feed.setAuthor(StringEscapeUtils.escapeXml(blogTitle));
            feed.setLink("http://" + blogHost);

            for (int i = 0; i < articles.size(); i++) {
                final JSONObject article = articles.get(i);
                final Entry entry = new Entry();
                feed.addEntry(entry);
                final String title = StringEscapeUtils.escapeXml(
                        article.getString(Article.ARTICLE_TITLE));
                entry.setTitle(title);
                final String summary =
                        StringEscapeUtils.escapeXml(article.getString(
                        Article.ARTICLE_ABSTRACT));
                entry.setSummary(summary);
                final Date updated = (Date) article.get(
                        Article.ARTICLE_UPDATE_DATE);
                entry.setUpdated(updated);
                final String id = article.getString(Keys.OBJECT_ID);
                entry.setId(id);
                final String link = "http://" + blogHost + article.getString(
                        Article.ARTICLE_PERMALINK);
                entry.setLink(link);
                final String authorName =
                        StringEscapeUtils.escapeXml(
                        articleUtils.getAuthor(article).getString(User.USER_NAME));
                entry.setAuthor(authorName);

                final String tagsString =
                        article.getString(Article.ARTICLE_TAGS_REF);
                final String[] tagStrings = tagsString.split(",");
                for (int j = 0; j < tagStrings.length; j++) {
                    final Category catetory = new Category();
                    entry.addCatetory(catetory);
                    final String tag = tagStrings[j];
                    catetory.setTerm(tag);
                }
            }

            final PrintWriter writer = response.getWriter();
            writer.write(feed.toString());
            writer.close();
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, "Get tag article feed error", e);
            throw new IOException(e);
        }
    }
}
