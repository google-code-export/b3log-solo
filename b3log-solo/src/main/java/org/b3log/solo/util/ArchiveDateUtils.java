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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.SortDirection;
import org.b3log.latke.util.CollectionUtils;
import org.b3log.solo.model.ArchiveDate;
import org.b3log.solo.model.Article;
import org.b3log.solo.repository.ArchiveDateArticleRepository;
import org.b3log.solo.repository.ArchiveDateRepository;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Archive date utilities.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.5, Dec 4, 2010
 */
public final class ArchiveDateUtils {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(ArchiveDateUtils.class.getName());
    /**
     * Archive date repository.
     */
    @Inject
    private ArchiveDateRepository archiveDateRepository;
    /**
     * Archive date-Article repository.
     */
    @Inject
    private ArchiveDateArticleRepository archiveDateArticleRepository;

    /**
     * Archive the create date with the specified article.
     * 
     * @param article the specified article, for example,
     * <pre>
     * {
     *     ....,
     *     "oId": "",
     *     "articleCreateDate": java.util.Date,
     *     ....
     * }
     * </pre>
     * @throws JSONException json exception
     * @throws RepositoryException repository exception
     */
    public void archiveDate(final JSONObject article)
            throws JSONException, RepositoryException {
        final Date createDate = (Date) article.get(Article.ARTICLE_CREATE_DATE);
        final String createDateString =
                ArchiveDate.DATE_FORMAT.format(createDate);
        JSONObject archiveDate = archiveDateRepository.getByArchiveDate(
                createDateString);
        if (null == archiveDate) {
            archiveDate = new JSONObject();
            try {
                archiveDate.put(ArchiveDate.ARCHIVE_DATE,
                                ArchiveDate.DATE_FORMAT.parse(createDateString));
                archiveDate.put(ArchiveDate.ARCHIVE_DATE_ARTICLE_COUNT, 0);
                archiveDate.put(ArchiveDate.ARCHIVE_DATE_PUBLISHED_ARTICLE_COUNT,
                                0);

                archiveDateRepository.add(archiveDate);
            } catch (final ParseException e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
                throw new RepositoryException(e);
            }
        }

        final JSONObject newArchiveDate = new JSONObject(
                archiveDate,
                CollectionUtils.jsonArrayToArray(archiveDate.names(),
                                                 String[].class));
        newArchiveDate.put(ArchiveDate.ARCHIVE_DATE_ARTICLE_COUNT,
                           archiveDate.getInt(
                ArchiveDate.ARCHIVE_DATE_ARTICLE_COUNT) + 1);
        if (article.getBoolean(Article.ARTICLE_IS_PUBLISHED)) {
            newArchiveDate.put(ArchiveDate.ARCHIVE_DATE_PUBLISHED_ARTICLE_COUNT,
                               archiveDate.getInt(
                    ArchiveDate.ARCHIVE_DATE_PUBLISHED_ARTICLE_COUNT) + 1);
        } else {
            newArchiveDate.put(ArchiveDate.ARCHIVE_DATE_PUBLISHED_ARTICLE_COUNT,
                               archiveDate.getInt(
                    ArchiveDate.ARCHIVE_DATE_PUBLISHED_ARTICLE_COUNT));
        }
        archiveDateRepository.update(archiveDate.getString(Keys.OBJECT_ID),
                                     newArchiveDate);

        final JSONObject archiveDateArticleRelation = new JSONObject();
        archiveDateArticleRelation.put(ArchiveDate.ARCHIVE_DATE + "_"
                                       + Keys.OBJECT_ID, archiveDate.getString(
                Keys.OBJECT_ID));
        archiveDateArticleRelation.put(Article.ARTICLE + "_"
                                       + Keys.OBJECT_ID, article.getString(
                Keys.OBJECT_ID));

        archiveDateArticleRepository.add(archiveDateArticleRelation);
    }

    /**
     * Un-archive an article specified by the given specified article id.
     *
     * @param articleId the given article id
     * @throws JSONException json exception
     * @throws RepositoryException repository exception
     */
    public void unArchiveDate(final String articleId)
            throws JSONException, RepositoryException {
        final JSONObject archiveDateArticleRelation =
                archiveDateArticleRepository.getByArticleId(articleId);
        final String archiveDateId =
                archiveDateArticleRelation.getString(ArchiveDate.ARCHIVE_DATE
                                                     + "_" + Keys.OBJECT_ID);
        final JSONObject archiveDate = archiveDateRepository.get(archiveDateId);
        int archiveDateArticleCnt =
                archiveDate.getInt(ArchiveDate.ARCHIVE_DATE_ARTICLE_COUNT);
        --archiveDateArticleCnt;
        int archiveDatePublishedArticleCnt =
                archiveDate.getInt(
                ArchiveDate.ARCHIVE_DATE_PUBLISHED_ARTICLE_COUNT);
        --archiveDatePublishedArticleCnt;
        if (0 == archiveDatePublishedArticleCnt) {
            archiveDateRepository.remove(archiveDateId);
        } else {
            final JSONObject newArchiveDate = new JSONObject(
                    archiveDate,
                    CollectionUtils.jsonArrayToArray(archiveDate.names(),
                                                     String[].class));
            newArchiveDate.put(ArchiveDate.ARCHIVE_DATE_ARTICLE_COUNT,
                               archiveDateArticleCnt);
            newArchiveDate.put(ArchiveDate.ARCHIVE_DATE_PUBLISHED_ARTICLE_COUNT,
                               archiveDatePublishedArticleCnt);
            archiveDateRepository.update(archiveDateId, newArchiveDate);
        }

        archiveDateArticleRepository.remove(archiveDateArticleRelation.getString(
                Keys.OBJECT_ID));
    }

    /**
     * Gets archive dates.
     *
     * @return a list of archive date, returns an empty list if
     * not found
     * @throws JSONException json exception
     * @throws RepositoryException repository exception
     */
    public List<JSONObject> getArchiveDates()
            throws JSONException, RepositoryException {
        final List<JSONObject> ret = new ArrayList<JSONObject>();
        final Map<String, SortDirection> sorts =
                new HashMap<String, SortDirection>();
        sorts.put(ArchiveDate.ARCHIVE_DATE, SortDirection.DESCENDING);
        final JSONObject result = archiveDateRepository.get(
                1, Integer.MAX_VALUE, sorts);

        try {
            final JSONArray archiveDates = result.getJSONArray(Keys.RESULTS);

            for (int i = 0; i < archiveDates.length(); i++) {
                final JSONObject archiveDate = archiveDates.getJSONObject(i);
                ret.add(archiveDate);
            }

            return ret;
        } catch (final JSONException e) { // not found
            return ret;
        }
    }

    /**
     * Removes archive dates of unpublished articles from the specified archive
     * dates.
     *
     * @param archiveDates the specified archive dates
     * @throws JSONException json exception
     * @throws RepositoryException repository exception
     */
    public void removeForUnpublishedArticles(
            final List<JSONObject> archiveDates) throws JSONException,
                                                        RepositoryException {
        final Iterator<JSONObject> iterator = archiveDates.iterator();
        while (iterator.hasNext()) {
            final JSONObject archiveDate = iterator.next();
            if (0 == archiveDate.getInt(
                    ArchiveDate.ARCHIVE_DATE_PUBLISHED_ARTICLE_COUNT)) {
                iterator.remove();
            }
        }
    }
}
