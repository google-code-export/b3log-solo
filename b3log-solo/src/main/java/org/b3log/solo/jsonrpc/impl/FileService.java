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
package org.b3log.solo.jsonrpc.impl;

import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.inject.Inject;
import java.io.IOException;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.latke.action.ActionException;
import org.b3log.solo.jsonrpc.AbstractGAEJSONRpcService;
import org.b3log.solo.repository.FileRepository;

/**
 * File service for JavaScript client.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Sep 8, 2010
 */
public final class FileService extends AbstractGAEJSONRpcService {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(FileService.class.getName());
    /**
     * Blob service.
     */
    private BlobstoreService blobstoreService =
            BlobstoreServiceFactory.getBlobstoreService();
    /**
     * File repository.
     */
    @Inject
    private FileRepository fileRepository;

    /**
     * Gets the upload URL.
     *
     * @param request the specified http servlet request
     * @param response the specified http servlet response
     * @return upload URL
     * @throws ActionException action exception
     * @throws IOException io exception
     */
    public String getUploadURL(final HttpServletRequest request,
                               final HttpServletResponse response)
            throws ActionException, IOException {
        checkAuthorized(request, response);

        return blobstoreService.createUploadUrl("/admin-index.do");
    }
}
