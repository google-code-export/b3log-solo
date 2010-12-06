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

package org.b3log.solo.action;

import com.google.inject.Scopes;
import org.b3log.solo.action.impl.ArticleAction;
import org.b3log.solo.action.impl.TagArticlesAction;
import org.b3log.solo.action.impl.IndexAction;
import org.b3log.solo.action.impl.TagsAction;
import org.b3log.solo.action.util.Filler;
import org.b3log.latke.action.AbstractActionModule;
import org.b3log.solo.action.captcha.CaptchaServlet;
import org.b3log.solo.action.impl.AdminIndexAction;
import org.b3log.solo.action.feed.BlogArticlesFeedServlet;
import org.b3log.solo.action.feed.TagArticlesFeedServlet;
import org.b3log.solo.action.file.BlobStoreFileAccessServlet;
import org.b3log.solo.action.file.DataStoreFileAccessServlet;
import org.b3log.solo.action.gae.LiveServlet;
import org.b3log.solo.action.impl.ArchiveDateArticlesAction;
import org.b3log.solo.action.impl.AdminDoNothingAction;
import org.b3log.solo.action.impl.AdminErrorAction;
import org.b3log.solo.action.impl.InitAction;
import org.b3log.solo.action.impl.PageAction;
import org.b3log.solo.filter.FilterModule;
import org.b3log.solo.google.GoogleModule;
import org.b3log.solo.upgrade.UpgradeModule;

/**
 * Action module for <a href="http://code.google.com/p/google-guice/">
 * Guice</a> configurations.
 *
 * <p>
 * All servlets and filters MUST be configured in this module. If another module
 * is also a servlet module, installs it in this module.
 * </p>
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.2.0, Dec 6, 2010
 */
public final class ActionModule extends AbstractActionModule {

    /**
     * Admin action serve URLs.
     */
    public static final String[] ADMIN_ACTIONS = new String[]{
        "/admin-index.do",
        "/admin-article.do",
        "/admin-article-list.do",
        "/admin-link-list.do",
        "/admin-preference.do",
        "/admin-article-sync.do",
        "/admin-file-list.do",
        "/admin-page.do",
        "/admin-others.do",
        "/admin-draft-list.do",
        "/admin-user-list.do"
    };

    /**
     * Public default constructor.
     */
    public ActionModule() {
        putJabsorbInitParam("gzip_threshold", "-1");
    }

    /**
     * {@inheritDoc}
     * And actions.
     *
     * <p>
     *   Modules:
     *   <ul>
     *     <li>{@link FilterModule}</li>
     *     <li>{@link GoogleModule}</li>
     *     <li>{@link UpgradeModule}</li>
     *   </ul>
     * </p>
     */
    @Override
    protected void configureServlets() {
        super.configureServlets();

        install(new FilterModule());
        install(new GoogleModule());
        install(new UpgradeModule());

        bind(InitAction.class).in(Scopes.SINGLETON);
        serve("/init.do").with(InitAction.class);

        bind(AdminIndexAction.class).in(Scopes.SINGLETON);
        serve(ADMIN_ACTIONS[0]).with(AdminIndexAction.class);

        bind(IndexAction.class).in(Scopes.SINGLETON);
        serve("/", "/index.do").with(IndexAction.class);

        bind(ArticleAction.class).in(Scopes.SINGLETON);
        serve("/article-detail.do").with(ArticleAction.class);

        bind(TagArticlesAction.class).in(Scopes.SINGLETON);
        serve("/tag-articles.do").with(TagArticlesAction.class);

        bind(ArchiveDateArticlesAction.class).in(Scopes.SINGLETON);
        serve("/archive-date-articles.do").with(ArchiveDateArticlesAction.class);

        bind(TagsAction.class).in(Scopes.SINGLETON);
        serve("/tags.do").with(TagsAction.class);

        bind(PageAction.class).in(Scopes.SINGLETON);
        serve("/page.do").with(PageAction.class);

        bind(AdminDoNothingAction.class).in(Scopes.SINGLETON);
        for (int i = 1; i < ADMIN_ACTIONS.length; i++) {
            serve(ADMIN_ACTIONS[i]).with(AdminDoNothingAction.class);
        }

        bind(Filler.class).in(Scopes.SINGLETON);

        bind(BlogArticlesFeedServlet.class).in(Scopes.SINGLETON);
        serve("/blog-articles-feed.do").with(BlogArticlesFeedServlet.class);
        bind(TagArticlesFeedServlet.class).in(Scopes.SINGLETON);
        serve("/tag-articles-feed.do").with(TagArticlesFeedServlet.class);

        bind(CaptchaServlet.class).in(Scopes.SINGLETON);
        serve("/captcha.do").with(CaptchaServlet.class);

        bind(AdminErrorAction.class).in(Scopes.SINGLETON);
        serve("/admin-error.do").with(AdminErrorAction.class);

        bind(BlobStoreFileAccessServlet.class).in(Scopes.SINGLETON);
        serve("/file-access.do").with(BlobStoreFileAccessServlet.class);

        bind(DataStoreFileAccessServlet.class).in(Scopes.SINGLETON);
        serve("/datastore-file-access.do").with(DataStoreFileAccessServlet.class);

        bind(LiveServlet.class).in(Scopes.SINGLETON);
        serve("/live.do").with(LiveServlet.class);
    }
}
