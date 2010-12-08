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

package org.b3log.solo.repository.impl;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import java.text.ParseException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.gae.AbstractGAERepository;
import org.b3log.solo.model.ArchiveDate;
import org.b3log.solo.repository.ArchiveDateRepository;
import org.json.JSONObject;

/**
 * Archive date relation Google App Engine repository.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Dec 3, 2010
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
            query.addFilter(ArchiveDate.ARCHIVE_DATE,
                            Query.FilterOperator.EQUAL,
                            ArchiveDate.DATE_FORMAT.parse(archiveDate));
            final PreparedQuery preparedQuery = getDatastoreService().prepare(
                    query);
            final Entity entity = preparedQuery.asSingleEntity();

            if (null == entity) {
                return null;
            }

            final Map<String, Object> properties = entity.getProperties();

            return new JSONObject(properties);
        } catch (final ParseException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new RepositoryException(e);
        }
    }
}
