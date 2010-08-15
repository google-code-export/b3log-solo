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

import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

/**
 * Exporter.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Aug 15, 2010
 */
public final class App {

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
     * Article id.
     */
    private static final String ARTICLE_ID = "5749003";
    /**
     * User id.
     */
    private static final String USER_ID = "DL88250";

    /**
     * Main.
     *
     * @param args args
     */
    public static void main(final String[] args) {
        for (int year = 2006; year < 2011; year++) {
            for (int month = 1; month < 13; month++) {
                String archiveDate = year + "/";
                if (month < 10) {
                    archiveDate += "0" + month;
                } else {
                    archiveDate += month;
                }

                System.out.println("Getting article ids[archiveDate="
                        + archiveDate + "]");

                final List<String> articleIds = getArticleIdsByArchive(
                        archiveDate);

                for (final String articleId : articleIds) {
                    saveArticleById(articleId);
                }
            }
        }
    }

    /**
     * Gets an article by the specified article id.
     *
     * @param articleId the specified article id
     */
    private static void saveArticleById(final String articleId) {
        final XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();

        try {
            config.setServerURL(new URL(URL));

            final XmlRpcClient client = new XmlRpcClient();
            client.setConfig(config);
            final List<String> params = new ArrayList<String>();
            params.add(articleId);
            params.add(USER_ID);
            @SuppressWarnings("unchecked")
            final Map<String, ?> result =
                    (Map<String, ?>) client.execute(METHOD, params);
            final PrintWriter out = new PrintWriter("/home/daniel/Desktop/archive/"
                    + articleId + ".html");
            out.println("Title: " + result.get("title") + "<br/>");
            final Object[] tags = (Object[]) result.get("categories");
            out.println("Tags: ");
            for (int i = 0; i < tags.length; i++) {
                final Object tag = tags[i];
                out.println(tag + ", ");
            }
            out.println("<br/>");

            out.println("Create Date: " + result.get("dateCreated") + "<br/>");
            final String des = new String(((String) result.get("description")).
                    getBytes());
            out.println(des.replaceAll("\\?", " "));
            out.close();
        } catch (final Exception e) {
            System.err.println("Export article[id=" + articleId + "] error[msg="
                    + e.toString() + "]");
        }
    }

    /**
     * Gets article ids by the specified archive date.
     *
     * @param archiveDate the specified archive date(yyyy/MM)
     * @return a list of article ids, returns an empty list if not found
     */
    private static List<String> getArticleIdsByArchive(final String archiveDate) {
        final ArchivePageReader archivePageReader =
                new ArchivePageReader(archiveDate);
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
    private App() {
    }
}
