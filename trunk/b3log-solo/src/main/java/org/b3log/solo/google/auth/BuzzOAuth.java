/*
 * Copyright (c) 2009, 2010, B3log Team
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

package org.b3log.solo.google.auth;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.inject.Inject;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.latke.Keys;
import org.b3log.latke.util.Locales;
import org.b3log.latke.util.Strings;
import org.b3log.solo.model.Preference;
import org.b3log.solo.util.Preferences;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * OAuth servlet.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.5, Dec 4, 2010
 */
public final class BuzzOAuth extends HttpServlet {

    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(BuzzOAuth.class.getName());
    /**
     * Sleep interval in milliseconds.
     */
    public static final long SLEEP_INTERVAL = 3000;
    /**
     * Http transport.
     */
//    private static HttpTransport httpTransport;
    /**
     * User service.
     */
    private UserService userService = UserServiceFactory.getUserService();
    /**
     * Preference utilities.
     */
    @Inject
    private Preferences preferenceUtils;
    /**
     * OAuth utilities.
     */
    @Inject
    private OAuths oAuths;

//    /**
//     * Gets http transport.
//     *
//     * @return http transport
//     */
//    public static HttpTransport getHttpTransport() {
//        return httpTransport;
//    }

    @Override
    protected void doGet(final HttpServletRequest request,
                         final HttpServletResponse response)
            throws ServletException, IOException {
        checkAuthorized(request, response);

        String googleOAuthConsumerSecret =
                request.getParameter(Preference.GOOGLE_OAUTH_CONSUMER_SECRET);
        if (Strings.isEmptyOrNull(googleOAuthConsumerSecret)) {
            try {
                final JSONObject preference = preferenceUtils.getPreference();
                if (null == preference) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                }

                googleOAuthConsumerSecret = preference.getString(
                        Preference.GOOGLE_OAUTH_CONSUMER_SECRET);
            } catch (final JSONException e) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
                throw new ServletException(e);
            }
        }

        LOGGER.log(Level.FINE, "Google OAuth consumer secret[{0}]",
                   googleOAuthConsumerSecret);
//        httpTransport = GoogleTransport.create();
//        httpTransport.addParser(new JsonCParser());
//        final String authorizationURL =
//                oAuths.getAuthorizationURL(httpTransport,
//                                               googleOAuthConsumerSecret);
//        if (null == authorizationURL) {
//            LOGGER.log(Level.WARNING,
//                       "Can not retrieve Google authorization URL");
//            response.setContentType("text/html");
//            response.setCharacterEncoding("UTF-8");
//
//            final PrintWriter writer = response.getWriter();
//            writer.write(genErrorPageHTMLContent());
//            writer.close();
//
//            return;
//        }
//
//        response.sendRedirect(authorizationURL);
    }

    /**
     * Checks the specified request authorized or not(Http Status Code:
     * Forbidden 403).
     * <p>
     * If the specified request is not send from the logged in administrator,
     * sends an error with status code 403.
     * </p>
     *
     * @param request the specified http servlet request
     * @param response the specified http servlet response
     * @throws IOException io exception
     */
    private void checkAuthorized(final HttpServletRequest request,
                                 final HttpServletResponse response)
            throws IOException {
        if (!userService.isUserLoggedIn() || !userService.isUserAdmin()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
        }
    }

    /**
     * Generates error page HTML content.
     *
     * @return error page HTML contente
     * @throws ServletException servlet exception
     */
    private String genErrorPageHTMLContent() throws ServletException {
        try {
            final JSONObject preference = preferenceUtils.getPreference();
            if (null == preference) {
                throw new ServletException("Not found preference");
            }
            
            final String localeString = preference.optString(
                    Preference.LOCALE_STRING);
            final Locale locale = new Locale(
                    Locales.getLanguage(localeString),
                    Locales.getCountry(localeString));
            final ResourceBundle lang =
                    ResourceBundle.getBundle(Keys.LANGUAGE, locale);
            final String blogTitle = preference.optString(Preference.BLOG_TITLE);

            final StringBuilder htmlContentBuilder = new StringBuilder();

            htmlContentBuilder.append(
                    "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" ");
            htmlContentBuilder.append(
                    "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">");
            htmlContentBuilder.append(
                    "<html xmlns=\"http://www.w3.org/1999/xhtml\"><head><title>");
            htmlContentBuilder.append(blogTitle);
            htmlContentBuilder.append("</title>");
            htmlContentBuilder.append(
                    "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />");
            htmlContentBuilder.append("</head><body>");
            htmlContentBuilder.append(lang.getString("noAuthorizationURLLabel"));
            htmlContentBuilder.append("</body></html>");

            return htmlContentBuilder.toString();
        } catch (final Exception e) {
            throw new ServletException(e);
        }
    }
}
