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

import java.util.logging.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.solo.model.ArchiveDate;
import org.b3log.solo.repository.ArchiveDateArticleRepository;
import org.b3log.solo.repository.ArchiveDateRepository;
import org.b3log.solo.repository.impl.ArchiveDateArticleRepositoryImpl;
import org.b3log.solo.repository.impl.ArchiveDateRepositoryImpl;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Archive date utilities.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.1.0, Oct 18, 2011
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
            ArchiveDateRepositoryImpl.getInstance();
    /**
     * Archive date-Article repository.
     */
    private ArchiveDateArticleRepository archiveDateArticleRepository =
            ArchiveDateArticleRepositoryImpl.getInstance();

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
