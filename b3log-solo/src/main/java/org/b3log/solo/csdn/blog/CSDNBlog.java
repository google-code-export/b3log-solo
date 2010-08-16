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
package org.b3log.solo.csdn.blog;

import java.net.URL;
import org.apache.log4j.Logger;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.b3log.latke.service.ServiceException;

/**
 * CSDN blog.
 *
 * <p>
 * CSDN blog provides remote article(post, entry, article, whatever) management
 * management via <a href="http://www.xmlrpc.com/metaWeblogApi">
 * MetaWeblog</a>. The service address is:
 * http://blog.csdn.net/<b>userId</b>/services/metablogapi.aspx
 * </p>
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Aug 16, 2010
 * @see #newPost(java.lang.String, java.lang.String, org.b3log.solo.csdn.blog.CSDNBlogArticle)
 *
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
     * XML-RPC client configuration.
     */
    private XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
    /**
     * XML-RPC client.
     */
    private XmlRpcClient client = new XmlRpcClient();

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
}
