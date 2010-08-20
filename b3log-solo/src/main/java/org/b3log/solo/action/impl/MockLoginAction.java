/*
 * Copyright (C) 2009, 2010, B3log Team
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
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.b3log.latke.client.Sessions;

/**
 * Mock login action. _ah/login
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Aug 20, 2010
 */
public final class MockLoginAction extends HttpServlet {

    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(MockLoginAction.class);

    @Override
    protected void doGet(final HttpServletRequest request,
                         final HttpServletResponse response)
            throws ServletException, IOException {
        LOGGER.debug("Mock login action, do get");
        final PrintWriter writer = response.getWriter();
        writer.write("<html>");
        writer.write("<body>");
        writer.write("<form action=\"_ah/login\" method=\"POST\"");
        writer.write("<p>Name: <input type=\"text\" name=\"name\" /></p>"
                     + "<p>Pwd: <input type=\"text\" name=\"pwd\" /></p>"
                     + "<input type=\"submit\" value=\"Submit\" />");
        writer.write("</form>");
        writer.write("</body>");
        writer.write("</html>");

        writer.close();
    }

    @Override
    protected void doPost(final HttpServletRequest request,
                          final HttpServletResponse response)
            throws ServletException, IOException {
        LOGGER.debug("Mock login action, do post");
        final String continueUrl = request.getParameter("continue");
        Sessions.login(request, "", "");
        response.sendRedirect(continueUrl);
    }
}
