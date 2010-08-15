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
import org.b3log.latke.model.User;
import org.b3log.latke.servlet.AbstractServletListener;
import org.b3log.latke.util.MD5;
import org.b3log.solo.client.ClientModule;
import org.b3log.solo.event.EventModule;
import org.b3log.solo.repository.RepositoryModule;
import org.b3log.solo.util.jabsorb.serializer.StatusCodesSerializer;
import org.b3log.latke.util.UtilModule;
import org.b3log.latke.util.cache.Cache;
import org.b3log.latke.util.cache.qualifier.LruMemory;
import org.b3log.solo.repository.PreferenceRepository;
import org.b3log.solo.repository.UserRepository;
import static org.b3log.solo.model.Preference.*;
import org.jabsorb.JSONRPCBridge;
import org.json.JSONObject;

/**
 * B3log Solo servlet listener.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.3, Aug 15, 2010
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
        setClientRemoteServicePackage("org/b3log/solo/client/jsonrpc/impl");
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
                                             new ClientModule(),
                                             new RepositoryModule(),
                                             new EventModule(),
                                             new UtilModule()));
        }

        return ret;
    }

    @Override
    public void contextInitialized(final ServletContextEvent servletContextEvent) {
        super.contextInitialized(servletContextEvent);

        initAdmin();
        initPreference();

        registerRemoteJSServiceSerializers();

        LOGGER.info("Initialized the context");
    }

    /**
     * Initializes administrator.
     */
    private void initAdmin() {
        final Injector injector = getInjector();

        try {
            final UserRepository userRepository =
                    injector.getInstance(UserRepository.class);
            JSONObject user = userRepository.get(User.USER);
            if (null == user) {
                user = new JSONObject();
                final ResourceBundle config = ResourceBundle.getBundle(
                        "b3log-solo");
                final String pwd = config.getString(User.USER_PASSWORD);
                user.put(User.USER_NAME, User.USER);
                user.put(Keys.OBJECT_ID, User.USER);
                user.put(User.USER_PASSWORD, MD5.hash(pwd));
                userRepository.add(user);
                LOGGER.info("Created user by configuration file");
            }
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
            // Try load preference from datastore.
            final PreferenceRepository preferenceRepository =
                    injector.getInstance(PreferenceRepository.class);
            preference = preferenceRepository.get(preferenceId);
            if (null == preference) {
                // Try load preference from configuration file and then cache and
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
