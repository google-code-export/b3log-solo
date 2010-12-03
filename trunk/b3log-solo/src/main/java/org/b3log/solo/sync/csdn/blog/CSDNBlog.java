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

package org.b3log.solo.sync.csdn.blog;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.b3log.solo.sync.AbstractMetaWeblog;
import org.b3log.solo.sync.util.PageReader;

/**
 * CSDN blog.
 *
 * <p>
 *   API address: http://blog.csdn.net/<b>userId</b>/services/metablogapi.aspx
 * </p>
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.6, Dec 3, 2010
 */
public final class CSDNBlog extends AbstractMetaWeblog {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(CSDNBlog.class.getName());

    static {
        final TimeZone cstTimeZone = TimeZone.getTimeZone("CST");
        CST_DATE_FORMAT.setTimeZone(cstTimeZone);

        final TimeZone utcTimeZone = TimeZone.getTimeZone("UTC");
        UTC_DATE_FORMAT.setTimeZone(utcTimeZone);
    }

    @Override
    public String getApiAddress() {
        return "http://blog.csdn.net/" + getUserName()
               + "/services/metablogapi.aspx";
    }

    @Override
    public String getBloggingServiceProvider() {
        return "CSDN Blog";
    }

    @Override
    public List<String> getArchiveDates() {
        final List<String> ret = new ArrayList<String>();

        final String pageContent = PageReader.getContent(getIndexPageURL());
        final String patternString = "<a href=\"/" + getUserName()
                                     + "/archive/\\d{4}/\\d{2}\\.aspx";
        final Pattern pattern = Pattern.compile(patternString);
        final Matcher matcher = pattern.matcher(pageContent);

        final List<String> matches = new ArrayList<String>();
        while (matcher.find()) {
            final String match = matcher.group();
            matches.add(match);
        }

        final int yearLength = 4;
        final int monthLength = 2;
        for (final String match : matches) {
            final int idx1 = match.lastIndexOf("/"); // yyyy^/MM.aspx
            final int idx2 = idx1 - yearLength; // ^yyyy/MM
            final int idx3 = idx1 + 1; // yyyy/^MM
            final String year = match.substring(idx2, idx1);
            final String month = match.substring(idx3, idx3 + monthLength);

            ret.add(year + "/" + month);
        }

        return ret;
    }

    /**
     * Gets the oldest archive date.
     *
     * @return the oldest archive date(yyyy/MM), returns {@code null} if
     * occurs error or not found
     */
    public String getOldestArchiveDate() {
        final String pageContent = PageReader.getContent(getIndexPageURL());
        final String patternString = "<a href=\"/" + getUserName()
                                     + "/archive/\\d{4}/\\d{2}\\.aspx";
        final Pattern pattern = Pattern.compile(patternString);
        final Matcher matcher = pattern.matcher(pageContent);

        final List<String> matches = new ArrayList<String>();
        while (matcher.find()) {
            final String match = matcher.group();
            matches.add(match);
        }

        if (0 == matches.size()) {
            return null;
        }

        final String match = matches.get(matches.size() - 1);
        final int yearLength = 4;
        final int monthLength = 2;

        final int idx1 = match.lastIndexOf("/"); // yyyy^/MM.aspx
        final int idx2 = idx1 - yearLength; // ^yyyy/MM
        final int idx3 = idx1 + 1; // yyyy/^MM
        final String year = match.substring(idx2, idx1);
        final String month = match.substring(idx3, idx3 + monthLength);

        return year + "/" + month;
    }

    @Override
    public List<String> getArticleIdsByArchiveDate(final String archiveDate) {
        final URL archivePageURL = getArchivePageURL(archiveDate);
        final String pageContent = PageReader.getContent(archivePageURL);
        final String patternString =
                "<code><a href=\"/" + getUserName() + "/archive/"
                + archiveDate + "/\\d\\d/\\d+\\.aspx";
        final Pattern pattern =
                Pattern.compile(patternString);
        final Matcher matcher = pattern.matcher(pageContent);

        final List<String> ret = new ArrayList<String>();
        while (matcher.find()) {
            final String match = matcher.group();
            final int idx1 = match.lastIndexOf("/") + 1;
            final int idx2 = match.lastIndexOf(".");
            final String id = match.substring(idx1, idx2);

            ret.add(id);
        }

        return ret;
    }

    @Override
    public URL getArchivePageURL(final String archiveDate) {
        try {
            return new URL("http://blog.csdn.net/" + getUserName()
                           + "/archive/" + archiveDate + ".aspx");
        } catch (final MalformedURLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public URL getIndexPageURL() {
        try {
            return new URL("http://blog.csdn.net/" + getUserName());
        } catch (final MalformedURLException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return null;
        }
    }
}
