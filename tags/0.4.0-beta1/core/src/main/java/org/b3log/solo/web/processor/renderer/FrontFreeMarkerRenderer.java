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

import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.latke.action.AbstractCacheablePageAction;
import org.b3log.latke.repository.Repository;
import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.renderer.freemarker.CacheFreeMarkerRenderer;
import org.b3log.solo.model.Common;
import org.b3log.solo.repository.impl.StatisticRepositoryImpl;
import org.b3log.solo.util.Statistics;
import org.b3log.solo.web.util.TopBars;

/**
 * <a href="http://freemarker.org">FreeMarker</a> HTTP response 
 * renderer.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.4, Nov 17, 2011
 * @since 0.3.1
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
     * Puts the top bar replacement flag into data model.
     * </p>
     */
    @Override
    protected void beforeRender(final HTTPRequestContext context)
            throws Exception {
        getDataModel().put(Common.TOP_BAR_REPLACEMENT_FLAG_KEY,
                           Common.TOP_BAR_REPLACEMENT_FLAG);
    }

    @Override
    protected void doRender(final String html,
                            final HttpServletRequest request,
                            final HttpServletResponse response)
            throws Exception {
        final PrintWriter writer = response.getWriter();
        if (response.isCommitted()) { // response has been sent redirect
            writer.flush();

            return;
        }

        final String pageContent =
                (String) request.getAttribute(
                AbstractCacheablePageAction.CACHED_CONTENT);
        String output = html;
        if (null != pageContent) {
            // Adds the top bar HTML content for output
            final String topBarHTML = TopBars.getTopBarHTML(request, response);
            output = html.replace(Common.TOP_BAR_REPLACEMENT_FLAG,
                                  topBarHTML);
        }

        writer.write(output);
        writer.flush();
        writer.close();
    }

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

        try {
            statistics.incBlogViewCount();
        } catch (final Exception e) {
            LOGGER.log(Level.WARNING, "After render failed", e);
        }
    }
}