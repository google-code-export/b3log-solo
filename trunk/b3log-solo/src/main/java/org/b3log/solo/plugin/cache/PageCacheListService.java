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

package org.b3log.solo.plugin.cache;

import java.util.logging.Logger;

import org.b3log.solo.jsonrpc.AbstractGAEJSONRpcService;

/**
 * Page cache list service for JavaScript client.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Jun 20, 2011
 */
public final class PageCacheListService extends AbstractGAEJSONRpcService {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(PageCacheListService.class.getName());

    /**
     * Test method.
     */
    public void test() {
        LOGGER.entering(PageCacheListService.class.getName(), "test()");
    }

    /**
     * Gets the {@link PageCacheListService} singleton.
     *
     * @return the singleton
     */
    public static PageCacheListService getInstance() {
        return SingletonHolder.SINGLETON;
    }

    /**
     * Private default constructor.
     */
    private PageCacheListService() {
    }

    /**
     * Singleton holder.
     *
     * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
     * @version 1.0.0.0, Jun 20, 2011
     */
    private static final class SingletonHolder {

        /**
         * Singleton.
         */
        private static final PageCacheListService SINGLETON =
                new PageCacheListService();

        /**
         * Private default constructor.
         */
        private SingletonHolder() {
        }
    }
}
