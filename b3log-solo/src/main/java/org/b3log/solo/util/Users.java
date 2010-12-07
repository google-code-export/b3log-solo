/*
 * Copyright (c) 2009, 2010, B3log Team
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

import com.google.inject.Inject;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.model.User;
import org.b3log.solo.repository.UserRepository;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * User utilities.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Dec 7, 2010
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
            if (user.getString(User.USER_EMAIL).equals(email)) {
                return true;
            }
        }

        return false;
    }
}
