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

package org.b3log.solo.util;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.inject.Inject;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.model.User;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.solo.model.Article;
import org.b3log.solo.repository.ArticleRepository;
import org.b3log.solo.repository.UserRepository;
import org.b3log.solo.repository.impl.ArticleGAERepository;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * User utilities.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.5, Jan 11, 2011
 */
public class Users {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(Users.class.getName());
    /**
     * User repository.
     */
    @Inject
    private UserRepository userRepository;
    /**
     * User service.
     */
    private UserService userService = UserServiceFactory.getUserService();
    /**
     * Article repository.
     */
    private ArticleRepository articleRepository =
            ArticleGAERepository.getInstance();

    /**
     * Determines whether if exists multiple users in current Solo.
     *
     * @return {@code true} if exists, {@code false} otherwise
     * @throws JSONException json exception
     * @throws RepositoryException repository exception
     */
    public boolean hasMultipleUsers() throws JSONException,
                                             RepositoryException {
        final JSONArray users =
                userRepository.get(1, Integer.MAX_VALUE).
                getJSONArray(Keys.RESULTS);

        return 1 != users.length();
    }

    /**
     * Can the current user access an article specified by the given article id?
     *
     * @param articleId the given article id
     * @return {@code true} if the current user can access the article,
     * {@code false} otherwise
     * @throws Exception exception
     */
    public boolean canAccessArticle(final String articleId)
            throws Exception {
        if (isAdminLoggedIn()) {
            return true;
        }

        final JSONObject article = articleRepository.get(articleId);
        final String currentUserEmail =
                getCurrentUser().getString(User.USER_EMAIL);

        if (!article.getString(Article.ARTICLE_AUTHOR_EMAIL).
                equals(currentUserEmail)) {
            return false;
        }

        return true;
    }

    /**
     * Determines whether the current logged in user is a collaborate
     * administrator(added via GAE Admin Console Permissions).
     *
     * @return {@code true} if it is, {@code false} otherwise
     */
    public boolean isCollaborateAdmin() {
        final boolean isUserLoggedIn = userService.isUserLoggedIn();
        if (!isUserLoggedIn) {
            return false;
        }

        final boolean isUserAdmin = userService.isUserAdmin();
        if (!isUserAdmin) {
            return false;
        }

        final com.google.appengine.api.users.User currentUser =
                userService.getCurrentUser();
        return !isSoloUser(currentUser.getEmail());
    }

    /**
     * Checks whether the current request is made by logged in user(including
     * default user and administrator lists in <i>users</i>).
     *
     * @return {@code true} if the current request is made by logged in user,
     * returns {@code false} otherwise
     */
    public boolean isLoggedIn() {
        final com.google.appengine.api.users.User currentUser =
                userService.getCurrentUser();
        if (null == currentUser) {
            return false;
        }

        return isSoloUser(currentUser.getEmail()) || userService.isUserAdmin();
    }

    /**
     * Checks whether the current request is made by logged in administrator.
     *
     * @return {@code true} if the current request is made by logged in
     * administrator, returns {@code false} otherwise
     */
    public boolean isAdminLoggedIn() {
        return userService.isUserLoggedIn() && userService.isUserAdmin();
    }

    /**
     * Gets the current user.
     *
     * @return the current user, {@code null} if not found
     */
    public JSONObject getCurrentUser() {
        final com.google.appengine.api.users.User currentUser =
                userService.getCurrentUser();
        if (null == currentUser) {
            return null;
        }

        final String email = currentUser.getEmail();
        return userRepository.getByEmail(email);
    }

    /**
     * Determines whether the specified email is a user's email of this Solo
     * application.
     *
     * @param email the specified email
     * @return {@code true} if it is, {@code false} otherwise
     */
    public boolean isSoloUser(final String email) {
        try {
            final JSONObject result = userRepository.get(1, Integer.MAX_VALUE);
            final JSONArray users = result.getJSONArray(Keys.RESULTS);

            return existEmail(email, users);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Determines whether the specified email exits in the specified users.
     *
     * @param email the specified email
     * @param users the specified user
     * @return {@code true} if exists, {@code false} otherwise
     * @throws JSONException json exception
     */
    private boolean existEmail(final String email, final JSONArray users)
            throws JSONException {
        for (int i = 0; i < users.length(); i++) {
            final JSONObject user = users.getJSONObject(i);
            if (user.getString(User.USER_EMAIL).equalsIgnoreCase(email)) {
                return true;
            }
        }

        return false;
    }
}
