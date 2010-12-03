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

package org.b3log.solo.jsonrpc.impl;

import java.util.Set;
import java.util.TimeZone;
import org.b3log.solo.SoloServletListener;
import org.b3log.latke.repository.RepositoryException;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.latke.Keys;
import org.b3log.latke.action.ActionException;
import org.b3log.latke.action.util.PageCaches;
import org.b3log.latke.action.util.Paginator;
import org.b3log.latke.event.Event;
import org.b3log.latke.event.EventManager;
import org.b3log.latke.model.Pagination;
import org.b3log.latke.model.User;
import org.b3log.latke.repository.gae.AbstractGAERepository;
import org.b3log.latke.util.freemarker.Templates;
import org.b3log.solo.action.StatusCodes;
import org.b3log.solo.event.EventTypes;
import org.b3log.solo.jsonrpc.AbstractGAEJSONRpcService;
import org.b3log.solo.model.Cache;
import org.b3log.solo.model.Preference;
import org.b3log.solo.repository.PreferenceRepository;
import org.b3log.solo.util.PageCacheKeys;
import static org.b3log.solo.model.Preference.*;
import org.b3log.solo.model.Skin;
import org.b3log.solo.model.Statistic;
import org.b3log.solo.repository.StatisticRepository;
import org.b3log.solo.repository.UserRepository;
import org.b3log.solo.util.Skins;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Administrator service for JavaScript client.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.1.0, Dec 4, 2010
 */
public final class AdminService extends AbstractGAEJSONRpcService {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(AdminService.class.getName());
    /**
     * User service.
     */
    private com.google.appengine.api.users.UserService userService =
            UserServiceFactory.getUserService();
    /**
     * Preference repository.
     */
    @Inject
    private PreferenceRepository preferenceRepository;
    /**
     * User repository.
     */
    @Inject
    private UserRepository userRepository;
    /**
     * Statistic repository.
     */
    @Inject
    private StatisticRepository statisticRepository;
    /**
     * Event manager.
     */
    @Inject
    private EventManager eventManager;
    /**
     * Skin utilities.
     */
    @Inject
    private Skins skins;
    /**
     * Page cache utilities.
     */
    @Inject
    private PageCacheKeys pageCacheKeys;

    /**
     * Removes a user with the specified request json object.
     *
     * @param requestJSONObject the specified request json object, for example,
     * <pre>
     * {
     *     "oId": ""
     * }
     * </pre>
     * @param request the specified http servlet request
     * @param response the specified http servlet response
     * @return for example,
     * <pre>
     * {
     *     "sc": "REMOVE_USER_SUCC"
     * }
     * </pre>
     * @throws ActionException action exception
     * @throws IOException io exception
     */
    public JSONObject removeUser(final JSONObject requestJSONObject,
                                 final HttpServletRequest request,
                                 final HttpServletResponse response)
            throws ActionException, IOException {
        checkAuthorized(request, response);

        final Transaction transaction =
                AbstractGAERepository.DATASTORE_SERVICE.beginTransaction();
        final JSONObject ret = new JSONObject();
        try {
            final String userId = requestJSONObject.getString(Keys.OBJECT_ID);
            userRepository.remove(userId);
            transaction.commit();

            ret.put(Keys.STATUS_CODE, StatusCodes.REMOVE_USER_SUCC);
        } catch (final Exception e) {
            transaction.rollback();
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new ActionException(e);
        }

        return ret;
    }

    /**
     * Adds a user with the specified request json object.
     *
     * @param requestJSONObject the specified request json object, for example,
     * <pre>
     * {
     *     "userName": "",
     *     "userEmail": ""
     * }
     * </pre>
     * @param request the specified http servlet request
     * @param response the specified http servlet response
     * @return for example,
     * <pre>
     * {
     *     "sc": "ADD_USER_SUCC"
     * }
     * </pre>
     * @throws ActionException action exception
     * @throws IOException io exception
     */
    public JSONObject addUser(final JSONObject requestJSONObject,
                              final HttpServletRequest request,
                              final HttpServletResponse response)
            throws ActionException, IOException {
        checkAuthorized(request, response);

        final Transaction transaction =
                AbstractGAERepository.DATASTORE_SERVICE.beginTransaction();
        final JSONObject ret = new JSONObject();
        try {
            final JSONObject user = new JSONObject();
            final String userEmail =
                    requestJSONObject.getString(User.USER_EMAIL);
            final JSONObject duplicatedUser =
                    userRepository.getByEmail(userEmail);
            if (null != duplicatedUser) {
                ret.put(Keys.STATUS_CODE,
                        StatusCodes.ADD_USER_FAIL_DUPLICATED_EMAIL);

                return ret;
            }

            final String userName = requestJSONObject.getString(User.USER_NAME);
            user.put(User.USER_EMAIL, userEmail);
            user.put(User.USER_NAME, userName);

            userRepository.add(user);
            transaction.commit();

            ret.put(Keys.STATUS_CODE, StatusCodes.ADD_USER_SUCC);
        } catch (final Exception e) {
            transaction.rollback();
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new ActionException(e);
        }

        return ret;
    }

