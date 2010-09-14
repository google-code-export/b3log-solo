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
     * OAuth signer.
     */
    private static OAuthHmacSigner signer;
    /**
     * OAuth credentials.
     */
    private static OAuthCredentialsResponse credentials;
    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(BuzzOAuth.class.getName());

    /**
     * Authorizes with the specified http transport.
     *
     * @param httpTransport the specified http transport
     * @throws Exception exception
     */
    public static void authorize(final HttpTransport httpTransport)
            throws Exception {
        final JSONObject preference = SoloServletListener.getUserPreference();
        final String host = preference.getString(Preference.BLOG_HOST);
        final String domain = host.split(":")[0];
        final String displayName = preference.getString(Preference.BLOG_TITLE);

        String tempToken = null;
        final GoogleOAuthGetTemporaryToken temporaryToken =
                new GoogleOAuthGetTemporaryToken();
        signer = new OAuthHmacSigner();
        signer.clientSharedSecret =
                preference.getString(Preference.GOOLE_OAUTH_CONSUMER_SECRET);
        temporaryToken.signer = signer;
        temporaryToken.consumerKey = domain;
        temporaryToken.scope = "https://www.googleapis.com/auth/buzz";
        temporaryToken.displayName = displayName;
        temporaryToken.callback = "http://" + host + "/oauth-callback.do";
        final OAuthCredentialsResponse tempCredentials =
                temporaryToken.execute();
        signer.tokenSharedSecret = tempCredentials.tokenSecret;
        final OAuthAuthorizeTemporaryTokenUrl authorizeURL =
                new OAuthAuthorizeTemporaryTokenUrl(
                "https://www.google.com/buzz/api/auth/OAuthAuthorizeToken");

        authorizeURL.set("scope", temporaryToken.scope);
        authorizeURL.set("domain", domain);
        authorizeURL.set("iconUrl", "http://" + host + "/favicon.png");
        authorizeURL.set("xoauth_displayname", displayName);
        tempToken = tempCredentials.token;
        authorizeURL.temporaryToken = tempToken;
        final String authorizationURL = authorizeURL.build();
        LOGGER.log(Level.INFO, "Authorization URL[{0}]", authorizationURL);

        final String verifier = OAuthBuzzCallback.getVerifier(tempToken);

        final GoogleOAuthGetAccessToken accessToken =
                new GoogleOAuthGetAccessToken();
        accessToken.temporaryToken = tempToken;
        accessToken.signer = signer;
        accessToken.consumerKey = domain;
        accessToken.verifier = verifier;
        credentials = accessToken.execute();
        signer.tokenSharedSecret = credentials.tokenSecret;
        createOAuthParameters().signRequestsUsingAuthorizationHeader(
                httpTransport);
        // GoogleService googleService = new GoogleService("cl", "oauth-sample-app");
    }

    /**
     * Revokes.
     */
    public static void revoke() {
        if (credentials != null) {
            try {
                GoogleOAuthGetAccessToken.revokeAccessToken(
                        createOAuthParameters());
            } catch (final Exception e) {
                e.printStackTrace(System.err);
            }
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
