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

import org.b3log.latke.Keys;
import org.b3log.latke.action.ActionException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.solo.action.util.Filler;
import org.b3log.solo.util.Articles;
import org.b3log.solo.model.Article;
import org.b3log.solo.repository.impl.ArticleGAERepository;
import org.b3log.latke.action.util.Paginator;
import org.b3log.latke.model.Pagination;
import org.b3log.latke.model.User;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.util.Dates;
import org.b3log.latke.util.Locales;
import org.b3log.latke.util.Strings;
import org.b3log.solo.action.util.Requests;
import org.b3log.solo.model.Common;
import org.b3log.solo.model.Preference;
import org.b3log.solo.repository.ArchiveDateArticleRepository;
import org.b3log.solo.model.ArchiveDate;
import org.b3log.solo.model.PageTypes;
import org.b3log.solo.repository.ArchiveDateRepository;
import org.b3log.solo.repository.ArticleRepository;
import org.b3log.solo.repository.impl.ArchiveDateArticleGAERepository;
import org.b3log.solo.repository.impl.ArchiveDateGAERepository;
import org.b3log.solo.util.comparator.Comparators;
import org.b3log.solo.util.Preferences;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Get articles by archive date.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.2.0, Jul 9, 2011
 */
public final class ArchiveArticlesAction extends AbstractFrontPageAction {

    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(ArchiveArticlesAction.class.getName());
    /**
     * Archive date repository.
     */
    private ArchiveDateRepository archiveDateRepository =
            ArchiveDateGAERepository.getInstance();
    /**
     * Article repository.
     */
    private ArticleRepository articleRepository =
            ArticleGAERepository.getInstance();
    /**
     * Archive date-Article repository.
     */
    private ArchiveDateArticleRepository archiveDateArticleRepository =
            ArchiveDateArticleGAERepository.getInstance();
    /**
     * Filler.
     */
    private Filler filler = Filler.getInstance();
    /**
     * Article utilities.
     */
    private Articles articleUtils = Articles.getInstance();
    /**
     * Language service.
     */
    private LangPropsService langPropsService = LangPropsService.getInstance();
    /**
     * Preference utilities.
     */
    private Preferences preferenceUtils = Preferences.getInstance();

    @Override
    protected Map<?, ?> doFreeMarkerAction(
            final freemarker.template.Template template,
            final HttpServletRequest request,
            final HttpServletResponse response) throws ActionException {
        final Map<String, Object> ret = new HashMap<String, Object>();

        try {
            String requestURI = request.getRequestURI();
            if (!requestURI.endsWith("/")) {
                requestURI += "/";
            }

            final String archiveDateString = getArchiveDate(requestURI);
            final int currentPageNum = getCurrentPageNum(requestURI);
            if (-1 == currentPageNum) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return ret;
            }

            LOGGER.log(Level.FINER,
                       "Request archive date[string={0}, currentPageNum={1}]",
                       new Object[]{archiveDateString});

            final JSONObject archiveDate =
                    archiveDateRepository.getByArchiveDate(archiveDateString);
            if (null == archiveDate) {
                LOGGER.log(Level.WARNING, "Can not find articles for the specified "
                                          + "archive date[string={0}]",
                           archiveDate);
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return ret;
            }

            final String archiveDateId = archiveDate.getString(Keys.OBJECT_ID);

            final JSONObject preference = preferenceUtils.getPreference();
            final String localeString = preference.getString(
                    Preference.LOCALE_STRING);
            final Locale locale = new Locale(
                    Locales.getLanguage(localeString),
                    Locales.getCountry(localeString));

            final Map<String, String> langs = langPropsService.getAll(locale);
            ret.putAll(langs);

            final int pageSize = preference.getInt(
                    Preference.ARTICLE_LIST_DISPLAY_COUNT);
            final int windowSize = preference.getInt(
                    Preference.ARTICLE_LIST_PAGINATION_WINDOW_SIZE);

            final JSONObject result =
                    archiveDateArticleRepository.getByArchiveDateId(
                    archiveDateId, currentPageNum, pageSize);

            @SuppressWarnings("unchecked")
            final JSONArray archiveDateArticleRelations = result.getJSONArray(
                    Keys.RESULTS);
            if (0 == archiveDateArticleRelations.length()) {
                try {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);

                    return ret;
                } catch (final IOException ex) {
                    LOGGER.severe(ex.getMessage());
                }
            }

            final List<JSONObject> articles = new ArrayList<JSONObject>();
            for (int i = 0; i < archiveDateArticleRelations.length(); i++) {
                final JSONObject archiveDateArticleRelation =
                        archiveDateArticleRelations.getJSONObject(i);
                final String articleId =
                        archiveDateArticleRelation.getString(Article.ARTICLE
                                                             + "_"
                                                             + Keys.OBJECT_ID);
                final JSONObject article = articleRepository.get(articleId);
                if (!article.getBoolean(Article.ARTICLE_IS_PUBLISHED)) {
                    continue;
                }

                fillExtraProperties(article, preference);

                articles.add(article);
            }

            final int pageCount = result.getJSONObject(
                    Pagination.PAGINATION).getInt(
                    Pagination.PAGINATION_PAGE_COUNT);
            final List<Integer> pageNums =
                    Paginator.paginate(currentPageNum, pageSize, pageCount,
                                       windowSize);

