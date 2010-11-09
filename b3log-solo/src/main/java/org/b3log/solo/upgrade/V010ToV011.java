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

package org.b3log.solo.upgrade;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.QueryResultList;
import com.google.appengine.api.datastore.Transaction;
import com.google.inject.Inject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.latke.Keys;
import org.b3log.latke.repository.gae.AbstractGAERepository;
import org.b3log.solo.SoloServletListener;
import org.b3log.solo.model.Article;
import org.b3log.solo.repository.ArticleRepository;
import org.json.JSONObject;

/**
 * Upgrader for <b>v010</b> to <b>v011</b>.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Oct 21, 2010
 */
public final class V010ToV011 extends HttpServlet {

    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(V010ToV011.class.getName());
    /**
     * Article repository.
     */
    @Inject
    private ArticleRepository articleRepository;
    /**
     * Update size in an request.
     */
    private static final int UPDATE_SIZE = 100;

    @Override
    protected void doGet(final HttpServletRequest request,
                         final HttpServletResponse response)
            throws ServletException, IOException {
        if ("0.1.1".equals(SoloServletListener.VERSION)) {
            LOGGER.info("Checking for consistency....");
            Transaction transaction = AbstractGAERepository.DATASTORE_SERVICE.
                    beginTransaction();
            boolean isConsistent = true;
            try {
                final Query query = new Query(Article.ARTICLE);
                final PreparedQuery preparedQuery =
                        AbstractGAERepository.DATASTORE_SERVICE.prepare(query);
                final QueryResultList<Entity> queryResultList =
                        preparedQuery.asQueryResultList(FetchOptions.Builder.
                        withDefaults());

                int cnt = 0;

                for (final Entity entity : queryResultList) {
                    if (!entity.hasProperty(Article.ARTICLE_PUT_TOP)) {
                        final JSONObject article =
                                AbstractGAERepository.entity2JSONObject(entity);
                        final String articleId = article.getString(
                                Keys.OBJECT_ID);
                        article.put(Article.ARTICLE_PUT_TOP, false);
                        articleRepository.update(articleId, article);

                        LOGGER.log(Level.INFO, "Updated article[oId={0}]",
                                   articleId);
                        isConsistent = false;
                        cnt++;
                    }

                    if (0 == cnt % UPDATE_SIZE) {
                        transaction.commit();
                        transaction = AbstractGAERepository.DATASTORE_SERVICE.
                                beginTransaction();
                    }
                }

                if (transaction.isActive()) {
                    transaction.commit();
                }
            } catch (final Exception e) {
                if (transaction.isActive()) {
                    transaction.rollback();
                }
                LOGGER.severe(e.getMessage());
                throw new ServletException("Upgrade fail from v010 to v011");
            }

            final PrintWriter writer = response.getWriter();
            final String partialUpgrade = "Upgrade from v010 to v011 partially, "
                                          + "please run this upgrader(visit this"
                                          + " URL) again.";
            final String upgraded = "Upgraded from v010 to v011 successfully :-)";
            if (!isConsistent) {
                LOGGER.info(partialUpgrade);
                writer.print(partialUpgrade);
            } else {
                LOGGER.info(upgraded);
                writer.print(upgraded);
            }

            writer.close();

            LOGGER.info("Checked for consistency");
        }
    }
}
