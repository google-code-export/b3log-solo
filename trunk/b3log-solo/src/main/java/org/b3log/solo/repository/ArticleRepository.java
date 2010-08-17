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
 * Article repository.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.6, Aug 18, 2010
 */
public interface ArticleRepository extends Repository {

    /**
     * Gets archived articles with the specified archive date.
     *
     * @param archiveDate the specified archive date(yyyy/MM)
     * @return a list of articles, returns an empty list if not found
     */
    List<JSONObject> getArchivedArticles(final String archiveDate);

    /**
     * Gets archive dates.
     *
     * @return a list of archive dates(["yyyy/MM", "yyyy/MM", ....]), returns
     * an empty list if not found
     */
    List<String> getArchiveDates();

    /**
     * Gets post articles recently with the specified number.
     *
     * @param num the specified number
     * @return a list of articles recently, returns an empty list if not found
     */
    List<JSONObject> getRecentArticles(final int num);

    /**
     * Gets most comment articles with the specified number.
     *
     * @param num the specified number
     * @return a list of most comment articles, returns an empty list if not
     * found
     */
    List<JSONObject> getMostCommentArticles(final int num);

    /**
     * Gets the id of the previous article((by create date, oId) by the
     * specified article id.
     *
     * @param articleId the specified article id
     * @return an article id, {@code null} if not found
     */
    String getPrevisouArticleId(final String articleId);

    /**
     * Gets the id of the next article((by create date, oId) by the specified
     * article id.
     *
     * @param articleId the specified article id
     * @return an article id, {@code null} if not found
     */
    String getNextArticleId(final String articleId);

    /**
     * Imports the specified article.
     *
     * <p>
     *   <b>Note</b>: This interface is designed to import article for external
     *   blogging system. Do NOT use this interface for adding article.
     * </p>
     *
     * @param article the specified article, for example,
     * <pre>
     * {
     *     "oId": "",
     *     "articleTitle": "",
     *     "articleAbstrace": "",
     *     "articleTags": ["", "", ....],
     *     "articleContent": ""
     *     "articleCreateDate": java.util.Date,
     *     "articleUpdateDate": java.util.Date
     * }
     * </pre>
     * @throws RepositoryException repository exception
     */
    void importArticle(final JSONObject article)
            throws RepositoryException;
}
