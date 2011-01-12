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

package org.b3log.solo.util;

import com.google.appengine.api.utils.SystemProperty;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.latke.util.Strings;

/**
 * Page cache key utilities.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.6, Jan 12, 2011
 */
public final class PageCacheKeys {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(PageCacheKeys.class.getName());

    /**
     * Gets page cache key by the specified URI and query string.
     *
     * <p>
     * In cluster environment(multiple application instance replicas), the memory
     * cache may be individual(depends on underlying memory cache strategy).
     * </p>
     *
     * <p>
     * <a href="http://code.google.com/appengine">Google App Engine</a>
     * will setup an application instance replicas sometimes(high traffic, redeploy
     * application, etc), so the cache service (provided by
     * <a href="http://code.google.com/appengine/docs/java/memcache/">
     * memcache service</a>) may be used in these instances individually. For
     * consistency reason, this method will add the <i>instance id</i> as the
     * prefix of the key. The <i>instance id</i> may be {@code null} if only one
     * instance is serving.
     * </p>
     *
     * @param uri the specified URI
     * @param queryString the specified query string
     * @return cache key
     */
    public String getPageCacheKey(final String uri,
                                  final String queryString) {
        String ret = SystemProperty.instanceReplicaId.get() + "_" + uri;

        try {
            if (!Strings.isEmptyOrNull(queryString)) {
                ret += "?" + queryString;
            }

            return ret;
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

        return ret;
    }

    /**
     * Gets the {@link PageCacheKeys} singleton.
     *
     * @return the singleton
     */
    public static PageCacheKeys getInstance() {
        return SingletonHolder.SINGLETON;
    }

    /**
     * Private default constructor.
     */
    private PageCacheKeys() {
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
        private static final PageCacheKeys SINGLETON = new PageCacheKeys();

        /**
         * Private default constructor.
         */
        private SingletonHolder() {
        }
    }
}
