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
package org.b3log.solo.filter;

import java.util.logging.Logger;

/**
 * Skips for request filtering.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.1.0.3, Nov 5, 2011
 * @see #shouldSkip(java.lang.String) 
 * @since 0.3.1
 */
public final class Skips {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(Skips.class.getName());

    /**
     * Determines whether the specified request URI points to a static resource.
     * 
     * @param requestURI the specified request URI
     * @return {@code true} if the specified request URI points to a static 
     * resource, returns {@code false} otherwise
     */
    public static boolean isStatic(final String requestURI) {
        return requestURI.endsWith(".css") || requestURI.endsWith(".js")
               || requestURI.endsWith("png"); // TODO: todo static postfix, see HTTPRequestDispatcher#service
    }

    /**
     * Private default constructor.
     */
    private Skips() {
    }
}
