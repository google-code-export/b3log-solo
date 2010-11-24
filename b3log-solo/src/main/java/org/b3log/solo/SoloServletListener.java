/*
 * Copyright (c) 2009, 2010, B3log Team
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

import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
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
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.RunsOnEnv;
import org.b3log.latke.cache.Cache;
import org.b3log.latke.cache.CacheFactory;
import org.b3log.latke.event.Event;
import org.b3log.latke.event.EventManager;
import org.b3log.latke.jsonrpc.JSONRpcServiceModule;
import org.b3log.latke.repository.gae.AbstractGAERepository;
import org.b3log.latke.servlet.AbstractServletListener;
import org.b3log.solo.util.UtilModule;
import org.b3log.solo.event.EventModule;
import org.b3log.solo.repository.RepositoryModule;
import org.b3log.solo.util.jabsorb.serializer.StatusCodesSerializer;
import org.b3log.solo.action.ActionModule;
import org.b3log.solo.event.EventTypes;
import org.b3log.solo.filter.FilterModule;
import org.b3log.solo.google.GoogleModule;
import org.b3log.solo.model.Link;
import static org.b3log.solo.model.Preference.*;
import org.b3log.solo.repository.LinkRepository;
import org.b3log.solo.repository.PreferenceRepository;
import org.b3log.solo.sync.SyncModule;
import org.b3log.solo.upgrade.UpgradeModule;
import org.b3log.solo.util.PreferenceUtils;
import org.jabsorb.JSONRPCBridge;
import org.json.JSONObject;

/**
 * B3log Solo servlet listener.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.2.8, Nov 24, 2010
 */
public final class SoloServletListener extends AbstractServletListener {

    /**
     * B3log Solo version.
     */
    public static final String VERSION = "0.2.1";
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
                                             new UpgradeModule(),
                                             new GoogleModule()));
        }

        return ret;
    }

    @Override
    public void contextInitialized(final ServletContextEvent servletContextEvent) {
        Latkes.setRunsOnEnv(RunsOnEnv.GAE);
        LOGGER.info("Latke is running on Google app enigne.");

        super.contextInitialized(servletContextEvent);

        MemcacheServiceFactory.getMemcacheService().clearAll();

        Transaction transaction =
                AbstractGAERepository.DATASTORE_SERVICE.beginTransaction();
        try {
            loadPreference();
            transaction.commit();
        } catch (final Exception e) {
            LOGGER.severe(e.getMessage());

            transaction.rollback();
        }

        transaction =
                AbstractGAERepository.DATASTORE_SERVICE.beginTransaction();
        try {
            initDefaultLinks();
            transaction.commit();
        } catch (final Exception e) {
            LOGGER.severe(e.getMessage());

            transaction.rollback();
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
     * Initializes default links if not found.
     *
     * <ul>
     *   <li>简约设计の艺术</li>
     *   http://88250.b3log.org
     *   <li>Vanessa</li>
     *   http://vanessa.b3log.org
     * </ul>
     */
    private void initDefaultLinks() {
        LOGGER.info("Checking default links....");

        try {
            final Injector injector = getInjector();
            final LinkRepository linkRepository =
                    injector.getInstance(LinkRepository.class);

            final String address1Of88250 =
                    "http://b3log-88250.appspot.com";
            JSONObject linkTo88250 = linkRepository.getByAddress(
                    address1Of88250);
            if (null != linkTo88250) {
                linkRepository.remove(linkTo88250.getString(Keys.OBJECT_ID));
            }
            final String address2Of88250 = "http://88250.b3log.org";
            linkTo88250 = linkRepository.getByAddress(address2Of88250);
            if (null != linkTo88250) {
                linkRepository.remove(linkTo88250.getString(Keys.OBJECT_ID));
            }

            linkTo88250 = new JSONObject();
            linkTo88250.put(Link.LINK_TITLE, "简约设计\u306e艺术").
                    put(Link.LINK_ADDRESS, address2Of88250);
            linkRepository.add(linkTo88250);
            LOGGER.info("Added a link[title=简约设计\u306e艺术] to your links");

            final String address1OfVanessa =
                    "http://b3log-vanessa.appspot.com";
            final String address2OfVanessa = "http://vanessa.b3log.org";
            JSONObject linkToVanessa =
                    linkRepository.getByAddress(address1OfVanessa);
            if (null != linkToVanessa) {
                linkRepository.remove(linkToVanessa.getString(Keys.OBJECT_ID));
            }
            linkToVanessa = linkRepository.getByAddress(address2OfVanessa);
            if (null != linkToVanessa) {
                linkRepository.remove(linkToVanessa.getString(Keys.OBJECT_ID));
            }

            linkToVanessa = new JSONObject();
            linkToVanessa.put(Link.LINK_TITLE, "Vanessa").
                    put(Link.LINK_ADDRESS, address2OfVanessa);
            linkRepository.add(linkToVanessa);
            LOGGER.info("Added a link[title=Vanessa] to your links");
        } catch (final Exception e) {
            LOGGER.log(Level.WARNING, e.getMessage());
        }

        LOGGER.info("Checked default links....");
    }

    /**
     * Loading preference.
     */
    private void loadPreference() {
        LOGGER.info("Loading preference....");

        final Injector injector = getInjector();
        final PreferenceUtils preferenceUtils =
                injector.getInstance(PreferenceUtils.class);
        JSONObject preference = null;

        try {
            preference = preferenceUtils.getPreference();
            if (null == preference) {
                throw new Exception(
                        "Can't not init default skin, please init B3log Solo first");
            }

            final EventManager eventManager =
                    getInjector().getInstance(EventManager.class);
            final PreferenceRepository preferenceRepository =
                    injector.getInstance(PreferenceRepository.class);

            eventManager.fireEventSynchronously(// for upgrade extensions
                    new Event<JSONObject>(EventTypes.PREFERENCE_LOAD,
                                          preference));

            preferenceRepository.update(PREFERENCE, preference);

            final Cache<String, Object> userPreferenceCache =
                    CacheFactory.getCache(PREFERENCE);
            userPreferenceCache.put(PREFERENCE, preference.toString());
        } catch (final Exception e) {
            LOGGER.severe(e.getMessage());

            throw new RuntimeException(e);
        }

        try {
            LOGGER.log(Level.INFO, "Loaded preference[{0}]",
                       preference.toString(JSON_PRINT_INDENT_FACTOR));
        } catch (final Exception e) {
            LOGGER.severe(e.getMessage());
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
}
