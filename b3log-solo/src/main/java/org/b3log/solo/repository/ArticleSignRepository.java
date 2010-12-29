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

package org.b3log.solo.repository;

import java.util.List;
import org.b3log.latke.repository.Repository;
import org.b3log.latke.repository.RepositoryException;
import org.json.JSONObject;

/**
 * Article-Sign repository.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Dec 29, 2010
 */
public interface ArticleSignRepository extends Repository {

    /**
     * Gets article-sign relations by the specified sign id.
     *
     * @param signId the specified sign id
     * @return for example
     * <pre>
     * [{
     *         "oId": "",
     *         "sign_oId": signId,
     *         "article_oId": ""
     * }, ....], returns an empty list if not found
     * </pre>
     * @throws RepositoryException repository exception
     */
    List<JSONObject> getBySignId(final String signId)
            throws RepositoryException;

    /**
     * Gets an article-sign relation by the specified article id.
     *
     * @param articleId the specified article id
     * @return for example
     * <pre>
     * [{
     *         "oId": "",
     *         "article_oId": articleId,
     *         "sign_oId": ""
     * }, ....], returns {@code null} if not found
     * </pre>
     * @throws RepositoryException repository exception
     */
    JSONObject getByArticleId(final String articleId) throws RepositoryException;
}
