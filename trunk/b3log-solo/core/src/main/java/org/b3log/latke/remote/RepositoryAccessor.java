/*
 * Copyright (c) 2009, 2010, 2011, 2012, B3log Team
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
package org.b3log.latke.remote;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.latke.Keys;
import org.b3log.latke.annotation.RequestProcessing;
import org.b3log.latke.annotation.RequestProcessor;
import org.b3log.latke.model.Pagination;
import org.b3log.latke.model.User;
import org.b3log.latke.repository.Query;
import org.b3log.latke.repository.Repositories;
import org.b3log.latke.repository.Repository;
import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.HTTPRequestMethod;
import org.b3log.latke.servlet.renderer.JSONRenderer;
import org.b3log.latke.util.Strings;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Accesses repository via HTTP protocol.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Apr 8, 2012
 */
// TODO: 88250, moves this class into Latke
@RequestProcessor
public final class RepositoryAccessor {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(RepositoryAccessor.class.getName());

    /**
     * Gets repository data.
     * 
     * <p>
     * Query parameters:
     * /latke/remote/getData?<em>userName=xxx&password=xxx&repositoryName=xxx&pageNum=xxx&pageSize=xxx</em><br/>
     * All parameters are required.
     * </p>
     * 
     * @param context the specified HTTP request context
     * @param request the specified HTTP servlet request
     * @param response the specified HTTP servlet response 
     */
    @RequestProcessing(value = "/latke/remote/getData", method = HTTPRequestMethod.GET)
    public void getData(final HTTPRequestContext context, final HttpServletRequest request, final HttpServletResponse response) {
        final JSONRenderer renderer = new JSONRenderer();
        context.setRenderer(renderer);

        final JSONObject jsonObject = new JSONObject();
        renderer.setJSONObject(jsonObject);

        jsonObject.put(Keys.STATUS_CODE, HttpServletResponse.SC_OK);
        jsonObject.put(Keys.MSG, "Got data");

        if (badRequest(request, jsonObject) || !authSucc(request, jsonObject)) {
            return;
        }

        final String repositoryName = request.getParameter("repositoryName");
        final Repository repository = Repositories.getRepository(repositoryName);
        if (null == repository) {
            jsonObject.put(Keys.STATUS_CODE, HttpServletResponse.SC_BAD_REQUEST);
            jsonObject.put(Keys.MSG, "Not found repository[name=" + repositoryName + "]");

            return;
        }

        final boolean cacheEnabled = repository.isCacheEnabled();

        repository.setCacheEnabled(false);

        final Query query = new Query().setCurrentPageNum(Integer.valueOf(request.getParameter("pageNum"))).
                setPageSize(Integer.valueOf(request.getParameter("pageSize")));

        try {
            final JSONObject result = repository.get(query);
            final JSONObject pagination = result.getJSONObject(Pagination.PAGINATION);
            final JSONArray data = result.getJSONArray(Keys.RESULTS);

            jsonObject.put(Pagination.PAGINATION, pagination);
            jsonObject.put(Keys.RESULTS, data);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, "Gets data failed", e);

            jsonObject.put(Keys.STATUS_CODE, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonObject.put(Keys.MSG, "Gets data failed[errorMsg=" + e.getMessage() + "]");
        } finally {
            repository.setCacheEnabled(cacheEnabled);
        }
    }

    /**
     * Determines whether the specified request is authenticated.
     * 
     * <p>
     * If the specified request is unauthenticated, puts {@link Keys#STATUS_CODE sc} and {@link Keys#MSG msg}
     * into the specified json object to render.
     * </p>
     * 
     * @param request the specified request
     * @param jsonObject the specified json object
     * @return {@code true} if authenticated, returns {@code false} otherwise
     */
    public boolean authSucc(final HttpServletRequest request, final JSONObject jsonObject) {
        final String userName = request.getParameter("userName");
        final String password = request.getParameter("password");

        final Repository repository = Repositories.getRepository(User.USER);
        if (null == repository) {
            jsonObject.put(Keys.STATUS_CODE, HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            jsonObject.put(Keys.MSG, "Not found user repository, please init your application first");

            return false;
        }

        try {
            final JSONObject result = repository.get(new Query());
            final JSONArray users = result.getJSONArray(Keys.RESULTS);

            for (int i = 0; i < users.length(); i++) {
                final JSONObject user = users.getJSONObject(i);
                if (userName.equals(user.getString(User.USER_NAME)) && password.equals(user.getString(User.USER_PASSWORD))) {
                    return true;
                }
            }

            jsonObject.put(Keys.STATUS_CODE, HttpServletResponse.SC_FORBIDDEN);
            jsonObject.put(Keys.MSG, "Auth failed[userName=" + userName + ", password=" + password + "]");
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            jsonObject.put(Keys.STATUS_CODE, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonObject.put(Keys.MSG, "Can not auth request[errorMsg=" + e.getMessage() + "]");
        }

        return false;
    }

    /**
     * Determines whether the specified request is bad.
     * 
     * <p>
     * If the specified request is bad, puts {@link Keys#STATUS_CODE sc} and {@link Keys#MSG msg}
     * into the specified json object to render.
     * </p>
     * 
     * @param request the specified request
     * @param jsonObject the specified jsonObject
     * @return {@code true} if it is bad, returns {@code false} otherwise
     */
    public boolean badRequest(final HttpServletRequest request, final JSONObject jsonObject) {
        final String userName = request.getParameter("userName");
        final String password = request.getParameter("password");
        final String repositoryName = request.getParameter("repositoryName");
        final String pageNumString = request.getParameter("pageNum");
        final String pageSizeString = request.getParameter("pageSize");

        if (Strings.isEmptyOrNull(userName)) {
            jsonObject.put(Keys.STATUS_CODE, HttpServletResponse.SC_BAD_REQUEST);
            jsonObject.put(Keys.MSG, "Requires parameter[userName]");
            return true;
        }

        if (Strings.isEmptyOrNull(password)) {
            jsonObject.put(Keys.STATUS_CODE, HttpServletResponse.SC_BAD_REQUEST);
            jsonObject.put(Keys.MSG, "Requires parameter[password]");
            return true;
        }

        if (Strings.isEmptyOrNull(repositoryName)) {
            jsonObject.put(Keys.STATUS_CODE, HttpServletResponse.SC_BAD_REQUEST);
            jsonObject.put(Keys.MSG, "Requires parameter[repositoryName]");
            return true;
        }

        if (Strings.isEmptyOrNull(pageNumString)) {
            jsonObject.put(Keys.STATUS_CODE, HttpServletResponse.SC_BAD_REQUEST);
            jsonObject.put(Keys.MSG, "Requires parameter[pageNum]");
            return true;
        }

        if (Strings.isEmptyOrNull(pageSizeString)) {
            jsonObject.put(Keys.STATUS_CODE, HttpServletResponse.SC_BAD_REQUEST);
            jsonObject.put(Keys.MSG, "Requires parameter[pageSize]");
            return true;
        }

        try {
            Integer.parseInt(pageNumString);
        } catch (final Exception e) {
            jsonObject.put(Keys.STATUS_CODE, HttpServletResponse.SC_BAD_REQUEST);
            jsonObject.put(Keys.MSG, "Parameter[pageNum] must be a integer");
            return true;
        }

        try {
            Integer.parseInt(pageSizeString);
        } catch (final Exception e) {
            jsonObject.put(Keys.STATUS_CODE, HttpServletResponse.SC_BAD_REQUEST);
            jsonObject.put(Keys.MSG, "Parameter[pageSize] must be a integer");
            return true;
        }

        return false;
    }
}
