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

package org.b3log.solo.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.Tag;
import org.b3log.solo.repository.ArticleCommentRepository;
import org.b3log.solo.repository.TagArticleRepository;
import org.b3log.latke.Keys;
import org.b3log.latke.repository.FilterOperator;
import org.b3log.latke.repository.Query;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.SortDirection;
import org.b3log.latke.util.CollectionUtils;
import org.b3log.solo.SoloServletListener;
import org.b3log.solo.model.Comment;
import org.b3log.solo.model.Common;
import org.b3log.solo.model.Preference;
import org.b3log.solo.model.Sign;
import org.b3log.solo.repository.ArticleRepository;
import org.b3log.solo.repository.ArticleSignRepository;
import org.b3log.solo.repository.CommentRepository;
import org.b3log.solo.repository.UserRepository;
import org.b3log.solo.repository.impl.ArticleCommentGAERepository;
import org.b3log.solo.repository.impl.ArticleGAERepository;
import org.b3log.solo.repository.impl.ArticleSignGAERepository;
import org.b3log.solo.repository.impl.CommentGAERepository;
import org.b3log.solo.repository.impl.TagArticleGAERepository;
import org.b3log.solo.repository.impl.UserGAERepository;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Article utilities.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.2.0, Jan 20, 2011
 */
public final class Articles {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(Articles.class.getName());
    /**
     * Comment repository.
     */
    private CommentRepository commentRepository =
            CommentGAERepository.getInstance();
    /**
     * Article-Comment repository.
     */
    private ArticleCommentRepository articleCommentRepository =
            ArticleCommentGAERepository.getInstance();
    /**
     * Article-Sign repository.
     */
    private ArticleSignRepository articleSignRepository =
            ArticleSignGAERepository.getInstance();
    /**
     * Tag-Article repository.
     */
    private TagArticleRepository tagArticleRepository =
            TagArticleGAERepository.getInstance();
    /**
     * Article repository.
     */
    private ArticleRepository articleRepository =
            ArticleGAERepository.getInstance();
    /**
     * Statistic utilities.
     */
    private Statistics statistics = Statistics.getInstance();
    /**
     * User repository.
     */
    private UserRepository userRepository = UserGAERepository.getInstance();

    /**
     * Gets the specified article's author. The specified article has a property
     * {@value Article#ARTICLE_AUTHOR_EMAIL}, this method will use this property
     * to get a user from users.
     *
     * @param article the specified article
     * @return user, {@code null} if not found
     * @throws JSONException json exception
     */
    public JSONObject getAuthor(final JSONObject article)
            throws JSONException {
        final String email = article.getString(Article.ARTICLE_AUTHOR_EMAIL);

        return userRepository.getByEmail(email);
    }

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
        final int commentCnt = article.getInt(Article.ARTICLE_COMMENT_COUNT);
        newArticle.put(Article.ARTICLE_COMMENT_COUNT, commentCnt + 1);
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
        final int commentCnt = article.getInt(Article.ARTICLE_COMMENT_COUNT);
        newArticle.put(Article.ARTICLE_COMMENT_COUNT, commentCnt - 1);
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
     * <p>
     * Removes related comments, article-comment relations, sets article/blog
     * comment statistic count.
     * </p>
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

        int blogCommentCount = statistics.getBlogCommentCount();
        final int articleCommentCount = articleCommentRelations.size();
        blogCommentCount -= articleCommentCount;
        statistics.setBlogCommentCount(blogCommentCount);