    /**
     * Gets users by the specified request json object.
     *
     * @param requestJSONObject the specified request json object, for example,
     * <pre>
     * {
     *     "paginationCurrentPageNum": 1,
     *     "paginationPageSize": 20,
     *     "paginationWindowSize": 10,
     * }, see {@link Pagination} for more details
     * </pre>
     * @param request the specified http servlet request
     * @param response the specified http servlet response
     * @return for example,
     * <pre>
     * {
     *     "pagination": {
     *         "paginationPageCount": 100,
     *         "paginationPageNums": [1, 2, 3, 4, 5]
     *     },
     *     "users": [{
     *         "oId": "",
     *         "userName": "",
     *         "userEmail": "",
     *         "roleName": ""
     *      }, ....]
     *     "sc": "GET_USERS_SUCC"
     * }
     * </pre>
     * @throws ActionException action exception
     * @throws IOException io exception
     * @see Pagination
     */
    public JSONObject getUsers(final JSONObject requestJSONObject,
                                  final HttpServletRequest request,
                                  final HttpServletResponse response)
            throws ActionException, IOException {
        checkAuthorized(request, response);
        
        final JSONObject ret = new JSONObject();
        try {
            final int currentPageNum = requestJSONObject.getInt(
                    Pagination.PAGINATION_CURRENT_PAGE_NUM);
            final int pageSize = requestJSONObject.getInt(
                    Pagination.PAGINATION_PAGE_SIZE);
            final int windowSize = requestJSONObject.getInt(
                    Pagination.PAGINATION_WINDOW_SIZE);
            final JSONObject result =
                    userRepository.get(currentPageNum, pageSize);

            final int pageCount = result.getJSONObject(Pagination.PAGINATION).
                    getInt(Pagination.PAGINATION_PAGE_COUNT);

            final JSONObject pagination = new JSONObject();
            ret.put(Pagination.PAGINATION, pagination);
            final List<Integer> pageNums =
                    Paginator.paginate(currentPageNum, pageSize, pageCount,
                                       windowSize);
            pagination.put(Pagination.PAGINATION_PAGE_COUNT, pageCount);
            pagination.put(Pagination.PAGINATION_PAGE_NUMS, pageNums);

            final JSONArray users = result.getJSONArray(Keys.RESULTS);
            ret.put(User.USERS, users);

            ret.put(Keys.STATUS_CODE, StatusCodes.GET_USERS_SUCC);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new ActionException(e);
        }

        return ret;
    }

    /**
     * Updates a user with the specified request json object.
     *
     * @param requestJSONObject the specified request json object, for example,
     * <pre>
     * {
     *     "oId": "",
     *     "userName": "",
     *     "userEmail": ""
     * }
     * </pre>
     * @param request the specified http servlet request
     * @param response the specified http servlet response
     * @return for example,
     * <pre>
     * {
     *     "sc": "UPDATE_USER_SUCC"
     * }
     * </pre>
     * @throws ActionException action exception
     * @throws IOException io exception
     */
    public JSONObject updateUser(final JSONObject requestJSONObject,
                                 final HttpServletRequest request,
                                 final HttpServletResponse response)
            throws ActionException, IOException {
        checkAuthorized(request, response);

        Transaction transaction =
                AbstractGAERepository.DATASTORE_SERVICE.beginTransaction();
        final JSONObject ret = new JSONObject();
        try {
            final String oldUserId = requestJSONObject.getString(Keys.OBJECT_ID);
            final JSONObject oldUser = userRepository.get(oldUserId);
            if (null != oldUser) {
                userRepository.remove(oldUserId);
                transaction.commit();
            }
            // XXX: GAE transaction isolation
            // http://code.google.com/intl/en/appengine/docs/java/datastore/transactions.html#Isolation_and_Consistency
            transaction =
                    AbstractGAERepository.DATASTORE_SERVICE.beginTransaction();

            addUser(requestJSONObject, request, response);
            transaction.commit();

            ret.put(Keys.STATUS_CODE, StatusCodes.UPDATE_USER_SUCC);
        } catch (final Exception e) {
            transaction.rollback();
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new ActionException(e);
        }

        return ret;
    }

    /**
     * Determines whether the administrator is logged in.
     * 
     * @return {@code true} if logged in, returns {@code false} otherwise
     */
    public boolean isAdminLoggedIn() {
        return userService.isUserLoggedIn() && userService.isUserAdmin();
    }

