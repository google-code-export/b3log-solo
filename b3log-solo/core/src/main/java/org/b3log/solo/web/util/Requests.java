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
import javax.servlet.http.HttpServletRequest;
import org.b3log.latke.model.Pagination;
import org.b3log.latke.util.Strings;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Request utilities.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.3, Nov 5, 2011
 * @see #PAGINATION_PATH_PATTERN
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
     * 
     * <p>
     * For example, the request URI is "xxx/1/2/3", so the specified path is 
     * "1/2/3". The first number represents "the current page number", the 
     * second number represents "the page size", and the third number represents 
     * "the window size", all of these for pagination.
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
     * Determines whether the specified request dose come from 
     * mobile device or not with its header ""User-Agent".
     * 
     * @param request the specified request
     * @return {@code true} if the specified request come from mobile device,
     * returns {@code false} otherwise
     */
    public static boolean mobileRequest(final HttpServletRequest request) {
        // TODO: dx, ;-)
        return false;
    }

    /**
     * Builds pagination request with the specified path.
     * 
     * @param path the specified path, see {@link #PAGINATION_PATH_PATTERN} 
     * for the details
     * @return pagination request json object, for example, 
     * <pre>
     * {
     *     "paginationCurrentPageNum": int,
     *     "paginationPageSize": int,
     *     "paginationWindowSize": int
     * }
     * </pre>
     * @see #PAGINATION_PATH_PATTERN
     */
    public static JSONObject buildPaginationRequest(final String path) {
        try {
            final Integer currentPageNum = Requests.getCurrentPageNum(path);
            final Integer pageSize = Requests.getPageSize(path);
            final Integer windowSize = Requests.getWindowSize(path);

            final JSONObject ret = new JSONObject();
            ret.put(Pagination.PAGINATION_CURRENT_PAGE_NUM,
                    currentPageNum);
            ret.put(Pagination.PAGINATION_PAGE_SIZE, pageSize);
            ret.put(Pagination.PAGINATION_WINDOW_SIZE, windowSize);

            return ret;
        } catch (final JSONException e) {
            // Should never
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets the request page number from the specified path.
     * 
     * @param path the specified path, see {@link #PAGINATION_PATH_PATTERN} 
     * for the details
     * @return page number, returns {@value #DEFAULT_CURRENT_PAGE_NUM} if the 
     * specified request URI can not convert to an number
     * @see #PAGINATION_PATH_PATTERN
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
     * @param path the specified path, see {@link #PAGINATION_PATH_PATTERN} 
     * for the details
     * @return page number, returns {@value #DEFAULT_PAGE_SIZE} if the 
     * specified request URI can not convert to an number
     * @see #PAGINATION_PATH_PATTERN
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
     * @param path the specified path, see {@link #PAGINATION_PATH_PATTERN} 
     * for the details
     * @return page number, returns {@value #DEFAULT_WINDOW_SIZE} if the 
     * specified request URI can not convert to an number
     * @see #PAGINATION_PATH_PATTERN
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
