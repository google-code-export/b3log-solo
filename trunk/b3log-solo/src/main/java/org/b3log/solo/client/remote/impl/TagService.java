/*
 * Copyright (C) 2009, 2010, B3log Team
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
package org.b3log.solo.client.remote.impl;

import com.google.inject.Inject;
import org.apache.log4j.Logger;
import org.b3log.solo.repository.TagArticleRepository;
import org.b3log.solo.repository.TagRepository;
import org.b3log.latke.client.remote.AbstractRemoteService;

/**
 * Tag service for JavaScript client.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Aug 8, 2010
 */
public final class TagService extends AbstractRemoteService {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(TagService.class);
    /**
     * Tag repository.
     */
    @Inject
    private TagRepository tagRepository;
    /**
     * Tag-Article repository.
     */
    @Inject
    private TagArticleRepository tagArticleRepository;
}
