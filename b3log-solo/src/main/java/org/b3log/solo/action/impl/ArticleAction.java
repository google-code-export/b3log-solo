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

package org.b3log.solo.action.impl;

import java.util.logging.Level;
import org.b3log.latke.Keys;
import org.b3log.latke.action.ActionException;
import com.google.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.latke.action.AbstractCacheablePageAction;
import org.b3log.latke.model.User;
import org.b3log.solo.action.util.Filler;
import org.b3log.solo.model.Article;
import org.b3log.solo.repository.TagArticleRepository;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.util.CollectionUtils;
import org.b3log.latke.util.Locales;
import org.b3log.latke.util.Strings;
import org.b3log.solo.model.Common;
import org.b3log.solo.model.Preference;
import org.b3log.solo.model.Skin;
import org.b3log.solo.repository.ArticleRepository;
import org.b3log.solo.repository.TagRepository;
import org.b3log.solo.repository.impl.ArticleGAERepository;
import org.b3log.solo.util.Articles;
import org.b3log.solo.util.Preferences;
import org.b3log.solo.util.comparator.Comparators;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

/**
 * Article action. article-detail.ftl.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.2.1, Jan 11, 2011
 */
public final class ArticleAction extends AbstractCacheablePageAction {

    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(ArticleAction.class.getName());
    /**
     * Article repository.
     */
    private ArticleRepository articleRepository =
            ArticleGAERepository.getInstance();
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
     * Article utilities.
     */
    @Inject
    private Articles articleUtils;
    /**
     * Preference utilities.
     */
    @Inject
    private Preferences preferenceUtils;

    @Override
    protected Map<?, ?> doFreeMarkerAction(
            final freemarker.template.Template template,
            final HttpServletRequest request,
            final HttpServletResponse response) throws ActionException {
        request.setAttribute(CACHED_TYPE, Article.ARTICLE);
        final Map<String, Object> ret = new HashMap<String, Object>();

        try {
            final JSONObject preference = preferenceUtils.getPreference();
            if (null == preference) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return ret;
            }

            final String localeString = preference.getString(
                    Preference.LOCALE_STRING);
            final Locale locale = new Locale(
                    Locales.getLanguage(localeString),
                    Locales.getCountry(localeString));

            final Map<String, String> langs = langPropsService.getAll(locale);
            ret.putAll(langs);

            final JSONObject queryStringJSONObject =
                    getQueryStringJSONObject(request);
            String articleId =
                    queryStringJSONObject.optString(Keys.OBJECT_ID);
            if (Strings.isEmptyOrNull(articleId)) {
                articleId = (String) request.getAttribute(Keys.OBJECT_ID);
            }

            if (Strings.isEmptyOrNull(articleId)) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);

