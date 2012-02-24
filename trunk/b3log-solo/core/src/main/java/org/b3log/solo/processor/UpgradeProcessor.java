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
import org.b3log.latke.taskqueue.Queue;
import org.b3log.latke.taskqueue.Task;
import org.b3log.latke.taskqueue.TaskQueueService;
import org.b3log.latke.taskqueue.TaskQueueServiceFactory;
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
 * @version 1.1.0.7, Feb 23, 2012
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
            if (null == preference) { // Not init yet
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
     *   </ul>
     * </p>
     * @throws Exception upgrade fails
     */
    private void v040ToV041() throws Exception {
        LOGGER.info("Upgrading from version 040 to version 041....");

        final Transaction transaction = userRepository.beginTransaction();
        try {
            final Queue queue = taskQueueService.getQueue("fix-queue");
            final Task task = new Task();
            task.setURL("/fix/normalization/articles/properties");
            task.setRequestMethod(HTTPRequestMethod.POST);
            queue.add(task);
            LOGGER.log(Level.INFO, "Subbmitted a task[{0}] to queue[name=fix-queue]", task.toString());

            // Do not care article properties fix task, keep going upgrade

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

            final JSONObject preference = preferenceRepository.get(Preference.PREFERENCE);
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
}
