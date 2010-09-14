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

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthMessage;
import net.oauth.OAuthProblemException;
import net.oauth.OAuthServiceProvider;
import net.oauth.client.OAuthClient;
import net.oauth.client.httpclient4.HttpClient4;
import net.oauth.http.HttpMessage;
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
     * Request token URL.
     */
    private static final String REQUEST_TOKEN_URL =
            "https://www.google.com/accounts/OAuthGetRequestToken";
    /**
     * User authorization URL.
     */
    private static final String USER_AUTHORIZATION_URL =
            "https://www.google.com/buzz/api/auth/OAuthAuthorizeToken";
    /**
     * Access token URL.
     */
    private static final String ACCESS_TOKEN_URL =
            "https://www.google.com/accounts/OAuthGetAccessToken";
    /**
     * Callback URL.
     */
    private static final String CALLBACK_URL = "/oauth-callback.do";
    /**
     * Buzz scope.
     */
    private static final String BUZZ_SCOPE =
            "https://www.googleapis.com/auth/buzz";

    /**
     * Authorizes.
     */
    public static void authorize() {
        final JSONObject preference = SoloServletListener.getUserPreference();

        try {
            final String consumerKey =
                    preference.getString(Preference.BLOG_HOST);
            final String consumerSecret =
                    preference.getString(Preference.GOOLE_OAUTH_CONSUMER_SECRET);

            final OAuthServiceProvider serviceProvider =
                    new OAuthServiceProvider(REQUEST_TOKEN_URL,
                                             USER_AUTHORIZATION_URL,
                                             ACCESS_TOKEN_URL);
            final OAuthConsumer consumer =
                    new OAuthConsumer(CALLBACK_URL,
                                      consumerKey,
                                      consumerSecret,
                                      serviceProvider);
            final OAuthAccessor accessor = new OAuthAccessor(consumer);
            final OAuthClient client = new OAuthClient(new HttpClient4());

            final String authorizationUrl =
                    getAuthorizationUrl(client,
                                        accessor,
                                        CALLBACK_URL);
            LOGGER.log(Level.INFO, "Authorization URL[{0}]", authorizationUrl);
            // TODO: redirect to authorization URL
            accessor.accessToken = null;
            LOGGER.log(Level.INFO, "Waiting for verification token...");
            final String verifier = OAuthBuzzCallback.getVerifier(
                    accessor.requestToken, -1);
            if (verifier == null) {
                return;
            }

            LOGGER.log(Level.INFO, "Verification token received: {0}",
                       verifier);

            final List<OAuth.Parameter> accessTokenParams = OAuth.newList(
                    OAuth.OAUTH_TOKEN, accessor.requestToken,
                    OAuth.OAUTH_VERIFIER, verifier);
            LOGGER.log(Level.INFO,
                       "Fetching access token with parameters: {0}",
                       accessTokenParams);
            try {
                final OAuthMessage accessTokenResponse = client.getAccessToken(
                        accessor, OAuthMessage.GET, accessTokenParams);
                LOGGER.log(Level.INFO, "Access token received: {0}", accessTokenResponse.
                        getParameters());
                LOGGER.log(Level.FINE, accessTokenResponse.getDump().get(
                        HttpMessage.RESPONSE).toString());

            } catch (final OAuthProblemException e) {
                if (400 == e.getHttpStatusCode()) {
                    LOGGER.log(Level.WARNING, "Invalid token", e);
                } else {
                    throw e;
                }
            }

        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Gets authorization URL with the specified parameters.
     *
     * @param client the specified OAuth client
     * @param accessor the specified OAuth accessor
     * @param callbackURL the specified callback URL
     * @return authorization URL
     * @throws Exception exception
     */
    private static String getAuthorizationUrl(final OAuthClient client,
                                              final OAuthAccessor accessor,
                                              final String callbackURL)
            throws Exception {
        final List<OAuth.Parameter> requestTokenParams = OAuth.newList();
        requestTokenParams.add(new OAuth.Parameter(OAuth.OAUTH_CALLBACK,
                                                   callbackURL));
        requestTokenParams.add(
                new OAuth.Parameter("scope", BUZZ_SCOPE));
        requestTokenParams.add(new OAuth.Parameter("xoauth_displayname",
                                                   "B3log Solo"));
        client.getRequestTokenResponse(accessor, OAuthMessage.POST,
                                       requestTokenParams);
        String authorizationUrl =
                accessor.consumer.serviceProvider.userAuthorizationURL;
        authorizationUrl =
                OAuth.addParameters(authorizationUrl,
                                    "scope", BUZZ_SCOPE,
                                    "domain", accessor.consumer.consumerKey);

        authorizationUrl = OAuth.addParameters(authorizationUrl,
                                               "xoauth_displayname",
                                               "B3log Solo");
        authorizationUrl = OAuth.addParameters(authorizationUrl,
                                               OAuth.OAUTH_TOKEN,
                                               accessor.requestToken);

        return authorizationUrl;
    }

    /**
     * Private default constructor.
     */
    private BuzzOAuth() {
    }
}
