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

package org.b3log.solo.action.file;

import com.google.appengine.api.datastore.Blob;
import com.google.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.b3log.latke.Keys;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.util.Ids;
import org.b3log.latke.util.Locales;
import org.b3log.solo.model.ErrorPage;
import org.b3log.solo.model.File;
import org.b3log.solo.model.Preference;
import org.b3log.solo.repository.FileRepository;
import org.b3log.solo.util.Preferences;
import org.json.JSONObject;

/**
 * File access via
 * <a href="http://code.google.com/appengine/docs/java/javadoc/com/google/appengine/api/datastore/package-summary.html">
 * Google Data Store Low-level API</a>.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.6, Dec 8, 2010
 */
public final class DataStoreFileAccessServlet extends HttpServlet {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(DataStoreFileAccessServlet.class.getName());
    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;
    /**
     * File repository.
     */
    @Inject
    private FileRepository fileRepository;
    /**
     * Maximum entity size limited by data store.
     */
    private static final long MAX_SIZE = 1024 * 1024;
    /**
     * Preference utilities.
     */
    @Inject
    private Preferences preferenceUtils;

    @Override
    protected void doPost(final HttpServletRequest request,
                          final HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        final ServletFileUpload upload = new ServletFileUpload();
        FileItemIterator iterator = null;

        try {
            iterator = upload.getItemIterator(request);

            while (iterator.hasNext()) {
                final FileItemStream item = iterator.next();
                final InputStream stream = item.openStream();

                final JSONObject preference = preferenceUtils.getPreference();
                if (null == preference) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }

                final String localeString = preference.getString(
                        Preference.LOCALE_STRING);
                final Locale locale = new Locale(
                        Locales.getLanguage(localeString),
                        Locales.getCountry(localeString));
                if (!item.isFormField()) {
                    final ResourceBundle lang =
                            ResourceBundle.getBundle(Keys.LANGUAGE, locale);
                    // XXX: check size before streaming
                    final byte[] contentBytes = IOUtils.toByteArray(stream);
                    if (contentBytes.length > MAX_SIZE) {
                        final String fail = lang.getString("uploadFailLabel");
                        final String cause =
                                lang.getString("exceedMaxUploadSizeLabel");
                        sendError(request, response,
                                  HttpServletResponse.SC_BAD_REQUEST,
                                  fail, cause);
                        return;
                    }

                    if (0 == contentBytes.length) {
                        final String fail = lang.getString("uploadFailLabel");
                        final String cause = lang.getString("fileEmptyLabel");
                        sendError(request, response,
                                  HttpServletResponse.SC_BAD_REQUEST,
                                  fail, cause);
                        return;
                    }

                    final Blob blob = new Blob(contentBytes);
                    final JSONObject file = new JSONObject();
                    final String id = Ids.genTimeMillisId();
                    file.put(Keys.OBJECT_ID, id);

                    file.put(File.FILE_CONTENT_TYPE, item.getContentType());
                    file.put(File.FILE_CONTENT, blob);

                    file.put(File.FILE_DOWNLOAD_COUNT, 0);
                    final Date createDate = new Date();
                    file.put(File.FILE_UPLOAD_DATE, createDate);
                    final String fileName = item.getName();
                    file.put(File.FILE_NAME, fileName);
                    final long fileSize = contentBytes.length;
                    file.put(File.FILE_SIZE, fileSize);
                    final String downloadURL = "/datastore-file-access.do?oId="
                                               + id;
                    file.put(File.FILE_DOWNLOAD_URL, downloadURL);

                    fileRepository.add(file);
                }
            }

            response.sendRedirect("/admin-index.do");
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new ServletException("File upload error: " + e.getMessage());
        }
    }

    @Override
    protected void doGet(final HttpServletRequest request,
                         final HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        final String id = request.getParameter(Keys.OBJECT_ID);
        final Transaction transaction = fileRepository.beginTransaction();
        try {
            final JSONObject file = fileRepository.get(id);
            if (null == file) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            final Blob content = (Blob) file.get(File.FILE_CONTENT);
            final String name = file.getString(File.FILE_NAME);
            String charset = "ISO-8859-1";
            if (request.getLocale().getLanguage().equals("zh")) {
                charset = "GBK";
            }
            response.addHeader("Content-Disposition",
                               "attachment; filename="
                               + new String(name.getBytes(charset), "ISO-8859-1"));
            response.setContentType(file.getString(File.FILE_CONTENT_TYPE));
            response.getOutputStream().write(content.getBytes());
            response.getOutputStream().close();

            final int downloadCnt = file.getInt(File.FILE_DOWNLOAD_COUNT);
            file.put(File.FILE_DOWNLOAD_COUNT, downloadCnt + 1);
            fileRepository.update(id, file);
            transaction.commit();
        } catch (final Exception e) {
            transaction.rollback();
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new ServletException("File download error: " + e.getMessage());
        }
    }

    /**
     * Sends error via {@linkplain HttpServletResponse#sendError(int, java.lang.String)}
     * with the specified error URI and cause.
     *
     * @param request the specified http servlet request
     * @param response the specified http servlet response
     * @param errorCode the specified error code
     * @param errorURI the specified error URI
     * @param cause the specified cause
     * @throws IOException io exception
     */
    private void sendError(final HttpServletRequest request,
                           final HttpServletResponse response,
                           final int errorCode,
                           final String errorURI,
                           final String cause) throws IOException {
        request.setAttribute(ErrorPage.ERROR_PAGE_REQUEST_URI,
                             request.getRequestURI());
        request.setAttribute(ErrorPage.ERROR_PAGE_CAUSE, errorURI + cause);
        response.sendError(errorCode);
    }
}
