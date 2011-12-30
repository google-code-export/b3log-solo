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
import org.b3log.solo.repository.LinkRepository;
import org.b3log.solo.repository.UserRepository;
import org.b3log.solo.repository.impl.LinkRepositoryImpl;
import org.b3log.solo.repository.impl.UserRepositoryImpl;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

/**
 * Abstract test case.
 * 
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Dec 30, 2011
 * @see #beforeClass() 
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

        userRepository = UserRepositoryImpl.getInstance();
        linkRepository = LinkRepositoryImpl.getInstance();
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
}
