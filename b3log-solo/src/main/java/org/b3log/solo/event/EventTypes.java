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
package org.b3log.solo.event;

/**
 * Event types.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.4, Oct 20, 2010
 */
public final class EventTypes {

    /**
     * Indicates a add article event.
     */
    public static final String ADD_ARTICLE = "Add Article";
    /**
     * Indicates a update article event.
     */
    public static final String UPDATE_ARTICLE = "Update Article";
    /**
     * Indicates a remove article event.
     */
    public static final String REMOVE_ARTICLE = "Remove Article";
    /**
     * Indicates a add comment event.
     */
    public static final String ADD_COMMENT = "Add Comment";
    /**
     * Indicates a remove comment event.
     */
    public static final String REMOVE_COMMENT = "Remove Comment";
    /**
     * Indicates the preference load event.
     */
    public static final String PREFERENCE_LOAD = "Preference Load";
    /**
     * Indicates the upgrade event.
     */
    public static final String UPGRADE = "Upgrade";

    /**
     * Private default constructor.
     */
    private EventTypes() {
    }
}
