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

package org.b3log.solo.util;

import com.google.inject.Inject;
import java.net.URLDecoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.util.Strings;
import org.b3log.solo.action.impl.TagsAction;
import org.b3log.solo.repository.ArticleRepository;
import org.b3log.solo.repository.PageRepository;
import org.b3log.solo.repository.TagRepository;
import org.json.JSONObject;

/**
 * Page cache key utilities.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.4, Dec 3, 2010
 */
public final class PageCacheKeys {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(PageCacheKeys.class.getName());
    /**
     * Page repository.
     */
    @Inject
    private PageRepository pageRepository;
    /**
     * Article repository.
     */
    @Inject
    private ArticleRepository articleRepository;
    /**
     * Tag repository.
     */
    @Inject
    private TagRepository tagRepository;
    /**
     * Article utilities.
     */
    @Inject
    private ArticleUtils articleUtils;

    /**
     * Gets page cache key by the specified URI and query string.
     *
     * @param uri the specified URI
     * @param queryString the specified query string
     * @return cache key
     */
    // XXX: Performance issue
    public String getPageCacheKey(final String uri,
                                  final String queryString) {
        String ret = null;

        try {
            if ("/tags.html".equals(uri) || "/tags.do".equals(uri)) {
                return TagsAction.CACHE_KEY;
            }

            final JSONObject article = articleRepository.getByPermalink(uri);
            if (null != article) {
                final String articleId = article.getString(Keys.OBJECT_ID);
                ret = "/article-detail.do?oId="
                      + articleId;
                incArticleViewCount(articleId);

                return ret;
            }

            if (uri.startsWith("/tags/")) {
                String tagTitle = uri.substring("/tags/".length());

                try {
                    tagTitle = URLDecoder.decode(tagTitle, "UTF-8");
                    LOGGER.log(Level.FINER, "Tag[title={0}]", tagTitle);
                } catch (final Exception e) {
                    LOGGER.warning(e.getMessage());
                }
                final JSONObject tag = tagRepository.getByTitle(tagTitle);
                if (null != tag) {
                    ret = "/tag-articles.do?oId="
                          + tag.getString(Keys.OBJECT_ID);

                    return ret;
                }
            }

            final JSONObject page = pageRepository.getByPermalink(uri);
            if (null != page) {
                ret = "/page.do?oId=" + page.getString(Keys.OBJECT_ID);

                return ret;
            }

            ret = uri;
            if (!Strings.isEmptyOrNull(queryString)) {
                ret += "?" + queryString;

                return ret;
            }
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

        return ret;
    }

    /**
     * View count +1 for an article specified by the given article id.
     * .
     * @param articleId the given article id
     */
    private void incArticleViewCount(final String articleId) {
        final Transaction transaction = articleRepository.beginTransaction();
        try {
            articleUtils.incArticleViewCount(articleId);
            transaction.commit();
        } catch (final Exception e) {
            transaction.rollback();
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }
}
