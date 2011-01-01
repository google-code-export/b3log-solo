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

package org.b3log.solo.jsonrpc.impl;

import com.google.inject.Inject;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.latke.action.ActionException;
import org.b3log.solo.jsonrpc.AbstractGAEJSONRpcService;
import org.b3log.solo.model.Statistic;
import org.b3log.solo.repository.StatisticRepository;
import org.b3log.solo.util.Statistics;
import org.b3log.solo.util.Users;
import org.json.JSONObject;

/**
 * Statistic service for JavaScript client.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.4, Dec 3, 2010
 */
public final class StatisticService extends AbstractGAEJSONRpcService {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(StatisticService.class.getName());
    /**
     * Statistic repository.
     */
    @Inject
    private StatisticRepository statisticRepository;
    /**
     * Statistic utilities.
     */
    @Inject
    private Statistics statistics;
    /**
     * User utilities.
     */
    @Inject
    private Users userUtils;

    /**
     * Gets the blog statistic.
     *
     * @param request the specified http servlet request
     * @param response the specified http servlet response
     * @return for example,
     * <pre>
     * {
     *     "statisticBlogViewCount": int,
     *     "statisticBlogCommentCount": int,
     *     "statisticBlogArticleCount": int
     * }
     * </pre>, returns {@code null} if not found
     * @throws ActionException action exception
     * @throws IOException io exception
     */
    public JSONObject getBlogStatistic(final HttpServletRequest request,
                                       final HttpServletResponse response)
            throws ActionException, IOException {
        if (!userUtils.isAdminLoggedIn()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return null;
        }

        JSONObject ret = new JSONObject();
        try {
            ret = statisticRepository.get(Statistic.STATISTIC);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

        return ret;
    }
}
