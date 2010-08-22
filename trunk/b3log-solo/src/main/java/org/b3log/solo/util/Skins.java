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
package org.b3log.solo.util;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import org.apache.log4j.Logger;
import org.b3log.solo.servlet.SoloServletListener;

/**
 * Skin utilities.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Aug 22, 2010
 */
public final class Skins {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(Skins.class);

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
        final File webRoot = new File(webRootPath);
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
            LOGGER.error("Skin directory is null");

            return null;
        }

        if (1 != skinDirs.length) {
            LOGGER.error("Skin directory count[" + skinDirs.length + "]");

            return null;
        }

        try {
            final Properties ret = new Properties();
            final String skinPropsPath = skinDirs[0].getPath() + "/"
                                         + "skin.properties";
            ret.load(new FileReader(skinPropsPath));

            return ret.getProperty("name");
        } catch (final Exception e) {
            LOGGER.error("Read skin configuration error[msg=" + e.getMessage()
                         + "]");

            return null;
        }
    }
}
