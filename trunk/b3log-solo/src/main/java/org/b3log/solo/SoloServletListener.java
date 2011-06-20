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

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.utils.SystemProperty;
import com.google.appengine.api.utils.SystemProperty.Environment.Value;
import java.io.BufferedInputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletRequestEvent;
import javax.servlet.http.HttpSessionEvent;
import org.b3log.latke.Latkes;
import org.b3log.latke.RunsOnEnv;
import org.b3log.latke.event.Event;
import org.b3log.latke.event.EventManager;
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
import org.b3log.solo.event.sync.impl.BlogJavaAddArticleProcessor;
import org.b3log.solo.event.sync.impl.BlogJavaRemoveArticleProcessor;
import org.b3log.solo.event.sync.impl.BlogJavaUpdateArticleProcessor;
import org.b3log.solo.event.sync.impl.CSDNBlogAddArticleProcessor;
import org.b3log.solo.event.sync.impl.CSDNBlogRemoveArticleProcessor;
import org.b3log.solo.event.sync.impl.CSDNBlogUpdateArticleProcessor;
import org.b3log.solo.event.sync.impl.CnBlogsAddArticleProcessor;
import org.b3log.solo.event.sync.impl.CnBlogsRemoveArticleProcessor;
import org.b3log.solo.event.sync.impl.CnBlogsUpdateArticleProcessor;
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
import org.b3log.solo.repository.PreferenceRepository;
import org.b3log.solo.repository.impl.PreferenceGAERepository;
import org.b3log.solo.util.Preferences;
import org.b3log.solo.util.Skins;
import org.jabsorb.JSONRPCBridge;
import org.json.JSONObject;

/**
 * B3log Solo servlet listener.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.4.3, Jun 19, 2011
 */
public final class SoloServletListener extends AbstractServletListener {

    /**
     * B3log Solo version.
     */
    public static final String VERSION = "0.3.0";
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
        Latkes.setRunsOnEnv(RunsOnEnv.GAE);

        final Value gaeEnvValue = SystemProperty.environment.value();
        if (SystemProperty.Environment.Value.Production == gaeEnvValue) {
            LOGGER.info("B3log Solo runs on [production] environment");
        } else {
            LOGGER.info("B3log Solo runs on [development] environment");
            Latkes.disablePageCache(); // Always disable page cache on dev environment
        }

        LOGGER.log(Level.INFO,
                   "Application[id={0}, version={1}, instanceReplicaId={2}]",
                   new Object[]{SystemProperty.applicationId.get(),
                                SystemProperty.applicationVersion.get(),
                                SystemProperty.instanceReplicaId.get()});

        super.contextInitialized(servletContextEvent);

        registerRemoteJSServices();
        registerEventProcessor();
        
        PluginManager.load();

        final PreferenceRepository preferenceRepository =
                PreferenceGAERepository.getInstance();
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

        loadCaptchas();
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
    public void requestInitialized(final ServletRequestEvent servletRequestEvent) {
    }

    @Override
    public void requestDestroyed(final ServletRequestEvent servletRequestEvent) {
    }

    /**
     * Loading preference.
     */
    private void loadPreference() {
        LOGGER.info("Loading preference....");

        final PreferenceRepository preferenceRepository =
                PreferenceGAERepository.getInstance();
        JSONObject preference = null;

        try {
            preference = preferenceRepository.get(Preference.PREFERENCE);
            if (null == preference) {
                LOGGER.log(Level.SEVERE,
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

        try {
            LOGGER.log(Level.INFO, "Loaded preference[{0}]",
                       preference.toString(JSON_PRINT_INDENT_FACTOR));
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
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
                        ImagesServiceFactory.makeImage(captchaCharData);

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
    // XXX: to find a better way?
    public static boolean isInited() {
        try {
            final DatastoreService datastoreService =
                    DatastoreServiceFactory.getDatastoreService();
            final Key parentKey = KeyFactory.createKey("parentKind",
                                                       "parentKeyName");
            final Key key = KeyFactory.createKey(parentKey,
                                                 Preference.PREFERENCE,
                                                 Preference.PREFERENCE);
            datastoreService.get(key);
            return true;
        } catch (final EntityNotFoundException e) {
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

//            new ActivityCreator(eventManager);
            new TencentMicroblogSender(eventManager);
            new ArticleCommentReplyNotifier(eventManager);
            new PageCommentReplyNotifier(eventManager);
            new AddArticleGoogleBlogSearchPinger(eventManager);
            new UpdateArticleGoogleBlogSearchPinger(eventManager);
            new ArticleSender(eventManager);
            new BlogJavaAddArticleProcessor(eventManager);
            new BlogJavaRemoveArticleProcessor(eventManager);
            new BlogJavaUpdateArticleProcessor(eventManager);
            new CSDNBlogAddArticleProcessor(eventManager);
            new CSDNBlogRemoveArticleProcessor(eventManager);
            new CSDNBlogUpdateArticleProcessor(eventManager);
            new CnBlogsAddArticleProcessor(eventManager);
            new CnBlogsRemoveArticleProcessor(eventManager);
            new CnBlogsUpdateArticleProcessor(eventManager);
            new ViewLoadEventHandler(eventManager);
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
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, "Register remote JavaScript service error",
                       e);
            throw new IllegalStateException(e);
        }
    }
}
