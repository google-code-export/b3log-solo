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

package org.b3log.solo.sync;

/**
 * Abstract blog.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Sep 4, 2010
 */
public abstract class AbstractBlog implements Blog {

    /**
     * User name.
     */
    private String userName;
    /**
     * User password.
     */
    private String userPassword;

    @Override
    public final void setUserName(final String userName) {
        this.userName = userName;
    }

    @Override
    public final void setUserPassword(final String userPassword) {
        this.userPassword = userPassword;
    }

    @Override
    public final String getUserName() {
        return userName;
    }

    @Override
    public final String getUserPassword() {
        return userPassword;
    }
}
