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
package org.b3log.solo.web.processor.stat;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.latke.annotation.RequestProcessing;
import org.b3log.latke.annotation.RequestProcessor;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.HTTPRequestMethod;
import org.b3log.latke.servlet.renderer.DoNothingRenderer;
import org.b3log.latke.util.Stopwatchs;
import org.b3log.solo.model.Statistic;
import org.b3log.solo.repository.StatisticRepository;
import org.b3log.solo.repository.impl.StatisticRepositoryImpl;
import org.json.JSONObject;

/**
 * Statistics processor.
 * 
 * <p>
 * Statistics of B3log Solo: 
 * 
 *   <ul>
 *     <li>{@link #statRequest(org.b3log.latke.servlet.HTTPRequestContext) Increments request counting}</li>
 *     <li>{@link #viewCounter(org.b3log.latke.servlet.HTTPRequestContext) Blog/Article view counting}</li>
 *     <li>TODO: 88250, stat proc</li>
 *   </ul>
 * <p>
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.9, Nov 17, 2011
 * @since 0.4.0
 */
@RequestProcessor
public final class StatProcessor {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(StatProcessor.class.getName());
    /**
     * Request statistics URI.
     */
    public static final String STAT_REQUEST_URI = "/console/stat/request";
    /**
     * Statistic repository.
     */
    private StatisticRepository statisticRepository =
            StatisticRepositoryImpl.getInstance();

    /**
     * Increments request counter.
     * 
     * @param context the specified context
     */
    @RequestProcessing(value = STAT_REQUEST_URI,
                       method = HTTPRequestMethod.POST)
    public void statRequest(final HTTPRequestContext context) {
        Stopwatchs.start("Inc Request Stat.");

        context.setRenderer(new DoNothingRenderer());

        LOGGER.log(Level.FINER, "Inc Request Stat.");

        Stopwatchs.end();
    }

    /**
     * Increments request counter.
     * 
     * @param context the specified context
     */
    @RequestProcessing(value = "/console/stat/viewcnt",
                       method = HTTPRequestMethod.POST)
    public void viewCounter(final HTTPRequestContext context) {
        LOGGER.log(Level.INFO, "Sync statistic from memcache to repository");

        context.setRenderer(new DoNothingRenderer());
        final JSONObject statistic =
                (JSONObject) statisticRepository.getCache().
                get(Statistic.STATISTIC);
        if (null == statistic) {
            LOGGER.log(Level.INFO, "Not found statistic in memcache");
        }

        final Transaction transaction = statisticRepository.beginTransaction();
        transaction.clearQueryCache(false);
        try {
            statisticRepository.update(Statistic.STATISTIC, statistic);
            transaction.commit();
        } catch (final RepositoryException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            LOGGER.log(Level.SEVERE, "Updates statistic failed", e);
        }
    }
}
