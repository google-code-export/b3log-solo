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
import org.b3log.solo.action.google.OAuthBuzzCallback;
import org.b3log.solo.model.Preference;
import org.b3log.solo.servlet.SoloServletListener;
import org.json.JSONObject;

/**
 * Google oauth.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Sep 14, 2010
 */
public final class BuzzOAuth {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(BuzzOAuth.class.getName());
    /**
     * Callback URL.
     */
    public static final String CALLBACK_URL = "/oauth-callback.do";
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
     * Authorizes the specified http transport.
     * 
     * @param transport the specified http transport
     */
    public static void authorize(final HttpTransport transport) {
        final JSONObject preference = SoloServletListener.getUserPreference();
        try {
            final String blogHost = preference.getString(Preference.BLOG_HOST);
            final String consumerKey = blogHost.split(":")[0];
            final String consumerSecret =
                    preference.getString(Preference.GOOLE_OAUTH_CONSUMER_SECRET);

            final GoogleOAuthGetTemporaryToken temporaryToken =
                    new GoogleOAuthGetTemporaryToken();
            signer = new OAuthHmacSigner();
            signer.clientSharedSecret = consumerSecret;
            temporaryToken.signer = signer;
            temporaryToken.consumerKey = consumerKey;
            temporaryToken.scope = BUZZ_SCOPE;
            temporaryToken.displayName = consumerKey;
            temporaryToken.callback = "http://" + blogHost + CALLBACK_URL;
            final OAuthCredentialsResponse tempCredentials =
                    temporaryToken.execute();
            signer.tokenSharedSecret = tempCredentials.tokenSecret;
            final OAuthAuthorizeTemporaryTokenUrl authorizeUrl =
                    new OAuthAuthorizeTemporaryTokenUrl(
                    "https://www.google.com/buzz/api/auth/OAuthAuthorizeToken");
            authorizeUrl.set("scope", temporaryToken.scope);
            authorizeUrl.set("domain", consumerKey);
            authorizeUrl.set("xoauth_displayname", consumerKey);
            final String tempToken = tempCredentials.token;
            authorizeUrl.temporaryToken = tempToken;
            final String authorizationUrl = authorizeUrl.build();
            LOGGER.log(Level.INFO, "Authorization URL[{0}]", authorizationUrl);

            final String verifier =
                    OAuthBuzzCallback.getVerifier(tempToken, -1);
            final GoogleOAuthGetAccessToken accessToken =
                    new GoogleOAuthGetAccessToken();
            accessToken.temporaryToken = tempToken;
            accessToken.signer = signer;
            accessToken.consumerKey = consumerKey;
            accessToken.verifier = verifier;
            credentials = accessToken.execute();
            signer.tokenSharedSecret = credentials.tokenSecret;
            createOAuthParameters().signRequestsUsingAuthorizationHeader(
                    transport);

        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

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
    private BuzzOAuth() {
    }
}
