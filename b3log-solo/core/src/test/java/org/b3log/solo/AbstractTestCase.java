/*
 * Copyright (c) 2009, 2010, 2011, B3log Team
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
package org.b3log.solo;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalMemcacheServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import org.b3log.latke.Latkes;
import org.b3log.latke.servlet.AbstractServletListener;
import org.b3log.solo.repository.ArchiveDateArticleRepository;
import org.b3log.solo.repository.ArchiveDateRepository;
import org.b3log.solo.repository.ArticleRepository;
import org.b3log.solo.repository.CommentRepository;
import org.b3log.solo.repository.FileRepository;
import org.b3log.solo.repository.LinkRepository;
import org.b3log.solo.repository.PageRepository;
import org.b3log.solo.repository.PluginRepository;
import org.b3log.solo.repository.PreferenceRepository;
import org.b3log.solo.repository.StatisticRepository;
import org.b3log.solo.repository.TagArticleRepository;
import org.b3log.solo.repository.TagRepository;
import org.b3log.solo.repository.UserRepository;
import org.b3log.solo.repository.impl.ArchiveDateArticleRepositoryImpl;
import org.b3log.solo.repository.impl.ArchiveDateRepositoryImpl;
import org.b3log.solo.repository.impl.ArticleRepositoryImpl;
import org.b3log.solo.repository.impl.CommentRepositoryImpl;
import org.b3log.solo.repository.impl.FileRepositoryImpl;
import org.b3log.solo.repository.impl.LinkRepositoryImpl;
import org.b3log.solo.repository.impl.PageRepositoryImpl;
import org.b3log.solo.repository.impl.PluginRepositoryImpl;
import org.b3log.solo.repository.impl.PreferenceRepositoryImpl;
import org.b3log.solo.repository.impl.StatisticRepositoryImpl;
import org.b3log.solo.repository.impl.TagArticleRepositoryImpl;
import org.b3log.solo.repository.impl.TagRepositoryImpl;
import org.b3log.solo.repository.impl.UserRepositoryImpl;
import org.b3log.solo.service.UserMgmtService;
import org.b3log.solo.service.UserQueryService;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

/**
 * Abstract test case.
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Dec 31, 2011
 * @see #beforeClass() 
 * @see #afterClass() 
 */
public abstract class AbstractTestCase {

