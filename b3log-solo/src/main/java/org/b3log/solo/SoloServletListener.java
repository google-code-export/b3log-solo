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
package org.b3log.solo;

import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletRequestEvent;
import javax.servlet.http.HttpSessionEvent;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.RunsOnEnv;
import org.b3log.latke.cache.Cache;
import org.b3log.latke.cache.CacheFactory;
import org.b3log.latke.event.Event;
import org.b3log.latke.event.EventManager;
import org.b3log.latke.jsonrpc.JSONRpcServiceModule;
import org.b3log.latke.servlet.AbstractServletListener;
import org.b3log.latke.util.freemarker.Templates;
import org.b3log.solo.util.UtilModule;
import org.b3log.solo.event.EventModule;
import org.b3log.solo.repository.RepositoryModule;
import org.b3log.solo.util.jabsorb.serializer.StatusCodesSerializer;
import org.b3log.solo.action.ActionModule;
import org.b3log.solo.event.EventTypes;
import org.b3log.solo.filter.FilterModule;
import org.b3log.solo.model.Link;
import org.b3log.solo.repository.PreferenceRepository;
import static org.b3log.solo.model.Preference.*;
import org.b3log.solo.model.Skin;
import static org.b3log.solo.model.Skin.*;
import org.b3log.solo.model.Statistic;
import org.b3log.solo.repository.LinkRepository;
import org.b3log.solo.repository.StatisticRepository;
import org.b3log.solo.sync.SyncModule;
import org.b3log.solo.upgrade.UpgradeModule;
import org.b3log.solo.util.PreferenceUtils;
import org.b3log.solo.util.Skins;
import org.jabsorb.JSONRPCBridge;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * B3log Solo servlet listener.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.2.4, Nov 7, 2010
 */
public final class SoloServletListener extends AbstractServletListener {

    /**
     * B3log Solo version.
     */
    public static final String VERSION = "0.2.0";
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
     * English month.
     */
    public static final Map<String, String> EN_MONTHS =
            new HashMap<String, String>();

    static {
        EN_MONTHS.put("01", "January");
        EN_MONTHS.put("02", "February");
        EN_MONTHS.put("03", "March");
        EN_MONTHS.put("04", "April");
        EN_MONTHS.put("05", "May");
        EN_MONTHS.put("06", "June");
        EN_MONTHS.put("07", "Jule");
        EN_MONTHS.put("08", "August");
        EN_MONTHS.put("09", "September");
        EN_MONTHS.put("10", "October");
        EN_MONTHS.put("11", "November");
        EN_MONTHS.put("12", "December");
    }

