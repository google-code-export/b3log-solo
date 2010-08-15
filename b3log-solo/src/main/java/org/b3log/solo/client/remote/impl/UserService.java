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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.client.Sessions;
import org.b3log.latke.client.action.ActionException;
import org.b3log.latke.client.remote.AbstractRemoteService;
import org.b3log.latke.model.User;
import org.b3log.latke.util.MD5;
import org.b3log.solo.client.StatusCodes;
import org.b3log.solo.repository.UserRepository;
import org.json.JSONObject;

/**
 * User service for JavaScript client.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Aug 14, 2010
 */
public final class UserService extends AbstractRemoteService {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(UserService.class);
    /**
     * User repository.
     */
    @Inject
    private UserRepository userRepository;

    /**
     *
     * @param requestJSONObject the specified request json object, for example,
     * <pre>
     * {
     *     "userNewPassword": "",
     *     "userPassword": ""
     * }
     * </pre>
     * @param request the specified http servlet request
     * @param response the specified http servlet response
     * @return for example,
     * <pre>
     * {
     *     "sc": UPDATE_PASSWORD_SUCC
     * }
     * </pre>
     * @throws ActionException action exception
     * @throws IOException io exception
     */
    public JSONObject updatePassword(final JSONObject requestJSONObject,
                                     final HttpServletRequest request,
                                     final HttpServletResponse response)
            throws ActionException, IOException {
        checkAuthorized(request, response);

        final JSONObject ret = new JSONObject();
        try {
            final String newPwd =
                    requestJSONObject.getString(User.USER_NEW_PASSWORD);
            final String requestPwd =
                    requestJSONObject.getString(User.USER_PASSWORD);

            final String currentUserName = Sessions.currentUserName(request);
            String currentUserPwd = Sessions.currentUserPwd(request);

            if (MD5.hash(requestPwd).equals(currentUserPwd)) {
                currentUserPwd = MD5.hash(newPwd);
                userRepository.updateUserPassword(User.USER_NAME,
                                                  currentUserPwd);
                Sessions.login(request, currentUserName, currentUserPwd);

                ret.put(Keys.STATUS_CODE, StatusCodes.UPDATE_PASSWORD_SUCC);
            }
        } catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new ActionException(e);
        }

        return ret;
    }

    /**
     * Logouts.
     *
     * @param request the specified http servlet request
     * @return for example,
     * <pre>
     * {
     *     "sc": USER_LOGOUT_SUCC
     * }
     * </pre>
     * @throws ActionException action exception
     */
    public JSONObject logout(final HttpServletRequest request)
            throws ActionException {
        final JSONObject ret = new JSONObject();
        Sessions.logout(request);
        try {
            ret.put(Keys.STATUS_CODE, StatusCodes.USER_LOGOUT_SUCC);
        } catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new ActionException(e);
        }

        return ret;
    }

    /**
     * Logins.
     *
     * @param requestJSONObject the specified request json object, for example,
     * <pre>
     * {
     *     "userPassword": ""
     * }
     * </pre>
     * @param request the specified http servlet request
     * @return for example,
     * <pre>
     * {
     *     "sc": USER_LOGIN_SUCC
     * }
     * </pre>
     * @throws ActionException action exception
     */
    public JSONObject login(final JSONObject requestJSONObject,
                            final HttpServletRequest request)
            throws ActionException {
        final JSONObject ret = new JSONObject();

        try {
            final String loginPassword =
                    requestJSONObject.getString(User.USER_PASSWORD);

            final JSONObject user = userRepository.get(User.USER);
            final String userName = user.getString(User.USER_NAME);
            final String userPassword = user.getString(User.USER_PASSWORD);
            if (!MD5.hash(loginPassword).equals(userPassword)) {
                ret.put(Keys.STATUS_CODE, StatusCodes.USER_LOGIN_FAIL_);
            } else {
                ret.put(Keys.STATUS_CODE, StatusCodes.USER_LOGIN_SUCC);
                Sessions.login(request, userName, userPassword);
            }
        } catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new ActionException(e);
        }

        return ret;
    }
}
