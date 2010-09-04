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
package org.b3log.solo.repository;

import com.google.inject.Scopes;
import org.b3log.solo.repository.impl.ArticleCommentGAERepository;
import org.b3log.solo.repository.impl.ArticleGAERepository;
import org.b3log.solo.repository.impl.CommentGAERepository;
import org.b3log.solo.repository.impl.TagArticleGAERepository;
import org.b3log.solo.repository.impl.TagGAERepository;
import org.b3log.latke.service.AbstractServiceModule;
import org.b3log.solo.repository.impl.ArchiveDateArticleGAERepository;
import org.b3log.solo.repository.impl.ArchiveDateGAERepository;
import org.b3log.solo.repository.impl.BlogSyncMgmtGAERepository;
import org.b3log.solo.repository.impl.ExternalArticleGAERepository;
import org.b3log.solo.repository.impl.ExternalArticleSoloArticleGAERepository;
import org.b3log.solo.repository.impl.LinkGAERepository;
import org.b3log.solo.repository.impl.PreferenceGAERepository;
import org.b3log.solo.repository.impl.SkinGAERepository;
import org.b3log.solo.repository.impl.StatisticGAERepository;
import org.b3log.solo.repository.impl.UserGAERepository;

/**
 * Repository module for <a href="http://code.google.com/p/google-guice/">
 * Guice</a> configurations.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.9, Aug 25, 2010
 */
public final class RepositoryModule extends AbstractServiceModule {

    @Override
    protected void configure() {
        super.configure();

        bind(ArticleRepository.class).to(ArticleGAERepository.class).
                in(Scopes.SINGLETON);
        bind(TagRepository.class).to(TagGAERepository.class).
                in(Scopes.SINGLETON);
        bind(TagArticleRepository.class).to(TagArticleGAERepository.class).
                in(Scopes.SINGLETON);
        bind(CommentRepository.class).to(CommentGAERepository.class).
                in(Scopes.SINGLETON);
        bind(ArticleCommentRepository.class).to(
                ArticleCommentGAERepository.class).
                in(Scopes.SINGLETON);
        bind(LinkRepository.class).to(LinkGAERepository.class).
                in(Scopes.SINGLETON);
        bind(PreferenceRepository.class).to(PreferenceGAERepository.class).
                in(Scopes.SINGLETON);
        bind(UserRepository.class).to(UserGAERepository.class).
                in(Scopes.SINGLETON);
        bind(StatisticRepository.class).to(StatisticGAERepository.class).
                in(Scopes.SINGLETON);
        bind(ExternalArticleRepository.class).to(
                ExternalArticleGAERepository.class).in(Scopes.SINGLETON);
        bind(ArchiveDateRepository.class).to(
                ArchiveDateGAERepository.class).in(Scopes.SINGLETON);
        bind(ArchiveDateArticleRepository.class).to(
                ArchiveDateArticleGAERepository.class).in(Scopes.SINGLETON);
        bind(SkinRepository.class).to(
                SkinGAERepository.class).in(Scopes.SINGLETON);
        bind(ExternalArticleSoloArticleRepository.class).to(
                ExternalArticleSoloArticleGAERepository.class).in(
                Scopes.SINGLETON);
        bind(BlogSyncManagementRepository.class).to(
                BlogSyncMgmtGAERepository.class).in(Scopes.SINGLETON);
    }
}
