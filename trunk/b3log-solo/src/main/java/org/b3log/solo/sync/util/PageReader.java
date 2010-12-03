/*
 * Copyright (c) 2009, 2010, B3log Team
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

package org.b3log.solo.sync.util;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;

/**
 * Web page reader.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Dec 3, 2010
 */
public final class PageReader {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(PageReader.class.getName());

    /**
     * Gets the content of a web page specified by the given URL.
     *
     * @param url the given URL
     * @return content of a web page, returns {@code ""} if occurs error or
     * an empty web page
     */
    public static String getContent(final URL url) {
        try {
            final URLConnection connection = url.openConnection();
            connection.addRequestProperty(
                    "User-Agent",
                    "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.2.8) Gecko/20100723 Ubuntu/10.04 (lucid) Firefox/3.6.8 GTB7.1");

            final InputStream inputStream = connection.getInputStream();
            return IOUtils.toString(inputStream, "UTF-8");
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

        return "";
    }

    /**
     * Private default constructor.
     */
    private PageReader() {
    }
}
