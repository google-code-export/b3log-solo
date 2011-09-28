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
package org.b3log.solo.jsonrpc.impl;

import java.util.Set;
import org.b3log.solo.SoloServletListener;
import org.b3log.latke.repository.RepositoryException;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.latke.Keys;
import org.b3log.latke.action.ActionException;
import org.b3log.latke.action.util.Paginator;
import org.b3log.latke.event.Event;
import org.b3log.latke.event.EventManager;
import org.b3log.latke.model.Pagination;
import org.b3log.latke.model.Role;
import org.b3log.latke.model.User;
import org.b3log.latke.repository.Query;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.user.UserService;
import org.b3log.latke.user.UserServiceFactory;
import org.b3log.latke.util.freemarker.Templates;
import org.b3log.solo.web.action.StatusCodes;
import org.b3log.solo.event.EventTypes;
import org.b3log.solo.jsonrpc.AbstractGAEJSONRpcService;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.Comment;
import org.b3log.solo.model.Preference;
import org.b3log.solo.repository.PreferenceRepository;
import static org.b3log.solo.model.Preference.*;
import org.b3log.solo.model.Skin;
import org.b3log.solo.model.Statistic;
import org.b3log.solo.repository.StatisticRepository;
import org.b3log.solo.repository.UserRepository;
import org.b3log.solo.repository.impl.PreferenceRepositoryImpl;
import org.b3log.solo.repository.impl.StatisticRepositoryImpl;
import org.b3log.solo.repository.impl.UserRepositoryImpl;
import org.b3log.solo.util.Skins;
import org.b3log.solo.util.TimeZones;
import org.b3log.solo.util.Users;
import org.b3log.solo.web.processor.CaptchaProcessor;
import org.b3log.solo.web.processor.CommentProcessor;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Administrator service for JavaScript client.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.1.0.0, Sep 28, 2011
 * @since 0.3.1
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
    private UserService userService = UserServiceFactory.getUserService();
    /**
     * Preference repository.
     */
    private PreferenceRepository preferenceRepository =
            PreferenceRepositoryImpl.getInstance();
    /**
     * User repository.
     */
    private UserRepository userRepository = UserRepositoryImpl.getInstance();
    /**
     * Statistic repository.
     */
    private StatisticRepository statisticRepository =
            StatisticRepositoryImpl.getInstance();
    /**
     * Event manager.
     */
    private EventManager eventManager = EventManager.getInstance();
    /**
     * Skin utilities.
     */
    private Skins skins = Skins.getInstance();
    /**
     * User utilities.
     */
    private Users userUtils = Users.getInstance();
    /**
     * Article service.
     */
    private ArticleService articleService = ArticleService.getInstance();
    /**
     * Time zone utilities.
     */
    private TimeZones timeZoneUtils = TimeZones.getInstance();
    /**
     * Maximum count of initialization.
     */
    private static final int MAX_RETRIES_CNT = 3;

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
        final JSONObject ret = new JSONObject();
        if (!userUtils.isAdminLoggedIn(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return ret;
        }

        final Transaction transaction = userRepository.beginTransaction();
        try {
            final String userId = requestJSONObject.getString(Keys.OBJECT_ID);
            userRepository.remove(userId);
            transaction.commit();

            ret.put(Keys.STATUS_CODE, StatusCodes.REMOVE_USER_SUCC);
        } catch (final Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new ActionException(e);
        }

        return ret;
    }

    /**
     * Adds a user(default role) with the specified request json object.
     *
     * @param requestJSONObject the specified request json object, for example,
     * <pre>
     * {
     *     "userName": "",
     *     "userEmail": "",
     *     "userRole": "" // optional, uses {@value Role#DEFAULT_ROLE} instead,
     *                       if not speciffied
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
        final JSONObject ret = new JSONObject();
        if (!userUtils.isAdminLoggedIn(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return ret;
        }

        final Transaction transaction = userRepository.beginTransaction();
        try {
            final JSONObject user = new JSONObject();
            final String userEmail =
                    requestJSONObject.getString(User.USER_EMAIL).
                    trim().toLowerCase();
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
            final String roleName = requestJSONObject.optString(
                    User.USER_ROLE, Role.DEFAULT_ROLE);
            user.put(User.USER_ROLE, roleName);
            userRepository.add(user);
            transaction.commit();

            ret.put(Keys.STATUS_CODE, StatusCodes.ADD_USER_SUCC);
        } catch (final Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new ActionException(e);
        }

        return ret;
    }

    /**
     * Gets a user by the specified request json object.
     *
     * @param requestJSONObject the specified request json object, for example,
     * <pre>
     * {
     *     "oId": ""
     * }
     * </pre>
     * @return for example,
     * <pre>
     * {
     *     "user": {
     *         "oId": "",
     *         "userName": "",
     *         "userEmail": ""
     *     },
     *     "sc": "GET_USER_SUCC"
     * }
     * </pre>
     * @throws ActionException action exception
     */
    public JSONObject getUser(final JSONObject requestJSONObject)
            throws ActionException {
        final JSONObject ret = new JSONObject();

        try {
            final String userId = requestJSONObject.getString(Keys.OBJECT_ID);
            final JSONObject user = userRepository.get(userId);
            ret.put(User.USER, user);

            ret.put(Keys.STATUS_CODE, StatusCodes.GET_USER_SUCC);

            LOGGER.log(Level.FINER, "Got a user[oId={0}]", userId);
        } catch (final Exception e) {
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
        final JSONObject ret = new JSONObject();
        if (!userUtils.isAdminLoggedIn(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return ret;
        }

        try {
            final int currentPageNum = requestJSONObject.getInt(
                    Pagination.PAGINATION_CURRENT_PAGE_NUM);
            final int pageSize = requestJSONObject.getInt(
                    Pagination.PAGINATION_PAGE_SIZE);
            final int windowSize = requestJSONObject.getInt(
                    Pagination.PAGINATION_WINDOW_SIZE);
            final Query query = new Query().setCurrentPageNum(currentPageNum).
                    setPageSize(pageSize);
            final JSONObject result = userRepository.get(query);

            final int pageCount = result.getJSONObject(Pagination.PAGINATION).
                    getInt(Pagination.PAGINATION_PAGE_COUNT);

            final JSONObject pagination = new JSONObject();
            ret.put(Pagination.PAGINATION, pagination);
            final List<Integer> pageNums =
                    Paginator.paginate(currentPageNum, pageSize,
                                       pageCount,
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
     *     "userEmail": "",
     *     "userRole": ""
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
        final JSONObject ret = new JSONObject();
        if (!userUtils.isAdminLoggedIn(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return ret;
        }

        final Transaction transaction = userRepository.beginTransaction();
        try {
            final String oldUserId = requestJSONObject.getString(Keys.OBJECT_ID);
            final JSONObject oldUser = userRepository.get(oldUserId);
            final String userNewEmail =
                    requestJSONObject.getString(User.USER_EMAIL).trim();

            final String userRole = requestJSONObject.getString(User.USER_ROLE);
            if (Role.ADMIN_ROLE.equals(userRole)) {
                final String adminOldEmail = oldUser.getString(User.USER_EMAIL);
                if (!adminOldEmail.equals(userNewEmail)) {
                    // Can't update the admin's email
                    ret.put(Keys.STATUS_CODE, StatusCodes.UPDATE_USER_FAIL_);

                    return ret;
                }
            }

            // Remove old user
            if (null == oldUser) {
                ret.put(Keys.STATUS_CODE, StatusCodes.UPDATE_USER_FAIL_);

                return ret;
            }

            // Check email is whether duplicated
            final JSONObject mayBeAnother = userRepository.getByEmail(
                    userNewEmail);
            if (null != mayBeAnother
                && !mayBeAnother.getString(Keys.OBJECT_ID).equals(oldUserId)) {
                // Exists someone else has the save email as requested
                ret.put(Keys.STATUS_CODE,
                        StatusCodes.ADD_USER_FAIL_DUPLICATED_EMAIL);

                return ret;
            }

            // Update
            final String userName = requestJSONObject.getString(User.USER_NAME);
            oldUser.put(User.USER_EMAIL, userNewEmail);
            oldUser.put(User.USER_NAME, userName);
            // Unchanges the default role

            userRepository.update(oldUserId, oldUser);
            transaction.commit();

            ret.put(Keys.STATUS_CODE, StatusCodes.UPDATE_USER_SUCC);
        } catch (final Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new ActionException(e);
        }

        return ret;
    }

    /**
     * Initializes B3log Solo.
     * 
     * <p>
     * Initializes the followings in sequence:
     *   <ol>
     *     <li>Statistic.</li>
     *     <li>Preference.</li>
     *     <li>Administrator.</li>
     *   </ol>
     * </p>
     * 
     * <p>
     *   We will try to initialize B3log Solo 3 times at most.
     * </p>
     * 
     * <p>
     *   Posts "Hello World!" article and its comment while B3log Solo 
     *   initialized.
     * </p>
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
        final JSONObject ret = new JSONObject();

        if (SoloServletListener.isInited()) {
            response.sendRedirect("/");

            return ret;
        }

        int retries = MAX_RETRIES_CNT;
        while (true) {
            final Transaction transaction = userRepository.beginTransaction();
            try {
                final JSONObject statistic =
                        statisticRepository.get(Statistic.STATISTIC);
                if (null == statistic) {
                    initStatistic();
                    initPreference(request);
                    initAdmin(request);
                }

                ret.put(Keys.STATUS_CODE, StatusCodes.INIT_B3LOG_SOLO_SUCC);

                transaction.commit();
                break;
            } catch (final Exception e) {
                if (0 == retries) {
                    LOGGER.log(Level.SEVERE, "Initialize B3log Solo error", e);
                    throw new ActionException("Initailize B3log Solo error!");
                }

                // Allow retry to occur
                --retries;
                LOGGER.log(Level.WARNING,
                           "Retrying to init B3log Solo[retries={0}]", retries);
            } finally {
                if (transaction.isActive()) {
                    transaction.rollback();
                }
            }
        }

        try {
            final JSONObject get =
                    userRepository.get(new Query());

            helloWorld(request, response);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, "Hello World error?!", e);
        }

        return ret;
    }

    /**
     * Publishes the first article "Hello World".
     *
     * @param request the specified http servlet request
     * @param response the specified http servlet response
     * @throws Exception exception
     */
    private void helloWorld(final HttpServletRequest request,
                            final HttpServletResponse response)
            throws Exception {
        LOGGER.info("Hello World!");

        final JSONObject article = new JSONObject();

        // XXX: no i18n
        article.put(Article.ARTICLE_TITLE, "Hello World!");
        final String content =
                "Welcome to <a style=\"text-decoration: none;\" target=\"_blank\" "
                + "href=\"http://b3log-solo.googlecode.com\">"
                + "<span style=\"color: orange;\">B</span>"
                + "<span style=\"font-size: 9px; color: blue;\">"
                + "<sup>3</sup></span><span style=\"color: green;\">L</span>"
                + "<span style=\"color: red;\">O</span>"
                + "<span style=\"color: blue;\">G</span> "
                + " <span style=\"color: orangered; font-weight: bold;\">Solo</span>"
                + "</a>. This is your first post. Edit or delete it, "
                + "then start blogging!";
        article.put(Article.ARTICLE_ABSTRACT, content);
        article.put(Article.ARTICLE_CONTENT, content);
        article.put(Article.ARTICLE_TAGS_REF, "B3log");
        article.put(Article.ARTICLE_PERMALINK, "/b3log-hello-wolrd.html");
        article.put(Article.ARTICLE_IS_PUBLISHED, true);
        article.put(Article.ARTICLE_SIGN_REF + "_" + Keys.OBJECT_ID, "0");

        JSONObject requestJSONObject = new JSONObject();
        requestJSONObject.put(Article.ARTICLE, article);
        final String articleId = articleService.addArticle(
                requestJSONObject, request,
                response).getString(Keys.OBJECT_ID);

        requestJSONObject = new JSONObject();
        final String captchaForInit = "captchaForInit";
        request.getSession().setAttribute(CaptchaProcessor.CAPTCHA,
                                          captchaForInit);
        requestJSONObject.put(CaptchaProcessor.CAPTCHA, captchaForInit);
        requestJSONObject.put(Keys.OBJECT_ID, articleId);
        requestJSONObject.put(Comment.COMMENT_NAME, "88250");
        requestJSONObject.put(Comment.COMMENT_EMAIL, "DL88250@gmail.com");
        requestJSONObject.put(Comment.COMMENT_URL, "http://88250.b3log.org");
        requestJSONObject.put(
                Comment.COMMENT_CONTENT,
                "Hi, this is a comment. To delete a comment, just log in and "
                + "view the post's comments. There you will have the option "
                + "to delete them.");

        CommentProcessor.addArticleCommentInteral(requestJSONObject, request);
    }

    /**
     * Initializes administrator.
     * 
     * <p>
     *   <ul>
     *     <li>Username: Admin</li>
     *     <li>User Email: test@b3log.org</li>
     *     Used for login authentication.
     *     <li>User Password: 111111</li>
     *     Used for login authentication.
     *   </ul>
     * </p>
     *
     * @param request the specified request
     * @throws Exception exception
     */
    private void initAdmin(final HttpServletRequest request) throws Exception {
        LOGGER.info("Initializing admin....");
        final JSONObject admin = new JSONObject();

        admin.put(User.USER_NAME, "Admin");
        admin.put(User.USER_EMAIL, "test@b3log.org");
        admin.put(User.USER_ROLE, Role.ADMIN_ROLE);
        admin.put(User.USER_PASSWORD, "111111");

        userRepository.add(admin);

        LOGGER.info("Initialized admin");
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
        final JSONObject ret = new JSONObject();
        ret.put(Keys.OBJECT_ID, Statistic.STATISTIC);
        ret.put(Statistic.STATISTIC_BLOG_ARTICLE_COUNT, 0);
        ret.put(Statistic.STATISTIC_PUBLISHED_ARTICLE_COUNT, 0);
        ret.put(Statistic.STATISTIC_BLOG_VIEW_COUNT, 0);
        ret.put(Statistic.STATISTIC_BLOG_COMMENT_COUNT, 0);
        ret.put(Statistic.STATISTIC_PUBLISHED_BLOG_COMMENT_COUNT, 0);
        statisticRepository.add(ret);

        LOGGER.info("Initialized statistic");

        return ret;
    }

    /**
     * Initializes preference.
     *
     * @param request the specified request
     * @return preference
     * @throws Exception exception
     */
    private JSONObject initPreference(final HttpServletRequest request)
            throws Exception {
        LOGGER.info("Initializing preference....");

        final JSONObject ret = new JSONObject();

        final String preferenceId = PREFERENCE;
        ret.put(NOTICE_BOARD, Default.DEFAULT_NOTICE_BOARD);
        ret.put(META_DESCRIPTION,
                Default.DEFAULT_META_DESCRIPTION);
        ret.put(META_KEYWORDS, Default.DEFAULT_META_KEYWORDS);
        ret.put(HTML_HEAD, Default.DEFAULT_HTML_HEAD);
        ret.put(GOOGLE_OAUTH_CONSUMER_SECRET,
                Default.DEFAULT_GOOLE_OAUTH_CONSUMER_SECRET);
        ret.put(Preference.RELEVANT_ARTICLES_DISPLAY_CNT,
                Default.DEFAULT_RELEVANT_ARTICLES_DISPLAY_COUNT);
        ret.put(Preference.RANDOM_ARTICLES_DISPLAY_CNT,
                Default.DEFAULT_RANDOM_ARTICLES_DISPLAY_COUNT);
        ret.put(Preference.EXTERNAL_RELEVANT_ARTICLES_DISPLAY_CNT,
                Default.DEFAULT_EXTERNAL_RELEVANT_ARTICLES_DISPLAY_COUNT);
        ret.put(Preference.MOST_VIEW_ARTICLE_DISPLAY_CNT,
                Default.DEFAULT_MOST_VIEW_ARTICLES_DISPLAY_COUNT);
        ret.put(ARTICLE_LIST_DISPLAY_COUNT,
                Default.DEFAULT_ARTICLE_LIST_DISPLAY_COUNT);
        ret.put(ARTICLE_LIST_PAGINATION_WINDOW_SIZE,
                Default.DEFAULT_ARTICLE_LIST_PAGINATION_WINDOW_SIZE);
        ret.put(MOST_USED_TAG_DISPLAY_CNT,
                Default.DEFAULT_MOST_USED_TAG_DISPLAY_COUNT);
        ret.put(MOST_COMMENT_ARTICLE_DISPLAY_CNT,
                Default.DEFAULT_MOST_COMMENT_ARTICLE_DISPLAY_COUNT);
        ret.put(RECENT_ARTICLE_DISPLAY_CNT,
                Default.DEFAULT_RECENT_ARTICLE_DISPLAY_COUNT);
        ret.put(RECENT_COMMENT_DISPLAY_CNT,
                Default.DEFAULT_RECENT_COMMENT_DISPLAY_COUNT);
        ret.put(BLOG_TITLE, Default.DEFAULT_BLOG_TITLE);
        ret.put(BLOG_SUBTITLE, Default.DEFAULT_BLOG_SUBTITLE);
        ret.put(BLOG_HOST, Default.DEFAULT_BLOG_HOST);
        ret.put(ADMIN_EMAIL, // Current logged in adminstrator's email
                userService.getCurrentUser(request).getEmail());
        ret.put(LOCALE_STRING, Default.DEFAULT_LANGUAGE);
        ret.put(ENABLE_ARTICLE_UPDATE_HINT,
                Default.DEFAULT_ENABLE_ARTICLE_UPDATE_HINT);
        ret.put(ENABLE_POST_TO_TENCENT_MICROBLOG,
                Default.DEFAULT_ENABLE_POST_TO_TENCENT_MICROBLOG);
        ret.put(SIGNS, Default.DEFAULT_SIGNS);
        ret.put(TIME_ZONE_ID, Default.DEFAULT_TIME_ZONE);
        ret.put(PAGE_CACHE_ENABLED, Default.DEFAULT_PAGE_CACHE_ENABLED);
        ret.put(ALLOW_VISIT_DRAFT_VIA_PERMALINK,
                Default.DEFAULT_ALLOW_VISIT_DRAFT_VIA_PERMALINK);
        ret.put(VERSION, SoloServletListener.VERSION);

        final String skinDirName = Default.DEFAULT_SKIN_DIR_NAME;
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
            LOGGER.log(Level.SEVERE, "Loads skins error!", e);
            throw new IllegalStateException(e);
        }

        timeZoneUtils.setTimeZone("Asia/Shanghai");

        ret.put(Keys.OBJECT_ID, preferenceId);
        preferenceRepository.add(ret);

        eventManager.fireEventSynchronously(// for upgrade extensions
                new Event<JSONObject>(EventTypes.PREFERENCE_LOAD,
                                      ret));

        preferenceRepository.update(preferenceId, ret);

        LOGGER.info("Initialized preference");

        return ret;
    }

    /**
     * Gets the URL of user logout.
     *
     * @param request the specified http servlet request
     * @param response the specified http servlet response
     * @return logout URL, returns {@code null} if the user is not logged in
     * @throws ActionException action exception
     * @throws IOException io exception
     */
    public String getLogoutURL(final HttpServletRequest request,
                               final HttpServletResponse response)
            throws ActionException, IOException {
        if (!userUtils.isLoggedIn(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return null;
        }

        return userService.createLogoutURL("/");
    }

    /**
     * Gets the URL of user login.
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
     * Gets the {@link AdminService} singleton.
     *
     * @return the singleton
     */
    public static AdminService getInstance() {
        return SingletonHolder.SINGLETON;
    }

    /**
     * Private default constructor.
     */
    private AdminService() {
    }

    /**
     * Singleton holder.
     *
     * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
     * @version 1.0.0.0, Jan 12, 2011
     */
    private static final class SingletonHolder {

        /**
         * Singleton.
         */
        private static final AdminService SINGLETON = new AdminService();

        /**
         * Private default constructor.
         */
        private SingletonHolder() {
        }
    }
}
