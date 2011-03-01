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

package org.b3log.solo.fix;

import com.google.appengine.api.mail.MailService;
import com.google.appengine.api.mail.MailService.Message;
import com.google.appengine.api.mail.MailServiceFactory;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.latke.action.util.PageCaches;
import org.b3log.solo.model.Preference;
import org.b3log.solo.util.Preferences;
import org.json.JSONObject;

/**
 * Restores the sign of preference to default.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Mar 1, 2011
 */
public final class RestoreSign extends HttpServlet {

    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(RestoreSign.class.getName());
    /**
     * Preference utilities.
     */
    private Preferences preferenceUtils = Preferences.getInstance();
    /**
     * Mail service.
     */
    private static final MailService MAIL_SVC =
            MailServiceFactory.getMailService();

    @Override
    protected void doGet(final HttpServletRequest request,
                         final HttpServletResponse response)
            throws ServletException, IOException {
        final PrintWriter writer = response.getWriter();

        try {
            final JSONObject preference = preferenceUtils.getPreference();
            final String originalSign =
                    preference.getString(Preference.SIGNS);
            preference.put(Preference.SIGNS, Preference.Default.DEFAULT_SIGNS);

            preferenceUtils.setPreference(preference);

            final Message msg = new MailService.Message(
                    preference.getString(Preference.ADMIN_EMAIL),
                    "DL88250@gmail.com", "Restore sign", originalSign);
            MAIL_SVC.send(msg);
            writer.println("Restores signs succeeded.");

            PageCaches.removeAll();
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            writer.println("Restores signs failed, error msg["
                           + e.getMessage() + "]");
        }

        writer.close();
    }
}
