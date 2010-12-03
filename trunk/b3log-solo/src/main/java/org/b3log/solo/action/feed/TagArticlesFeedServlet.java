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

package org.b3log.solo.action.feed;

import com.google.inject.Inject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringEscapeUtils;
import org.b3log.latke.Keys;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.Preference;
import org.b3log.solo.model.Tag;
import org.b3log.solo.repository.ArticleRepository;
import org.b3log.solo.repository.TagArticleRepository;
import org.b3log.solo.repository.TagRepository;
import org.b3log.solo.util.PreferenceUtils;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Tag articles feed.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.8, Dec 4, 2010
 */
public final class TagArticlesFeedServlet extends HttpServlet {

    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Article repository.
     */
    @Inject
    private ArticleRepository articleRepository;
    /**
     * Tag repository.
     */
    @Inject
    private TagRepository tagRepository;
    /**
     * Tag-Article repository.
     */
    @Inject
    private TagArticleRepository tagArticleRepository;
    /**
     * Preference utilities.
     */
    @Inject
    private PreferenceUtils preferenceUtils;
    /**
     * Count of output entry.
     */
    public static final int ENTRY_OUTPUT_CNT = 10;

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
            final List<JSONObject> articles = new ArrayList<JSONObject>();
            for (int i = 0; i < tagArticleRelations.length(); i++) {
                final JSONObject tagArticleRelation =
                        tagArticleRelations.getJSONObject(i);
                final String articleId =
                        tagArticleRelation.getString(Article.ARTICLE + "_"
                                                     + Keys.OBJECT_ID);
                final JSONObject article = articleRepository.get(articleId);
                if (article.getBoolean(Article.ARTICLE_IS_PUBLISHED)) {
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

            feed.setTitle(blogTitle);
            feed.setSubtitle(blogSubtitle);
            feed.setUpdated(new Date());
            feed.setAuthor(blogTitle);
            feed.setLink("http://" + blogHost);

            for (int i = 0; i < articles.size(); i++) {
                final JSONObject article = articles.get(i);
                final Entry entry = new Entry();
                feed.addEntry(entry);
                final String title = article.getString(Article.ARTICLE_TITLE);
                final String summary = StringEscapeUtils.escapeHtml(
                        article.getString(Article.ARTICLE_ABSTRACT));
                final Date updated = (Date) article.get(
                        Article.ARTICLE_UPDATE_DATE);
                final String id = article.getString(Keys.OBJECT_ID);
                final String link = "http://" + blogHost + article.getString(
                        Article.ARTICLE_PERMALINK);

                entry.setTitle(title);
                entry.setLink(link);
                entry.setId(id);
                entry.setUpdated(updated);
                entry.setSummary(summary);
            }

            final PrintWriter writer = response.getWriter();
            writer.write(feed.toString());
            writer.close();
        } catch (final Exception e) {
            throw new IOException(e);
        }
    }
}
