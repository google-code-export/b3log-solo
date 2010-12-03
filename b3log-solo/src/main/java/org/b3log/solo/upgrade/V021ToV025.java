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

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.QueryResultList;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
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
import org.b3log.solo.model.ArchiveDate;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.Preference;
import org.b3log.solo.model.Tag;
import org.b3log.solo.repository.ArchiveDateRepository;
import org.b3log.solo.repository.ArticleRepository;
import org.b3log.solo.repository.TagRepository;
import org.b3log.solo.util.PreferenceUtils;
import org.json.JSONObject;

/**
 * Upgrader for <b>v021</b> to <b>v025</b>.
 *
 * <p>
 * Model:
 *   <ul>
 *     <li>
 *       Adds a property(named {@value Article#ARTICLE_IS_PUBLISHED}) to
 *       {@link Article article} entity
 *     </li>
 *     <li>
 *       Adds a property(named {@value Article#ARTICLE_AUTHOR_EMAIL}) to
 *       {@link Article article} entity
 *     </li>
 *     <li>
 *       Renames property name from {@value #OLD_ADMIN_EMAIL_PROPERTY_NAME} to
 *       {@value Preference#ADMIN_EMAIL} of {@link Preference preference} entity.
 *     </li>
 *     <li>
 *       Adds a property(named {@value Tag#TAG_PUBLISHED_REFERENCE_COUNT}) to
 *       {@link Tag tag} entity
 *     </li>
 *     <li>
 *       Adds a property(named {@value ArchiveDate#ARCHIVE_DATE_PUBLISHED_ARTICLE_COUNT})
 *       to {@link ArchiveDate archive date} entity
 *     </li>
 *   </ul>
 * </p>
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.4, Dec 3, 2010
 */
public final class V021ToV025 extends HttpServlet {

    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(V021ToV025.class.getName());
    /**
     * Key of old administrator's email.
     */
    private static final String OLD_ADMIN_EMAIL_PROPERTY_NAME =
            "adminGmail";
    /**
     * Article repository.
     */
    @Inject
    private ArticleRepository articleRepository;
    /**
     * Preference utilities.
     */
    @Inject
    private PreferenceUtils preferenceUtils;
    /**
     * User service.
     */
    private static final UserService USER_SERVICE =
            UserServiceFactory.getUserService();
    /**
     * Tag repository.
     */
    @Inject
    private TagRepository tagRepository;
    /**
     * Archive date repository.
     */
    @Inject
    private ArchiveDateRepository archiveDateRepository;
    /**
     * Update size in an request.
     */
    private static final int UPDATE_SIZE = 100;

