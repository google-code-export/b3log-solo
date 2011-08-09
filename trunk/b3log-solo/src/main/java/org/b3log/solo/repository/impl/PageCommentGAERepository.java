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

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.solo.model.Comment;
import org.b3log.latke.Keys;
import org.b3log.latke.repository.FilterOperator;
import org.b3log.latke.repository.Query;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.SortDirection;
import org.b3log.latke.repository.gae.AbstractGAERepository;
import org.b3log.latke.util.CollectionUtils;
import org.b3log.solo.model.Page;
import org.b3log.solo.repository.PageCommentRepository;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Page-Comment relation Google App Engine repository.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Jan 12, 2011
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
        final Query query = new Query();
        query.addFilter(Page.PAGE + "_" + Keys.OBJECT_ID,
                        FilterOperator.EQUAL, pageId);
        query.addSort(Keys.OBJECT_ID, SortDirection.DESCENDING);
        final JSONObject result = get(query);

        try {
            final JSONArray pageComments = result.getJSONArray(Keys.RESULTS);

            return CollectionUtils.jsonArrayToList(pageComments);
        } catch (final JSONException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new RepositoryException(e);
        }
    }

    @Override
    public JSONObject getByCommentId(final String commentId)
            throws RepositoryException {
        final Query query = new Query();
        query.addFilter(Comment.COMMENT + "_" + Keys.OBJECT_ID,
                        FilterOperator.EQUAL, commentId);
        
        final JSONObject result = get(query);
        final JSONArray array = result.optJSONArray(Keys.RESULTS);
        if (0 == array.length()) {
            return null;
        }

        return array.optJSONObject(0);
    }

    /**
     * Gets the {@link PageCommentGAERepository} singleton.
     *
     * @return the singleton
     */
    public static PageCommentGAERepository getInstance() {
        return SingletonHolder.SINGLETON;
    }

    /**
     * Private default constructor.
     */
    private PageCommentGAERepository() {
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
        private static final PageCommentGAERepository SINGLETON =
                new PageCommentGAERepository();

        /**
         * Private default constructor.
         */
        private SingletonHolder() {
        }
    }
}
