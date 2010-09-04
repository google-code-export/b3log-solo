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

import org.testng.annotations.Test;
import org.b3log.latke.Keys;
import java.util.List;
import java.util.Date;
import java.util.UUID;
import org.b3log.latke.service.ServiceException;
import org.b3log.solo.model.Article;
import org.b3log.solo.sync.MetaWeblogPost;
import org.json.JSONException;
import org.json.JSONObject;
import static org.testng.Assert.*;

/**
 * {@link CSDNBlog} test case.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.4, Sep 4, 2010
 */
public final class CSDNBlogTestCase {

    /**
     * Article sum of 2006/12.
     */
    private static final int ARTICLE_SUM_2006_12 = 49;
    /**
     * Article sum of 2009/08.
     */
    private static final int ARTICLE_SUM_2009_08 = 13;
    /**
     * CSDN blog.
     */
    private CSDNBlog csdnBlog = new CSDNBlog();

    /**
     * Public default constructor.
     */
    public CSDNBlogTestCase() {
        csdnBlog.setUserName("DL88250");
        csdnBlog.setUserPassword("ModifiedThis");
    }

    /**
     * Tests
     * {@linkplain CSDNBlog#getArchiveDates(java.lang.String) } method.
     */
    //@Test
    public void getArchiveDates() {
        final List<String> archiveDates = csdnBlog.getArchiveDates();

        final int dl88250ArchiveDateCnt = 46;  // Maybe?
        assertEquals(archiveDates.size(), dl88250ArchiveDateCnt);
    }

    /**
     * Tests
     * {@linkplain CSDNBlog#getArchiveDates() } method.
     */
    //@Test
    public void getArticleIdsByArchiveDate() {
        List<String> articleIds = csdnBlog.getArticleIdsByArchiveDate("2006/12");
        assertEquals(articleIds.size(), ARTICLE_SUM_2006_12);

        articleIds = csdnBlog.getArticleIdsByArchiveDate("2009/08");
        assertEquals(articleIds.size(), ARTICLE_SUM_2009_08);
    }

    /**
     * Tests {@linkplain CSDNBlog#getPost(java.lang.String)} method.
     * @throws ServiceException service exception
     */
    //@Test
    public void getPost() throws ServiceException {
        final MetaWeblogPost article = csdnBlog.getPost("5817062");
        assertNotNull(article);
        assertEquals(article.getTitle(), "HTTP/1.1 Status Code Definitions");
    }

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

        final String articleId = csdnBlog.newPost(csdnBlogArticle);

        csdnBlog.deletePost(articleId);
    }

    /**
     * Gets an article for testing.
     * 
     * @return article
     * @throws JSONException json exception
     */
    private JSONObject getArticle() throws JSONException {
        final JSONObject ret = new JSONObject();

        ret.put(Keys.OBJECT_ID, "test id");
        ret.put(Article.ARTICLE_TITLE,
                UUID.randomUUID().toString());
        ret.put(Article.ARTICLE_CREATE_DATE, new Date());
        ret.put(Article.ARTICLE_CONTENT, "test content");
        final String categories = "Game";
        ret.put(Article.ARTICLE_TAGS_REF, categories);

        return ret;
    }
}