    @Override
    protected void doGet(final HttpServletRequest request,
                         final HttpServletResponse response)
            throws ServletException, IOException {
        if ("0.2.5".equals(SoloServletListener.VERSION)) {
            LOGGER.info("Checking for consistency....");

            final String currentUserEmail =
                    USER_SERVICE.getCurrentUser().getEmail();

            upgradePreference(currentUserEmail);
            upgradeTags();
            upgradeArchiveDates();

            Transaction transaction =
                    AbstractGAERepository.DATASTORE_SERVICE.beginTransaction();
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
                    final JSONObject article =
                            AbstractGAERepository.entity2JSONObject(entity);
                    final String articleId = article.getString(
                            Keys.OBJECT_ID);

                    if (!article.has(Article.ARTICLE_IS_PUBLISHED)) {
                        article.put(Article.ARTICLE_IS_PUBLISHED, true);
                        article.put(Article.ARTICLE_AUTHOR_EMAIL,
                                    currentUserEmail);

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
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
                throw new ServletException("Upgrade fail from v021 to v025");
            }

            final PrintWriter writer = response.getWriter();
            final String partialUpgrade = "Upgrade from v021 to v025 partially, "
                                          + "please run this upgrader(visit this"
                                          + " URL) again.";
            final String upgraded =
                    "Upgraded from v021 to v025 successfully :-)";
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

    /**
     * Upgrades archive dates.
     *
     * @throws ServletException upgrades fails
     */
    private void upgradeArchiveDates() throws ServletException {
        final Transaction transaction =
                AbstractGAERepository.DATASTORE_SERVICE.beginTransaction();
        try {
            final Query query =
                    new Query(ArchiveDate.ARCHIVE_DATE);
            final PreparedQuery preparedQuery =
                    AbstractGAERepository.DATASTORE_SERVICE.prepare(query);
            final QueryResultList<Entity> queryResultList =
                    preparedQuery.asQueryResultList(FetchOptions.Builder.
                    withDefaults());
            for (final Entity entity : queryResultList) {
                if (entity.hasProperty(
                        ArchiveDate.ARCHIVE_DATE_PUBLISHED_ARTICLE_COUNT)) {
                    final JSONObject archiveDate =
                            AbstractGAERepository.entity2JSONObject(entity);
                    archiveDate.put(
                            ArchiveDate.ARCHIVE_DATE_PUBLISHED_ARTICLE_COUNT,
                            archiveDate.getInt(
                            ArchiveDate.ARCHIVE_DATE_ARTICLE_COUNT));
                    archiveDateRepository.update(archiveDate.getString(
                            Keys.OBJECT_ID),
                                                 archiveDate);
                }
            }
            transaction.commit();
        } catch (final Exception e) {
            transaction.rollback();
            LOGGER.log(Level.SEVERE, "Upgrade archive date fail: {0}",
                       e.getMessage());
            throw new ServletException("Upgrade fail from v021 to v025");
        }
    }

    /**
     * Upgrades tags.
     *
     * @throws ServletException upgrade fails
     */
    private void upgradeTags() throws ServletException {
        final Transaction transaction =
                AbstractGAERepository.DATASTORE_SERVICE.beginTransaction();
        try {
            final Query query =
                    new Query(Tag.TAG);
            final PreparedQuery preparedQuery =
                    AbstractGAERepository.DATASTORE_SERVICE.prepare(query);
            final QueryResultList<Entity> queryResultList =
                    preparedQuery.asQueryResultList(FetchOptions.Builder.
                    withDefaults());
            for (final Entity entity : queryResultList) {
                if (entity.hasProperty(Tag.TAG_PUBLISHED_REFERENCE_COUNT)) {
                    final JSONObject tag =
                            AbstractGAERepository.entity2JSONObject(entity);
                    tag.put(Tag.TAG_PUBLISHED_REFERENCE_COUNT,
                            tag.getInt(Tag.TAG_REFERENCE_COUNT));
                    tagRepository.update(tag.getString(Keys.OBJECT_ID), tag);
                }
            }
            transaction.commit();
        } catch (final Exception e) {
            transaction.rollback();
            LOGGER.log(Level.SEVERE, "Upgrade tag fail: {0}", e.getMessage());
            throw new ServletException("Upgrade fail from v021 to v025");
        }
    }

    /**
     * Upgrades preference withe the specified user email.
     *
     * @param currentUserEmail the specified user email
     * @throws ServletException upgrade fails
     */
    private void upgradePreference(final String currentUserEmail)
            throws ServletException {
        final Transaction transaction =
                AbstractGAERepository.DATASTORE_SERVICE.beginTransaction();
        try {
            final JSONObject preference = preferenceUtils.getPreference();
            preference.remove(OLD_ADMIN_EMAIL_PROPERTY_NAME);
            if (!preference.has(Preference.ADMIN_EMAIL)) {
                preference.put(Preference.ADMIN_EMAIL, currentUserEmail);
            }
            preferenceUtils.setPreference(preference);
            transaction.commit();
        } catch (final Exception e) {
            transaction.rollback();
            LOGGER.log(Level.SEVERE, "Upgrade preference fail: {0}",
                       e.getMessage());
            throw new ServletException("Upgrade fail from v021 to v025");
        }
    }
}
