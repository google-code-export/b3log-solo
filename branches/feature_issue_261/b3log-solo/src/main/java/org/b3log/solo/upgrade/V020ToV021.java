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
import org.b3log.solo.model.Page;
import org.b3log.solo.repository.PageRepository;
import org.b3log.solo.repository.impl.PageGAERepository;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Upgrader for <b>v020</b> to <b>v021</b>.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.2, Jan 12, 2011
 */
public final class V020ToV021 extends HttpServlet {

    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(V020ToV021.class.getName());
    /**
     * Page repository.
     */
    private PageRepository pageRepository =
            PageGAERepository.getInstance();

    @Override
    protected void doGet(final HttpServletRequest request,
                         final HttpServletResponse response)
            throws ServletException, IOException {
        if ("0.2.1".equals(SoloServletListener.VERSION)) {
            LOGGER.info("Checking for consistency....");
            final Transaction transaction = pageRepository.beginTransaction();
            try {
                final Query query = new Query();
                final JSONObject result = pageRepository.get(query);
                final JSONArray array = result.getJSONArray(Keys.RESULTS);

                for (int i = 0; i < array.length(); i++) {
                    final JSONObject page = array.getJSONObject(i);
                    if (!page.has(Page.PAGE_PERMALINK)) {
                        final String pageId = page.getString(
                                Keys.OBJECT_ID);
                        page.put(Page.PAGE_PERMALINK, "/page.do?oId=" + pageId);
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
                throw new ServletException("Upgrade fail from v020 to v021");
            }

            final PrintWriter writer = response.getWriter();
            final String upgraded =
                    "Upgraded from v020 to v021 successfully :-)";
            LOGGER.info(upgraded);
            writer.print(upgraded);

            writer.close();

            LOGGER.info("Checked for consistency");
        }
    }
}
