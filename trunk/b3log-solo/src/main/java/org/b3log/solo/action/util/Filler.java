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

package org.b3log.solo.action.util;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.inject.Inject;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.solo.util.ArticleUtils;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.Common;
import org.b3log.solo.repository.ArticleRepository;
import org.b3log.solo.repository.TagRepository;
import org.b3log.latke.Keys;
import org.b3log.latke.action.util.Paginator;
import org.b3log.latke.model.Pagination;
import org.b3log.latke.repository.Filter;
import org.b3log.latke.repository.FilterOperator;
import org.b3log.latke.repository.SortDirection;
import org.b3log.latke.util.Locales;
import org.b3log.solo.model.ArchiveDate;
import org.b3log.solo.model.Link;
import org.b3log.solo.model.Preference;
import org.b3log.solo.repository.CommentRepository;
import org.b3log.solo.repository.LinkRepository;
import org.b3log.solo.SoloServletListener;
import org.b3log.solo.jsonrpc.impl.ArticleService;
import org.b3log.solo.model.Comment;
import org.b3log.solo.model.Page;
import org.b3log.solo.model.Statistic;
import org.b3log.solo.repository.PageRepository;
import org.b3log.solo.repository.StatisticRepository;
import org.b3log.solo.util.ArchiveDateUtils;
import org.b3log.solo.util.CommentUtils;
import org.b3log.solo.util.PreferenceUtils;
import org.b3log.solo.util.TagUtils;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Filler utilities.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.1.8, Dec 3, 2010
 */
public final class Filler {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(Filler.class.getName());
    /**
     * Article repository.
     */
    @Inject
    private ArticleRepository articleRepository;
    /**
     * Comment repository.
     */
    @Inject
    private CommentRepository commentRepository;
    /**
     * Tag repository.
     */
    @Inject
    private TagRepository tagRepository;
    /**
     * Article utilities.
     */
    @Inject
    private ArticleUtils articleUtils;
    /**
     * Comment utilities.
     */
    @Inject
    private CommentUtils commentUtils;
    /**
     * Tag utilities.
     */
    @Inject
    private TagUtils tagUtils;
    /**
     * Link repository.
     */
    @Inject
    private LinkRepository linkRepository;
    /**
     * Page repository.
     */
    @Inject
    private PageRepository pageRepository;
    /**
     * Archive date utilities.
     */
    @Inject
    private ArchiveDateUtils archiveDateUtils;
    /**
     * Preference utilities.
     */
    @Inject
    private PreferenceUtils preferenceUtils;
    /**
     * Statistic repository.
     */
    @Inject
    private StatisticRepository statisticRepository;
    /**
     * User service.
     */
    private static final UserService USER_SERVICE =
            UserServiceFactory.getUserService();

