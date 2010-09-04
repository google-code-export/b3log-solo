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

package org.b3log.solo.sync;

import java.net.URL;
import java.util.List;

/**
 * Blog.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Sep 4, 2010
 */
public interface Blog {

    /**
     * Gets blogging service provider.
     *
     * @return blogging service provider, for example {@code CSDN Blog}
     */
    String getBloggingServiceProvider();

    /**
     * Sets the user name with the specified user name.
     *
     * @param userName the specified user name
     */
    void setUserName(final String userName);

    /**
     * Sets the user password with the specified user password.
     *
     * @param userPassword the specified user password
     */
    void setUserPassword(final String userPassword);

    /**
     * Gets the user name of this blog.
     *
     * @return user name
     */
    String getUserName();

    /**
     * Gets the user password of this blog.
     *
     * @return user password
     */
    String getUserPassword();

    /**
     * Gets the archive web page URL by the specified archive date.
     *
     * @param archiveDate the specified archive date(yyyy/MM)
     * @return archive web page URL, returns {@code null} if occurs error or
     * not found
     */
    URL getArchivePageURL(final String archiveDate);

    /**
     * Gets the index web page URL.
     *
     * @return index web page URL, returns {@code null} if occurs error or
     * not found
     */
    URL getIndexPageURL();

    /**
     * Gets the all archive dates.
     *
     * @return a set of archive dates(yyyy/MM), returns an empty set if not
     * found any archive date
     */
    List<String> getArchiveDates();

    /**
     * Gets article ids by the specified archive date.
     *
     * @param archiveDate the specified archive date(yyyy/MM)
     * @return a set of article ids, returns an empty list if not found
     */
    List<String> getArticleIdsByArchiveDate(final String archiveDate);
}
