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

import org.b3log.latke.repository.Repository;
import org.b3log.latke.repository.RepositoryException;
import org.json.JSONObject;

/**
 * CSDN blog article(for sync)-Solo article repository.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Aug 27, 2010
 */
public interface CSDNBlogArticleSoloArticleRepository extends Repository {

    /**
     * Gets Solo article id by the specified CSDN blog article id.
     *
     * @param csdnBlogArticleId the specified CSDN blog article id
     * @return Solo article id, returns {@code null} if not found
     * @throws RepositoryException repository exception
     */
    String getSoloArticleId(final String csdnBlogArticleId)
            throws RepositoryException;

    /**
     * Gets CSDN blog article id by the specified Solo article id.
     *
     * @param soloArticleId the specified Solo article id
     * @return CSDN blog article id, returns {@code null} if not found
     * @throws RepositoryException repository exception
     */
    String getCSDNBlogArticleId(final String soloArticleId)
            throws RepositoryException;

    /**
     * Gets a CSDN blog article-Solo article relation by the specified CSDN
     * blog article id.
     *
     * @param csdnBlogArticleId the specified CSDN blog article id
     * @return the relation, {@code null} if not found
     * @throws RepositoryException repository exception
     */
    JSONObject getByCSDNBlogArticleId(final String csdnBlogArticleId)
            throws RepositoryException;

    /**
     * Gets a CSDN blog article-Solo article relation by the specified Solo
     * article id.
     *
     * @param soloArticleId the specified Solo article id
     * @return the relation, {@code null} if not found
     * @throws RepositoryException repository exception
     */
    JSONObject getBySoloArticleId(final String soloArticleId)
            throws RepositoryException;
}
