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
package org.b3log.solo.sync.csdn.blog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import org.apache.log4j.Logger;

/**
 * Archive page reader.
 *
 * <p>
 * http://blog.csdn.net/<b>userId</b>/archive/<b>yyyy/MM</b>.aspx
 * </p>
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Aug 16, 2010
 */
final class ArchivePageReader {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(ArchivePageReader.class);
    /**
     * Connection.
     */
    private URLConnection connection;
    /**
     * Archive date.
     */
    private String archiveDate;
    /**
     * User id.
     */
    private String userId;

    /**
     * Constructs a {@link ArchivePageReader} object. Connects the archive web
     * page with the specified user id and archive date.
     *
     * @param userId the specified user id
     * @param archiveDate the specified archive date
     */
    ArchivePageReader(final String userId, final String archiveDate) {
        this.archiveDate = archiveDate;
        this.userId = userId;

        connect();
    }

    /**
     * Connects.
     */
    private void connect() {
        try {
            final URL url = new URL("http://blog.csdn.net/" + userId
                    + "/archive/"
                    + archiveDate + ".aspx");
            connection = url.openConnection();
            connection.addRequestProperty(
                    "User-Agent",
                    "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.2.8) Gecko/20100723 Ubuntu/10.04 (lucid) Firefox/3.6.8 GTB7.1");
        } catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * Gets web page content.
     *
     * @return content string
     */
    String getContent() {
        BufferedReader bufferedReader = null;

        final StringBuilder stringBuilder = new StringBuilder();
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));

            String line = bufferedReader.readLine();
            while (null != line) {
                stringBuilder.append(line);
                line = bufferedReader.readLine();
            }
        } catch (final IOException e) {
            LOGGER.error(e.getMessage(), e);
        }

        return stringBuilder.toString();
    }
}
