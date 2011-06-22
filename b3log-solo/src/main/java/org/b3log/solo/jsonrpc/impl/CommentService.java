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

package org.b3log.solo.jsonrpc.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.solo.action.StatusCodes;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.Comment;
import org.b3log.solo.repository.ArticleCommentRepository;
import org.b3log.solo.repository.CommentRepository;
import org.b3log.latke.Keys;
import org.b3log.latke.action.ActionException;
import org.b3log.latke.action.util.PageCaches;
import org.b3log.latke.event.Event;
import org.b3log.latke.event.EventManager;
import org.b3log.latke.repository.Transaction;
import org.b3log.solo.SoloServletListener;
import org.b3log.solo.event.EventTypes;
import org.b3log.solo.jsonrpc.AbstractGAEJSONRpcService;
import org.b3log.solo.model.Page;
import org.b3log.solo.repository.PageCommentRepository;
import org.b3log.solo.repository.PageRepository;
import org.b3log.solo.repository.impl.ArticleCommentGAERepository;
import org.b3log.solo.repository.impl.CommentGAERepository;
import org.b3log.solo.repository.impl.PageCommentGAERepository;
import org.b3log.solo.repository.impl.PageGAERepository;
import org.b3log.solo.util.Articles;
import org.b3log.solo.util.Pages;
import org.b3log.solo.util.Preferences;
import org.b3log.solo.util.Statistics;
import org.b3log.solo.util.Users;
import org.json.JSONObject;

/**
 * Comment service for JavaScript client.
 *
 * <p>
 * Comment adding operation via servlet
 * {@link org.b3log.solo.action.impl.AddArticleCommentAction} and
 * {@link org.b3log.solo.action.impl.AddPageCommentAction}.
 * </p>
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.4.0, Jan 17, 2011
 */
public final class CommentService extends AbstractGAEJSONRpcService {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(CommentService.class.getName());
    /**
     * Comment repository.
     */
    private CommentRepository commentRepository =
            CommentGAERepository.getInstance();
    /**
     * Event manager.
     */
    private EventManager eventManager = EventManager.getInstance();
    /**
     * Article-Comment repository.
     */
    private ArticleCommentRepository articleCommentRepository =
            ArticleCommentGAERepository.getInstance();
    /**
     * Page-Comment repository.
     */
    private PageCommentRepository pageCommentRepository =
            PageCommentGAERepository.getInstance();
    /**
     * Page repository.
     */
    private PageRepository pageRepository = PageGAERepository.getInstance();
    /**
     * Article utilities.
     */
    private Articles articleUtils = Articles.getInstance();
    /**
     * Page utilities.
     */
    private Pages pageUtils = Pages.getInstance();
    /**
     * Statistic utilities.
     */
    private Statistics statistics = Statistics.getInstance();
    /**
     * User utilities.
     */
    private Users userUtils = Users.getInstance();
    /**
     * Preference utilities.
     */
    private Preferences preferenceUtils = Preferences.getInstance();

//    /**
//     * Gets recent comments with the specified http servlet request and response.
//     *
//     * @param request the specified http servlet request
//     * @param response the specified http servlet response
//     * @return for example:
//     * <pre>
//     * {
//     *     "recentComments": [{
//     *         "oId": "",
//     *         "commentName": "",
//     *         "thumbnailUrl": "",
//     *         "commentURL": "",
//     *         "commentContent": "",
//     *         "commentDate": "",
//     *      }, ....]
//     *     "sc": "GET_COMMENTS_SUCC"
//     * }
//     * </pre>
//     * @throws ActionException action exception
//     * @throws IOException io exception
//     */
//    public JSONObject getRecentComments(final HttpServletRequest request,
//                                        final HttpServletResponse response)
//            throws ActionException, IOException {
//        final JSONObject ret = new JSONObject();
//        try {
//            final JSONObject preference = preferenceUtils.getPreference();
//            if (null == preference) {
//                response.sendError(HttpServletResponse.SC_NOT_FOUND);
//                return ret;
//            }
//
//            final int fetchSize = preference.getInt(
//                    Preference.RECENT_COMMENT_DISPLAY_CNT);
//            final List<JSONObject> recentComments =
//                    commentRepository.getRecentComments(fetchSize);
//            // Erase email for security reason
//            for (final JSONObject comment : recentComments) {
//                comment.remove(Comment.COMMENT_EMAIL);
//            }
//
//            ret.put(Common.RECENT_COMMENTS, recentComments);
//            ret.put(Keys.STATUS_CODE, StatusCodes.GET_COMMENTS_SUCC);
//        } catch (final Exception e) {
//            LOGGER.log(Level.SEVERE, e.getMessage(), e);
//            throw new ActionException(e);
//        }
//
//        return ret;
//    }

