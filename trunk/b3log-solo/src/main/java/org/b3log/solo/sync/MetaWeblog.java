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

/**
 * Article remote management via via
 * <a href="http://www.xmlrpc.com/metaWeblogApi">MetaWeblog</a> blog.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.2, Nov 3, 2010
 */
public interface MetaWeblog extends Blog {

    /**
     * Gets API address with the specified user name.
     *
     * @return API address, for example,
     * {@code http://blog.csdn.net/${userName}/services/metablogapi.aspx}
     */
    String getApiAddress();

    /**
     * Gets a post from a MetaWeblog blog with the specified parameters.
     *
     * @param postId the specified post id
     * @return blog post, returns {@code null} if occurs error or not
     * found
     * @throws SyncException sync exception
     */
    Post getPost(final String postId)
            throws SyncException;

    /**
     * Deletes a post from a MetaWeblog blog with specified parameters.
     *
     * @param postId the specified post id
     * @throws SyncException sync exception
     */
    void deletePost(final String postId)
            throws SyncException;

    /**
     * Creates a post to a MetaWeblog blog with specified parameters.
     *
     * @param metaWeblogPost the specified blog post
     * @return post id just created
     * @throws SyncException sync exception
     */
    String newPost(final Post metaWeblogPost)
            throws SyncException;

    /**
     * Updates a post specified by the given post id to MetaWeblog blog with
     * specified parameters.
     *
     * @param postId the given post id
     * @param metaWeblogPost the specified blog post to update
     * @throws SyncException sync exception
     */
    void editPost(final String postId,
                  final Post metaWeblogPost)
            throws SyncException;
}