    /**
     * Public default constructor. Initializes the package name of remote
     * JavaScript services and event listeners.
     */
    public SoloServletListener() {
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
                                             new FilterModule(),
                                             new ActionModule(),
                                             new JSONRpcServiceModule(),
                                             new RepositoryModule(),
                                             new EventModule(),
                                             new SyncModule(),
                                             new UtilModule(),
                                             new UpgradeModule()));
        }

        return ret;
    }

    @Override
    public void contextInitialized(final ServletContextEvent servletContextEvent) {
        Latkes.setRunsOnEnv(RunsOnEnv.GAE);
        LOGGER.info("Latke is running on Google app enigne.");

        super.contextInitialized(servletContextEvent);

        MemcacheServiceFactory.getMemcacheService().clearAll();

        initPreference();
        initStatistic();
        initDefaultLinks();
        loadCaptchas();

        registerRemoteJSServiceSerializers();

        LOGGER.info("Initialized the context");
    }

    /**
     * Initializes default links if not found.
     *
     * <ul>
     *   <li>简约设计の艺术</li>
     *   http://b3log-88250.appspot.com
     *   <li>Vanessa</li>
     *   http://b3log-vanessa.appspot.com
     * </ul>
     */
    private void initDefaultLinks() {
        LOGGER.info("Checking default links....");

        try {
            final Injector injector = getInjector();
            final LinkRepository linkRepository =
                    injector.getInstance(LinkRepository.class);

            final String address88250 = "http://b3log-88250.appspot.com";
            final String addressVanessa = "http://b3log-vanessa.appspot.com";

            JSONObject linkTo88250 = linkRepository.getByAddress(address88250);
            if (null == linkTo88250) {
                linkTo88250 = new JSONObject();
                linkTo88250.put(Link.LINK_TITLE, "简约设计\u306e艺术").
                        put(Link.LINK_ADDRESS, address88250);
                linkRepository.add(linkTo88250);
                LOGGER.info("Added a link [title=简约设计\u306e艺术] to your links");
            }

            JSONObject linkToVanessa =
                    linkRepository.getByAddress(addressVanessa);
            if (null == linkToVanessa) {
                linkToVanessa = new JSONObject();
                linkToVanessa.put(Link.LINK_TITLE, "Vanessa").
                        put(Link.LINK_ADDRESS, addressVanessa);
                linkRepository.add(linkToVanessa);
                LOGGER.info("Added a link [title=Vanessa] to your links");
            }
        } catch (final Exception e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        }

        LOGGER.info("Checked default links....");
    }

    /**
     * Initializes skins from the default configuration for the specified
     * preference.
     *
     * @param preference the specified preference
     * @throws JSONException json exception
     */
    private void initSkins(final JSONObject preference) throws JSONException {
        LOGGER.info("Loading skins....");
        final String skinDirName =
                preference.optString(SKIN_DIR_NAME,
                                     DefaultPreference.DEFAULT_SKIN_DIR_NAME);
        preference.put(SKIN_DIR_NAME, skinDirName);

        final Skins skins = getInjector().getInstance(Skins.class);
        final String skinName = skins.getSkinName(skinDirName);
        preference.put(SKIN_NAME, skinName);

        final Set<String> skinDirNames = skins.getSkinDirNames();
        LOGGER.log(Level.FINER, "Loaded skins[dirNames={0}]", skinDirNames);
        final JSONArray skinArray = new JSONArray();
        for (final String dirName : skinDirNames) {
            final JSONObject skin = new JSONObject();
            skinArray.put(skin);

            final String name = skins.getSkinName(dirName);
            skin.put(SKIN_NAME, name);
            skin.put(SKIN_DIR_NAME, dirName);
        }

        preference.put(SKINS, skinArray.toString());

        try {
            final String webRootPath = SoloServletListener.getWebRoot();
            final String skinPath = webRootPath + Skin.SKINS + "/" + skinDirName;
            Templates.CONFIGURATION.setDirectoryForTemplateLoading(
                    new File(skinPath));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }

        LOGGER.info("Loaded skins....");
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

            LOGGER.log(Level.INFO, "Loaded statistic[{0}]", statistic.toString(
                    JSON_PRINT_INDENT_FACTOR));
        } catch (final Exception e) {
            LOGGER.severe(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Loads captchas.
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

            throw new RuntimeException(e);
        }

        LOGGER.info("Loaded captch images");
    }

    /**
     * Initializes preference.
     */
    private void initPreference() {
        LOGGER.info("Loading preference....");

        try {
            final Injector injector = getInjector();

            final String preferenceId = PREFERENCE;
            // Try to load preference from datastore.
            final PreferenceRepository preferenceRepository =
                    injector.getInstance(PreferenceRepository.class);
            JSONObject userPreference = preferenceRepository.get(preferenceId);
            if (null == userPreference) {
                // Try to load preference from configuration file and then
                // persist it.
                userPreference = new JSONObject();
                userPreference.put(ARTICLE_LIST_DISPLAY_COUNT,
                                   DefaultPreference.DEFAULT_ARTICLE_LIST_DISPLAY_COUNT);
                userPreference.put(ARTICLE_LIST_PAGINATION_WINDOW_SIZE,
                                   DefaultPreference.DEFAULT_ARTICLE_LIST_PAGINATION_WINDOW_SIZE);
                userPreference.put(MOST_USED_TAG_DISPLAY_CNT,
                                   DefaultPreference.DEFAULT_MOST_USED_TAG_DISPLAY_COUNT);
                userPreference.put(MOST_COMMENT_ARTICLE_DISPLAY_CNT,
                                   DefaultPreference.DEFAULT_MOST_COMMENT_ARTICLE_DISPLAY_COUNT);
                userPreference.put(RECENT_ARTICLE_DISPLAY_CNT,
                                   DefaultPreference.DEFAULT_RECENT_ARTICLE_DISPLAY_COUNT);
                userPreference.put(RECENT_COMMENT_DISPLAY_CNT,
                                   DefaultPreference.DEFAULT_RECENT_COMMENT_DISPLAY_COUNT);
                userPreference.put(BLOG_TITLE,
                                   DefaultPreference.DEFAULT_BLOG_TITLE);
                userPreference.put(BLOG_SUBTITLE,
                                   DefaultPreference.DEFAULT_BLOG_SUBTITLE);
                userPreference.put(BLOG_HOST,
                                   DefaultPreference.DEFAULT_BLOG_HOST);
                userPreference.put(ADMIN_GMAIL,
                                   DefaultPreference.DEFAULT_ADMIN_GMAIL);
                userPreference.put(LOCALE_STRING,
                                   DefaultPreference.DEFAULT_LANGUAGE);

                userPreference.put(Keys.OBJECT_ID, preferenceId);
                preferenceRepository.add(userPreference);
            }

            initSkins(userPreference);

            final EventManager eventManager =
                    getInjector().getInstance(EventManager.class);
            eventManager.fireEventSynchronously(// for upgrade extensions
                    new Event<JSONObject>(EventTypes.PREFERENCE_LOAD,
                                          userPreference));

            preferenceRepository.update(preferenceId, userPreference);

            final Cache<String, Object> userPreferenceCache =
                    CacheFactory.getCache(PREFERENCE);
            userPreferenceCache.put(PREFERENCE, userPreference.toString());

            LOGGER.log(Level.INFO, "Loaded preference[{0}]",
                       userPreference.toString(JSON_PRINT_INDENT_FACTOR));
        } catch (final Exception e) {
            LOGGER.severe(e.getMessage());
            throw new RuntimeException("Preference load error!");
        }

        Templates.CONFIGURATION.setTimeZone(// XXX: freemarker timezone
                TimeZone.getTimeZone("Asia/Shanghai"));
    }

    @Override
    public void contextDestroyed(final ServletContextEvent servletContextEvent) {
        super.contextDestroyed(servletContextEvent);

        try {
            final Injector injector = getInjector();
            final PreferenceUtils preferenceUtils =
                    injector.getInstance(PreferenceUtils.class);
            // Flush cache to repository
            final JSONObject preference = preferenceUtils.getPreference();
            preferenceUtils.setPreference(preference);

        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

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
            LOGGER.severe(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Default preference.
     *
     * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
     * @version 1.0.0.2, Nov 1, 2010
     */
    public static final class DefaultPreference {

        /**
         * Default recent article display count.
         */
        public static final int DEFAULT_RECENT_ARTICLE_DISPLAY_COUNT = 10;
        /**
         * Default recent comment display count.
         */
        public static final int DEFAULT_RECENT_COMMENT_DISPLAY_COUNT = 10;
        /**
         * Default most used tag display count.
         */
        public static final int DEFAULT_MOST_USED_TAG_DISPLAY_COUNT = 20;
        /**
         * Default article list display count.
         */
        public static final int DEFAULT_ARTICLE_LIST_DISPLAY_COUNT = 20;
        /**
         * Default article list pagination window size.
         */
        public static final int DEFAULT_ARTICLE_LIST_PAGINATION_WINDOW_SIZE =
                15;
        /**
         * Default most comment article display count.
         */
        public static final int DEFAULT_MOST_COMMENT_ARTICLE_DISPLAY_COUNT = 5;
        /**
         * Default blog title.
         */
        public static final String DEFAULT_BLOG_TITLE = "Solo 示例";
        /**
         * Default blog subtitle.
         */
        public static final String DEFAULT_BLOG_SUBTITLE = "GAE 上的个人博客";
        /**
         * Default skin directory name.
         */
        public static final String DEFAULT_SKIN_DIR_NAME = "classic";
        /**
         * Default administrator mail.
         */
        public static final String DEFAULT_ADMIN_GMAIL = "b3log.solo@gmail.com";
        /**
         * Default blog host.
         */
        public static final String DEFAULT_BLOG_HOST = "localhost:8080";
        /**
         * Default language.
         */
        public static final String DEFAULT_LANGUAGE = "zh_CN";

        /**
         * Private default constructor.
         */
        private DefaultPreference() {
        }
    }
}
