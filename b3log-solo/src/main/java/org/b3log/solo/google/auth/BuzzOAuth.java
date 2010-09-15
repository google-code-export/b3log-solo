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
package org.b3log.solo.google.auth;

import java.util.logging.Logger;
import org.b3log.solo.servlet.SoloServletListener;
import org.json.JSONObject;

/**
 * Google oauth.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Sep 14, 2010
 */
public final class BuzzOAuth {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(BuzzOAuth.class.getName());
    /**
     * Callback URL.
     */
    private static final String CALLBACK_URL = "/oauth-callback.do";
    /**
     * Buzz scope.
     */
    private static final String BUZZ_SCOPE =
            "https://www.googleapis.com/auth/buzz";

    /**
     * Authorizes.
     */
    public static void authorize() {
        final JSONObject preference = SoloServletListener.getUserPreference();
    }

    /**
     * Private default constructor.
     */
    private BuzzOAuth() {
    }
}
