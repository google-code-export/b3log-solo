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
import java.io.IOException;
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
import org.b3log.solo.util.Articles;
import org.b3log.solo.model.Article;
import org.b3log.solo.repository.impl.ArticleGAERepository;
import org.b3log.latke.action.util.Paginator;
import org.b3log.latke.model.Pagination;
import org.b3log.latke.model.User;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.util.CollectionUtils;
import org.b3log.latke.util.Locales;
import org.b3log.latke.util.Strings;
import org.b3log.solo.model.Common;
import org.b3log.solo.model.Preference;
import org.b3log.solo.model.Skin;
import org.b3log.solo.model.Tag;
import org.b3log.solo.repository.UserRepository;
import org.b3log.solo.util.comparator.ArticleUpdateDateComparator;
import org.b3log.solo.util.Preferences;
import org.b3log.solo.util.comparator.ArticleCreateDateComparator;
import org.json.JSONObject;

/**
 * Get articles by author action. author-articles.ftl.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.1.3, Dec 13, 2010
 */
public final class AuthorArticlesAction extends AbstractCacheablePageAction {

    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(AuthorArticlesAction.class.getName());
    /**
     * Article repository.
     */
    @Inject
    private ArticleGAERepository articleRepository;
    /**
     * User repository.
     */
    @Inject
    private UserRepository userRepository;
    /**
     * Filler.
     */
    @Inject
    private Filler filler;
    /**
     * Article utilities.
     */
    @Inject
    private Articles articleUtils;
    /**
     * Language service.
     */
    @Inject
    private LangPropsService langPropsService;
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
            final String authorId =
                    queryStringJSONObject.optString(Keys.OBJECT_ID);
            if (Strings.isEmptyOrNull(authorId)) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);

                return ret;
            }

            final int currentPageNum = queryStringJSONObject.optInt(
                    Pagination.PAGINATION_CURRENT_PAGE_NUM, 1);
            final int pageSize = preference.getInt(
                    Preference.ARTICLE_LIST_DISPLAY_COUNT);
            final int windowSize = preference.getInt(
                    Preference.ARTICLE_LIST_PAGINATION_WINDOW_SIZE);

            final JSONObject author = userRepository.get(authorId);
            final String authorEmail = author.getString(User.USER_EMAIL);
            final JSONObject result =
                    articleRepository.getByAuthorEmail(authorEmail,
                                                       currentPageNum,
                                                       pageSize);
            final int pageCount = result.getJSONObject(
                    Pagination.PAGINATION).getInt(
                    Pagination.PAGINATION_PAGE_COUNT);
            final List<JSONObject> articles = CollectionUtils.jsonArrayToList(
                    result.getJSONArray(Keys.RESULTS));

            final List<Integer> pageNums =
                    Paginator.paginate(currentPageNum, pageSize, pageCount,
                                       windowSize);

            LOGGER.log(Level.FINEST, "tag-articles[pageNums={0}]", pageNums);
            final JSONObject tag = userRepository.get(authorId);

            articleUtils.addTags(articles);
            if (preference.getBoolean(Preference.ENABLE_ARTICLE_UPDATE_HINT)) {
                Collections.sort(articles, new ArticleUpdateDateComparator());
            } else {
                Collections.sort(articles, new ArticleCreateDateComparator());
            }
            for (final JSONObject article : articles) {
                article.put(Common.HAS_UPDATED, articleUtils.hasUpdated(article));
            }
            ret.put(Article.ARTICLES, articles);

            ret.put(Pagination.PAGINATION_FIRST_PAGE_NUM, pageNums.get(0));
            ret.put(Pagination.PAGINATION_LAST_PAGE_NUM,
                    pageNums.get(pageNums.size() - 1));
            ret.put(Pagination.PAGINATION_PAGE_COUNT, pageCount);
            ret.put(Pagination.PAGINATION_PAGE_NUMS, pageNums);
            ret.put(Common.ACTION_NAME, Common.TAG_ARTICLES);
            ret.put(Keys.OBJECT_ID, authorId);
            ret.put(Tag.TAG, tag);
            final String skinDirName = preference.getString(Skin.SKIN_DIR_NAME);
            ret.put(Skin.SKIN_DIR_NAME, skinDirName);

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

    @Override
    protected JSONObject doAjaxAction(final JSONObject data,
                                      final HttpServletRequest request,
                                      final HttpServletResponse response)
            throws ActionException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
