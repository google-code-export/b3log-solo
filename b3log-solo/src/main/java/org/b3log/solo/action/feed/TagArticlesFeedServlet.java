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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Text.Type;
import org.apache.commons.lang.StringEscapeUtils;
import org.b3log.latke.Keys;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.Preference;
import org.b3log.solo.model.Tag;
import org.b3log.solo.repository.ArticleRepository;
import org.b3log.solo.repository.TagArticleRepository;
import org.b3log.solo.repository.TagRepository;
import org.b3log.solo.SoloServletListener;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Tag articles feed.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.5, Sep 19, 2010
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

        final String queryString = request.getQueryString();
        final String oIdMap = queryString.split("&")[0];
        final String tagId = oIdMap.split("=")[1];

        final Feed feed = feedFactory.newFeed();
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
                articles.add(articleRepository.get(articleId));
            }

            final String tagTitle =
                    tagRepository.get(tagId).getString(Tag.TAG_TITLE);

            final JSONObject preference =
                    SoloServletListener.getUserPreference();
            final String blogTitle = preference.getString(Preference.BLOG_TITLE);
            final String blogSubtitle = preference.getString(
                    Preference.BLOG_SUBTITLE) + ", " + tagTitle;

            feed.setTitle(blogTitle);
            feed.setSubtitle(blogSubtitle);
            feed.setUpdated(new Date());
            feed.addAuthor(blogTitle);

            for (int i = 0; i < articles.size(); i++) {
                final JSONObject article = articles.get(i);
                final Entry entry = feed.addEntry();
                final String title = article.getString(Article.ARTICLE_TITLE);
                final String summary = StringEscapeUtils.escapeHtml(
                        article.getString(Article.ARTICLE_ABSTRACT));
                final Date updated = (Date) article.get(
                        Article.ARTICLE_UPDATE_DATE);
                final String id = article.getString(Keys.OBJECT_ID);
                final String link = article.getString(Article.ARTICLE_PERMALINK);

                entry.setTitle(title);
                entry.addLink(link);
                entry.setId(id);
                entry.setUpdated(updated);
                entry.setSummary(StringEscapeUtils.unescapeHtml(summary),
                                 Type.HTML);
            }

            feed.getDocument().writeTo(response.getOutputStream());
        } catch (final Exception e) {
            throw new IOException(e);
        }
    }
}
