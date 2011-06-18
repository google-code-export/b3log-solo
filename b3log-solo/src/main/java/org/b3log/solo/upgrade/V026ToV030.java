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
import org.b3log.latke.action.util.PageCaches;
import org.b3log.latke.repository.Transaction;
import org.b3log.solo.SoloServletListener;
import org.b3log.solo.model.Preference;
import org.b3log.solo.repository.PreferenceRepository;
import org.b3log.solo.repository.impl.PreferenceGAERepository;
import org.b3log.solo.util.Preferences;
import org.json.JSONObject;

/**
 * Upgrader for <b>v026</b> to <b>v030</b>.
 *
 * <p>
 * Model:
 *   <ul>
 *     <li>
 *       Adds a property(named {@value Preference#PAGE_CACHE_ENABLED}) to
 *       entity {@link Preference preference}
 *     </li>
 *   </ul>
 * </p>
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Jun 18, 2011
 */
public final class V026ToV030 extends HttpServlet {

    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(V026ToV030.class.getName());
    /**
     * Preference utilities.
     */
    private Preferences preferenceUtils = Preferences.getInstance();
    /**
     * Preference repository.
     */
    private PreferenceRepository preferenceRepository =
            PreferenceGAERepository.getInstance();

    @Override
    protected void doGet(final HttpServletRequest request,
                         final HttpServletResponse response)
            throws ServletException, IOException {
        if ("0.2.6".equals(SoloServletListener.VERSION)) {
            LOGGER.info("Checking for consistency....");

            upgradePreference();


            final PrintWriter writer = response.getWriter();
            final String upgraded =
                    "Upgraded from v026 to v030 successfully :-)";
            LOGGER.info(upgraded);
            writer.print(upgraded);

            writer.close();

            LOGGER.info("Checked for consistency");
        }

        PageCaches.removeAll();
    }

    /**
     * Upgrades preference model..
     *
     * @throws ServletException upgrade fails
     */
    private void upgradePreference() throws ServletException {
        final Transaction transaction = preferenceRepository.beginTransaction();
        try {
            final JSONObject preference = preferenceUtils.getPreference();

            if (!preference.has(Preference.PAGE_CACHE_ENABLED)) {
                preference.put(Preference.PAGE_CACHE_ENABLED,
                               Preference.Default.DEFAULT_PAGE_CACHE_ENABLED);
            }

            preferenceUtils.setPreference(preference);
            transaction.commit();
        } catch (final Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            LOGGER.log(Level.SEVERE, "Upgrade preference fail.", e);
            throw new ServletException("Upgrade fail from v026 to v030");
        }
    }
}