    /**
     * Fills articles in index.ftl.
     *
     * @param dataModel data model
     * @param currentPageNum current page number
     * @throws Exception exception
     */
    @SuppressWarnings("unchecked")
    public void fillIndexArticles(final Map<String, Object> dataModel,
                                  final int currentPageNum)
            throws Exception {
        final JSONObject preference = preferenceUtils.getPreference();
        if (null == preference) {
            throw new Exception("Not found preference");
        }

        final int pageSize =
                preference.getInt(Preference.ARTICLE_LIST_DISPLAY_COUNT);
        final int windowSize =
                preference.getInt(Preference.ARTICLE_LIST_PAGINATION_WINDOW_SIZE);

        final Map<String, SortDirection> sorts =
                new HashMap<String, SortDirection>();
        sorts.put(Article.ARTICLE_UPDATE_DATE, SortDirection.DESCENDING);
        sorts.put(Article.ARTICLE_PUT_TOP, SortDirection.DESCENDING);
        final Set<Filter> filters = new HashSet<Filter>();
        filters.add(new Filter(Article.ARTICLE_IS_PUBLISHED,
                               FilterOperator.EQUAL,
                               true)); // Filter unpublished articles
        final JSONObject result =
                articleRepository.get(currentPageNum, pageSize,
                                      sorts, filters);

        final int pageCount = result.getJSONObject(Pagination.PAGINATION).
                getInt(Pagination.PAGINATION_PAGE_COUNT);

        final List<Integer> pageNums = Paginator.paginate(currentPageNum,
                                                          pageSize,
                                                          pageCount,
                                                          windowSize);
        if (0 != pageNums.size()) {
            dataModel.put(Pagination.PAGINATION_FIRST_PAGE_NUM,
                          pageNums.get(0));
            dataModel.put(Pagination.PAGINATION_LAST_PAGE_NUM,
                          pageNums.get(pageNums.size() - 1));
        }
        dataModel.put(Pagination.PAGINATION_PAGE_COUNT, pageCount);
        dataModel.put(Pagination.PAGINATION_PAGE_NUMS, pageNums);

        final List<JSONObject> articles = org.b3log.latke.util.CollectionUtils.
                jsonArrayToList(result.getJSONArray(Keys.RESULTS));
        for (final JSONObject article : articles) {
            final Date updateDate =
                    (Date) article.get(Article.ARTICLE_UPDATE_DATE);
            article.put(Common.HAS_UPDATED,
                        !ArticleService.DEFAULT_UPDATE_DATE.equals(updateDate));
            LOGGER.info("~~~~~~~~~~~" + !ArticleService.DEFAULT_UPDATE_DATE.equals(updateDate));
        }

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
        LOGGER.finer("Filling most used tags....");
        final JSONObject preference = preferenceUtils.getPreference();
        if (null == preference) {
            throw new Exception("Not found preference");
        }

        final int mostUsedTagDisplayCnt =
                preference.getInt(Preference.MOST_USED_TAG_DISPLAY_CNT);

        final List<JSONObject> tags =
                tagRepository.getMostUsedTags(mostUsedTagDisplayCnt);
        tagUtils.removeForUnpublishedArticles(tags);

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
        final JSONObject preference = preferenceUtils.getPreference();
        if (null == preference) {
            throw new Exception("Not found preference");
        }

        final List<JSONObject> archiveDates = archiveDateUtils.getArchiveDates();
        archiveDateUtils.removeForUnpublishedArticles(archiveDates);

        final String localeString = preference.getString(
                Preference.LOCALE_STRING);
        final String language = Locales.getLanguage(localeString);

        for (final JSONObject archiveDate : archiveDates) {
            final Date date = (Date) archiveDate.get(ArchiveDate.ARCHIVE_DATE);
            final String dateString = ArchiveDate.DATE_FORMAT.format(date);
            final String[] dateStrings = dateString.split("/");
            final String year = dateStrings[0];
            final String month = dateStrings[1];
            archiveDate.put(ArchiveDate.ARCHIVE_DATE_YEAR, year);

            if ("en".equals(language)) {
                archiveDate.put(ArchiveDate.ARCHIVE_DATE_MONTH,
                                SoloServletListener.EN_MONTHS.get(month));
            } else {
                archiveDate.put(ArchiveDate.ARCHIVE_DATE_MONTH, month);
            }
        }

        dataModel.put(ArchiveDate.ARCHIVE_DATES, archiveDates);
    }

    /**
     * Fills most view count articles.
     *
     * @param dataModel data model
     * @throws Exception exception
     */
    public void fillMostViewCountArticles(final Map<String, Object> dataModel)
            throws Exception {
        final JSONObject preference = preferenceUtils.getPreference();
        if (null == preference) {
            throw new Exception("Not found preference");
        }

        final int mostCommentArticleDisplayCnt =
                preference.getInt(Preference.MOST_VIEW_ARTICLE_DISPLAY_CNT);
        final List<JSONObject> mostViewCountArticles =
                articleRepository.getMostViewCountArticles(
                mostCommentArticleDisplayCnt);

        dataModel.put(Common.MOST_VIEW_COUNT_ARTICLES, mostViewCountArticles);
    }

    /**
     * Fills most comments articles.
     *
     * @param dataModel data model
     * @throws Exception exception
     */
    public void fillMostCommentArticles(final Map<String, Object> dataModel)
            throws Exception {
        LOGGER.finer("Filling most comment articles....");
        final JSONObject preference = preferenceUtils.getPreference();
        if (null == preference) {
            throw new Exception("Not found preference");
        }

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
        final JSONObject preference = preferenceUtils.getPreference();
        if (null == preference) {
            throw new Exception("Not found preference");
        }

        final int recentArticleDisplayCnt =
                preference.getInt(Preference.RECENT_ARTICLE_DISPLAY_CNT);

        final List<JSONObject> recentArticles =
                articleRepository.getRecentArticles(recentArticleDisplayCnt);

        dataModel.put(Common.RECENT_ARTICLES, recentArticles);
    }

