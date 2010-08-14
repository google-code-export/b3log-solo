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
package org.b3log.solo.client.action.util;

import com.google.inject.Inject;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.b3log.solo.client.util.ArticleUtils;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.Common;
import org.b3log.solo.model.Tag;
import org.b3log.solo.repository.ArticleRepository;
import org.b3log.solo.repository.TagArticleRepository;
import org.b3log.solo.repository.TagRepository;
import org.b3log.latke.Keys;
import org.b3log.latke.client.action.util.Paginator;
import org.b3log.latke.model.Pagination;
import org.b3log.latke.repository.SortDirection;
import org.json.JSONObject;

/**
 * Filler utilities.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.2, Aug 12, 2010
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
     * Page size of pagination.
     */
    public static final int PAGE_SIZE = 2;
    /**
     * Window size of pagination.
     */
    public static final int WINDOW_SIZE = 20;
    /**
     * Count of recent articles.
     */
    public static final int RECENT_ARTICLE_CNT = 10;
    /**
     * Count of most used tags.
     */
    public static final int MOST_USED_TAG_CNT = 10;
    /**
     * Article utilities.
     */
    @Inject
    private ArticleUtils articleUtils;

    /**
     * Fills articles in index.html.
     *
     * @param dataModel data model
     * @param currentPageNum current page number
     * @throws Exception exception
     */
    public void fillIndexArticles(final Map<String, Object> dataModel,
                                  final int currentPageNum)
            throws Exception {
        final JSONObject result =
                articleRepository.get(currentPageNum,
                                      PAGE_SIZE,
                                      Article.ARTICLE_UPDATE_DATE,
                                      SortDirection.DESCENDING);

        final int pageCount = result.getJSONObject(Pagination.PAGINATION).
                getInt(Pagination.PAGINATION_PAGE_COUNT);

        final List<Integer> pageNums = Paginator.paginate(currentPageNum,
                                                          PAGE_SIZE,
                                                          pageCount,
                                                          WINDOW_SIZE);

        dataModel.put(Pagination.PAGINATION_PAGE_COUNT, pageCount);
        dataModel.put(Pagination.PAGINATION_PAGE_NUMS, pageNums);
        final List<JSONObject> articles = org.b3log.latke.util.CollectionUtils.
                jsonArrayToList(result.getJSONArray(Keys.RESULTS));
        articleUtils.addTags(articles);

        dataModel.put(Article.ARTICLES, articles);
    }

    /**
     * Fills most used tags.
     *
     * @param dataModel data model
     * @throws Exception exception
     */
    public void fillMostUsedTags(final Map<String, Object> dataModel)
            throws Exception {
        final List<JSONObject> tags =
                tagRepository.getMostUsedTags(MOST_USED_TAG_CNT);

        dataModel.put(Common.MOST_USED_TAGS, tags);
    }

    /**
     * Fills most comments articles.
     *
     * @param dataModel data model
     * @throws Exception exception
     */
    public void fillMostCommentArticles(final Map<String, Object> dataModel)
            throws Exception {
        final List<JSONObject> mostCommentArticles =
                articleRepository.getMostCommentArticles(RECENT_ARTICLE_CNT);

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
        final List<JSONObject> recentArticles =
                articleRepository.getRecentArticles(RECENT_ARTICLE_CNT);

        dataModel.put(Common.RECENT_ARTICLES, recentArticles);
    }

    /**
     * Fills article-side.html.
     *
     * @param dataModel data model
     * @throws Exception exception
     */
    public void fillSide(final Map<String, Object> dataModel)
            throws Exception {
        final JSONObject result = tagRepository.get(1, Integer.MAX_VALUE);
        final List<JSONObject> tags =
                org.b3log.latke.util.CollectionUtils.jsonArrayToList(
                result.getJSONArray(Keys.RESULTS));

        dataModel.put(Tag.TAGS, tags);

        fillRecentArticles(dataModel);
        fillMostUsedTags(dataModel);
        fillMostCommentArticles(dataModel);
    }
}
