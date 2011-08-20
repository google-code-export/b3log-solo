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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.latke.Keys;
import org.b3log.latke.action.util.PageCaches;
import org.b3log.latke.repository.Query;
import org.b3log.latke.repository.Repository;
import org.b3log.latke.repository.Transaction;
import org.b3log.solo.repository.impl.ArchiveDateArticleGAERepository;
import org.b3log.solo.repository.impl.ArchiveDateGAERepository;
import org.b3log.solo.repository.impl.ArticleCommentGAERepository;
import org.b3log.solo.repository.impl.ArticleGAERepository;
import org.b3log.solo.repository.impl.ArticleSignGAERepository;
import org.b3log.solo.repository.impl.BlogSyncMgmtGAERepository;
import org.b3log.solo.repository.impl.CommentGAERepository;
import org.b3log.solo.repository.impl.ExternalArticleSoloArticleGAERepository;
import org.b3log.solo.repository.impl.FileGAERepository;
import org.b3log.solo.repository.impl.LinkGAERepository;
import org.b3log.solo.repository.impl.PageCommentGAERepository;
import org.b3log.solo.repository.impl.PageGAERepository;
import org.b3log.solo.repository.impl.PreferenceGAERepository;
import org.b3log.solo.repository.impl.SkinGAERepository;
import org.b3log.solo.repository.impl.StatisticGAERepository;
import org.b3log.solo.repository.impl.TagArticleGAERepository;
import org.b3log.solo.repository.impl.TagGAERepository;
import org.b3log.solo.repository.impl.UserGAERepository;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Remove all data.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Jan 25, 2011
 */
public final class RemoveAllDataServlet extends HttpServlet {

    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(RemoveAllDataServlet.class.getName());

    @Override
    protected void doGet(final HttpServletRequest request,
                         final HttpServletResponse response)
            throws ServletException, IOException {
        try {
            final StringBuilder htmlBuilder = new StringBuilder();
            htmlBuilder.append("<html><head><title>WARNING!</title>");
            htmlBuilder.append("<script type='text/javascript'");
            htmlBuilder.append(
                    "src='http://ajax.googleapis.com/ajax/libs/jquery/1.4.3/jquery.min.js'");
            htmlBuilder.append("></script></head><body>");
            htmlBuilder.append("<button id='ok' onclick='remove()'>");
            htmlBuilder.append("Continue to delete ALL DATA</button></body>");
            htmlBuilder.append("<script type='text/javascript'>");
            htmlBuilder.append("function remove() {");
            htmlBuilder.append("$.ajax({type: 'POST',url: '/rm-all-data.do',");
            htmlBuilder.append(
                    "dataType: 'text/html',success: function(result){");
            htmlBuilder.append("$('html').html(result);}});}</script></html>");
            final PrintWriter printWriter = response.getWriter();

            printWriter.write(htmlBuilder.toString());
            printWriter.flush();
            printWriter.close();
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(final HttpServletRequest request,
                          final HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        LOGGER.info("Removing all data....");

        PageCaches.removeAll();

        boolean succeed = false;
        try {
            remove(ArchiveDateArticleGAERepository.getInstance());

            remove(ArchiveDateGAERepository.getInstance());

            remove(ArticleCommentGAERepository.getInstance());

            remove(ArticleGAERepository.getInstance());

            remove(ArticleSignGAERepository.getInstance());

            remove(BlogSyncMgmtGAERepository.getInstance());

            remove(CommentGAERepository.getInstance());

            remove(ExternalArticleSoloArticleGAERepository.getInstance());

            remove(FileGAERepository.getInstance());

            remove(LinkGAERepository.getInstance());

            remove(PageCommentGAERepository.getInstance());

            remove(PageGAERepository.getInstance());

            remove(PreferenceGAERepository.getInstance());

            remove(SkinGAERepository.getInstance());

            remove(StatisticGAERepository.getInstance());

            remove(TagArticleGAERepository.getInstance());

            remove(TagGAERepository.getInstance());

            remove(UserGAERepository.getInstance());

            succeed = true;
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

        final StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append("<html><head><title>Result</title></head><body>");

        try {
            final PrintWriter printWriter = response.getWriter();
            if (succeed) {
                htmlBuilder.append("Removed all data!");
            } else {
                htmlBuilder.append(
                        "Refresh this page and run this remover again.");
            }
            htmlBuilder.append("</body></html>");

            printWriter.write(htmlBuilder.toString());
            printWriter.flush();
            printWriter.close();
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new ServletException(e);
        }

        PageCaches.removeAll();

        LOGGER.info("Removed all data....");
    }

    /**
     * Removes data in the specified repository.
     *
     * @param repository the specified repository
     * @throws ExecutionException execution exception
     * @throws InterruptedException interrupted exception
     */
    private void remove(final Repository repository)
            throws ExecutionException, InterruptedException {
        final long startTime = System.currentTimeMillis();
        final long step = 20000;

        final Transaction transaction = repository.beginTransaction();

        try {
            final JSONObject result = repository.get(new Query());
            final JSONArray array = result.getJSONArray(Keys.RESULTS);
            for (int i = 0; i < array.length(); i++) {
                final JSONObject object = array.getJSONObject(i);
                repository.remove(object.getString(Keys.OBJECT_ID));

                if (System.currentTimeMillis() >= startTime + step) {
                    break;
                }
            }

            transaction.commit();
        } catch (final Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
        }
    }
}
