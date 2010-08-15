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

package org.b3log.csdn.blog.exporter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Exporter.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Aug 15, 2010
 */
public final class ArchivePageReader {

    /**
     * Connection.
     */
    private URLConnection connection;
    /**
     * Archive date.
     */
    private String archiveDate;

    /**
     * Constructs a {@link ArchivePageReader} object. Connects the archive web
     * page with the specified archive date.
     *
     * @param archiveDate the specified archive date
     */
    public ArchivePageReader(final String archiveDate) {
        this.archiveDate = archiveDate;

        connect();
    }

    /**
     * Connects.
     */
    private void connect() {
        try {
            final URL url = new URL("http://blog.csdn.net/DL88250/archive/"
                    + archiveDate + ".aspx");
            connection = url.openConnection();
            connection.addRequestProperty("User-Agent",
                    "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.2.8) Gecko/20100723 Ubuntu/10.04 (lucid) Firefox/3.6.8 GTB7.1");


        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets web page content.
     *
     * @return content string
     */
    public String getContent() {
        BufferedReader in = null;

        final StringBuilder stringBuilder = new StringBuilder();
        try {
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));

            String line = in.readLine();
            while (null != line) {
                stringBuilder.append(line);
                line = in.readLine();
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }

        return stringBuilder.toString();
    }
}
