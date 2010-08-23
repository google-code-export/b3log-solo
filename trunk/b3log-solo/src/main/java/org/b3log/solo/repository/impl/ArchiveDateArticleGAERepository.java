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
package org.b3log.solo.repository.impl;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import static com.google.appengine.api.datastore.FetchOptions.Builder.*;
import com.google.appengine.api.datastore.QueryResultList;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.b3log.latke.Keys;
import org.b3log.solo.model.Article;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.gae.AbstractGAERepository;
import org.b3log.solo.model.ArchiveDate;
import org.b3log.solo.repository.ArchiveDateArticleRepository;
import org.json.JSONObject;

/**
 * Archive date-Article relation Google App Engine repository.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Aug 23, 2010
 */
public class ArchiveDateArticleGAERepository extends AbstractGAERepository
        implements ArchiveDateArticleRepository {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(ArchiveDateArticleGAERepository.class);

    @Override
    public String getName() {
        return ArchiveDate.ARCHIVE_DATE + "_" + Article.ARTICLE;
    }

    @Override
    public List<JSONObject> getByArchiveDateId(final String archiveDateId,
                                               final int currentPageNum,
                                               final int pageSize)
            throws RepositoryException {
        final Query query = new Query(getName());
        query.addFilter(ArchiveDate.ARCHIVE_DATE + "_" + Keys.OBJECT_ID,
                        Query.FilterOperator.EQUAL, archiveDateId);

        final PreparedQuery preparedQuery = getDatastoreService().prepare(query);
        final int offset = pageSize * (currentPageNum - 1);
        final QueryResultList<Entity> queryResultList =
                preparedQuery.asQueryResultList(
                withOffset(offset).limit(pageSize));

        final List<JSONObject> ret = new ArrayList<JSONObject>();
        for (final Entity entity : queryResultList) {
            final Map<String, Object> properties = entity.getProperties();
            final JSONObject e = new JSONObject(properties);

            ret.add(e);
        }

        return ret;
    }

    @Override
    public JSONObject getByArticleId(final String articleId)
            throws RepositoryException {
        final Query query = new Query(getName());
        query.addFilter(Article.ARTICLE + "_" + Keys.OBJECT_ID,
                        Query.FilterOperator.EQUAL, articleId);
        final PreparedQuery preparedQuery = getDatastoreService().prepare(query);

        final Entity entity = preparedQuery.asSingleEntity();
        if (null == entity) {
            return null;
        }

        final Map<String, Object> properties = entity.getProperties();

        return new JSONObject(properties);
    }
}
