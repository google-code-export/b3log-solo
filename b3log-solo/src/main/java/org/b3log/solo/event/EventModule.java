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

package org.b3log.solo.event;

import java.util.logging.Logger;
import org.b3log.solo.event.sync.impl.CSDNBlogAddArticleProcessor;
import org.b3log.latke.event.AbstractEventModule;
import org.b3log.solo.event.buzz.ActivityCreator;
import org.b3log.solo.event.comment.ArticleCommentReplyNotifier;
import org.b3log.solo.event.comment.PageCommentReplyNotifier;
import org.b3log.solo.event.preference.DisplayCntLoader;
import org.b3log.solo.event.preference.GoogleSettingsLoader;
import org.b3log.solo.event.preference.HTMLHeadLoader;
import org.b3log.solo.event.preference.MetaTagLoader;
import org.b3log.solo.event.preference.NoticeBoardLoader;
import org.b3log.solo.event.rhythm.ArticleSender;
import org.b3log.solo.event.sync.impl.BlogJavaAddArticleProcessor;
import org.b3log.solo.event.sync.impl.BlogJavaRemoveArticleProcessor;
import org.b3log.solo.event.sync.impl.BlogJavaUpdateArticleProcessor;
import org.b3log.solo.event.sync.impl.CSDNBlogRemoveArticleProcessor;
import org.b3log.solo.event.sync.impl.CSDNBlogUpdateArticleProcessor;
import org.b3log.solo.event.sync.impl.CnBlogsAddArticleProcessor;
import org.b3log.solo.event.sync.impl.CnBlogsRemoveArticleProcessor;
import org.b3log.solo.event.sync.impl.CnBlogsUpdateArticleProcessor;

/**
 * Event module for IoC
 * environment(<a href="http://code.google.com/p/google-guice/">Guice</a>)
 * configurations.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.1.6, Oct 29, 2010
 */
public final class EventModule extends AbstractEventModule {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(EventModule.class.getName());

    /**
     * Public default constructor.
     */
    public EventModule() {
    }

    /**
     * Configures event manager and event listeners.
     */
    @Override
    protected void configure() {
        super.configure();

        bind(CSDNBlogAddArticleProcessor.class).asEagerSingleton();
        bind(CSDNBlogRemoveArticleProcessor.class).asEagerSingleton();
        bind(CSDNBlogUpdateArticleProcessor.class).asEagerSingleton();
        bind(CnBlogsAddArticleProcessor.class).asEagerSingleton();
        bind(CnBlogsRemoveArticleProcessor.class).asEagerSingleton();
        bind(CnBlogsUpdateArticleProcessor.class).asEagerSingleton();
        bind(BlogJavaAddArticleProcessor.class).asEagerSingleton();
        bind(BlogJavaRemoveArticleProcessor.class).asEagerSingleton();
        bind(BlogJavaUpdateArticleProcessor.class).asEagerSingleton();

        bind(NoticeBoardLoader.class).asEagerSingleton();
        bind(HTMLHeadLoader.class).asEagerSingleton();
        bind(MetaTagLoader.class).asEagerSingleton();
        bind(GoogleSettingsLoader.class).asEagerSingleton();
        bind(DisplayCntLoader.class).asEagerSingleton();
        bind(ArticleCommentReplyNotifier.class).asEagerSingleton();
        bind(PageCommentReplyNotifier.class).asEagerSingleton();
        bind(ActivityCreator.class).asEagerSingleton();
        bind(ArticleSender.class).asEagerSingleton();
    }
}
