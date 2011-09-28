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

import java.io.BufferedInputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletRequestEvent;
import javax.servlet.http.HttpSessionEvent;
import org.b3log.latke.Latkes;
import org.b3log.latke.RuntimeEnv;
import org.b3log.latke.event.Event;
import org.b3log.latke.event.EventManager;
import org.b3log.latke.image.Image;
import org.b3log.latke.image.ImageService;
import org.b3log.latke.image.ImageServiceFactory;
import org.b3log.latke.plugin.PluginManager;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.servlet.AbstractServletListener;
import org.b3log.solo.util.jabsorb.serializer.StatusCodesSerializer;
import org.b3log.solo.event.EventTypes;
import org.b3log.solo.event.comment.ArticleCommentReplyNotifier;
import org.b3log.solo.event.comment.PageCommentReplyNotifier;
import org.b3log.solo.event.ping.AddArticleGoogleBlogSearchPinger;
import org.b3log.solo.event.ping.UpdateArticleGoogleBlogSearchPinger;
import org.b3log.solo.event.rhythm.ArticleSender;
import org.b3log.solo.event.tencent.microblog.TencentMicroblogSender;
import org.b3log.solo.jsonrpc.impl.AdminService;
import org.b3log.solo.jsonrpc.impl.ArticleService;
import org.b3log.solo.jsonrpc.impl.BlogSyncService;
import org.b3log.solo.jsonrpc.impl.CommentService;
import org.b3log.solo.jsonrpc.impl.FileService;
import org.b3log.solo.jsonrpc.impl.LinkService;
import org.b3log.solo.jsonrpc.impl.PageService;
import org.b3log.solo.jsonrpc.impl.PreferenceService;
import org.b3log.solo.jsonrpc.impl.StatisticService;
import org.b3log.solo.jsonrpc.impl.TagService;
import org.b3log.solo.model.Preference;
import org.b3log.latke.plugin.ViewLoadEventHandler;
import org.b3log.solo.event.plugin.PluginRefresher;
import org.b3log.solo.jsonrpc.impl.PluginService;
import org.b3log.solo.repository.PreferenceRepository;
import org.b3log.solo.repository.impl.PreferenceRepositoryImpl;
import org.b3log.solo.repository.impl.UserRepositoryImpl;
import org.b3log.solo.util.Preferences;
import org.b3log.solo.util.Skins;
import org.jabsorb.JSONRPCBridge;
import org.json.JSONObject;

/**
 * B3log Solo servlet listener.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.5.5, Sep 18, 2011
 */
public final class SoloServletListener extends AbstractServletListener {

    /**
     * B3log Solo version.
     */
    public static final String VERSION = "0.3.1";
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
     * Maximum captcha row.
     */
    public static final int MAX_CAPTCHA_ROW = 10;
    /**
     * Maximum captcha column.
     */
    public static final int MAX_CAPTCHA_COLUM = 10;
    /**
     * Width of a captcha character.
     */
    public static final int WIDTH_CAPTCHA_CHAR = 13;
    /**
     * Height of a captcha character.
     */
    public static final int HEIGHT_CAPTCHA_CHAR = 20;
    /**
     * Captcha &lt;"imageName", Image&gt;.
     * For example &lt;"0/5.png", Image&gt;.
     */
    public static final Map<String, Image> CAPTCHAS =
            new HashMap<String, Image>();
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

        loadCaptchas();

        registerRemoteJSServices();
        registerEventProcessor();
        registerRemoteJSServiceSerializers();

        LOGGER.info("Initialized the context");
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
    public void requestDestroyed(final ServletRequestEvent servletRequestEvent) {
    }

    /**
     * Loading preference.
     * 
     * <p>
     * Loads preference from repository, loads skins from skin directory then
     * sets it into preference, puts preference into cache and saves it to 
     * repository finally.
     * </p>
     * 
     * <p>
     *   <b>Note</b>: Do NOT use method {@linkplain Preferences#getPreference()}
     *   to load it, caused by the method may retrieve it from cache.
     * </p>
     */
    private void loadPreference() {
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

            final EventManager eventManager = EventManager.getInstance();

            eventManager.fireEventSynchronously(// for upgrade extensions
                    new Event<JSONObject>(EventTypes.PREFERENCE_LOAD,
                                          preference));

            Preferences.getInstance().setPreference(preference);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);

