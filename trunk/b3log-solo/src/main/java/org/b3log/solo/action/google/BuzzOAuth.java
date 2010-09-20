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
package org.b3log.solo.action.google;

import com.google.api.client.googleapis.GoogleTransport;
import com.google.api.client.googleapis.json.JsonCParser;
import com.google.api.client.http.HttpTransport;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.latke.util.Strings;
import org.b3log.solo.SoloServletListener;
import org.b3log.solo.google.auth.OAuths;
import org.b3log.solo.model.Preference;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Buzz OAuth.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Sep 20, 2010
 */
public final class BuzzOAuth extends HttpServlet {

    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(BuzzOAuth.class.getName());
    /**
     * Sleep interval in milliseconds.
     */
    public static final long SLEEP_INTERVAL = 3000;
    /**
     * Http transport.
     */
    private static HttpTransport httpTransport;
    /**
     * Buzz scope.
     */
    public static final String BUZZ_SCOPE =
            "https://www.googleapis.com/auth/buzz";

    /**
     * Gets http transport.
     *
     * @return http transport
     */
    public static HttpTransport getHttpTransport() {
        return httpTransport;
    }

    @Override
    protected void doGet(final HttpServletRequest request,
                         final HttpServletResponse response)
            throws ServletException, IOException {
        String googleOAuthConsumerSecret =
                request.getParameter(Preference.GOOGLE_OAUTH_CONSUMER_SECRET);
        if (Strings.isEmptyOrNull(googleOAuthConsumerSecret)) {
            final JSONObject preference =
                    SoloServletListener.getUserPreference();
            try {
                googleOAuthConsumerSecret = preference.getString(
                        Preference.GOOGLE_OAUTH_CONSUMER_SECRET);
            } catch (final JSONException e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
                throw new ServletException(e);
            }
        }

        httpTransport = GoogleTransport.create();
        httpTransport.addParser(new JsonCParser());
        final String buzzAuthorizationURL =
                OAuths.getBuzzAuthorizationURL(httpTransport,
                                               googleOAuthConsumerSecret);

        response.sendRedirect(buzzAuthorizationURL);
    }
}
