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
package org.b3log.solo.action.util;

import com.google.inject.Inject;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.solo.util.ArticleUtils;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.Common;
import org.b3log.solo.repository.ArticleRepository;
import org.b3log.solo.repository.TagArticleRepository;
import org.b3log.solo.repository.TagRepository;
import org.b3log.latke.Keys;
import org.b3log.latke.client.Sessions;
import org.b3log.latke.client.action.util.Paginator;
import org.b3log.latke.model.Pagination;
import org.b3log.latke.repository.SortDirection;
import org.b3log.solo.util.Preferences;
import org.b3log.solo.model.Link;
import org.b3log.solo.model.Preference;
import org.b3log.solo.repository.LinkRepository;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Filler utilities.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.4, Aug 18, 2010
 */
public final class Filler {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(Filler.class);
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
     * Article utilities.
     */
    @Inject
    private ArticleUtils articleUtils;
    /**
     * Preference utilities.
     */
    @Inject
    private Preferences preferences;
    /**
     * Link repository.
     */
    @Inject
    private LinkRepository linkRepository;

    /**
     * Fills articles in index.html.
     *
     * @param dataModel data model
     * @param currentPageNum current page number
     * @throws Exception exception
     */
    @SuppressWarnings("unchecked")
    public void fillIndexArticles(final Map<String, Object> dataModel,
                                  final int currentPageNum)
            throws Exception {
        final JSONObject preference = preferences.getPreference();

        final int pageSize =
                preference.getInt(Preference.ARTICLE_LIST_DISPLAY_COUNT);
        final int windowSize =
                preference.getInt(Preference.ARTICLE_LIST_PAGINATION_WINDOW_SIZE);

        final JSONObject result =
                articleRepository.get(currentPageNum,
                                      pageSize,
                                      Article.ARTICLE_UPDATE_DATE,
                                      SortDirection.DESCENDING);

        final int pageCount = result.getJSONObject(Pagination.PAGINATION).
                getInt(Pagination.PAGINATION_PAGE_COUNT);

        final List<Integer> pageNums = Paginator.paginate(currentPageNum,
                                                          pageSize,
                                                          pageCount,
                                                          windowSize);

        dataModel.put(Pagination.PAGINATION_PAGE_COUNT, pageCount);
        dataModel.put(Pagination.PAGINATION_PAGE_NUMS, pageNums);
        final List<JSONObject> articles = org.b3log.latke.util.CollectionUtils.
                jsonArrayToList(result.getJSONArray(Keys.RESULTS));
        articleUtils.addTags(articles);

        dataModel.put(Article.ARTICLES, articles);
    }

    /**
     * Fills links.
     *
     * @param dataModel data model
     * @throws JSONException json exception
     * @throws RepositoryException repository exception
     */
    private void fillLinks(final Map<String, Object> dataModel)
            throws JSONException, RepositoryException {
        final JSONObject linkResult = linkRepository.get(1,
                                                         Integer.MAX_VALUE);
        final List<JSONObject> links = org.b3log.latke.util.CollectionUtils.
                jsonArrayToList(linkResult.getJSONArray(Keys.RESULTS));

        dataModel.put(Link.LINKS, links);
    }

    /**
     * Fills most used tags.
     *
     * @param dataModel data model
     * @throws Exception exception
     */
    public void fillMostUsedTags(final Map<String, Object> dataModel)
            throws Exception {
        final JSONObject preference = preferences.getPreference();
        final int mostUsedTagDisplayCnt =
                preference.getInt(Preference.MOST_USED_TAG_DISPLAY_CNT);

        final List<JSONObject> tags =
                tagRepository.getMostUsedTags(mostUsedTagDisplayCnt);

        dataModel.put(Common.MOST_USED_TAGS, tags);
    }

    /**
     * Fills archive dates.
     *
     * @param dataModel data model
     * @throws Exception exception
     */
    public void fillArchiveDates(final Map<String, Object> dataModel)
            throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Fills most view count articles.
     *
     * @param dataModel data model
     * @throws Exception exception
     */
    public void fillMostViewCountArticles(final Map<String, Object> dataModel)
            throws Exception {
        final JSONObject preference = preferences.getPreference();
        final int mostCommentArticleDisplayCnt =
                preference.getInt(Preference.MOST_COMMENT_ARTICLE_DISPLAY_CNT);
        final List<JSONObject> mostViewCountArticles =
                articleRepository.getMostCommentArticles(
                mostCommentArticleDisplayCnt);

        dataModel.put(Common.MOST_COMMENT_ARTICLES, mostViewCountArticles);
    }

    /**
     * Fills most comments articles.
     *
     * @param dataModel data model
     * @throws Exception exception
     */
    public void fillMostCommentArticles(final Map<String, Object> dataModel)
            throws Exception {
        final JSONObject preference = preferences.getPreference();
        final int mostCommentArticleDisplayCnt =
                preference.getInt(Preference.MOST_COMMENT_ARTICLE_DISPLAY_CNT);
        final List<JSONObject> mostCommentArticles =
                articleRepository.getMostCommentArticles(
                mostCommentArticleDisplayCnt);

        dataModel.put(Common.MOST_COMMENT_ARTICLES, mostCommentArticles);
    }

    /**
     * Fills post articles recently.
     *
     * @param dataModel data model
     * @throws Exception exception
     */
    public void fillRecentArticles(final Map<String, Object> dataModel)
            throws Exception {
        final JSONObject preference = preferences.getPreference();
        final int recentArticleDisplayCnt =
                preference.getInt(Preference.RECENT_ARTICLE_DISPLAY_CNT);

        final List<JSONObject> recentArticles =
                articleRepository.getRecentArticles(recentArticleDisplayCnt);

        dataModel.put(Common.RECENT_ARTICLES, recentArticles);
    }

    /**
     * Fills article-header.html.
     *
     * @param dataModel data model
     * @param request http servlet request
     * @throws Exception exception
     */
    public void fillBlogHeader(final Map<String, Object> dataModel,
                               final HttpServletRequest request)
            throws Exception {
        final JSONObject preference = preferences.getPreference();
        final String blogTitle = preference.getString(Preference.BLOG_TITLE);
        final String blogSubtitle = preference.getString(
                Preference.BLOG_SUBTITLE);

        dataModel.put(Preference.BLOG_TITLE, blogTitle);
        dataModel.put(Preference.BLOG_SUBTITLE, blogSubtitle);
        final String currentUserName = Sessions.currentUserName(request);
        if (null == currentUserName) {
            dataModel.put(Common.LOGINT_STATUS, 0);
        } else {
            dataModel.put(Common.LOGINT_STATUS, 1);
        }
    }

    /**
     * Fills article-side.html.
     *
     * @param dataModel data model
     * @throws Exception exception
     */
    public void fillSide(final Map<String, Object> dataModel)
            throws Exception {
        fillLinks(dataModel);

        fillRecentArticles(dataModel);
        fillMostUsedTags(dataModel);
        fillMostCommentArticles(dataModel);
        fillMostViewCountArticles(dataModel);
    }
}
