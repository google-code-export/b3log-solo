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
package org.b3log.solo.jsonrpc.impl;

import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import com.google.inject.Inject;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.b3log.solo.action.StatusCodes;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.Comment;
import org.b3log.solo.repository.ArticleCommentRepository;
import org.b3log.solo.repository.ArticleRepository;
import org.b3log.solo.repository.CommentRepository;
import org.b3log.latke.Keys;
import org.b3log.latke.client.action.ActionException;
import org.b3log.solo.jsonrpc.AbstractJSONRpcService;
import org.b3log.solo.util.ArticleUtils;
import org.b3log.solo.util.Buzzs;
import org.b3log.solo.util.Statistics;
import org.json.JSONObject;

/**
 * Comment service for JavaScript client.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.7, Aug 24, 2010
 */
public final class CommentService extends AbstractJSONRpcService {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(CommentService.class);
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
     * Article repository.
     */
    @Inject
    private ArticleRepository articleRepository;
    /**
     * Article utilities.
     */
    @Inject
    private ArticleUtils articleUtils;
    /**
     * Statistic utilities.
     */
    @Inject
    private Statistics statistics;
    /**
     * URL fetch service.
     */
    private URLFetchService urlFetchService =
            URLFetchServiceFactory.getURLFetchService();
    /**
     * Default user thumbnail.
     */
    private static final String DEFAULT_USER_THUMBNAIL =
            "default-user-thumbnail.png";

    /**
     * Gets comments of an article specified by the article id.
     *
     * @param requestJSONObject the specified request json object, for example,
     * <pre>
     * {
     *     "oId": articleId
     * }
     * </pre>
     * @param request the specified http servlet request
     * @param response the specified http servlet response
     * @return for example:
     * <pre>
     * {
     *     "comments": [{
     *         "oId": "",
     *         "commentName": "",
     *         "thumbnailUrl": "",
     *         "commentURL": "",
     *         "commentContent": "",
     *         "commentDate": "",
     *      }, ....]
     *     "sc": "GET_COMMENTS_SUCC"
     * }
     * </pre>
     * @throws ActionException action exception
     * @throws IOException io exception
     */
    public JSONObject getComments(final JSONObject requestJSONObject,
                                  final HttpServletRequest request,
                                  final HttpServletResponse response)
            throws ActionException, IOException {
        checkAuthorized(request, response);

        final JSONObject ret = new JSONObject();

        try {
            final String articleId = requestJSONObject.getString(Keys.OBJECT_ID);
            // Step 1: Get article-comment relations
            final List<JSONObject> articleCommentRelations =
                    articleCommentRepository.getByArticleId(articleId);
            // Step 2: Get comments
            final List<JSONObject> comments = new ArrayList<JSONObject>();
            for (int i = 0; i < articleCommentRelations.size(); i++) {
                final JSONObject articleCommentRelation =
                        articleCommentRelations.get(i);
                final String commentId =
                        articleCommentRelation.getString(Comment.COMMENT + "_"
                        + Keys.OBJECT_ID);

                final JSONObject comment = commentRepository.get(commentId);
                comment.remove(Comment.COMMENT_EMAIL); // Remove email
                comments.add(comment);
            }

            ret.put(Comment.COMMENTS, comments);
            ret.put(Keys.STATUS_CODE, StatusCodes.GET_COMMENTS_SUCC);
        } catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new ActionException(e);
        }

