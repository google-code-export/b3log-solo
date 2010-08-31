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
package org.b3log.solo.jsonrpc.impl;

import com.google.inject.Inject;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.client.action.ActionException;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.solo.jsonrpc.AbstractJSONRpcService;
import org.b3log.solo.repository.TagRepository;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Tag service for JavaScript client.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.2, Aug 31, 2010
 */
public final class TagService extends AbstractJSONRpcService {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(TagService.class);
    /**
     * Tag repository.
     */
    @Inject
    private TagRepository tagRepository;

    /**
     * Gets all tags.
     *
     * @param request the specified http servlet request
     * @param response the specified http servlet response
     * @return for example,
     * <pre>
     * [
     *     {"tagTitle": "", "tagReferenceCount": int},
     *     ....
     * ]
     * </pre>
     * @throws ActionException action exception
     * @throws IOException io exception
     */
    public JSONArray getTags(final HttpServletRequest request,
                             final HttpServletResponse response)
            throws ActionException, IOException {
        checkAuthorized(request, response);

        JSONArray ret = new JSONArray();
        try {
            final JSONObject result = tagRepository.get(1, Integer.MAX_VALUE);
            final JSONArray tagArray = result.optJSONArray(Keys.RESULTS);
            if (null != tagArray) {
                ret = tagArray;
            }
        } catch (final RepositoryException e) {
            LOGGER.error(e.getMessage(), e);
        }

        return ret;
    }
}
