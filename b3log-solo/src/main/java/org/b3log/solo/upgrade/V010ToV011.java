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
package org.b3log.solo.upgrade;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.latke.Keys;
import org.b3log.latke.repository.Query;
import org.b3log.latke.repository.Transaction;
import org.b3log.solo.SoloServletListener;
import org.b3log.solo.model.Article;
import org.b3log.solo.repository.ArticleRepository;
import org.b3log.solo.repository.impl.ArticleGAERepository;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Upgrader for <b>v010</b> to <b>v011</b>.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.4, Jan 12, 2011
 * @deprecated As of Solo 0.3.1, removes servlet URL mapping in web.xml, with 
 * no replacement.
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
    private ArticleRepository articleRepository =
            ArticleGAERepository.getInstance();
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
            Transaction transaction = articleRepository.beginTransaction();
            boolean isConsistent = true;
            try {
                final Query query = new Query();
                final JSONObject result = articleRepository.get(query);
                final JSONArray array = result.getJSONArray(Keys.RESULTS);

                int cnt = 0;

                for (int i = 0; i < array.length(); i++) {
                    final JSONObject article = array.getJSONObject(i);
                    if (!article.has(Article.ARTICLE_PUT_TOP)) {
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
                        transaction = articleRepository.beginTransaction();
                    }
                }

                if (transaction.isActive()) {
                    transaction.commit();
                }
            } catch (final Exception e) {
                if (transaction.isActive()) {
                    transaction.rollback();
                }
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
                throw new ServletException("Upgrade fail from v010 to v011");
            }

            final PrintWriter writer = response.getWriter();
            final String partialUpgrade = "Upgrade from v010 to v011 partially, "
                                          + "please run this upgrader(visit this"
                                          + " URL) again.";
            final String upgraded =
                    "Upgraded from v010 to v011 successfully :-)";
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
