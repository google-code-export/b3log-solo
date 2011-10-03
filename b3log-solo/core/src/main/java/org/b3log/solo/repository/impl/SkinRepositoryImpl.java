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
import org.b3log.latke.repository.AbstractRepository;
import org.b3log.solo.model.Skin;
import org.b3log.solo.repository.SkinRepository;

/**
 * Skin repository.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Jan 12, 2011
 * @since 0.3.1
 */
public final class SkinRepositoryImpl extends AbstractRepository
        implements SkinRepository {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(SkinRepositoryImpl.class.getName());

    /**
     * Gets the {@link SkinRepositoryImpl} singleton.
     *
     * @return the singleton
     */
    public static SkinRepositoryImpl getInstance() {
        return SingletonHolder.SINGLETON;
    }

    /**
     * Private constructor.
     * 
     * @param name the specified name
     */
    private SkinRepositoryImpl(final String name) {
        super(name);
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
        private static final SkinRepositoryImpl SINGLETON =
                new SkinRepositoryImpl(Skin.SKIN);

        /**
         * Private default constructor.
         */
        private SingletonHolder() {
        }
    }
}
