/*
 * Copyright (C) 2009, 2010, B3log Team
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
package org.b3log.solo.servlet;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Stage;
import com.google.inject.TypeLiteral;
import java.util.ResourceBundle;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletRequestEvent;
import javax.servlet.http.HttpSessionEvent;
import org.apache.log4j.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.servlet.AbstractServletListener;
import org.b3log.solo.util.UtilModule;
import org.b3log.solo.event.EventModule;
import org.b3log.solo.repository.RepositoryModule;
import org.b3log.solo.util.jabsorb.serializer.StatusCodesSerializer;
import org.b3log.latke.util.cache.Cache;
import org.b3log.latke.util.cache.qualifier.LruMemory;
import org.b3log.solo.action.ActionModule;
import org.b3log.solo.repository.PreferenceRepository;
import static org.b3log.solo.model.Preference.*;
import org.b3log.solo.model.Statistic;
import org.b3log.solo.repository.StatisticRepository;
import org.b3log.solo.sync.SyncModule;
import org.jabsorb.JSONRPCBridge;
import org.json.JSONObject;

/**
 * B3log Solo servlet listener.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.6, Aug 18, 2010
 */
public final class B3logServletListener extends AbstractServletListener {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(B3logServletListener.class);
    /**
     * JSONO print indent factor.
     */
    public static final int JSON_PRINT_INDENT_FACTOR = 4;

    /**
     * Public default constructor. Initializes the package name of remote
     * JavaScript services and event listeners.
     */
    public B3logServletListener() {
        setClientRemoteServicePackage("org/b3log/solo/jsonrpc/impl");
    }

    /**
     * Gets the injector.
     *
     * @return injector
     */
    @Override
    public Injector getInjector() {
        final Injector ret = super.getInjector();

        if (null == ret) {
            LOGGER.info("Initializing Guice....");
            setInjector(Guice.createInjector(Stage.PRODUCTION,
                                             new ActionModule(),
                                             new RepositoryModule(),
                                             new EventModule(),
                                             new SyncModule(),
                                             new UtilModule()));
        }

        return ret;
    }

    @Override
    public void contextInitialized(final ServletContextEvent servletContextEvent) {
        super.contextInitialized(servletContextEvent);

        initPreference();
        initStatistic();

        registerRemoteJSServiceSerializers();

        LOGGER.info("Initialized the context");
    }

    /**
     * Initializes statistic.
     */
    private void initStatistic() {
        LOGGER.info("Loading statistic....");
        final Injector injector = getInjector();

        try {
            final StatisticRepository statisticRepository =
                    injector.getInstance(StatisticRepository.class);
            JSONObject statistic = statisticRepository.get(Statistic.STATISTIC);
            if (null == statistic) {
                statistic = new JSONObject();
                statistic.put(Keys.OBJECT_ID, Statistic.STATISTIC);
                statistic.put(Statistic.STATISTIC_BLOG_ARTICLE_COUNT, 0);
                statistic.put(Statistic.STATISTIC_BLOG_VIEW_COUNT, 0);
                statistic.put(Statistic.STATISTIC_BLOG_COMMENT_COUNT, 0);
                statisticRepository.add(statistic);
                LOGGER.info("Created blog statistic");
            }

            LOGGER.info("Loaded statistic[" + statistic.toString(
                    JSON_PRINT_INDENT_FACTOR) + "]");
        } catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Initializes preference.
     */
    private void initPreference() {
        LOGGER.info("Loading preference....");

        JSONObject preference = null;
        try {
            final Injector injector = getInjector();
            @SuppressWarnings(value = "unchecked")
            final Cache<String, JSONObject> cache =
                    (Cache<String, JSONObject>) injector.getInstance(Key.get(new TypeLiteral<Cache<String, ?>>() {
            }, LruMemory.class));

            final String preferenceId = PREFERENCE;
            // Try to load preference from datastore.
            final PreferenceRepository preferenceRepository =
                    injector.getInstance(PreferenceRepository.class);
            preference = preferenceRepository.get(preferenceId);
            if (null == preference) {
                // Try to load preference from configuration file and then
                // persist it.
                preference = new JSONObject();
                final ResourceBundle config = ResourceBundle.getBundle(
                        "b3log-solo");
                final int articleListDisplayCnt = Integer.valueOf(config.
                        getString(ARTICLE_LIST_DISPLAY_COUNT));
                preference.put(ARTICLE_LIST_DISPLAY_COUNT,
                               articleListDisplayCnt);
                final int articleListPaginationWindowSize = Integer.valueOf(
                        config.getString(ARTICLE_LIST_PAGINATION_WINDOW_SIZE));
                preference.put(ARTICLE_LIST_PAGINATION_WINDOW_SIZE,
                               articleListPaginationWindowSize);
                final int mostUsedTagDisplayCnt = Integer.valueOf(config.
                        getString(MOST_USED_TAG_DISPLAY_CNT));
                preference.put(MOST_USED_TAG_DISPLAY_CNT,
                               mostUsedTagDisplayCnt);
                final int mostCommentArticleDisplayCnt =
                        Integer.valueOf(config.getString(
                        MOST_COMMENT_ARTICLE_DISPLAY_CNT));
                preference.put(MOST_COMMENT_ARTICLE_DISPLAY_CNT,
                               mostCommentArticleDisplayCnt);
                final int recentArticleDisplayCnt = Integer.valueOf(config.
                        getString(RECENT_ARTICLE_DISPLAY_CNT));
                preference.put(RECENT_ARTICLE_DISPLAY_CNT,
                               recentArticleDisplayCnt);

                final String blogTitle = config.getString(BLOG_TITLE);
                preference.put(BLOG_TITLE, blogTitle);
                final String blogSubtitle = config.getString(BLOG_SUBTITLE);
                preference.put(BLOG_SUBTITLE, blogSubtitle);

                final String skinFileName = config.getString(SKIN_NAME);
                preference.put(SKIN_NAME, skinFileName);

                preference.put(Keys.OBJECT_ID, preferenceId);
                preferenceRepository.add(preference);
            }

            cache.put(preferenceId, preference);

            LOGGER.info("Loaded preference[" + preference.toString(
                    JSON_PRINT_INDENT_FACTOR) + "]");
        } catch (final Exception e) {
            LOGGER.fatal(e.getMessage(), e);
            throw new RuntimeException("Preference load error!");
        }
    }

    @Override
    public void contextDestroyed(final ServletContextEvent servletContextEvent) {
        super.contextDestroyed(servletContextEvent);

        LOGGER.info("Destroyed the context");
    }

    @Override
    public void sessionCreated(final HttpSessionEvent httpSessionEvent) {
    }

    @Override
    public void sessionDestroyed(final HttpSessionEvent httpSessionEvent) {
    }

    @Override
    public void requestInitialized(final ServletRequestEvent servletRequestEvent) {
    }

    @Override
    public void requestDestroyed(final ServletRequestEvent servletRequestEvent) {
    }

    /**
     * Registers remote JavaScript service serializers.
     */
    private void registerRemoteJSServiceSerializers() {
        final JSONRPCBridge jsonRpcBridge = JSONRPCBridge.getGlobalBridge();

        try {
            jsonRpcBridge.registerSerializer(new StatusCodesSerializer());
        } catch (final Exception e) {
            LOGGER.fatal(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
