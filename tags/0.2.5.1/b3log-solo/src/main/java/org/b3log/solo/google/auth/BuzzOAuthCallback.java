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

package org.b3log.solo.google.auth;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.solo.model.Preference;
import org.b3log.solo.util.Preferences;
import org.json.JSONObject;

/**
 * OAuth callback.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.5, Jan 12, 2011
 */
public final class BuzzOAuthCallback extends HttpServlet {

    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(BuzzOAuthCallback.class.getName());
    /**
     * Sleep interval in milliseconds.
     */
    public static final long SLEEP_INTERVAL = 3000;
    /**
     * Preference utilities.
     */
    private Preferences preferenceUtils = Preferences.getInstance();
    /**
     * OAuth utilities.
     */
    private OAuths oAuths = OAuths.getInstance();
    /**
     * Key of Google Buzz token.
     */
    public static final String GOOGLE_BUZZ_TOKEN =
            "googleBuzzToken";
    /**
     * Key of Google Buzz verifier.
     */
    public static final String GOOGLE_BUZZ_VERIFIER =
            "googleBuzzVerifier";

    @Override
    protected void doGet(final HttpServletRequest request,
                         final HttpServletResponse response)
            throws ServletException, IOException {
        final String requestToken = request.getParameter("oauth_token");
        final String verifier = request.getParameter("oauth_verifier");
        LOGGER.log(Level.FINE,
                   "OAuth callback from Google[requestToken={0}, verifier={1}",
                   new String[]{requestToken, verifier});

//        try {
//            oAuths.sign(requestToken, verifier, BuzzOAuth.getHttpTransport());
//        } catch (final Exception e) {
//            LOGGER.log(Level.SEVERE, e.getMessage(), e);
//            throw new ServletException(e);
//        }

        try {
            final JSONObject preference = preferenceUtils.getPreference();
            preference.put(GOOGLE_BUZZ_TOKEN, requestToken);
            preference.put(GOOGLE_BUZZ_VERIFIER, verifier);

            final JSONObject requestJSONObject = new JSONObject();
            requestJSONObject.put(Preference.PREFERENCE, preference);

            preferenceUtils.setPreference(preference);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new ServletException(e);
        }

        response.sendRedirect("/admin-index.do");
    }
}
