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
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.QueryResultIterable;
import com.google.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.solo.model.Tag;
import org.b3log.solo.repository.TagRepository;
import org.b3log.latke.Keys;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.gae.AbstractGAERepository;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Tag Google App Engine repository.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.5, Dec 3, 2010
 */
public final class TagGAERepository extends AbstractGAERepository
        implements TagRepository {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(TagGAERepository.class.getName());
    /**
     * Tag-Article relation repository.
     */
    @Inject
    private TagArticleGAERepository tagArticleRepository;

    @Override
    public String getName() {
        return Tag.TAG;
    }

    @Override
    public JSONObject getByTitle(final String tagTitle)
            throws RepositoryException {
        final Query query = new Query(Tag.TAG);
        query.addFilter(Tag.TAG_TITLE, Query.FilterOperator.EQUAL, tagTitle);
        final PreparedQuery preparedQuery = getDatastoreService().prepare(query);
        final Entity entity = preparedQuery.asSingleEntity();
        if (null == entity) {
            return null;
        }

        final Map<String, Object> properties = entity.getProperties();

        return new JSONObject(properties);
    }

    @Override
    public List<JSONObject> getMostUsedTags(final int num) {
        final Query query = new Query(getName());
        query.addSort(Tag.TAG_PUBLISHED_REFERENCE_COUNT,
                      Query.SortDirection.DESCENDING);
        final PreparedQuery preparedQuery = getDatastoreService().prepare(query);
        final QueryResultIterable<Entity> queryResultIterable =
                preparedQuery.asQueryResultIterable(FetchOptions.Builder.
                withLimit(num));

        final List<JSONObject> ret = new ArrayList<JSONObject>();
        for (final Entity entity : queryResultIterable) {
            final JSONObject tag = entity2JSONObject(entity);
            ret.add(tag);
        }

        return ret;
    }

    @Override
    public List<JSONObject> getByArticleId(final String articleId)
            throws RepositoryException {
        final List<JSONObject> ret = new ArrayList<JSONObject>();

        try {
            final List<JSONObject> tagArticleRelations =
                    tagArticleRepository.getByArticleId(articleId);
            for (final JSONObject tagArticleRelation : tagArticleRelations) {
                final String tagId =
                        tagArticleRelation.getString(Tag.TAG + "_"
                                                     + Keys.OBJECT_ID);
                final JSONObject tag = get(tagId);

                ret.add(tag);
            }
        } catch (final JSONException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new RepositoryException(e);
        }

        return ret;
    }
}
