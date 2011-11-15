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
package org.b3log.solo.web.processor;

import org.b3log.solo.service.PreferenceQueryService;
import org.b3log.solo.util.Statistics;
import org.b3log.latke.repository.Repository;
import org.b3log.latke.repository.Transaction;
import org.b3log.solo.repository.ArchiveDateRepository;
import org.b3log.solo.repository.impl.ArchiveDateRepositoryImpl;
import org.b3log.latke.action.util.Paginator;
import org.b3log.latke.model.Pagination;
import org.b3log.latke.util.Dates;
import org.b3log.latke.util.Locales;
import org.b3log.solo.web.util.Requests;
import org.b3log.solo.model.ArchiveDate;
import org.b3log.solo.repository.ArchiveDateArticleRepository;
import org.b3log.solo.repository.impl.ArchiveDateArticleRepositoryImpl;
import org.b3log.solo.web.processor.renderer.FrontFreeMarkerRenderer;
import java.util.ArrayList;
import java.util.Collections;
import org.b3log.solo.util.comparator.Comparators;
import org.json.JSONArray;
import org.json.JSONException;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.model.User;
import org.b3log.solo.model.Preference;
import org.jsoup.Jsoup;
import org.b3log.solo.util.Articles;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import org.b3log.latke.annotation.RequestProcessing;
import org.b3log.latke.annotation.RequestProcessor;
import org.b3log.solo.web.util.Filler;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.service.ServiceException;
import org.b3log.latke.servlet.renderer.freemarker.AbstractFreeMarkerRenderer;
import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.HTTPRequestMethod;
import org.b3log.latke.servlet.renderer.JSONRenderer;
import org.b3log.latke.servlet.renderer.TextHTMLRenderer;
import org.b3log.latke.util.Stopwatchs;
import org.b3log.latke.util.Strings;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.Common;
import org.b3log.solo.model.PageTypes;
import org.b3log.solo.repository.impl.StatisticRepositoryImpl;
import org.b3log.solo.service.ArticleQueryService;
import org.b3log.solo.service.CommentQueryService;
import org.b3log.solo.service.UserQueryService;
import org.b3log.solo.util.Skins;
import org.b3log.solo.util.Users;
import org.json.JSONObject;
import static org.b3log.latke.action.AbstractCacheablePageAction.*;

/**
 * Article processor.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.1.0.8, Nov 11, 2011
 * @since 0.3.1
 */
@RequestProcessor
public final class ArticleProcessor {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(ArticleProcessor.class.getName());
    /**
     * Article query service.
     */
    private ArticleQueryService articleQueryService =
            ArticleQueryService.getInstance();
    /**
     * Comment query service.
     */
    private CommentQueryService commentQueryService =
            CommentQueryService.getInstance();
    /**
     * Filler.
     */
    private Filler filler = Filler.getInstance();
    /**
     * Language service.
     */
    private LangPropsService langPropsService = LangPropsService.getInstance();
    /**
     * Article utilities.
     */
    private Articles articleUtils = Articles.getInstance();
    /**
     * Statistic utilities.
     */
    private Statistics statistics = Statistics.getInstance();
    /**
     * Preference query service.
     */
    private PreferenceQueryService preferenceQueryService =
            PreferenceQueryService.getInstance();
    /**
     * Skin utilities.
     */
    private Skins skins = Skins.getInstance();
    /**
     * Archive date-Article repository.
     */
    private ArchiveDateArticleRepository archiveDateArticleRepository =
            ArchiveDateArticleRepositoryImpl.getInstance();
    /**
     * Archive date repository.
     */
    private ArchiveDateRepository archiveDateRepository =
            ArchiveDateRepositoryImpl.getInstance();
    /**
     * User query service.
     */
    private UserQueryService userQueryService = UserQueryService.getInstance();
    /**
     * Default update count for article random value.
     */
    private static final int DEFAULT_UPDATE_CNT = 10;

