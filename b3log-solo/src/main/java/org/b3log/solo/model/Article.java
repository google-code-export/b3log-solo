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
 * This class defines all article model relevant keys.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.7, Oct 20, 2010
 */
public final class Article {

    /**
     * Article.
     */
    public static final String ARTICLE = "article";
    /**
     * Articles.
     */
    public static final String ARTICLES = "articles";
    /**
     * Key of title.
     */
    public static final String ARTICLE_TITLE = "articleTitle";
    /**
     * Key of abstract.
     */
    public static final String ARTICLE_ABSTRACT = "articleAbstract";
    /**
     * Key of content.
     */
    public static final String ARTICLE_CONTENT = "articleContent";
    /**
     * Key of create date.
     */
    public static final String ARTICLE_CREATE_DATE = "articleCreateDate";
    /**
     * Key of update date.
     */
    public static final String ARTICLE_UPDATE_DATE = "articleUpdateDate";
    /**
     * Key of tags.
     */
    public static final String ARTICLE_TAGS_REF = "articleTags";
    /**
     * Key of comment count.
     */
    public static final String ARTICLE_COMMENT_COUNT = "articleCommentCount";
    /**
     * Key of view count.
     */
    public static final String ARTICLE_VIEW_COUNT = "articleViewCount";
    /**
     * Key of comments.
     */
    public static final String ARTICLE_COMMENTS_REF = "articleComments";
    /**
     * Key of permalink.
     */
    public static final String ARTICLE_PERMALINK = "articlePermalink";
    /**
     * Key of put top.
     */
    public static final String ARTICLE_PUT_TOP = "articlePutTop";

    /**
     * Private default constructor.
     */
    private Article() {
    }
}