    /**
     * Gets the administrator logout.
     *
     * @param request the specified http servlet request
     * @param response the specified http servlet response
     * @return logout URL
     * @throws ActionException action exception
     * @throws IOException io exception
     */
    public String getLogoutURL(final HttpServletRequest request,
                               final HttpServletResponse response)
            throws ActionException, IOException {
        checkAuthorized(request, response);

        return userService.createLogoutURL("/");
    }

    /**
     * Gets the administrator login.
     *
     * @param redirectURL redirect URL after logged in
     * @param request the specified http servlet request
     * @param response the specified http servlet response
     * @return login URL
     * @throws ActionException action exception
     * @throws IOException io exception
     */
    public String getLoginURL(final String redirectURL,
                              final HttpServletRequest request,
                              final HttpServletResponse response)
            throws ActionException, IOException {
        return userService.createLoginURL(redirectURL);
    }

    /**
     * Gets page cache states with the specified http servlet request and http
     * servlet response.
     *
     * @param request the specified http servlet request
     * @param response the specified http servlet response
     * @return for example,
     * <pre>
     * {
     *     "cacheCachedCount": long,
     *     "cacheHitCount": long,
     *     "cachedBytes": long,
     *     "hitBytes": long,
     *     "cacheMissCount": long
     * }
     * </pre>
     * @throws ActionException action exception
     * @throws IOException io exception
     */
    public JSONObject getPageCache(final HttpServletRequest request,
                                   final HttpServletResponse response)
            throws ActionException, IOException {
        checkAuthorized(request, response);

        final org.b3log.latke.cache.Cache<String, Object> cache =
                PageCaches.getCache();
        final long cachedCount = cache.getCachedCount();
        final long hitCount = cache.getHitCount();
        final long missCount = cache.getMissCount();
        final long cachedBytes = cache.getCachedBytes();
        final long hitBytes = cache.getHitBytes();

        final JSONObject ret = new JSONObject();
        try {
            ret.put(Cache.CACHE_CACHED_COUNT, cachedCount);
            ret.put(Cache.CACHE_HIT_COUNT, hitCount);
            ret.put(Cache.CACHE_CACHED_BYTES, cachedBytes);
            ret.put(Cache.CACHE_HIT_BYTES, hitBytes);
            ret.put(Cache.CACHE_MISS_COUNT, missCount);
        } catch (final JSONException e) {
            LOGGER.log(Level.SEVERE, "Get page cache error: {0}", e.getMessage());
            throw new ActionException(e);
        }

        return ret;
    }

    /**
     * Clears a page cache specified by the given URI.
     *
     * @param uri the specified URI
     * @param request the specified http servlet request
     * @param response the specified http servlet response
     * @throws ActionException action exception
     * @throws IOException io exception
     */
    public void clearPageCache(final String uri,
                               final HttpServletRequest request,
                               final HttpServletResponse response)
            throws ActionException, IOException {
        checkAuthorized(request, response);

        LOGGER.log(Level.FINE, "URI[{0}]", uri);

        String pageCacheKey = uri;
        pageCacheKey = pageCacheKeys.getPageCacheKey(uri, null);

        LOGGER.log(Level.FINER, "pageCacheKey[{0}]", pageCacheKey);

        PageCaches.remove(pageCacheKey);
    }

    /**
     * Clears all page cache.
     * 
     * @param request the specified http servlet request
     * @param response the specified http servlet response
     * @throws ActionException action exception
     * @throws IOException io exception
     */
    public void clearAllPageCache(final HttpServletRequest request,
                                  final HttpServletResponse response)
            throws ActionException, IOException {
        checkAuthorized(request, response);

        PageCaches.removeAll();
    }

    /**
     * Initializes B3log Solo.
     * 
     * @param request the specified http servlet request
     * @param response the specified http servlet response
     * @throws ActionException action exception
     * @throws IOException io exception
     * @return for example,
     * <pre>
     * {
     *     "sc": "INIT_B3LOG_SOLO_SUCC"
     * }
     * </pre>
     */
    public JSONObject init(final HttpServletRequest request,
                           final HttpServletResponse response)
            throws ActionException, IOException {
        checkAuthorized(request, response);

        final JSONObject ret = new JSONObject();

        try {
            initStatistic();
            initPreference();

            ret.put(Keys.STATUS_CODE, StatusCodes.INIT_B3LOG_SOLO_SUCC);
        } catch (final Exception e) {
            LOGGER.severe("Initialize B3log Solo error");
        }

        return ret;
    }

