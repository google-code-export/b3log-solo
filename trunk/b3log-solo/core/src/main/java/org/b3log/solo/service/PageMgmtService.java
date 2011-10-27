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
import org.b3log.latke.Keys;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.service.ServiceException;
import org.b3log.solo.model.Page;
import org.b3log.solo.repository.PageRepository;
import org.b3log.solo.repository.impl.PageRepositoryImpl;
import org.b3log.solo.util.Pages;
import org.b3log.solo.util.Permalinks;
import org.json.JSONObject;

/**
 * Page management service.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Oct 27, 2011
 * @since 0.4.0
 */
public final class PageMgmtService {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(PageMgmtService.class.getName());
    /**
     * Page repository.
     */
    private PageRepository pageRepository = PageRepositoryImpl.getInstance();
    /**
     * Page utilities.
     */
    private Pages pageUtils = Pages.getInstance();
    /**
     * Permalink utilities.
     */
    private Permalinks permalinks = Permalinks.getInstance();

    /**
     * Changes the order of a page specified by the given page id to the 
     * specified target order.
     *
     * @param pageId the given page id
     * @param targetPageOrder the specified target order
     * @throws ServiceException service exception
     */
    public void changeOrder(final String pageId, final int targetPageOrder)
            throws ServiceException {

        final Transaction transaction = pageRepository.beginTransaction();
        try {
            final JSONObject page1 = pageRepository.get(pageId);
            final JSONObject page2 = pageRepository.getByOrder(targetPageOrder);

            final int srcPageOrder = page1.getInt(Page.PAGE_ORDER);

            // Swaps
            page2.put(Page.PAGE_ORDER, srcPageOrder);
            page1.put(Page.PAGE_ORDER, targetPageOrder);

            pageRepository.update(page1.getString(Keys.OBJECT_ID), page1);
            pageRepository.update(page2.getString(Keys.OBJECT_ID), page2);

            transaction.commit();
        } catch (final Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }

            LOGGER.log(Level.SEVERE, "Changes page's order failed", e);

            throw new ServiceException(e);
        }
    }

    /**
     * Gets the {@link PageMgmtService} singleton.
     *
     * @return the singleton
     */
    public static PageMgmtService getInstance() {
        return SingletonHolder.SINGLETON;
    }

    /**
     * Private constructor.
     */
    private PageMgmtService() {
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
        private static final PageMgmtService SINGLETON =
                new PageMgmtService();

        /**
         * Private default constructor.
         */
        private SingletonHolder() {
        }
    }
}
