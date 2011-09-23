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
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.solo.model.Tag;
import org.b3log.solo.repository.TagRepository;
import org.b3log.latke.Keys;
import org.b3log.latke.repository.FilterOperator;
import org.b3log.latke.repository.Query;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.SortDirection;
import org.b3log.latke.repository.gae.AbstractGAERepository;
import org.b3log.latke.util.CollectionUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Tag Google App Engine repository.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.9, Mar 7, 2011
 */
public final class TagRepositoryImpl extends AbstractGAERepository
        implements TagRepository {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(TagRepositoryImpl.class.getName());
    /**
     * Tag-Article relation repository.
     */
    private TagArticleRepositoryImpl tagArticleRepository =
            TagArticleRepositoryImpl.getInstance();

    @Override
    public String getName() {
        return Tag.TAG;
    }

    @Override
    public JSONObject getByTitle(final String tagTitle)
            throws RepositoryException {
        final Query query = new Query();
        query.addFilter(Tag.TAG_TITLE, FilterOperator.EQUAL, tagTitle);
        try {
            final JSONObject result = get(query);
            final JSONArray array = result.getJSONArray(Keys.RESULTS);

            if (0 == array.length()) {
                return null;
            }

            return array.getJSONObject(0);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);

            return null;
        }
    }

    @Override
    public List<JSONObject> getMostUsedTags(final int num) {
        final Query query = new Query();
        query.addSort(Tag.TAG_PUBLISHED_REFERENCE_COUNT,
                      SortDirection.DESCENDING);
        query.setCurrentPageNum(1);
        query.setPageSize(num);

        try {
            final JSONObject result = get(query);
            final JSONArray array = result.getJSONArray(Keys.RESULTS);
            
            return CollectionUtils.jsonArrayToList(array);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return Collections.emptyList();
        }
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

    /**
     * Gets the {@link TagGAERepository} singleton.
     *
     * @return the singleton
     */
    public static TagRepositoryImpl getInstance() {
        return SingletonHolder.SINGLETON;
    }

    /**
     * Private default constructor.
     */
    private TagRepositoryImpl() {
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
        private static final TagRepositoryImpl SINGLETON =
                new TagRepositoryImpl();

        /**
         * Private default constructor.
         */
        private SingletonHolder() {
        }
    }
}
