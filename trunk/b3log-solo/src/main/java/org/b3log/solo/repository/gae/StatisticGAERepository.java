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
package org.b3log.solo.repository.gae;

import java.util.logging.Logger;
import org.b3log.latke.repository.gae.AbstractGAERepository;
import org.b3log.solo.model.Statistic;
import org.b3log.solo.repository.StatisticRepository;

/**
 * Statistic Google App Engine repository.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Jan 12, 2011
 */
public final class StatisticGAERepository extends AbstractGAERepository
        implements StatisticRepository {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(StatisticGAERepository.class.getName());

    @Override
    public String getName() {
        return Statistic.STATISTIC;
    }

    /**
     * Gets the {@link StatisticGAERepository} singleton.
     *
     * @return the singleton
     */
    public static StatisticGAERepository getInstance() {
        return SingletonHolder.SINGLETON;
    }

    /**
     * Private default constructor.
     */
    private StatisticGAERepository() {
    }

    /**
     * Singleton holder.
     *
     * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
     * @version 1.0.0.0, Jan 12, 2011
     */
    private static final class SingletonHolder {

        /**
         * Singleton.
         */
        private static final StatisticGAERepository SINGLETON =
                new StatisticGAERepository();

        /**
         * Private default constructor.
         */
        private SingletonHolder() {
        }
    }
}
