/*
 * Copyright (C) 2009, 2010, B3log Team
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
package org.b3log.solo.client.remote.impl;

import com.google.inject.Inject;
import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.client.action.ActionException;
import org.b3log.latke.client.action.util.Paginator;
import org.b3log.latke.client.remote.AbstractRemoteService;
import org.b3log.latke.model.Pagination;
import org.b3log.solo.client.StatusCodes;
import org.b3log.solo.model.Link;
import org.b3log.solo.repository.LinkRepository;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Link service for JavaScript client.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Aug 14, 2010
 */
public final class LinkService extends AbstractRemoteService {

    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(LinkService.class);
    /**
     * Link repository.
     */
    @Inject
    private LinkRepository linkRepository;

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
     *     "oId": "",
     *     "linkTitle": "",
     *     "linkAddress": ""
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

            LOGGER.debug("Got an link[oId=" + linkId + "]");
        } catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
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
     * @see LINK
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

            final JSONObject result =
                    linkRepository.get(currentPageNum, pageSize);
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
            LOGGER.error(e.getMessage(), e);
            throw new ActionException(e);
        }

        return ret;
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
        checkAuthorized(request, response);

        final JSONObject ret = new JSONObject();

        try {
            final JSONObject link =
                    requestJSONObject.getJSONObject(Link.LINK);
            final String linkId = link.getString(Keys.OBJECT_ID);
            linkRepository.update(linkId, link);

            ret.put(Keys.STATUS_CODE, StatusCodes.UPDATE_LINK_SUCC);

            LOGGER.debug("Updated an link[oId=" + linkId + "]");
        } catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new ActionException(e);
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
        checkAuthorized(request, response);

        final JSONObject ret = new JSONObject();

        try {
            final String linkId = requestJSONObject.getString(Keys.OBJECT_ID);
            LOGGER.debug("Removing a link[oId=" + linkId + "]");
            linkRepository.remove(linkId);

            ret.put(Keys.STATUS_CODE, StatusCodes.REMOVE_LINK_SUCC);

            LOGGER.debug("Removed a link[oId=" + linkId + "]");
        } catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new ActionException(e);
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
        checkAuthorized(request, response);

        final JSONObject ret = new JSONObject();

        try {
            final JSONObject link =
                    requestJSONObject.getJSONObject(Link.LINK);
            final String articleId = linkRepository.add(link);
            ret.put(Keys.OBJECT_ID, articleId);

            ret.put(Keys.STATUS_CODE, StatusCodes.ADD_LINK_SUCC);


        } catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new ActionException(e);
        }

        return ret;
    }
}
