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

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.SortDirection;
import org.b3log.latke.repository.gae.AbstractGAERepository;
import org.b3log.solo.model.ArchiveDate;
import org.b3log.solo.repository.ArchiveDateRepository;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Archive date Google App Engine repository.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.5, Jun 30, 2011
 */
public final class ArchiveDateGAERepository extends AbstractGAERepository
        implements ArchiveDateRepository {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(ArchiveDateGAERepository.class.getName());

    @Override
    public String getName() {
        return ArchiveDate.ARCHIVE_DATE;
    }

    @Override
    public JSONObject getByArchiveDate(final String archiveDate)
            throws RepositoryException {
        try {
            final Query query = new Query(getName());
            query.addFilter(ArchiveDate.ARCHIVE_TIME,
                            Query.FilterOperator.EQUAL,
                            ArchiveDate.DATE_FORMAT.parse(archiveDate).getTime());
            final PreparedQuery preparedQuery = getDatastoreService().prepare(
                    query);
            final Entity entity = preparedQuery.asSingleEntity();

            if (null == entity) {
                return null;
            }

            return entity2JSONObject(entity);
        } catch (final ParseException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new RepositoryException(e);
        }
    }

    @Override
    public List<JSONObject> getArchiveDates() throws RepositoryException {
        final String cacheKey = "[archiveDates]";
        @SuppressWarnings("unchecked")
        List<JSONObject> ret = (List<JSONObject>) CACHE.get(cacheKey);
        if (null != ret) {
            LOGGER.log(Level.FINEST, "Got the archive dates from cache");
        } else {
            ret = new ArrayList<JSONObject>();
            final org.b3log.latke.repository.Query
                    query = new org.b3log.latke.repository.Query().
                    addSort(ArchiveDate.ARCHIVE_TIME, SortDirection.DESCENDING);
            final JSONObject result = get(query);

            try {
                final JSONArray archiveDates = result.getJSONArray(Keys.RESULTS);

                for (int i = 0; i < archiveDates.length(); i++) {
                    final JSONObject archiveDate = archiveDates.getJSONObject(i);
                    ret.add(archiveDate);
                }
            } catch (final JSONException e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
                throw new RepositoryException(e);
            }

            try {
                removeForUnpublishedArticles(ret);
            } catch (final JSONException e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }

            CACHE.put(cacheKey, ret);

            LOGGER.log(Level.FINEST,
                       "Got the archive dates, then put it into cache");
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
     * Gets the {@link ArchiveDateGAERepository} singleton.
     *
     * @return the singleton
     */
    public static ArchiveDateGAERepository getInstance() {
        return SingletonHolder.SINGLETON;
    }

    /**
     * Private default constructor.
     */
    private ArchiveDateGAERepository() {
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
        private static final ArchiveDateGAERepository SINGLETON =
                new ArchiveDateGAERepository();

        /**
         * Private default constructor.
         */
        private SingletonHolder() {
        }
    }
}