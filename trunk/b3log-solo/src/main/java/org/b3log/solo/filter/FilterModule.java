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

package org.b3log.solo.filter;

import com.google.inject.Scopes;
import com.google.inject.servlet.ServletModule;
import org.b3log.solo.SoloServletListener;
import org.b3log.solo.action.ActionModule;

/**
 * Filter module for <a href="http://code.google.com/p/google-guice/">
 * Guice</a> configurations.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.7, Dec 7, 2010
 */
public final class FilterModule extends ServletModule {

    @Override
    protected void configureServlets() {
        bind(AuthFilter.class).in(Scopes.SINGLETON);
        for (int i = 0; i < ActionModule.ADMIN_ACTIONS.length; i++) {
            filter(ActionModule.ADMIN_ACTIONS[i]).through(AuthFilter.class);
        }

        if (!SoloServletListener.isInited()) {
            bind(InitCheckFilter.class).in(Scopes.SINGLETON);
            filter("/*").through(InitCheckFilter.class);
        }

        bind(PageCacheFilter.class).in(Scopes.SINGLETON);
        filter("/*").through(PageCacheFilter.class);

        bind(ArticlePermalinkFilter.class).in(Scopes.SINGLETON);
        filter("/*").through(ArticlePermalinkFilter.class);

        bind(PagePermalinkFilter.class).in(Scopes.SINGLETON);
        filter("/*").through(PagePermalinkFilter.class);

        bind(DefaultArticlePermalinkFilter.class).in(Scopes.SINGLETON);
        filterRegex("/articles/\\d{4}/\\d{2}/\\d{2}/\\d+.html").
                through(DefaultArticlePermalinkFilter.class);

        bind(TagPermalinkFilter.class).in(Scopes.SINGLETON);
        filter("/tags/*").through(TagPermalinkFilter.class);

        bind(TagsFilter.class).in(Scopes.SINGLETON);
        filter("/tags.html").through(TagsFilter.class);
    }
}
