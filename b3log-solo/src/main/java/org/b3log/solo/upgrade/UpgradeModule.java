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

package org.b3log.solo.upgrade;

import com.google.inject.Scopes;
import com.google.inject.servlet.ServletModule;

/**
 * Upgrade module for <a href="http://code.google.com/p/google-guice/">
 * Guice</a> configurations.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.3, Dec 2, 2010
 */
public final class UpgradeModule extends ServletModule {

    @Override
    protected void configureServlets() {
        super.configureServlets();

        bind(V010ToV011.class).in(Scopes.SINGLETON);
        serve("/upgrade/v010-v011.do").with(V010ToV011.class);

        bind(V011ToV020.class).in(Scopes.SINGLETON);
        serve("/upgrade/v011-v020.do").with(V011ToV020.class);

        bind(V020ToV021.class).in(Scopes.SINGLETON);
        serve("/upgrade/v020-v021.do").with(V020ToV021.class);

        bind(V021ToV025.class).in(Scopes.SINGLETON);
        serve("/upgrade/v021-v025.do").with(V021ToV025.class);
    }
}
