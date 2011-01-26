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
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.util.Strings;
import org.b3log.solo.model.Preference;
import org.b3log.solo.repository.impl.PreferenceGAERepository;
import org.b3log.solo.util.Preferences;
import org.json.JSONObject;

/**
 * OAuth servlet.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Jan 26, 2010
 */
public final class AuthorizeToken extends HttpServlet {

    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(AuthorizeToken.class.getName());
    /**
     * Preference utilities.
     */
    private Preferences preferenceUtils = Preferences.getInstance();

    @Override
    protected void doGet(final HttpServletRequest request,
                         final HttpServletResponse response)
            throws ServletException, IOException {
        LOGGER.fine("GetRequestToken....");

        final String appKey = request.getParameter("appKey");
        final String appSecret = request.getParameter("appSecret");

        if (Strings.isEmptyOrNull(appKey)
            || Strings.isEmptyOrNull(appSecret)) {
            LOGGER.info("App key or secret empty!");

            return;
        }

        final JSONObject preference = preferenceUtils.getPreference();
        final String blogHost = preference.optString(Preference.BLOG_HOST);

        final QWeiboSyncApi api = new QWeiboSyncApi();
        final String callbackURL =
                "http://" + blogHost + "/tencent-microblog-oauth-callback.do";
        final String resp = api.getRequestToken(appKey, appSecret,
                                                callbackURL);

        LOGGER.log(Level.FINE, "Response[{0}]", resp);

        final OauthKey oauthKey = parseToken(resp);
        if (null == oauthKey) {
            return;
        }

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

        response.sendRedirect("http://open.t.qq.com/cgi-bin/authorize?oauth_token="
                              + oauthKey.tokenKey);
    }

    /**
     * Parses token and secret with the specified response.
     *
     * @param response the specified response
     * @return oauth key, returns {@code null} if parse fail
     */
    static OauthKey parseToken(final String response) {
        if (response == null || response.equals("")) {
            return null;
        }

        final String[] tokenArray = response.split("&");

        if (tokenArray.length < 2) {
            return null;
        }

        final String strTokenKey = tokenArray[0];
        final String strTokenSecret = tokenArray[1];

        final String[] token1 = strTokenKey.split("=");
        if (token1.length < 2) {
            return null;
        }

        final OauthKey ret = new OauthKey();
        ret.tokenKey = token1[1];

        final String[] token2 = strTokenSecret.split("=");
        if (token2.length < 2) {
            return null;
        }
        ret.tokenSecret = token2[1];

        LOGGER.log(Level.FINER, "Token[key={0}, secrect={1}]",
                   new Object[]{ret.tokenKey, ret.tokenSecret});

        return ret;
    }
}