        final JSONObject article = articleRepository.get(articleId);
        if (article.getBoolean(Article.ARTICLE_IS_PUBLISHED)) {
            int publishedBlogCommentCount =
                    statistics.getPublishedBlogCommentCount();
            publishedBlogCommentCount -= articleCommentCount;
            statistics.setPublishedBlogCommentCount(publishedBlogCommentCount);
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
     * Gets comments of an article specified by the article id.
     *
     * @param articleId the specified article id
     * @return a list of comments, returns an empty list if not found
     * @throws RepositoryException repository exception
     * @throws JSONException json exception
     */
    public List<JSONObject> getComments(final String articleId)
            throws JSONException, RepositoryException {
        final List<JSONObject> ret = new ArrayList<JSONObject>();
        final List<JSONObject> articleCommentRelations =
                articleCommentRepository.getByArticleId(articleId);
        for (int i = 0; i < articleCommentRelations.size(); i++) {
            final JSONObject articleCommentRelation =
                    articleCommentRelations.get(i);
            final String commentId =
                    articleCommentRelation.getString(Comment.COMMENT + "_"
                                                     + Keys.OBJECT_ID);

            final JSONObject comment = commentRepository.get(commentId);
            final String content = comment.getString(Comment.COMMENT_CONTENT).
                    replaceAll(SoloServletListener.ENTER_ESC, "<br/>");
            comment.put(Comment.COMMENT_CONTENT, content);
            comment.remove(Comment.COMMENT_EMAIL); // Remove email

            if (comment.has(Comment.COMMENT_ORIGINAL_COMMENT_ID)) {
                comment.put(Common.IS_REPLY, true);
                final String originalCommentName = comment.getString(
                        Comment.COMMENT_ORIGINAL_COMMENT_NAME);
                comment.put(Comment.COMMENT_ORIGINAL_COMMENT_NAME,
                            originalCommentName);
            } else {
                comment.put(Common.IS_REPLY, false);
            }

            ret.add(comment);
        }

        return ret;
    }

    /**
     * Adds relation of the specified article and sign.
     *
     * @param signId the specified sign id
     * @param articleId the specified article id
     * @throws JSONException json exception
     * @throws RepositoryException repository exception
     */
    public void addArticleSignRelation(final String signId,
                                       final String articleId)
            throws JSONException, RepositoryException {
        final JSONObject articleSignRelation = new JSONObject();

        articleSignRelation.put(Sign.SIGN + "_" + Keys.OBJECT_ID,
                                signId);
        articleSignRelation.put(Article.ARTICLE + "_" + Keys.OBJECT_ID,
                                articleId);

        articleSignRepository.add(articleSignRelation);
    }

    /**
     * Gets sign id of an article specified by the article id.
     *
     * @param articleId the specified article id
     * @param preference the specified preference
     * @return article sign, returns the default sign(which oId is "1") if not
     * found
     * @throws RepositoryException repository exception
     * @throws JSONException json exception
     */
    public JSONObject getSign(final String articleId,
                              final JSONObject preference)
            throws JSONException, RepositoryException {
        final JSONArray signs = new JSONArray(
                preference.getString(Preference.SIGNS));

        final JSONObject relation =
                articleSignRepository.getByArticleId(articleId);
        if (null == relation) {
            for (int i = 0; i < signs.length(); i++) {
                final JSONObject ret = signs.getJSONObject(i);
                if ("1".equals(ret.getString(Keys.OBJECT_ID))) {
                    LOGGER.log(Level.FINEST, "Used default article sign[{0}]",
                               ret);
                    return ret;
                }
            }
        }

        for (int i = 0; i < signs.length(); i++) {
            final JSONObject ret = signs.getJSONObject(i);
            if (relation.getString(Sign.SIGN + "_" + Keys.OBJECT_ID).
                    equals(ret.getString(Keys.OBJECT_ID))) {
                return ret;
            }
        }

        throw new RuntimeException("Can't load article sign!");
    }

    /**
     * Determines the specified article has updated.
     *
     * @param article the specified article
     * @return {@code true} if it has updated, {@code false} otherwise
     * @throws JSONException json exception
     */
    public boolean hasUpdated(final JSONObject article)
            throws JSONException {
        final Date updateDate = (Date) article.get(Article.ARTICLE_UPDATE_DATE);
        final Date createDate = (Date) article.get(Article.ARTICLE_CREATE_DATE);

        return !createDate.equals(updateDate);
    }

    /**
     * Determines the specified article had been published.
     *
     * @param article the specified article
     * @return {@code true} if it had been published, {@code false} otherwise
     * @throws JSONException json exception
     */
    public boolean hadBeenPublished(final JSONObject article)
            throws JSONException {
        return article.getBoolean(Article.ARTICLE_HAD_BEEN_PUBLISHED);
    }

    /**
     * Gets all unpublished articles.
     *
     * @return articles all unpublished articles
     * @throws RepositoryException repository exception
     * @throws JSONException json exception
     */
    public List<JSONObject> getUnpublishedArticles()
            throws RepositoryException, JSONException {
        final Map<String, SortDirection> sorts =
                new HashMap<String, SortDirection>();
        sorts.put(Article.ARTICLE_CREATE_DATE, SortDirection.DESCENDING);
        sorts.put(Article.ARTICLE_PUT_TOP, SortDirection.DESCENDING);
        final Query query = new Query().addFilter(Article.ARTICLE_IS_PUBLISHED,
                                                  FilterOperator.EQUAL,
                                                  true);
        final JSONObject result = articleRepository.get(query);
        final JSONArray articles = result.getJSONArray(Keys.RESULTS);

        return CollectionUtils.jsonArrayToList(articles);
    }

    /**
     * Gets the {@link Articles} singleton.
     *
     * @return the singleton
     */
    public static Articles getInstance() {
        return SingletonHolder.SINGLETON;
    }

    /**
     * Private default constructor.
     */
    private Articles() {
    }

    /**
     * Singleton holder.
     *
     * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
     * @version 1.0.0.0, Jan 12, 2011
     */
    private static final class SingletonHolder {

        /**
         * Singleton.
         */
        private static final Articles SINGLETON = new Articles();

        /**
         * Private default constructor.
         */
        private SingletonHolder() {
        }
    }
}
