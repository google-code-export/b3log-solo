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

package org.b3log.solo.util;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import org.b3log.solo.SoloServletListener;
import org.b3log.solo.model.Skin;
import org.json.JSONArray;
import java.util.TimeZone;
import org.b3log.latke.Keys;
import org.b3log.latke.event.Event;
import org.b3log.latke.event.EventManager;
import org.b3log.latke.util.freemarker.Templates;
import org.b3log.solo.event.EventTypes;
import static org.b3log.solo.model.Skin.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.google.inject.Inject;
import org.b3log.solo.repository.PreferenceRepository;
import org.json.JSONException;
import org.json.JSONObject;
import org.b3log.latke.cache.Cache;
import org.b3log.latke.cache.CacheFactory;
import org.b3log.latke.repository.RepositoryException;
import static org.b3log.solo.model.Preference.*;

/**
 * Preference utilities.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Nov 8, 2010
 */
public final class PreferenceUtils {

    /**
     * Preference cache.
     */
    private static Cache<String, Object> userPreferenceCache;
    /**
     * Preference repository.
     */
    @Inject
    private PreferenceRepository preferenceRepository;
    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(PreferenceUtils.class.getName());
    /**
     * Event manager.
     */
    @Inject
    private EventManager eventManager;
    /**
     * Skin utilities.
     */
    @Inject
    private Skins skins;

    static {
        userPreferenceCache = CacheFactory.getCache(PREFERENCE);
    }

    /**
     * Gets the user preference.
     *
     * @return user preference
     * @throws JSONException json exception
     */
    public JSONObject getPreference() throws JSONException {
        final Object preferenceString = userPreferenceCache.get(PREFERENCE);
        JSONObject ret = null;
        try {
            if (null == preferenceString) {
                LOGGER.info("Load preference from datastore");
                ret = preferenceRepository.get(PREFERENCE);
                if (null == ret) {
                    ret = initPreference();
                }

                loadSkins(ret);

                userPreferenceCache.put(PREFERENCE, ret.toString());
                setPreference(ret);
            } else {
                ret = new JSONObject(preferenceString.toString());
            }
        } catch (final RepositoryException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new RuntimeException(e);
        }

        return ret;
    }

    /**
     * Sets the user preference with the specified preference.
     *
     * @param preference the specified preference
     * @throws JSONException json exception
     * @throws RepositoryException repository exception
     */
    public void setPreference(final JSONObject preference)
            throws JSONException, RepositoryException {
        userPreferenceCache.put(PREFERENCE, preference.toString());
        preferenceRepository.update(PREFERENCE, preference);
    }

    /**
     * Initializes preference.
     *
     * @return preference
     */
    private synchronized JSONObject initPreference() {
        LOGGER.info("Loading preference....");

        JSONObject ret = null;
        try {
            final String preferenceId = PREFERENCE;
            // Try to load preference from datastore.
            ret = preferenceRepository.get(preferenceId);
            if (null == ret) {
                LOGGER.info("Initializing preference....");
                // Try to load default preference and then persist it.
                ret = new JSONObject();
                ret.put(ARTICLE_LIST_DISPLAY_COUNT,
                        DefaultPreference.DEFAULT_ARTICLE_LIST_DISPLAY_COUNT);
                ret.put(ARTICLE_LIST_PAGINATION_WINDOW_SIZE,
                        DefaultPreference.DEFAULT_ARTICLE_LIST_PAGINATION_WINDOW_SIZE);
                ret.put(MOST_USED_TAG_DISPLAY_CNT,
                        DefaultPreference.DEFAULT_MOST_USED_TAG_DISPLAY_COUNT);
                ret.put(MOST_COMMENT_ARTICLE_DISPLAY_CNT,
                        DefaultPreference.DEFAULT_MOST_COMMENT_ARTICLE_DISPLAY_COUNT);
                ret.put(RECENT_ARTICLE_DISPLAY_CNT,
                        DefaultPreference.DEFAULT_RECENT_ARTICLE_DISPLAY_COUNT);
                ret.put(RECENT_COMMENT_DISPLAY_CNT,
                        DefaultPreference.DEFAULT_RECENT_COMMENT_DISPLAY_COUNT);
                ret.put(BLOG_TITLE,
                        DefaultPreference.DEFAULT_BLOG_TITLE);
                ret.put(BLOG_SUBTITLE,
                        DefaultPreference.DEFAULT_BLOG_SUBTITLE);
                ret.put(BLOG_HOST,
                        DefaultPreference.DEFAULT_BLOG_HOST);
                ret.put(ADMIN_GMAIL,
                        DefaultPreference.DEFAULT_ADMIN_GMAIL);
                ret.put(LOCALE_STRING,
                        DefaultPreference.DEFAULT_LANGUAGE);

                ret.put(Keys.OBJECT_ID, preferenceId);
                preferenceRepository.add(ret);
                LOGGER.info("Initialized preference");
            }

            loadSkins(ret);

            eventManager.fireEventSynchronously(// for upgrade extensions
                    new Event<JSONObject>(EventTypes.PREFERENCE_LOAD,
                                          ret));

            preferenceRepository.update(preferenceId, ret);
            userPreferenceCache.put(PREFERENCE, ret.toString());

            LOGGER.log(Level.INFO, "Loaded preference[{0}]",
                       ret.toString(
                    SoloServletListener.JSON_PRINT_INDENT_FACTOR));
        } catch (final Exception e) {
            LOGGER.severe(e.getMessage());
            throw new RuntimeException("Preference load error!");
        }

        Templates.CONFIGURATION.setTimeZone(// XXX: freemarker timezone
                TimeZone.getTimeZone("Asia/Shanghai"));

        return ret;
    }

    /**
     * Loads skins for the specified preference.
     *
     * @param preference the specified preference
     * @throws JSONException json exception
     */
    private void loadSkins(final JSONObject preference) throws JSONException {
        LOGGER.info("Loading skins....");
        final String skinDirName = DefaultPreference.DEFAULT_SKIN_DIR_NAME;
        preference.put(SKIN_DIR_NAME, skinDirName);

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

        final String localeString = preference.getString(
                LOCALE_STRING);
        if ("zh_CN".equals(localeString)) {
            Templates.CONFIGURATION.setTimeZone(
                    TimeZone.getTimeZone("Asia/Shanghai"));
        }

        LOGGER.info("Loaded skins....");
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
