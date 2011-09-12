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
package org.b3log.solo.web.action.util;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.latke.util.Strings;

/**
 * Request utilities.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Jul 9, 2011
 */
public final class Requests {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(Requests.class.getName());

    /**
     * Gets the request page number from the specified path.
     * 
     * @param path the specified path
     * @return page number, returns {@code -1} if the specified request URI
     * can not convert to an number
     */
    public static int getCurrentPageNum(final String path) {
        final String pageNumString = path.replaceAll("/", "");

        LOGGER.log(Level.FINEST, "Page number[string={0}]", pageNumString);

        if (Strings.isEmptyOrNull(pageNumString)) {
            return 1;
        }

        if (!Strings.isNumeric(pageNumString)) {
            return -1;
        }

        return Integer.valueOf(pageNumString);
    }

    /**
     * Private default constructor.
     */
    private Requests() {
    }
}
