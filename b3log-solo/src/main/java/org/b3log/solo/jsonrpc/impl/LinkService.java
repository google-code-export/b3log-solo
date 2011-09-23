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
package org.b3log.solo.jsonrpc.impl;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.latke.Keys;
import org.b3log.latke.action.ActionException;
import org.b3log.latke.action.util.Paginator;
import org.b3log.latke.model.Pagination;
import org.b3log.latke.repository.Query;
import org.b3log.latke.repository.SortDirection;
import org.b3log.latke.repository.Transaction;
import org.b3log.solo.web.action.StatusCodes;
import org.b3log.solo.jsonrpc.AbstractGAEJSONRpcService;
import org.b3log.solo.model.Link;
import org.b3log.solo.repository.LinkRepository;
import org.b3log.solo.repository.impl.LinkGAERepository;
import org.b3log.solo.util.Users;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Link service for JavaScript client.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.1.0, Aug 21, 2011
 */
public final class LinkService extends AbstractGAEJSONRpcService {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(LinkService.class.getName());
    /**
     * Link repository.
     */
    private LinkRepository linkRepository = LinkGAERepository.getInstance();
    /**
     * User utilities.
     */
    private Users userUtils = Users.getInstance();

    /**
     * Gets a link by the specified request json object.
     *
     * @param requestJSONObject the specified request json object, for example,
     * <pre>
     * {
     *     "oId": ""
     * }
     * </pre>
     * @return for example,
     * <pre>
     * {
     *     "link": {
     *         "oId": "",
     *         "linkTitle": "",
     *         "linkAddress": ""
     *     },
     *     "sc": "GET_LINK_SUCC"
     * }
     * </pre>
     * @throws ActionException action exception
     */
    public JSONObject getLink(final JSONObject requestJSONObject)
            throws ActionException {
        final JSONObject ret = new JSONObject();

        try {
            final String linkId = requestJSONObject.getString(Keys.OBJECT_ID);
            final JSONObject link = linkRepository.get(linkId);
            ret.put(Link.LINK, link);

            ret.put(Keys.STATUS_CODE, StatusCodes.GET_LINK_SUCC);

            LOGGER.log(Level.FINER, "Got a link[oId={0}]", linkId);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new ActionException(e);
        }

        return ret;
    }

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
     *     "sc": "GET_LINKS_SUCC"
     * }
     * </pre>
     * @throws ActionException action exception
     * @see Pagination
     */
    public JSONObject getLinks(final JSONObject requestJSONObject)
            throws ActionException {
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
            ret.put(Keys.STATUS_CODE, StatusCodes.GET_LINKS_SUCC);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new ActionException(e);
        }

        return ret;
    }

    /**
     * Changes link order by the specified link id and order.
     *
     * @param linkId the specified link id
     * @param linkOrder the specified order
     * @param request the specified http servlet request
     * @param response the specified http servlet response
     * @return {@code true} if changed, {@code false} otherwise
     * @throws ActionException action exception
     * @throws IOException io exception
     */
    public boolean changeOrder(final String linkId, final int linkOrder,
                               final HttpServletRequest request,
                               final HttpServletResponse response)
            throws ActionException, IOException {
        final JSONObject ret = new JSONObject();
        if (!userUtils.isAdminLoggedIn()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }

        final Transaction transaction = linkRepository.beginTransaction();

        try {
            final JSONObject link1 = linkRepository.get(linkId);
            final String link1Id = linkId;
            final JSONObject link2 = linkRepository.getByOrder(linkOrder);
            final String link2Id = link2.getString(Keys.OBJECT_ID);
            final int oldLink1Order = link1.getInt(Link.LINK_ORDER);

            final JSONObject newLink2 =
                    new JSONObject(link2, JSONObject.getNames(link2));
            newLink2.put(Link.LINK_ORDER, oldLink1Order);
            final JSONObject newLink1 =
                    new JSONObject(link1, JSONObject.getNames(link1));
            newLink1.put(Link.LINK_ORDER, linkOrder);

            linkRepository.update(link2Id, newLink2);
            linkRepository.update(link1Id, newLink1);

            transaction.commit();

            return true;
        } catch (final Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            LOGGER.log(Level.SEVERE, e.getMessage(), e);

            return false;
        }
    }

    /**
     * Updates a link by the specified request json object.
     *
     * @param requestJSONObject the specified request json object, for example,
     * <pre>
     * {
     *     "link": {
     *         "oId": "",
     *         "linkTitle": "",
     *         "linkAddress": ""
     *     }
     * }, see {@link Link} for more details
     * </pre>
     * @param request the specified http servlet request
     * @param response the specified http servlet response
     * @return for example,
     * <pre>
     * {
     *     "sc": "UPDATE_LINK_SUCC"
     * }
     * </pre>
     * @throws ActionException action exception
     * @throws IOException io exception
     */
    public JSONObject updateLink(final JSONObject requestJSONObject,
                                 final HttpServletRequest request,
                                 final HttpServletResponse response)
            throws ActionException, IOException {
        final JSONObject ret = new JSONObject();
        if (!userUtils.isAdminLoggedIn()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return ret;
        }
        final Transaction transaction = linkRepository.beginTransaction();

        try {
            final JSONObject link =
                    requestJSONObject.getJSONObject(Link.LINK);
            final String linkId = link.getString(Keys.OBJECT_ID);
            final JSONObject oldLink = linkRepository.get(linkId);
            link.put(Link.LINK_ORDER, oldLink.getInt(Link.LINK_ORDER));

            linkRepository.update(linkId, link);

            transaction.commit();
            ret.put(Keys.STATUS_CODE, StatusCodes.UPDATE_LINK_SUCC);

            LOGGER.log(Level.FINER, "Updated a link[oId={0}]",
                       linkId);
        } catch (final Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            LOGGER.log(Level.SEVERE, e.getMessage(), e);

            try {
                ret.put(Keys.STATUS_CODE, StatusCodes.UPDATE_LINK_FAIL_);
            } catch (final JSONException ex) {
                LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
                throw new ActionException(ex);
            }
        }

        return ret;
    }

    /**
     * Removes a link by the specified request json object.
     *
     * @param requestJSONObject the specified request json object, for example,
     * <pre>
     * {
     *     "oId": "",
     * }
     * </pre>
     * @param request the specified http servlet request
     * @param response the specified http servlet response
     * @return for example,
     * <pre>
     * {
     *     "sc": "REMOVE_LINK_SUCC"
     * }
     * </pre>
     * @throws ActionException action exception
     * @throws IOException io exception
     */
    public JSONObject removeLink(final JSONObject requestJSONObject,
                                 final HttpServletRequest request,
                                 final HttpServletResponse response)
            throws ActionException, IOException {
        final JSONObject ret = new JSONObject();
        if (!userUtils.isAdminLoggedIn()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return ret;
        }
        final Transaction transaction = linkRepository.beginTransaction();

        try {
            final String linkId = requestJSONObject.getString(Keys.OBJECT_ID);
            LOGGER.log(Level.FINER, "Removing a link[oId={0}]", linkId);
            linkRepository.remove(linkId);

            transaction.commit();
            ret.put(Keys.STATUS_CODE, StatusCodes.REMOVE_LINK_SUCC);

            LOGGER.log(Level.FINER, "Removed a link[oId={0}]", linkId);
        } catch (final Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            LOGGER.log(Level.SEVERE, e.getMessage(), e);

            try {
                ret.put(Keys.STATUS_CODE, StatusCodes.REMOVE_LINK_FAIL_);
            } catch (final JSONException ex) {
                LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
                throw new ActionException(ex);
            }
        }

        return ret;
    }

    /**
     * Adds a link with the specified request json object.
     * 
     * @param requestJSONObject the specified request json object, for example,
     * <pre>
     * {
     *     "link": {
     *         "linkTitle": "",
     *         "linkAddress": ""
     *     }
     * }, see {@link Link} for more details
     * </pre>
     * @param request the specified http servlet request
     * @param response the specified http servlet response
     * @return for example,
     * <pre>
     * {
     *     "oId": generatedLinkId,
     *     "sc": ADD_LINK_SUCC
     * }
     * </pre>
     * @throws ActionException action exception
     * @throws IOException io exception
     */
    public JSONObject addLink(final JSONObject requestJSONObject,
                              final HttpServletRequest request,
                              final HttpServletResponse response)
            throws ActionException, IOException {
        final JSONObject ret = new JSONObject();
        if (!userUtils.isAdminLoggedIn()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return ret;
        }
        final Transaction transaction = linkRepository.beginTransaction();

        try {
            final JSONObject link =
                    requestJSONObject.getJSONObject(Link.LINK);
            final int maxOrder = linkRepository.getMaxOrder();
            link.put(Link.LINK_ORDER, maxOrder + 1);
            final String linkId = linkRepository.add(link);

            transaction.commit();
            ret.put(Keys.OBJECT_ID, linkId);

            ret.put(Keys.STATUS_CODE, StatusCodes.ADD_LINK_SUCC);
        } catch (final Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new ActionException(e);
        }

        return ret;
    }

    /**
     * Gets the {@link LinkService} singleton.
     *
     * @return the singleton
     */
    public static LinkService getInstance() {
        return SingletonHolder.SINGLETON;
    }

    /**
     * Private default constructor.
     */
    private LinkService() {
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
        private static final LinkService SINGLETON = new LinkService();

        /**
         * Private default constructor.
         */
        private SingletonHolder() {
        }
    }
}
