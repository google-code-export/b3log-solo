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
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringEscapeUtils;
import org.b3log.latke.Keys;
import org.b3log.latke.model.User;
import org.b3log.latke.repository.Filter;
import org.b3log.latke.repository.FilterOperator;
import org.b3log.latke.repository.SortDirection;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.Preference;
import org.b3log.solo.repository.ArticleRepository;
import org.b3log.solo.util.Articles;
import org.b3log.solo.util.Preferences;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Blog articles feed.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.1.2, Dec 16, 2010
 */
public final class BlogArticlesFeedServlet extends HttpServlet {

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
     * Preference utilities.
     */
    @Inject
    private Preferences preferenceUtils;
    /**
     * Count of output entry.
     */
    public static final int ENTRY_OUTPUT_CNT = 10;
    /**
     * Article utilities.
     */
    @Inject
    private Articles articleUtils;

    @Override
    protected void doGet(final HttpServletRequest request,
                         final HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/atom+xml");
        response.setCharacterEncoding("UTF-8");

        final Feed feed = new Feed();
        try {
            final JSONObject preference = preferenceUtils.getPreference();
            if (null == preference) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            final String blogTitle = preference.getString(Preference.BLOG_TITLE);
            final String blogSubtitle = preference.getString(
                    Preference.BLOG_SUBTITLE);
            final String blogHost = preference.getString(Preference.BLOG_HOST);

            feed.setTitle(blogTitle);
            feed.setSubtitle(blogSubtitle);
            feed.setUpdated(new Date());
            feed.setAuthor(blogTitle);
            feed.setLink("http://" + blogHost);

            final Map<String, SortDirection> sorts =
                    new HashMap<String, SortDirection>();
            sorts.put(Article.ARTICLE_CREATE_DATE, SortDirection.DESCENDING);
            final Set<Filter> filters = new HashSet<Filter>();
            filters.add(new Filter(Article.ARTICLE_IS_PUBLISHED,
                                   FilterOperator.EQUAL, true));
            final JSONObject articleResult =
                    articleRepository.get(1, ENTRY_OUTPUT_CNT,
                                          sorts, filters);
            final JSONArray articles = articleResult.getJSONArray(Keys.RESULTS);
            for (int i = 0; i < articles.length(); i++) {
                final JSONObject article = articles.getJSONObject(i);
                final Entry entry = new Entry();
                feed.addEntry(entry);
                final String title = article.getString(Article.ARTICLE_TITLE);
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
                        articleUtils.getAuthor(article).getString(User.USER_NAME);
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
            throw new IOException(e);
        }
    }
}
