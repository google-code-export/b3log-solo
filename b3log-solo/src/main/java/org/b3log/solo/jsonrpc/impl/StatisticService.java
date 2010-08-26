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
import org.apache.log4j.Logger;
import org.b3log.solo.jsonrpc.AbstractJSONRpcService;
import org.b3log.solo.model.Statistic;
import org.b3log.solo.repository.StatisticRepository;
import org.json.JSONObject;

/**
 * Statistic service for JavaScript client.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Aug 27, 2010
 */
public final class StatisticService extends AbstractJSONRpcService {

    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(StatisticService.class);
    /**
     * Statistic repository.
     */
    @Inject
    private StatisticRepository statisticRepository;

    /**
     * Gets the statistic.
     *
     * @return for example,
     * <pre>
     * {
     *     "statisticBlogViewCount": int,
     *     "statisticBlogCommentCount": int,
     *     "statisticBlogArticleCount": int
     * }
     * </pre>
     */
    public JSONObject getStatistic() {
        JSONObject ret = null;
        try {
            ret = statisticRepository.get(Statistic.STATISTIC);
        } catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        return ret;
    }

    /**
     * View count +1.
     */
    public void incViewCount() {
        try {
            final JSONObject statistic =
                    statisticRepository.get(Statistic.STATISTIC);
            final int viewCount =
                    statistic.getInt(Statistic.STATISTIC_BLOG_VIEW_COUNT);
            statistic.put(Statistic.STATISTIC_BLOG_VIEW_COUNT, viewCount + 1);

            statisticRepository.update(Statistic.STATISTIC, statistic);
        } catch (final Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
