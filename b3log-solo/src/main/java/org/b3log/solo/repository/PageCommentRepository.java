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
package org.b3log.solo.repository;

import java.util.List;
import org.b3log.latke.repository.Repository;
import org.b3log.latke.repository.RepositoryException;
import org.json.JSONObject;

/**
 * Page-Comment repository.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Oct 29, 2010
 */
public interface PageCommentRepository extends Repository {

    /**
     * Gets page-comment relations by the specified page id.
     *
     * @param pageId the specified page id
     * @return for example
     * <pre>
     * [{
     *         "oId": "",
     *         "comment_oId": "",
     *         "page_oId": pageId
     * }, ....], returns an empty list if not found
     * </pre>
     * @throws RepositoryException repository exception
     */
    List<JSONObject> getByPageId(final String pageId)
            throws RepositoryException;

    /**
     * Gets an page-comment relation by the specified comment id.
     *
     * @param commentId the specified comment id
     * @return for example
     * <pre>
     * [{
     *         "oId": "",
     *         "comment_oId": commentId,,
     *         "page_oId": ""
     * }, ....], returns {@code null} if not found
     * </pre>
     * @throws RepositoryException repository exception
     */
    JSONObject getByCommentId(final String commentId) throws RepositoryException;
}