    /**
     * Local service test helper.
     */
    private final LocalServiceTestHelper localServiceTestHelper =
            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig(),
                                       new LocalMemcacheServiceTestConfig());
    /**
     * User repository.
     */
    private UserRepository userRepository;
    /**
     * Link repository.
     */
    private LinkRepository linkRepository;
    /**
     * Article repository.
     */
    private ArticleRepository articleRepository;
    /**
     * Tag repository.
     */
    private TagRepository tagRepository;
    /**
     * Tag-Article repository.
     */
    private TagArticleRepository tagArticleRepository;
    /**
     * Page repository.
     */
    private PageRepository pageRepository;
    /**
     * Comment repository.
     */
    private CommentRepository commentRepository;
    /**
     * Archive date repository.
     */
    private ArchiveDateRepository archiveDateRepository;
    /**
     * Archive date article repository.
     */
    private ArchiveDateArticleRepository archiveDateArticleRepository;
    /**
     * Plugin repository.
     */
    private PluginRepository pluginRepository;
    /**
     * File repository.
     */
    private FileRepository fileRepository;
    /**
     * Preference repository.
     */
    private PreferenceRepository preferenceRepository;
    /**
     * Statistic repository.
     */
    private StatisticRepository statisticRepository;
    /**
     * User management service.
     */
    private UserMgmtService userMgmtService;
    /**
     * User query service.
     */
    private UserQueryService userQueryService;

    /**
     * Before class.
     * 
     * <ol>
     *   <li>Sets up GAE unit test runtime environment</li>
     *   <li>Initializes Latke runtime</li>
     *   <li>Instantiates repositories</li>
     * </ol>
     */
    @BeforeClass
    public void beforeClass() {
        localServiceTestHelper.setUp();

        Latkes.initRuntimeEnv();
        final String webRoot = AbstractServletListener.getWebRoot();
        System.out.println("Web Root[" + webRoot + "]");

        // Repositories
        userRepository = UserRepositoryImpl.getInstance();
        linkRepository = LinkRepositoryImpl.getInstance();
        articleRepository = ArticleRepositoryImpl.getInstance();
        tagRepository = TagRepositoryImpl.getInstance();
        tagArticleRepository = TagArticleRepositoryImpl.getInstance();
        pageRepository = PageRepositoryImpl.getInstance();
        commentRepository = CommentRepositoryImpl.getInstance();
        archiveDateRepository = ArchiveDateRepositoryImpl.getInstance();
        archiveDateArticleRepository =
                ArchiveDateArticleRepositoryImpl.getInstance();
        pluginRepository = PluginRepositoryImpl.getInstance();
        fileRepository = FileRepositoryImpl.getInstance();
        preferenceRepository = PreferenceRepositoryImpl.getInstance();
        statisticRepository = StatisticRepositoryImpl.getInstance();

        // Services
        userMgmtService = UserMgmtService.getInstance();
        userQueryService = UserQueryService.getInstance();
    }

    /**
     * After class.
     * 
     * <ol>
     *   <li>Tears down GAE unit test runtime environment</li>
     *   <li>Shutdowns Latke runtime</li>
     * </ol>
     */
    @AfterClass
    public void afterClass() {
        localServiceTestHelper.tearDown();

        Latkes.shutdown();
    }

    /**
     * Gets user repository.
     * 
     * @return user repository
     */
    public UserRepository getUserRepository() {
        return userRepository;
    }

    /**
     * Gets link repository.
     * 
     * @return link repository
     */
    public LinkRepository getLinkRepository() {
        return linkRepository;
    }

    /**
     * Gets article repository.
     * 
     * @return article repository
     */
    public ArticleRepository getArticleRepository() {
        return articleRepository;
    }

    /**
     * Gets tag repository.
     * 
     * @return tag repository
     */
    public TagRepository getTagRepository() {
        return tagRepository;
    }

    /**
     * Gets tag-article repository.
     * 
     * @return tag-article repository
     */
    public TagArticleRepository getTagArticleRepository() {
        return tagArticleRepository;
    }

    /**
     * Gets page repository.
     * 
     * @return page repository
     */
    public PageRepository getPageRepository() {
        return pageRepository;
    }

    /**
     * Gets comment repository.
     * 
     * @return comment repository
     */
    public CommentRepository getCommentRepository() {
        return commentRepository;
    }

    /**
     * Gets archive date repository.
     * 
     * @return archive date repository
     */
    public ArchiveDateRepository getArchiveDateRepository() {
        return archiveDateRepository;
    }

    /**
     * Archive date article repository.
     * 
     * @return archive date article repository
     */
    public ArchiveDateArticleRepository getArchiveDateArticleRepository() {
        return archiveDateArticleRepository;
    }

    /**
     * Gets plugin repository.
     * 
     * @return plugin repository
     */
    public PluginRepository getPluginRepository() {
        return pluginRepository;
    }

    /**
     * Gets file repository.
     * 
     * @return file repository
     */
    public FileRepository getFileRepository() {
        return fileRepository;
    }

    /**
     * Gets preference repository.
     * 
     * @return preference repository
     */
    public PreferenceRepository getPreferenceRepository() {
        return preferenceRepository;
    }

    /**
     * Gets statistic repository.
     * 
     * @return statistic repository
     */
    public StatisticRepository getStatisticRepository() {
        return statisticRepository;
    }

    /**
     * Gets user management service.
     * 
     * @return user management service
     */
    public UserMgmtService getUserMgmtService() {
        return userMgmtService;
    }

    /**
     * Gets user query service.
     * 
     * @return user query service
     */
    public UserQueryService getUserQueryService() {
        return userQueryService;
    }
}
