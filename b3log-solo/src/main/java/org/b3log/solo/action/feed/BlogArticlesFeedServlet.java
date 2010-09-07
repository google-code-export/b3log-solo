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
package org.b3log.solo.action.feed;

import com.google.inject.Inject;
import java.io.IOException;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Text;
import org.apache.commons.lang.StringEscapeUtils;
import org.b3log.latke.Keys;
import org.b3log.latke.repository.SortDirection;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.Preference;
import org.b3log.solo.repository.ArticleRepository;
import org.b3log.solo.servlet.SoloServletListener;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Blog articles feed.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.5, Sep 7, 2010
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
     * Feed factory.
     */
    private Factory feedFactory = Abdera.getNewFactory();
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

        final Feed feed = feedFactory.newFeed();
        try {
            final JSONObject preference =
                    SoloServletListener.getUserPreference();
            final String blogTitle = preference.getString(Preference.BLOG_TITLE);
            final String blogSubtitle = preference.getString(
                    Preference.BLOG_SUBTITLE);

            feed.setTitle(blogTitle);
            feed.setSubtitle(blogSubtitle);
            feed.setUpdated(new Date());
            feed.addAuthor(blogTitle);

            final JSONObject articleResult =
                    articleRepository.get(1, ENTRY_OUTPUT_CNT,
                                          Article.ARTICLE_CREATE_DATE,
                                          SortDirection.DESCENDING);
            final JSONArray articles = articleResult.getJSONArray(Keys.RESULTS);
            for (int i = 0; i < articles.length(); i++) {
                final JSONObject article = articles.getJSONObject(i);
                final Entry entry = feed.addEntry();
                final String title = article.getString(Article.ARTICLE_TITLE);
                final String summary = StringEscapeUtils.escapeHtml(article.
                        getString(Article.ARTICLE_ABSTRACT));
                final Date updated = (Date) article.get(
                        Article.ARTICLE_UPDATE_DATE);
                final String id = article.getString(Keys.OBJECT_ID);
                final String link = "/article-detail.do?oId=" + id;

                entry.setTitle(title);
                entry.addLink(link);
                entry.setId(id);
                entry.setUpdated(updated);
                entry.setSummary(summary, Text.Type.HTML);
            }

            feed.getDocument().writeTo(response.getOutputStream());
        } catch (final Exception e) {
            throw new IOException(e);
        }
    }
}
