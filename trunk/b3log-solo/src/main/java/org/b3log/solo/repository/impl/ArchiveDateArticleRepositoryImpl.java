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

import java.util.logging.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.repository.AbstractRepository;
import org.b3log.latke.repository.FilterOperator;
import org.b3log.latke.repository.Query;
import org.b3log.solo.model.Article;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.SortDirection;
import org.b3log.solo.model.ArchiveDate;
import org.b3log.solo.repository.ArchiveDateArticleRepository;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Archive date-Article relation Google App Engine repository.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.5, Jan 12, 2011
 */
public final class ArchiveDateArticleRepositoryImpl
        extends AbstractRepository
        implements ArchiveDateArticleRepository {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(ArchiveDateArticleRepositoryImpl.class.getName());

    @Override
    public JSONObject getByArchiveDateId(final String archiveDateId,
                                         final int currentPageNum,
                                         final int pageSize)
            throws RepositoryException {
        final Query query = new Query();
        query.addFilter(ArchiveDate.ARCHIVE_DATE + "_" + Keys.OBJECT_ID,
                        FilterOperator.EQUAL, archiveDateId);
        query.addSort(Article.ARTICLE + "_" + Keys.OBJECT_ID,
                      SortDirection.DESCENDING);
        query.setCurrentPageNum(currentPageNum);
        query.setPageSize(pageSize);

        return get(query);
    }

    @Override
    public JSONObject getByArticleId(final String articleId)
            throws RepositoryException {
        final Query query = new Query();
        query.addFilter(Article.ARTICLE + "_" + Keys.OBJECT_ID,
                        FilterOperator.EQUAL, articleId);

        final JSONObject result = get(query);
        final JSONArray array = result.optJSONArray(Keys.RESULTS);
        if (0 == array.length()) {
            return null;
        }

        return array.optJSONObject(0);
    }

    /**
     * Gets the {@link ArchiveDateArticleGAERepository} singleton.
     *
     * @return the singleton
     */
    public static ArchiveDateArticleRepositoryImpl getInstance() {
        return SingletonHolder.SINGLETON;
    }

    /**
     * Private constructor.
     * 
     * @param name the specified name
     */
    private ArchiveDateArticleRepositoryImpl(final String name) {
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
        private static final ArchiveDateArticleRepositoryImpl SINGLETON =
                new ArchiveDateArticleRepositoryImpl(ArchiveDate.ARCHIVE_DATE
                                                     + "_" + Article.ARTICLE);

        /**
         * Private default constructor.
         */
        private SingletonHolder() {
        }
    }
}
