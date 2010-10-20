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
package org.b3log.solo.util;

import com.google.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.Tag;
import org.b3log.solo.repository.ArticleCommentRepository;
import org.b3log.solo.repository.TagArticleRepository;
import org.b3log.solo.repository.TagRepository;
import org.b3log.latke.Keys;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.SortDirection;
import org.b3log.solo.model.Comment;
import org.b3log.solo.repository.ArticleRepository;
import org.b3log.solo.repository.CommentRepository;
import org.b3log.solo.repository.TopArticleRepository;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Article utilities.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.2, Oct 20, 2010
 */
public final class ArticleUtils {

    /**
     * Comment repository.
     */
    @Inject
    private CommentRepository commentRepository;
    /**
     * Article-Comment repository.
     */
    @Inject
    private ArticleCommentRepository articleCommentRepository;
    /**
     * Tag-Article repository.
     */
    @Inject
    private TagArticleRepository tagArticleRepository;
    /**
     * Tag repository.
     */
    @Inject
    private TagRepository tagRepository;
    /**
     * Article repository.
     */
    @Inject
    private ArticleRepository articleRepository;
    /**
     * Top article repository.
     */
    @Inject
    private TopArticleRepository topArticleRepository;

    /**
     * Article comment count +1 for an article specified by the given article id.
     *
     * @param articleId the given article id
     * @throws JSONException json exception
     * @throws RepositoryException repository exception
     */
    public void incArticleCommentCount(final String articleId)
            throws JSONException, RepositoryException {
        final JSONObject article = articleRepository.get(articleId);
        final JSONObject newArticle =
                new JSONObject(article, JSONObject.getNames(article));
        final int viewCnt = article.getInt(Article.ARTICLE_COMMENT_COUNT);
        newArticle.put(Article.ARTICLE_COMMENT_COUNT, viewCnt + 1);
        articleRepository.update(articleId, newArticle);
    }

    /**
     * Article comment count -1 for an article specified by the given article id.
     *
     * @param articleId the given article id
     * @throws JSONException json exception
     * @throws RepositoryException repository exception
     */
    public void decArticleCommentCount(final String articleId)
            throws JSONException, RepositoryException {
        final JSONObject article = articleRepository.get(articleId);
        final JSONObject newArticle =
                new JSONObject(article, JSONObject.getNames(article));
        final int viewCnt = article.getInt(Article.ARTICLE_COMMENT_COUNT);
        newArticle.put(Article.ARTICLE_COMMENT_COUNT, viewCnt - 1);
        articleRepository.update(articleId, newArticle);
    }

    /**
     * Gets article view count1 for an article specified by the given article id.
     *
     * @param articleId the given article id
     * @return article view count
     * @throws JSONException json exception
     * @throws RepositoryException repository exception
     */
    public int getArticleViewCount(final String articleId)
            throws JSONException, RepositoryException {
        final JSONObject article = articleRepository.get(articleId);
        final JSONObject newArticle = new JSONObject(
                article, JSONObject.getNames(article));
        return article.getInt(Article.ARTICLE_VIEW_COUNT);
    }

    /**
     * Article view count +1 for an article specified by the given article id.
     *
     * @param articleId the given article id
     * @throws JSONException json exception
     * @throws RepositoryException repository exception
     */
    public void incArticleViewCount(final String articleId)
            throws JSONException, RepositoryException {
        final JSONObject article = articleRepository.get(articleId);
        final JSONObject newArticle = new JSONObject(
                article, JSONObject.getNames(article));
        final int viewCnt = article.getInt(Article.ARTICLE_VIEW_COUNT);
        newArticle.put(Article.ARTICLE_VIEW_COUNT, viewCnt + 1);
        articleRepository.update(articleId, newArticle);
    }

    /**
     * Removes tag-article relations by the specified article id.
     *
     * @param articleId the specified article id
     * @throws JSONException json exception
     * @throws RepositoryException repository exception
     */
    public void removeTagArticleRelations(final String articleId)
            throws JSONException, RepositoryException {
        final List<JSONObject> tagArticleRelations =
                tagArticleRepository.getByArticleId(articleId);
        for (int i = 0; i < tagArticleRelations.size(); i++) {
            final JSONObject tagArticleRelation =
                    tagArticleRelations.get(i);
            final String relationId =
                    tagArticleRelation.getString(Keys.OBJECT_ID);
            tagArticleRepository.remove(relationId);
        }
    }