    /**
     * Initializes statistic.
     *
     * @return statistic
     * @throws RepositoryException repository exception
     * @throws JSONException json exception
     */
    private JSONObject initStatistic() throws RepositoryException,
                                              JSONException {
        LOGGER.info("Initializing statistic....");
        final Transaction transaction =
                AbstractGAERepository.DATASTORE_SERVICE.beginTransaction();
        final JSONObject ret = new JSONObject();
        try {
            ret.put(Keys.OBJECT_ID, Statistic.STATISTIC);
            ret.put(Statistic.STATISTIC_BLOG_ARTICLE_COUNT, 0);
            ret.put(Statistic.STATISTIC_BLOG_VIEW_COUNT, 0);
            ret.put(Statistic.STATISTIC_BLOG_COMMENT_COUNT, 0);
            statisticRepository.add(ret);
            transaction.commit();
        } catch (final Exception e) {
            transaction.rollback();
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new RuntimeException("Statistic init error!");
        }

        LOGGER.info("Initialized statistic");

        return ret;
    }

    /**
     * Initializes preference.
     *
     * @return preference
     */
    private JSONObject initPreference() {
        LOGGER.info("Initializing preference....");

        final Transaction transaction =
                AbstractGAERepository.DATASTORE_SERVICE.beginTransaction();
        final JSONObject ret = new JSONObject();

        try {
            final String preferenceId = PREFERENCE;
            ret.put(ARTICLE_LIST_DISPLAY_COUNT,
                    Preference.Default.DEFAULT_ARTICLE_LIST_DISPLAY_COUNT);
            ret.put(ARTICLE_LIST_PAGINATION_WINDOW_SIZE,
                    Preference.Default.DEFAULT_ARTICLE_LIST_PAGINATION_WINDOW_SIZE);
            ret.put(MOST_USED_TAG_DISPLAY_CNT,
                    Preference.Default.DEFAULT_MOST_USED_TAG_DISPLAY_COUNT);
            ret.put(MOST_COMMENT_ARTICLE_DISPLAY_CNT,
                    Preference.Default.DEFAULT_MOST_COMMENT_ARTICLE_DISPLAY_COUNT);
            ret.put(RECENT_ARTICLE_DISPLAY_CNT,
                    Preference.Default.DEFAULT_RECENT_ARTICLE_DISPLAY_COUNT);
            ret.put(RECENT_COMMENT_DISPLAY_CNT,
                    Preference.Default.DEFAULT_RECENT_COMMENT_DISPLAY_COUNT);
            ret.put(BLOG_TITLE,
                    Preference.Default.DEFAULT_BLOG_TITLE);
            ret.put(BLOG_SUBTITLE,
                    Preference.Default.DEFAULT_BLOG_SUBTITLE);
            ret.put(BLOG_HOST,
                    Preference.Default.DEFAULT_BLOG_HOST);
            ret.put(ADMIN_EMAIL, // Current logged in adminstrator's email
                    userService.getCurrentUser().getEmail());
            ret.put(LOCALE_STRING,
                    Preference.Default.DEFAULT_LANGUAGE);

            final String skinDirName = Preference.Default.DEFAULT_SKIN_DIR_NAME;
            ret.put(Skin.SKIN_DIR_NAME, skinDirName);

            final String skinName = skins.getSkinName(skinDirName);
            ret.put(Skin.SKIN_NAME, skinName);

            final Set<String> skinDirNames = skins.getSkinDirNames();
            final JSONArray skinArray = new JSONArray();
            for (final String dirName : skinDirNames) {
                final JSONObject skin = new JSONObject();
                skinArray.put(skin);

                final String name = skins.getSkinName(dirName);
                skin.put(Skin.SKIN_NAME, name);
                skin.put(Skin.SKIN_DIR_NAME, dirName);
            }

            ret.put(Skin.SKINS, skinArray.toString());

            try {
                final String webRootPath = SoloServletListener.getWebRoot();
                final String skinPath = webRootPath + Skin.SKINS + "/"
                                        + skinDirName;
                Templates.CONFIGURATION.setDirectoryForTemplateLoading(
                        new File(skinPath));
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }

            Templates.CONFIGURATION.setTimeZone(
                    TimeZone.getTimeZone("Asia/Shanghai"));

            ret.put(Keys.OBJECT_ID, preferenceId);
            preferenceRepository.add(ret);

            eventManager.fireEventSynchronously(// for upgrade extensions
                    new Event<JSONObject>(EventTypes.PREFERENCE_LOAD,
                                          ret));

            preferenceRepository.update(preferenceId, ret);
            transaction.commit();
        } catch (final Exception e) {
            transaction.rollback();
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new RuntimeException("Preference init error!");
        }

        LOGGER.info("Initialized preference");

        return ret;
    }
}
