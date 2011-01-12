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

package org.b3log.solo.util;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.solo.SoloServletListener;
import org.b3log.solo.model.Comment;
import org.b3log.solo.model.Common;
import org.b3log.solo.model.Page;
import org.b3log.solo.repository.CommentRepository;
import org.b3log.solo.repository.PageCommentRepository;
import org.b3log.solo.repository.PageRepository;
import org.b3log.solo.repository.impl.CommentGAERepository;
import org.b3log.solo.repository.impl.PageCommentGAERepository;
import org.b3log.solo.repository.impl.PageGAERepository;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Page utilities.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.2, Jan 12, 2011
 */
public final class Pages {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(Pages.class.getName());
    /**
     * Comment repository.
     */
    private CommentRepository commentRepository =
            CommentGAERepository.getInstance();
    /**
     * Page-Comment repository.
     */
    private PageCommentRepository pageCommentRepository =
            PageCommentGAERepository.getInstance();
    /**
     * Page repository.
     */
    private PageRepository pageRepository =
            PageGAERepository.getInstance();
    /**
     * Statistic utilities.
     */
    private Statistics statistics = Statistics.getInstance();

    /**
     * Gets comments of an article specified by the page id.
     *
     * @param pageId the specified page id
     * @return a list of comments, returns an empty list if not found
     * @throws RepositoryException repository exception
     * @throws JSONException json exception
     */
    public List<JSONObject> getComments(final String pageId)
            throws JSONException, RepositoryException {
        final List<JSONObject> ret = new ArrayList<JSONObject>();
        final List<JSONObject> pageCommentRelations =
                pageCommentRepository.getByPageId(pageId);
        for (int i = 0; i < pageCommentRelations.size(); i++) {
            final JSONObject pageCommentRelation =
                    pageCommentRelations.get(i);
            final String commentId =
                    pageCommentRelation.getString(Comment.COMMENT + "_"
                                                  + Keys.OBJECT_ID);

            final JSONObject comment = commentRepository.get(commentId);
            final String content = comment.getString(Comment.COMMENT_CONTENT).
                    replaceAll(SoloServletListener.ENTER_ESC, "<br/>");
            comment.put(Comment.COMMENT_CONTENT, content);
            comment.remove(Comment.COMMENT_EMAIL); // Remove email

            if (comment.has(Comment.COMMENT_ORIGINAL_COMMENT_ID)) {
                comment.put(Common.IS_REPLY, true);
                final String originalCommentName = comment.getString(
                        Comment.COMMENT_ORIGINAL_COMMENT_NAME);
                comment.put(Comment.COMMENT_ORIGINAL_COMMENT_NAME,
                            originalCommentName);
            } else {
                comment.put(Common.IS_REPLY, false);
            }

            ret.add(comment);
        }

        return ret;
    }

    /**
     * Page comment count +1 for an page specified by the given page id.
     *
     * @param pageId the given page id
     * @throws JSONException json exception
     * @throws RepositoryException repository exception
     */
    public void incPageCommentCount(final String pageId)
            throws JSONException, RepositoryException {
        final JSONObject page = pageRepository.get(pageId);
        final JSONObject newPage =
                new JSONObject(page, JSONObject.getNames(page));
        final int commentCnt = page.getInt(Page.PAGE_COMMENT_COUNT);
        newPage.put(Page.PAGE_COMMENT_COUNT, commentCnt + 1);
        pageRepository.update(pageId, newPage);
    }

    /**
     * Page comment count -1 for an page specified by the given page id.
     *
     * @param pageId the given page id
     * @throws JSONException json exception
     * @throws RepositoryException repository exception
     */
    public void decPageCommentCount(final String pageId)
            throws JSONException, RepositoryException {
        final JSONObject page = pageRepository.get(pageId);
        final JSONObject newPage =
                new JSONObject(page, JSONObject.getNames(page));
        final int commentCnt = page.getInt(Page.PAGE_COMMENT_COUNT);
        newPage.put(Page.PAGE_COMMENT_COUNT, commentCnt - 1);
        pageRepository.update(pageId, newPage);
    }

    /**
     * Removes page comments by the specified page id.
     *
     * <p>
     * Removes related comments, page-comment relations, sets page/blog
     * comment statistic count.
     * </p>
     *
     * @param pageId the specified page id
     * @throws JSONException json exception
     * @throws RepositoryException repository exception
     */
    public void removePageComments(final String pageId)
            throws JSONException, RepositoryException {
        final List<JSONObject> pageCommentRelations =
                pageCommentRepository.getByPageId(pageId);
        for (int i = 0; i < pageCommentRelations.size(); i++) {
            final JSONObject pageCommentRelation =
                    pageCommentRelations.get(i);
            final String commentId =
                    pageCommentRelation.getString(Comment.COMMENT + "_"
                                                  + Keys.OBJECT_ID);
            commentRepository.remove(commentId);
            final String relationId =
                    pageCommentRelation.getString(Keys.OBJECT_ID);
            pageCommentRepository.remove(relationId);
        }

        int blogCommentCount = statistics.getBlogCommentCount();
        final int pageCommentCount = pageCommentRelations.size();
        blogCommentCount -= pageCommentCount;
        statistics.setBlogCommentCount(blogCommentCount);
    }

    /**
     * Gets the {@link Pages} singleton.
     *
     * @return the singleton
     */
    public static Pages getInstance() {
        return SingletonHolder.SINGLETON;
    }

    /**
     * Private default constructor.
     */
    private Pages() {
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
        private static final Pages SINGLETON = new Pages();

        /**
         * Private default constructor.
         */
        private SingletonHolder() {
        }
    }
}