    /**
     * Removes article comments by the specified article id.
     *
     * @param articleId the specified article id
     * @throws JSONException json exception
     * @throws RepositoryException repository exception
     */
    public void removeArticleComments(final String articleId)
            throws JSONException, RepositoryException {
        final List<JSONObject> articleCommentRelations =
                articleCommentRepository.getByArticleId(articleId);
        for (int i = 0; i < articleCommentRelations.size(); i++) {
            final JSONObject articleCommentRelation =
                    articleCommentRelations.get(i);
            final String commentId =
                    articleCommentRelation.getString(Comment.COMMENT + "_"
                                                     + Keys.OBJECT_ID);
            commentRepository.remove(commentId);
            final String relationId =
                    articleCommentRelation.getString(Keys.OBJECT_ID);
            articleCommentRepository.remove(relationId);
        }
    }

    /**
     * Adds relation of the specified tags and article.
     *
     * @param tags the specified tags
     * @param article the specified article
     * @throws JSONException json exception
     * @throws RepositoryException repository exception
     */
    public void addTagArticleRelation(final JSONArray tags,
                                      final JSONObject article)
            throws JSONException, RepositoryException {
        for (int i = 0; i < tags.length(); i++) {
            final JSONObject tag = tags.getJSONObject(i);
            final JSONObject tagArticleRelation = new JSONObject();

            tagArticleRelation.put(Tag.TAG + "_" + Keys.OBJECT_ID,
                                   tag.getString(Keys.OBJECT_ID));
            tagArticleRelation.put(Article.ARTICLE + "_" + Keys.OBJECT_ID,
                                   article.getString(Keys.OBJECT_ID));

            tagArticleRepository.add(tagArticleRelation);
        }
    }

    /**
     * Adds tags for every article of the specified articles.
     *
     * @param articles the specified articles
     * @throws RepositoryException repository exception
     * @throws JSONException json exception
     */
    public void addTags(final List<JSONObject> articles)
            throws RepositoryException, JSONException {
        for (final JSONObject article : articles) {
            final String articleId = article.getString(Keys.OBJECT_ID);
            final List<JSONObject> tagArticleRelations =
                    tagArticleRepository.getByArticleId(articleId);

            final List<JSONObject> tags = new ArrayList<JSONObject>();
            for (int i = 0; i < tagArticleRelations.size(); i++) {
                final JSONObject tagArticleRelation =
                        tagArticleRelations.get(i);
                final String tagId =
                        tagArticleRelation.getString(Tag.TAG + "_"
                                                     + Keys.OBJECT_ID);
                final JSONObject tag = tagRepository.get(tagId);
                tags.add(tag);
            }

            article.put(Article.ARTICLE_TAGS_REF,
                        /* Avoid convert to JSONArray, which FreeMarker can't
                         * process in <#list/> */
                        (Object) tags);
        }
    }

    /**
     * Gets top articles.
     *
     * @return top articles, returns an empty list if not found
     * @throws RepositoryException repository exception
     * @throws JSONException json exception
     */
    public List<JSONObject> getTopArticles() throws RepositoryException,
                                                    JSONException {
        final List<String> topArticleIds = getTopArticleIds();
        final List<JSONObject> ret = new ArrayList<JSONObject>();
        for (final String articleId : topArticleIds) {
            final JSONObject article = articleRepository.get(articleId);
            ret.add(article);
        }

        return ret;
    }

    /**
     * Gets top article ids order by oId descending.
     *
     * @return top article ids
     * @throws RepositoryException reposition exception
     * @throws JSONException json exception
     */
    public List<String> getTopArticleIds() throws RepositoryException,
                                                  JSONException {
        final List<String> ret = new ArrayList<String>();
        final JSONObject topArticleResults =
                topArticleRepository.get(1, Integer.MAX_VALUE, Keys.OBJECT_ID,
                                         SortDirection.DESCENDING);
        if (topArticleResults.has(Keys.RESULTS)) {
            final JSONArray ids = topArticleResults.getJSONArray(Keys.RESULTS);
            for (int i = 0; i < ids.length(); i++) {
                final String id = ids.getJSONObject(i).getString(
                        Article.ARTICLE + "_" + Keys.OBJECT_ID);
                ret.add(id);
            }
        }

        return ret;
    }
}
