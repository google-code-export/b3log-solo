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

import org.b3log.latke.action.ActionException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.solo.action.util.Filler;
import org.b3log.latke.model.Pagination;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.util.Locales;
import org.b3log.latke.util.Strings;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.Common;
import org.b3log.solo.model.PageTypes;
import org.b3log.solo.model.Preference;
import org.b3log.solo.model.Skin;
import org.b3log.solo.util.Preferences;
import org.json.JSONObject;

/**
 * Index action.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.1.3, Jul 1, 2011
 */
public final class IndexAction extends AbstractFrontPageAction {

    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(IndexAction.class.getName());
    /**
     * Language service.
     */
    private LangPropsService langPropsService = LangPropsService.getInstance();
    /**
     * Filler.
     */
    private Filler filler = Filler.getInstance();
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

        final String requestURI = request.getRequestURI();
        try {
            final int currentPageNum = getCurrentPageNum(requestURI);
            if (-1 == currentPageNum) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return ret;
            }

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
            request.setAttribute(CACHED_OID, "No id");
            request.setAttribute(CACHED_TITLE,
                                 langs.get(PageTypes.INDEX_ARTICLES)
                                 + "  [" + langs.get("pageNumLabel") + "="
                                 + currentPageNum + "]");
            request.setAttribute(CACHED_TYPE,
                                 langs.get(PageTypes.INDEX_ARTICLES));

            if ("valentine".equals(preference.getString(Skin.SKIN_DIR_NAME))) {
                final JSONObject queryStringJSONObject =
                        getQueryStringJSONObject(request);
                final int leftCurrentPageNum = queryStringJSONObject.optInt(
                        Pagination.PAGINATION_CURRENT_PAGE_NUM
                        + Common.LEFT_PART_NAME, 1);
                final int rightCurrentPageNum = queryStringJSONObject.optInt(
                        Pagination.PAGINATION_CURRENT_PAGE_NUM
                        + Common.RIGHT_PART_NAME, 1);
                filler.fillIndexArticlesForValentine(ret, leftCurrentPageNum,
                                                     rightCurrentPageNum,
                                                     preference);
            } else {
                filler.fillIndexArticles(ret, currentPageNum, preference);
            }

            @SuppressWarnings("unchecked")
            final List<JSONObject> articles =
                    (List<JSONObject>) ret.get(Article.ARTICLES);
            if (articles.isEmpty()) {
                try {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);

                    return ret;
                } catch (final IOException ex) {
                    LOGGER.severe(ex.getMessage());
                }
            }

            filler.fillSide(ret, preference);
            filler.fillBlogHeader(ret, preference);
            filler.fillBlogFooter(ret, preference);

            ret.put(Pagination.PAGINATION_CURRENT_PAGE_NUM, currentPageNum);
            final String previousPageNum =
                    Integer.toString(currentPageNum > 1 ? currentPageNum - 1
                                     : 0);
            ret.put(Pagination.PAGINATION_PREVIOUS_PAGE_NUM,
                    "0".equals(previousPageNum) ? "" : previousPageNum);
            final Integer pageCount =
                    (Integer) ret.get(Pagination.PAGINATION_PAGE_COUNT);
            if (null != pageCount) {
                if (pageCount == currentPageNum + 1) { // The next page is the last page
                    ret.put(Pagination.PAGINATION_NEXT_PAGE_NUM, "");
                } else {
                    ret.put(Pagination.PAGINATION_NEXT_PAGE_NUM, currentPageNum
                                                                 + 1);
                }
            }
            final String nextPageNum =
                    Integer.toString(currentPageNum > 1 ? currentPageNum - 1
                                     : 0);
            ret.put(Pagination.PAGINATION_PREVIOUS_PAGE_NUM,
                    "0".equals(nextPageNum) ? "" : nextPageNum);

            ret.put(Common.PATH, "");
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
    protected String getTemplateName(final String requestURI) {
        return "index.ftl";
    }

    @Override
    protected JSONObject doAjaxAction(final JSONObject data,
                                      final HttpServletRequest request,
                                      final HttpServletResponse response)
            throws ActionException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Gets the request page number from the specified request URI.
     * 
     * @param requestURI the specified request URI
     * @return page number, returns {@code -1} if the specified request URI
     * can not convert to an number
     */
    private static int getCurrentPageNum(final String requestURI) {
        final String pageNumString = requestURI.substring("/".length());

        if (Strings.isEmptyOrNull(pageNumString)) {
            return 1;
        }

        if (!Strings.isNumeric(pageNumString)) {
            return -1;
        }

        return Integer.valueOf(pageNumString);
    }
}
