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

import com.google.appengine.api.users.UserServiceFactory;
import com.google.inject.Inject;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.solo.util.ArticleUtils;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.Common;
import org.b3log.solo.repository.ArticleRepository;
import org.b3log.solo.repository.TagRepository;
import org.b3log.latke.Keys;
import org.b3log.latke.action.util.Paginator;
import org.b3log.latke.model.Pagination;
import org.b3log.latke.model.User;
import org.b3log.latke.repository.SortDirection;
import org.b3log.solo.model.ArchiveDate;
import org.b3log.solo.model.Link;
import org.b3log.solo.model.Preference;
import org.b3log.solo.repository.CommentRepository;
import org.b3log.solo.repository.LinkRepository;
import org.b3log.solo.servlet.SoloServletListener;
import org.b3log.solo.util.ArchiveDateUtils;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Filler utilities.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.7, Sep 1, 2010
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
     * Link repository.
     */
    @Inject
    private LinkRepository linkRepository;
    /**
     * Archive date utilities.
     */
    @Inject
    private ArchiveDateUtils archiveDateUtils;
    /**
     * User service.
     */
    private com.google.appengine.api.users.UserService userService =
            UserServiceFactory.getUserService();

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
        final JSONObject preference = SoloServletListener.getUserPreference();

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

        dataModel.put(Pagination.PAGINATION_WINDOW_SIZE, windowSize);
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
        final JSONObject preference = SoloServletListener.getUserPreference();
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
        final List<JSONObject> archiveDates = archiveDateUtils.getArchiveDates();
        for (final JSONObject archiveDate : archiveDates) {
            final Date date = (Date) archiveDate.get(ArchiveDate.ARCHIVE_DATE);
            final String dateString = ArchiveDate.DATE_FORMAT.format(date);
            final String[] dateStrings = dateString.split("/");
            final String year = dateStrings[0];
            final String month = dateStrings[1];
            archiveDate.put(ArchiveDate.ARCHIVE_DATE_YEAR, year);
            archiveDate.put(ArchiveDate.ARCHIVE_DATE_MONTH, month);
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
        final JSONObject preference = SoloServletListener.getUserPreference();
        final int mostCommentArticleDisplayCnt =
                preference.getInt(Preference.MOST_COMMENT_ARTICLE_DISPLAY_CNT);
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
        final JSONObject preference = SoloServletListener.getUserPreference();
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
        final JSONObject preference = SoloServletListener.getUserPreference();
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
        final JSONObject preference = SoloServletListener.getUserPreference();
        final int recentCommentDisplayCnt =
                preference.getInt(Preference.RECENT_COMMENT_DISPLAY_CNT);

        final List<JSONObject> recentComments =
                commentRepository.getRecentComments(recentCommentDisplayCnt);

        dataModel.put(Common.RECENT_COMMENTS, recentComments);
    }

    /**
     * Fills article-footer.html.
     *
     * @param dataModel data model
     * @param request http servlet request
     */
    public void fillBlogFooter(final Map<String, Object> dataModel,
                               final HttpServletRequest request) {
        dataModel.put(Common.VERSION, SoloServletListener.VERSION);
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
        LOGGER.finest("Filling blog header....");
        final JSONObject preference = SoloServletListener.getUserPreference();
        final String blogTitle = preference.getString(Preference.BLOG_TITLE);
        final String blogSubtitle = preference.getString(
                Preference.BLOG_SUBTITLE);

        dataModel.put(Preference.BLOG_TITLE, blogTitle);
        dataModel.put(Preference.BLOG_SUBTITLE, blogSubtitle);
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
//        fillRecentArticles(dataModel);
//        fillRecentComments(dataModel);
        fillMostUsedTags(dataModel);
        fillMostCommentArticles(dataModel);
        fillMostViewCountArticles(dataModel);

        final JSONObject preference = SoloServletListener.getUserPreference();
        final String adminGmail = preference.getString(Preference.ADMIN_GMAIL);
        LOGGER.log(Level.FINER, "Current user[userId={0}]", adminGmail);
        dataModel.put(User.USER_EMAIL, adminGmail);
    }
}
