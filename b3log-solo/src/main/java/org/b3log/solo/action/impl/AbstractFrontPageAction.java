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

package org.b3log.solo.action.impl;

import freemarker.template.Template;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.latke.action.AbstractCacheablePageAction;
import org.b3log.latke.action.ActionException;
import org.b3log.solo.util.Statistics;
import org.json.JSONObject;

/**
 * Abstract cacheable front page action.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Jul 3, 2011
 */
public abstract class AbstractFrontPageAction extends AbstractCacheablePageAction {

    /**
     * Statistic utilities.
     */
    private Statistics statistics = Statistics.getInstance();

    /**
     * {@inheritDoc}
     * 
     * <p>
     * Blog statistic view count +1.
     * </p>
     */
    @Override
    protected void processPageCacheHit(final JSONObject cachedPageContentObject) {
        statistics.incBlogViewCount();
    }

    /**
     * {@inheritDoc}
     * 
     * <p>
     * Blog statistic view count +1.
     * </p>
     */
    @Override
    protected void afterDoFreeMarkerTemplateAction(
            final HttpServletRequest request, final HttpServletResponse response,
            final Map<?, ?> dataModel, final Template template)
            throws ActionException {
        super.afterDoFreeMarkerTemplateAction(request, response, dataModel,
                                              template);
        statistics.incBlogViewCount();
    }
}
