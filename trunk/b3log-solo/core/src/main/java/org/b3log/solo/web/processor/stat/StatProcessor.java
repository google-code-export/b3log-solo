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
import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.HTTPRequestMethod;
import org.b3log.latke.servlet.renderer.DoNothingRenderer;
import org.b3log.latke.util.Stopwatchs;

/**
 * Statistics processor.
 * 
 * <p>
 * Statistics of B3log Solo runtime: 
 * 
 *   <ul>
 *     <li>{@link #incRequest(org.b3log.latke.servlet.HTTPRequestContext) Increments request counter}</li>
 *     <li>TODO: 88250, stat proc</li>
 *   </ul>
 * <p>
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.1.0.8, Nov 11, 2011
 * @since 0.3.1
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
}
