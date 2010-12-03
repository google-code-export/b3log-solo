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

package org.b3log.solo.util;

import com.google.inject.Inject;
import java.util.List;
import java.util.logging.Logger;
import org.b3log.solo.model.Article;
import org.b3log.solo.repository.ArticleCommentRepository;
import org.b3log.latke.Keys;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.solo.repository.ArticleRepository;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Comment utilities.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Dec 3, 2010
 */
public final class CommentUtils {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(CommentUtils.class.getName());
    /**
     * Article-Comment repository.
     */
    @Inject
    private ArticleCommentRepository articleCommentRepository;
    /**
     * Article repository.
     */
    @Inject
    private ArticleRepository articleRepository;

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
        for (final JSONObject comment : comments) {
            final String commentId = comment.getString(Keys.OBJECT_ID);
            final JSONObject articleCommentRelation =
                    articleCommentRepository.getByCommentId(commentId);
            final String articleId = articleCommentRelation.getString(
                    Article.ARTICLE + "_" + Keys.OBJECT_ID);
            if (!articleRepository.isPublished(articleId)) {
                comments.remove(comment);
            }
        }
    }
}
