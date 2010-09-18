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

import com.google.api.client.googleapis.json.JsonCContent;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;
import java.io.IOException;

/**
 * Buzz activity, such as a Buzz post.
 *
 * <p>
 * The JSON of a typical activity looks like this:
 * <pre>
 * {
 *     id: "tag:google.com,2010:buzz:z12puk22ajfyzsz",
 *     object: {
 *         content: "Hey, this is my first Buzz Post!",
 *         ...
 *     },
 *     ...
 * }
 * </pre>
 * 
 * @author Yaniv Inbar
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Sep 18, 2010
 */
public final class BuzzActivity extends GenericJson {

    /** 
     * Activity identifier.
     */
    @Key
    private String id;
    /** 
     * Buzz Object.
     */
    @Key
    private BuzzObject object;

    /**
     * Post this Buzz Activity.
     *
     * @param httpTransport Google http transport
     * @return posted Buzz Activity response from the Buzz server
     * @throws IOException any I/O exception
     */
    public BuzzActivity post(final HttpTransport httpTransport)
            throws IOException {
        final HttpRequest request = httpTransport.buildPostRequest();
        request.url = BuzzUrl.forMyActivityFeed();
        request.content = toContent();

        return request.execute().parseAs(BuzzActivity.class);
    }

    /**
     * Update this Buzz Activity.
     *
     * @param transport Google transport
     * @return updated Buzz Activity response from the Buzz server
     * @throws IOException any I/O exception
     */
    public BuzzActivity update(final HttpTransport transport) throws IOException {
        final HttpRequest request = transport.buildPutRequest();
        request.url = BuzzUrl.forMyActivity(this.id);
        request.content = toContent();

        return request.execute().parseAs(BuzzActivity.class);
    }

    /**
     * Post this Buzz Activity.
     *
     * @param transport Google transport
     * @throws IOException any I/O exception
     */
    public void delete(final HttpTransport transport) throws IOException {
        final HttpRequest request = transport.buildDeleteRequest();
        request.url = BuzzUrl.forMyActivity(this.id);
        request.execute().ignore();
    }

    /**
     * Gets the id.
     *
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the id with the specified id.
     *
     * @param id the specified id
     */
    public void setId(final String id) {
        this.id = id;
    }

    /**
     * Gets the buzz object.
     *
     * @return buzz object
     */
    public BuzzObject getBuzzObject() {
        return object;
    }

    /**
     * Sets the buzz object with the specified buzz object.
     *
     * @param object the specified buzz object
     */
    public void setBuzzObject(final BuzzObject object) {
        this.object = object;
    }

    /** 
     * Returns a new JSON-C content serializer for this Buzz activity.
     * @return JSON-C content
     */
    private JsonCContent toContent() {
        final JsonCContent result = new JsonCContent();
        result.data = this;

        return result;
    }
}