    /**
     * Gets random articles with the specified context.
     * 
     * @param context the specified context
     */
    @RequestProcessing(value = {"/get-random-articles.do"},
                       method = HTTPRequestMethod.POST)
    public void getRandomArticles(final HTTPRequestContext context) {
        Stopwatchs.start("Get Random Articles");


        final JSONObject jsonObject = new JSONObject();

        try {
            final List<JSONObject> randomArticles =
                    getRandomArticles(preferenceQueryService.getPreference());

            jsonObject.put(Common.RANDOM_ARTICLES, randomArticles);

            final JSONRenderer renderer = new JSONRenderer();
            context.setRenderer(renderer);
            renderer.setJSONObject(jsonObject);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

        Stopwatchs.end();
    }

    /**
     * Gets relevant articles with the specified context.
     * 
     * @param context the specified context
     * @param request the specified request
     * @param response the specified response
     * @throws Exception exception 
     */
    @RequestProcessing(value = {"/article/id/*/relevant/articles"},
                       method = HTTPRequestMethod.GET)
    public void getRelevantArticles(final HTTPRequestContext context,
                                    final HttpServletRequest request,
                                    final HttpServletResponse response)
            throws Exception {
        Stopwatchs.start("Get Relevant Articles");
        final String requestURI = request.getRequestURI();

        final String articleId =
                StringUtils.substringBetween(requestURI,
                                             "/article/id/",
                                             "/relevant/articles");
        if (Strings.isEmptyOrNull(articleId)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);

            return;
        }

        final JSONObject article = articleQueryService.getArticleById(articleId);
        if (null == article) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);