                return ret;
            }

            final JSONObject article = articleRepository.get(articleId);
            if (null == article
                || !article.getBoolean(Article.ARTICLE_IS_PUBLISHED)) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);

                return ret;
            }

            request.setAttribute(CACHED_OID, articleId);
            incArticleViewCount(articleId);

            LOGGER.log(Level.FINEST, "Article[title={0}]",
                       article.getString(Article.ARTICLE_TITLE));
            ret.put(Article.ARTICLE, article);

            // For <meta name="description" content="${article.articleAbstract}"/>
            final String metaDescription = Jsoup.parse(article.getString(
                    Article.ARTICLE_ABSTRACT)).text();
            article.put(Article.ARTICLE_ABSTRACT, metaDescription);

            if (preference.getBoolean(Preference.ENABLE_ARTICLE_UPDATE_HINT)) {
                article.put(Common.HAS_UPDATED, articleUtils.hasUpdated(article));
            } else {
                article.put(Common.HAS_UPDATED, false);
            }

            final JSONObject author = articleUtils.getAuthor(article);
            final String authorName = author.getString(User.USER_NAME);
            article.put(Common.AUTHOR_NAME, authorName);
            final String authorId = author.getString(Keys.OBJECT_ID);
            article.put(Common.AUTHOR_ID, authorId);

            article.put(Article.ARTICLE_SIGN_REF,
                        articleUtils.getSign(articleId, preference));

            final JSONObject previous =
                    articleRepository.getPreviousArticle(articleId);
            if (null != previous) {
                final String previousArticlePermalink =
                        previous.getString(Article.ARTICLE_PERMALINK);
                final String previousArticleTitle =
                        previous.getString(Article.ARTICLE_TITLE);
                ret.put(Common.PREVIOUS_ARTICLE_PERMALINK,
                        previousArticlePermalink);
                ret.put(Common.PREVIOUS_ARTICLE_TITLE, previousArticleTitle);
            }

            final JSONObject next =
                    articleRepository.getNextArticle(articleId);
            if (null != next) {
                final String nextArticlePermalink =
                        next.getString(Article.ARTICLE_PERMALINK);
                final String nextArticleTitle =
                        next.getString(Article.ARTICLE_TITLE);
                ret.put(Common.NEXT_ARTICLE_PERMALINK, nextArticlePermalink);
                ret.put(Common.NEXT_ARTICLE_TITLE, nextArticleTitle);
            }

            final String skinDirName = preference.getString(Skin.SKIN_DIR_NAME);
            ret.put(Skin.SKIN_DIR_NAME, skinDirName);

            final List<JSONObject> articleComments =
                    articleUtils.getComments(articleId);
            ret.put(Article.ARTICLE_COMMENTS_REF, articleComments);

            final List<JSONObject> relevantArticles = getRelevantArticles(
                    articleId, article.getString(Article.ARTICLE_TAGS_REF));
            ret.put(Common.RELEVANT_ARTICLES, relevantArticles);

            ret.put(Preference.EXTERNAL_RELEVANT_ARTICLES_DISPLAY_CNT,
                    preference.getInt(
                    Preference.EXTERNAL_RELEVANT_ARTICLES_DISPLAY_CNT));

            ret.put(Preference.BLOG_HOST,
                    preference.getString(Preference.BLOG_HOST));

            filler.fillSide(ret);
            filler.fillBlogHeader(ret);
            filler.fillBlogFooter(ret);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);

            try {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);

                return ret;
            } catch (final IOException ex) {
                LOGGER.severe(ex.getMessage());
            }
        }

        return ret;
    }

    /**
     * Gets the relevant articles by the specified article tags string excludes
     * the specified article id.
     *
     * @param articleId the specified article id
     * @param articleTagsString the specified article tags string
     * @return a list of articles, returns an empty list if not found
     * @throws RepositoryException repository exception
     * @throws JSONException json exception
     */
    private List<JSONObject> getRelevantArticles(
            final String articleId, final String articleTagsString)
            throws JSONException, RepositoryException {
        final JSONObject preference = preferenceUtils.getPreference();
        if (null == preference) {
            throw new RepositoryException("Not found preference");
        }

        final int displayCnt =
                preference.getInt(Preference.RELEVANT_ARTICLES_DISPLAY_CNT);
        final String[] tagTitles = articleTagsString.split(",");
        final int maxTagCnt = displayCnt > tagTitles.length
                ? tagTitles.length : displayCnt;
        final List<JSONObject> articles = new ArrayList<JSONObject>();
        for (int i = 0; i < maxTagCnt; i++) {  // XXX: should average by tag?
            final String tagTitle = tagTitles[i];
            final JSONObject tag = tagRepository.getByTitle(tagTitle);
            final String tagId = tag.getString(Keys.OBJECT_ID);
            final JSONObject result =
                    tagArticleRepository.getByTagId(tagId, 1, displayCnt);
            final JSONArray tagArticleRelations =
                    result.getJSONArray(Keys.RESULTS);

            final int relationSize = displayCnt < tagArticleRelations.length()
                    ? displayCnt : tagArticleRelations.length();
            for (int j = 0; j < relationSize; j++) {
                final JSONObject tagArticleRelation =
                        tagArticleRelations.getJSONObject(j);
                final String relatedArticleId =
                        tagArticleRelation.getString(Article.ARTICLE + "_"
                                                     + Keys.OBJECT_ID);
                if (articleId.equals(relatedArticleId)) {
                    continue;
                }

                final JSONObject article =
                        articleRepository.get(relatedArticleId);

                boolean existed = false;
                for (final JSONObject relevantArticle : articles) {
                    if (relevantArticle.getString(Keys.OBJECT_ID).
                            equals(article.getString(Keys.OBJECT_ID))) {
                        existed = true;
                    }
                }

                if (!existed) {
                    articles.add(article);
                }
            }
        }

        if (displayCnt > articles.size()) {
            return articles;
        }

        final List<Integer> randomIntegers =
                CollectionUtils.getRandomIntegers(0,
                                                  articles.size() - 1,
                                                  displayCnt);
        final List<JSONObject> ret = new ArrayList<JSONObject>();
        for (final int index : randomIntegers) {
            ret.add(articles.get(index));
        }

        Collections.sort(ret, Comparators.ARTICLE_UPDATE_DATE_COMPARATOR);

        return ret;
    }

    @Override
    protected JSONObject doAjaxAction(final JSONObject data,
                                      final HttpServletRequest request,
                                      final HttpServletResponse response)
            throws ActionException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * View count +1 for an article specified by the given article id.
     * .
     * @param articleId the given article id
     */
    private void incArticleViewCount(final String articleId) {
        final Transaction transaction = articleRepository.beginTransaction();
        try {
            articleUtils.incArticleViewCount(articleId);
            transaction.commit();
        } catch (final Exception e) {
            transaction.rollback();
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }
}
