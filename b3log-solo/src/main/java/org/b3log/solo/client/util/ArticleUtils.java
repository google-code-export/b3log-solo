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

package org.b3log.solo.client.util;

import com.google.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.Tag;
import org.b3log.solo.repository.ArticleCommentRepository;
import org.b3log.solo.repository.TagArticleRepository;
import org.b3log.solo.repository.TagRepository;
import org.b3log.latke.Keys;
import org.b3log.latke.repository.RepositoryException;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Article utilities.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Aug 13, 2010
 */
public final class ArticleUtils {

    /**
     * Article-Comment repository.
     */
    @Inject
    private ArticleCommentRepository articleCommentRepository;
    /**
     * Tag-Article repository.
     */
    @Inject
    private TagArticleRepository tagArticleRepository;
    /**
     * Tag repository.
     */
    @Inject
    private TagRepository tagRepository;

    /**
     * Adds tags for every article of the specified articles.
     *
     * @param articles the specified articles
     * @throws RepositoryException repository exception
     * @throws JSONException json exception
     */
    public void addTags(final List<JSONObject> articles)
            throws RepositoryException, JSONException {
        for (final JSONObject article : articles) {
            final String articleId = article.getString(Keys.OBJECT_ID);
            final List<JSONObject> tagArticleRelations =
                    tagArticleRepository.getByArticleId(articleId);

            final List<JSONObject> tags = new ArrayList<JSONObject>();
            for (int i = 0; i < tagArticleRelations.size(); i++) {
                final JSONObject tagArticleRelation =
                        tagArticleRelations.get(i);
                final String tagId =
                        tagArticleRelation.getString(Tag.TAG + "_"
                                                     + Keys.OBJECT_ID);
                final JSONObject tag = tagRepository.get(tagId);
                tags.add(tag);
            }

            article.put(Article.ARTICLE_TAGS_REF,
                        /* Avoid convert to JSONArray, which FreeMarker can't
                         * process in <#list/> */
                        (Object) tags);
        }
    }
}
