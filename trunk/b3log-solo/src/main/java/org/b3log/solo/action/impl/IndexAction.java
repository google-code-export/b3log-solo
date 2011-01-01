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
import com.google.inject.Inject;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.latke.action.AbstractCacheablePageAction;
import org.b3log.solo.action.util.Filler;
import org.b3log.latke.model.Pagination;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.util.Locales;
import org.b3log.solo.model.Common;
import org.b3log.solo.model.Preference;
import org.b3log.solo.model.Skin;
import org.b3log.solo.util.Preferences;
import org.json.JSONObject;

/**
 * Index action. index.ftl.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.8, Dec 9, 2010
 */
public final class IndexAction extends AbstractCacheablePageAction {

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
    @Inject
    private LangPropsService langPropsService;
    /**
     * Filler.
     */
    @Inject
    private Filler filler;
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
            final JSONObject queryStringJSONObject =
                    getQueryStringJSONObject(request);
            final int currentPageNum = queryStringJSONObject.optInt(
                    Pagination.PAGINATION_CURRENT_PAGE_NUM, 1);

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

            if ("valentine".equals(preference.getString(Skin.SKIN_DIR_NAME))) {
                final int leftCurrentPageNum = queryStringJSONObject.optInt(
                        Pagination.PAGINATION_CURRENT_PAGE_NUM
                        + Common.LEFT_PART_NAME, 1);
                final int rightCurrentPageNum = queryStringJSONObject.optInt(
                        Pagination.PAGINATION_CURRENT_PAGE_NUM
                        + Common.RIGHT_PART_NAME, 1);
                filler.fillIndexArticlesForValentine(ret, leftCurrentPageNum,
                                                     rightCurrentPageNum);
            } else {
                filler.fillIndexArticles(ret, currentPageNum);
            }
            filler.fillSide(ret);
            filler.fillBlogHeader(ret);
            filler.fillBlogFooter(ret);
            ret.put(Common.ACTION_NAME, Common.INDEX);

            final String skinDirName = preference.getString(Skin.SKIN_DIR_NAME);
            ret.put(Skin.SKIN_DIR_NAME, skinDirName);
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
