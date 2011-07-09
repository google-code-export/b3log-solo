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
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.solo.action.util.Filler;
import org.b3log.solo.util.Articles;
import org.b3log.solo.model.Article;
import org.b3log.solo.repository.TagArticleRepository;
import org.b3log.solo.repository.TagRepository;
import org.b3log.solo.repository.impl.ArticleGAERepository;
import org.b3log.latke.action.util.Paginator;
import org.b3log.latke.model.Pagination;
import org.b3log.latke.model.User;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.util.Locales;
import org.b3log.latke.util.Strings;
import org.b3log.solo.model.Common;
import org.b3log.solo.model.PageTypes;
import org.b3log.solo.model.Preference;
import org.b3log.solo.model.Tag;
import org.b3log.solo.repository.ArticleRepository;
import org.b3log.solo.repository.impl.TagArticleGAERepository;
import org.b3log.solo.repository.impl.TagGAERepository;
import org.b3log.solo.util.comparator.Comparators;
import org.b3log.solo.util.Preferences;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Get articles by tag action.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.2.4, Jul 1, 2011
 */
public final class TagArticlesAction extends AbstractFrontPageAction {

    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(TagArticlesAction.class.getName());
    /**
     * Article repository.
     */
    private ArticleRepository articleRepository =
            ArticleGAERepository.getInstance();
    /**
     * Tag repository.
     */
    private TagRepository tagRepository = TagGAERepository.getInstance();
    /**
     * Tag-Article repository.
     */
    private TagArticleRepository tagArticleRepository =
            TagArticleGAERepository.getInstance();
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

        JSONObject tag = null;

        try {
            final String requestURI = request.getRequestURI();
            String tagTitle = getTagTitle(requestURI);
            final int currentPageNum = getCurrentPageNum(requestURI, tagTitle);
            LOGGER.log(Level.FINER, "Tag[title={0}, currentPageNum={1}]",
                       new Object[]{tagTitle, currentPageNum});

            tagTitle = URLDecoder.decode(tagTitle, "UTF-8");
            tag = tagRepository.getByTitle(tagTitle);

            if (null == tag) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);

                return ret;
            }

            final String tagId = tag.getString(Keys.OBJECT_ID);

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

            final int pageSize = preference.getInt(
                    Preference.ARTICLE_LIST_DISPLAY_COUNT);
            final int windowSize = preference.getInt(
                    Preference.ARTICLE_LIST_PAGINATION_WINDOW_SIZE);

            request.setAttribute(CACHED_OID, tagId);
            request.setAttribute(
                    CACHED_TITLE,
                    langs.get(PageTypes.TAG_ARTICLES) + "  ["
                    + langs.get("pageNumLabel") + "=" + currentPageNum + ", "
                    + langs.get("tagLabel") + "=" + tagTitle + "]");
            request.setAttribute(CACHED_TYPE, langs.get(PageTypes.TAG_ARTICLES));

            final JSONObject result =
                    tagArticleRepository.getByTagId(tagId,
                                                    currentPageNum,
                                                    pageSize);
            final int pageCount = result.getJSONObject(
                    Pagination.PAGINATION).getInt(
                    Pagination.PAGINATION_PAGE_COUNT);
            if (0 == pageCount) {
                try {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);

                    return ret;
                } catch (final IOException ex) {
                    LOGGER.severe(ex.getMessage());
                }
            }

            final JSONArray tagArticleRelations =
                    result.getJSONArray(Keys.RESULTS);
            final List<JSONObject> articles = new ArrayList<JSONObject>();
            for (int i = 0; i < tagArticleRelations.length(); i++) {
                final JSONObject tagArticleRelation =
                        tagArticleRelations.getJSONObject(i);
                final String articleId =
                        tagArticleRelation.getString(Article.ARTICLE + "_"
                                                     + Keys.OBJECT_ID);
                final JSONObject article = articleRepository.get(articleId);
                if (!article.getBoolean(Article.ARTICLE_IS_PUBLISHED)) {
                    continue;
                }
                // Puts author name
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

                articles.add(article);
            }

            LOGGER.log(Level.FINEST,
                       "Paginate tag-articles[currentPageNum={0}, pageSize={1}, pageCount={2}, windowSize={3}]",
                       new Object[]{currentPageNum,
                                    pageSize,
                                    pageCount,
                                    windowSize});
            final List<Integer> pageNums =
                    Paginator.paginate(currentPageNum, pageSize, pageCount,
                                       windowSize);

            LOGGER.log(Level.FINEST, "tag-articles[pageNums={0}]", pageNums);

            if (preference.getBoolean(Preference.ENABLE_ARTICLE_UPDATE_HINT)) {
                Collections.sort(articles,
                                 Comparators.ARTICLE_UPDATE_DATE_COMPARATOR);
            } else {
                Collections.sort(articles,
                                 Comparators.ARTICLE_CREATE_DATE_COMPARATOR);
            }
            ret.put(Article.ARTICLES, articles);
            ret.put(Pagination.PAGINATION_CURRENT_PAGE_NUM, currentPageNum);
            ret.put(Pagination.PAGINATION_FIRST_PAGE_NUM, pageNums.get(0));
            ret.put(Pagination.PAGINATION_LAST_PAGE_NUM,
                    pageNums.get(pageNums.size() - 1));
            ret.put(Pagination.PAGINATION_PAGE_COUNT, pageCount);
            ret.put(Pagination.PAGINATION_PAGE_NUMS, pageNums);
            ret.put(Common.PATH, "/tags/" + tagTitle);
            ret.put(Keys.OBJECT_ID, tagId);
            ret.put(Tag.TAG, tag);

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

    @Override
    protected JSONObject doAjaxAction(final JSONObject data,
                                      final HttpServletRequest request,
                                      final HttpServletResponse response)
            throws ActionException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected String getTemplateName(final String requestURI) {
        return "tag-articles.ftl";
    }

    /**
     * Gets tag title from the specified URI.
     * 
     * @param requestURI the specified request URI
     * @return tag title
     */
    private static String getTagTitle(final String requestURI) {
        final String path = requestURI.substring("/tags/".length());

        if (path.contains("/")) {
            return path.substring(0, path.indexOf("/"));
        } else {
            return path.substring(0);
        }
    }

    /**
     * Gets the request page number from the specified request URI and tag title.
     * 
     * @param requestURI the specified request URI
     * @param tagTitle the specified tag title
     * @return page number
     */
    private static int getCurrentPageNum(final String requestURI,
                                         final String tagTitle) {
        if (!requestURI.endsWith("/")) {
            return 1;
        }

        final String ret = requestURI.substring(("/tags/" + tagTitle + "/").
                length());

        if (Strings.isNumeric(ret)) {
            return Integer.valueOf(ret);
        } else {
            return 1;
        }
    }
}
