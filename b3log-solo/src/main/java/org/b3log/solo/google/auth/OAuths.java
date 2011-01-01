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

package org.b3log.solo.google.auth;

import com.google.inject.Inject;
import java.util.logging.Logger;
import org.b3log.solo.util.Preferences;

/**
 * Google OAuth utilities.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.5, Dec 4, 2010
 */
public final class OAuths {

    /**
     * XXX: remove this constructor.
     */
    public void test() {
        System.out.println("REMOVE THIS METHOD.");
    }
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
     * Picasa scope.
     */
    private static final String PICASA_SCOPE =
            "https://picasaweb.google.com/data/";
    /**
     * Signer.
     */
//    private static OAuthHmacSigner signer;
    /**
     * Credentials.
     */
//    private static OAuthCredentialsResponse credentials;
    /**
     * Preference utilities.
     */
    @Inject
    private Preferences preferenceUtils;
    /**
     * Consumer key.
     */
    private static String consumerKey;
    /**
     * Gets the authorization URL for the specified http transport.
     * 
     * @param httpTransport the specified http transport
     * @param consumerSecret the specified Google OAuth consumer secret
     * @return authorization URL, returns {@code null} if error
     */
//    public String getAuthorizationURL(
//            final HttpTransport httpTransport,
//            final String consumerSecret) {
//        try {
//            final JSONObject preference = preferenceUtils.getPreference();
//            final String blogHost = preference.getString(Preference.BLOG_HOST);
//            consumerKey = blogHost.split(":")[0];
//            LOGGER.log(Level.INFO,
//                       "Google OAuth[consumerKey={0}, consumerSecret={1}]",
//                       new String[]{consumerKey, consumerSecret});
//
//            final GoogleOAuthGetTemporaryToken temporaryToken =
//                    new GoogleOAuthGetTemporaryToken();
//            signer = new OAuthHmacSigner();
//            signer.clientSharedSecret = consumerSecret;
//            temporaryToken.signer = signer;
//            temporaryToken.consumerKey = consumerKey;
//            temporaryToken.scope = PICASA_SCOPE;
//            final String blogTitle = preference.getString(Preference.BLOG_TITLE);
//            temporaryToken.displayName = blogTitle;
//            temporaryToken.callback = "http://" + blogHost + BUZZ_CALLBACK_URL;
//            final OAuthCredentialsResponse tempCredentials =
//                    temporaryToken.execute();
//            signer.tokenSharedSecret = tempCredentials.tokenSecret;
//            final OAuthAuthorizeTemporaryTokenUrl authorizeURL =
//                    new GoogleOAuthAuthorizeTemporaryTokenUrl();
//            final String tempToken = tempCredentials.token;
//            authorizeURL.temporaryToken = tempToken;
//
//            final String ret = authorizeURL.build();
//            LOGGER.log(Level.FINE, "Authorization URL[{0}]", ret);
//
//            return ret;
//        } catch (final Exception e) {
//            LOGGER.log(Level.SEVERE, e.getMessage(), e);
//            return null;
//        }
//    }
//
//    /**
//     * Signs the specified request token, verifier and http transport.
//     *
//     * @param requestToken the specified request token
//     * @param verifier the specified verifier
//     * @param httpTransport the specified http transport
//     * @throws Exception exception
//     */
//    public void sign(final String requestToken,
//                     final String verifier,
//                     final HttpTransport httpTransport)
//            throws Exception {
//        final GoogleOAuthGetAccessToken accessToken =
//                new GoogleOAuthGetAccessToken();
//        accessToken.temporaryToken = requestToken;
//        accessToken.signer = signer;
//        accessToken.consumerKey = consumerKey;
//        accessToken.verifier = verifier;
//        credentials = accessToken.execute();
//        signer.tokenSharedSecret = credentials.tokenSecret;
//        createOAuthParameters().signRequestsUsingAuthorizationHeader(
//                httpTransport);
//    }
    /**
     * Creates OAuth parameters.
     *
     * @return OAuth parameters
     */
//    private OAuthParameters createOAuthParameters() {
//        final OAuthParameters ret = new OAuthParameters();
//        ret.consumerKey = consumerKey;
//        ret.signer = signer;
//        ret.token = credentials.token;
//
//        return ret;
//    }
//}
}
