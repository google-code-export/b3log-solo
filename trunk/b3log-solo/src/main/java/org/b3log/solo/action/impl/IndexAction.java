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

import org.b3log.latke.client.action.ActionException;
import org.b3log.latke.client.action.AbstractAction;
import com.google.inject.Inject;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.b3log.solo.action.util.Filler;
import org.b3log.latke.model.Pagination;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.util.Locales;
import org.b3log.solo.model.Common;
import org.b3log.solo.model.Preference;
import org.b3log.solo.util.Preferences;
import org.b3log.solo.util.Statistics;
import org.json.JSONObject;

/**
 * Index action. index.html.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.6, Aug 21, 2010
 */
public final class IndexAction extends AbstractAction {

    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(IndexAction.class);
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
     * Statistic utilities.
     */
    @Inject
    private Statistics statistics;
    /**
     * Preference utilities.
     */
    @Inject
    private Preferences preferences;

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

            final Locale locale = Locales.getLocale(request);
            Locales.setLocale(request, locale);

            final Map<String, String> langs = langPropsService.getAll(locale);
            ret.putAll(langs);

            filler.fillIndexArticles(ret, currentPageNum);
            filler.fillSide(ret);
            filler.fillBlogHeader(ret, request);
            filler.fillStatistic(ret);
            filler.fillArchiveDates(ret);
            ret.put(Common.ACTION_NAME, Common.INDEX);

            final String skinName = preferences.getPreference().
                    getString(Preference.SKIN_NAME);
            ret.put(Preference.SKIN_NAME, skinName);
            
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
