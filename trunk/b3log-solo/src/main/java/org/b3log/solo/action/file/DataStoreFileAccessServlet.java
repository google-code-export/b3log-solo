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
package org.b3log.solo.action.file;

import com.google.appengine.api.datastore.Blob;
import com.google.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
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
import org.b3log.latke.util.Ids;
import org.b3log.solo.SoloServletListener;
import org.b3log.solo.model.File;
import org.b3log.solo.model.Preference;
import org.b3log.solo.repository.FileRepository;
import org.json.JSONObject;

/**
 * File access via
 * <a href="http://code.google.com/appengine/docs/java/javadoc/com/google/appengine/api/datastore/package-summary.html">
 * Google Data Store Low-level API</a>.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Nov 1, 2010
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

    @Override
    protected void doPost(final HttpServletRequest request,
                          final HttpServletResponse response)
            throws ServletException, IOException {
        final ServletFileUpload upload = new ServletFileUpload();
        FileItemIterator iterator = null;

        try {
            iterator = upload.getItemIterator(request);

            while (iterator.hasNext()) {
                final FileItemStream item = iterator.next();
                final InputStream stream = item.openStream();

                if (!item.isFormField()) {
                    final Blob blob = new Blob(IOUtils.toByteArray(stream));
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
                    final long fileSize = stream.available();
                    file.put(File.FILE_SIZE, fileSize);
                    final JSONObject preference =
                            SoloServletListener.getUserPreference();
                    final String host = "http://" + preference.getString(
                            Preference.BLOG_HOST);
                    final String downloadURL = host
                                               + "/datastore-file-access.do?oId="
                                               + id;
                    file.put(File.FILE_DOWNLOAD_URL, downloadURL);

                    fileRepository.add(file);
                }
            }
        } catch (final Exception e) {
            LOGGER.severe(e.getMessage());
            throw new ServletException("File upload error: " + e.getMessage());
        }
    }

    @Override
    protected void doGet(final HttpServletRequest request,
                         final HttpServletResponse response)
            throws ServletException, IOException {
        final String id = request.getParameter(Keys.OBJECT_ID);
        try {
            final JSONObject file = fileRepository.get(id);
            final Blob content = (Blob) file.get(File.FILE_CONTENT);
            response.setContentType(file.getString(File.FILE_CONTENT_TYPE));
            response.getOutputStream().write(content.getBytes());
            response.getOutputStream().close();
        } catch (final Exception e) {
            LOGGER.severe(e.getMessage());
            throw new ServletException("File download error: " + e.getMessage());
        }
    }
}