    /**
     * Fills post comments recently.
     *
     * @param dataModel data model
     * @throws Exception exception
     */
    public void fillRecentComments(final Map<String, Object> dataModel)
            throws Exception {
        LOGGER.finer("Filling recent comments....");
        final JSONObject preference = preferenceUtils.getPreference();
        if (null == preference) {
            throw new Exception("Not found preference");
        }

        final int recentCommentDisplayCnt =
                preference.getInt(Preference.RECENT_COMMENT_DISPLAY_CNT);

        final List<JSONObject> recentComments =
                commentRepository.getRecentComments(recentCommentDisplayCnt);
        commentUtils.removeForUnpublishedArticles(recentComments);

        // Erase email for security reason
        for (final JSONObject comment : recentComments) {
            comment.remove(Comment.COMMENT_EMAIL);
        }

        dataModel.put(Common.RECENT_COMMENTS, recentComments);
    }

    /**
     * Fills article-footer.ftl.
     *
     * @param dataModel data model
     * @throws Exception exception
     */
    public void fillBlogFooter(final Map<String, Object> dataModel)
            throws Exception {
        final JSONObject preference = preferenceUtils.getPreference();
        if (null == preference) {
            throw new Exception("Not found preference");
        }

        final String blogTitle = preference.getString(Preference.BLOG_TITLE);
        dataModel.put(Preference.BLOG_TITLE, blogTitle);
        final String blogHost = preference.getString(Preference.BLOG_HOST);
        dataModel.put(Preference.BLOG_HOST, blogHost);

        dataModel.put(Common.VERSION, SoloServletListener.VERSION);
    }

    /**
     * Fills article-header.ftl.
     *
     * @param dataModel data model
     * @throws Exception exception
     */
    public void fillBlogHeader(final Map<String, Object> dataModel)
            throws Exception {
        final JSONObject preference = preferenceUtils.getPreference();
        if (null == preference) {
            throw new Exception("Not found preference");
        }

        dataModel.put(Preference.LOCALE_STRING,
                      preference.getString(Preference.LOCALE_STRING));
        dataModel.put(Preference.BLOG_TITLE,
                      preference.getString(Preference.BLOG_TITLE));
        dataModel.put(Preference.BLOG_SUBTITLE,
                      preference.getString(Preference.BLOG_SUBTITLE));
        dataModel.put(Preference.HTML_HEAD,
                      preference.getString(Preference.HTML_HEAD));
        dataModel.put(Preference.META_KEYWORDS,
                      preference.getString(Preference.META_KEYWORDS));
        dataModel.put(Preference.META_DESCRIPTION,
                      preference.getString(Preference.META_DESCRIPTION));

        fillPageNavigations(dataModel);
        fillStatistic(dataModel);
    }

    /**
     * Fills article-side.ftl.
     *
     * @param dataModel data model
     * @throws Exception exception
     */
    public void fillSide(final Map<String, Object> dataModel)
            throws Exception {
        fillLinks(dataModel);
//        fillRecentArticles(dataModel);
        fillRecentComments(dataModel);
        fillMostUsedTags(dataModel);
        fillMostCommentArticles(dataModel);
        fillMostViewCountArticles(dataModel);
        fillArchiveDates(dataModel);

        final JSONObject preference = preferenceUtils.getPreference();
        if (null == preference) {
            throw new Exception("Not found preference");
        }

        final String noticeBoard =
                preference.getString(Preference.NOTICE_BOARD);
        dataModel.put(Preference.NOTICE_BOARD, noticeBoard);
    }

    /**
     * Fills page navigations.
     *
     * @param dataModel data model
     * @throws Exception exception
     */
    private void fillPageNavigations(final Map<String, Object> dataModel)
            throws Exception {
        final Map<String, SortDirection> sorts =
                new HashMap<String, SortDirection>();
        sorts.put(Page.PAGE_ORDER, SortDirection.ASCENDING);
        final JSONObject result = pageRepository.get(1,
                                                     Integer.MAX_VALUE, sorts);
        final List<JSONObject> pageNavigations =
                org.b3log.latke.util.CollectionUtils.jsonArrayToList(result.
                getJSONArray(Keys.RESULTS));

        dataModel.put(Common.PAGE_NAVIGATIONS, pageNavigations);
    }

    /**
     * Fills statistic.
     *
     * @param dataModel data model
     * @throws Exception exception
     */
    private void fillStatistic(final Map<String, Object> dataModel)
            throws Exception {
        final JSONObject statistic =
                statisticRepository.get(Statistic.STATISTIC);

        dataModel.put(Statistic.STATISTIC, statistic);
    }
}
