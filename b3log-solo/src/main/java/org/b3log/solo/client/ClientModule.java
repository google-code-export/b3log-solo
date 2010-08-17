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
package org.b3log.solo.client;

import com.google.inject.Scopes;
import org.b3log.solo.client.action.impl.ArticleAction;
import org.b3log.solo.client.action.impl.TagArticlesAction;
import org.b3log.solo.client.action.impl.IndexAction;
import org.b3log.solo.client.action.impl.TagsAction;
import org.b3log.solo.client.action.util.Filler;
import org.b3log.solo.client.util.ArticleUtils;
import org.b3log.latke.client.AbstractClientModule;
import org.b3log.latke.client.action.DoNothingAction;
import org.b3log.latke.servlet.filter.AuthenticationFilter;
import org.b3log.solo.client.action.impl.AdminIndexAction;
import org.b3log.solo.client.feed.FeedServlet;
import org.b3log.solo.client.util.Preferences;
import org.b3log.solo.client.util.TagUtils;

/**
 * Client-side module for <a href="http://code.google.com/p/google-guice/">
 * Guice</a> configurations.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.8, Aug 17, 2010
 */
public final class ClientModule extends AbstractClientModule {

    @Override
    protected void configureServlets() {
        super.configureServlets();

        bind(AuthenticationFilter.class).in(Scopes.SINGLETON);
        filter("/admin-index.do").through(AuthenticationFilter.class);

        bind(AdminIndexAction.class).in(Scopes.SINGLETON);
        serve("/admin-index.do").with(AdminIndexAction.class);

        bind(IndexAction.class).in(Scopes.SINGLETON);
        serve("/index.do").with(IndexAction.class);

        bind(ArticleAction.class).in(Scopes.SINGLETON);
        serve("/article-detail.do").with(ArticleAction.class);

        bind(TagArticlesAction.class).in(Scopes.SINGLETON);
        serve("/tag-articles.do").with(TagArticlesAction.class);

        bind(TagsAction.class).in(Scopes.SINGLETON);
        serve("/tags.do").with(TagsAction.class);

        bind(DoNothingAction.class).in(Scopes.SINGLETON);
        serve("/admin-article.do",
              "/admin-article-list.do",
              "/admin-link-list.do",
              "/admin-preference.do",
              "/admin-sync.do").with(DoNothingAction.class);

        bind(Filler.class).in(Scopes.SINGLETON);

        bind(ArticleUtils.class).in(Scopes.SINGLETON);
        bind(TagUtils.class).in(Scopes.SINGLETON);
        bind(Preferences.class).in(Scopes.SINGLETON);

        bind(FeedServlet.class).in(Scopes.SINGLETON);
        serve("/feed.do").with(FeedServlet.class);
    }
}
