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

import com.google.appengine.api.urlfetch.HTTPHeader;
import com.google.appengine.api.urlfetch.HTTPMethod;
import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import java.net.URL;
import javax.servlet.http.HttpServletResponse;

/**
 * HTTP client for GAE application.
 *
 * @author unascribed
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Jan 26, 2011
 */
final class QHttpClient {

    private URLFetchService urlFetchService =
            URLFetchServiceFactory.getURLFetchService();

    public QHttpClient() {
    }

    /**
     * Using GET method.
     *
     * @param url
     *            The remote URL.
     * @param queryString
     *            The query string containing parameters
     * @return Response string.
     * @throws Exception
     */
    public String httpGet(String url, String queryString) throws Exception {
        String ret = null;

        if (queryString != null && !queryString.equals("")) {
            url += "?" + queryString;
        }

        final HTTPRequest request = new HTTPRequest(new URL(url));

        try {
            final HTTPResponse response = urlFetchService.fetch(request);
            if (HttpServletResponse.SC_OK != response.getResponseCode()) {
                System.err.println("HttpGet Method failed: "
                                   + response.getResponseCode());
            }
            ret = new String(response.getContent(), "UTF-8");

        } catch (Exception e) {
            throw new Exception(e);
        }

        return ret;
    }

    /**
     * Using POST method.
     *
     * @param url
     *            The remote URL.
     * @param queryString
     *            The query string containing parameters
     * @return Response string.
     * @throws Exception
     */
    public String httpPost(String url, String queryString) throws Exception {
        String ret = null;
        final HTTPRequest request = new HTTPRequest(new URL(url),
                                                    HTTPMethod.POST);
        request.setHeader(new HTTPHeader("Content-Type",
                                         "application/x-www-form-urlencoded"));
        if (queryString != null && !queryString.equals("")) {
            request.setPayload(queryString.getBytes("UTF-8"));
        }

        try {
            final HTTPResponse response = urlFetchService.fetch(request);
            if (HttpServletResponse.SC_OK != response.getResponseCode()) {
                System.err.println("HttpPost Method failed: "
                                   + response.getResponseCode());
            }
            ret = new String(response.getContent(), "UTF-8");
        } catch (Exception e) {
            throw new Exception(e);
        }

        return ret;
    }
    /**
     * Using POST method with multiParts.
     *
     * @param url
     *            The remote URL.
     * @param queryString
     *            The query string containing parameters
     * @param files
     *            The list of image files
     * @return Response string.
     * @throws Exception
     */
//	public String httpPostWithFile(String url, String queryString,
//			List<QParameter> files) throws Exception {
//
//		String responseData = null;
//		url += '?' + queryString;
//		HttpClient httpClient = new HttpClient();
//		PostMethod httpPost = new PostMethod(url);
//		try {
//			List<QParameter> listParams = QHttpUtil
//					.getQueryParameters(queryString);
//			int length = listParams.size() + (files == null ? 0 : files.size());
//			Part[] parts = new Part[length];
//			int i = 0;
//			for (QParameter param : listParams) {
//				parts[i++] = new StringPart(param.mName,
//						QHttpUtil.formParamDecode(param.mValue), "UTF-8");
//			}
//			for (QParameter param : files) {
//				File file = new File(param.mValue);
//				parts[i++] = new FilePart(param.mName, file.getName(), file,
//						QHttpUtil.getContentType(file), "UTF-8");
//			}
//
//			httpPost.setRequestEntity(new MultipartRequestEntity(parts,
//					httpPost.getParams()));
//
//			int statusCode = httpClient.executeMethod(httpPost);
//			if (statusCode != HttpStatus.SC_OK) {
//				System.err.println("HttpPost Method failed: "
//						+ httpPost.getStatusLine());
//			}
//			responseData = httpPost.getResponseBodyAsString();
//		} catch (Exception e) {
//			throw new Exception(e);
//		} finally {
//			httpPost.releaseConnection();
//			httpClient = null;
//		}
//
//		return responseData;
//	}
}
