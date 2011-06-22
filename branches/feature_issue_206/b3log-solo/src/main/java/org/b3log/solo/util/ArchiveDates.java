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

import java.text.ParseException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.util.CollectionUtils;
import org.b3log.solo.model.ArchiveDate;
import org.b3log.solo.model.Article;
import org.b3log.solo.repository.ArchiveDateArticleRepository;
import org.b3log.solo.repository.ArchiveDateRepository;
import org.b3log.solo.repository.ArticleRepository;
import org.b3log.solo.repository.impl.ArchiveDateArticleGAERepository;
import org.b3log.solo.repository.impl.ArchiveDateGAERepository;
import org.b3log.solo.repository.impl.ArticleGAERepository;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Archive date utilities.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.9, Jan 12, 2011
 */
public final class ArchiveDates {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(ArchiveDates.class.getName());
    /**
     * Archive date repository.
     */
    private ArchiveDateRepository archiveDateRepository =
            ArchiveDateGAERepository.getInstance();
    /**
     * Archive date-Article repository.
     */
    private ArchiveDateArticleRepository archiveDateArticleRepository =
            ArchiveDateArticleGAERepository.getInstance();
    /**
     * Article repository.
     */
    private ArticleRepository articleRepository =
            ArticleGAERepository.getInstance();

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
     * Decrements reference count of archive date of an published article specified
     * by the given article id.
     *
     * @param articleId the given article id
     * @throws JSONException json exception
     * @throws RepositoryException repository exception
     */
    public void decArchiveDatePublishedRefCount(final String articleId)
            throws JSONException, RepositoryException {
        final JSONObject archiveDateArticleRelation =
                archiveDateArticleRepository.getByArticleId(articleId);
        final String archiveDateId =
                archiveDateArticleRelation.getString(ArchiveDate.ARCHIVE_DATE
                                                     + "_" + Keys.OBJECT_ID);
        final JSONObject archiveDate = archiveDateRepository.get(archiveDateId);
        archiveDate.put(ArchiveDate.ARCHIVE_DATE_PUBLISHED_ARTICLE_COUNT,
                        archiveDate.getInt(
                ArchiveDate.ARCHIVE_DATE_PUBLISHED_ARTICLE_COUNT) - 1);
        archiveDateRepository.update(archiveDateId, archiveDate);
    }

    /**
     * Increments reference count of archive date of an published article specified
     * by the given article id.
     *
     * @param articleId the given article id
     * @throws JSONException json exception
     * @throws RepositoryException repository exception
     */
    public void incArchiveDatePublishedRefCount(final String articleId)
            throws JSONException, RepositoryException {
        final JSONObject archiveDateArticleRelation =
                archiveDateArticleRepository.getByArticleId(articleId);
        final String archiveDateId =
                archiveDateArticleRelation.getString(ArchiveDate.ARCHIVE_DATE
                                                     + "_" + Keys.OBJECT_ID);
        final JSONObject archiveDate = archiveDateRepository.get(archiveDateId);
        archiveDate.put(ArchiveDate.ARCHIVE_DATE_PUBLISHED_ARTICLE_COUNT,
                        archiveDate.getInt(
                ArchiveDate.ARCHIVE_DATE_PUBLISHED_ARTICLE_COUNT) + 1);
        archiveDateRepository.update(archiveDateId, archiveDate);
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
        final JSONObject article = articleRepository.get(articleId);
        if (article.getBoolean(Article.ARTICLE_IS_PUBLISHED)) {
            --archiveDatePublishedArticleCnt;
        }

        if (0 == archiveDateArticleCnt) {
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
     * Gets the {@link ArchiveDates} singleton.
     *
     * @return the singleton
     */
    public static ArchiveDates getInstance() {
        return SingletonHolder.SINGLETON;
    }

    /**
     * Private default constructor.
     */
    private ArchiveDates() {
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
        private static final ArchiveDates SINGLETON = new ArchiveDates();

        /**
         * Private default constructor.
         */
        private SingletonHolder() {
        }
    }
}
