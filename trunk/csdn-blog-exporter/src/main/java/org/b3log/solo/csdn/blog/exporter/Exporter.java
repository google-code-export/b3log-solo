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
package org.b3log.solo.csdn.blog.exporter;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

/**
 * Exporter.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Aug 15, 2010
 */
public final class Exporter {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(Exporter.class);
    /**
     * Method.
     */
    private static final String METHOD = "metaWeblog.getPostByID";
    /**
     * URL.
     */
    private static final String URL =
            "http://blog.csdn.net/DL88250/services/metablogapi.aspx";
    /**
     * XML-RPC client configuration.
     */
    private XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
    /**
     * XML-RPC client.
     */
    private XmlRpcClient client = new XmlRpcClient();
    /**
     * Blogger.
     */
    private Blogger blogger;

    /**
     * Constructs a CSDN blog exporter for the specified blogger.
     *
     * @param blogger the specified blogger
     */
    public Exporter(final Blogger blogger) {
        this.blogger = blogger;

        try {
            config.setServerURL(new URL(URL));
            client.setConfig(config);
        } catch (final MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Exports articles.
     *
     * @return the exported articles, returns an empty set if not found
     * @throws Exception exception
     */
    public Set<Article> export() throws Exception {
        final Set<Article> ret = new HashSet<Article>();

        final int startYear = blogger.getArchiveStartYear();
        final int endYear = blogger.getArchiveEndYear();

        final int startMonth = blogger.getArchiveStartMonth();
        final int endMonth = blogger.getArchiveEndMonth();

        final int oct = 10;
        final int dec = 12;

        for (int year = startYear; year <= endYear; year++) {
            for (int month = (year == startYear) ? startMonth : 1;
                    month <= ((year == endYear) ? endMonth : dec);
                    month++) {
                String archiveDate = year + "/";

                if (month < oct) {
                    archiveDate += "0" + month;
                } else {
                    archiveDate += month;
                }

                LOGGER.info("Getting article ids[archiveDate="
                        + archiveDate + "]....");

                final List<String> articleIds = getArticleIdsByArchive(
                        archiveDate);

                LOGGER.info("Start to get artiches....");
                for (final String articleId : articleIds) {

                    try {
                        Thread.sleep(5000);
                    } catch (final InterruptedException e) {
                        LOGGER.error(e.getMessage(), e);
                    }

                    final Article article = getArticleById(articleId);

                    if (null != article) {
                        ret.add(article);
                    } else {
                        LOGGER.info("Can't export the article[id=" + articleId
                                + "] caused by an error");
                    }
                }
            }
        }

        return ret;
    }

    /**
     * Gets an article by the specified article id.
     *
     * @param articleId the specified article id
     * @return article, returns {@code null} if error or not found
     */
    public Article getArticleById(final String articleId) {
        final Article ret = new Article();

        try {
            final List<String> params = new ArrayList<String>();
            params.add(articleId);
            params.add(blogger.getUserId());
            @SuppressWarnings("unchecked")
            final Map<String, ?> result =
                    (Map<String, ?>) client.execute(METHOD, params);

            final String title = (String) result.get("title");
            ret.setTitle(title);

            final Object[] tagObjects = (Object[]) result.get("categories");
            for (int i = 0; i < tagObjects.length; i++) {
                final Object tag = tagObjects[i];
                ret.addTag(tag.toString());
            }

            final Date createDate = (Date) result.get("dateCreated");
            ret.setCreateDate(createDate);

            final String des = new String(((String) result.get("description")).
                    getBytes());
            final String content = des.replaceAll("\\?", " ");
            ret.setContent(content);

        } catch (final Exception e) {
            LOGGER.error("Export article[id=" + articleId + "] error[msg="
                    + e.toString() + "]");

            return null;
        }

        return ret;
    }

    /**
     * Gets article ids by the specified archive date.
     *
     * @param archiveDate the specified archive date(yyyy/MM)
     * @return a list of article ids, returns an empty list if not found
     */
    private List<String> getArticleIdsByArchive(final String archiveDate) {
        final ArchivePageReader archivePageReader =
                new ArchivePageReader(blogger.getUserId(), archiveDate);
        final String pageContent = archivePageReader.getContent();
        final String patternString =
                "<code><a href=\"/DL88250/archive/"
                + archiveDate + "/\\d\\d/\\d+\\.aspx";
        final Pattern pattern = Pattern.compile(patternString);
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

    /**
     * Private default constructor.
     */
    private Exporter() {
    }
}
