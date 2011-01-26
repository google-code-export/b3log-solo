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

package org.b3log.solo.tencent.microblog.oauth;

import com.mime.qweibo.OauthKey;
import com.mime.qweibo.QWeiboSyncApi;
import com.mime.qweibo.QWeiboType.PageFlag;
import com.mime.qweibo.QWeiboType.ResultType;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.latke.repository.Transaction;
import org.b3log.solo.model.Preference;
import org.b3log.solo.repository.impl.PreferenceGAERepository;
import org.b3log.solo.util.Preferences;
import org.json.JSONObject;

/**
 * OAuth callback.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Jan 26, 2011
 */
public final class Callback extends HttpServlet {

    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(Callback.class.getName());
    /**
     * Preference utilities.
     */
    private Preferences preferenceUtils = Preferences.getInstance();

    @Override
    protected void doGet(final HttpServletRequest request,
                         final HttpServletResponse response)
            throws ServletException, IOException {
        LOGGER.fine("Tencent microblog OAuth allback....");

        final JSONObject preference = preferenceUtils.getPreference();
        String appKey = null;
        String appSecret = null;
        String tokenSecret = null;
        try {
            appKey = preference.getString(Preference.TENCENT_MICROBLOG_APP_KEY);
            appSecret = preference.getString(
                    Preference.TENCENT_MICROBLOG_APP_SECRET);
            tokenSecret = preference.getString(
                    Preference.TENCENT_MICROBLOG_TOKEN_SECRET);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);

            return;
        }

        final String tokenKey = request.getParameter("oauth_token");
        final String verifier = request.getParameter("oauth_verifier");

        final QWeiboSyncApi api = new QWeiboSyncApi();
        final String resp =
                api.getAccessToken(appKey, appSecret,
                                   tokenKey, tokenSecret,
                                   verifier);
        final OauthKey oauthKey = AuthorizeToken.parseToken(resp);

         final Transaction transaction =
                PreferenceGAERepository.getInstance().beginTransaction();
        try {
            preference.put(Preference.TENCENT_MICROBLOG_APP_KEY, appKey);
            preference.put(Preference.TENCENT_MICROBLOG_APP_SECRET, appSecret);
            preference.put(Preference.TENCENT_MICROBLOG_TOKEN_KEY,
                           oauthKey.tokenKey);
            preference.put(Preference.TENCENT_MICROBLOG_TOKEN_SECRET,
                           oauthKey.tokenSecret);

            preferenceUtils.setPreference(preference);
            transaction.commit();
        } catch (final Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            LOGGER.log(Level.SEVERE, e.getMessage(), e);

            return;
        }

        final String homeMsg = api.getHomeMsg(appKey, appSecret,
                                              oauthKey.tokenKey,
                                              oauthKey.tokenSecret,
                                              ResultType.ResultType_Json,
                                              PageFlag.PageFlag_First, 20);
        LOGGER.finer(homeMsg);

        response.sendRedirect("/admin-index.do");
    }
}
