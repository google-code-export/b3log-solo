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
package org.b3log.solo.repository.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.repository.AbstractRepository;
import org.b3log.latke.repository.FilterOperator;
import org.b3log.latke.repository.Query;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.SortDirection;
import org.b3log.latke.util.CollectionUtils;
import org.b3log.solo.model.ArchiveDate;
import org.b3log.solo.repository.ArchiveDateRepository;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Archive date repository.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.5, Jun 30, 2011
 * @since 0.3.1
 */
public final class ArchiveDateRepositoryImpl extends AbstractRepository
        implements ArchiveDateRepository {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(ArchiveDateRepositoryImpl.class.getName());

    @Override
    public JSONObject getByArchiveDate(final String archiveDate)
            throws RepositoryException {
        try {
            final Query query = new Query();
            query.addFilter(ArchiveDate.ARCHIVE_TIME,
                            FilterOperator.EQUAL,
                            ArchiveDate.DATE_FORMAT.parse(archiveDate).getTime()).
                    setPageCount(1);

            final JSONObject result = get(query);
            final JSONArray array = result.getJSONArray(Keys.RESULTS);

            if (0 == array.length()) {
                return null;
            }

            return array.getJSONObject(0);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new RepositoryException(e);
        }
    }

    @Override
    public List<JSONObject> getArchiveDates() throws RepositoryException {
        final org.b3log.latke.repository.Query query =
                new Query().addSort(
                ArchiveDate.ARCHIVE_TIME, SortDirection.DESCENDING).
                setPageCount(1);
        final JSONObject result = get(query);

        List<JSONObject> ret = new ArrayList<JSONObject>();
        try {
            final JSONArray archiveDates = result.getJSONArray(Keys.RESULTS);

            ret = CollectionUtils.jsonArrayToList(archiveDates);
        } catch (final JSONException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new RepositoryException(e);
        }

        try {
            removeForUnpublishedArticles(ret);
        } catch (final JSONException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

        return ret;
    }

    /**
     * Removes archive dates of unpublished articles from the specified archive
     * dates.
     *
     * @param archiveDates the specified archive dates
     * @throws JSONException json exception
     * @throws RepositoryException repository exception
     */
    private void removeForUnpublishedArticles(
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

    /**
     * Gets the {@link ArchiveDateRepositoryImpl} singleton.
     *
     * @return the singleton
     */
    public static ArchiveDateRepositoryImpl getInstance() {
        return SingletonHolder.SINGLETON;
    }

    /**
     * Private constructor.
     * 
     * @param name the specified name
     */
    private ArchiveDateRepositoryImpl(final String name) {
        super(name);
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
        private static final ArchiveDateRepositoryImpl SINGLETON =
                new ArchiveDateRepositoryImpl(ArchiveDate.ARCHIVE_DATE);

        /**
         * Private default constructor.
         */
        private SingletonHolder() {
        }
    }
}