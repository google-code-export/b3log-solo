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
package org.b3log.solo.repository.impl;

import org.b3log.latke.Keys;
import org.b3log.latke.model.Role;
import org.b3log.latke.model.User;
import org.b3log.latke.repository.FilterOperator;
import org.b3log.latke.repository.Query;
import org.b3log.latke.repository.Transaction;
import org.b3log.solo.AbstractTestCase;
import org.b3log.solo.repository.UserRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * {@link UserRepositoryImpl} test case.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Dec 29, 2011
 */
public final class UserRepositoryImplTestCase extends AbstractTestCase {

    /**
     * Tests.
     * 
     * @throws Exception exception
     */
    @Test
    public void test() throws Exception {
        final UserRepository userRepository = getUserRepository();

        final JSONObject user = new JSONObject();
        user.put(User.USER_NAME, "test");
        user.put(User.USER_EMAIL, "test@gmail.com");
        user.put(User.USER_PASSWORD, "pass");
        user.put(User.USER_ROLE, Role.ADMIN_ROLE);

        final JSONObject another = new JSONObject();
        another.put(User.USER_NAME, "test1");
        another.put(User.USER_EMAIL, "test1@gmail.com");
        another.put(User.USER_PASSWORD, "pass1");
        another.put(User.USER_ROLE, Role.DEFAULT_ROLE);

        final Transaction transaction = userRepository.beginTransaction();

        userRepository.add(user);
        userRepository.add(another);

        transaction.commit();

        Assert.assertTrue(userRepository.isAdminEmail("test@gmail.com"));


        final JSONObject admin = userRepository.getAdmin();

        Assert.assertNotNull(admin);
        Assert.assertEquals("test", admin.optString(User.USER_NAME));

        final JSONObject result =
                userRepository.get(new Query().addFilter(User.USER_NAME,
                                                         FilterOperator.EQUAL,
                                                         "test1"));

        final JSONArray users = result.getJSONArray(Keys.RESULTS);
        Assert.assertEquals(users.length(), 1);
        Assert.assertEquals(users.getJSONObject(0).getString(User.USER_EMAIL),
                            "test1@gmail.com");

        final JSONObject notFound =
                userRepository.getByEmail("not.found@gmail.com");
        Assert.assertNull(notFound);

        final JSONObject found = userRepository.getByEmail("test1@gmail.com");
        Assert.assertNotNull(found);
        Assert.assertEquals(found.getString(User.USER_PASSWORD), "pass1");
    }
}
