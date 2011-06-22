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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.b3log.latke.util.Strings;

/**
 * Page cache key utilities.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.7, Jun 21, 2011
 */
public final class PageCacheKeys {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(PageCacheKeys.class.
            getName());

    /**
     * Gets page cache key by the specified URI and query string.
     *
     * @param uri the specified URI
     * @param queryString the specified query string
     * @return cache key
     */
    public String getPageCacheKey(final String uri, final String queryString) {
        String ret = uri;

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