        return ret;
    }

    /**
     * Adds a comment to an article.
     *
     * @param requestJSONObject the specified request json object, for example,
     * <pre>
     * {
     *     "oId": articleId,
     *     "commentContent": ""
     * }
     * </pre>
     * @param request the specified http servlet request
     * @param response the specified http servlet response
     * @return for example,
     * <pre>
     * {
     *     "oId": generatedCommentId
     *     "sc": "COMMENT_ARTICLE_SUCC"
     * }
     * </pre>
     * @throws ActionException action exception
     * @throws IOException io exception
     */
    public JSONObject addComment(final JSONObject requestJSONObject,
                                 final HttpServletRequest request,
                                 final HttpServletResponse response)
            throws ActionException, IOException {
        // TODO: captcha
        final JSONObject ret = new JSONObject();

        try {
            final String articleId = requestJSONObject.getString(Keys.OBJECT_ID);
            final String commentName =
                    requestJSONObject.getString(Comment.COMMENT_NAME);
            final String commentEmail =
                    requestJSONObject.getString(Comment.COMMENT_EMAIL);
            final String commentURL =
                    requestJSONObject.getString(Comment.COMMENT_URL);
            final String commentContent =
                    requestJSONObject.getString(Comment.COMMENT_CONTENT);
            // Step 1: Add comment
            final JSONObject comment = new JSONObject();
            comment.put(Comment.COMMENT_NAME, commentName);
            comment.put(Comment.COMMENT_EMAIL, commentEmail);
            comment.put(Comment.COMMENT_URL, commentURL);
            comment.put(Comment.COMMENT_CONTENT, commentContent);
            comment.put(Comment.COMMENT_DATE,
                        Keys.SIMPLE_DATE_FORMAT.format(
                    System.currentTimeMillis()));
            setCommentThumbnailURL(comment);

            final String commentId = commentRepository.add(comment);
            // Step 2: Add article-comment relation
            final JSONObject articleCommentRelation = new JSONObject();
            articleCommentRelation.put(Article.ARTICLE + "_" + Keys.OBJECT_ID,
                                       articleId);
            articleCommentRelation.put(Comment.COMMENT + "_" + Keys.OBJECT_ID,
                                       commentId);
            articleCommentRepository.add(articleCommentRelation);
            // Step 3: Update article comment count
            articleUtils.incArticleCommentCount(articleId);
            // Step 4: Update blog statistic comment count
            statistics.incBlogCommentCount();

            ret.put(Keys.STATUS_CODE, StatusCodes.COMMENT_ARTICLE_SUCC);
            ret.put(Keys.OBJECT_ID, commentId);
        } catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new ActionException(e);
        }

        return ret;
    }

    /**
     * Removes a comment by the specified request json object.
     *
     * @param requestJSONObject the specified request json object, for example,
     * <pre>
     * {
     *     "oId": commentId,
     * }
     * </pre>
     * @param request the specified http servlet request
     * @param response the specified http servlet response
     * @return for example,
     * <pre>
     * {
     *     "sc": "REMOVE_COMMENT_SUCC"
     * }
     * </pre>
     * @throws ActionException action exception
     * @throws IOException io exception
     */
    public JSONObject removeComment(final JSONObject requestJSONObject,
                                    final HttpServletRequest request,
                                    final HttpServletResponse response)
            throws ActionException, IOException {
        checkAuthorized(request, response);

        final JSONObject ret = new JSONObject();

        try {
            final String commentId = requestJSONObject.getString(Keys.OBJECT_ID);
            LOGGER.debug("Removing comment[oId=" + commentId + "]");

            // Step 1: Remove article-comment relation
            final JSONObject articleCommentRelation =
                    articleCommentRepository.getByCommentId(commentId);
            final String articleCommentRelationId =
                    articleCommentRelation.getString(Keys.OBJECT_ID);
            articleCommentRepository.remove(articleCommentRelationId);

            final String articleId = articleCommentRelation.getString(
                    Article.ARTICLE + "_" + Keys.OBJECT_ID);
            // Step 2: Remove comment
            commentRepository.remove(commentId);
            // Step 3: Update article comment count
            articleUtils.decArticleCommentCount(articleId);
            // Step 4: Update blog statistic comment count
            statistics.decBlogCommentCount();

            ret.put(Keys.STATUS_CODE, StatusCodes.REMOVE_COMMENT_SUCC);

            LOGGER.debug("Removed comment[oId=" + commentId + "]");
        } catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new ActionException(e);
        }

        return ret;
    }

    /**
     * Sets commenter thumbnail URL for the specified comment.
     *
     * @param comment the specified comment
     * @throws Exception exception
     */
    private void setCommentThumbnailURL(final JSONObject comment)
            throws Exception {
        final String commentEmail = comment.getString(Comment.COMMENT_EMAIL);
        final String id = commentEmail.split("@")[0];
        final String domain = commentEmail.split("@")[1];


        if ("gmail.com".equals(domain.toLowerCase())) {
            final URL googleProfileURL =
                    new URL(Buzzs.GOOGLE_PROFILE_RETRIEVAL.replace("{userId}",
                                                                   id));
            // XXX: use async url fetch instead????
            final HTTPResponse response =
                    urlFetchService.fetch(googleProfileURL);
            final int statusCode = response.getResponseCode();

            if (HttpServletResponse.SC_OK == statusCode) {
                final byte[] content = response.getContent();
                final String profileJSONString = new String(content);
                LOGGER.trace("Google profile[jsonString=" + profileJSONString
                        + "]");
                final JSONObject profile = new JSONObject(profileJSONString);
                final JSONObject profileData = profile.getJSONObject("data");
                final String thumbnailUrl = profileData.getString("thumbnailUrl");

                comment.put(Comment.COMMENT_THUMBNAIL_URL, thumbnailUrl);
            } else {
                LOGGER.warn("Can not fetch google profile[userId=" + id + ", "
                        + "statusCode=" + statusCode + "]");
            }
        } else {
            LOGGER.warn("Not supported yet for comment thumbnail excepts Gmail "
                    + "user");
            comment.put(Comment.COMMENT_THUMBNAIL_URL, "/images/"
                    + DEFAULT_USER_THUMBNAIL);
            // TODO: process other comment thumbnail URL
        }
    }
}
