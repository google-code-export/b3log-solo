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

import org.b3log.latke.util.Strings;

/**
 * Html utilities.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Aug 18, 2010
 */
public final class Htmls {

    /**
     * Removes all html tags of the specified html string and return a string
     * without any html tags.
     *
     * @param htmlString the specified html string
     * @return a string without html tags, returns "" if the specified html
     * string is {@code null} of ""
     */
    public static String removeHtmlTags(final String htmlString) {
        if (Strings.isEmptyOrNull(htmlString)) {
            return "";
        }

        return htmlString.replaceAll("\\&[a-zA-Z]+;", "").replaceAll(
                "<[^>]*>", "").replaceAll("[(/>)<]", "");
    }

    /**
     * Private default constructor.
     */
    private Htmls() {
    }
}
