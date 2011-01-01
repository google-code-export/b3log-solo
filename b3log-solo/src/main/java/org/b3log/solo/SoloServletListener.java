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
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;
import java.io.BufferedInputStream;
import java.net.URL;
import java.util.Collection;
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
import org.b3log.latke.cache.Cache;
import org.b3log.latke.cache.CacheFactory;
import org.b3log.latke.event.Event;
import org.b3log.latke.event.EventManager;
import org.b3log.latke.jsonrpc.JSONRpcServiceModule;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.servlet.AbstractServletListener;
import org.b3log.solo.util.UtilsModule;
import org.b3log.solo.event.EventModule;
import org.b3log.solo.repository.RepositoryModule;
import org.b3log.solo.util.jabsorb.serializer.StatusCodesSerializer;
import org.b3log.solo.action.ActionModule;
import org.b3log.solo.event.EventTypes;
import org.b3log.solo.model.Preference;
import static org.b3log.solo.model.Preference.*;
import org.b3log.solo.repository.PreferenceRepository;
import org.b3log.solo.sync.SyncModule;
import org.b3log.solo.util.Skins;
import org.jabsorb.JSONRPCBridge;
import org.json.JSONObject;

/**
 * B3log Solo servlet listener.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.3.7, Jan 1, 2011
 */
public final class SoloServletListener extends AbstractServletListener {

    /**
     * B3log Solo version.
     */
    public static final String VERSION = "0.2.5 Beta2";
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
    public synchronized Injector getInjector() {
        final Injector ret = super.getInjector();

        if (null == ret) {
            final Value gaeEnvValue =
                    SystemProperty.environment.value();
            LOGGER.info("Initializing Guice....");
            final Collection<Module> modules = createModules();

            if (SystemProperty.Environment.Value.Production
                == gaeEnvValue) {
                LOGGER.info("B3log Solo runs on [production] environment");
                setInjector(Guice.createInjector(Stage.PRODUCTION,
                                                 modules));
            } else {
                LOGGER.info("B3log Solo runs on [development] environment");
                setInjector(Guice.createInjector(Stage.DEVELOPMENT,
                                                 modules));
            }

            LOGGER.log(Level.INFO,
                       "Application[id={0}, version={1}, instanceReplicaId={2}]",
                       new Object[]{SystemProperty.applicationId.get(),
                                    SystemProperty.applicationVersion.get(),
                                    SystemProperty.instanceReplicaId.get()});
        }

        return ret;
    }

    @Override
    public void contextInitialized(final ServletContextEvent servletContextEvent) {
        Latkes.setRunsOnEnv(RunsOnEnv.GAE);
        LOGGER.info("Latke runs on Google app enigne.");

        super.contextInitialized(servletContextEvent);

        final PreferenceRepository preferenceRepository =
                getInjector().getInstance(PreferenceRepository.class);
        final Transaction transaction = preferenceRepository.beginTransaction();
        try {
            loadPreference();
            transaction.commit();
        } catch (final Exception e) {
            transaction.rollback();
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

        final Injector injector = getInjector();
        final PreferenceRepository preferenceRepository =
                injector.getInstance(PreferenceRepository.class);
        JSONObject preference = null;

        try {
            preference = preferenceRepository.get(Preference.PREFERENCE);
            if (null == preference) {
                LOGGER.log(Level.SEVERE,
                           "Can't not init default skin, please init B3log Solo first");
                return;
            }

            final Skins skins = injector.getInstance(Skins.class);
            skins.loadSkins(preference);

            final EventManager eventManager =
                    getInjector().getInstance(EventManager.class);

            eventManager.fireEventSynchronously(// for upgrade extensions
                    new Event<JSONObject>(EventTypes.PREFERENCE_LOAD,
                                          preference));

            preferenceRepository.update(PREFERENCE, preference);

            final Cache<String, Object> userPreferenceCache =
                    CacheFactory.getCache(PREFERENCE);
            userPreferenceCache.put(PREFERENCE, preference.toString());
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);

            throw new RuntimeException(e);
        }

        try {
            LOGGER.log(Level.INFO, "Loaded preference[{0}]",
                       preference.toString(JSON_PRINT_INDENT_FACTOR));
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
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
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new RuntimeException(e);
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
     * Creates all modules used in B3log Solo.
     *
     * @return modules
     */
    private Collection<Module> createModules() {
        final Collection<Module> ret = new HashSet<Module>();

        ret.add(new ActionModule());
        ret.add(new JSONRpcServiceModule());
        ret.add(new RepositoryModule());
        ret.add(new EventModule());
        ret.add(new SyncModule());
        ret.add(new UtilsModule());

        return ret;
    }
}
