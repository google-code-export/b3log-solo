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

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Article(post, entry, article, whatever).
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Aug 15, 2010
 */
public final class Article {

    /**
     * Title.
     */
    private String title;
    /**
     * Create date.
     */
    private Date createDate;
    /**
     * Tags(categories).
     */
    private Set<String> tags = new HashSet<String>();
    /**
     * Content.
     */
    private String content;

    /**
     * Gets the create date.
     *
     * @return create date
     */
    public Date getCreateDate() {
        return createDate;
    }

    /**
     * Sets the create date with the specified create date.
     *
     * @param createDate the specified create date
     */
    public void setCreateDate(final Date createDate) {
        this.createDate = createDate;
    }

    /**
     * Gets the tags.
     *
     * @return tags
     */
    public Set<String> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    /**
     * Adds the specified tag.
     *
     * @param tag the specified tag
     */
    public void addTag(final String tag) {
        tags.add(tag);
    }

    /**
     * Gets the title.
     *
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title with the specified title.
     *
     * @param title the specified title
     */
    public void setTitle(final String title) {
        this.title = title;
    }

    /**
     * Gets the content.
     *
     * @return content
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets the content with the specified content.
     *
     * @param content the specified content
     */
    public void setContent(final String content) {
        this.content = content;
    }
}
