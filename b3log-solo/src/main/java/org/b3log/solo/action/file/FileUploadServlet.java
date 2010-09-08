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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.b3log.solo.repository.FileRepository;

/**
 * File upload.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Sep 8, 2010
 */
public final class FileUploadServlet extends HttpServlet {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(FileUploadServlet.class.getName());
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
        try {
            FileItemIterator itemIterator = upload.getItemIterator(request);
            while (itemIterator.hasNext()) {
                FileItemStream fileItemStream = itemIterator.next();
                final InputStream stream = fileItemStream.openStream();

                if (!fileItemStream.isFormField()) {
                    final Blob blob = new Blob(IOUtils.toByteArray(stream));
                    fileRepository.
                }
            }
        } catch (final FileUploadException ex) {
            LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }
}
