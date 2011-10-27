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
package org.b3log.solo;

import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletRequestEvent;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSessionEvent;
import org.b3log.latke.Latkes;
import org.b3log.latke.RuntimeEnv;
import org.b3log.latke.event.EventManager;
import org.b3log.latke.plugin.PluginManager;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.servlet.AbstractServletListener;
import org.b3log.solo.util.jabsorb.serializer.StatusCodesSerializer;
import org.b3log.solo.event.comment.ArticleCommentReplyNotifier;
import org.b3log.solo.event.comment.PageCommentReplyNotifier;
import org.b3log.solo.event.ping.AddArticleGoogleBlogSearchPinger;
import org.b3log.solo.event.ping.UpdateArticleGoogleBlogSearchPinger;
import org.b3log.solo.event.rhythm.ArticleSender;
import org.b3log.solo.event.tencent.microblog.TencentMicroblogSender;
import org.b3log.solo.jsonrpc.impl.AdminService;
import org.b3log.solo.jsonrpc.impl.ArticleService;
import org.b3log.solo.jsonrpc.impl.CommentService;
import org.b3log.solo.jsonrpc.impl.PageService;
import org.b3log.solo.jsonrpc.impl.PreferenceService;
import org.b3log.solo.model.Preference;
import org.b3log.latke.plugin.ViewLoadEventHandler;
import org.b3log.latke.util.Stopwatchs;
import org.b3log.latke.util.Strings;
import org.b3log.solo.event.plugin.PluginRefresher;
import org.b3log.solo.jsonrpc.impl.PluginService;
import org.b3log.solo.repository.PreferenceRepository;
import org.b3log.solo.repository.impl.PreferenceRepositoryImpl;
import org.b3log.solo.repository.impl.UserRepositoryImpl;
import org.b3log.solo.util.Skins;
import org.jabsorb.JSONRPCBridge;
import org.json.JSONObject;

/**
 * B3log Solo servlet listener.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.6.1, Oct 27, 2011
 * @since 0.3.1
 */
public final class SoloServletListener extends AbstractServletListener {

    /**
     * B3log Solo version.
     */
    public static final String VERSION = "0.3.5";
    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(SoloServletListener.class.getName());
    /**
     * JSONO print indent factor.
     */
    public static final int JSON_PRINT_INDENT_FACTOR = 4;
    /**
     * B3log Rhythm address.
     */
    public static final String B3LOG_RHYTHM_ADDRESS =
            "http://b3log-rhythm.appspot.com:80";
    /**
     * Enter escape.
     */
    public static final String ENTER_ESC = "_esc_enter_88250_";

    @Override
    public void contextInitialized(final ServletContextEvent servletContextEvent) {
        Stopwatchs.start("Context Initialized");

        super.contextInitialized(servletContextEvent);

        if (RuntimeEnv.LOCAL == Latkes.getRuntimeEnv()) {
            final String repositoryPath = ResourceBundle.getBundle("local").
                    getString("repositoryPath");

            Latkes.setRepositoryPath(repositoryPath);
            LOGGER.log(Level.INFO, "Sets repository[path={0}]", repositoryPath);
        }

        final PreferenceRepository preferenceRepository =
                PreferenceRepositoryImpl.getInstance();

        final Transaction transaction = preferenceRepository.beginTransaction();

        // Cache will be cleared manaully if necessary, see loadPreference.
        transaction.clearQueryCache(false);
        try {
            loadPreference();

            transaction.commit();
        } catch (final Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

        PluginManager.getInstance().load();

        registerRemoteJSServices();
        registerEventProcessor();

        LOGGER.info("Initialized the context");

        Stopwatchs.end();
        LOGGER.log(Level.FINE, "Stopwatch: {0}{1}",
                   new Object[]{Strings.LINE_SEPARATOR,
                                Stopwatchs.getTimingStat()});
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
        final HttpServletRequest servletRequest =
                (HttpServletRequest) servletRequestEvent.getServletRequest();
        Stopwatchs.start("Request Initialized[requestURI=" + servletRequest.
                getRequestURI() + "]");
    }

    @Override
    public void requestDestroyed(final ServletRequestEvent servletRequestEvent) {
        Stopwatchs.end();

        LOGGER.log(Level.FINE, "Stopwatch: {0}{1}",
                   new Object[]{Strings.LINE_SEPARATOR,
                                Stopwatchs.getTimingStat()});
        Stopwatchs.release();
    }

    /**
     * Loads preference.
     * 
     * <p>
     *   Loads preference from repository, loads skins from skin directory then
     *   sets it into preference if the skins changed. Puts preference into 
     *   cache and persists it to repository finally.
     * </p>
     * 
     * <p>
     *   <b>Note</b>: Do NOT use method 
     *   {@linkplain org.b3log.solo.util.Preferences#getPreference()}
     *   to load it, caused by the method may retrieve it from cache.
     * </p>
     */
    private void loadPreference() {
        Stopwatchs.start("Load Preference");

        LOGGER.info("Loading preference....");

        final PreferenceRepository preferenceRepository =
                PreferenceRepositoryImpl.getInstance();
        JSONObject preference = null;

        try {
            preference = preferenceRepository.get(Preference.PREFERENCE);
            if (null == preference) {
                LOGGER.log(Level.WARNING,
                           "Can't not init default skin, please init B3log Solo first");
                return;
            }

            final Skins skins = Skins.getInstance();
            skins.loadSkins(preference);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);

            throw new IllegalStateException(e);
        }

        Stopwatchs.end();
    }

