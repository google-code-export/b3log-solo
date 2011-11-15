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
import org.b3log.solo.util.Permalinks;

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
     * Determines whether the specified request URI should be skipped filter.
     *
     * <p>
     *   <b>Note</b>: This method SHOULD be invoked for all filters with pattern
     *   "/*".
     * </p>
     *
     * @param requestURI the specified request URI
     * @return {@code true} if should be skipped, returns {@code false} 
     * otherwise
     */
    // XXX: performance issue, super hard coding....
    // TODO: skips new urls....
    static boolean shouldSkip(final String requestURI) {
        return containsMoreThenOneSlash(requestURI)
               || isReservedLink(requestURI) || isStatic(requestURI);
    }

    /**
     * Determines whether the specified request URI contains more then one slash. 
     * 
     * @param requestURI the specified request URI
     * @return {@code true} if it contains more then one slash, returns 
     * {@code false} otherwise
     */
    private static boolean containsMoreThenOneSlash(final String requestURI) {
        int slashCnt = 0;
        for (int i = 0; i < requestURI.length(); i++) {
            if ('/' == requestURI.charAt(i)) {
                slashCnt++;
            }

            if (slashCnt > 1) {
                return true;
            }
        }

        return false;
    }

    /**
     * Determines whether the specified request URI is a reserved link.
     * 
     * <p>
     * A URI starts with one of {@link Permalinks#RESERVED_LINKS reserved links}
     * will be treated as reserved link.
     * </p>
     * 
     * @param requestURI the specified request URI
     * @return {@code true} if it is a reserved link, returns {@code false}
     * otherwise
     */
    public static boolean isReservedLink(final String requestURI) {
        for (int i = 0; i < Permalinks.RESERVED_LINKS.length; i++) {
            final String reservedLink = Permalinks.RESERVED_LINKS[i];
            if (reservedLink.startsWith(requestURI)) {
                return true;
            }
        }

        return false;
    }

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
