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
package org.b3log.solo.web.processor;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.annotation.RequestProcessing;
import org.b3log.latke.annotation.RequestProcessor;
import org.b3log.latke.model.User;
import org.b3log.latke.repository.AbstractRepository;
import org.b3log.latke.repository.Query;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.HTTPRequestMethod;
import org.b3log.solo.SoloServletListener;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.Comment;
import org.b3log.solo.model.Page;
import org.b3log.solo.model.Preference;
import org.b3log.solo.repository.CommentRepository;
import org.b3log.solo.repository.UserRepository;
import org.b3log.solo.repository.impl.CommentRepositoryImpl;
import org.b3log.solo.repository.impl.UserRepositoryImpl;
import org.b3log.solo.util.Preferences;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Upgrader.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.1.0.2, Oct 2, 2011
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
     * User repository.
     */
    private UserRepository userRepository = UserRepositoryImpl.getInstance();
    /**
     * Preference utility.
     */
    private Preferences preferences = Preferences.getInstance();

    /**
     * Checks upgrade.
     * 
     * @param context the specified context
     */
    @RequestProcessing(value = {"/upgrade/checker.do"},
                       method = HTTPRequestMethod.GET)
    public void upgrade(final HTTPRequestContext context) {
        try {
            final JSONObject preference = preferences.getPreference();
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
            } else {
                LOGGER.warning(
                        "Your B3log Solo is too old to upgrader, please contact the B3log Solo developers");
            }
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Upgrades from v030 to v031.
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
        LOGGER.info("Upgrading from v030 to v031....");

        final Transaction transaction = pageCommentRepository.beginTransaction();
        try {
            upgradeComments(Article.ARTICLE);
            upgradeComments(Page.PAGE);

            final JSONObject preference = preferences.getPreference();
            preference.put(Preference.VERSION, "0.3.1");
            preferences.setPreference(preference);
            final JSONArray users =
                    userRepository.get(new Query()).getJSONArray(Keys.RESULTS);
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
            throw new Exception("Upgrade fail from v030 to v031");
        }

        LOGGER.info("Upgraded from v030 to v031 successfully :-)");
    }

    /**
     * Upgrades from v031 to v035.
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
        LOGGER.info("Upgrading from v031 to v035....");

        final Transaction transaction = userRepository.beginTransaction();
        try {
            final JSONObject preference = preferences.getPreference();

            if (!preference.has(Preference.ARTICLE_LIST_STYLE)) {
                preference.put(Preference.ARTICLE_LIST_STYLE,
                               Preference.Default.DEFAULT_ARTICLE_LIST_STYLE);
            }

            preferences.setPreference(preference);

            transaction.commit();
        } catch (final Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }

            LOGGER.log(Level.SEVERE, "Upgrade comments fail.", e);
            throw new Exception("Upgrade fail from v031 to v035");
        }

        LOGGER.info("Upgraded from v031 to v035 successfully :-)");
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
