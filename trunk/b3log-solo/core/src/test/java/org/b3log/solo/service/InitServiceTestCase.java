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
package org.b3log.solo.service;

import org.b3log.solo.AbstractTestCase;
import org.testng.annotations.Test;

/**
 * {@link InitService} test case.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Jan 6, 2012
 */
public class InitServiceTestCase extends AbstractTestCase {

    /**
     * Init.
     */
    @Test
    public void init() {
        final InitService initService = InitService.getInstance();

        //initService.init(requestJSONObject, null, null);
    }
}
