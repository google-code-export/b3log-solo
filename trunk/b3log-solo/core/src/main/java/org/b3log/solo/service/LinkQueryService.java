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

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.action.util.Paginator;
import org.b3log.latke.model.Pagination;
import org.b3log.latke.repository.Query;
import org.b3log.latke.repository.SortDirection;
import org.b3log.latke.service.ServiceException;
import org.b3log.solo.model.Link;
import org.b3log.solo.repository.LinkRepository;
import org.b3log.solo.repository.impl.LinkRepositoryImpl;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Link query service.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Oct 27, 2011
 * @since 0.4.0
 */
public final class LinkQueryService {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(LinkQueryService.class.getName());
    /**
     * Link repository.
     */
    private LinkRepository linkRepository = LinkRepositoryImpl.getInstance();

    /**
     * Gets links by the specified request json object.
     *
     * @param requestJSONObject the specified request json object, for example,
     * <pre>
     * {
     *     "paginationCurrentPageNum": 1,
     *     "paginationPageSize": 20,
     *     "paginationWindowSize": 10
     * }, see {@link Pagination} for more details
     * </pre>
     * @return for example,
     * <pre>
     * {
     *     "pagination": {
     *         "paginationPageCount": 100,
     *         "paginationPageNums": [1, 2, 3, 4, 5]
     *     },
     *     "links": [{
     *         "oId": "",
     *         "linkTitle": "",
     *         "linkAddress": "",
     *      }, ....]
     * }
     * </pre>
     * @throws ServiceException service exception
     * @see Pagination
     */
    public JSONObject getLinks(final JSONObject requestJSONObject)
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
                    addSort(Link.LINK_ORDER, SortDirection.ASCENDING);
            final JSONObject result = linkRepository.get(query);
            final int pageCount = result.getJSONObject(Pagination.PAGINATION).
                    getInt(Pagination.PAGINATION_PAGE_COUNT);

            final JSONObject pagination = new JSONObject();
            final List<Integer> pageNums =
                    Paginator.paginate(currentPageNum, pageSize, pageCount,
                                       windowSize);
            pagination.put(Pagination.PAGINATION_PAGE_COUNT, pageCount);
            pagination.put(Pagination.PAGINATION_PAGE_NUMS, pageNums);

            final JSONArray links = result.getJSONArray(Keys.RESULTS);

            ret.put(Pagination.PAGINATION, pagination);
            ret.put(Link.LINKS, links);

            return ret;
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, "Gets links failed", e);
            throw new ServiceException(e);
        }
    }

    /**
     * Gets a link by the specified link id.
     *
     * @param linkId the specified link id
     * @return for example,
     * <pre>
     * {
     *     "link": {
     *         "oId": "",
     *         "linkTitle": "",
     *         "linkAddress": ""
     *     }
     * }
     * </pre>, returns {@code null} if not found
     * @throws ServiceException service exception
     */
    public JSONObject getLink(final String linkId)
            throws ServiceException {
        final JSONObject ret = new JSONObject();

        try {
            final JSONObject link = linkRepository.get(linkId);
            if (null == link) {
                return null;
            }

            ret.put(Link.LINK, link);

            return ret;
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, "Gets a link failed", e);

            throw new ServiceException(e);
        }
    }

    /**
     * Gets the {@link LinkQueryService} singleton.
     *
     * @return the singleton
     */
    public static LinkQueryService getInstance() {
        return SingletonHolder.SINGLETON;
    }

    /**
     * Private constructor.
     */
    private LinkQueryService() {
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
        private static final LinkQueryService SINGLETON =
                new LinkQueryService();

        /**
         * Private default constructor.
         */
        private SingletonHolder() {
        }
    }
}
