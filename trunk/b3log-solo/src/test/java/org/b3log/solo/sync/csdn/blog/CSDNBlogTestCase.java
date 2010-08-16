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

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.b3log.solo.model.Article;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * {@link CSDNBlogTestCase} test case.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Aug 16, 2010
 */
public final class CSDNBlogTestCase {

    /**
     * CSDN user name.
     */
    private static final String USER_NAME = "DL88250";
    /**
     * CSDN user password.
     */
    private static final String USER_PASSWORD = "8825088250";

    /**
     * Tests
     * {@linkplain CSDNBlog#newPost(java.lang.String, java.lang.String, org.b3log.solo.csdn.blog.CSDNBlogArticle) }
     * and
     * {@linkplain CSDNBlog#deletePost(java.lang.String, java.lang.String, java.lang.String) }
     * methods.
     * 
     * @throws Exception exception
     */
    //@org.testng.annotations.Test
    public void newPost() throws Exception {
        final JSONObject article = getArticle();
        final CSDNBlogArticle csdnBlogArticle = new CSDNBlogArticle(article);

        final CSDNBlog csdnBlog = new CSDNBlog();
        final String articleId =
                csdnBlog.newPost(USER_NAME, USER_PASSWORD, csdnBlogArticle);

        csdnBlog.deletePost(USER_NAME, USER_PASSWORD, articleId);
    }

    /**
     * Gets an article for testing.
     * 
     * @return article
     * @throws JSONException json exception
     */
    private JSONObject getArticle() throws JSONException {
        final JSONObject ret = new JSONObject();

        ret.put(Article.ARTICLE_TITLE,
                UUID.randomUUID().toString());
        ret.put(Article.ARTICLE_CREATE_DATE, new Date());
        ret.put(Article.ARTICLE_CONTENT, "test content");
        final Set<String> categories = new HashSet<String>();
        categories.add("Game");
        ret.put(Article.ARTICLE_TAGS_REF, (Object) categories);

        return ret;
    }
}
