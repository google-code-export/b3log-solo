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
package org.b3log.solo.event.sync;

/**
 * This enumeration defines all status codes of blog sync actions.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Sep 24, 2010
 */
public final class BlogSyncStatusCodes {

    /**
     * Indicates sync add an article to CSDN blog successfully.
     */
    public static final String BLOG_SYNC_ADD_CSDN_BLOG_SUCC =
            "BLOG_SYNC_ADD_CSDN_BLOG_SUCC";
    /**
     * Indicates sync add an article to CSDN blog fail.
     */
    public static final String BLOG_SYNC_ADD_CSDN_BLOG_FAIL =
            "BLOG_SYNC_ADD_CSDN_BLOG_FAIL";
    /**
     * Indicates sync update an article to CSDN blog successfully.
     */
    public static final String BLOG_SYNC_UPDATE_CSDN_BLOG_SUCC =
            "BLOG_SYNC_UPDATE_CSDN_BLOG_SUCC";
    /**
     * Indicates sync update an article to CSDN blog fail.
     */
    public static final String BLOG_SYNC_UPDATE_CSDN_BLOG_FAIL =
            "BLOG_SYNC_UPDATE_CSDN_BLOG_FAIL";
    /**
     * Indicates sync remove an article from CSDN blog successfully.
     */
    public static final String BLOG_SYNC_REMOVE_CSDN_BLOG_SUCC =
            "BLOG_SYNC_REMOVE_CSDN_BLOG_SUCC";
    /**
     * Indicates sync remove an article from CSDN blog fail.
     */
    public static final String BLOG_SYNC_REMOVE_CSDN_BLOG_FAIL =
            "BLOG_SYNC_REMOVE_CSDN_BLOG_FAIL";
    /**
     * Indicates sync add an article to CnBlogs successfully.
     */
    public static final String BLOG_SYNC_ADD_CNBLOGS_SUCC =
            "BLOG_SYNC_ADD_BLOG_SYNC_CNBLOGS_SUCC";
    /**
     * Indicates sync add an article to CnBlogs fail.
     */
    public static final String BLOG_SYNC_ADD_CNBLOGS_FAIL =
            "BLOG_SYNC_ADD_BLOG_SYNC_CNBLOGS_FAIL";
    /**
     * Indicates sync update an article to CnBlogs successfully.
     */
    public static final String BLOG_SYNC_UPDATE_CNBLOGS_SUCC =
            "BLOG_SYNC_UPDATE_BLOG_SYNC_CNBLOGS_SUCC";
    /**
     * Indicates sync update an article to CnBlogs fail.
     */
    public static final String BLOG_SYNC_UPDATE_CNBLOGS_FAIL =
            "BLOG_SYNC_UPDATE_BLOG_SYNC_CNBLOGS_FAIL";
    /**
     * Indicates sync remove an article from CnBlogs successfully.
     */
    public static final String BLOG_SYNC_REMOVE_CNBLOGS_SUCC =
            "BLOG_SYNC_REMOVE_BLOG_SYNC_CNBLOGS_SUCC";
    /**
     * Indicates sync remove an article from CnBlogs fail.
     */
    public static final String BLOG_SYNC_REMOVE_CNBLOGS_FAIL =
            "BLOG_SYNC_REMOVE_BLOG_SYNC_CNBLOGS_FAIL";
    /**
     * Indicates sync add an article to BlogJava successfully.
     */
    public static final String BLOG_SYNC_ADD_BLOGJAVA_SUCC =
            "BLOG_SYNC_ADD_BLOGJAVA_SUCC";
    /**
     * Indicates sync add an article to BlogJava fail.
     */
    public static final String BLOG_SYNC_ADD_BLOGJAVA_FAIL =
            "BLOG_SYNC_ADD_BLOGJAVA_FAIL";
    /**
     * Indicates sync update an article to BlogJava successfully.
     */
    public static final String BLOG_SYNC_UPDATE_BLOGJAVA_SUCC =
            "BLOG_SYNC_UPDATE_BLOGJAVA_SUCC";
    /**
     * Indicates sync update an article to BlogJava fail.
     */
    public static final String BLOG_SYNC_UPDATE_BLOGJAVA_FAIL =
            "BLOG_SYNC_UPDATE_BLOGJAVA_FAIL";
    /**
     * Indicates sync remove an article from BlogJava successfully.
     */
    public static final String BLOG_SYNC_REMOVE_BLOGJAVA_SUCC =
            "BLOG_SYNC_REMOVE_BLOGJAVA_SUCC";
    /**
     * Indicates sync remove an article from BlogJava fail.
     */
    public static final String BLOG_SYNC_REMOVE_BLOGJAVA_FAIL =
            "BLOG_SYNC_REMOVE_BLOGJAVA_FAIL";

    /**
     * Private default constructor.
     */
    private BlogSyncStatusCodes() {
    }
}
