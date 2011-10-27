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
package org.b3log.solo.service;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.service.ServiceException;
import org.b3log.solo.model.Link;
import org.b3log.solo.repository.LinkRepository;
import org.b3log.solo.repository.impl.LinkRepositoryImpl;
import org.json.JSONObject;

/**
 * Link management service.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Oct 27, 2011
 * @since 0.4.0
 */
public final class LinkMgmtService {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(LinkMgmtService.class.getName());
    /**
     * Link repository.
     */
    private LinkRepository linkRepository = LinkRepositoryImpl.getInstance();

    /**
     * Adds a link with the specified request json object.
     * 
     * @param requestJSONObject the specified request json object, for example,
     * <pre>
     * {
     *     "link": {
     *         "linkTitle": "",
     *         "linkAddress": ""
     *     }
     * }, see {@link Link} for more details
     * </pre>
     * @return generated link id
     * @throws ServiceException service exception
     */
    public String addLink(final JSONObject requestJSONObject)
            throws ServiceException {
        final Transaction transaction = linkRepository.beginTransaction();

        try {
            final JSONObject link =
                    requestJSONObject.getJSONObject(Link.LINK);
            final int maxOrder = linkRepository.getMaxOrder();
            link.put(Link.LINK_ORDER, maxOrder + 1);
            final String ret = linkRepository.add(link);

            transaction.commit();
            
            return ret;
        } catch (final Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            
            LOGGER.log(Level.SEVERE, "Adds link failed", e);
            throw new ServiceException(e);
        }
    }

    /**
     * Gets the {@link LinkMgmtService} singleton.
     *
     * @return the singleton
     */
    public static LinkMgmtService getInstance() {
        return SingletonHolder.SINGLETON;
    }

    /**
     * Private constructor.
     */
    private LinkMgmtService() {
    }

    /**
     * Singleton holder.
     *
     * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
     * @version 1.0.0.0, Oct 27, 2011
     */
    private static final class SingletonHolder {

        /**
         * Singleton.
         */
        private static final LinkMgmtService SINGLETON =
                new LinkMgmtService();

        /**
         * Private default constructor.
         */
        private SingletonHolder() {
        }
    }
}
