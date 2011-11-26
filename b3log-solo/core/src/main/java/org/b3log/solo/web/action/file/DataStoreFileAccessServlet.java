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
package org.b3log.solo.web.action.file;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.latke.Keys;
import org.b3log.latke.repository.Blob;
import org.b3log.latke.repository.Transaction;
import org.b3log.solo.model.File;
import org.b3log.solo.repository.FileRepository;
import org.b3log.solo.repository.impl.FileRepositoryImpl;
import org.json.JSONObject;

/**
 * File access via
 * <a href="http://code.google.com/appengine/docs/java/javadoc/com/google/appengine/api/datastore/package-summary.html">
 * Google Data Store Low-level API</a>.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.1.1, Nov 26, 2011
 * @since 0.3.1
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
    private FileRepository fileRepository = FileRepositoryImpl.getInstance();

    @Override
    protected void doGet(final HttpServletRequest request,
                         final HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        final String id = request.getParameter(Keys.OBJECT_ID);
        final Transaction transaction = fileRepository.beginTransaction();
        transaction.clearQueryCache(false);
        try {
            final JSONObject file = fileRepository.get(id);
            if (null == file) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            final Blob content = (Blob) file.get(File.FILE_CONTENT);
            final String name = file.getString(File.FILE_NAME);
            response.addHeader("Content-Disposition",
                               "attachment; filename="
                               + URLEncoder.encode(name, "UTF-8"));
            response.setContentType(file.getString(File.FILE_CONTENT_TYPE));
            response.getOutputStream().write(content.getBytes());
            response.getOutputStream().close();

            final int downloadCnt = file.getInt(File.FILE_DOWNLOAD_COUNT);
            file.put(File.FILE_DOWNLOAD_COUNT, downloadCnt + 1);
            fileRepository.update(id, file);
            transaction.commit();
        } catch (final Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new ServletException("File download error: " + e.getMessage());
        }
    }
}
