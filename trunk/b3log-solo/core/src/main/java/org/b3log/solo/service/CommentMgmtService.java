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
package org.b3log.solo.service;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.service.ServiceException;
import org.b3log.solo.model.Comment;
import org.b3log.solo.repository.ArticleRepository;
import org.b3log.solo.repository.CommentRepository;
import org.b3log.solo.repository.impl.ArticleRepositoryImpl;
import org.b3log.solo.repository.impl.CommentRepositoryImpl;
import org.b3log.solo.util.Articles;
import org.b3log.solo.util.Pages;
import org.b3log.solo.util.Statistics;
import org.json.JSONObject;

/**
 * Comment management service.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Oct 28, 2011
 * @since 0.3.5
 */
public final class CommentMgmtService {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(CommentMgmtService.class.getName());
    /**
     * Comment repository.
     */
    private CommentRepository commentRepository =
            CommentRepositoryImpl.getInstance();
    /**
     * Article repository.
     */
    private ArticleRepository articleRepository =
            ArticleRepositoryImpl.getInstance();
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
     * Removes a comment of a page with the specified comment id.
     * 
     * @param commentId the given comment id
     * @throws ServiceException service exception
     */
    public void removePageComment(final String commentId)
            throws ServiceException {
        final Transaction transaction = commentRepository.beginTransaction();

        try {
            final JSONObject comment = commentRepository.get(commentId);
            final String pageId = comment.getString(Comment.COMMENT_ON_ID);
            // Step 1: Remove comment
            commentRepository.remove(commentId);
            // Step 2: Update page comment count
            pageUtils.decPageCommentCount(pageId);
            // Step 3: Update blog statistic comment count
            statistics.decBlogCommentCount();
            statistics.decPublishedBlogCommentCount();

            transaction.commit();
        } catch (final Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }

            LOGGER.log(Level.SEVERE, "Removes a comment of a page failed", e);
            throw new ServiceException(e);
        }
    }

    /**
     * Removes a comment of an article with the specified comment id.
     *
     * @param commentId the given comment id
     * @throws ServiceException service exception
     */
    public void removeArticleComment(final String commentId)
            throws ServiceException {
        final Transaction transaction = commentRepository.beginTransaction();

        try {
            final JSONObject comment = commentRepository.get(commentId);
            final String articleId = comment.getString(Comment.COMMENT_ON_ID);

            // Step 1: Remove comment
            commentRepository.remove(commentId);
            // Step 2: Update article comment count
            articleUtils.decArticleCommentCount(articleId);
            // Step 3: Update blog statistic comment count
            statistics.decBlogCommentCount();
            statistics.decPublishedBlogCommentCount();

            transaction.commit();
        } catch (final Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }

            LOGGER.log(Level.SEVERE, "Removes a comment of an article failed", e);
            throw new ServiceException(e);
        }
    }

    /**
     * Gets the {@link CommentMgmtService} singleton.
     *
     * @return the singleton
     */
    public static CommentMgmtService getInstance() {
        return SingletonHolder.SINGLETON;
    }

    /**
     * Private constructor.
     */
    private CommentMgmtService() {
    }

    /**
     * Singleton holder.
     *
     * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
     * @version 1.0.0.0, Oct 18, 2011
     */
    private static final class SingletonHolder {

        /**
         * Singleton.
         */
        private static final CommentMgmtService SINGLETON =
                new CommentMgmtService();

        /**
         * Private default constructor.
         */
        private SingletonHolder() {
        }
    }
}
