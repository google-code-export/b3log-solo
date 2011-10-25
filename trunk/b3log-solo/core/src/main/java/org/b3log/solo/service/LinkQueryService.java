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
import org.b3log.latke.service.ServiceException;
import org.b3log.solo.model.Link;
import org.b3log.solo.repository.LinkRepository;
import org.b3log.solo.repository.impl.LinkRepositoryImpl;
import org.json.JSONObject;

/**
 * Link query service.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Oct 25, 2011
 * @since 0.4.0
 */
public final class LinkQueryService {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(LinkQueryService.class.getName());
    /**
     * Link repository.
     */
    private LinkRepository linkRepository = LinkRepositoryImpl.getInstance();

    /**
     * Gets a link by the specified link id.
     *
     * @param linkId the specified link id
     * @return for example,
     * <pre>
     * {
     *     "link": {
     *         "oId": "",
     *         "linkTitle": "",
     *         "linkAddress": ""
     *     }
     * }
     * </pre>, returns {@code null} if not found
     * @throws ServiceException service exception
     */
    public JSONObject getLink(final String linkId)
            throws ServiceException {
        final JSONObject ret = new JSONObject();

        try {
            final JSONObject link = linkRepository.get(linkId);
            if (null == link) {
                return null;
            }

            ret.put(Link.LINK, link);

            return ret;
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);

            throw new ServiceException(e);
        }
    }

    /**
     * Gets the {@link LinkQueryService} singleton.
     *
     * @return the singleton
     */
    public static LinkQueryService getInstance() {
        return SingletonHolder.SINGLETON;
    }

    /**
     * Private constructor.
     */
    private LinkQueryService() {
    }

    /**
     * Singleton holder.
     *
     * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
     * @version 1.0.0.0, Oct 25, 2011
     */
    private static final class SingletonHolder {

        /**
         * Singleton.
         */
        private static final LinkQueryService SINGLETON =
                new LinkQueryService();

        /**
         * Private default constructor.
         */
        private SingletonHolder() {
        }
    }
}
