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

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.b3log.latke.service.ServiceException;

/**
 * CSDN blog.
 *
 * <p>
 * CSDN blog provides remote article(post, entry, article, whatever) management
 * via <a href="http://www.xmlrpc.com/metaWeblogApi">MetaWeblog</a>. The service
 * address is:http://blog.csdn.net/<b>userId</b>/services/metablogapi.aspx
 * </p>
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Aug 18, 2010
 */
public final class CSDNBlog {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(CSDNBlog.class);
    /**
     * New post method.
     */
    private static final String NEW_POST = "metaWeblog.newPost";
    /**
     * Delete post method.
     */
    private static final String DELETE_POST = "blogger.deletePost";
    /**
     * Get post by id method.
     */
    private static final String GET_POST_BY_ID = "metaWeblog.getPostByID";
    /**
     * XML-RPC client configuration.
     */
    private XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
    /**
     * XML-RPC client.
     */
    private XmlRpcClient client = new XmlRpcClient();
    /**
     * Sleep millisecond between every article get operation.
     */
    private static final long GET_ARTICLE_SLEEP_MILLIS = 3000;

    /**
     * Deletes a post from CSDN blog with specified parameters.
     *
     * @param csdnBlogUserName the specified CSDN blog user name
     * @param csdnBlogUserPwd the specified CSDN blog user password
     * @param csdnBlogArticleId the specified post id
     * @throws ServiceException service exception
     */
    public void deletePost(final String csdnBlogUserName,
                           final String csdnBlogUserPwd,
                           final String csdnBlogArticleId) throws
            ServiceException {
        final Object[] params = new Object[]{
            "ignored",
            csdnBlogArticleId,
            csdnBlogUserName,
            csdnBlogUserPwd,
            true};

        try {
            config.setServerURL(
                    new URL("http://blog.csdn.net/" + csdnBlogUserName
                    + "/services/metablogapi.aspx"));
            client.setConfig(config);
            client.execute(DELETE_POST, params);
            LOGGER.info("Deleted article[id=" + csdnBlogArticleId
                    + "] from CSDN blog");
        } catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);

            throw new ServiceException("New a post to CSDN blog error");
        }
    }

    /**
     * Creates a post to CSDN blog with specified parameters.
     *
     * @param csdnBlogUserName the specified CSDN blog user name
     * @param csdnBlogUserPwd the specified CSDN blog user password
     * @param csdnBlogArticle the specified CSDN blog article
     * @return post id just created
     * @throws ServiceException service exception
     */
    public String newPost(final String csdnBlogUserName,
                          final String csdnBlogUserPwd,
                          final CSDNBlogArticle csdnBlogArticle)
            throws ServiceException {
        final Object[] params = new Object[]{
            csdnBlogUserName,
            csdnBlogUserName,
            csdnBlogUserPwd,
            csdnBlogArticle.toPost(), true};

        String ret = null;
        try {
            config.setServerURL(
                    new URL("http://blog.csdn.net/" + csdnBlogUserName
                    + "/services/metablogapi.aspx"));
            client.setConfig(config);
            final String articleId = (String) client.execute(NEW_POST, params);
            LOGGER.info("Post article to CSDN blog[result=" + articleId + "]");

            ret = articleId;
        } catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);

            throw new ServiceException("New a post to CSDN blog error");
        }

        return ret;
    }

    /**
     * Gets the all archive dates(yyyy/MM) by the specified CSDN blog user
     * name.
     *
     * @param csdnBlogUserName the specified CSDN blog user name
     * @return a set of archive dates(yyyy/MM), returns an empty set if not
     * found any archive date
     */
    public List<String> getArchiveDates(final String csdnBlogUserName) {
        final List<String> ret = new ArrayList<String>();

        final IndexPageReader archivePageReader =
                new IndexPageReader(csdnBlogUserName);
        final String pageContent = archivePageReader.getContent();
        final String patternString = "<a href=\"/" + csdnBlogUserName
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
     * Gets the oldest archive date(yyyy/MM) by the specified CSDN blog user
     * name.
     *
     * @param csdnBlogUserName the specified CSDN blog user name
     * @return the oldest archive date(yyyy/MM), returns {@code null} if not
     * found any archive date
     */
    public String getOldestArchiveDate(final String csdnBlogUserName) {
        final IndexPageReader archivePageReader =
                new IndexPageReader(csdnBlogUserName);
        final String pageContent = archivePageReader.getContent();
        final String patternString = "<a href=\"/" + csdnBlogUserName
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

    /**
     * Gets article ids by the specified archive date.
     *
     * @param csdnBlogUserName the specified CSDN blog user name
     * @param archiveDate the specified archive date(yyyy/MM)
     * @return a set of article ids, returns an empty list if not found
     */
    public List<String> getArticleIdsByArchiveDate(
            final String csdnBlogUserName, final String archiveDate) {
        final ArchivePageReader archivePageReader =
                new ArchivePageReader(csdnBlogUserName, archiveDate);
        final String pageContent = archivePageReader.getContent();
        final String patternString =
                "<code><a href=\"/" + csdnBlogUserName + "/archive/"
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
     * Gets an article by the specified article id.
     *
     * @param csdnBlogUserName the specified CSDN blog user name
     * @param articleId the specified article id
     * @return article, returns {@code null} if error or not found or time out
     */
    public CSDNBlogArticle getArticleById(final String csdnBlogUserName,
                                          final String articleId) {
        final CSDNBlogArticle ret = new CSDNBlogArticle();
        ret.setId(articleId);

        try {
            config.setServerURL(new URL("http://blog.csdn.net/"
                    + csdnBlogUserName + "/services/metablogapi.aspx"));
            client.setConfig(config);

            final List<String> params = new ArrayList<String>();
            params.add(articleId);
            params.add(csdnBlogUserName);
            @SuppressWarnings("unchecked")
            final Map<String, ?> result =
                    (Map<String, ?>) client.execute(GET_POST_BY_ID, params);

            final String title = (String) result.get("title");
            ret.setTitle(title);

            final Object[] categoryObjects = (Object[]) result.get("categories");
            if (null != categoryObjects) {
                for (int i = 0; i < categoryObjects.length; i++) {
                    final Object category = categoryObjects[i];
                    ret.addCategory(category.toString());
                }
            }

            final Date createDate = (Date) result.get("dateCreated");
            ret.setCreateDate(createDate);

            final String description =
                    new String(((String) result.get("description")).getBytes());
            final String content = description.replaceAll("\\?", " "); // XXX: is really need this?
            ret.setContent(content);

        } catch (final Exception e) {
            LOGGER.error("Export article[id=" + articleId + "] error[msg="
                    + e.getMessage() + "]");

            return null;
        }

        try {
            LOGGER.trace("Sleep main thread [" + GET_ARTICLE_SLEEP_MILLIS
                    + "] millis for getting article from CSDN....");
            Thread.sleep(GET_ARTICLE_SLEEP_MILLIS);
        } catch (final InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
        }

        return ret;
    }
}
