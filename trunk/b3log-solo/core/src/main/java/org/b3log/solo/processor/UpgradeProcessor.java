/*
 * Copyright (c) 2009, 2010, 2011, 2012, B3log Team
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

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.annotation.RequestProcessing;
import org.b3log.latke.annotation.RequestProcessor;
import org.b3log.latke.model.User;
import org.b3log.latke.repository.*;
import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.HTTPRequestMethod;
import org.b3log.latke.servlet.renderer.TextHTMLRenderer;
import org.b3log.latke.taskqueue.TaskQueueService;
import org.b3log.latke.taskqueue.TaskQueueServiceFactory;
import org.b3log.latke.util.CollectionUtils;
import org.b3log.solo.SoloServletListener;
import org.b3log.solo.model.*;
import org.b3log.solo.repository.*;
import org.b3log.solo.repository.impl.*;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Upgrader.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.1.0.8, Mar 28, 2012
 * @since 0.3.1
 */
@RequestProcessor
public final class UpgradeProcessor {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(UpgradeProcessor.class.getName());
    /**
     * Article repository.
     */
    private ArticleRepository articleRepository = ArticleRepositoryImpl.getInstance();
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
    private PreferenceRepository preferenceRepository = PreferenceRepositoryImpl.getInstance();
    /**
     * Task queue service.
     */
    private TaskQueueService taskQueueService = TaskQueueServiceFactory.getTaskQueueService();

    /**
     * Checks upgrade.
     * 
     * @param context the specified context
     */
    @RequestProcessing(value = {"/upgrade/checker.do"}, method = HTTPRequestMethod.GET)
    public void upgrade(final HTTPRequestContext context) {
        final TextHTMLRenderer renderer = new TextHTMLRenderer();
        context.setRenderer(renderer);

        try {
            final JSONObject preference = preferenceRepository.get(Preference.PREFERENCE);
            if (null == preference) {
                LOGGER.log(Level.INFO, "Not init yet");
                renderer.setContent("Not init yet");

                return;
            }

            renderer.setContent("Upgrade successfully ;-)");

            final String version = preference.getString(Preference.VERSION);

            if (SoloServletListener.VERSION.equals(version)) {
                return;
            }

            if ("0.4.0".equals(version)) { // 0.4.0 -> 0.4.1
                v040ToV041();
            } else {
                final String msg = "Your B3log Solo is too old to upgrader, please contact the B3log Solo developers";
                LOGGER.warning(msg);
                renderer.setContent(msg);
            }
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            renderer.setContent("Upgrade failed [" + e.getMessage() + "], please contact the B3log Solo developers or reports this "
                                + "issue directly (https://code.google.com/p/b3log-solo/issues/entry) ");
        }
    }

