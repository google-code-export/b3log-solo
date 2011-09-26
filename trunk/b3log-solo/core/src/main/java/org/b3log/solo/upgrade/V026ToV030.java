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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.time.DateUtils;
import org.b3log.latke.Keys;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.SortDirection;
import org.b3log.latke.repository.Transaction;
import org.b3log.solo.SoloServletListener;
import org.b3log.solo.model.ArchiveDate;
import org.b3log.solo.model.Preference;
import org.b3log.solo.repository.ArchiveDateRepository;
import org.b3log.solo.repository.PreferenceRepository;
import org.b3log.solo.repository.impl.ArchiveDateRepositoryImpl;
import org.b3log.solo.repository.impl.PreferenceRepositoryImpl;
import org.b3log.solo.util.Preferences;
import org.json.JSONArray;
import org.json.JSONException;
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
 *     <li>
 *       Adds a property(named {@value Preference#ALLOW_VISIT_DRAFT_VIA_PERMALINK}) to
 *       entity {@link Preference preference}
 *     </li>
 *   </ul>
 * </p>
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.2, Aug 5, 2011
 * @deprecated As of Solo 0.3.1, removes servlet URL mapping in web.xml, with 
 * no replacement.
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
            PreferenceRepositoryImpl.getInstance();
    /**
     * Archive date repository.
     */
    private ArchiveDateRepository archiveDateRepository =
            ArchiveDateRepositoryImpl.getInstance();

    @Override
    protected void doGet(final HttpServletRequest request,
                         final HttpServletResponse response)
            throws ServletException, IOException {
        final PrintWriter writer = response.getWriter();
        if ("0.3.0".equals(SoloServletListener.VERSION)) {
            LOGGER.info("Checking for consistency....");

            upgradePreference();
            upgradeArchiveDates();

            final String upgraded =
                    "Upgraded from v026 to v030 successfully :-)";
            LOGGER.info(upgraded);
            writer.print(upgraded);

            writer.close();

            LOGGER.info("Checked for consistency");
        } else {
            final String ignored =
                    "Ignored upgrade to v030, caused by the old version is NOT v026!";
            LOGGER.info(ignored);
            writer.print(ignored);

            writer.close();
        }
    }

    /**
     * Upgrades archive dates model.
     * 
     * @throws ServletException upgrade fails
     */
    private void upgradeArchiveDates() throws ServletException {
        final Transaction transaction = archiveDateRepository.beginTransaction();
        try {
            final List<JSONObject> archiveDates = getOldArchiveDates();
            for (final JSONObject archiveDate : archiveDates) {
                final Date date =
                        (Date) archiveDate.get(ArchiveDate.ARCHIVE_DATE);
                final Date round = DateUtils.round(date, Calendar.MONTH);
                LOGGER.log(Level.FINER,
                           "date={0}, time={1}, round={2}, roundTime={3}",
                           new Object[]{date, date.getTime(),
                                        round, round.getTime()});
                archiveDate.put(ArchiveDate.ARCHIVE_TIME, round.getTime());
                archiveDate.remove(ArchiveDate.ARCHIVE_DATE); // Removes the old version property

                archiveDateRepository.update(archiveDate.getString(
                        Keys.OBJECT_ID), archiveDate);
            }

            transaction.commit();
        } catch (final Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            LOGGER.log(Level.SEVERE, "Upgrade archive dates fail.", e);
            throw new ServletException("Upgrade fail from v026 to v030");
        }
    }

    /**
     * Upgrades preference model.
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

            if (!preference.has(Preference.ALLOW_VISIT_DRAFT_VIA_PERMALINK)) {
                preference.put(Preference.ALLOW_VISIT_DRAFT_VIA_PERMALINK,
                               Preference.Default.DEFAULT_ALLOW_VISIT_DRAFT_VIA_PERMALINK);
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

    /**
     * Gets old version (0.2.6) archive dates.
     * 
     * @return archive dates, returns an empty list if not found
     * @throws RepositoryException repository exception
     */
    private List<JSONObject> getOldArchiveDates() throws RepositoryException {
        final List<JSONObject> ret = new ArrayList<JSONObject>();
        final org.b3log.latke.repository.Query query =
                new org.b3log.latke.repository.Query().addSort(
                ArchiveDate.ARCHIVE_DATE, SortDirection.DESCENDING);
        final JSONObject result = archiveDateRepository.get(query);

        try {
            final JSONArray archiveDates = result.getJSONArray(Keys.RESULTS);

            for (int i = 0; i < archiveDates.length(); i++) {
                final JSONObject archiveDate = archiveDates.getJSONObject(i);
                ret.add(archiveDate);
            }
        } catch (final JSONException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new RepositoryException(e);
        }

        return ret;
    }
}
