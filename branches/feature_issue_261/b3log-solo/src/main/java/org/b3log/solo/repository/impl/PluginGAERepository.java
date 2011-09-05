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
package org.b3log.solo.repository.impl;

import java.util.logging.Logger;
import org.b3log.latke.model.Plugin;
import org.b3log.latke.repository.gae.AbstractGAERepository;
import org.b3log.solo.repository.PluginRepository;

/**
 * Plugin Google App Engine repository.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Jul 21, 2011
 */
public final class PluginGAERepository extends AbstractGAERepository
        implements PluginRepository {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(PluginGAERepository.class.getName());

    @Override
    public String getName() {
        return Plugin.PLUGIN;
    }

    /**
     * Gets the {@link PluginGAERepository} singleton.
     *
     * @return the singleton
     */
    public static PluginGAERepository getInstance() {
        return SingletonHolder.SINGLETON;
    }

    /**
     * Private default constructor.
     */
    private PluginGAERepository() {
    }

    /**
     * Singleton holder.
     *
     * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
     * @version 1.0.0.0, Jan 12, 2011
     */
    private static final class SingletonHolder {

        /**
         * Singleton.
         */
        private static final PluginGAERepository SINGLETON =
                new PluginGAERepository();

        /**
         * Private default constructor.
         */
        private SingletonHolder() {
        }
    }
}
