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
package com.mime.qweibo;

import java.util.List;

/**
 * Request of Tencent microblog for GAE application.
 *
 * @author unascribed
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Jan 26, 2011
 */
public final class QWeiboRequest {

    public QWeiboRequest() {
    }

    /**
     * Do sync request.
     *
     * @param url
     *            The full url that needs to be signed including its non OAuth
     *            url parameters
     * @param httpMethod
     *            The http method used. Must be a valid HTTP method verb
     *            (POST,GET,PUT, etc)
     * @param key
     *            OAuth key
     * @param listParam
     *            Query parameters
     * @param listFile
     *            Files for post
     * @return
     * @throws Exception
     */
    public String request(String url, String httpMethod, OauthKey key,
                          List<QParameter> listParam,
                          List<QParameter> listFile)
            throws Exception {
        if (url == null || url.equals("")) {
            return null;
        }
        OAuth oauth = new OAuth();

        StringBuffer sbQueryString = new StringBuffer();
        String oauthUrl = oauth.getOauthUrl(url, httpMethod, key.customKey,
                                            key.customSecret, key.tokenKey,
                                            key.tokenSecret, key.verify,
                                            key.callbackUrl, listParam,
                                            sbQueryString);
        String queryString = sbQueryString.toString();

        QHttpClient http = new QHttpClient();
        if ("GET".equals(httpMethod)) {
            return http.httpGet(oauthUrl, queryString);
        } else if ((listFile == null) || (listFile.size() == 0)) {
            return http.httpPost(oauthUrl, queryString);
        }

        throw new UnsupportedOperationException();
//        else {
//            return http.httpPostWithFile(oauthUrl, queryString, listFile);
//        }
    }
}
