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

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.inject.Inject;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.latke.Keys;
import org.b3log.solo.model.File;
import org.b3log.solo.model.Preference;
import org.b3log.solo.repository.FileRepository;
import org.b3log.solo.util.PreferenceUtils;
import org.json.JSONObject;

/**
 * File access via
 * <a href="http://code.google.com/appengine/docs/java/blobstore/">
 * Google Blog Store</a>.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Oct 27, 2010
 */
public final class BlobStoreFileAccessServlet extends HttpServlet {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(BlobStoreFileAccessServlet.class.getName());
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
     * Datastore service.
     */
    private DatastoreService datastoreService =
            DatastoreServiceFactory.getDatastoreService();
    /**
     * Blog info factory.
     */
    private BlobInfoFactory blobInfoFactory = new BlobInfoFactory(
            datastoreService);
    /**
     * Blob service.
     */
    private BlobstoreService blobstoreService =
            BlobstoreServiceFactory.getBlobstoreService();
    /**
     * Preference utilities.
     */
    @Inject
    private PreferenceUtils preferenceUtils;

    @Override
    protected void doPost(final HttpServletRequest request,
                          final HttpServletResponse response)
            throws ServletException, IOException {
        LOGGER.info("Uploading file....");
        final Map<String, BlobKey> blobs =
                blobstoreService.getUploadedBlobs(request);
        final BlobKey blobKey = blobs.get("myFile");

        if (null == blobKey) {
            LOGGER.log(Level.SEVERE, "File upload error");
            response.sendRedirect("/");
        } else {
            try {
                final BlobInfo blobInfo = blobInfoFactory.loadBlobInfo(blobKey);
                final String blobId = blobKey.getKeyString();
                final JSONObject file = new JSONObject();
                file.put(Keys.OBJECT_ID, blobId);
                file.put(File.FILE_DOWNLOAD_COUNT, 0);

                final String contentType = blobInfo.getContentType();
                final Date createDate = blobInfo.getCreation();
                file.put(File.FILE_UPLOAD_DATE, createDate);
                final String fileName = blobInfo.getFilename();
                file.put(File.FILE_NAME, fileName);
                final long fileSize = blobInfo.getSize();
                file.put(File.FILE_SIZE, fileSize);
                final JSONObject preference = preferenceUtils.getPreference();
                final String host = "http://" + preference.getString(
                        Preference.BLOG_HOST);
                final String downloadURL = host + "/file-access.do?oId="
                                           + blobId;
                file.put(File.FILE_DOWNLOAD_URL, downloadURL);

                fileRepository.add(file);
                LOGGER.log(Level.INFO, "Uploaded file[name={0}, size={1}]",
                           new Object[]{fileName, fileSize});
                response.sendRedirect(downloadURL);
            } catch (final Exception e) {
                LOGGER.log(Level.SEVERE, "File upload error");
                response.sendRedirect("/");
            }
        }
    }

    @Override
    protected void doGet(final HttpServletRequest request,
                         final HttpServletResponse response)
            throws ServletException, IOException {
        LOGGER.finer("Getting a file....");
        final BlobKey blobKey =
                new BlobKey(request.getParameter(Keys.OBJECT_ID));
        final String blobId = blobKey.getKeyString();
        try {
            final JSONObject file = fileRepository.get(blobId);
            final int cnt = file.getInt(File.FILE_DOWNLOAD_COUNT);
            file.put(File.FILE_DOWNLOAD_COUNT, cnt + 1);
            fileRepository.update(blobId, file);
        } catch (final Exception e) {
            LOGGER.warning("Inc file download count error!");
        }

        blobstoreService.serve(blobKey, response);
    }
}