    /**
     * Gets comments of an article specified by the article id for administrator.
     *
     * @param requestJSONObject the specified request json object, for example,
     * <pre>
     * {
     *     "oId": articleId
     * }
     * </pre>
     * @param request the specified http servlet request
     * @param response the specified http servlet response
     * @return for example,
     * <pre>
     * {
     *     "comments": [{
     *         "oId": "",
     *         "commentName": "",
     *         "commentEmail": "",
     *         "thumbnailUrl": "",
     *         "commentURL": "",
     *         "commentContent": "",
     *         "commentDate": "",
     *         "commentSharpURL": ""
     *      }, ....]
     *     "sc": "GET_COMMENTS_SUCC"
     * }
     * </pre>
     * @throws ActionException action exception
     * @throws IOException io exception
     */
    public JSONObject getCommentsOfArticle(final JSONObject requestJSONObject,
                                           final HttpServletRequest request,
                                           final HttpServletResponse response)
            throws ActionException, IOException {
        final JSONObject ret = new JSONObject();
        if (!userUtils.isLoggedIn()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return ret;
        }

        try {
            final String articleId = requestJSONObject.getString(Keys.OBJECT_ID);
            // Step 1: Get article-comment relations
            final List<JSONObject> articleCommentRelations =
                    articleCommentRepository.getByArticleId(articleId);
            // Step 2: Get comments
            final List<JSONObject> comments = new ArrayList<JSONObject>();
            for (int i = 0; i < articleCommentRelations.size(); i++) {
                final JSONObject articleCommentRelation =
                        articleCommentRelations.get(i);
                final String commentId =
                        articleCommentRelation.getString(Comment.COMMENT + "_"
                                                         + Keys.OBJECT_ID);

                final JSONObject comment = commentRepository.get(commentId);
                final String content = comment.getString(Comment.COMMENT_CONTENT).
                        replaceAll(SoloServletListener.ENTER_ESC, "<br/>");
                comment.put(Comment.COMMENT_CONTENT, content);
                comments.add(comment);
            }

            ret.put(Comment.COMMENTS, comments);
            ret.put(Keys.STATUS_CODE, StatusCodes.GET_COMMENTS_SUCC);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new ActionException(e);
        }

        return ret;
    }

    /**
     * Gets comments of a page specified by the page id for administrator.
     *
     * @param requestJSONObject the specified request json object, for example,
     * <pre>
     * {
     *     "oId": pageId
     * }
     * </pre>
     * @param request the specified http servlet request
     * @param response the specified http servlet response
     * @return for example,
     * <pre>
     * {
     *     "comments": [{
     *         "oId": "",
     *         "commentName": "",
     *         "commentEmail": "",
     *         "thumbnailUrl": "",
     *         "commentURL": "",
     *         "commentContent": "",
     *         "commentDate": "",
     *         "commentSharpURL": ""
     *      }, ....]
     *     "sc": "GET_COMMENTS_SUCC"
     * }
     * </pre>
     * @throws ActionException action exception
     * @throws IOException io exception
     */
    public JSONObject getCommentsOfPage(final JSONObject requestJSONObject,
                                        final HttpServletRequest request,
                                        final HttpServletResponse response)
            throws ActionException, IOException {
        final JSONObject ret = new JSONObject();
        if (!userUtils.isLoggedIn()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return ret;
        }

        try {
            final String pageId = requestJSONObject.getString(Keys.OBJECT_ID);
            // Step 1: Get page-comment relations
            final List<JSONObject> pageCommentRelations =
                    pageCommentRepository.getByPageId(pageId);
            // Step 2: Get comments
            final List<JSONObject> comments = new ArrayList<JSONObject>();
            for (int i = 0; i < pageCommentRelations.size(); i++) {
                final JSONObject pageCommentRelation =
                        pageCommentRelations.get(i);
                final String commentId =
                        pageCommentRelation.getString(Comment.COMMENT + "_"
                                                      + Keys.OBJECT_ID);

                final JSONObject comment = commentRepository.get(commentId);
                comments.add(comment);
            }

            ret.put(Comment.COMMENTS, comments);
            ret.put(Keys.STATUS_CODE, StatusCodes.GET_COMMENTS_SUCC);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new ActionException(e);
        }

        return ret;
    }

