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
import java.util.logging.Logger;
import org.b3log.latke.repository.gae.AbstractGAERepository;
import org.b3log.solo.model.BlogSync;
import org.b3log.solo.repository.BlogSyncManagementRepository;
import org.json.JSONObject;

/**
 * Blog sync management Google App Engine repository.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.2, Jan 12, 2011
 */
public final class BlogSyncMgmtGAERepository extends AbstractGAERepository
        implements BlogSyncManagementRepository {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(BlogSyncMgmtGAERepository.class.getName());

    @Override
    public String getName() {
        return BlogSync.BLOG_SYNC_MANAGEMENT;
    }

    @Override
    public JSONObject getByExternalBloggingSystem(
            final String externalBloggingSystem) {
        final Query query = new Query(getName());
        query.addFilter(BlogSync.BLOG_SYNC_EXTERNAL_BLOGGING_SYS,
                        Query.FilterOperator.EQUAL, externalBloggingSystem);
        final PreparedQuery preparedQuery = getDatastoreService().prepare(query);
        final Entity entity = preparedQuery.asSingleEntity();
        if (null == entity) {
            return null;
        }

        return entity2JSONObject(entity);
    }

    /**
     * Gets the {@link BlogSyncMgmtGAERepository} singleton.
     *
     * @return the singleton
     */
    public static BlogSyncMgmtGAERepository getInstance() {
        return SingletonHolder.SINGLETON;
    }

    /**
     * Private default constructor.
     */
    private BlogSyncMgmtGAERepository() {
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
        private static final BlogSyncMgmtGAERepository SINGLETON =
                new BlogSyncMgmtGAERepository();

        /**
         * Private default constructor.
         */
        private SingletonHolder() {
        }
    }
}