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
import com.google.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.b3log.latke.client.action.AbstractCacheablePageAction;
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
import org.b3log.solo.model.Skin;
import org.b3log.solo.servlet.SoloServletListener;
import org.b3log.solo.util.Statistics;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Article action. article-detail.html.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.5, Aug 26, 2010
 */
public final class ArticleAction extends AbstractCacheablePageAction {

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
    /**
     * Statistic utilities.
     */
    @Inject
    private Statistics statistics;

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
            final JSONObject article = articleRepository.get(articleId);
            LOGGER.trace("Article[title="
                         + article.getString(Article.ARTICLE_TITLE) + "]");
            ret.put(Article.ARTICLE, article);

            final List<JSONObject> articleTags = getTags(articleId);
            ret.put(Article.ARTICLE_TAGS_REF, articleTags);

            final String previsouArticleId = articleRepository.
                    getPreviousArticleId(articleId);
            ret.put(Common.PREVIOUS_ARTICLE_ID, previsouArticleId);

            final String nextArticleId =
                    articleRepository.getNextArticleId(articleId);
            ret.put(Common.NEXT_ARTICLE_ID, nextArticleId);

            final JSONObject preference =
                    SoloServletListener.getUserPreference();
            final String skinDirName = preference.getString(Skin.SKIN_DIR_NAME);
            ret.put(Skin.SKIN_DIR_NAME, skinDirName);

            final List<JSONObject> articleComments = getComments(articleId);
            ret.put(Article.ARTICLE_COMMENTS_REF, articleComments);

            // Remove cached page for this article
            AbstractCacheablePageAction.PAGE_CACHE.remove("article-detail.dooId="
                                                          + articleId);

            filler.fillSide(ret);
            filler.fillBlogHeader(ret, request);
            filler.fillBlogFooter(ret, request);
            filler.fillArchiveDates(ret);
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
            comment.remove(Comment.COMMENT_EMAIL); // Remove email
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
