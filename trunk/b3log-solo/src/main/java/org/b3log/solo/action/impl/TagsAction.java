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

import java.util.List;
import org.b3log.latke.action.ActionException;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.solo.action.util.Filler;
import org.b3log.solo.model.Tag;
import org.b3log.solo.repository.TagRepository;
import org.b3log.latke.Keys;
import org.b3log.latke.repository.Query;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.util.CollectionUtils;
import org.b3log.latke.util.Locales;
import org.b3log.solo.model.PageTypes;
import org.b3log.solo.model.Preference;
import org.b3log.solo.repository.impl.TagGAERepository;
import org.b3log.solo.util.Preferences;
import org.b3log.solo.util.Tags;
import org.b3log.solo.util.comparator.Comparators;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Tag action.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.1.5, Aug 2, 2011
 */
public final class TagsAction extends AbstractFrontPageAction {

    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(TagsAction.class.getName());
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
     * Preference utilities.
     */
    private Preferences preferenceUtils = Preferences.getInstance();
    /**
     * Tag utilities.
     */
    private Tags tagUtils = Tags.getInstance();

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
            request.setAttribute(CACHED_OID, "No id");
            request.setAttribute(CACHED_TITLE, langs.get(PageTypes.ALL_TAGS));
            request.setAttribute(CACHED_TYPE, langs.get(PageTypes.ALL_TAGS));
            request.setAttribute(CACHED_LINK, "/tags.html");

            final JSONObject result = tagRepository.get(new Query());
            final JSONArray tagArray = result.getJSONArray(Keys.RESULTS);

            final List<JSONObject> tags =
                    CollectionUtils.jsonArrayToList(tagArray);
            tagUtils.removeForUnpublishedArticles(tags);
            Collections.sort(tags, Comparators.TAG_REF_CNT_COMPARATOR);

            ret.put(Tag.TAGS, tags);

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
        return "tags.ftl";
    }
}