    /**
     * Upgrades from version 040 to version 041.
     *
     * <p>
     * Model:
     *   <ul>
     *     <li>
     *       Removes all unused properties of {@link Article} (not existed in "article" in repository.json)
     *     </li>
     *     <li>
     *       Adds a property(named {@value UserExt#USER_ARTICLE_COUNT}) to entity {@link User user}
     *     </li>
     *     <li>
     *       Adds a property(named {@value UserExt#USER_PUBLISHED_ARTICLE_COUNT}) to entity {@link User user}
     *     </li>
     *     <li>
     *       Adds a property(named {@value Preference#COMMENTABLE}) to entity {@link Preference}
     *     </li>
     *     <li>
     *       Adds a property(named {@value Preference#FEED_OUTPUT_MODE}) to entity {@link Preference}
     *     </li>
     *   </ul>
     * </p>
     * @throws Exception upgrade fails
     */
    private void v040ToV041() throws Exception {
        LOGGER.info("Upgrading from version 040 to version 041....");

        final Transaction transaction = userRepository.beginTransaction();
        try {
            upgradeArticles();

            // Upgrades user models
            final JSONArray users = userRepository.get(new Query()).getJSONArray(Keys.RESULTS);
            LOGGER.log(Level.INFO, "Users[length={0}]", users.length());
            for (int i = 0; i < users.length(); i++) {
                final JSONObject user = users.getJSONObject(i);
                final String authorEmail = user.getString(User.USER_EMAIL);

                Query query = new Query().addFilter(Article.ARTICLE_AUTHOR_EMAIL, FilterOperator.EQUAL, authorEmail).
                        addFilter(Article.ARTICLE_IS_PUBLISHED, FilterOperator.EQUAL, true);
                final int authorPublishedArticleCnt = articleRepository.get(query).getJSONArray(Keys.RESULTS).length();
                LOGGER.log(Level.INFO, "Author[email={0}] published [{1}] articles", new Object[]{authorEmail, authorPublishedArticleCnt});
                user.put(UserExt.USER_PUBLISHED_ARTICLE_COUNT, authorPublishedArticleCnt);

                query = new Query().addFilter(Article.ARTICLE_AUTHOR_EMAIL, FilterOperator.EQUAL, authorEmail);
                final int authorArticleCnt = articleRepository.get(query).getJSONArray(Keys.RESULTS).length();
                LOGGER.log(Level.INFO, "Author[email={0}] has [{1}] articles totally", new Object[]{authorEmail, authorArticleCnt});
                user.put(UserExt.USER_ARTICLE_COUNT, authorArticleCnt);

                userRepository.update(user.getString(Keys.OBJECT_ID), user);
            }

            // Upgrades page models
            final JSONArray pages = pageRepository.get(new Query()).getJSONArray(Keys.RESULTS);
            LOGGER.log(Level.INFO, "Pages[length={0}]", pages.length());
            for (int i = 0; i < pages.length(); i++) {
                final JSONObject page = pages.getJSONObject(i);
                page.put(Page.PAGE_COMMENTABLE, true);
                page.put(Page.PAGE_OPEN_TARGET, "_self");
                page.put(Page.PAGE_TYPE, "page");

                LOGGER.log(Level.INFO, "Upgraded page[id={0}, title={1}]",
                           new Object[]{page.getString(Keys.OBJECT_ID), page.getString(Page.PAGE_TITLE)});
                pageRepository.update(page.getString(Keys.OBJECT_ID), page);
            }

            // Upgrades preference model
            final JSONObject preference = preferenceRepository.get(Preference.PREFERENCE);

            preference.put(Preference.COMMENTABLE, Preference.Default.DEFAULT_COMMENTABLE);
            preference.put(Preference.FEED_OUTPUT_MODE, Preference.Default.DEFAULT_FEED_OUTPUT_MODE);
            preference.put(Preference.VERSION, "0.4.1");

            preferenceRepository.update(Preference.PREFERENCE, preference);

            transaction.commit();
        } catch (final Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }

            LOGGER.log(Level.SEVERE, "Upgrade comments fail.", e);
            throw new Exception("Upgrade fail from version 040 to version 041");
        }

        LOGGER.info("Upgraded from version 040 to version 041 successfully :-)");
    }

    /**
     * Upgrades articles.
     * 
     * @throws Exception exception
     */
    private void upgradeArticles() throws Exception {
        LOGGER.log(Level.INFO, "Processes remove unused article properties");

        final JSONArray articles = articleRepository.get(new Query()).getJSONArray(Keys.RESULTS);
        if (articles.length() <= 0) {
            LOGGER.log(Level.FINEST, "No unused article properties");
            return;
        }

        final ArticleSignRepositoryImpl articleSignRepository = ArticleSignRepositoryImpl.getInstance();

        final Set<String> keyNames = Repositories.getKeyNames(Article.ARTICLE);
        for (int i = 0; i < articles.length(); i++) {
            final JSONObject article = articles.getJSONObject(i);

            final String articleId = article.optString(Keys.OBJECT_ID);
            final JSONObject articleSignRel = articleSignRepository.getByArticleId(articleId);
            final String signId = articleSignRel.getString("sign_oId");
            LOGGER.log(Level.INFO, "Found an article[id={0}, signId={1}]", new Object[]{articleId, signId});
            article.put(Article.ARTICLE_SIGN_ID, signId);
            article.put(Article.ARTICLE_COMMENTABLE, true);
            article.put(Article.ARTICLE_VIEW_PWD, "");
            
            articleSignRepository.remove(articleSignRel.getString(Keys.OBJECT_ID));

            final JSONArray names = article.names();
            final Set<String> nameSet = CollectionUtils.<String>jsonArrayToSet(names);

            if (nameSet.removeAll(keyNames)) {
                for (final String unusedName : nameSet) {
                    article.remove(unusedName);
                }

                articleRepository.update(article.getString(Keys.OBJECT_ID), article);
                LOGGER.log(Level.INFO, "Found an article[id={0}] exists unused properties[{1}]", new Object[]{articleId, nameSet});
            }
        }
    }
}
