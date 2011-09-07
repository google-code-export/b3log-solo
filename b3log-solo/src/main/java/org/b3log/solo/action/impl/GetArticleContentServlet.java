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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.latke.util.Strings;
import org.b3log.solo.model.Article;
import org.b3log.solo.repository.ArticleRepository;
import org.b3log.solo.repository.impl.ArticleGAERepository;
import org.json.JSONObject;

/**
 * Gets the content of an article.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Sep 7, 2011
 */
public final class GetArticleContentServlet extends HttpServlet {

    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(GetArticleContentServlet.class.getName());
    /**
     * Article repository.
     */
    private ArticleRepository articleRepository =
            ArticleGAERepository.getInstance();

    @Override
    protected void doGet(final HttpServletRequest request,
                         final HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        request.setCharacterEncoding("UTF-8");

        // XXX: Determines request coming from outer
        final String articleId = request.getParameter("id");

        if (Strings.isEmptyOrNull(articleId)) {
            return;
        }

        try {
            final JSONObject article = articleRepository.get(articleId);
            if (null == article) {
                return;
            }

            final String content = article.getString(Article.ARTICLE_CONTENT);

            final PrintWriter writer = response.getWriter();
            writer.write(content);
            writer.close();
        } catch (final Exception e) {
            LOGGER.log(Level.WARNING, "Updates article random value failed.");
        }
    }
}