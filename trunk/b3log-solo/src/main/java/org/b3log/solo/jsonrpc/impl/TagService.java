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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.latke.Keys;
import org.b3log.latke.action.ActionException;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.solo.action.StatusCodes;
import org.b3log.solo.jsonrpc.AbstractGAEJSONRpcService;
import org.b3log.solo.model.Tag;
import org.b3log.solo.repository.TagRepository;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Tag service for JavaScript client.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.3, Oct 25, 2010
 */
public final class TagService extends AbstractGAEJSONRpcService {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(TagService.class.getName());
    /**
     * Tag repository.
     */
    @Inject
    private TagRepository tagRepository;

    /**
     * Gets all unused tags.
     *
     * @param request the specified http servlet request
     * @param response the specified http servlet response
     * @return for example,
     * <pre>
     * [
     *     {"tagTitle": "tag1", "tagReferenceCount": 0},
     *     {"tagTitle": "tag2", "tagReferenceCount": 0},
     *     ....
     * ]
     * </pre>, returns an empty if not found
     * @throws ActionException action exception
     * @throws IOException io exception
     */
    public List<JSONObject> getUnusedTags(final HttpServletRequest request,
                                          final HttpServletResponse response)
            throws ActionException, IOException {
        checkAuthorized(request, response);
        final JSONArray tags = getTags(request, response);
        final List<JSONObject> ret = new ArrayList<JSONObject>();
        try {
            for (int i = 0; i < tags.length(); i++) {
                final JSONObject tag = tags.getJSONObject(i);
                final int tagRefCnt = tag.getInt(Tag.TAG_REFERENCE_COUNT);
                if (0 == tagRefCnt) {
                    ret.add(tag);
                }
            }
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, "Remove unused tags fail: {0}",
                       e.getMessage());
        }

        return ret;
    }

    /**
     * Removes all unused tags.
     *
     * @param request the specified http servlet request
     * @param response the specified http servlet response
     * @return for example,
     * <pre>
     * {
     *     "sc": "REMOVE_UNUSED_TAGS_SUCC"
     * }
     * </pre>
     * @throws ActionException action exception
     * @throws IOException io exception
     */
    public JSONObject removeUnusedTags(final HttpServletRequest request,
                                       final HttpServletResponse response)
            throws ActionException, IOException {
        checkAuthorized(request, response);
        final JSONArray tags = getTags(request, response);

        final JSONObject ret = new JSONObject();
        try {
            for (int i = 0; i < tags.length(); i++) {
                final JSONObject tag = tags.getJSONObject(i);
                final int tagRefCnt = tag.getInt(Tag.TAG_REFERENCE_COUNT);
                if (0 == tagRefCnt) {
                    final String tagId = tag.getString(Keys.OBJECT_ID);
                    tagRepository.remove(tagId);
                }
            }


            ret.put(Keys.STATUS_CODE, StatusCodes.REMOVE_UNUSED_TAGS_SUCC);

        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, "Remove unused tags fail: {0}",
                       e.getMessage());

            try {
                ret.put(Keys.STATUS_CODE, StatusCodes.REMOVE_UNUSED_TAGS_FAIL_);
            } catch (final JSONException ex) {
                LOGGER.severe(ex.getMessage());
                throw new ActionException(e);
            }
        }

        return ret;
    }

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
            LOGGER.severe(e.getMessage());
        }

        return ret;
    }
}