    /**
     * Removes a comment of an article by the specified request json object.
     *
     * @param requestJSONObject the specified request json object, for example,
     * <pre>
     * {
     *     "oId": commentId,
     * }
     * </pre>
     * @param request the specified http servlet request
     * @param response the specified http servlet response
     * @return for example,
     * <pre>
     * {
     *     "sc": "REMOVE_COMMENT_SUCC"
     * }
     * </pre>
     * @throws ActionException action exception
     * @throws IOException io exception
     */
    public JSONObject removeCommentOfArticle(final JSONObject requestJSONObject,
                                             final HttpServletRequest request,
                                             final HttpServletResponse response)
            throws ActionException, IOException {
        final JSONObject ret = new JSONObject();
        if (!userUtils.isLoggedIn()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return ret;
        }

        final Transaction transaction = commentRepository.beginTransaction();
        try {
            final String commentId = requestJSONObject.getString(Keys.OBJECT_ID);
            LOGGER.log(Level.FINER, "Removing comment[oId={0}]", commentId);

            // Step 1: Remove article-comment relation
            final JSONObject articleCommentRelation =
                    articleCommentRepository.getByCommentId(commentId);
            final String articleCommentRelationId =
                    articleCommentRelation.getString(Keys.OBJECT_ID);
            articleCommentRepository.remove(articleCommentRelationId);

            final String articleId = articleCommentRelation.getString(
                    Article.ARTICLE + "_" + Keys.OBJECT_ID);

            if (!userUtils.canAccessArticle(articleId)) {
                ret.put(Keys.STATUS_CODE,
                        StatusCodes.REMOVE_COMMENT_FAIL_FORBIDDEN);

                return ret;
            }

            // Step 2: Remove comment
            commentRepository.remove(commentId);
            // Step 3: Update article comment count
            articleUtils.decArticleCommentCount(articleId);
            // Step 4: Update blog statistic comment count
            statistics.decBlogCommentCount();
            statistics.decPublishedBlogCommentCount();
            // Step 5: Fire remove comment event
            eventManager.fireEventSynchronously(
                    new Event<String>(EventTypes.REMOVE_COMMENT, articleId));

            PageCaches.removeAll();

            transaction.commit();
            ret.put(Keys.STATUS_CODE, StatusCodes.REMOVE_COMMENT_SUCC);

            LOGGER.log(Level.FINER, "Removed comment[oId={0}]", commentId);
        } catch (final Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new ActionException(e);
        }

        return ret;
    }

    /**
     * Removes a comment of a page by the specified request json object.
     *
     * @param requestJSONObject the specified request json object, for example,
     * <pre>
     * {
     *     "oId": commentId,
     * }
     * </pre>
     * @param request the specified http servlet request
     * @param response the specified http servlet response
     * @return for example,
     * <pre>
     * {
     *     "sc": "REMOVE_COMMENT_SUCC"
     * }
     * </pre>
     * @throws ActionException action exception
     * @throws IOException io exception
     */
    public JSONObject removeCommentOfPage(final JSONObject requestJSONObject,
                                          final HttpServletRequest request,
                                          final HttpServletResponse response)
            throws ActionException, IOException {
        final JSONObject ret = new JSONObject();
        if (!userUtils.isAdminLoggedIn()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return ret;
        }

        final Transaction transaction = commentRepository.beginTransaction();
        try {
            final String commentId = requestJSONObject.getString(Keys.OBJECT_ID);
            LOGGER.log(Level.FINER, "Removing comment[oId={0}]", commentId);

            // Step 1: Remove page-comment relation
            final JSONObject pageCommentRelation =
                    pageCommentRepository.getByCommentId(commentId);
            final String pageCommentRelationId =
                    pageCommentRelation.getString(Keys.OBJECT_ID);
            pageCommentRepository.remove(pageCommentRelationId);

            final String pageId = pageCommentRelation.getString(
                    Page.PAGE + "_" + Keys.OBJECT_ID);
            // Step 2: Remove comment
            commentRepository.remove(commentId);
            // Step 3: Update page comment count
            pageUtils.decPageCommentCount(pageId);
            // Step 4: Update blog statistic comment count
            statistics.decBlogCommentCount();
            statistics.decPublishedBlogCommentCount();
            // Step 5: Fire remove comment event
            eventManager.fireEventSynchronously(
                    new Event<String>(EventTypes.REMOVE_COMMENT, pageId));

            PageCaches.removeAll();

            transaction.commit();
            ret.put(Keys.STATUS_CODE, StatusCodes.REMOVE_COMMENT_SUCC);

            LOGGER.log(Level.FINER,
                       "Removed comment[oId={0}] of page[{oId={1}}]",
                       new String[]{commentId, pageId});
        } catch (final Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new ActionException(e);
        }

        return ret;
    }

    /**
     * Gets the {@link CommentService} singleton.
     *
     * @return the singleton
     */
    public static CommentService getInstance() {
        return SingletonHolder.SINGLETON;
    }

    /**
     * Private default constructor.
     */
    private CommentService() {
    }

    /**
     * Singleton holder.
     *
     * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
     * @version 1.0.0.0, Jan 12, 2011
     */
    private static final class SingletonHolder {

        /**
         * Singleton.
         */
        private static final CommentService SINGLETON = new CommentService();

        /**
         * Private default constructor.
         */
        private SingletonHolder() {
        }
    }
}
