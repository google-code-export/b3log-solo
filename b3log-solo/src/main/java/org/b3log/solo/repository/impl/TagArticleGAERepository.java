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

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.Tag;
import org.b3log.solo.repository.TagArticleRepository;
import org.b3log.latke.Keys;
import org.b3log.latke.repository.FilterOperator;
import org.b3log.latke.repository.Query;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.SortDirection;
import org.b3log.latke.repository.gae.AbstractGAERepository;
import org.b3log.latke.util.CollectionUtils;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Tag-Article relation Google App Engine repository.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.8, Jan 12, 2011
 */
public final class TagArticleGAERepository extends AbstractGAERepository
        implements TagArticleRepository {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(TagArticleGAERepository.class.getName());

    @Override
    public String getName() {
        return Tag.TAG + "_" + Article.ARTICLE;
    }

    @Override
    public List<JSONObject> getByArticleId(final String articleId)
            throws RepositoryException {
        final Query query = new Query();
        query.addFilter(Article.ARTICLE + "_" + Keys.OBJECT_ID,
                        FilterOperator.EQUAL, articleId);

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
    public JSONObject getByTagId(final String tagId,
                                 final int currentPageNum,
                                 final int pageSize)
            throws RepositoryException {
        final Query query = new Query();
        query.addFilter(Tag.TAG + "_" + Keys.OBJECT_ID,
                        FilterOperator.EQUAL, tagId);
        query.addSort(Article.ARTICLE + "_" + Keys.OBJECT_ID,
                      SortDirection.DESCENDING);
        query.setCurrentPageNum(currentPageNum);
        query.setPageSize(pageSize);

        return get(query);
    }

    /**
     * Gets the {@link TagArticleGAERepository} singleton.
     *
     * @return the singleton
     */
    public static TagArticleGAERepository getInstance() {
        return SingletonHolder.SINGLETON;
    }

    /**
     * Private default constructor.
     */
    private TagArticleGAERepository() {
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
        private static final TagArticleGAERepository SINGLETON =
                new TagArticleGAERepository();

        /**
         * Private default constructor.
         */
        private SingletonHolder() {
        }
    }
}