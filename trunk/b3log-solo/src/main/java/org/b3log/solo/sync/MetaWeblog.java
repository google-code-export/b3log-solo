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

package org.b3log.solo.sync;

import org.b3log.latke.service.ServiceException;

/**
 * Article remote management via via
 * <a href="http://www.xmlrpc.com/metaWeblogApi">MetaWeblog</a> blog.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Sep 3, 2010
 */
public interface Metaweblog {

    /**
     * Deletes a post from a MetaWeblog blog with specified parameters.
     *
     * @param userName the specified blog user name
     * @param userPwd the specified blog user password
     * @param postId the specified post id
     * @throws ServiceException service exception
     */
    void deletePost(final String userName,
                    final String userPwd,
                    final String postId)
            throws ServiceException;

    /**
     * Creates a post to a MetaWeblog blog with specified parameters.
     *
     * @param userName the specified blog user name
     * @param userPwd the specified blog user password
     * @param metaWeblogPost the specified blog post
     * @return post id just created
     * @throws ServiceException service exception
     */
    String newPost(final String userName,
                   final String userPwd,
                   final MetaWeblogPost metaWeblogPost)
            throws ServiceException;

    /**
     * Updates a post specified by the given post id to MetaWeblog blog with
     * specified parameters.
     *
     * @param postId the given post id
     * @param userName the specified blog user name
     * @param userPwd the specified blog user password
     * @param metaWeblogPost the specified blog post to update
     * @throws ServiceException service exception
     */
    void editPost(final String postId,
                  final String userName,
                  final String userPwd,
                  final MetaWeblogPost metaWeblogPost)
            throws ServiceException;
}
