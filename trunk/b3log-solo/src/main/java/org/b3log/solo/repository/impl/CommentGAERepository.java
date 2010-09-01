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
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.QueryResultIterable;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.b3log.latke.Keys;
import org.b3log.solo.model.Comment;
import org.b3log.solo.repository.CommentRepository;
import org.b3log.latke.repository.gae.AbstractGAERepository;
import org.json.JSONObject;

/**
 * Comment Google App Engine repository.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.2, Sep 1, 2010
 */
public class CommentGAERepository extends AbstractGAERepository
        implements CommentRepository {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(CommentGAERepository.class);

    @Override
    public String getName() {
        return Comment.COMMENT;
    }

    @Override
    public List<JSONObject> getRecentComments(final int num) {
        final Query query = new Query(getName());
        query.addSort(Keys.OBJECT_ID,
                      Query.SortDirection.DESCENDING);
        final PreparedQuery preparedQuery = DATASTORE_SERVICE.prepare(query);
        final QueryResultIterable<Entity> queryResultIterable =
                preparedQuery.asQueryResultIterable(FetchOptions.Builder.
                withLimit(num));

        final List<JSONObject> ret = new ArrayList<JSONObject>();
        for (final Entity entity : queryResultIterable) {
            final JSONObject comment = entity2JSONObject(entity);
            ret.add(comment);
        }

        return ret;
    }
}
