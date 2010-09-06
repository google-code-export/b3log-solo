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
package org.b3log.solo.event.listener.sync.impl;

import com.google.inject.Inject;
import org.b3log.latke.event.EventManager;
import org.b3log.solo.event.listener.sync.AbstractAddArticleProcessor;
import org.b3log.solo.model.BlogSync;

/**
 * This listener is responsible for blog sync add article to BlogJava.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Sep 6, 2010
 */
public final class BlogJavaAddArticleProcessor
        extends AbstractAddArticleProcessor {

    /**
     * Constructs a {@link BlogJavaAddArticleProcessor} object with the
     * specified event manager.
     *
     * @param eventManager the specified event manager
     */
    @Inject
    public BlogJavaAddArticleProcessor(final EventManager eventManager) {
        super(eventManager);
    }

    @Override
    public String getExternalBloggingSys() {
        return BlogSync.BLOG_SYNC_BLOGJAVA;
    }
}
