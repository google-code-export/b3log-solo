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
import org.b3log.latke.util.Strings;
import org.b3log.solo.web.action.StatusCodes;
import org.b3log.solo.jsonrpc.AbstractGAEJSONRpcService;
import org.b3log.solo.model.Page;
import org.b3log.solo.repository.PageRepository;
import org.b3log.solo.repository.impl.PageRepositoryImpl;
import org.b3log.solo.util.Pages;
import org.b3log.solo.util.Permalinks;
import org.b3log.solo.util.Users;
import org.json.JSONObject;

/**
 * Page service for JavaScript client.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.1.0.0, Sep 28, 2011
 * @since 0.3.1
 */
public final class PageService extends AbstractGAEJSONRpcService {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(PageService.class.getName());
    /**
     * Page repository.
     */
    private PageRepository pageRepository = PageRepositoryImpl.getInstance();
    /**
     * Page utilities.
     */
    private Pages pageUtils = Pages.getInstance();
    /**
     * Permalink utilities.
     */
    private Permalinks permalinks = Permalinks.getInstance();
    /**
     * User utilities.
     */
    private Users userUtils = Users.getInstance();

    /**
     * Updates a page by the specified request json object.
     *
     * @param requestJSONObject the specified request json object, for example,
     * <pre>
     * {
     *     "page": {
     *         "oId": "",
     *         "pageTitle": "",
     *         "pageContent": "",
     *         "pageOrder": int,
     *         "pageCommentCount": int,
     *         "pagePermalink": ""
     *     }
     * }, see {@link Page} for more details
     * </pre>
     * @param request the specified http servlet request
     * @param response the specified http servlet response
     * @return for example,
     * <pre>
     * {
     *     "sc": "UPDATE_PAGE_SUCC"
     * }
     * </pre>
     * @throws ActionException action exception
     * @throws IOException io exception
     */
    public JSONObject updatePage(final JSONObject requestJSONObject,
                                 final HttpServletRequest request,
                                 final HttpServletResponse response)
            throws ActionException, IOException {
        final JSONObject ret = new JSONObject();
        if (!userUtils.isAdminLoggedIn(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return ret;
        }

        final Transaction transaction = pageRepository.beginTransaction();
        try {
            final JSONObject page =
                    requestJSONObject.getJSONObject(Page.PAGE);
            final String pageId = page.getString(Keys.OBJECT_ID);
            final JSONObject oldPage = pageRepository.get(pageId);
            final JSONObject newPage =
                    new JSONObject(page, JSONObject.getNames(page));
            newPage.put(Page.PAGE_ORDER, oldPage.getInt(Page.PAGE_ORDER));
            newPage.put(Page.PAGE_COMMENT_COUNT,
                        oldPage.getInt(Page.PAGE_COMMENT_COUNT));
            String permalink = page.optString(Page.PAGE_PERMALINK).trim();

            final String oldPermalink = oldPage.getString(Page.PAGE_PERMALINK);
            if (!oldPermalink.equals(permalink)) {
                if (Strings.isEmptyOrNull(permalink)) {
                    permalink = "/pages/" + pageId + ".html";
                }

                if (!permalink.startsWith("/")) {
                    permalink = "/" + permalink;
                }

                if (permalinks.invalidPagePermalinkFormat(permalink)) {
                    ret.put(Keys.STATUS_CODE,
                            StatusCodes.UPDATE_PAGE_FAIL_INVALID_PERMALINK_FORMAT);

                    throw new Exception("Update page fail, caused by invalid permalink format["
                                        + ret + "]");
                }

                if (!oldPermalink.equals(permalink)
                    && permalinks.exist(permalink)) {
                    ret.put(Keys.STATUS_CODE,
                            StatusCodes.UPDATE_PAGE_FAIL_DUPLICATED_PERMALINK);

                    throw new Exception("Update page fail, caused by duplicated permalink["
                                        + permalink + "]");
                }
            }
            newPage.put(Page.PAGE_PERMALINK, permalink);

            pageRepository.update(pageId, newPage);

            transaction.commit();
            ret.put(Keys.STATUS_CODE, StatusCodes.UPDATE_PAGE_SUCC);

            LOGGER.log(Level.FINER, "Updated a page[oId={0}]", pageId);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            if (transaction.isActive()) {
                transaction.rollback();
            }

            return ret;
        }

        return ret;
    }

    /**
     * Removes a page by the specified request json object.
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
     *     "sc": "REMOVE_PAGE_SUCC"
     * }
     * </pre>
     * @throws ActionException action exception
     * @throws IOException io exception
     */
    public JSONObject removePage(final JSONObject requestJSONObject,
                                 final HttpServletRequest request,
                                 final HttpServletResponse response)
            throws ActionException, IOException {
        final JSONObject ret = new JSONObject();
        if (!userUtils.isAdminLoggedIn(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return ret;
        }

        final Transaction transaction = pageRepository.beginTransaction();
        try {
            final String pageId = requestJSONObject.getString(Keys.OBJECT_ID);
            LOGGER.log(Level.FINER, "Removing a page[oId={0}]", pageId);
            pageUtils.removePageComments(pageId);
            pageRepository.remove(pageId);

            transaction.commit();
            ret.put(Keys.STATUS_CODE, StatusCodes.REMOVE_PAGE_SUCC);

            LOGGER.log(Level.FINER, "Removed a page[oId={0}]", pageId);
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
     * Gets the {@link PageService} singleton.
     *
     * @return the singleton
     */
    public static PageService getInstance() {
        return SingletonHolder.SINGLETON;
    }

    /**
     * Private default constructor.
     */
    private PageService() {
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
        private static final PageService SINGLETON = new PageService();

        /**
         * Private default constructor.
         */
        private SingletonHolder() {
        }
    }
}
