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
package org.b3log.solo.google.auth;

import com.google.api.client.auth.oauth.OAuthAuthorizeTemporaryTokenUrl;
import com.google.api.client.auth.oauth.OAuthCredentialsResponse;
import com.google.api.client.auth.oauth.OAuthHmacSigner;
import com.google.api.client.auth.oauth.OAuthParameters;
import com.google.api.client.googleapis.auth.oauth.GoogleOAuthGetAccessToken;
import com.google.api.client.googleapis.auth.oauth.GoogleOAuthGetTemporaryToken;
import com.google.api.client.http.HttpTransport;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.solo.model.Preference;
import org.b3log.solo.SoloServletListener;
import org.json.JSONObject;

/**
 * Google OAuth utilities.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.2, Sep 20, 2010
 */
public final class OAuths {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(OAuths.class.getName());
    /**
     * Buzz OAuth callback URL.
     */
    public static final String BUZZ_CALLBACK_URL = "/buzz-oauth-callback.do";
    /**
     * Buzz scope.
     */
    private static final String BUZZ_SCOPE =
            "https://www.googleapis.com/auth/buzz";
    /**
     * Signer.
     */
    private static OAuthHmacSigner signer;
    /**
     * Credentials.
     */
    private static OAuthCredentialsResponse credentials;

    /**
     * Gets the Buzz authorization URL for the specified http transport.
     * 
     * @param httpTransport the specified http transport
     * @param consumerSecret the specified Google OAuth consumer secret
     * @return Buzz authorization URL, returns {@code null} if error
     */
    public static String getBuzzAuthorizationURL(
            final HttpTransport httpTransport,
            final String consumerSecret) {
        final JSONObject preference = SoloServletListener.getUserPreference();
        try {
            final String blogHost = preference.getString(Preference.BLOG_HOST);
            final String consumerKey = blogHost.split(":")[0];

            final GoogleOAuthGetTemporaryToken temporaryToken =
                    new GoogleOAuthGetTemporaryToken();
            signer = new OAuthHmacSigner();
            signer.clientSharedSecret = consumerSecret;
            temporaryToken.signer = signer;
            temporaryToken.consumerKey = consumerKey;
            temporaryToken.scope = BUZZ_SCOPE;
            final String blogTitle = preference.getString(Preference.BLOG_TITLE);
            temporaryToken.displayName = blogTitle;
            temporaryToken.callback = "http://" + blogHost + BUZZ_CALLBACK_URL;
            final OAuthCredentialsResponse tempCredentials =
                    temporaryToken.execute();
            signer.tokenSharedSecret = tempCredentials.tokenSecret;
            final OAuthAuthorizeTemporaryTokenUrl authorizeURL =
                    new OAuthAuthorizeTemporaryTokenUrl(
                    "https://www.google.com/buzz/api/auth/OAuthAuthorizeToken");
            authorizeURL.set("scope", temporaryToken.scope);
            authorizeURL.set("domain", consumerKey);
            authorizeURL.set("iconUrl",
                             "http://code.google.com/p/b3log-solo/logo?cct=1283958195");
            final String tempToken = tempCredentials.token;
            authorizeURL.temporaryToken = tempToken;

            final String ret = authorizeURL.build();
            LOGGER.log(Level.FINE, "Authorization URL[{0}]", ret);

            return ret;
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return null;
        }
    }

    /**
     * Signs the specified request token, verifier and http transport.
     *
     * @param requestToken the specified request token
     * @param verifier the specified verifier
     * @param httpTransport the specified http transport
     * @throws Exception exception
     */
    public static void sign(final String requestToken,
                            final String verifier,
                            final HttpTransport httpTransport)
            throws Exception {
        final JSONObject preference = SoloServletListener.getUserPreference();
        final String consumerKey = preference.getString(Preference.BLOG_HOST).
                split(":")[0];
        final GoogleOAuthGetAccessToken accessToken =
                new GoogleOAuthGetAccessToken();
        accessToken.temporaryToken = requestToken;
        accessToken.signer = signer;
        accessToken.consumerKey = consumerKey;
        accessToken.verifier = verifier;
        credentials = accessToken.execute();
        signer.tokenSharedSecret = credentials.tokenSecret;
        createOAuthParameters().signRequestsUsingAuthorizationHeader(
                httpTransport);

    }

    /**
     * Creates OAuth parameters.
     *
     * @return OAuth parameters
     */
    private static OAuthParameters createOAuthParameters() {
        final OAuthParameters ret = new OAuthParameters();
        ret.consumerKey = "anonymous";
        ret.signer = signer;
        ret.token = credentials.token;
        return ret;
    }

    /**
     * Private default constructor.
     */
    private OAuths() {
    }
}
