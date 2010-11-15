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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.b3log.solo.model.Comment;
import org.b3log.latke.Keys;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.gae.AbstractGAERepository;
import org.b3log.solo.model.Page;
import org.b3log.solo.repository.PageCommentRepository;
import org.json.JSONObject;

/**
 * Page-Comment relation Google App Engine repository.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Oct 29, 2010
 */
public final class PageCommentGAERepository extends AbstractGAERepository
        implements PageCommentRepository {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(PageCommentGAERepository.class.getName());

    @Override
    public String getName() {
        return Page.PAGE + "_" + Comment.COMMENT;
    }

    @Override
    public List<JSONObject> getByPageId(final String pageId)
            throws RepositoryException {
        final Query query = new Query(getName());
        query.addFilter(Page.PAGE + "_" + Keys.OBJECT_ID,
                        Query.FilterOperator.EQUAL, pageId);
        query.addSort(Keys.OBJECT_ID, Query.SortDirection.DESCENDING);
        final PreparedQuery preparedQuery = DATASTORE_SERVICE.prepare(query);

        final List<JSONObject> ret = new ArrayList<JSONObject>();
        for (final Entity entity : preparedQuery.asIterable()) {
            final Map<String, Object> properties = entity.getProperties();
            final JSONObject e = new JSONObject(properties);

            ret.add(e);
        }

        return ret;
    }

    @Override
    public JSONObject getByCommentId(final String commentId)
            throws RepositoryException {
        final Query query = new Query(getName());
        query.addFilter(Comment.COMMENT + "_" + Keys.OBJECT_ID,
                        Query.FilterOperator.EQUAL, commentId);
        final PreparedQuery preparedQuery = DATASTORE_SERVICE.prepare(query);

        final Entity entity = preparedQuery.asSingleEntity();
        if (null == entity) {
            return null;
        }

        final Map<String, Object> properties = entity.getProperties();

        return new JSONObject(properties);
    }
}
