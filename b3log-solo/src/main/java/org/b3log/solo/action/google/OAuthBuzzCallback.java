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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * OAuth callback.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Sep 14, 2010
 */
public final class OAuthBuzzCallback extends HttpServlet {

    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(OAuthBuzzCallback.class.getName());
    /**
     * Verifiers.
     */
    private static final Map<String, String> VERIFIERS =
            new HashMap<String, String>();
    /**
     * Sleep interval in milliseconds.
     */
    public static final long SLEEP_INTERVAL = 3000;

    @Override
    protected void doGet(final HttpServletRequest request,
                         final HttpServletResponse response)
            throws ServletException, IOException {
        final String requestToken = request.getParameter("oauth_token");
        final String verifier = request.getParameter("oauth_verifier");
        LOGGER.log(Level.INFO,
                   "OAuth callback from Google[requestToken={0}, verifier={1}",
                   new String[]{requestToken, verifier});
        synchronized (VERIFIERS) {
            VERIFIERS.put(requestToken, verifier);
            VERIFIERS.notifyAll();
        }

        response.sendRedirect("/admin-index.do");
    }

    /**
     * Gets verifier by the specified request token.
     *
     * @param requestToken the specified request token
     * @param waitMillis amount of time we're willing to wait, it millisecond.
     * @return verifier
     */
    public static String getVerifier(final String requestToken,
                                     final long waitMillis) {
        final long startTime = System.currentTimeMillis();

        synchronized (VERIFIERS) {
            while (!VERIFIERS.containsKey(requestToken)) {
                try {
                    VERIFIERS.wait(SLEEP_INTERVAL);
                } catch (final InterruptedException e) {
                    LOGGER.log(Level.SEVERE, e.getMessage(), e);
                    return null;
                }

                if (waitMillis != -1 && System.currentTimeMillis()
                                        > startTime + waitMillis) {
                    LOGGER.log(Level.WARNING, "Timeout while waiting for token");
                    return null;
                }
            }

            return VERIFIERS.remove(requestToken);
        }
    }
}
