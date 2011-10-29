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
package org.b3log.solo.service;

import org.b3log.latke.service.ServiceException;
import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.b3log.latke.Keys;
import org.b3log.latke.model.Role;
import org.b3log.latke.model.User;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.util.Sessions;
import org.b3log.latke.util.Strings;
import org.b3log.latke.util.freemarker.Templates;
import org.b3log.solo.SoloServletListener;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.Comment;
import org.b3log.solo.model.Preference;
import org.b3log.solo.model.Skin;
import org.b3log.solo.model.Statistic;
import org.b3log.solo.repository.PreferenceRepository;
import org.b3log.solo.repository.StatisticRepository;
import org.b3log.solo.repository.UserRepository;
import org.b3log.solo.repository.impl.PreferenceRepositoryImpl;
import org.b3log.solo.repository.impl.StatisticRepositoryImpl;
import org.b3log.solo.repository.impl.UserRepositoryImpl;
import org.b3log.solo.util.Skins;
import org.b3log.solo.util.TimeZones;
import org.b3log.solo.web.processor.CaptchaProcessor;
import org.b3log.solo.web.processor.CommentProcessor;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import static org.b3log.solo.model.Preference.*;

/**
 * B3log Solo initialization service.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Oct 28, 2011
 * @since 0.4.0
 */
public final class InitService {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(InitService.class.getName());
    /**
     * Time zone utilities.
     */
    private TimeZones timeZoneUtils = TimeZones.getInstance();
    /**
     * Statistic repository.
     */
    private StatisticRepository statisticRepository =
            StatisticRepositoryImpl.getInstance();
    /**
     * Article management service.
     */
    private ArticleMgmtService articleMgmtService =
            ArticleMgmtService.getInstance();
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
     * Skin utilities.
     */
    private Skins skins = Skins.getInstance();
    /**
     * Maximum count of initialization.
     */
    private static final int MAX_RETRIES_CNT = 3;

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
     * 
     * @param requestJSONObject the specified request json object, for example,
     * <pre>
     * {
     *     "userName": "",
     *     "userEmail": "",
     *     "userPassword": ""
     * }
     * </pre>
     * @param request the specified http servlet request
     * @param response the specified http servlet response
     * @throws ServiceException service exception
     */
    public void init(final JSONObject requestJSONObject,
                     final HttpServletRequest request,
                     final HttpServletResponse response)
            throws ServiceException {
        if (SoloServletListener.isInited()) {
            return;
        }

        int retries = MAX_RETRIES_CNT;
        while (true) {
            final Transaction transaction = userRepository.beginTransaction();
            try {
                final JSONObject statistic =
                        statisticRepository.get(Statistic.STATISTIC);
                if (null == statistic) {
                    initStatistic();
                    initPreference(requestJSONObject);
                    initAdmin(requestJSONObject, request, response);
                }

                transaction.commit();
                break;
            } catch (final Exception e) {
                if (0 == retries) {
                    LOGGER.log(Level.SEVERE, "Initialize B3log Solo error", e);
                    throw new ServiceException("Initailize B3log Solo error: "
                                               + e.getMessage());
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

        final Transaction transaction = userRepository.beginTransaction();
        try {
            helloWorld(request, response);
            transaction.commit();
        } catch (final Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }

            LOGGER.log(Level.SEVERE, "Hello World error?!", e);
        }
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

        final String articleId =
                articleMgmtService.addArticleInternal(article, request);

        final JSONObject requestJSONObject = new JSONObject();
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

        CommentProcessor.addArticleCommentInternal(requestJSONObject, request);
        LOGGER.info("Hello World!");
    }

    /**
     * Initializes administrator with the specified request json object, and 
     * then logins it.
     *
     * @param requestJSONObject the specified request json object, for example,
     * <pre>
     * {
     *     "userName": "",
     *     "userEmail": "",
     *     "userPassowrd": ""
     * }
     * </pre>
     * @param request the specified request
     * @param response the specified response
     * @throws Exception exception
     */
    private void initAdmin(final JSONObject requestJSONObject,
                           final HttpServletRequest request,
                           final HttpServletResponse response) throws Exception {
        LOGGER.info("Initializing admin....");
        final JSONObject admin = new JSONObject();

        final String userName = requestJSONObject.getString(User.USER_NAME);
        final String userEmail = requestJSONObject.getString(User.USER_EMAIL);
        final String userPassword =
                requestJSONObject.getString(User.USER_PASSWORD);

        if (Strings.isEmptyOrNull(userName)
            || Strings.isEmptyOrNull(userEmail)
            || Strings.isEmptyOrNull(userPassword)
            || !Strings.isEmail(userEmail)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        admin.put(User.USER_NAME, userName);
        admin.put(User.USER_EMAIL, userEmail);
        admin.put(User.USER_ROLE, Role.ADMIN_ROLE);
        admin.put(User.USER_PASSWORD, userPassword);

        userRepository.add(admin);

        LOGGER.info("Initialized admin");

        Sessions.login(request, response, admin);
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
     * @param requestJSONObject the specified json object
     * @return preference
     * @throws Exception exception
     */
    private JSONObject initPreference(final JSONObject requestJSONObject)
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
        final String userEmail = requestJSONObject.getString(User.USER_EMAIL);
        ret.put(ADMIN_EMAIL, userEmail);
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
        ret.put(ARTICLE_LIST_STYLE, Default.DEFAULT_ARTICLE_LIST_STYLE);

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

        preferenceRepository.update(preferenceId, ret);

        LOGGER.info("Initialized preference");

        return ret;
    }

    /**
     * Gets the {@link InitService} singleton.
     *
     * @return the singleton
     */
    public static InitService getInstance() {
        return SingletonHolder.SINGLETON;
    }

    /**
     * Private constructor.
     */
    private InitService() {
    }

    /**
     * Singleton holder.
     *
     * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
     * @version 1.0.0.0, Oct 24, 2011
     */
    private static final class SingletonHolder {

        /**
         * Singleton.
         */
        private static final InitService SINGLETON =
                new InitService();

        /**
         * Private default constructor.
         */
        private SingletonHolder() {
        }
    }
}
