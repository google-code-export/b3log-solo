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

package org.b3log.solo.repository.impl;

import java.util.logging.Logger;
import org.b3log.latke.repository.gae.AbstractGAERepository;
import org.b3log.solo.model.Page;
import org.b3log.solo.repository.PageRepository;

/**
 * Page Google App Engine repository.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Sep 28, 2010
 */
public final class PageGAERepository extends AbstractGAERepository
        implements PageRepository {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(PageGAERepository.class.getName());

    @Override
    public String getName() {
        return Page.PAGE;
    }
}
