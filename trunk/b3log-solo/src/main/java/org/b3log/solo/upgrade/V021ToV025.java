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
import org.b3log.latke.model.Role;
import org.b3log.latke.model.User;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.repository.gae.AbstractGAERepository;
import org.b3log.solo.SoloServletListener;
import org.b3log.solo.model.ArchiveDate;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.Link;
import org.b3log.solo.model.Preference;
import org.b3log.solo.model.Statistic;
import org.b3log.solo.model.Tag;
import org.b3log.solo.repository.ArchiveDateRepository;
import org.b3log.solo.repository.ArticleRepository;
import org.b3log.solo.repository.LinkRepository;
import org.b3log.solo.repository.StatisticRepository;
import org.b3log.solo.repository.TagRepository;
import org.b3log.solo.repository.UserRepository;
import org.b3log.solo.util.Preferences;
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
 *     <li>
 *       Adds a property(named {@value Article#ARTICLE_HAD_BEEN_PUBLISHED})
 *       to {@link Article article} entity
 *     </li>
 *     <li>
 *       Adds a property(named {@value Link#LINK_ORDER})
 *       to {@link Link link} entity
 *     </li>
 *     <li>
 *       Saves the administrator to {@value User#USER} entities.
 *     </li>
 *     <li>
 *       Adds a property(named {@value Preference#ARTICLE_UPDATE_HINT_ENABLED}
 *       to {@link Preference preference} entity.
 *     </li>
 *     <li>
 *       Adds a property(named {@value Statistic#STATISTIC_PUBLISHED_ARTICLE_COUNT}
 *       to {@link Statistic statistic} entity.
 *     </li>
 *     <li>
 *       Adds a property(named {@value Statistic#STATISTIC_PUBLISHED_BLOG_COMMENT_COUNT}
 *       to {@link Statistic statistic} entity.
 *     </li>
 *   </ul>
 * </p>
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.9, Dec 9, 2010
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
     * Statistic repository.
     */
    @Inject
    private StatisticRepository statisticRepository;
    /**
     * Article repository.
     */
    @Inject
    private ArticleRepository articleRepository;
    /**
     * Preference utilities.
     */
    @Inject
    private Preferences preferenceUtils;
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
     * Link repository.
     */
    @Inject
    private LinkRepository linkRepository;
    /**
     * User repository.
     */
    @Inject
    private UserRepository userRepository;
    /**
     * Update size in an request.
     */
    private static final int UPDATE_SIZE = 100;
    /**
     * GAE datastore service.
     */
    private final DatastoreService datastoreService =
            DatastoreServiceFactory.getDatastoreService();

    @Override
    protected void doGet(final HttpServletRequest request,
                         final HttpServletResponse response)
            throws ServletException, IOException {
        if ("0.2.5".equals(SoloServletListener.VERSION)) {
            LOGGER.info("Checking for consistency....");

            final String currentUserEmail =
                    USER_SERVICE.getCurrentUser().getEmail();
            final String currentUserName =
                    USER_SERVICE.getCurrentUser().getNickname();

            upgradePreference(currentUserEmail);
            upgradeStatistic();
            upgradeTags();
            upgradeArchiveDates();
            upgradeLinks();
            saveAdmin(currentUserName, currentUserEmail);

            Transaction transaction = articleRepository.beginTransaction();
            boolean isConsistent = true;
            try {
                final Query query = new Query(Article.ARTICLE);
                final PreparedQuery preparedQuery = datastoreService.prepare(
                        query);
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
                        article.put(Article.ARTICLE_HAD_BEEN_PUBLISHED, true);

                        articleRepository.update(articleId, article);
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
     * Upgrades links.
     *
     * @throws ServletException upgrades fails
     */
    private void upgradeLinks() throws ServletException {
        final Transaction transaction = linkRepository.beginTransaction();
        try {
            final Query query = new Query(Link.LINK);
            final PreparedQuery preparedQuery = datastoreService.prepare(query);
            final QueryResultList<Entity> queryResultList =
                    preparedQuery.asQueryResultList(FetchOptions.Builder.
                    withDefaults());
            for (final Entity entity : queryResultList) {
                if (!entity.hasProperty(Link.LINK_ORDER)) {
                    final JSONObject link =
                            AbstractGAERepository.entity2JSONObject(entity);
                    final int maxOrder = linkRepository.getMaxOrder();
                    link.put(Link.LINK_ORDER, maxOrder + 1);
                    linkRepository.update(link.getString(
                            Keys.OBJECT_ID), link);
                }
            }
            transaction.commit();
        } catch (final Exception e) {
            transaction.rollback();
            LOGGER.log(Level.SEVERE, "Upgrade link fail: {0}", e.getMessage());
            throw new ServletException("Upgrade fail from v021 to v025");
        }
    }

    /**
     * Upgrades archive dates.
     *
     * @throws ServletException upgrades fails
     */
    private void upgradeArchiveDates() throws ServletException {
        final Transaction transaction = archiveDateRepository.beginTransaction();
        try {
            final Query query = new Query(ArchiveDate.ARCHIVE_DATE);
            final PreparedQuery preparedQuery = datastoreService.prepare(query);
            final QueryResultList<Entity> queryResultList =
                    preparedQuery.asQueryResultList(FetchOptions.Builder.
                    withDefaults());
            for (final Entity entity : queryResultList) {
                if (!entity.hasProperty(
                        ArchiveDate.ARCHIVE_DATE_PUBLISHED_ARTICLE_COUNT)) {
                    final JSONObject archiveDate =
                            AbstractGAERepository.entity2JSONObject(entity);
                    archiveDate.put(
                            ArchiveDate.ARCHIVE_DATE_PUBLISHED_ARTICLE_COUNT,
                            archiveDate.getInt(
                            ArchiveDate.ARCHIVE_DATE_ARTICLE_COUNT));
                    archiveDateRepository.update(archiveDate.getString(
                            Keys.OBJECT_ID), archiveDate);
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
     * Saves the administrator.
     *
     * @param adminName the specified administrator name
     * @param adminEmail the specified administrator email
     * @throws ServletException upgrades fails
     */
    private void saveAdmin(final String adminName, final String adminEmail)
            throws ServletException {
        final Transaction transaction = userRepository.beginTransaction();
        try {
            final Query query = new Query(User.USER);
            query.addFilter(User.USER_ROLE,
                            Query.FilterOperator.EQUAL, Role.ADMIN_ROLE);
            final PreparedQuery preparedQuery = datastoreService.prepare(query);
            final Entity adminEntity = preparedQuery.asSingleEntity();
            if (null == adminEntity) {
                final JSONObject admin = new JSONObject();
                admin.put(User.USER_EMAIL, adminEmail);
                admin.put(User.USER_NAME, adminName);
                admin.put(User.USER_ROLE, Role.ADMIN_ROLE);

                userRepository.add(admin);
                transaction.commit();
            }
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
        final Transaction transaction = tagRepository.beginTransaction();
        try {
            final Query query = new Query(Tag.TAG);
            final PreparedQuery preparedQuery = datastoreService.prepare(query);
            final QueryResultList<Entity> queryResultList =
                    preparedQuery.asQueryResultList(FetchOptions.Builder.
                    withDefaults());
            for (final Entity entity : queryResultList) {
                if (!entity.hasProperty(Tag.TAG_PUBLISHED_REFERENCE_COUNT)) {
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
     * Upgrades statistic.
     *
     * @throws ServletException upgrade fails
     */
    private void upgradeStatistic() throws ServletException {
        final Transaction transaction = tagRepository.beginTransaction();
        try {
            final Query query = new Query(Statistic.STATISTIC);
            final PreparedQuery preparedQuery = datastoreService.prepare(query);
            final Entity entity = preparedQuery.asSingleEntity();
            if (!entity.hasProperty(
                    Statistic.STATISTIC_PUBLISHED_ARTICLE_COUNT)) {
                final JSONObject statistic =
                        AbstractGAERepository.entity2JSONObject(entity);
                statistic.put(Statistic.STATISTIC_PUBLISHED_ARTICLE_COUNT,
                              statistic.getInt(
                        Statistic.STATISTIC_BLOG_ARTICLE_COUNT));
                statistic.put(Statistic.STATISTIC_PUBLISHED_BLOG_COMMENT_COUNT,
                              statistic.getInt(
                        Statistic.STATISTIC_BLOG_COMMENT_COUNT));

                statisticRepository.update(statistic.getString(Keys.OBJECT_ID),
                                           statistic);
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
        final Transaction transaction = userRepository.beginTransaction();
        try {
            final JSONObject preference = preferenceUtils.getPreference();
            if (preference.has(OLD_ADMIN_EMAIL_PROPERTY_NAME)) {
                preference.put(Preference.ADMIN_EMAIL, currentUserEmail);
            }

            if (!preference.has(Preference.ENABLE_ARTICLE_UPDATE_HINT)) {
                preference.put(Preference.ENABLE_ARTICLE_UPDATE_HINT,
                               true);
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
