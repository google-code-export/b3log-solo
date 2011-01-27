package com.mime.qweibo;

import java.util.ArrayList;
import java.util.List;

import com.mime.qweibo.QWeiboType.PageFlag;
import com.mime.qweibo.QWeiboType.ResultType;
import java.net.URLEncoder;

public class QWeiboSyncApi {

    /**
     * Get request token.
     *
     * @param customKey
     *            Your AppKey.
     * @param customSecret
     *            Your AppSecret.
     * @param callbackURL the specified callback encoded URL
     * @return The request token.
     */
    public String getRequestToken(final String customKey,
                                  final String customSecret,
                                  final String callbackURL) {
        String url = "https://open.t.qq.com/cgi-bin/request_token";
        List<QParameter> parameters = new ArrayList<QParameter>();
        OauthKey oauthKey = new OauthKey();
        oauthKey.customKey = customKey;
        oauthKey.customSecret = customSecret;
        oauthKey.callbackUrl = callbackURL;

        QWeiboRequest request = new QWeiboRequest();
        String res = null;
        try {
            res = request.request(url, "GET", oauthKey, parameters, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Get access token.
     *
     * @param customKey
     *            Your AppKey.
     * @param customSecret
     *            Your AppSecret
     * @param requestToken
     *            The request token.
     * @param requestTokenSecret
     *            The request token Secret
     * @param verify
     *            The verification code.
     * @return
     */
    public String getAccessToken(String customKey, String customSecret,
                                 String requestToken, String requestTokenSecret,
                                 String verify) {

        String url = "https://open.t.qq.com/cgi-bin/access_token";
        List<QParameter> parameters = new ArrayList<QParameter>();
        OauthKey oauthKey = new OauthKey();
        oauthKey.customKey = customKey;
        oauthKey.customSecret = customSecret;
        oauthKey.tokenKey = requestToken;
        oauthKey.tokenSecret = requestTokenSecret;
        oauthKey.verify = verify;

        QWeiboRequest request = new QWeiboRequest();
        String res = null;
        try {
            res = request.request(url, "GET", oauthKey, parameters, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Get home page messages.
     *
     * @param customKey
     *            Your AppKey
     * @param customSecret
     *            Your AppSecret
     * @param requestToken
     *            The access token
     * @param requestTokenSecret
     *            The access token secret
     * @param format
     *            Response format, xml or json
     * @param pageFlag
     *            Page number.
     * @param nReqNum
     *            Number of messages you want.
     * @return Response messages based on the specified format.
     */
    public String getHomeMsg(String customKey, String customSecret,
                             String requestToken, String requestTokenSecret,
                             ResultType format,
                             PageFlag pageFlag, int nReqNum) {

        String url = "http://open.t.qq.com/api/statuses/home_timeline";
        List<QParameter> parameters = new ArrayList<QParameter>();
        OauthKey oauthKey = new OauthKey();
        oauthKey.customKey = customKey;
        oauthKey.customSecret = customSecret;
        oauthKey.tokenKey = requestToken;
        oauthKey.tokenSecret = requestTokenSecret;

        String strFormat = null;
        if (format == ResultType.ResultType_Xml) {
            strFormat = "xml";
        } else if (format == ResultType.ResultType_Json) {
            strFormat = "json";
        } else {
            return "";
        }

        parameters.add(new QParameter("format", strFormat));
        parameters.add(new QParameter("pageflag", String.valueOf(pageFlag.
                ordinal())));
        parameters.add(new QParameter("reqnum", String.valueOf(nReqNum)));

        QWeiboRequest request = new QWeiboRequest();
        String res = null;
        try {
            res = request.request(url, "GET", oauthKey, parameters, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * Publish a Weibo message.
     *
     * @param customKey
     *            Your AppKey
     * @param customSecret
     *            Your AppSecret
     * @param requestToken
     *            The access token
     * @param requestTokenSecret
     *            The access token secret
     * @param content
     *            The content of your message
     * @param pic
     *            The files of your images.
     * @param format
     *            Response format, xml or json(Default).
     * @return Result info based on the specified format.
     */
    public String publishMsg(String customKey, String customSecret,
                             String requestToken, String requestTokenSecret,
                             String content,
                             String pic, ResultType format) {

        List<QParameter> files = new ArrayList<QParameter>();
        String url = null;
        String httpMethod = "POST";

        if (pic == null || pic.trim().equals("")) {
            url = "http://open.t.qq.com/api/t/add";
        } else {
            url = "http://open.t.qq.com/api/t/add_pic";
            files.add(new QParameter("pic", pic));
        }

        OauthKey oauthKey = new OauthKey();
        oauthKey.customKey = customKey;
        oauthKey.customSecret = customSecret;
        oauthKey.tokenKey = requestToken;
        oauthKey.tokenSecret = requestTokenSecret;

        List<QParameter> parameters = new ArrayList<QParameter>();

        String strFormat = null;
        if (format == ResultType.ResultType_Xml) {
            strFormat = "xml";
        } else if (format == ResultType.ResultType_Json) {
            strFormat = "json";
        } else {
            return "";
        }

        try {
        parameters.add(new QParameter("format", strFormat));
        parameters.add(new QParameter("content", content));
        parameters.add(new QParameter("clientip", "127.0.0.1"));
        } catch (final Exception e) {
            System.err.println(e.getMessage());
        }

        QWeiboRequest request = new QWeiboRequest();
        String res = null;
        try {
            res = request.request(url, httpMethod, oauthKey, parameters,
                                  files);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }
}
