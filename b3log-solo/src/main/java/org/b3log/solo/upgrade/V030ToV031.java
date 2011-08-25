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
package org.b3log.solo.upgrade;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.latke.Keys;
import org.b3log.latke.action.util.PageCaches;
import org.b3log.latke.repository.Query;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.repository.gae.AbstractGAERepository;
import org.b3log.solo.SoloServletListener;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.Comment;
import org.b3log.solo.model.Page;
import org.b3log.solo.repository.CommentRepository;
import org.b3log.solo.repository.impl.CommentGAERepository;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Upgrader for <b>v030</b> to <b>v031</b>.
 *
 * <p>
 * Model:
 *   <ul>
 *     <li>
 *       Adds a property(named {@value Comment#COMMENT_ON_ID}) to
 *       entity {@link Comment comment}
 *     </li>
 *     <li>
 *       Adds a property(named {@value Comment#COMMENT_ON_TYPE}) to
 *       entity {@link Comment comment}
 *     </li>
 *     <li>Clears {@code article_comment} repository</li>
 *     <li>Clears {@code page_comment} repository</li>
 *   </ul>
 * </p>
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.2, Aug 5, 2011
 */
public final class V030ToV031 extends HttpServlet {

    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(V030ToV031.class.getName());
    /**
     * Article-Comment repository.
     */
    private ArticleCommentGAERepository articleCommentRepository =
            ArticleCommentGAERepository.getInstance();
    /**
     * Page-Comment repository.
     */
    private PageCommentGAERepository pageCommentRepository =
            PageCommentGAERepository.getInstance();
    /**
     * Comment repository.
     */
    private CommentRepository commentRepository =
            CommentGAERepository.getInstance();

    @Override
    protected void doGet(final HttpServletRequest request,
                         final HttpServletResponse response)
            throws ServletException, IOException {
        final PrintWriter writer = response.getWriter();
        if ("0.3.1".equals(SoloServletListener.VERSION)) {
            LOGGER.info("Checking for consistency....");

            upgradeComments();

            final String upgraded =
                    "Upgraded from v030 to v031 successfully :-)";
            LOGGER.info(upgraded);
            writer.print(upgraded);

            writer.close();

            LOGGER.info("Checked for consistency");
        } else {
            final String ignored =
                    "Ignored upgrade to v031, caused by the old version is NOT v030!";
            LOGGER.info(ignored);
            writer.print(ignored);

            writer.close();
        }

        PageCaches.removeAll();
    }

    /**
     * Upgrades comments model.
     *
     * @throws ServletException upgrade fails
     */
    private void upgradeComments() throws ServletException {
        final Transaction transaction = pageCommentRepository.beginTransaction();
        try {
            upgradeComments(Article.ARTICLE);
            upgradeComments(Page.PAGE);

            transaction.commit();
        } catch (final Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            LOGGER.log(Level.SEVERE, "Upgrade comments fail.", e);
            throw new ServletException("Upgrade fail from v030 to v031");
        }
    }

    /**
     * Upgrades comments for the specified type.
     * 
     * @param onType the specified type ("article"/"page")
     * @throws Exception exception
     */
    private void upgradeComments(final String onType) throws Exception {
        final Query query = new Query();
        JSONObject cmtRelResults = null;

        if (Article.ARTICLE.equals(onType)) {
            cmtRelResults = articleCommentRepository.get(query);
        } else if (Page.PAGE.equals(onType)) {
            cmtRelResults = pageCommentRepository.get(query);
        } else {
            throw new Exception("Unspecified comment on type!");
        }

        final JSONArray cmtRels =
                cmtRelResults.getJSONArray(Keys.RESULTS);
        for (int i = 0; i < cmtRels.length(); i++) {
            final JSONObject cmtRel = cmtRels.getJSONObject(i);
            final String cmtId = cmtRel.getString("comment_oId");
            final JSONObject cmt = commentRepository.get(cmtId);

            if (null != cmt) {
                cmt.put(Comment.COMMENT_ON_TYPE, onType);
                final String onId = cmtRel.getString(onType + "_oId");
                cmt.put(Comment.COMMENT_ON_ID, onId);

                commentRepository.update(cmtId, cmt);
            }

            final String relId = cmtRel.getString(Keys.OBJECT_ID);
            if (Article.ARTICLE.equals(onType)) {
                articleCommentRepository.remove(relId);
            } else {
                pageCommentRepository.remove(relId);
            }
        }
    }
}

/**
 * Page-Comment relation Google App Engine repository.
 * 
 * <p>
 * <b>Note</b>: Do NOT use this class excepts {@link V030ToV031}.
 * </p>
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.2, Aug 25, 2011
 */
final class PageCommentGAERepository extends AbstractGAERepository {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(PageCommentGAERepository.class.getName());

    @Override
    public String getName() {
        return Page.PAGE + "_" + Comment.COMMENT;
    }

    /**
     * Gets the {@link PageCommentGAERepository} singleton.
     *
     * @return the singleton
     */
    public static PageCommentGAERepository getInstance() {
        return SingletonHolder.SINGLETON;
    }

    /**
     * Private default constructor.
     */
    private PageCommentGAERepository() {
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
        private static final PageCommentGAERepository SINGLETON =
                new PageCommentGAERepository();

        /**
         * Private default constructor.
         */
        private SingletonHolder() {
        }
    }
}

/**
 * Article-Comment relation Google App Engine repository.
 * 
 * <p>
 * <b>Note</b>: Do NOT use this class excepts {@link V030ToV031}.
 * </p>
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.6, Aug 25, 2011
 */
final class ArticleCommentGAERepository extends AbstractGAERepository {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(ArticleCommentGAERepository.class.getName());

    @Override
    public String getName() {
        return Article.ARTICLE + "_" + Comment.COMMENT;
    }

    /**
     * Gets the {@link ArticleCommentGAERepository} singleton.
     *
     * @return the singleton
     */
    public static ArticleCommentGAERepository getInstance() {
        return SingletonHolder.SINGLETON;
    }

    /**
     * Private default constructor.
     */
    private ArticleCommentGAERepository() {
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
        private static final ArticleCommentGAERepository SINGLETON =
                new ArticleCommentGAERepository();

        /**
         * Private default constructor.
         */
        private SingletonHolder() {
        }
    }
}