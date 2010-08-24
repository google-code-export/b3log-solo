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
package org.b3log.solo.model;

/**
 * This class defines all comment model relevant keys.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.2, Aug 24, 2010
 */
public final class Comment {

    /**
     * Comment.
     */
    public static final String COMMENT = "comment";
    /**
     * Comments.
     */
    public static final String COMMENTS = "comments";
    /**
     * Key of comment.
     */
    public static final String COMMENT_CONTENT = "commentContent";
    /**
     * Key of comment name.
     */
    public static final String COMMENT_NAME = "commentName";
    /**
     * Key of comment email.
     */
    public static final String COMMENT_EMAIL = "commentEmail";
    /**
     * Key of comment URL.
     */
    public static final String COMMENT_URL = "commentURL";
    /**
     * Key of comment date.
     */
    public static final String COMMENT_DATE = "commentDate";
    /**
     * Key of comment thumbnail URL.
     */
    public static final String COMMENT_THUMBNAIL_URL = "commentThumbnailURL";

    /**
     * Private default constructor.
     */
    private Comment() {
    }
}
