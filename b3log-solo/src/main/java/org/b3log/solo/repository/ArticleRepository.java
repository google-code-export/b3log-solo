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

package org.b3log.solo.repository;

import com.google.appengine.api.datastore.Entity;
import java.util.List;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.gae.GAERepository;
import org.json.JSONObject;

/**
 * Article repository.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.1.4, Jan 30, 2011
 */
public interface ArticleRepository extends GAERepository {

    /**
     * Gets articles by the specified author email, current page number and
     * page size.
     *
     * @param authorEmail the specified author email
     * @param currentPageNum the specified current page number, MUST greater
     * then {@code 0}
     * @param pageSize the specified page size(count of a page contains objects),
     * MUST greater then {@code 0}
     * @return for example
     * <pre>
     * {
     *     "pagination": {
     *       "paginationPageCount": 88250
     *     },
     *     "rslts": [{
     *         // article keys....
     *     }, ....]
     * }
     * </pre>
     * @throws RepositoryException repository exception
     */
    JSONObject getByAuthorEmail(final String authorEmail,
                                final int currentPageNum,
                                final int pageSize) throws RepositoryException;

    /**
     * Gets an article by the specified permalink.
     *
     * @param permalink the specified permalink
     * @return an article, returns {@code null} if not found
     */
    JSONObject getByPermalink(final String permalink);

    /**
     * Gets post articles recently with the specified fetch size.
     *
     * @param fetchSize the specified fetch size
     * @return a list of articles recently, returns an empty list if not found
     */
    List<JSONObject> getRecentArticles(final int fetchSize);

    /**
     * Gets most commented and published articles with the specified number.
     *
     * @param num the specified number
     * @return a list of most comment articles, returns an empty list if not
     * found
     */
    List<JSONObject> getMostCommentArticles(final int num);

    /**
     * Gets most view count and published articles with the specified number.
     *
     * @param num the specified number
     * @return a list of most view count articles, returns an empty list if not
     * found
     */
    List<JSONObject> getMostViewCountArticles(final int num);

    /**
     * Gets the id of the previous article(by create date, oId) by the
     * specified article id.
     *
     * @param articleId the specified article id
     * @return an article id, {@code null} if not found
     */
    String getPreviousArticleId(final String articleId);

    /**
     * Gets the id of the next article(by create date, oId) by the specified
     * article id.
     *
     * @param articleId the specified article id
     * @return an article id, {@code null} if not found
     */
    String getNextArticleId(final String articleId);

    /**
     * Gets the previous article(by create date, oId) by the specified article
     * id.
     *
     * @param articleId the specified article id
     * @return the previous article,
     * <pre>
     * {
     *     "articleTitle": "",
     *     "articlePermalink": ""
     * }
     * </pre>
     * returns {@code null} if not found
     */
    JSONObject getPreviousArticle(final String articleId);

    /**
     * Gets the previous article(by create date, oId) by the specified article
     * id asynchronously.
     *
     * @param articleId the specified article id
     * @return the previous article,
     * <pre>
     * {
     *     "articleTitle": "",
     *     "articlePermalink": ""
     * }
     * </pre>
     * returns {@code null} if not found
     */
    Iterable<Entity> getPreviousArticleAsync(final String articleId);

    /**
     * Gets the next article(by create date, oId) by the specified article id.
     *
     * @param articleId the specified article id
     * @return the next article,
     * <pre>
     * {
     *     "articleTitle": "",
     *     "articlePermalink": ""
     * }
     * </pre>
     * returns {@code null} if not found
     */
    JSONObject getNextArticle(final String articleId);

    /**
     * Gets the next article(by create date, oId) by the specified article id
     * asynchronously.
     *
     * @param articleId the specified article id
     * @return the next article,
     * <pre>
     * {
     *     "articleTitle": "",
     *     "articlePermalink": ""
     * }
     * </pre>
     * returns {@code null} if not found
     */
    Iterable<Entity> getNextArticleAsync(final String articleId);

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
     *     "articleUpdateDate": java.util.Date,
     *     "articlePermalink": ""
     * }
     * </pre>
     * @throws RepositoryException repository exception
     */
    void importArticle(final JSONObject article)
            throws RepositoryException;

    /**
     * Determines an article specified by the given article id is published.
     * 
     * @param articleId the given article id
     * @return {@code true} if it is published, {@code false} otherwise
     * @throws RepositoryException repository exception
     */
    boolean isPublished(final String articleId) throws RepositoryException;
}
