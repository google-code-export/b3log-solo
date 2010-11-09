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

package org.b3log.solo.event.buzz;

/**
 * This enumeration defines all status codes of Google Buzz actions.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Oct 30, 2010
 */
public final class BuzzStatusCodes {

    /**
     * Indicates post to Google Buzz successfully.
     */
    public static final String POST_TO_BUZZ_SUCC =
            "POST_TO_BUZZ_SUCC";
    /**
     * Indicates post to Google Buzz fail.
     */
    public static final String POST_TO_BUZZ_FAIL =
            "POST_TO_BUZZ_FAIL";
    /**
     * Indicates no post to Google Buzz configured.
     */
    public static final String NO_NEED_TO_POST_BUZZ =
            "NO_NEED_TO_POST_BUZZ";

    /**
     * Private default constructor.
     */
    private BuzzStatusCodes() {
    }
}
