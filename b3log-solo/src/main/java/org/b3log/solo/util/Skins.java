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
package org.b3log.solo.util;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.latke.util.freemarker.Templates;
import org.b3log.solo.SoloServletListener;
import org.b3log.solo.model.Preference;
import static org.b3log.solo.model.Skin.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Skin utilities.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.3, Jan 2, 2011
 */
public final class Skins {

    /**
     * Time zone utilities.
     */
    private TimeZones timeZoneUtils = TimeZones.getInstance();
    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(Skins.class.getName());

    /**
     * Loads skins for the specified preference and initializes templates 
     * loading.
     *
     * @param preference the specified preference
     * @throws JSONException json exception
     */
    public void loadSkins(final JSONObject preference) throws JSONException {
        LOGGER.info("Loading skins....");

        final String currentSkinDirName = preference.getString(SKIN_DIR_NAME);
        preference.put(SKIN_DIR_NAME, currentSkinDirName);

        final String skinName = getSkinName(currentSkinDirName);
        preference.put(SKIN_NAME, skinName);
        LOGGER.log(Level.INFO, "Current skin[name={0}]", skinName);

        final Set<String> skinDirNames = getSkinDirNames();
        LOGGER.log(Level.FINER, "Loaded skins[dirNames={0}]", skinDirNames);
        final JSONArray skinArray = new JSONArray();
        for (final String dirName : skinDirNames) {
            final JSONObject skin = new JSONObject();
            skinArray.put(skin);

            final String name = getSkinName(dirName);
            skin.put(SKIN_NAME, name);
            skin.put(SKIN_DIR_NAME, dirName);
        }

        if (!skinDirNames.contains(currentSkinDirName)) {
            LOGGER.log(Level.WARNING,
                       "Configred skin[dirName={0}] can not find, try to use "
                       + "default skin[dirName=classic] instead.",
                       currentSkinDirName);
            if (!skinDirNames.contains("classic")) {
                LOGGER.log(Level.SEVERE, "Can not find skin[dirName=classic]");

                throw new IllegalStateException(
                        "Can not find default skin[dirName=classic], please "
                        + "redeploy your B3log Solo and make sure contains this "
                        + "default skin!");
            }

            preference.put(SKIN_DIR_NAME, "classic");
            preference.put(SKIN_NAME, "经典淡蓝");
        }

        preference.put(SKINS, skinArray.toString());
        setDirectoryForTemplateLoading(currentSkinDirName);

        final String localeString = preference.getString(
                Preference.LOCALE_STRING);
        if ("zh_CN".equals(localeString)) {
            timeZoneUtils.setTimeZone("Asia/Shanghai");
        }

        LOGGER.info("Loaded skins....");
    }

    /**
     * Sets the directory for template loading with the specified skin directory
     * name.
     * 
     * @param skinDirName the specified skin directory name
     */
    private void setDirectoryForTemplateLoading(final String skinDirName) {
        try {
            final String webRootPath = SoloServletListener.getWebRoot();
            final String skinPath = webRootPath + SKINS + "/"
                                    + skinDirName;
            Templates.CONFIGURATION.setDirectoryForTemplateLoading(
                    new File(skinPath));
        } catch (final IOException e) {
            LOGGER.log(Level.SEVERE, "Loads skins error!", e);
            throw new IllegalStateException(e);
        }
    }

    /**
     * Gets all skin directory names. Scans the
     * {@linkplain SoloServletListener#getWebRoot() Web root}/skins/ directory,
     * using the subdirectory of it as the skin directory name, for example,
     * <pre>
     * ${Web root}/skins/
     *     <b>classic</b>/
     *     <b>simple-art</b>/
     * </pre>
     *
     * @return a set of skin name, returns an empty set if not found
     */
    public Set<String> getSkinDirNames() {
        final String webRootPath = SoloServletListener.getWebRoot();
        final File webRoot = new File(webRootPath + "skins/");
        final File[] skinDirs = webRoot.listFiles(new FileFilter() {

            @Override
            public boolean accept(final File pathname) {
                return pathname.isDirectory() ? true : false;
            }
        });

        final Set<String> ret = new HashSet<String>();
        for (int i = 0; i < skinDirs.length; i++) {
            final File file = skinDirs[i];
            ret.add(file.getName());
        }

        return ret;
    }

    /**
     * Gets the skin name for the specified skin directory name. The skin name
     * was configured in skin.properties file({@code name} as the key) under
     * skin directory specified by the given skin directory name.
     *
     * @param skinDirName the given skin directory name
     * @return skin name, returns {@code null} if not found or error occurs
     * @see #getSkinDirNames()
     */
    public String getSkinName(final String skinDirName) {
        final String webRootPath = SoloServletListener.getWebRoot();
        final File skins = new File(webRootPath + "skins/");
        final File[] skinDirs = skins.listFiles(new FileFilter() {

            @Override
            public boolean accept(final File pathname) {
                return pathname.isDirectory()
                       && pathname.getName().equals(skinDirName) ? true : false;
            }
        });

        if (null == skinDirs) {
            LOGGER.severe("Skin directory is null");

            return null;
        }

        if (1 != skinDirs.length) {
            LOGGER.log(Level.SEVERE, "Skin directory count[{0}]",
                       skinDirs.length);

            return null;
        }

        try {
            final Properties ret = new Properties();
            final String skinPropsPath = skinDirs[0].getPath() + "/"
                                         + "skin.properties";
            ret.load(new FileReader(skinPropsPath));

            return ret.getProperty("name");
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, "Read skin configuration error[msg={0}]",
                       e.getMessage());

            return null;
        }
    }

    /**
     * Gets the {@link Skins} singleton.
     *
     * @return the singleton
     */
    public static Skins getInstance() {
        return SingletonHolder.SINGLETON;
    }

    /**
     * Private default constructor.
     */
    private Skins() {
    }

    /**
     * Singleton holder.
     *
     * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
     * @version 1.0.0.0, Jan 12, 2011
     */
    private static final class SingletonHolder {

        /**
         * Singleton.
         */
        private static final Skins SINGLETON = new Skins();

        /**
         * Private default constructor.
         */
        private SingletonHolder() {
        }
    }
}
