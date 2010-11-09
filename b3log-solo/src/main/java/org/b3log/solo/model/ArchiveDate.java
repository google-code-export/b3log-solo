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

package org.b3log.solo.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * This class defines all archive date model relevant keys.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Aug 20, 2010
 */
public final class ArchiveDate {

    /**
     * Archive date.
     */
    public static final String ARCHIVE_DATE = "archiveDate";
    /**
     * Archive dates.
     */
    public static final String ARCHIVE_DATES = "archiveDates";
    /**
     * Key of archive date article count.
     */
    public static final String ARCHIVE_DATE_ARTICLE_COUNT = "archiveDateArticleCount";
    /**
     * Archive date year.
     */
    public static final String ARCHIVE_DATE_YEAR = "archiveDateYear";
    /**
     * Archive date month.
     */
    public static final String ARCHIVE_DATE_MONTH = "archiveDateMonth";
    /**
     * Date format(yyyy/MM).
     */
    public static final DateFormat DATE_FORMAT =
            new SimpleDateFormat("yyyy/MM");

    /**
     * Private default constructor.
     */
    private ArchiveDate() {
    }
}
