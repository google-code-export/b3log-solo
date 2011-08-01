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
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.util.CollectionUtils;
import org.b3log.latke.util.Locales;
import org.b3log.latke.util.Strings;
import org.b3log.solo.model.Common;
import org.b3log.solo.model.PageTypes;
import org.b3log.solo.model.Preference;
import org.b3log.solo.repository.ArticleRepository;
import org.b3log.solo.repository.TagRepository;
import org.b3log.solo.repository.impl.ArticleGAERepository;
import org.b3log.solo.repository.impl.TagArticleGAERepository;
import org.b3log.solo.repository.impl.TagGAERepository;
import org.b3log.solo.util.Articles;
import org.b3log.solo.util.Preferences;
import org.b3log.solo.util.Statistics;
import org.b3log.solo.util.comparator.Comparators;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

/**
 * Article action.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.3.1, Jul 11, 2011
 */
public final class ArticleAction extends AbstractFrontPageAction {

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
    private TagArticleRepository tagArticleRepository =
            TagArticleGAERepository.getInstance();
    /**
     * Tag repository.
     */
    private TagRepository tagRepository = TagGAERepository.getInstance();
    /**
     * Filler.
     */
    private Filler filler = Filler.getInstance();
    /**
     * Language service.
     */
    private LangPropsService langPropsService = LangPropsService.getInstance();
    /**
     * Article utilities.
     */
    private Articles articleUtils = Articles.getInstance();
    /**
     * Preference utilities.
     */
    private Preferences preferenceUtils = Preferences.getInstance();
    /**
     * Statistic utilities.
     */
    private Statistics statistics = Statistics.getInstance();

    /**
     * {@inheritDoc}
     * 
     * <p>
     * Article statistic view count +1 and article view count +1.
     * </p>
     */
    @Override
    protected void processPageCacheHit(final JSONObject cachedPageContentObject) {
        super.processPageCacheHit(cachedPageContentObject);
        
        try {
            final String oId = cachedPageContentObject.getString(
                    AbstractCacheablePageAction.CACHED_OID);
            LOGGER.log(Level.FINEST, "Page cached object[id={0}, type={1}]",
                       new Object[]{oId, cachedPageContentObject.optString(
                        AbstractCacheablePageAction.CACHED_TYPE)});
            
            statistics.incArticleViewCount(oId);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, "Process page cache hit error", e);
        }
    }

    @Override
    protected Map<?, ?> doFreeMarkerAction(
            final freemarker.template.Template template,
            final HttpServletRequest request,
            final HttpServletResponse response) throws ActionException {
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
            request.setAttribute(CACHED_TYPE, langs.get(PageTypes.ARTICLE));

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
            request.setAttribute(CACHED_TITLE,
                                 article.getString(Article.ARTICLE_TITLE));
            statistics.incArticleViewCount(articleId);

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
            article.put(Common.AUTHOR_ROLE, author.getString(User.USER_ROLE));

            LOGGER.finer("Getting article sign....");
            article.put(Article.ARTICLE_SIGN_REF,
                        articleUtils.getSign(articleId, preference));
            LOGGER.finer("Got article sign");

            LOGGER.finer("Getting the previous article....");
            final JSONObject previousArticle =
                    articleRepository.getPreviousArticle(articleId);
            if (null != previousArticle) {
                ret.put(Common.PREVIOUS_ARTICLE_PERMALINK,
                        previousArticle.getString(Article.ARTICLE_PERMALINK));
                ret.put(Common.PREVIOUS_ARTICLE_TITLE,
                        previousArticle.getString(Article.ARTICLE_TITLE));
                LOGGER.finer("Got the previous article");
            }

            LOGGER.finer("Getting the next article....");
            final JSONObject nextArticle =
                    articleRepository.getNextArticle(articleId);
            if (null != nextArticle) {
                ret.put(Common.NEXT_ARTICLE_PERMALINK,
                        nextArticle.getString(Article.ARTICLE_PERMALINK));
                ret.put(Common.NEXT_ARTICLE_TITLE,
                        nextArticle.getString(Article.ARTICLE_TITLE));
                LOGGER.finer("Got the next article");
            }

            LOGGER.finer("Getting article's comments....");
            final List<JSONObject> articleComments =
                    articleUtils.getComments(articleId);
            ret.put(Article.ARTICLE_COMMENTS_REF, articleComments);
            LOGGER.finer("Got article's comments");

            LOGGER.finer("Getting relevant articles....");
            final List<JSONObject> relevantArticles = getRelevantArticles(
                    articleId,
                    article.getString(Article.ARTICLE_TAGS_REF),
                    preference);
            ret.put(Common.RELEVANT_ARTICLES, relevantArticles);
            LOGGER.finer("Got relevant articles....");

            ret.put(Preference.EXTERNAL_RELEVANT_ARTICLES_DISPLAY_CNT,
                    preference.getInt(
                    Preference.EXTERNAL_RELEVANT_ARTICLES_DISPLAY_CNT));

            filler.fillSide(ret, preference);
            filler.fillBlogHeader(ret, preference);
            filler.fillBlogFooter(ret, preference);
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
     * Gets the relevant published articles by the specified article tags string
     * excludes the specified article id.
     *
     * @param articleId the specified article id
     * @param articleTagsString the specified article tags string
     * @param preference the specified preference
     * @return a list of articles, returns an empty list if not found
     * @throws RepositoryException repository exception
     * @throws JSONException json exception
     */
    private List<JSONObject> getRelevantArticles(
            final String articleId, final String articleTagsString,
            final JSONObject preference)
            throws JSONException, RepositoryException {
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

                if (articleRepository.isPublished(relatedArticleId)) {
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
        }

        Collections.sort(articles, Comparators.ARTICLE_UPDATE_DATE_COMPARATOR);

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