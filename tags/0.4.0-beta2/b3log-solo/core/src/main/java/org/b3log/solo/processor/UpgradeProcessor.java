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
package org.b3log.solo.processor;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.annotation.RequestProcessing;
import org.b3log.latke.annotation.RequestProcessor;
import org.b3log.latke.model.User;
import org.b3log.latke.repository.AbstractRepository;
import org.b3log.latke.repository.Query;
import org.b3log.latke.repository.SortDirection;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.HTTPRequestMethod;
import org.b3log.latke.servlet.renderer.AbstractHTTPResponseRenderer;
import org.b3log.latke.servlet.renderer.DoNothingRenderer;
import org.b3log.solo.SoloServletListener;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.Comment;
import org.b3log.solo.model.Link;
import org.b3log.solo.model.Page;
import org.b3log.solo.model.Preference;
import org.b3log.solo.repository.CommentRepository;
import org.b3log.solo.repository.LinkRepository;
import org.b3log.solo.repository.PageRepository;
import org.b3log.solo.repository.PreferenceRepository;
import org.b3log.solo.repository.UserRepository;
import org.b3log.solo.repository.impl.CommentRepositoryImpl;
import org.b3log.solo.repository.impl.LinkRepositoryImpl;
import org.b3log.solo.repository.impl.PageRepositoryImpl;
import org.b3log.solo.repository.impl.PreferenceRepositoryImpl;
import org.b3log.solo.repository.impl.UserRepositoryImpl;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Upgrader.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.1.0.5, Nov 2, 2011
 * @since 0.3.1
 */
@RequestProcessor
public final class UpgradeProcessor {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(UpgradeProcessor.class.getName());
    /**
     * Article-Comment repository.
     */
    private ArticleCommentRepository articleCommentRepository =
            ArticleCommentRepository.getInstance();
    /**
     * Page-Comment repository.
     */
    private PageCommentRepository pageCommentRepository =
            PageCommentRepository.getInstance();
    /**
     * Comment repository.
     */
    private CommentRepository commentRepository =
            CommentRepositoryImpl.getInstance();
    /**
     * Link repository.
     */
    private LinkRepository linkRepository = LinkRepositoryImpl.getInstance();
    /**
     * Page repository.
     */
    private PageRepository pageRepository = PageRepositoryImpl.getInstance();
    /**
     * User repository.
     */
    private UserRepository userRepository = UserRepositoryImpl.getInstance();
    /**
     * Preference repository.
     */
    private PreferenceRepository preferenceRepository =
            PreferenceRepositoryImpl.getInstance();

