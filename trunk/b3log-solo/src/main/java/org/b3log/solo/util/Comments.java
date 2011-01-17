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

import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import org.b3log.solo.model.Article;
import org.b3log.solo.repository.ArticleCommentRepository;
import org.b3log.latke.Keys;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.solo.repository.ArticleRepository;
import org.b3log.solo.repository.impl.ArticleCommentGAERepository;
import org.b3log.solo.repository.impl.ArticleGAERepository;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Comment utilities.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.4, Jan 17, 2011
 */
public final class Comments {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(Comments.class.getName());
    /**
     * Article-Comment repository.
     */
    private ArticleCommentRepository articleCommentRepository =
            ArticleCommentGAERepository.getInstance();
    /**
     * Article repository.
     */
    private ArticleRepository articleRepository =
            ArticleGAERepository.getInstance();

    /**
     * Removes comments of unpublished articles for the specified comments.
     *
     * @param comments the specified comments
     * @throws JSONException json exception
     * @throws RepositoryException repository exception
     */
    public void removeForUnpublishedArticles(
            final List<JSONObject> comments) throws JSONException,
                                                    RepositoryException {
        LOGGER.finer("Removing unpublished articles' comments....");
        final Iterator<JSONObject> iterator = comments.iterator();
        while (iterator.hasNext()) {
            final JSONObject comment = iterator.next();
            final String commentId = comment.getString(Keys.OBJECT_ID);
            final JSONObject articleCommentRelation =
                    articleCommentRepository.getByCommentId(commentId);
            if (null == articleCommentRelation) {
                continue; // This comment is a page comment or comment has been removed just
            }
            final String articleId = articleCommentRelation.getString(
                    Article.ARTICLE + "_" + Keys.OBJECT_ID);
            if (!articleRepository.isPublished(articleId)) {
                iterator.remove();
            }
        }

        LOGGER.finer("Removed unpublished articles' comments....");
    }

    /**
     * Gets the {@link Comments} singleton.
     *
     * @return the singleton
     */
    public static Comments getInstance() {
        return SingletonHolder.SINGLETON;
    }

    /**
     * Private default constructor.
     */
    private Comments() {
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
        private static final Comments SINGLETON = new Comments();

        /**
         * Private default constructor.
         */
        private SingletonHolder() {
        }
    }
}
