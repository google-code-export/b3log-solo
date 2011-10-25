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

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.action.util.Paginator;
import org.b3log.latke.model.Pagination;
import org.b3log.latke.repository.Query;
import org.b3log.latke.repository.SortDirection;
import org.b3log.latke.service.ServiceException;
import org.b3log.solo.model.File;
import org.b3log.solo.repository.FileRepository;
import org.b3log.solo.repository.impl.FileRepositoryImpl;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * File query service.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Oct 25, 2011
 * @since 0.4.0
 */
public final class FileQueryService {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(FileQueryService.class.getName());
    /**
     * File repository.
     */
    private FileRepository fileRepository = FileRepositoryImpl.getInstance();

    /**
     * Gets the file with the specified request json object, http servlet
     * request and response.
     *
     * @param requestJSONObject the specified request json object, for example,
     * <pre>
     * {
     *     "paginationCurrentPageNum": 1,
     *     "paginationPageSize": 20,
     *     "paginationWindowSize": 10
     * }
     * </pre>
     * @return for example,
     * <pre>
     * {
     *     "pagination": {
     *         "paginationPageCount": int,
     *         "paginationPageNums": [1, 2, 3, 4, 5, int]
     *     },
     *     "rslts": [{
     *         "fileName": "",
     *         "fileSize": long,
     *         "fileDownloadCount": int,
     *         "fileUploadTime": long
     *     }, ....]
     * }
     * </pre>
     * @throws ServiceException service exception
     */
    public JSONObject getFiles(final JSONObject requestJSONObject)
            throws ServiceException {
        final JSONObject ret = new JSONObject();

        try {
            final int currentPageNum = requestJSONObject.getInt(
                    Pagination.PAGINATION_CURRENT_PAGE_NUM);
            final int pageSize = requestJSONObject.getInt(
                    Pagination.PAGINATION_PAGE_SIZE);
            final int windowSize = requestJSONObject.getInt(
                    Pagination.PAGINATION_WINDOW_SIZE);

            final Query query = new Query().setCurrentPageNum(currentPageNum).
                    setPageSize(pageSize).
                    addSort(File.FILE_UPLOAD_DATE, SortDirection.DESCENDING);
            final JSONObject result = fileRepository.get(query);
            final int pageCount = result.getJSONObject(Pagination.PAGINATION).
                    getInt(Pagination.PAGINATION_PAGE_COUNT);

            final JSONObject pagination = new JSONObject();
            ret.put(Pagination.PAGINATION, pagination);
            final List<Integer> pageNums =
                    Paginator.paginate(currentPageNum, pageSize, pageCount,
                                       windowSize);
            pagination.put(Pagination.PAGINATION_PAGE_COUNT, pageCount);
            pagination.put(Pagination.PAGINATION_PAGE_NUMS, pageNums);

            final JSONArray files = result.getJSONArray(Keys.RESULTS);
            for (int i = 0; i < files.length(); i++) { // Remove unused properties
                final JSONObject file = files.getJSONObject(i);
                file.remove(File.FILE_CONTENT_TYPE);
                file.remove(File.FILE_CONTENT);
                file.put(File.FILE_UPLOAD_TIME,
                         ((Date) file.get(File.FILE_UPLOAD_DATE)).getTime());
                file.remove(File.FILE_UPLOAD_DATE);
            }

            ret.put(File.FILES, files);

            return ret;
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);

            throw new ServiceException(e);
        }
    }

    /**
     * Gets the {@link FileQueryService} singleton.
     *
     * @return the singleton
     */
    public static FileQueryService getInstance() {
        return SingletonHolder.SINGLETON;
    }

    /**
     * Private constructor.
     */
    private FileQueryService() {
    }

    /**
     * Singleton holder.
     *
     * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
     * @version 1.0.0.0, Oct 24, 2011
     */
    private static final class SingletonHolder {

        /**
         * Singleton.
         */
        private static final FileQueryService SINGLETON =
                new FileQueryService();

        /**
         * Private default constructor.
         */
        private SingletonHolder() {
        }
    }
}
