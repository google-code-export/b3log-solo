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
package org.b3log.solo.action.impl;

import org.b3log.latke.Keys;
import org.b3log.latke.client.action.ActionException;
import org.b3log.latke.client.action.AbstractAction;
import com.google.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.b3log.solo.action.util.Filler;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.Comment;
import org.b3log.solo.model.Tag;
import org.b3log.solo.repository.ArticleCommentRepository;
import org.b3log.solo.repository.CommentRepository;
import org.b3log.solo.repository.TagArticleRepository;
import org.b3log.solo.repository.TagRepository;
import org.b3log.solo.repository.impl.ArticleGAERepository;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.util.Locales;
import org.b3log.solo.model.Common;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Article action. article-detail.html.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.3, Aug 14, 2010
 */
public final class ArticleAction extends AbstractAction {

    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(ArticleAction.class);
    /**
     * Article repository.
     */
    @Inject
    private ArticleGAERepository articleRepository;
    /**
     * Tag repository.
     */
    @Inject
    private TagRepository tagRepository;
    /**
     * Tag-Article repository.
     */
    @Inject
    private TagArticleRepository tagArticleRepository;
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
     * Filler.
     */
    @Inject
    private Filler filler;
    /**
     * Language service.
     */
    @Inject
    private LangPropsService langPropsService;

    @Override
    protected Map<?, ?> doFreeMarkerAction(
            final freemarker.template.Template template,
            final HttpServletRequest request,
            final HttpServletResponse response) throws ActionException {
        final Map<String, Object> ret = new HashMap<String, Object>();
        final Locale locale = Locales.getLocale(request);
        Locales.setLocale(request, locale);

        try {
            final Map<String, String> langs = langPropsService.getAll(locale);
            ret.putAll(langs);

            final JSONObject queryStringJSONObject =
                    getQueryStringJSONObject(request);
            final String articleId =
                    queryStringJSONObject.getString(Keys.OBJECT_ID);
            // Get the article
            final JSONObject article = articleRepository.get(articleId);
            LOGGER.trace("Article[title="
                    + article.getString(Article.ARTICLE_TITLE) + "]");
            ret.put(Article.ARTICLE, article);
            // Get tags
            final List<JSONObject> articleTags = getTags(articleId);
            ret.put(Article.ARTICLE_TAGS_REF, articleTags);
            // Get comments
            final List<JSONObject> articleComments = getComments(articleId);
            ret.put(Article.ARTICLE_COMMENTS_REF, articleComments);
            // Get the previous article id
            final String previsouArticleId = articleRepository.
                    getPrevisouArticleId(articleId);
            ret.put(Common.PREVIOUS_ARTICLE_ID, previsouArticleId);
            // Get the next article id
            final String nextArticleId =
                    articleRepository.getNextArticleId(articleId);
            ret.put(Common.NEXT_ARTICLE_ID, nextArticleId);


            filler.fillSide(ret);
            filler.fillBlogHeader(ret, request);
        } catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new ActionException(e);
        }

        return ret;
    }

    /**
     * Gets comments of an article specified by the article id.
     *
     * @param articleId the specified article id
     * @return a list of comments, returns an empty list if not found
     * @throws RepositoryException repository exception
     * @throws JSONException json exception
     */
    private List<JSONObject> getComments(final String articleId)
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
            ret.add(comment);
        }

        return ret;
    }

    /**
     * Gets tags of an article specified by the article id.
     *
     * @param articleId the specified article id
     * @return a list of tags, returns an empty list if not found
     * @throws RepositoryException repository exception
     * @throws JSONException json exception
     */
    private List<JSONObject> getTags(final String articleId)
            throws RepositoryException, JSONException {
        final List<JSONObject> ret = new ArrayList<JSONObject>();
        final List<JSONObject> tagArticleRelations =
                tagArticleRepository.getByArticleId(articleId);
        for (int i = 0; i < tagArticleRelations.size(); i++) {
            final JSONObject tagArticleRelation =
                    tagArticleRelations.get(i);
            final String tagId =
                    tagArticleRelation.getString(Tag.TAG + "_" + Keys.OBJECT_ID);

            final JSONObject tag = tagRepository.get(tagId);
            ret.add(tag);
        }

        return ret;
    }

    @Override
    protected JSONObject doAjaxAction(final JSONObject data,
                                      final HttpServletRequest request,
                                      final HttpServletResponse response)
            throws ActionException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
