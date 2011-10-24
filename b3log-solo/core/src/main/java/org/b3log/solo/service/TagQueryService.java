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
package org.b3log.solo.service;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.repository.Query;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.util.CollectionUtils;
import org.b3log.solo.repository.TagRepository;
import org.b3log.solo.repository.impl.TagRepositoryImpl;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Tag query service.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Oct 24, 2011
 * @since 0.4.0
 */
public final class TagQueryService {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(TagQueryService.class.getName());
    /**
     * Tag repository.
     */
    private TagRepository tagRepository =
            TagRepositoryImpl.getInstance();

    /**
     * Gets all tags.
     *
     * @return for example,
     * <pre>
     * [
     *     {"tagTitle": "", "tagReferenceCount": int, ....},
     *     ....
     * ]
     * </pre>
     * @throws Exception exception
     */
    public List<JSONObject> getTags()
            throws Exception {
        try {
            final JSONObject result = tagRepository.get(new Query());
            final JSONArray tagArray = result.getJSONArray(Keys.RESULTS);

            return CollectionUtils.jsonArrayToList(tagArray);
        } catch (final RepositoryException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);

            throw new Exception(e);
        }
    }

    /**
     * Gets the {@link TagQueryService} singleton.
     *
     * @return the singleton
     */
    public static TagQueryService getInstance() {
        return SingletonHolder.SINGLETON;
    }

    /**
     * Private constructor.
     */
    private TagQueryService() {
    }

    /**
     * Singleton holder.
     *
     * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
     * @version 1.0.0.0, Oct 24, 2011
     */
    private static final class SingletonHolder {

        /**
         * Singleton.
         */
        private static final TagQueryService SINGLETON =
                new TagQueryService();

        /**
         * Private default constructor.
         */
        private SingletonHolder() {
        }
    }
}
