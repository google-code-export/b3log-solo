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

package org.b3log.solo.upgrade;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.QueryResultList;
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
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.repository.gae.AbstractGAERepository;
import org.b3log.solo.SoloServletListener;
import org.b3log.solo.model.Page;
import org.b3log.solo.repository.PageRepository;
import org.json.JSONObject;

/**
 * Upgrader for <b>v011</b> to <b>v020</b>.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Dec 3, 2010
 */
public final class V011ToV020 extends HttpServlet {

    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(V011ToV020.class.getName());
    /**
     * Page repository.
     */
    @Inject
    private PageRepository pageRepository;
    /**
     * GAE datastore service.
     */
    private final DatastoreService datastoreService =
            DatastoreServiceFactory.getDatastoreService();

    @Override
    protected void doGet(final HttpServletRequest request,
                         final HttpServletResponse response)
            throws ServletException, IOException {
        if ("0.2.0".equals(SoloServletListener.VERSION)) {
            LOGGER.info("Checking for consistency....");
            final Transaction transaction = pageRepository.beginTransaction();
            try {
                final Query query = new Query(Page.PAGE);
                final PreparedQuery preparedQuery = 
                        datastoreService.prepare(query);
                final QueryResultList<Entity> queryResultList =
                        preparedQuery.asQueryResultList(FetchOptions.Builder.
                        withDefaults());

                for (final Entity entity : queryResultList) {
                    if (!entity.hasProperty(Page.PAGE_COMMENT_COUNT)) {
                        final JSONObject page =
                                AbstractGAERepository.entity2JSONObject(entity);
                        final String pageId = page.getString(
                                Keys.OBJECT_ID);
                        page.put(Page.PAGE_COMMENT_COUNT, 0);
                        pageRepository.update(pageId, page);

                        LOGGER.log(Level.INFO, "Updated page[oId={0}]",
                                   pageId);
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
                throw new ServletException("Upgrade fail from v011 to v020");
            }

            final PrintWriter writer = response.getWriter();
            final String upgraded =
                    "Upgraded from v011 to v020 successfully :-)";
            LOGGER.info(upgraded);
            writer.print(upgraded);

            writer.close();

            LOGGER.info("Checked for consistency");
        }
    }
}