            sort(preference, articles);

            ret.put(Article.ARTICLES, articles);
            ret.put(Pagination.PAGINATION_CURRENT_PAGE_NUM, currentPageNum);
            ret.put(Pagination.PAGINATION_FIRST_PAGE_NUM, pageNums.get(0));
            ret.put(Pagination.PAGINATION_LAST_PAGE_NUM,
                    pageNums.get(pageNums.size() - 1));
            ret.put(Pagination.PAGINATION_PAGE_COUNT, pageCount);
            ret.put(Pagination.PAGINATION_PAGE_NUMS, pageNums);
            ret.put(Common.PATH, "/archives/" + archiveDateString);
            ret.put(Keys.OBJECT_ID, archiveDateId);

            filler.fillSide(ret, preference);
            filler.fillBlogHeader(ret, preference);
            filler.fillBlogFooter(ret, preference);

            final long time = archiveDate.getLong(ArchiveDate.ARCHIVE_TIME);
            final String dateString = ArchiveDate.DATE_FORMAT.format(time);
            final String[] dateStrings = dateString.split("/");
            final String year = dateStrings[0];
            final String month = dateStrings[1];
            archiveDate.put(ArchiveDate.ARCHIVE_DATE_YEAR, year);
            final String language = Locales.getLanguage(localeString);
            String cachedTitle = null;
            if ("en".equals(language)) {
                archiveDate.put(ArchiveDate.ARCHIVE_DATE_MONTH,
                                Dates.EN_MONTHS.get(month));
                cachedTitle = Dates.EN_MONTHS.get(month) + " " + year;
            } else {
                archiveDate.put(ArchiveDate.ARCHIVE_DATE_MONTH, month);
                cachedTitle = year + " " + langs.get("yearLabel") + " "
                              + month + " " + langs.get("monthLabel");
            }
            ret.put(ArchiveDate.ARCHIVE_DATE, archiveDate);

            request.setAttribute(CACHED_TYPE, langs.get(PageTypes.DATE_ARTICLES));
            request.setAttribute(CACHED_OID, archiveDateId);
            request.setAttribute(CACHED_TITLE,
                                 cachedTitle + "  [" + langs.get("pageNumLabel")
                                 + "=" + currentPageNum + "]");
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
     * Fills some extra properties into the specified article with the specified 
     * preference.
     * 
     * <p>
     * Some extra properties for the specified article:
     * <pre>
     * {
     *     ...., 
     *     "authorName": "",
     *     "authorId": "",
     *     "hasUpdated": boolean
     * }
     * </pre>
     * </p>
     * 
     * @param article the specified article
     * @param preference the specified preference
     * @throws JSONException json exception
     */
    private void fillExtraProperties(final JSONObject article,
                                     final JSONObject preference)
            throws JSONException {
        final JSONObject author = articleUtils.getAuthor(article);
        final String authorName = author.getString(User.USER_NAME);
        article.put(Common.AUTHOR_NAME, authorName);
        final String authorId = author.getString(Keys.OBJECT_ID);
        article.put(Common.AUTHOR_ID, authorId);

        if (preference.getBoolean(Preference.ENABLE_ARTICLE_UPDATE_HINT)) {
            article.put(Common.HAS_UPDATED,
                        articleUtils.hasUpdated(article));
        } else {
            article.put(Common.HAS_UPDATED, false);
        }
    }

    /**
     * Sorts the specified articles by the specified preference.
     * 
     * @param preference the specified preference
     * @param articles the specified articles
     * @throws JSONException json exception
     * @see Comparators#ARTICLE_UPDATE_DATE_COMPARATOR
     * @see Comparators#ARTICLE_CREATE_DATE_COMPARATOR
     */
    private void sort(final JSONObject preference,
                      final List<JSONObject> articles) throws JSONException {
        if (preference.getBoolean(Preference.ENABLE_ARTICLE_UPDATE_HINT)) {
            Collections.sort(articles,
                             Comparators.ARTICLE_UPDATE_DATE_COMPARATOR);
        } else {
            Collections.sort(articles,
                             Comparators.ARTICLE_CREATE_DATE_COMPARATOR);
        }
    }

    /**
     * Returns "archive-articles.ftl".
     * 
     * <p>
     * Ignores the specified request URI
     * </p>
     * 
     * @param requestURI the specified request URI
     * @return "archive-date-articles.ftl"
     */
    @Override
    protected String getTemplateName(final String requestURI) {
        return "archive-articles.ftl";
    }

    @Override
    protected JSONObject doAjaxAction(final JSONObject data,
                                      final HttpServletRequest request,
                                      final HttpServletResponse response)
            throws ActionException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Gets archive date from the specified URI.
     * 
     * @param requestURI the specified request URI
     * @return archive date
     */
    private static String getArchiveDate(final String requestURI) {
        final String path = requestURI.substring("/archives/".length());

        return path.substring(0, "yyyy/MM".length());
    }

    /**
     * Gets the request page number from the specified request URI.
     * 
     * @param requestURI the specified request URI
     * @return page number, returns {@code -1} if the specified request URI
     * can not convert to an number
     */
    private static int getCurrentPageNum(final String requestURI) {
        final String pageNumString = requestURI.substring("/archives/yyyy/MM/".
                length());
       
        return Requests.getCurrentPageNum(pageNumString);
    }
}
