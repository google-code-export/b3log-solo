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
package org.b3log.solo.repository.impl;

import java.io.Serializable;
import org.apache.log4j.Logger;
import org.b3log.latke.model.User;
import org.b3log.latke.repository.gae.AbstractGAERepository;
import org.b3log.solo.repository.UserRepository;

/**
 * User Google App Engine repository.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Aug 14, 2010
 */
public class UserGAERepository extends AbstractGAERepository
        implements UserRepository, Serializable {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(UserGAERepository.class);
    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;

    @Override
    public String getName() {
        return User.USER;
    }
}
