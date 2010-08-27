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
package org.b3log.solo.repository.impl;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import java.util.Map;
import org.apache.log4j.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.gae.AbstractGAERepository;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.BlogSync;
import org.b3log.solo.repository.CSDNBlogArticleSoloArticleRepository;
import org.json.JSONObject;

/**
 * CSDN blog article-Solo article Google App Engine repository.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Aug 27, 2010
 */
public class CSDNBlogArticleSoloArticleGAERepository
        extends AbstractGAERepository
        implements CSDNBlogArticleSoloArticleRepository {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(CSDNBlogArticleSoloArticleGAERepository.class);

    @Override
    public String getName() {
        return BlogSync.BLOG_SYNC_CSDN_BLOG_ARTICLE + "_" + Article.ARTICLE;
    }

    @Override
    public String getSoloArticleId(final String csdnBlogArticleId)
            throws RepositoryException {
        final Query query = new Query(getName());
        query.addFilter(BlogSync.BLOG_SYNC_CSDN_BLOG_ARTICLE_ID,
                        Query.FilterOperator.EQUAL,
                        csdnBlogArticleId);
        final PreparedQuery preparedQuery =
                getDatastoreService().prepare(query);
        final Entity entity = preparedQuery.asSingleEntity();

        if (null == entity) {
            return null;
        }

        final Map<String, Object> properties = entity.getProperties();

        return (String) properties.get(Article.ARTICLE + "_" + Keys.OBJECT_ID);
    }

    @Override
    public String getCSDNBlogArticleId(final String soloArticleId)
            throws RepositoryException {
        final Query query = new Query(getName());
        query.addFilter(Article.ARTICLE + "_" + Keys.OBJECT_ID,
                        Query.FilterOperator.EQUAL, soloArticleId);
        final PreparedQuery preparedQuery =
                getDatastoreService().prepare(query);
        final Entity entity = preparedQuery.asSingleEntity();

        if (null == entity) {
            return null;
        }

        final Map<String, Object> properties = entity.getProperties();

        return (String) properties.get(BlogSync.BLOG_SYNC_CSDN_BLOG_ARTICLE_ID);
    }

    @Override
    public JSONObject getByCSDNBlogArticleId(final String csdnBlogArticleId)
            throws RepositoryException {
        final Query query = new Query(getName());
        query.addFilter(BlogSync.BLOG_SYNC_CSDN_BLOG_ARTICLE_ID,
                        Query.FilterOperator.EQUAL,
                        csdnBlogArticleId);
        final PreparedQuery preparedQuery =
                getDatastoreService().prepare(query);
        final Entity entity = preparedQuery.asSingleEntity();

        if (null == entity) {
            return null;
        }

        return entity2JSONObject(entity);
    }

    @Override
    public JSONObject getBySoloArticleId(final String soloArticleId)
            throws RepositoryException {
        final Query query = new Query(getName());
        query.addFilter(Article.ARTICLE + "_" + Keys.OBJECT_ID,
                        Query.FilterOperator.EQUAL, soloArticleId);
        final PreparedQuery preparedQuery =
                getDatastoreService().prepare(query);
        final Entity entity = preparedQuery.asSingleEntity();

        if (null == entity) {
            return null;
        }

        return entity2JSONObject(entity);
    }
}
