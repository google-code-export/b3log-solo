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

package org.b3log.solo.sync.cnblogs;

import org.b3log.solo.sync.SyncException;
import org.b3log.solo.sync.MetaWeblogPost;
import java.util.List;
import java.util.Date;
import org.b3log.solo.model.Article;
import org.b3log.solo.sync.Post;
import org.json.JSONException;
import org.json.JSONObject;
import static org.testng.Assert.*;

/**
 * {@link CnBlogsBlog} test case.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Nov 3, 2010
 */
public final class CnBlogsBlogTestCase {

    /**
     * Article sum of 2010/08.
     */
    private static final int ARTICLE_SUM_2010_09 = 1;
    /**
     * CnBlogs blog.
     */
    private CnBlogsBlog cnBlogsBlog = new CnBlogsBlog();

    /**
     * Public default constructor.
     */
    public CnBlogsBlogTestCase() {
        cnBlogsBlog.setUserName("DL88250");
        cnBlogsBlog.setUserPassword("dl88250");
    }

    /**
     * Tests
     * {@linkplain CnBlogsBlog#getArchiveDates(java.lang.String) } method.
     */
    //@org.testng.annotations.Test
    public void getArchiveDates() {
        final List<String> archiveDates = cnBlogsBlog.getArchiveDates();

        final int archiveDateCnt = 1;  // Maybe?
        assertEquals(archiveDates.size(), archiveDateCnt);
    }

    /**
     * Tests
     * {@linkplain CnBlogsBlog#getArchiveDates() } method.
     */
    //@org.testng.annotations.Test
    public void getArticleIdsByArchiveDate() {
        final List<String> articleIds =
                cnBlogsBlog.getArticleIdsByArchiveDate("2010/09");
        assertEquals(articleIds.size(), ARTICLE_SUM_2010_09);
        System.out.println(articleIds);
    }

    /**
     * Tests {@linkplain CnBlogsBlog#getPost(java.lang.String)} method.
     * @throws SyncException sync exception
     */
    //@org.testng.annotations.Test
    public void getPost() throws SyncException {
        final Post article = cnBlogsBlog.getPost("1818114");
        assertNotNull(article);
        assertEquals(article.getTitle(), "For Solo 同步测试");
    }

    /**
     * Tests
     * {@linkplain CnBlogsBlog#newPost(org.b3log.solo.sync.MetaWeblogPost) }
     * and
     * {@linkplain CnBlogsBlog#deletePost(java.lang.String) }
     * methods.
     *
     * @throws Exception exception
     */
    //@org.testng.annotations.Test
    public void newPost() throws Exception {
        final JSONObject article = getArticle();
        final MetaWeblogPost cnBlogsBlogArticle =
                new MetaWeblogPost(article);

        final String articleId = cnBlogsBlog.newPost(cnBlogsBlogArticle);

        cnBlogsBlog.deletePost(articleId);
    }

    /**
     * Gets an article for testing.
     * 
     * @return article
     * @throws JSONException json exception
     */
    private JSONObject getArticle() throws JSONException {
        final JSONObject ret = new JSONObject();

        ret.put(Article.ARTICLE_TITLE, "测试 Solo 同步发文");
        ret.put(Article.ARTICLE_CREATE_DATE, new Date());
        ret.put(Article.ARTICLE_CONTENT, "测试 Solo 同步发文");
        final String categories = "B3log, 测试";
        ret.put(Article.ARTICLE_TAGS_REF, categories);

        return ret;
    }
}