            throw new IllegalStateException(e);
        }
    }

    /**
     * Loads captcha.
     */
    private void loadCaptchas() {
        try {
            final URL captchaURL =
                    SoloServletListener.class.getClassLoader().getResource(
                    "captcha.zip");
            final ZipFile zipFile = new ZipFile(captchaURL.getFile());
            final Set<String> imageNames = new HashSet<String>();
            for (int row = 0; row < MAX_CAPTCHA_ROW; row++) {
                for (int column = 0; column < MAX_CAPTCHA_COLUM; column++) {
                    imageNames.add(row + "/" + column + ".png");
                }

            }

            final ImageService imageService =
                    ImageServiceFactory.getImageService();

            final Iterator<String> i = imageNames.iterator();
            while (i.hasNext()) {
                final String imageName = i.next();
                final ZipEntry zipEntry = zipFile.getEntry(imageName);

                final BufferedInputStream bufferedInputStream =
                        new BufferedInputStream(zipFile.getInputStream(zipEntry));
                final byte[] captchaCharData = new byte[bufferedInputStream.
                        available()];
                bufferedInputStream.read(captchaCharData);
                bufferedInputStream.close();

                final Image captchaChar =
                        imageService.makeImage(captchaCharData);

                CAPTCHAS.put(imageName, captchaChar);
            }

            zipFile.close();
        } catch (final Exception e) {
            LOGGER.severe("Can not load captchs!");

            throw new IllegalStateException(e);
        }

        LOGGER.info("Loaded captch images");
    }

    /**
     * Registers remote JavaScript service serializers.
     */
    private void registerRemoteJSServiceSerializers() {
        final JSONRPCBridge jsonRpcBridge = JSONRPCBridge.getGlobalBridge();

        try {
            jsonRpcBridge.registerSerializer(new StatusCodesSerializer());
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new IllegalStateException(e);
        }
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
        try {
            final EventManager eventManager = EventManager.getInstance();

            eventManager.registerListener(new TencentMicroblogSender());
            eventManager.registerListener(new ArticleCommentReplyNotifier());
            eventManager.registerListener(new PageCommentReplyNotifier());
            eventManager.registerListener(new AddArticleGoogleBlogSearchPinger());
            eventManager.registerListener(
                    new UpdateArticleGoogleBlogSearchPinger());
            eventManager.registerListener(new ArticleSender());

            /* 
             * See issue 225 (http://code.google.com/p/b3log-solo/issues/detail?id=225#c4)
             * for more details.
             * 
             * eventManager.registerListener(new BlogJavaAddArticleProcessor());
             * eventManager.registerListener(new BlogJavaRemoveArticleProcessor());
             * eventManager.registerListener(new BlogJavaUpdateArticleProcessor());
             * eventManager.registerListener(new CSDNBlogAddArticleProcessor());
             * eventManager.registerListener(new CSDNBlogRemoveArticleProcessor());
             * eventManager.registerListener(new CSDNBlogUpdateArticleProcessor());
             * eventManager.registerListener(new CnBlogsAddArticleProcessor());
             * eventManager.registerListener(new CnBlogsRemoveArticleProcessor());
             * eventManager.registerListener(new CnBlogsUpdateArticleProcessor());
             */

            eventManager.registerListener(new PluginRefresher());
            eventManager.registerListener(new ViewLoadEventHandler());
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, "Register event processors error", e);
            throw new IllegalStateException(e);
        }
    }

    /**
     * Registers remote JavaScript services.
     */
    private void registerRemoteJSServices() {
        try {
            final AdminService adminService = AdminService.getInstance();
            JSONRPCBridge.getGlobalBridge().registerObject(adminService.
                    getServiceObjectName(), adminService);

            final ArticleService articleService = ArticleService.getInstance();
            JSONRPCBridge.getGlobalBridge().registerObject(articleService.
                    getServiceObjectName(), articleService);

            final BlogSyncService blogSyncService =
                    BlogSyncService.getInstance();
            JSONRPCBridge.getGlobalBridge().registerObject(blogSyncService.
                    getServiceObjectName(), blogSyncService);

            final CommentService commentService = CommentService.getInstance();
            JSONRPCBridge.getGlobalBridge().registerObject(commentService.
                    getServiceObjectName(), commentService);

            final FileService fileService = FileService.getInstance();
            JSONRPCBridge.getGlobalBridge().registerObject(fileService.
                    getServiceObjectName(), fileService);

            final LinkService linkService = LinkService.getInstance();
            JSONRPCBridge.getGlobalBridge().registerObject(linkService.
                    getServiceObjectName(), linkService);

            final PageService pageService = PageService.getInstance();
            JSONRPCBridge.getGlobalBridge().registerObject(pageService.
                    getServiceObjectName(), pageService);

            final PreferenceService preferenceService =
                    PreferenceService.getInstance();
            JSONRPCBridge.getGlobalBridge().registerObject(preferenceService.
                    getServiceObjectName(), preferenceService);

            final StatisticService statisticService =
                    StatisticService.getInstance();
            JSONRPCBridge.getGlobalBridge().registerObject(statisticService.
                    getServiceObjectName(), statisticService);

            final TagService tagService = TagService.getInstance();
            JSONRPCBridge.getGlobalBridge().registerObject(tagService.
                    getServiceObjectName(), tagService);

            final PluginService pluginService = PluginService.getInstance();
            JSONRPCBridge.getGlobalBridge().registerObject(pluginService.
                    getServiceObjectName(), pluginService);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, "Register remote JavaScript service error",
                       e);
            throw new IllegalStateException(e);
        }
    }
}