    /**
     * Checks upgrade.
     * 
     * @param context the specified context
     */
    @RequestProcessing(value = {"/upgrade/checker.do"},
                       method = HTTPRequestMethod.GET)
    public void upgrade(final HTTPRequestContext context) {
        final AbstractHTTPResponseRenderer renderer = new DoNothingRenderer();
        context.setRenderer(renderer);
        
        try {
            final JSONObject preference =
                    preferenceRepository.get(Preference.PREFERENCE);
            if (null == preference) { // Not init yet
                LOGGER.log(Level.INFO, "Not init yet");

                return;
            }

            if (!preference.has(Preference.VERSION)) {
                v030ToV031();

                return;
            }

            final String version = preference.getString(Preference.VERSION);

            if (SoloServletListener.VERSION.equals(version)) {
                return;
            }

            if ("0.3.0".equals(version)) { // 0.3.0 -> 0.3.1
                v030ToV031();
            } else if ("0.3.1".equals(version)) { // 0.3.1 -> 0.3.5
                v031ToV035();
            } else if ("0.3.5".equals(version)) { // 0.3.5 -> 0.4.0
                v035ToV040();
            } else {
                LOGGER.warning(
                        "Your B3log Solo is too old to upgrader, please contact the B3log Solo developers");
            }
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Upgrades from version 030 to version 031.
     * 
     * <p>
     * Model:
     *   <ul>
     *     <li>
     *       Adds a property(named {@value Comment#COMMENT_ON_ID}) to
     *       entity {@link Comment comment}
     *     </li>
     *     <li>
     *       Adds a property(named {@value Comment#COMMENT_ON_TYPE}) to
     *       entity {@link Comment comment}
     *     </li>
     *     <li>Clears {@code article_comment} repository</li>
     *     <li>Clears {@code page_comment} repository</li>
     *     <li>
     *       Adds a property(named {@value Preference#VERSION}) to
     *       entity {@link Preference preference}
     *     </li>
     *     <li>
     *       Adds a property(named {@value User#USER_PASSWORD}) to
     *       entity {@link User user}
     *     </li>
     *   </ul>
     * </p>
     * @throws Exception upgrade fails
     */
    private void v030ToV031() throws Exception {
        LOGGER.info("Upgrading from version 030 to version 031....");

        final Transaction transaction = pageCommentRepository.beginTransaction();
        try {
            upgradeComments(Article.ARTICLE);
            upgradeComments(Page.PAGE);

            final JSONObject preference =
                    preferenceRepository.get(Preference.PREFERENCE);

            preference.put(Preference.VERSION, "0.3.1");

            preferenceRepository.update(Preference.PREFERENCE, preference);

            final JSONArray users =
                    userRepository.get(new Query()).getJSONArray(Keys.RESULTS);
            LOGGER.log(Level.INFO, "Users[length={0}]", users.length());
            for (int i = 0; i < users.length(); i++) {
                final JSONObject user = users.getJSONObject(i);
                user.put(User.USER_PASSWORD,
                         Preference.Default.DEFAULT_ADMIN_PWD);

                userRepository.update(user.getString(Keys.OBJECT_ID), user);
            }

            transaction.commit();
        } catch (final Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }

            LOGGER.log(Level.SEVERE, "Upgrade comments fail.", e);
            throw new Exception("Upgrade fail from version 030 to version 031");
        }

        LOGGER.info("Upgraded from version 030 to version 031 successfully :-)");
    }

    /**
     * Upgrades from version 035 to version 040.
     * 
     * <p>
     * Model:
     *   <ul>
     *     <li>
     *       Restores the orders of links
     *     </li>
     *     <li>
     *       Adds a property(named {@value Link#LINK_ADDRESS}) to entity 
     *      {@link Link link}
     *     </li>
     *     <li>
     *       Restores the orders of pages
     *     </li>
     *     <li>
     *       Adds an entity(named {@value Preference#REPLY_NOTIFICATION_TEMPLATE}) to
     *       repository {@link Preference preference}.
     *     </li>
     *   </ul>
     * </p>
     * @throws Exception upgrade fails
     */
    private void v035ToV040() throws Exception {
        LOGGER.info("Upgrading from versiona 035 to version 040....");

        final Transaction transaction = linkRepository.beginTransaction();
        try {
            final JSONObject preference =
                    preferenceRepository.get(Preference.PREFERENCE);

            // Restores the orders of links.
            final JSONObject linkResult =
                    linkRepository.get(
                    new Query().addSort(Link.LINK_ORDER, SortDirection.ASCENDING));
            final JSONArray links = linkResult.getJSONArray(Keys.RESULTS);

            for (int i = 0; i < links.length(); i++) {
                final JSONObject link = links.getJSONObject(i);
                link.put(Link.LINK_ORDER, i);
                
                link.put(Link.LINK_DESCRIPTION, ""); // Adds default link description

                linkRepository.update(link.getString(Keys.OBJECT_ID), link);
            }

            // Restores the orders of pages.
            final JSONObject pageResult =
                    pageRepository.get(
                    new Query().addSort(Page.PAGE_ORDER, SortDirection.ASCENDING));
            final JSONArray pages = pageResult.getJSONArray(Keys.RESULTS);

            for (int i = 0; i < pages.length(); i++) {
                final JSONObject page = pages.getJSONObject(i);
                page.put(Page.PAGE_ORDER, i);

                pageRepository.update(page.getString(Keys.OBJECT_ID), page);
            }

            JSONObject replyNotificationTemplate = preferenceRepository.get(
                    Preference.REPLY_NOTIFICATION_TEMPLATE);
            if (null == replyNotificationTemplate) {
                replyNotificationTemplate = 
                        new JSONObject(Preference.Default.DEFAULT_REPLY_NOTIFICATION_TEMPLATE);
                replyNotificationTemplate.put(Keys.OBJECT_ID,
                                              Preference.REPLY_NOTIFICATION_TEMPLATE);

                preferenceRepository.add(replyNotificationTemplate);
            }

            preference.put(Preference.VERSION, "0.4.0");

            preferenceRepository.update(Preference.PREFERENCE, preference);

            transaction.commit();
        } catch (final Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }

            LOGGER.log(Level.SEVERE, "Upgrade comments fail.", e);
            throw new Exception("Upgrade fail from version 035 to version 040");
        }

        LOGGER.info("Upgraded from version 035 to version 040 successfully :-)");
    }

    /**
     * Upgrades from version 031 to version 035.
     * 
     * <p>
     * Model:
     *   <ul>
     *     <li>
     *       Adds a property(named {@value Preference#ARTICLE_LIST_STYLE}) to
     *       entity {@link Preference preference}
     *     </li>
     *   </ul>
     * </p>
     * @throws Exception upgrade fails
     */
    private void v031ToV035() throws Exception {
        LOGGER.info("Upgrading from version 031 to version 035....");

        final Transaction transaction = userRepository.beginTransaction();
        try {
            final JSONObject preference =
                    preferenceRepository.get(Preference.PREFERENCE);

            if (!preference.has(Preference.ARTICLE_LIST_STYLE)) {
                preference.put(Preference.ARTICLE_LIST_STYLE,
                               Preference.Default.DEFAULT_ARTICLE_LIST_STYLE);
            }

            preference.put(Preference.VERSION, "0.3.5");

            preferenceRepository.update(Preference.PREFERENCE, preference);

            transaction.commit();
        } catch (final Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }

            LOGGER.log(Level.SEVERE, "Upgrade comments fail.", e);
            throw new Exception("Upgrade fail from version 031 to version 035");
        }

        LOGGER.info("Upgraded from version 031 to version 035 successfully :-)");
    }

    /**
     * Upgrades comments for the specified type.
     * 
     * @param onType the specified type ("article"/"page")
     * @throws Exception exception
     */
    private void upgradeComments(final String onType) throws Exception {
        final Query query = new Query();
        JSONObject cmtRelResults = null;

        if (Article.ARTICLE.equals(onType)) {
            cmtRelResults = articleCommentRepository.get(query);
        } else if (Page.PAGE.equals(onType)) {
            cmtRelResults = pageCommentRepository.get(query);
        } else {
            throw new Exception("Unspecified comment on type!");
        }

        final JSONArray cmtRels = cmtRelResults.getJSONArray(Keys.RESULTS);
        for (int i = 0; i < cmtRels.length(); i++) {
            final JSONObject cmtRel = cmtRels.getJSONObject(i);
            final String cmtId = cmtRel.getString("comment_oId");
            final JSONObject cmt = commentRepository.get(cmtId);

            if (null != cmt) {
                cmt.put(Comment.COMMENT_ON_TYPE, onType);
                final String onId = cmtRel.getString(onType + "_oId");
                cmt.put(Comment.COMMENT_ON_ID, onId);

                commentRepository.update(cmtId, cmt);
            }

            final String relId = cmtRel.getString(Keys.OBJECT_ID);
            if (Article.ARTICLE.equals(onType)) {
                articleCommentRepository.remove(relId);
            } else {
                pageCommentRepository.remove(relId);
            }
        }
    }
}

/**
 * Page-Comment relation Google App Engine repository.
 * 
 * <p>
 * <b>Note</b>: Do NOT use this class excepts {@link UpgradeProcessor#v030ToV031()}.
 * </p>
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.2, Aug 25, 2011
 */
final class PageCommentRepository extends AbstractRepository {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(PageCommentRepository.class.getName());

    /**
     * Gets the {@link PageCommentRepository} singleton.
     *
     * @return the singleton
     */
    public static PageCommentRepository getInstance() {
        return SingletonHolder.SINGLETON;
    }

    /**
     * Private constructor.
     * 
     * @param name the specified name
     */
    private PageCommentRepository(final String name) {
        super(name);
    }

    /**
     * Singleton holder.
     *
     * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
     * @version 1.0.0.0, Jan 12, 2011
     */
    private static final class SingletonHolder {

        /**
         * Singleton.
         */
        private static final PageCommentRepository SINGLETON =
                new PageCommentRepository(Page.PAGE + "_" + Comment.COMMENT);

        /**
         * Private default constructor.
         */
        private SingletonHolder() {
        }
    }
}

/**
 * Article-Comment relation Google App Engine repository.
 * 
 * <p>
 * <b>Note</b>: Do NOT use this class excepts {@link UpgradeProcessor#v030ToV031()}.
 * </p>
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.6, Aug 25, 2011
 */
final class ArticleCommentRepository extends AbstractRepository {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(ArticleCommentRepository.class.getName());

    /**
     * Gets the {@link ArticleCommentRepository} singleton.
     *
     * @return the singleton
     */
    public static ArticleCommentRepository getInstance() {
        return SingletonHolder.SINGLETON;
    }

    /**
     * Private constructor.
     * 
     * @param name the specified name
     */
    private ArticleCommentRepository(final String name) {
        super(name);
    }

    /**
     * Singleton holder.
     *
     * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
     * @version 1.0.0.0, Jan 12, 2011
     */
    private static final class SingletonHolder {

        /**
         * Singleton.
         */
        private static final ArticleCommentRepository SINGLETON =
                new ArticleCommentRepository(Article.ARTICLE + "_"
                                             + Comment.COMMENT);

        /**
         * Private default constructor.
         */
        private SingletonHolder() {
        }
    }
}