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

import java.util.Date;
import java.util.Map;
import java.util.Set;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Post(Article/Entry).
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Sep 4, 2010
 */
public interface Post {

    /**
     * Gets the create date.
     *
     * @return create date
     */
    Date getCreateDate();

    /**
     * Sets the create date with the specified create date.
     *
     * @param createDate the specified create date
     */
    void setCreateDate(final Date createDate);

    /**
     * Gets the id.
     *
     * @return id
     */
    String getId();

    /**
     * Sets the id with the specified id.
     *
     * @param id the specified id
     */
    void setId(final String id);

    /**
     * Gets the content.
     *
     * @return content
     */
    String getContent();

    /**
     * Sets the content with the specified content.
     *
     * @param content the specified content
     */
    void setContent(final String content);

    /**
     * Gets the title.
     *
     * @return title
     */
    String getTitle();

    /**
     * Sets the title with the specified title.
     *
     * @param title the specified title
     */
    void setTitle(final String title);

    /**
     * Gets the categories.
     *
     * @return categories
     */
    Set<String> getCategories();

    /**
     * Sets the categories with the specified categories.
     *
     * @param categories the specified categories
     */
    void setCategories(final Set<String> categories);

    /**
     * Adds the specified category.
     *
     * @param category the specified category
     */
    void addCategory(final String category);

    /**
     * Transforms this object to a json object.
     *
     * @return json object
     * @throws JSONException json exception
     */
    JSONObject toJSONObject() throws JSONException;

    /**
     * Transforms this object to a MetaWeblog post structure.
     *
     * @return MetaWeblog post structure
     */
    Map<String, Object> toMetaWeblogPost();
}
