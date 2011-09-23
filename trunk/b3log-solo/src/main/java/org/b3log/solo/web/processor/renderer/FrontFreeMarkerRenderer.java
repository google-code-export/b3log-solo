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
package org.b3log.solo.web.processor.renderer;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.latke.repository.Repository;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.renderer.freemarker.CacheFreeMarkerRenderer;
import org.b3log.solo.repository.impl.StatisticRepositoryImpl;
import org.b3log.solo.util.Statistics;

/**
 * <a href="http://freemarker.org">FreeMarker</a> HTTP response 
 * renderer.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.3, Sep 18, 2011
 */
public final class FrontFreeMarkerRenderer extends CacheFreeMarkerRenderer {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(FrontFreeMarkerRenderer.class.getName());
    /**
     * Statistic utilities.
     */
    private Statistics statistics = Statistics.getInstance();
    /**
     * Statistic repository.
     */
    private Repository statisticRepository =
            StatisticRepositoryImpl.getInstance();

    /**
     * {@inheritDoc}
     * 
     * <p>
     * Blog statistic view count +1.
     * </p>
     */
    @Override
    protected void afterRender(final HTTPRequestContext context)
            throws Exception {
        super.afterRender(context);

        final Transaction transaction = statisticRepository.beginTransaction();
        transaction.clearQueryCache(false);
        try {
            statistics.incBlogViewCount();
            transaction.commit();
        } catch (final Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }

            LOGGER.log(Level.WARNING, "After render failed", e);
        }
    }
}