    /**
     * Registers remote JavaScript service serializers.
     */
    private void registerRemoteJSServiceSerializers() {
        LOGGER.log(Level.INFO,
                   "Registering remote JavaScript service serializers....");
        final JSONRPCBridge jsonRpcBridge = JSONRPCBridge.getGlobalBridge();

        try {
            jsonRpcBridge.registerSerializer(new StatusCodesSerializer());
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new IllegalStateException(e);
        }
        LOGGER.log(Level.INFO,
                   "Registered remote JavaScript service serializers....");
    }

    /**
     * Determines Solo had been initialized.
     *
     * @return {@code true} if it had been initialized, {@code false} otherwise
     */
    // XXX: to find a better way (isInited)?
    public static boolean isInited() {
        try {
            final JSONObject admin =
                    UserRepositoryImpl.getInstance().getAdmin();

            return null != admin;
        } catch (final Exception e) {
            LOGGER.log(Level.WARNING, "B3log Solo has not been initialized");
            return false;
        }
    }

    /**
     * Register event processors.
     */
    private void registerEventProcessor() {
        Stopwatchs.start("Register Event Processors");

        LOGGER.log(Level.INFO, "Registering event processors....");
        try {
            final EventManager eventManager = EventManager.getInstance();

            eventManager.registerListener(new TencentMicroblogSender());
            eventManager.registerListener(new ArticleCommentReplyNotifier());
            eventManager.registerListener(new PageCommentReplyNotifier());
            eventManager.registerListener(new AddArticleGoogleBlogSearchPinger());
            eventManager.registerListener(
                    new UpdateArticleGoogleBlogSearchPinger());
            eventManager.registerListener(new ArticleSender());
            eventManager.registerListener(new PluginRefresher());
            eventManager.registerListener(new ViewLoadEventHandler());
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, "Register event processors error", e);
            throw new IllegalStateException(e);
        }

        LOGGER.log(Level.INFO, "Registering event processors....");

        Stopwatchs.end();
    }

    /**
     * Registers remote JavaScript services.
     */
    private void registerRemoteJSServices() {
        Stopwatchs.start("Register JS SVCs");

        LOGGER.log(Level.INFO, "Registering remote JavaScript services....");
        try {
            final AdminService adminService = AdminService.getInstance();
            JSONRPCBridge.getGlobalBridge().registerObject(adminService.
                    getServiceObjectName(), adminService);

            final ArticleService articleService = ArticleService.getInstance();
            JSONRPCBridge.getGlobalBridge().registerObject(articleService.
                    getServiceObjectName(), articleService);

            final CommentService commentService = CommentService.getInstance();
            JSONRPCBridge.getGlobalBridge().registerObject(commentService.
                    getServiceObjectName(), commentService);

            final PageService pageService = PageService.getInstance();
            JSONRPCBridge.getGlobalBridge().registerObject(pageService.
                    getServiceObjectName(), pageService);

            final PreferenceService preferenceService =
                    PreferenceService.getInstance();
            JSONRPCBridge.getGlobalBridge().registerObject(preferenceService.
                    getServiceObjectName(), preferenceService);

            final PluginService pluginService = PluginService.getInstance();
            JSONRPCBridge.getGlobalBridge().registerObject(pluginService.
                    getServiceObjectName(), pluginService);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, "Register remote JavaScript service error",
                       e);
            throw new IllegalStateException(e);
        }

        LOGGER.log(Level.INFO, "Registered remote JavaScript services....");

        registerRemoteJSServiceSerializers();

        Stopwatchs.end();
    }
}
