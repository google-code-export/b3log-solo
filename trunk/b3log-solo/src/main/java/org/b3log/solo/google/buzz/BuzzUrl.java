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
package org.b3log.solo.google.buzz;

import com.google.api.client.googleapis.GoogleUrl;

/**
 * Buzz URL builder.
 *
 * @author Yaniv Inbar
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Sep 14, 2010
 */
public final class BuzzURL extends GoogleUrl {

    /** 
     * Constructs a new Buzz URL from the specified encoded URL.
     *
     * @param encodedURL the specified encoded URL
     */
    public BuzzURL(final String encodedURL) {
        super(encodedURL);
        alt = "json";
        prettyprint = true;
    }

    /**
     * Gets my activity feed URL.
     *
     * @return feed URL
     */
    public static BuzzURL forMyActivityFeed() {
        return new BuzzURL(
                "https://www.googleapis.com/buzz/v1/activities/@me/@self");
    }

    /**
     * Gets my activity URL by the specified activity id.
     *
     * @param activityId the specified activity id
     * @return activity URL
     */
    public static BuzzURL forMyActivity(final String activityId) {
        final BuzzURL result = forMyActivityFeed();
        result.pathParts.add(activityId);

        return result;
    }
}
