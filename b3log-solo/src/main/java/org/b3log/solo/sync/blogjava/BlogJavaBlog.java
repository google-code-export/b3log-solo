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
package org.b3log.solo.sync.blogjava;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.b3log.solo.sync.AbstractMetaWeblog;
import org.b3log.solo.sync.util.PageReader;

/**
 * BlogJava blog.
 *
 * <p>
 *   API address: http://www.blogjava.net/<b>userId</b>/services/metaweblog.aspx
 * </p>
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Sep 6, 2010
 */
public final class BlogJavaBlog extends AbstractMetaWeblog {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(BlogJavaBlog.class.getName());

    @Override
    public String getApiAddress() {
        return "http://www.blogjava.net/" + getUserName()
               + "/services/metaweblog.aspx";
    }

    @Override
    public String getBloggingServiceProvider() {
        return "BlogJava";
    }

    @Override
    public List<String> getArchiveDates() {
        final List<String> ret = new ArrayList<String>();

        final String pageContent = PageReader.getContent(getIndexPageURL());
        final String patternString = "http://www.blogjava.net" + getUserName()
                                     + "/archive/\\d{4}/\\d{2}.html";
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

    @Override
    public List<String> getArticleIdsByArchiveDate(final String archiveDate) {
        final URL archivePageURL = getArchivePageURL(archiveDate);
        final String pageContent = PageReader.getContent(archivePageURL);
        final String patternString = "http://www.blogjava.net" + getUserName()
                                     + "/archive/\\d{4}/\\d{2}/\\d{2}/\\d+.html";
        final Pattern pattern =
                Pattern.compile(patternString);
        final Matcher matcher = pattern.matcher(pageContent);

        final Set<String> ids = new HashSet<String>();
        while (matcher.find()) {
            final String match = matcher.group();
            final int idx1 = match.lastIndexOf("/") + 1;
            final int idx2 = match.lastIndexOf(".");
            final String id = match.substring(idx1, idx2);

            ids.add(id);
        }

        return new ArrayList<String>(ids);
    }

    @Override
    public URL getArchivePageURL(final String archiveDate) {
        try {
            return new URL("http://www.cnblogs.com/" + getUserName()
                           + "/archive/" + archiveDate + ".aspx");
        } catch (final MalformedURLException e) {
            LOGGER.severe(e.getMessage());
            return null;
        }
    }

    @Override
    public URL getIndexPageURL() {
        try {
            return new URL("http://www.cnblogs.com/" + getUserName());
        } catch (final MalformedURLException e) {
            LOGGER.severe(e.getMessage());
            return null;
        }
    }
}
