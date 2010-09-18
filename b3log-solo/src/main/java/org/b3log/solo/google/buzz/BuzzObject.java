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

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;
import java.util.ArrayList;
import java.util.List;

/**
 * Buzz Object.
 *
 * <p>
 * The JSON of a typical Buzz object looks like this:
 * <pre>
 * {
 *     content: "Hey, this is my first Buzz Post!",
 *     ...
 * }
 * </pre>
 *
 * @author Yaniv Inbar
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Sep 14, 2010
 */
public class BuzzObject extends GenericJson {

    /** 
     * HTML content.
     */
    @Key
    private String content;
    /**
     * Attachments.
     */
    @Key
    private List<GenericJson> attachments = new ArrayList<GenericJson>();

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

    /**
     * Adds the specified attachment.
     *
     * @param attachment the specified attachment
     */
    public void addAttachments(final GenericJson attachment) {
        attachments.add(attachment);
    }
}
