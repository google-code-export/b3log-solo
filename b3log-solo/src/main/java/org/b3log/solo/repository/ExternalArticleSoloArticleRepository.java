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
 * External blogging system article(for sync)-Solo article repository.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.2, Sep 4, 2010
 */
public interface ExternalArticleSoloArticleRepository extends Repository {

    /**
     * Gets Solo article id by the specified external blog article id.
     *
     * @param externalArticleId the specified external blog article id
     * @return Solo article id, returns {@code null} if not found
     * @throws RepositoryException repository exception
     */
    String getSoloArticleId(final String externalArticleId)
            throws RepositoryException;

    /**
     * Gets external blogging system blog article id by the specified Solo
     * article id.
     *
     * @param soloArticleId the specified Solo article id
     * @return external blog article id, returns {@code null} if not found
     * @throws RepositoryException repository exception
     */
    String getExternalArticleId(final String soloArticleId)
            throws RepositoryException;

    /**
     * Gets a external blog article-Solo article relation by the specified
     * external article id.
     *
     * @param externalArticleId the specified external article id
     * @return the relation, {@code null} if not found
     * @throws RepositoryException repository exception
     */
    JSONObject getByExternalArticleId(final String externalArticleId)
            throws RepositoryException;

    /**
     * Gets an external blog article-Solo article relation by the specified Solo
     * article id.
     *
     * @param soloArticleId the specified Solo article id
     * @return the relation, {@code null} if not found
     * @throws RepositoryException repository exception
     */
    JSONObject getBySoloArticleId(final String soloArticleId)
            throws RepositoryException;
}
