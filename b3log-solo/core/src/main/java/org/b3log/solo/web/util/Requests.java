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
package org.b3log.solo.web.util;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.latke.util.Strings;

/**
 * Request utilities.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Oct 24, 2011
 */
// TODO: 88250, moves the class into Latke
public final class Requests {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(Requests.class.getName());
    /**
     * The pagination path pattern.
     * 
     * <p>
     * The first star represents "the current page number", the 
     * second star represents "the page size", and the third star represents 
     * "the window size". Argument of each of these stars should be a number.
     * </p>
     */
    public static final String PAGINATION_PATH_PATTERN = "*/*/*";
    /**
     * Default current page number.
     */
    private static final int DEFAULT_CURRENT_PAGE_NUM = 1;
    /**
     * Default page size.
     */
    private static final int DEFAULT_PAGE_SIZE = 15;
    /**
     * Default window size.
     */
    private static final int DEFAULT_WINDOW_SIZE = 20;

    /**
     * Gets the request page number from the specified path.
     * 
     * <p>
     * For example, the request URI is "xxx/1/2/3", so the specified path is 
     * "1/2/3". The first number represents "the current page number", the 
     * second number represents "the page size", and the third number represents 
     * "the window size", all of these for pagination.
     * </p>
     * 
     * @param path the specified path
     * @return page number, returns {@value #DEFAULT_CURRENT_PAGE_NUM} if the 
     * specified request URI can not convert to an number
     */
    public static int getCurrentPageNum(final String path) {
        LOGGER.log(Level.FINEST, "Page number[string={0}]", path);

        if (Strings.isEmptyOrNull(path)) {
            return DEFAULT_CURRENT_PAGE_NUM;
        }

        final String currentPageNumber = path.split("/")[0];

        if (!Strings.isNumeric(currentPageNumber)) {
            return DEFAULT_CURRENT_PAGE_NUM;
        }

        return Integer.valueOf(currentPageNumber);
    }

    /**
     * Gets the request page size from the specified path.
     * 
     * <p>
     * For example, the request URI is "xxx/1/2/3", so the specified path is 
     * "1/2/3". The first number represents "the current page number", the 
     * second number represents "the page size", and the third number represents 
     * "the window size", all of these for pagination.
     * </p>
     * 
     * @param path the specified path
     * @return page number, returns {@value #DEFAULT_PAGE_SIZE} if the 
     * specified request URI can not convert to an number
     */
    public static int getPageSize(final String path) {
        LOGGER.log(Level.FINEST, "Page number[string={0}]", path);

        if (Strings.isEmptyOrNull(path)) {
            return DEFAULT_PAGE_SIZE;
        }

        final String[] parts = path.split("/");
        if (1 >= parts.length) {
            return DEFAULT_PAGE_SIZE;
        }

        final String pageSize = parts[1];

        if (!Strings.isNumeric(pageSize)) {
            return DEFAULT_PAGE_SIZE;
        }

        return Integer.valueOf(pageSize);
    }

    /**
     * Gets the request window size from the specified path.
     * 
     * <p>
     * For example, the request URI is "xxx/1/2/3", so the specified path is 
     * "1/2/3". The first number represents "the current page number", the 
     * second number represents "the page size", and the third number represents 
     * "the window size", all of these for pagination.
     * </p>
     * 
     * @param path the specified path
     * @return page number, returns {@value #DEFAULT_WINDOW_SIZE} if the 
     * specified request URI can not convert to an number
     */
    public static int getWindowSize(final String path) {
        LOGGER.log(Level.FINEST, "Page number[string={0}]", path);

        if (Strings.isEmptyOrNull(path)) {
            return DEFAULT_WINDOW_SIZE;
        }

        final String[] parts = path.split("/");
        if (2 >= parts.length) {
            return DEFAULT_WINDOW_SIZE;
        }

        final String windowSize = parts[2];

        if (!Strings.isNumeric(windowSize)) {
            return DEFAULT_WINDOW_SIZE;
        }

        return Integer.valueOf(windowSize);
    }

    /**
     * Private default constructor.
     */
    private Requests() {
    }
}
