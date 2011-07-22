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

import java.util.Date;
import java.util.TimeZone;
import org.b3log.latke.util.freemarker.Templates;
import org.b3log.solo.model.ArchiveDate;
import org.b3log.solo.model.Comment;

/**
 * Time zone utilities.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Jan 3, 2011
 */
public final class TimeZones {

    /**
     * Gets the current date with the specified time zone id.
     *
     * @param timeZoneId the specified time zone id
     * @return date
     */
    public Date getTime(final String timeZoneId) {
        final TimeZone timeZone = TimeZone.getTimeZone(timeZoneId);
        final TimeZone defaultTimeZone = TimeZone.getDefault();
        TimeZone.setDefault(timeZone);
        final Date ret = new Date();
        TimeZone.setDefault(defaultTimeZone);

        return ret;
    }

    /**
     * Sets time zone by the specified time zone id.
     *
     * <p>
     * This method will call {@linkplain TimeZone#setDefault(java.util.TimeZone)},
     * and set time zone for all date formats and template configuration.
     * </p>
     *
     * @param timeZoneId the specified time zone id
     */
    public void setTimeZone(final String timeZoneId) {
        final TimeZone timeZone = TimeZone.getTimeZone(timeZoneId);

        TimeZone.setDefault(timeZone);
        System.setProperty("user.timezone", timeZoneId);
        ArchiveDate.DATE_FORMAT.setTimeZone(timeZone);
        Comment.DATE_FORMAT.setTimeZone(timeZone);
        Templates.CONFIGURATION.setTimeZone(timeZone);
    }

    /**
     * Gets the {@link TimeZones} singleton.
     *
     * @return the singleton
     */
    public static TimeZones getInstance() {
        return SingletonHolder.SINGLETON;
    }

    /**
     * Private default constructor.
     */
    private TimeZones() {
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
        private static final TimeZones SINGLETON = new TimeZones();

        /**
         * Private default constructor.
         */
        private SingletonHolder() {
        }
    }
}
