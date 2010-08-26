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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.b3log.latke.client.action.AbstractCacheablePageAction;
import org.b3log.solo.action.util.Filler;
import org.b3log.solo.util.ArticleUtils;
import org.b3log.solo.model.Article;
import org.b3log.solo.repository.impl.ArticleGAERepository;
import org.b3log.latke.client.action.util.Paginator;
import org.b3log.latke.model.Pagination;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.util.Locales;
import org.b3log.solo.model.Common;
import org.b3log.solo.model.Preference;
import org.b3log.solo.model.Skin;
import org.b3log.solo.repository.ArchiveDateArticleRepository;
import org.b3log.solo.servlet.SoloServletListener;
import org.b3log.solo.util.ArticleUpdateDateComparator;
import org.b3log.solo.util.Statistics;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Get articles by archive date. archive-articles.html.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Aug 26, 2010
 */
public final class ArchiveDateArticlesAction extends AbstractCacheablePageAction {

    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(ArchiveDateArticlesAction.class);
    /**
     * Article repository.
     */
    @Inject
    private ArticleGAERepository articleRepository;
    /**
     * Archive date-Article repository.
     */
    @Inject
    private ArchiveDateArticleRepository archiveDateArticleRepository;
    /**
     * Filler.
     */
    @Inject
    private Filler filler;
    /**
     * Article utilities.
     */
    @Inject
    private ArticleUtils articleUtils;
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
            final String archiveDateId =
                    queryStringJSONObject.getString(Keys.OBJECT_ID);
            final int currentPageNum = queryStringJSONObject.optInt(
                    Pagination.PAGINATION_CURRENT_PAGE_NUM, 1);

            final JSONObject preference =
                    SoloServletListener.getUserPreference();
            final int pageSize = preference.getInt(
                    Preference.ARTICLE_LIST_DISPLAY_COUNT);
            final int windowSize = preference.getInt(
                    Preference.ARTICLE_LIST_PAGINATION_WINDOW_SIZE);


            final JSONObject result =
                    archiveDateArticleRepository.getByArchiveDateId(
                    archiveDateId, currentPageNum, pageSize);
            final int pageCount = result.getJSONObject(
                    Pagination.PAGINATION).getInt(
                    Pagination.PAGINATION_PAGE_COUNT);
            final JSONArray archiveDateArticleRelations = result.getJSONArray(
                    Keys.RESULTS);
            final List<JSONObject> articles = new ArrayList<JSONObject>();
            for (int i = 0; i < archiveDateArticleRelations.length(); i++) {
                final JSONObject archiveDateArticleRelation =
                        archiveDateArticleRelations.getJSONObject(i);
                final String articleId =
                        archiveDateArticleRelation.getString(Article.ARTICLE
                        + "_"
                        + Keys.OBJECT_ID);
                final JSONObject article = articleRepository.get(articleId);
                articles.add(article);
            }

            Collections.sort(articles, new ArticleUpdateDateComparator());

            final List<Integer> pageNums =
                    Paginator.paginate(currentPageNum, pageSize, pageCount,
                                       windowSize);

            articleUtils.addTags(articles);
            ret.put(Article.ARTICLES, articles);
            ret.put(Pagination.PAGINATION_PAGE_COUNT, pageCount);
            ret.put(Pagination.PAGINATION_PAGE_NUMS, pageNums);
            ret.put(Common.ACTION_NAME, Common.ARCHIVED_DATE_ARTICLES);
            ret.put(Keys.OBJECT_ID, archiveDateId);
            final String skinDirName = preference.getString(Skin.SKIN_DIR_NAME);
            ret.put(Skin.SKIN_DIR_NAME, skinDirName);

            filler.fillSide(ret);
            filler.fillBlogHeader(ret, request);
            filler.fillArchiveDates(ret);

            statistics.incBlogViewCount();
        } catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new ActionException(e);
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
