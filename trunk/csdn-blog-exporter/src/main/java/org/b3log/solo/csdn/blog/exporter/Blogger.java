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

package org.b3log.solo.csdn.blog.exporter;

/**
 * Blogger.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Aug 15, 2010
 */
public final class Blogger {

    /**
     * User id.
     */
    private String userId;
    /**
     * Archive start date(yyyy/MM).
     */
    private String archiveStartDate;
    /**
     * Archive end date(yyyy/MM).
     */
    private String archiveEndDate;

    /**
     * Gets the archive start date.
     *
     * <p>
     * yyyy/MM
     * </p>
     *
     * @return archive start date
     */
    public String getArchiveStartDate() {
        return archiveStartDate;
    }

    /**
     * Sets the archive start date with the specified archive start date.
     *
     * @param archiveStartDate the specified archive start date(yyyy/MM)
     */
    public void setArchiveStartDate(final String archiveStartDate) {
        this.archiveStartDate = archiveStartDate;
    }

    /**
     * Gets the archive start month.
     * 
     * @return the archive start month
     */
    public int getArchiveStartMonth() {
        return Integer.valueOf(archiveStartDate.split("/")[1]);
    }

    /**
     * Gets the archive start year.
     *
     * @return the archive start year
     */
    public int getArchiveStartYear() {
        return Integer.valueOf(archiveStartDate.split("/")[0]);
    }

    /**
     * Gets the archive end date.
     *
     * <p>
     * yyyy/MM
     * </p>
     *
     * @return archive end date
     */
    public String getArchiveEndDate() {
        return archiveStartDate;
    }

    /**
     * Sets the archive end date with the specified archive end date.
     *
     * @param archiveEndDate the specified archive end date(yyyy/MM)
     */
    public void setArchiveEndDate(final String archiveEndDate) {
        this.archiveEndDate = archiveEndDate;
    }

    /**
     * Gets the archive end month.
     *
     * @return the archive end month
     */
    public int getArchiveEndMonth() {
        return Integer.valueOf(archiveEndDate.split("/")[1]);
    }

    /**
     * Gets the archive end year.
     *
     * @return the archive end year
     */
    public int getArchiveEndYear() {
        return Integer.valueOf(archiveEndDate.split("/")[0]);
    }

    /**
     * Gets the blogger id.
     *
     * @return blogger id
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the blogger id with the specified user id.
     *
     * @param userId the specified user id
     */
    public void setId(final String userId) {
        this.userId = userId;
    }
}
