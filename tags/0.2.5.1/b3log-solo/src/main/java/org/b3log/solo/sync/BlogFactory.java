/*
 * Copyright (c) 2009, 2010, 2011, B3log Team
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

import org.b3log.solo.model.BlogSync;
import org.b3log.solo.sync.blogjava.BlogJavaBlog;
import org.b3log.solo.sync.cnblogs.CnBlogsBlog;
import org.b3log.solo.sync.csdn.blog.CSDNBlog;

/**
 * Blog factory.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Sep 6, 2010
 */
public final class BlogFactory {

    /**
     * Gets a {@link MetaWeblog} by the specified external blogging system name.
     * 
     * @param externalSysName the specified external blogging system name
     * @return MetaWeblog
     */
    public static MetaWeblog getMetaWeblog(final String externalSysName) {
        if (BlogSync.BLOG_SYNC_CSDN_BLOG.equals(externalSysName)) {
            return new CSDNBlog();
        } else if (BlogSync.BLOG_SYNC_CNBLOGS.equals(externalSysName)) {
            return new CnBlogsBlog();
        } else if (BlogSync.BLOG_SYNC_BLOGJAVA.equals(externalSysName)) {
            return new BlogJavaBlog();
        } else {
            throw new RuntimeException("Not supported external blogging system["
                                       + externalSysName + "]");
        }
    }

    /**
     * Private default constructor.
     */
    private BlogFactory() {
    }
}
