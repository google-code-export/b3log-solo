/*
 * Copyright (c) 2009, 2010, B3log Team
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
import org.b3log.solo.action.util.Filler;
import org.b3log.solo.model.Article;
import org.b3log.solo.repository.TagArticleRepository;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.util.CollectionUtils;
import org.b3log.latke.util.Locales;
import org.b3log.latke.util.Strings;
import org.b3log.solo.model.Common;
import org.b3log.solo.model.Preference;
import org.b3log.solo.model.Skin;
import org.b3log.solo.repository.ArticleRepository;
import org.b3log.solo.util.ArticleUpdateDateComparator;
import org.b3log.solo.util.ArticleUtils;
import org.b3log.solo.util.PreferenceUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Article action. article-detail.ftl.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.1.2, Nov 15, 2010
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
    @Inject
    private ArticleRepository articleRepository;
    /**
     * Tag-Article repository.
     */
    @Inject
    private TagArticleRepository tagArticleRepository;
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
    private ArticleUtils articleUtils;
    /**
     * Preference utilities.
     */
    @Inject
    private PreferenceUtils preferenceUtils;

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
            if (null == article) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);

                return ret;
            }

            LOGGER.log(Level.FINEST, "Article[title={0}]",
                       article.getString(Article.ARTICLE_TITLE));
            ret.put(Article.ARTICLE, article);

            final List<JSONObject> articleTags = articleUtils.getTags(articleId);
            ret.put(Article.ARTICLE_TAGS_REF, articleTags);

            final JSONObject previous =
                    articleRepository.getPreviousArticle(articleId);
            if (null != previous) {
                final String previousArticleId =
                        previous.getString(Keys.OBJECT_ID);
                final String previousArticleTitle =
                        previous.getString(Article.ARTICLE_TITLE);
                ret.put(Common.PREVIOUS_ARTICLE_ID, previousArticleId);
                ret.put(Common.PREVIOUS_ARTICLE_TITLE, previousArticleTitle);
            }

            final JSONObject next =
                    articleRepository.getNextArticle(articleId);
            if (null != next) {
                final String nextArticleId =
                        next.getString(Keys.OBJECT_ID);
                final String nextArticleTitle = next.getString(
                        Article.ARTICLE_TITLE);
                ret.put(Common.NEXT_ARTICLE_ID, nextArticleId);
                ret.put(Common.NEXT_ARTICLE_TITLE, nextArticleTitle);
            }

            final String skinDirName = preference.getString(Skin.SKIN_DIR_NAME);
            ret.put(Skin.SKIN_DIR_NAME, skinDirName);

            final List<JSONObject> articleComments =
                    articleUtils.getComments(articleId);
            ret.put(Article.ARTICLE_COMMENTS_REF, articleComments);

            final List<JSONObject> relevantArticles =
                    getRelevantArticles(articleId, articleTags);
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
            LOGGER.severe(e.getMessage());
            throw new ActionException(e);
        }

        return ret;
    }

    /**
     * Gets the relevant articles by the specified article tags excludes the
     * specified article id.
     *
     * @param articleId the specified article id
     * @param articleTags the specified article tags
     * @return a list of articles, returns an empty list if not found
     * @throws RepositoryException repository exception
     * @throws JSONException json exception
     */
    private List<JSONObject> getRelevantArticles(
            final String articleId, final List<JSONObject> articleTags)
            throws JSONException, RepositoryException {
        final JSONObject preference = preferenceUtils.getPreference();
        if (null == preference) {
            throw new RepositoryException("Not found preference");
        }

        final int displayCnt =
                preference.getInt(Preference.RELEVANT_ARTICLES_DISPLAY_CNT);

        // XXX: should average by tag?
        final List<JSONObject> articles = new ArrayList<JSONObject>();
        for (final JSONObject articleTag : articleTags) {
            final String tagId = articleTag.getString(Keys.OBJECT_ID);
            final JSONObject result =
                    tagArticleRepository.getByTagId(tagId, 1, displayCnt);
            final JSONArray tagArticleRelations =
                    result.getJSONArray(Keys.RESULTS);

            final int relationSize = displayCnt < tagArticleRelations.length()
                                     ? displayCnt : tagArticleRelations.length();
            for (int i = 0; i < relationSize; i++) {
                final JSONObject tagArticleRelation =
                        tagArticleRelations.getJSONObject(i);
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

        Collections.sort(ret, new ArticleUpdateDateComparator());

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
