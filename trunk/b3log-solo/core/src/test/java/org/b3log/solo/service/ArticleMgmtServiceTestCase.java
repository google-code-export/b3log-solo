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
package org.b3log.solo.service;

import org.b3log.latke.Keys;
import org.b3log.latke.model.User;
import org.b3log.latke.util.Requests;
import org.b3log.solo.AbstractTestCase;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.Common;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * {@link ArticleMgmtService} test case.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Jan 7, 2012
 */
@Test(suiteName = "service")
public class ArticleMgmtServiceTestCase extends AbstractTestCase {

    /**
     * Init.
     * 
     * @throws Exception exception
     */
    @Test
    public void init() throws Exception {
        final InitService initService = InitService.getInstance();

        final JSONObject requestJSONObject = new JSONObject();
        requestJSONObject.put(User.USER_EMAIL, "test@gmail.com");
        requestJSONObject.put(User.USER_NAME, "Admin");
        requestJSONObject.put(User.USER_PASSWORD, "pass");

        initService.init(requestJSONObject);

        final UserQueryService userQueryService = UserQueryService.getInstance();
        Assert.assertNotNull(userQueryService.getUserByEmail("test@gmail.com"));
    }

    /**
     * Add Article.
     *
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "init")
    public void addArticle() throws Exception {
        final ArticleMgmtService articleMgmtService =
                ArticleMgmtService.getInstance();

        final JSONObject requestJSONObject = new JSONObject();
        final JSONObject article = new JSONObject();
        requestJSONObject.put(Article.ARTICLE, article);

        article.put(Article.ARTICLE_AUTHOR_EMAIL, "test@gmail.com");
        article.put(Article.ARTICLE_TITLE, "article1 title");
        article.put(Article.ARTICLE_ABSTRACT, "article1 abstract");
        article.put(Article.ARTICLE_CONTENT, "article1 content");
        article.put(Article.ARTICLE_TAGS_REF, "tag1, tag2, tag3");
        article.put(Article.ARTICLE_PERMALINK, "article1 permalink");
        article.put(Article.ARTICLE_IS_PUBLISHED, true);
        article.put(Common.POST_TO_COMMUNITY, true);
        article.put(Article.ARTICLE_SIGN_REF + '_' + Keys.OBJECT_ID, "1");

        final String articleId =
                articleMgmtService.addArticle(requestJSONObject);

        Assert.assertNotNull(articleId);
    }

    /**
     * Update Article.
     * 
     * @throws Exception exception
     */
    @Test
    public void updateArticle() throws Exception {
        final ArticleMgmtService articleMgmtService =
                ArticleMgmtService.getInstance();

        final JSONObject requestJSONObject = new JSONObject();
        final JSONObject article = new JSONObject();
        requestJSONObject.put(Article.ARTICLE, article);

        article.put(Article.ARTICLE_AUTHOR_EMAIL, "test@gmail.com");
        article.put(Article.ARTICLE_TITLE, "article2 title");
        article.put(Article.ARTICLE_ABSTRACT, "article2 abstract");
        article.put(Article.ARTICLE_CONTENT, "article2 content");
        article.put(Article.ARTICLE_TAGS_REF, "tag1, tag2, tag3");
        article.put(Article.ARTICLE_PERMALINK, "article2 permalink");
        article.put(Article.ARTICLE_IS_PUBLISHED, true);
        article.put(Common.POST_TO_COMMUNITY, true);
        article.put(Article.ARTICLE_SIGN_REF + '_' + Keys.OBJECT_ID, "1");

        final String articleId =
                articleMgmtService.addArticle(requestJSONObject);

        Assert.assertNotNull(articleId);

        article.put(Keys.OBJECT_ID, articleId);
        article.put(Article.ARTICLE_TITLE, "updated article2 title");

        articleMgmtService.updateArticle(requestJSONObject);

        final ArticleQueryService articleQueryService =
                ArticleQueryService.getInstance();
        final JSONObject updated = articleQueryService.getArticleById(articleId);
        Assert.assertNotNull(updated);
        Assert.assertEquals(updated.getString(Article.ARTICLE_TITLE),
                            "updated article2 title");
    }

    /**
     * Remove Article.
     * 
     * @throws Exception exception
     */
    @Test
    public void removeArticle() throws Exception {
        final ArticleMgmtService articleMgmtService =
                ArticleMgmtService.getInstance();

        final JSONObject requestJSONObject = new JSONObject();
        final JSONObject article = new JSONObject();
        requestJSONObject.put(Article.ARTICLE, article);

        article.put(Article.ARTICLE_AUTHOR_EMAIL, "test@gmail.com");
        article.put(Article.ARTICLE_TITLE, "article3 title");
        article.put(Article.ARTICLE_ABSTRACT, "article3 abstract");
        article.put(Article.ARTICLE_CONTENT, "article3 content");
        article.put(Article.ARTICLE_TAGS_REF, "tag1, tag2, tag3");
        article.put(Article.ARTICLE_PERMALINK, "article3 permalink");
        article.put(Article.ARTICLE_IS_PUBLISHED, true);
        article.put(Common.POST_TO_COMMUNITY, true);
        article.put(Article.ARTICLE_SIGN_REF + '_' + Keys.OBJECT_ID, "1");

        final String articleId =
                articleMgmtService.addArticle(requestJSONObject);

        Assert.assertNotNull(articleId);

        articleMgmtService.removeArticle(articleId);

        final ArticleQueryService articleQueryService =
                ArticleQueryService.getInstance();
        final JSONObject updated = articleQueryService.getArticleById(articleId);
        Assert.assertNull(updated);
    }

    /**
     * Top Article.
     * 
     * @throws Exception exception
     */
    @Test(dependsOnMethods = "addArticle")
    public void topArticle() throws Exception {
        final ArticleMgmtService articleMgmtService =
                ArticleMgmtService.getInstance();
        final ArticleQueryService articleQueryService =
                ArticleQueryService.getInstance();
        final JSONObject paginationRequest =
                Requests.buildPaginationRequest("1/10/20");
        final JSONArray articles =
                articleQueryService.getArticles(paginationRequest).
                optJSONArray(Article.ARTICLES);

        Assert.assertNotEquals(articles.length(), 0);
        final JSONObject article = articles.getJSONObject(0);

        final String articleId = article.getString(Keys.OBJECT_ID);
        articleMgmtService.topArticle(articleId, true);
        articleMgmtService.topArticle(articleId, false);
        
        // TODO: assertions
    }
}