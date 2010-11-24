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

package org.b3log.solo.sync;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

/**
 * Abstract MetaWeblog.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.2, Nov 3, 2010
 */
public abstract class AbstractMetaWeblog extends AbstractBlog
        implements MetaWeblog {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(AbstractMetaWeblog.class.getName());
    /**
     * New post method.
     */
    public static final String NEW_POST = "metaWeblog.newPost";
    /**
     * Delete post method.
     */
    public static final String DELETE_POST = "blogger.deletePost";
    /**
     * Edit post method.
     */
    public static final String EDIT_POST = "metaWeblog.editPost";
    /**
     * Get post by id method(need password).
     */
    public static final String GET_POST = "metaWeblog.getPost";
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
     * Connection timeout in milliseconds.
     */
    private static final int CONNECTION_TIMEOUT = 10000;
    /**
     * Shanghai date format.
     */
    public static final DateFormat CST_DATE_FORMAT =
            new SimpleDateFormat();
    /**
     * CST date format.
     */
    public static final DateFormat UTC_DATE_FORMAT =
            new SimpleDateFormat();

    static {
        final TimeZone cstTimeZone = TimeZone.getTimeZone("CST");
        CST_DATE_FORMAT.setTimeZone(cstTimeZone);

        final TimeZone utcTimeZone = TimeZone.getTimeZone("UTC");
        UTC_DATE_FORMAT.setTimeZone(utcTimeZone);
    }

    @Override
    public void deletePost(final String postId) throws SyncException {
        final Object[] params = new Object[]{"ignored",
                                             postId,
                                             getUserName(),
                                             getUserPassword(),
                                             true};
        try {
            config.setConnectionTimeout(CONNECTION_TIMEOUT);
            config.setServerURL(new URL(getApiAddress()));
            client.setConfig(config);
            client.execute(DELETE_POST, params);
            LOGGER.log(Level.INFO, "Deleted article[id={0}] from [{1}]",
                       new String[]{postId, getBloggingServiceProvider()});
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, "Delete post to [{0}] error",
                       getBloggingServiceProvider());
            throw new SyncException(e.getMessage());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Post getPost(final String postId) throws SyncException {
        final Object[] params = new Object[]{postId,
                                             getUserName(),
                                             getUserPassword()};
        try {
            config.setConnectionTimeout(CONNECTION_TIMEOUT);
            config.setServerURL(new URL(getApiAddress()));
            client.setConfig(config);
            final Map<String, ?> result =
                    (Map<String, ?>) client.execute(GET_POST, params);
            LOGGER.log(Level.FINEST, "metaWeblog.getPost result[{0}]",
                       result);
            LOGGER.log(Level.INFO, "Got an article[id={0}] from [{1}]",
                       new String[]{postId, getBloggingServiceProvider()});

            final MetaWeblogPost ret = new MetaWeblogPost();
            ret.setId(postId);
            LOGGER.log(Level.FINEST, "Post[keys={0}]", result.keySet());
            final String title = (String) result.get("title");
            ret.setTitle(title);

            final Object[] categoryObjects = (Object[]) result.get("categories");
            // XXX: BlogJava always empty
            if (null != categoryObjects) {
                LOGGER.log(Level.FINEST, "Category length[{0}]",
                           categoryObjects.length);
                for (int i = 0; i < categoryObjects.length; i++) {
                    final Object category = categoryObjects[i];
                    LOGGER.log(Level.FINEST, "Category[{0}]",
                               category.toString());
                    ret.addCategory(category.toString());
                }
            }

            final Date createDate = (Date) result.get("dateCreated");
            ret.setCreateDate(createDate);

            final String description = (String) result.get("description");
            // XXX: Syntax highlighting for CSDN
            final String content = description.replaceAll(
                    "<textarea",
                    "<pre name='code' class='brush:java;'").
                    replaceAll("</textarea>", "</pre>");
            ret.setContent(content);

            try {
                LOGGER.log(Level.INFO,
                           "Sleeping [{0}] milliseconds after retrieving post[id={1}]",
                           new Object[]{String.valueOf(GET_ARTICLE_SLEEP_MILLIS),
                                        postId});
                Thread.sleep(GET_ARTICLE_SLEEP_MILLIS);
            } catch (final InterruptedException e) {
                LOGGER.warning(e.getMessage());
            }

            return ret;
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public String newPost(final Post metaWeblogPost)
            throws SyncException {
        final Object[] params = new Object[]{getUserName(),
                                             getUserName(),
                                             getUserPassword(),
                                             metaWeblogPost.toMetaWeblogPost(),
                                             true};
        String ret = null;
        try {
            config.setConnectionTimeout(CONNECTION_TIMEOUT);
            config.setServerURL(new URL(getApiAddress()));
            client.setConfig(config);
            final String articleId = (String) client.execute(NEW_POST, params);
            LOGGER.log(Level.INFO, "Post an article to [{0}] [result={1}]",
                       new String[]{getBloggingServiceProvider(), articleId});

            ret = articleId;
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, "New post to [{0}] error",
                       getBloggingServiceProvider());
            throw new SyncException(e.getMessage());
        }

        return ret;
    }

    @Override
    public void editPost(final String postId,
                         final Post metaWeblogPost)
            throws SyncException {
        final Object[] params = new Object[]{postId,
                                             getUserName(),
                                             getUserPassword(),
                                             metaWeblogPost.toMetaWeblogPost(),
                                             true};
        try {
            config.setConnectionTimeout(CONNECTION_TIMEOUT);
            config.setServerURL(new URL(getApiAddress()));
            client.setConfig(config);
            client.execute(EDIT_POST, params);
            LOGGER.log(Level.INFO, "Edit an article[postId={0}] to [{1}]",
                       new String[]{postId, getBloggingServiceProvider()});


        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, "Edit post to [{0}] error",
                       getBloggingServiceProvider());
            throw new SyncException(e.getMessage());
        }
    }
}
