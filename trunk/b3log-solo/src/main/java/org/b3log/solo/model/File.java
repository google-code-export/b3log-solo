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
package org.b3log.solo.model;

/**
 * This class defines all file model relevant keys.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Sep 8, 2010
 */
public final class File {

    /**
     * File.
     */
    public static final String FILE = "file";
    /**
     * Key of file name.
     */
    public static final String FILE_NAME = "fileName";
    /**
     * Key of file size.
     */
    public static final String FILE_SIZE = "fileSize";
    /**
     * Key of file download count.
     */
    public static final String FILE_DOWNLOAD_COUNT = "fileDownloadCount";
    /**
     * Key of file upload date.
     */
    public static final String FILE_UPLOAD_DATE = "fileUploadDate";
    /**
     * Key of file download URL.
     */
    public static final String FILE_DOWNLOAD_URL = "fileDownloadURL";

    /**
     * Private default constructor.
     */
    private File() {
    }
}
