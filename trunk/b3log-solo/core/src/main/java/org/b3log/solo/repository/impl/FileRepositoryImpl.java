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
import org.b3log.solo.model.File;
import org.b3log.solo.repository.FileRepository;

/**
 * File Google App Engine repository.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.3, Feb 8, 2011
 */
public final class FileRepositoryImpl extends AbstractRepository
        implements FileRepository {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(FileRepositoryImpl.class.getName());

    /**
     * Gets the {@link FileGAERepository} singleton.
     *
     * @return the singleton
     */
    public static FileRepositoryImpl getInstance() {
        return SingletonHolder.SINGLETON;
    }

    /**
     * Private constructor.
     * 
     * @param name the specified name
     */
    private FileRepositoryImpl(final String name) {
        super(name);
        setCacheEnabled(false);
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
        private static final FileRepositoryImpl SINGLETON =
                new FileRepositoryImpl(File.FILE);

        /**
         * Private default constructor.
         */
        private SingletonHolder() {
        }
    }
}