/*
 * Copyright (c) 2009, 2010, B3log Team
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

package org.b3log.solo.repository;

import org.b3log.latke.repository.Repository;
import org.json.JSONObject;

/**
 * Page repository.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.2, Dec 5, 2010
 */
public interface PageRepository extends Repository {

    /**
     * Gets a page by the specified permalink.
     *
     * @param permalink the specified permalink
     * @return page, returns {@code null} if not found
     */
    JSONObject getByPermalink(final String permalink);

    /**
     * Gets the maximum order.
     *
     * @return order number, returns {@code -1} if not found
     */
    int getMaxOrder();

    /**
     * Gets a page by the specified order.
     *
     * @param order the specified order
     * @return page, returns {@code null} if not found
     */
    JSONObject getByOrder(final int order);
}
