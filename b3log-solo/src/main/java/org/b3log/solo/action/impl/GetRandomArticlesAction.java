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

import com.google.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.latke.Keys;
import org.b3log.latke.action.AbstractAction;
import org.b3log.latke.action.ActionException;
import static org.b3log.solo.model.Article.*;
import org.b3log.solo.model.Common;
import org.b3log.solo.model.Preference;
import org.b3log.solo.repository.ArticleRepository;
import org.b3log.solo.repository.impl.ArticleGAERepository;
import org.b3log.solo.util.Preferences;
import org.json.JSONObject;

/**
 * Gets random articles action.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.2, Jan 11, 2011
 */
public final class GetRandomArticlesAction extends AbstractAction {

    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(GetRandomArticlesAction.class.getName());
    /**
     * Article repository.
     */
    private ArticleRepository articleRepository =
            ArticleGAERepository.getInstance();
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
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected JSONObject doAjaxAction(final JSONObject data,
                                      final HttpServletRequest request,
                                      final HttpServletResponse response)
            throws ActionException {
        final List<JSONObject> randomArticles = getRandomArticles();
        final JSONObject ret = new JSONObject();

        try {
            ret.put(Common.RANDOM_ARTICLES, randomArticles);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

        return ret;
    }

    /**
     * Gets the random articles.
     *
     * @return a list of articles, returns an empty list if not found
     * @throws ActionException action exception
     */
    public List<JSONObject> getRandomArticles() throws ActionException {
        try {
            final JSONObject preference = preferenceUtils.getPreference();
            if (null == preference) {
                throw new ActionException("Not found preference");
            }

            final int displayCnt =
                    preference.getInt(Preference.RANDOM_ARTICLES_DISPLAY_CNT);
            final List<JSONObject> ret =
                    articleRepository.getRandomly(displayCnt);

            // Remove unused properties
            for (final JSONObject article : ret) {
                article.remove(Keys.OBJECT_ID);
                article.remove(ARTICLE_AUTHOR_EMAIL);
                article.remove(ARTICLE_ABSTRACT);
                article.remove(ARTICLE_COMMENT_COUNT);
                article.remove(ARTICLE_CONTENT);
                article.remove(ARTICLE_CREATE_DATE);
                article.remove(ARTICLE_TAGS_REF);
                article.remove(ARTICLE_UPDATE_DATE);
                article.remove(ARTICLE_VIEW_COUNT);
                article.remove(ARTICLE_RANDOM_DOUBLE);
            }

            return ret;
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new ActionException(e);
        }
    }
}
