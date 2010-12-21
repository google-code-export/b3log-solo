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

package org.b3log.solo.repository.impl;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.latke.model.Role;
import org.b3log.latke.model.User;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.gae.AbstractGAERepository;
import org.b3log.solo.repository.UserRepository;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * User Google App Engine repository.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.3, Dec 19, 2010
 */
public final class UserGAERepository extends AbstractGAERepository
        implements UserRepository {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(UserGAERepository.class.getName());

    @Override
    public String getName() {
        return User.USER;
    }

    @Override
    public JSONObject getByEmail(final String email) {
        final Query query = new Query(getName());
        query.addFilter(User.USER_EMAIL, Query.FilterOperator.EQUAL,
                        email.toLowerCase());
        final PreparedQuery preparedQuery = getDatastoreService().prepare(query);
        final Entity entity = preparedQuery.asSingleEntity();
        if (null == entity) {
            return null;
        }

        return entity2JSONObject(entity);
    }

    @Override
    public boolean isAdminEmail(final String email)
            throws RepositoryException {
        final JSONObject user = getByEmail(email);

        if (null == user) {
            return false;
        }

        try {
            return Role.ADMIN_ROLE.equals(user.getString(User.USER_ROLE));
        } catch (final JSONException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);

            throw new RepositoryException(e);
        }
    }
}
