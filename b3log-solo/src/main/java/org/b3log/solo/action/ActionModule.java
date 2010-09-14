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
package org.b3log.solo.action;

import com.google.inject.Scopes;
import org.b3log.solo.action.impl.ArticleAction;
import org.b3log.solo.action.impl.TagArticlesAction;
import org.b3log.solo.action.impl.IndexAction;
import org.b3log.solo.action.impl.TagsAction;
import org.b3log.solo.action.util.Filler;
import org.b3log.latke.action.AbstractActionModule;
import org.b3log.latke.action.DoNothingAction;
import org.b3log.solo.action.captcha.CaptchaServlet;
import org.b3log.solo.action.impl.AdminIndexAction;
import org.b3log.solo.action.feed.BlogArticlesFeedServlet;
import org.b3log.solo.action.feed.TagArticlesFeedServlet;
import org.b3log.solo.action.file.FileAccessServlet;
import org.b3log.solo.action.impl.ArchiveDateArticlesAction;
import org.b3log.solo.auth.AuthFilter;
import org.b3log.solo.action.google.OAuthCallback;

/**
 * Action module for <a href="http://code.google.com/p/google-guice/">
 * Guice</a> configurations.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.8, Sep 14, 2010
 */
public final class ActionModule extends AbstractActionModule {

    @Override
    protected void configureServlets() {
        super.configureServlets();

        bind(AuthFilter.class).in(Scopes.SINGLETON);
        filter("/admin-index.do").through(AuthFilter.class);

        bind(AdminIndexAction.class).in(Scopes.SINGLETON);
        serve("/admin-index.do").with(AdminIndexAction.class);

        bind(IndexAction.class).in(Scopes.SINGLETON);
        serve("/index.do").with(IndexAction.class);

        bind(ArticleAction.class).in(Scopes.SINGLETON);
        serve("/article-detail.do").with(ArticleAction.class);

        bind(TagArticlesAction.class).in(Scopes.SINGLETON);
        serve("/tag-articles.do").with(TagArticlesAction.class);

        bind(ArchiveDateArticlesAction.class).in(Scopes.SINGLETON);
        serve("/archive-date-articles.do").with(ArchiveDateArticlesAction.class);

        bind(TagsAction.class).in(Scopes.SINGLETON);
        serve("/tags.do").with(TagsAction.class);

        bind(DoNothingAction.class).in(Scopes.SINGLETON);
        serve("/admin-article.do",
              "/admin-article-list.do",
              "/admin-link-list.do",
              "/admin-preference.do",
              "/admin-article-sync.do",
              "/admin-file-list.do").with(DoNothingAction.class);

        bind(Filler.class).in(Scopes.SINGLETON);

        bind(BlogArticlesFeedServlet.class).in(Scopes.SINGLETON);
        serve("/blog-articles-feed.do").with(BlogArticlesFeedServlet.class);
        bind(TagArticlesFeedServlet.class).in(Scopes.SINGLETON);
        serve("/tag-articles-feed.do").with(TagArticlesFeedServlet.class);

        bind(CaptchaServlet.class).in(Scopes.SINGLETON);
        serve("/captcha.do").with(CaptchaServlet.class);

//        bind(ErrorAction.class).in(Scopes.SINGLETON);
//        serve("/error.do").with(ErrorAction.class);

        bind(FileAccessServlet.class).in(Scopes.SINGLETON);
        serve("/file-access.do").with(FileAccessServlet.class);

        bind(OAuthCallback.class).in(Scopes.SINGLETON);
        serve("/oauth-callback.do").with(OAuthCallback.class);

    }
}