            return;
        }

        final List<JSONObject> relevantArticles =
                articleQueryService.getRelevantArticles(article,
                                                        preferenceQueryService.
                getPreference());
        final JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(Common.RELEVANT_ARTICLES, relevantArticles);

            final JSONRenderer renderer = new JSONRenderer();
            context.setRenderer(renderer);
            renderer.setJSONObject(jsonObject);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

        Stopwatchs.end();
    }

    /**
     * Gets article content with the specified context.
     * 
     * @param context the specified context
     * @param request the specified request 
     */
    @RequestProcessing(value = {"/get-article-content"},
                       method = HTTPRequestMethod.GET)
    public void getArticleContent(final HTTPRequestContext context,
                                  final HttpServletRequest request) {
        // XXX: Determines request coming from outer
        final String articleId = request.getParameter("id");

        if (Strings.isEmptyOrNull(articleId)) {
            return;
        }

        final TextHTMLRenderer renderer = new TextHTMLRenderer();
        context.setRenderer(renderer);

        String content = null;
        try {
            content = articleQueryService.getArticleContent(articleId);
        } catch (final ServiceException e) {
            LOGGER.log(Level.SEVERE, "Can not get article content", e);
            return;
        }

        if (null == content) {
            return;
        }

        renderer.setContent(content);
    }

    /**
     * Shows author articles with the specified context.
     * 
     * @param context the specified context
     * @param request the specified request
     * @param response the specified response
     * @throws IOException io exception
     * @throws JSONException json exception 
     */
    @RequestProcessing(value = {"/authors/**"}, method = HTTPRequestMethod.GET)
    public void showAuthorArticles(final HTTPRequestContext context,
                                   final HttpServletRequest request,
                                   final HttpServletResponse response)
            throws IOException, JSONException {
        final AbstractFreeMarkerRenderer renderer =
                new FrontFreeMarkerRenderer();
        context.setRenderer(renderer);

        renderer.setTemplateName("author-articles.ftl");

        try {
            String requestURI = request.getRequestURI();
            if (!requestURI.endsWith("/")) {
                requestURI += "/";
            }
            final String authorId = getAuthorId(requestURI);

            LOGGER.log(Level.FINER,
                       "Request author articles[requestURI={0}, authorId={1}]",
                       new Object[]{requestURI, authorId});

            final int currentPageNum = getAuthorCurrentPageNum(requestURI,
                                                               authorId);
            if (-1 == currentPageNum) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            LOGGER.log(Level.FINER,
                       "Request author articles[authorId={0}, currentPageNum={1}]",
                       new Object[]{authorId, currentPageNum});

            final JSONObject preference = preferenceQueryService.getPreference();
            if (null == preference) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            final int pageSize = preference.getInt(
                    Preference.ARTICLE_LIST_DISPLAY_COUNT);
            final int windowSize = preference.getInt(
                    Preference.ARTICLE_LIST_PAGINATION_WINDOW_SIZE);

            JSONObject result = userQueryService.getUser(authorId);
            final JSONObject author = result.getJSONObject(User.USER);

            final Map<String, String> langs =
                    langPropsService.getAll(Latkes.getLocale());
            request.setAttribute(CACHED_TYPE,
                                 langs.get(PageTypes.AUTHOR_ARTICLES));
            request.setAttribute(CACHED_OID, "No id");
            request.setAttribute(
                    CACHED_TITLE,
                    langs.get(PageTypes.AUTHOR_ARTICLES) + "  ["
                    + langs.get("pageNumLabel") + "=" + currentPageNum + ", "
                    + langs.get("authorLabel") + "=" + author.getString(
                    User.USER_NAME) + "]");
            request.setAttribute(CACHED_LINK, requestURI);

            final String authorEmail = author.getString(User.USER_EMAIL);
            result = articleQueryService.getArticlesByAuthorEmail(authorEmail,
                                                                  currentPageNum,
                                                                  pageSize);
            final List<JSONObject> articles =
                    org.b3log.latke.util.CollectionUtils.jsonArrayToList(result.
                    getJSONArray(Keys.RESULTS));
            if (articles.isEmpty()) {
                try {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                } catch (final IOException ex) {
                    LOGGER.severe(ex.getMessage());
                }
            }

            filler.setArticlesExProperties(articles, author, preference);

            if (preference.optBoolean(Preference.ENABLE_ARTICLE_UPDATE_HINT)) {
                Collections.sort(articles,
                                 Comparators.ARTICLE_UPDATE_DATE_COMPARATOR);
            } else {
                Collections.sort(articles,
                                 Comparators.ARTICLE_CREATE_DATE_COMPARATOR);
            }

            final int pageCount = result.optJSONObject(
                    Pagination.PAGINATION).optInt(
                    Pagination.PAGINATION_PAGE_COUNT);
            final List<Integer> pageNums =
                    Paginator.paginate(currentPageNum, pageSize, pageCount,
                                       windowSize);

            final Map<String, Object> dataModel = renderer.getDataModel();
            prepareShowAuthorArticles(pageNums, dataModel, pageCount,
                                      currentPageNum, articles, author,
                                      preference);

        } catch (final ServiceException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);

            try {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            } catch (final IOException ex) {
                LOGGER.severe(ex.getMessage());
            }
        }
    }

    /**
     * Shows archive articles with the specified context.
     * 
     * @param context the specified context
     * @param request the specified request
     * @param response the specified response 
     */
    @RequestProcessing(value = {"/archives/**"}, method = HTTPRequestMethod.GET)
    public void showArchiveArticles(final HTTPRequestContext context,
                                    final HttpServletRequest request,
                                    final HttpServletResponse response) {
        final AbstractFreeMarkerRenderer renderer =
                new FrontFreeMarkerRenderer();
        context.setRenderer(renderer);

        renderer.setTemplateName("archive-articles.ftl");

        try {
            String requestURI = request.getRequestURI();
            if (!requestURI.endsWith("/")) {
                requestURI += "/";
            }

            final String archiveDateString = getArchiveDate(requestURI);
            final int currentPageNum = getArchiveCurrentPageNum(requestURI);
            if (-1 == currentPageNum) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            LOGGER.log(Level.FINER,
                       "Request archive date[string={0}, currentPageNum={1}]",
                       new Object[]{archiveDateString, currentPageNum});

            final JSONObject archiveDate =
                    archiveDateRepository.getByArchiveDate(archiveDateString);
            if (null == archiveDate) {
                LOGGER.log(Level.WARNING,
                           "Can not find articles for the specified "
                           + "archive date[string={0}]",
                           archiveDate);
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            final String archiveDateId = archiveDate.getString(Keys.OBJECT_ID);

            final JSONObject preference = preferenceQueryService.getPreference();
            final int pageSize = preference.getInt(
                    Preference.ARTICLE_LIST_DISPLAY_COUNT);

            final int articleCount = archiveDate.getInt(
                    ArchiveDate.ARCHIVE_DATE_PUBLISHED_ARTICLE_COUNT);
            final int pageCount = (int) Math.ceil((double) articleCount
                                                  / (double) pageSize);

            final JSONObject result =
                    archiveDateArticleRepository.getByArchiveDateId(
                    archiveDateId, currentPageNum, pageSize, articleCount);

            @SuppressWarnings("unchecked")
            final JSONArray archiveDateArticleRelations = result.getJSONArray(
                    Keys.RESULTS);
            if (0 == archiveDateArticleRelations.length()) {
                try {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                } catch (final IOException ex) {
                    LOGGER.severe(ex.getMessage());
                }
            }

            final List<JSONObject> articles = new ArrayList<JSONObject>();
            for (int i = 0; i < archiveDateArticleRelations.length(); i++) {
                final JSONObject archiveDateArticleRelation =
                        archiveDateArticleRelations.getJSONObject(i);
                final String articleId =
                        archiveDateArticleRelation.getString(Article.ARTICLE
                                                             + "_"
                                                             + Keys.OBJECT_ID);
                final JSONObject article =
                        articleQueryService.getArticleById(articleId);
                if (!article.getBoolean(Article.ARTICLE_IS_PUBLISHED)) { // Skips the unpublished article
                    continue;
                }

                articles.add(article);
            }

            final boolean hasMultipleUsers =
                    Users.getInstance().hasMultipleUsers();
            if (hasMultipleUsers) {
                filler.setArticlesExProperties(articles, preference);
            } else {
                if (!articles.isEmpty()) {
                    final JSONObject author =
                            articleUtils.getAuthor(articles.get(0));
                    filler.setArticlesExProperties(articles, author, preference);
                }
            }

            sort(preference, articles);

            final Map<String, Object> dataModel = renderer.getDataModel();
            final String cachedTitle =
                    prepareShowArchiveArticles(preference, dataModel, articles,
                                               currentPageNum,
                                               pageCount, archiveDateString,
                                               archiveDate);

            final Map<String, String> langs =
                    langPropsService.getAll(Latkes.getLocale());
            request.setAttribute(CACHED_TYPE, langs.get(PageTypes.DATE_ARTICLES));
            request.setAttribute(CACHED_OID, archiveDateId);
            request.setAttribute(CACHED_TITLE,
                                 cachedTitle + "  [" + langs.get("pageNumLabel")
                                 + "=" + currentPageNum + "]");
            request.setAttribute(CACHED_LINK, requestURI);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);

            try {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            } catch (final IOException ex) {
                LOGGER.severe(ex.getMessage());
            }
        }
    }

    /**
     * Updates article random double value.
     * 
     * @param request the specified request
     */
    @RequestProcessing(value = {"/article-random-double-gen.do"},
                       method = HTTPRequestMethod.GET)
    public void updateArticlesRandomValue(final HttpServletRequest request) {
        // Commented for issue 308, see http://code.google.com/p/b3log-solo/issues/detail?id=308#c4 and 
        // cron.xml for more details.
//        int updateCnt = DEFAULT_UPDATE_CNT;
//        try {
//            updateCnt =
//                    Integer.valueOf(request.getParameter("cnt"));
//        } catch (final NumberFormatException e) {
//            LOGGER.log(Level.WARNING, e.getMessage(), e);
//        }
//
//        try {
//            articleMgmtService.updateArticlesRandomValue(updateCnt);
//        } catch (final ServiceException e) {
//            LOGGER.log(Level.SEVERE, "Updates articles random values failed", e);
//        }
    }

    /**
     * Shows an article with the specified context.
     * 
     * @param context the specified context
     */
    @RequestProcessing(value = {"/article"}, method = HTTPRequestMethod.GET)
    public void showArticle(final HTTPRequestContext context) {
        final AbstractFreeMarkerRenderer renderer =
                new FrontFreeMarkerRenderer();
        context.setRenderer(renderer);

        renderer.setTemplateName("article.ftl");
        final HttpServletRequest request = context.getRequest();
        final HttpServletResponse response = context.getResponse();

        String articleId = null;
        try {
            final JSONObject preference = preferenceQueryService.getPreference();
            if (null == preference) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            final Map<String, String> langs =
                    langPropsService.getAll(Latkes.getLocale());

            request.setAttribute(CACHED_TYPE, langs.get(PageTypes.ARTICLE));

            final JSONObject article =
                    (JSONObject) request.getAttribute(Article.ARTICLE);
            if (null == article) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            articleId = article.getString(Keys.OBJECT_ID);
            LOGGER.log(Level.FINER, "Article[id={0}]", articleId);

            final boolean allowVisitDraftViaPermalink = preference.getBoolean(
                    Preference.ALLOW_VISIT_DRAFT_VIA_PERMALINK);
            if (!article.getBoolean(Article.ARTICLE_IS_PUBLISHED)
                && !allowVisitDraftViaPermalink) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);

                return;
            }

            request.setAttribute(CACHED_OID, articleId);
            request.setAttribute(CACHED_TITLE,
                                 article.getString(Article.ARTICLE_TITLE));
            request.setAttribute(CACHED_LINK,
                                 article.getString(Article.ARTICLE_PERMALINK));

            LOGGER.log(Level.FINEST, "Article[title={0}]",
                       article.getString(Article.ARTICLE_TITLE));

            // For <meta name="description" content="${article.articleAbstract}"/>
            final String metaDescription = Jsoup.parse(article.getString(
                    Article.ARTICLE_ABSTRACT)).text();
            article.put(Article.ARTICLE_ABSTRACT, metaDescription);

            if (preference.getBoolean(Preference.ENABLE_ARTICLE_UPDATE_HINT)) {
                article.put(Common.HAS_UPDATED, articleUtils.hasUpdated(article));
            } else {
                article.put(Common.HAS_UPDATED, false);
            }

            final JSONObject author = articleUtils.getAuthor(article);
            final String authorName = author.getString(User.USER_NAME);
            article.put(Common.AUTHOR_NAME, authorName);
            final String authorId = author.getString(Keys.OBJECT_ID);
            article.put(Common.AUTHOR_ID, authorId);
            article.put(Common.AUTHOR_ROLE, author.getString(User.USER_ROLE));

            LOGGER.finer("Getting article sign....");
            article.put(Article.ARTICLE_SIGN_REF,
                        articleUtils.getSign(articleId, preference));
            LOGGER.finer("Got article sign");

            final Map<String, Object> dataModel = renderer.getDataModel();

            prepareShowArticle(preference, dataModel, article);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);

            try {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            } catch (final IOException ex) {
                LOGGER.severe(ex.getMessage());
            }
        }

        if (!Strings.isEmptyOrNull(articleId)) {
            final Repository statisticRepository =
                    StatisticRepositoryImpl.getInstance();
            final Transaction transaction =
                    statisticRepository.beginTransaction();
            transaction.clearQueryCache(false);
            try {
                statistics.incArticleViewCount(articleId);
                transaction.commit();
            } catch (final Exception e) {
                if (transaction.isActive()) {
                    transaction.rollback();
                }

                LOGGER.log(Level.WARNING, "Inc article view count failed", e);
            }
        }
    }

    /**
     * Sorts the specified articles by the specified preference.
     * 
     * @param preference the specified preference
     * @param articles the specified articles
     * @throws JSONException json exception
     * @see Comparators#ARTICLE_UPDATE_DATE_COMPARATOR
     * @see Comparators#ARTICLE_CREATE_DATE_COMPARATOR
     */
    private void sort(final JSONObject preference,
                      final List<JSONObject> articles) throws JSONException {
        if (preference.getBoolean(Preference.ENABLE_ARTICLE_UPDATE_HINT)) {
            Collections.sort(articles,
                             Comparators.ARTICLE_UPDATE_DATE_COMPARATOR);
        } else {
            Collections.sort(articles,
                             Comparators.ARTICLE_CREATE_DATE_COMPARATOR);
        }
    }

    /**
     * Gets archive date from the specified URI.
     * 
     * @param requestURI the specified request URI
     * @return archive date
     */
    private static String getArchiveDate(final String requestURI) {
        final String path = requestURI.substring("/archives/".length());

        return path.substring(0, "yyyy/MM".length());
    }

    /**
     * Gets the request page number from the specified request URI.
     * 
     * @param requestURI the specified request URI
     * @return page number, returns {@code -1} if the specified request URI
     * can not convert to an number
     */
    private static int getArchiveCurrentPageNum(final String requestURI) {
        final String pageNumString = requestURI.substring("/archives/yyyy/MM/".
                length());

        return Requests.getCurrentPageNum(pageNumString);
    }

    /**
     * Gets author id from the specified URI.
     * 
     * @param requestURI the specified request URI
     * @return author id
     */
    private static String getAuthorId(final String requestURI) {
        final String path = requestURI.substring("/authors/".length());

        final int idx = path.indexOf("/");
        if (-1 == idx) {
            return path.substring(0);
        } else {
            return path.substring(0, idx);
        }
    }

    /**
     * Gets the request page number from the specified request URI and author id.
     * 
     * @param requestURI the specified request URI
     * @param authorId the specified author id
     * @return page number
     */
    private static int getAuthorCurrentPageNum(final String requestURI,
                                               final String authorId) {
//        final String pageNumString =
//                requestURI.substring(("/authors/" + authorId + "/").length());
//
//        return Requests.getCurrentPageNum(pageNumString);
        return 1; // TODO: 88250, 041 to fix, upgrades user model by adding two 
        // properties (published article count & article count)
    }

    /**
     * Gets the random articles.
     *
     * @param preference the specified preference
     * @return a list of articles, returns an empty list if not found
     */
    private List<JSONObject> getRandomArticles(final JSONObject preference) {
        try {
            final int displayCnt =
                    preference.getInt(Preference.RANDOM_ARTICLES_DISPLAY_CNT);
            final List<JSONObject> ret =
                    articleQueryService.getArticlesRandomly(displayCnt);

            return ret;
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);

            return Collections.emptyList();
        }
    }

    /**
     * Prepares the specified data model for rendering author articles.
     * 
     * @param pageNums the specified page numbers
     * @param dataModel the specified data model
     * @param pageCount the specified page count
     * @param currentPageNum the specified  current page number 
     * @param articles the specified articles
     * @param author the specified author
     * @param preference the specified preference
     * @throws ServiceException service exception
     */
    private void prepareShowAuthorArticles(final List<Integer> pageNums,
                                           final Map<String, Object> dataModel,
                                           final int pageCount,
                                           final int currentPageNum,
                                           final List<JSONObject> articles,
                                           final JSONObject author,
                                           final JSONObject preference)
            throws ServiceException {
        if (0 != pageNums.size()) {
            dataModel.put(Pagination.PAGINATION_FIRST_PAGE_NUM,
                          pageNums.get(0));
            dataModel.put(Pagination.PAGINATION_LAST_PAGE_NUM,
                          pageNums.get(pageNums.size() - 1));
        }
        dataModel.put(Pagination.PAGINATION_PAGE_COUNT, pageCount);
        dataModel.put(Pagination.PAGINATION_PAGE_NUMS, pageNums);

        dataModel.put(Pagination.PAGINATION_CURRENT_PAGE_NUM, currentPageNum);
        final String previousPageNum =
                Integer.toString(currentPageNum > 1 ? currentPageNum - 1
                                 : 0);
        dataModel.put(Pagination.PAGINATION_PREVIOUS_PAGE_NUM,
                      "0".equals(previousPageNum) ? "" : previousPageNum);
        if (pageCount == currentPageNum + 1) { // The next page is the last page
            dataModel.put(Pagination.PAGINATION_NEXT_PAGE_NUM, "");
        } else {
            dataModel.put(Pagination.PAGINATION_NEXT_PAGE_NUM, currentPageNum
                                                               + 1);
        }

        dataModel.put(Article.ARTICLES, articles);
        final String authorId = author.optString(Keys.OBJECT_ID);
        dataModel.put(Common.PATH, "/authors/" + authorId);
        dataModel.put(Keys.OBJECT_ID, authorId);

        dataModel.put(Common.AUTHOR_NAME, author.optString(User.USER_NAME));
        dataModel.put(Pagination.PAGINATION_CURRENT_PAGE_NUM, currentPageNum);

        filler.fillSide(dataModel, preference);
        filler.fillBlogHeader(dataModel, preference);
        filler.fillBlogFooter(dataModel, preference);
        skins.fillSkinLangs(preference, dataModel);
    }

    /**
     * Prepares the specified data model for rendering archive articles.
     * 
     * @param preference the specified preference
     * @param dataModel the specified data model
     * @param articles the specified articles
     * @param currentPageNum the specified current page number
     * @param pageCount the specified page count
     * @param archiveDateString the specified archive data string
     * @param archiveDate the specified archive date
     * @return page title for caching
     * @throws Exception  exception
     */
    private String prepareShowArchiveArticles(final JSONObject preference,
                                              final Map<String, Object> dataModel,
                                              final List<JSONObject> articles,
                                              final int currentPageNum,
                                              final int pageCount,
                                              final String archiveDateString,
                                              final JSONObject archiveDate)
            throws Exception {
        final int pageSize = preference.getInt(
                Preference.ARTICLE_LIST_DISPLAY_COUNT);
        final int windowSize = preference.getInt(
                Preference.ARTICLE_LIST_PAGINATION_WINDOW_SIZE);

        final List<Integer> pageNums =
                Paginator.paginate(currentPageNum, pageSize, pageCount,
                                   windowSize);

        skins.fillSkinLangs(preference, dataModel);
        dataModel.put(Article.ARTICLES, articles);
        final String previousPageNum =
                Integer.toString(currentPageNum > 1 ? currentPageNum - 1 : 0);
        dataModel.put(Pagination.PAGINATION_PREVIOUS_PAGE_NUM,
                      "0".equals(previousPageNum) ? "" : previousPageNum);
        if (pageCount == currentPageNum + 1) { // The next page is the last page
            dataModel.put(Pagination.PAGINATION_NEXT_PAGE_NUM, "");
        } else {
            dataModel.put(Pagination.PAGINATION_NEXT_PAGE_NUM,
                          currentPageNum + 1);
        }
        dataModel.put(Pagination.PAGINATION_CURRENT_PAGE_NUM, currentPageNum);
        dataModel.put(Pagination.PAGINATION_FIRST_PAGE_NUM, pageNums.get(0));
        dataModel.put(Pagination.PAGINATION_LAST_PAGE_NUM,
                      pageNums.get(pageNums.size() - 1));
        dataModel.put(Pagination.PAGINATION_PAGE_COUNT, pageCount);
        dataModel.put(Pagination.PAGINATION_PAGE_NUMS, pageNums);
        dataModel.put(Common.PATH, "/archives/" + archiveDateString);
        dataModel.put(Keys.OBJECT_ID, archiveDate.getString(Keys.OBJECT_ID));
        filler.fillSide(dataModel, preference);
        filler.fillBlogHeader(dataModel, preference);
        filler.fillBlogFooter(dataModel, preference);
        final long time = archiveDate.getLong(ArchiveDate.ARCHIVE_TIME);
        final String dateString = ArchiveDate.DATE_FORMAT.format(time);
        final String[] dateStrings = dateString.split("/");
        final String year = dateStrings[0];
        final String month = dateStrings[1];
        archiveDate.put(ArchiveDate.ARCHIVE_DATE_YEAR, year);
        final String language = Locales.getLanguage(
                preference.getString(Preference.LOCALE_STRING));
        String ret = null;
        if ("en".equals(language)) {
            archiveDate.put(ArchiveDate.ARCHIVE_DATE_MONTH,
                            Dates.EN_MONTHS.get(month));
            ret = Dates.EN_MONTHS.get(month) + " " + year;
        } else {
            archiveDate.put(ArchiveDate.ARCHIVE_DATE_MONTH, month);
            ret = year + " " + dataModel.get("yearLabel") + " "
                  + month + " " + dataModel.get("monthLabel");
        }
        dataModel.put(ArchiveDate.ARCHIVE_DATE, archiveDate);

        return ret;
    }

    /**
     * Prepares the specified data model for rendering article.
     * 
     * @param preference the specified preference
     * @param dataModel the specified data model
     * @param article the specified article
     * @throws Exception exception
     */
    private void prepareShowArticle(final JSONObject preference,
                                    final Map<String, Object> dataModel,
                                    final JSONObject article)
            throws Exception {
        skins.fillSkinLangs(preference, dataModel);
        dataModel.put(Article.ARTICLE, article);
        final String articleId = article.getString(Keys.OBJECT_ID);

        Stopwatchs.start("Get Next Article");
        LOGGER.finer("Getting the next article....");
        final JSONObject nextArticle =
                articleQueryService.getNextArticle(articleId);
        if (null != nextArticle) {
            dataModel.put(Common.NEXT_ARTICLE_PERMALINK,
                          nextArticle.getString(Article.ARTICLE_PERMALINK));
            dataModel.put(Common.NEXT_ARTICLE_TITLE,
                          nextArticle.getString(Article.ARTICLE_TITLE));
            LOGGER.finer("Got the next article");
        }
        Stopwatchs.end();

        Stopwatchs.start("Get Previous Article");
        LOGGER.finer("Getting the previous article....");
        final JSONObject previousArticle =
                articleQueryService.getPreviousArticle(articleId);
        if (null != previousArticle) {
            dataModel.put(Common.PREVIOUS_ARTICLE_PERMALINK,
                          previousArticle.getString(
                    Article.ARTICLE_PERMALINK));
            dataModel.put(Common.PREVIOUS_ARTICLE_TITLE,
                          previousArticle.getString(Article.ARTICLE_TITLE));
            LOGGER.finer("Got the previous article");
        }
        Stopwatchs.end();

        Stopwatchs.start("Get Article CMTs");
        LOGGER.finer("Getting article's comments....");
        final List<JSONObject> articleComments =
                commentQueryService.getComments(articleId);
        dataModel.put(Article.ARTICLE_COMMENTS_REF, articleComments);
        LOGGER.finer("Got article's comments");
        Stopwatchs.end();

        dataModel.put(Preference.EXTERNAL_RELEVANT_ARTICLES_DISPLAY_CNT,
                      preference.getInt(
                Preference.EXTERNAL_RELEVANT_ARTICLES_DISPLAY_CNT));
        dataModel.put(Preference.RANDOM_ARTICLES_DISPLAY_CNT,
                      preference.getInt(Preference.RANDOM_ARTICLES_DISPLAY_CNT));
        dataModel.put(Preference.RELEVANT_ARTICLES_DISPLAY_CNT,
                      preference.getInt(Preference.RELEVANT_ARTICLES_DISPLAY_CNT));

        filler.fillSide(dataModel, preference);
        filler.fillBlogHeader(dataModel, preference);
        filler.fillBlogFooter(dataModel, preference);
    }
}
