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

import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.latke.repository.Transaction;
import org.b3log.solo.repository.FileRepository;
import org.b3log.solo.repository.impl.FileRepositoryImpl;

/**
 * File management service.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Oct 25, 2011
 * @since 0.4.0
 */
public final class FileMgmtService {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(FileMgmtService.class.getName());
    /**
     * File repository.
     */
    private FileRepository fileRepository = FileRepositoryImpl.getInstance();

    /**
     * Removes a file specified by the given file id.
     *
     * @param fileId the given file id
     * @throws Exception exception
     */
    public void removeFile(final String fileId) throws Exception {
        final Transaction transaction = fileRepository.beginTransaction();

        try {
            fileRepository.remove(fileId);
            transaction.commit();
        } catch (final Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }

            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Gets the {@link FileMgmtService} singleton.
     *
     * @return the singleton
     */
    public static FileMgmtService getInstance() {
        return SingletonHolder.SINGLETON;
    }

    /**
     * Private constructor.
     */
    private FileMgmtService() {
    }

    /**
     * Singleton holder.
     *
     * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
     * @version 1.0.0.0, Oct 25, 2011
     */
    private static final class SingletonHolder {

        /**
         * Singleton.
         */
        private static final FileMgmtService SINGLETON =
                new FileMgmtService();

        /**
         * Private default constructor.
         */
        private SingletonHolder() {
        }
    }
}
