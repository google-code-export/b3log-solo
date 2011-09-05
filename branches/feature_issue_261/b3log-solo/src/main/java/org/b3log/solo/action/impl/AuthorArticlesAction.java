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

import java.util.Iterator;
import java.util.logging.Level;
import org.b3log.latke.Keys;
import org.b3log.latke.action.ActionException;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.solo.action.util.Filler;
import org.b3log.solo.model.Article;
import org.b3log.solo.repository.impl.ArticleGAERepository;
import org.b3log.latke.action.util.Paginator;
import org.b3log.latke.model.Pagination;
import org.b3log.latke.model.User;
import org.b3log.latke.service.LangPropsService;
import org.b3log.solo.action.util.Requests;
import org.b3log.solo.model.Common;
import org.b3log.solo.model.PageTypes;
import org.b3log.solo.model.Preference;
import org.b3log.solo.repository.ArticleRepository;
import org.b3log.solo.repository.UserRepository;
import org.b3log.solo.repository.impl.UserGAERepository;
import org.b3log.solo.util.comparator.Comparators;
import org.b3log.solo.util.Preferences;
import org.b3log.solo.util.Skins;
import org.json.JSONObject;

/**
 * Get articles by author action.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.2.6, Sep 3, 2011
 */
public final class AuthorArticlesAction extends AbstractFrontPageAction {

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
    private ArticleRepository articleRepository =
            ArticleGAERepository.getInstance();
    /**
     * User repository.
     */
    private UserRepository userRepository = UserGAERepository.getInstance();
    /**
     * Filler.
     */
    private Filler filler = Filler.getInstance();
    /**
     * Language service.
     */
    private LangPropsService langPropsService = LangPropsService.getInstance();
    /**
     * Preference utilities.
     */
    private Preferences preferenceUtils = Preferences.getInstance();
    /**
     * Skin utilities.
     */
    private Skins skins = Skins.getInstance();

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
            final String authorId = getAuthorId(requestURI);

            LOGGER.log(Level.FINER,
                       "Request author articles[requestURI={0}, authorId={1}]",
                       new Object[]{requestURI, authorId});

            final int currentPageNum = getCurrentPageNum(requestURI, authorId);
            if (-1 == currentPageNum) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return ret;
            }

            LOGGER.log(Level.FINER,
                       "Request author articles[authorId={0}, currentPageNum={1}]",
                       new Object[]{authorId, currentPageNum});

            final JSONObject preference = preferenceUtils.getPreference();
            if (null == preference) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);

                return ret;
            }

            skins.fillLanguage(preference, ret);

            final int pageSize = preference.getInt(
                    Preference.ARTICLE_LIST_DISPLAY_COUNT);
            final int windowSize = preference.getInt(
                    Preference.ARTICLE_LIST_PAGINATION_WINDOW_SIZE);

            final JSONObject author = userRepository.get(authorId);

            request.setAttribute(CACHED_TYPE,
                                 ret.get(PageTypes.AUTHOR_ARTICLES));
            request.setAttribute(CACHED_OID, "No id");
            request.setAttribute(
                    CACHED_TITLE,
                    ret.get(PageTypes.AUTHOR_ARTICLES) + "  ["
                    + ret.get("pageNumLabel") + "=" + currentPageNum + ", "
                    + ret.get("authorLabel") + "=" + author.getString(
                    User.USER_NAME) + "]");
            request.setAttribute(CACHED_LINK, requestURI);

            final String authorEmail = author.getString(User.USER_EMAIL);
            final JSONObject result =
                    articleRepository.getByAuthorEmail(authorEmail,
                                                       currentPageNum,
                                                       pageSize);
            final List<JSONObject> articles =
                    org.b3log.latke.util.CollectionUtils.jsonArrayToList(result.
                    getJSONArray(Keys.RESULTS));
            final Iterator<JSONObject> iterator = articles.iterator();
            while (iterator.hasNext()) {
                final JSONObject article = iterator.next();
                if (!article.getBoolean(Article.ARTICLE_IS_PUBLISHED)) {  // Skips the unpublished article
                    iterator.remove();
                }
            }

            if (articles.isEmpty()) {
                try {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);

                    return ret;
                } catch (final IOException ex) {
                    LOGGER.severe(ex.getMessage());
                }
            }

            final int pageCount = result.getJSONObject(
                    Pagination.PAGINATION).getInt(
                    Pagination.PAGINATION_PAGE_COUNT);
            final List<Integer> pageNums =
                    Paginator.paginate(currentPageNum, pageSize, pageCount,
                                       windowSize);

            if (0 != pageNums.size()) {
                ret.put(Pagination.PAGINATION_FIRST_PAGE_NUM,
                        pageNums.get(0));
                ret.put(Pagination.PAGINATION_LAST_PAGE_NUM,
                        pageNums.get(pageNums.size() - 1));
            }
            ret.put(Pagination.PAGINATION_PAGE_COUNT, pageCount);
            ret.put(Pagination.PAGINATION_PAGE_NUMS, pageNums);

            ret.put(Pagination.PAGINATION_CURRENT_PAGE_NUM, currentPageNum);
            final String previousPageNum =
                    Integer.toString(currentPageNum > 1 ? currentPageNum - 1
                                     : 0);
            ret.put(Pagination.PAGINATION_PREVIOUS_PAGE_NUM,
                    "0".equals(previousPageNum) ? "" : previousPageNum);
            if (pageCount == currentPageNum + 1) { // The next page is the last page
                ret.put(Pagination.PAGINATION_NEXT_PAGE_NUM, "");
            } else {
                ret.put(Pagination.PAGINATION_NEXT_PAGE_NUM, currentPageNum
                                                             + 1);
            }

            filler.setArticlesExProperties(articles, preference);

            if (preference.getBoolean(Preference.ENABLE_ARTICLE_UPDATE_HINT)) {
                Collections.sort(articles,
                                 Comparators.ARTICLE_UPDATE_DATE_COMPARATOR);
            } else {
                Collections.sort(articles,
                                 Comparators.ARTICLE_CREATE_DATE_COMPARATOR);
            }
            ret.put(Article.ARTICLES, articles);
            ret.put(Common.PATH, "/authors/" + authorId);
            ret.put(Keys.OBJECT_ID, authorId);

            final String authorName = author.getString(User.USER_NAME);
            ret.put(Common.AUTHOR_NAME, authorName);
            ret.put(Pagination.PAGINATION_CURRENT_PAGE_NUM, currentPageNum);
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
        return "author-articles.ftl";
    }

    /**
     * Gets author id from the specified URI.
     * 
     * @param requestURI the specified request URI
     * @return author id
     */
    private static String getAuthorId(final String requestURI) {
        final String path = requestURI.substring("/authors/".length());

        final int idx = path.indexOf("/");
        if (-1 == idx) {
            return path.substring(0);
        } else {
            return path.substring(0, idx);
        }
    }

    /**
     * Gets the request page number from the specified request URI and author id.
     * 
     * @param requestURI the specified request URI
     * @param authorId the specified author id
     * @return page number
     */
    private static int getCurrentPageNum(final String requestURI,
                                         final String authorId) {
        final String pageNumString =
                requestURI.substring(("/authors/" + authorId + "/").length());

        return Requests.getCurrentPageNum(pageNumString);
    }
}
