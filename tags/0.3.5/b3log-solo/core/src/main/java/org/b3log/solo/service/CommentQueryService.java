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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.solo.SoloServletListener;
import org.b3log.solo.model.Comment;
import org.b3log.solo.model.Common;
import org.b3log.solo.repository.CommentRepository;
import org.b3log.solo.repository.impl.CommentRepositoryImpl;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Comment query service.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Oct 18, 2011
 * @since 0.3.5
 */
public final class CommentQueryService {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(CommentQueryService.class.getName());
    /**
     * Comment repository.
     */
    private CommentRepository commentRepository =
            CommentRepositoryImpl.getInstance();

    /**
     * Gets the {@link CommentQueryService} singleton.
     *
     * @return the singleton
     */
    public static CommentQueryService getInstance() {
        return SingletonHolder.SINGLETON;
    }

    /**
     * Gets comments of an article specified by the article id.
     *
     * @param articleId the specified article id
     * @return a list of comments, returns an empty list if not found
     * @throws RepositoryException repository exception
     * @throws JSONException json exception
     */
    public List<JSONObject> getComments(final String articleId)
            throws JSONException, RepositoryException {
        final List<JSONObject> ret = new ArrayList<JSONObject>();

        final List<JSONObject> comments =
                commentRepository.getComments(articleId, 1, Integer.MAX_VALUE);
        for (final JSONObject comment : comments) {
            final String content = comment.getString(Comment.COMMENT_CONTENT).
                    replaceAll(SoloServletListener.ENTER_ESC, "<br/>");
            comment.put(Comment.COMMENT_CONTENT, content);
            comment.remove(Comment.COMMENT_EMAIL); // Removes email

            comment.put(Common.IS_REPLY, false); // Assumes this comment is not a reply

            if (comment.has(Comment.COMMENT_ORIGINAL_COMMENT_ID)) {
                // This comment is a reply
                comment.put(Common.IS_REPLY, true);
            }

            ret.add(comment);
        }

        return ret;
    }

    /**
     * Private constructor.
     */
    private CommentQueryService() {
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
        private static final CommentQueryService SINGLETON =
                new CommentQueryService();

        /**
         * Private default constructor.
         */
        private SingletonHolder() {
        }
    }
}
