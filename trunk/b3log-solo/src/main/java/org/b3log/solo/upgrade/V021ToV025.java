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
import org.b3log.latke.action.util.PageCaches;
import org.b3log.latke.model.Role;
import org.b3log.latke.model.User;
import org.b3log.latke.repository.FilterOperator;
import org.b3log.latke.repository.Query;
import org.b3log.latke.repository.Transaction;
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
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Upgrader for <b>v021</b> to <b>v025</b>.
 *
 * <p>
 * Model:
 *   <ul>
 *     <li>
 *       Adds a property(named {@value Article#ARTICLE_IS_PUBLISHED}) to
 *       entity {@link Article article}
 *     </li>
 *     <li>
 *       Adds a property(named {@value Article#ARTICLE_AUTHOR_EMAIL}) to
 *       entity {@link Article article}
 *     </li>
 *     <li>
 *       Renames property name from {@value #OLD_ADMIN_EMAIL_PROPERTY_NAME} to
 *       entity {@value Preference#ADMIN_EMAIL} of {@link Preference preference}
 *     </li>
 *     <li>
 *       Adds a property(named {@value Tag#TAG_PUBLISHED_REFERENCE_COUNT}) to
 *       entity {@link Tag tag}
 *     </li>
 *     <li>
 *       Adds a property(named {@value ArchiveDate#ARCHIVE_DATE_PUBLISHED_ARTICLE_COUNT})
 *       to entity {@link ArchiveDate archive date}
 *     </li>
 *     <li>
 *       Adds a property(named {@value Article#ARTICLE_HAD_BEEN_PUBLISHED})
 *       to entity {@link Article article}
 *     </li>
 *     <li>
 *       Adds a property(named {@value Link#LINK_ORDER})
 *       to entity {@link Link link}
 *     </li>
 *     <li>
 *       Saves the administrator to {@value User#USER} entities
 *     </li>
 *     <li>
 *       Adds a property(named {@value Preference#ENABLE_ARTICLE_UPDATE_HINT})
 *       to entity {@link Preference preference}
 *     </li>
 *     <li>
 *       Adds a property(named {@value Preference#CURRENT_VERSION_NUMBER})
 *       to entity {@link Preference preference}
 *     </li>
 *     <li>
 *       Adds a property(named {@value Statistic#STATISTIC_PUBLISHED_ARTICLE_COUNT})
 *       to entity {@link Statistic statistic}
 *     </li>
 *     <li>
 *       Adds a property(named {@value Statistic#STATISTIC_PUBLISHED_BLOG_COMMENT_COUNT})
 *       to entity {@link Statistic statistic}
 *     </li>
 *     <li>
 *       Adds a property(named {@value Preference#SIGNS}) to entity
 *       {@link Preference preference}
 *     </li>
 *     <li>
 *       Adds a property(named {@value Preference#TIME_ZONE_ID}) to entity
 *       {@link Preference preference}
 *     </li>
 *     <li>
 *       Adds a property(named {@value Article#ARTICLE_RANDOM_DOUBLE})
 *       to entity {@link Article article}
 *     </li>
 *   </ul>
 * </p>
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.1.8, Jan 10, 2011
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

    @Override
    protected void doGet(final HttpServletRequest request,
                         final HttpServletResponse response)
            throws ServletException, IOException {
        if ("0.2.5 Beta2".equals(SoloServletListener.VERSION)) {
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
                final Query query = new Query();
                final JSONObject result = articleRepository.get(query);
                final JSONArray articles = result.getJSONArray(Keys.RESULTS);
                int cnt = 0;
                for (int i = 0; i < articles.length(); i++) {
                    final JSONObject article = articles.getJSONObject(i);
                    final String articleId = article.getString(Keys.OBJECT_ID);

                    if (!article.has(Article.ARTICLE_IS_PUBLISHED)) {
                        article.put(Article.ARTICLE_IS_PUBLISHED, true);
                        article.put(Article.ARTICLE_AUTHOR_EMAIL,
                                    currentUserEmail);
                        article.put(Article.ARTICLE_HAD_BEEN_PUBLISHED, true);

                        articleRepository.update(articleId, article);
                        isConsistent = false;
                    }

                    if (!article.has(Article.ARTICLE_AUTHOR_EMAIL)) {
                        article.put(Article.ARTICLE_AUTHOR_EMAIL,
                                    currentUserEmail);

                        articleRepository.update(articleId, article);
                        isConsistent = false;
                    }

                    if (!article.has(Article.ARTICLE_HAD_BEEN_PUBLISHED)) {
                        article.put(Article.ARTICLE_HAD_BEEN_PUBLISHED, true);

                        articleRepository.update(articleId, article);
                        isConsistent = false;
                    }

                    if (!article.has(Article.ARTICLE_RANDOM_DOUBLE)) {
                        article.put(Article.ARTICLE_RANDOM_DOUBLE, Math.random());

                        articleRepository.update(articleId, article);
                        isConsistent = false;
                    }


                    if (0 == cnt % UPDATE_SIZE) {
                        transaction.commit();
                        transaction = articleRepository.beginTransaction();
                    }

                    cnt++;
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

        PageCaches.removeAll();
    }

    /**
     * Upgrades links.
     *
     * @throws ServletException upgrades fails
     */
    private void upgradeLinks() throws ServletException {
        Transaction transaction = null;
        try {
            final Query query = new Query();
            final JSONObject result = linkRepository.get(query);
            final JSONArray links = result.getJSONArray(Keys.RESULTS);
            for (int i = 0; i < links.length(); i++) {
                final JSONObject link = links.getJSONObject(i);
                if (!link.has(Link.LINK_ORDER)) {
                    transaction = linkRepository.beginTransaction();
                    final int maxOrder = linkRepository.getMaxOrder();
                    link.put(Link.LINK_ORDER, maxOrder + 1);
                    linkRepository.update(link.getString(
                            Keys.OBJECT_ID), link);
                    transaction.commit();
                }
            }
        } catch (final Exception e) {
            if (null != transaction && transaction.isActive()) {
                transaction.rollback();
            }
            LOGGER.log(Level.SEVERE, "Upgrade link fail.", e);
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
            final Query query = new Query();
            final JSONObject result = archiveDateRepository.get(query);
            final JSONArray archiveDates = result.getJSONArray(Keys.RESULTS);
            for (int i = 0; i < archiveDates.length(); i++) {
                final JSONObject archiveDate = archiveDates.getJSONObject(i);
                if (!archiveDate.has(
                        ArchiveDate.ARCHIVE_DATE_PUBLISHED_ARTICLE_COUNT)) {
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
            LOGGER.log(Level.SEVERE, "Upgrade archive date fail.", e);
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
            final Query query = new Query();
            query.addFilter(User.USER_ROLE,
                            FilterOperator.EQUAL, Role.ADMIN_ROLE);
            final JSONObject result = userRepository.get(query);
            final JSONArray users = result.getJSONArray(Keys.RESULTS);
            if (0 == users.length()) {
                final JSONObject admin = new JSONObject();
                admin.put(User.USER_EMAIL, adminEmail);
                admin.put(User.USER_NAME, adminName);
                admin.put(User.USER_ROLE, Role.ADMIN_ROLE);

                userRepository.add(admin);
            }
            transaction.commit();
        } catch (final Exception e) {
            transaction.rollback();
            LOGGER.log(Level.SEVERE, "Upgrade archive date fail.", e);
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
            final JSONObject result = tagRepository.get(1, Integer.MAX_VALUE);
            final JSONArray tags = result.getJSONArray(Keys.RESULTS);
            for (int i = 0; i < tags.length(); i++) {
                final JSONObject tag = tags.getJSONObject(i);
                if (!tag.has(Tag.TAG_PUBLISHED_REFERENCE_COUNT)) {
                    tag.put(Tag.TAG_PUBLISHED_REFERENCE_COUNT,
                            tag.getInt(Tag.TAG_REFERENCE_COUNT));
                    tagRepository.update(tag.getString(Keys.OBJECT_ID), tag);
                }
            }
            transaction.commit();
        } catch (final Exception e) {
            transaction.rollback();
            LOGGER.log(Level.SEVERE, "Upgrade tag fail.", e);
            throw new ServletException("Upgrade fail from v021 to v025");
        }
    }

    /**
     * Upgrades statistic.
     *
     * @throws ServletException upgrade fails
     */
    private void upgradeStatistic() throws ServletException {
        final Transaction transaction = statisticRepository.beginTransaction();
        try {
            final JSONObject statistic =
                    statisticRepository.get(Statistic.STATISTIC);
            if (!statistic.has(
                    Statistic.STATISTIC_PUBLISHED_ARTICLE_COUNT)) {
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
            LOGGER.log(Level.SEVERE, "Upgrade tag fail.", e);
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
                preference.remove(OLD_ADMIN_EMAIL_PROPERTY_NAME);
            }

            if (!preference.has(Preference.ENABLE_ARTICLE_UPDATE_HINT)) {
                preference.put(Preference.ENABLE_ARTICLE_UPDATE_HINT,
                               Preference.Default.DEFAULT_ENABLE_ARTICLE_UPDATE_HINT);
            }

            final int signLength = 4;
            if (!preference.has(Preference.SIGNS)
                || signLength
                   != new JSONArray(preference.getString(Preference.SIGNS)).
                    length()) { // patch on patch - -~
                preference.put(Preference.SIGNS,
                               Preference.Default.DEFAULT_SIGNS);
            }

            if (!preference.has(Preference.TIME_ZONE_ID)) {
                preference.put(Preference.TIME_ZONE_ID,
                               Preference.Default.DEFAULT_TIME_ZONE);
            }

            preference.put(Preference.CURRENT_VERSION_NUMBER,
                           SoloServletListener.VERSION);

            preferenceUtils.setPreference(preference);
            transaction.commit();
        } catch (final Exception e) {
            transaction.rollback();
            LOGGER.log(Level.SEVERE, "Upgrade preference fail.", e);
            throw new ServletException("Upgrade fail from v021 to v025");
        }
    }
}
