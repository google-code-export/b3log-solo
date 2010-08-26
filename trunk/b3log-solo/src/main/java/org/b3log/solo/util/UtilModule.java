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
package org.b3log.solo.util;

import com.google.inject.Scopes;

/**
 * Utilities module for <a href="http://code.google.com/p/google-guice/">
 * Guice</a> configurations.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.1.0, Aug 19, 2010
 */
public final class UtilModule extends org.b3log.latke.util.UtilModule {

    @Override
    protected void configure() {
        super.configure();

        bind(ArticleUtils.class).in(Scopes.SINGLETON);
        bind(TagUtils.class).in(Scopes.SINGLETON);
        bind(Statistics.class).in(Scopes.SINGLETON);
        bind(ArchiveDateUtils.class).in(Scopes.SINGLETON);
        bind(Skins.class).in(Scopes.SINGLETON);
    }
}
