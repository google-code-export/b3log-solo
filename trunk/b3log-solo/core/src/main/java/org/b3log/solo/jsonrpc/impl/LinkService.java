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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.latke.Keys;
import org.b3log.latke.action.ActionException;
import org.b3log.latke.repository.Transaction;
import org.b3log.solo.web.action.StatusCodes;
import org.b3log.solo.jsonrpc.AbstractGAEJSONRpcService;
import org.b3log.solo.model.Link;
import org.b3log.solo.repository.LinkRepository;
import org.b3log.solo.repository.impl.LinkRepositoryImpl;
import org.b3log.solo.util.Users;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Link service for JavaScript client.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.1.0, Aug 21, 2011
 * @since 0.3.1
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
    private LinkRepository linkRepository = LinkRepositoryImpl.getInstance();
    /**
     * User utilities.
     */
    private Users userUtils = Users.getInstance();

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
        if (!userUtils.isAdminLoggedIn(request)) {
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
        if (!userUtils.isAdminLoggedIn(request)) {
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
