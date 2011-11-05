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
package org.b3log.solo.metaweblog;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.b3log.latke.Keys;
import org.b3log.latke.annotation.RequestProcessing;
import org.b3log.latke.annotation.RequestProcessor;
import org.b3log.latke.model.User;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.service.ServiceException;
import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.HTTPRequestMethod;
import org.b3log.latke.servlet.renderer.TextXMLRenderer;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.Preference;
import org.b3log.solo.model.Tag;
import org.b3log.solo.repository.ArticleRepository;
import org.b3log.solo.repository.impl.ArticleRepositoryImpl;
import org.b3log.solo.service.ArticleMgmtService;
import org.b3log.solo.service.ArticleQueryService;
import org.b3log.solo.service.PreferenceQueryService;
import org.b3log.solo.service.TagQueryService;
import org.b3log.solo.service.UserQueryService;
import org.b3log.solo.util.Users;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

/**
 * <a href="http://www.xmlrpc.com/metaWeblogApi">MetaWeblog API</a> 
 * requests processing.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Nov 5, 2011
 * @since 0.4.0
 */
@RequestProcessor
public final class MetaWeblogAPI {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(MetaWeblogAPI.class.getName());
    /**
     * User utilities.
     */
    private Users userUtils = Users.getInstance();
    /**
     * Preference query service.
     */
    private PreferenceQueryService preferenceQueryService =
            PreferenceQueryService.getInstance();
    /**
     * Tag query service.
     */
    private TagQueryService tagQueryService = TagQueryService.getInstance();
    /**
     * Article query service.
     */
    private ArticleQueryService articleQueryService =
            ArticleQueryService.getInstance();
    /**
     * Article management service.
     */
    private ArticleMgmtService articleMgmtService =
            ArticleMgmtService.getInstance();
    /**
     * Article repository.
     */
    private ArticleRepository articleRepository =
            ArticleRepositoryImpl.getInstance();
    /**
     * User query service.
     */
    private UserQueryService userQueryService = UserQueryService.getInstance();
    /**
     * Key of method call.
     */
    private static final String METHOD_CALL = "methodCall";
    /**
     * Key of method name.
     */
    private static final String METHOD_NAME = "methodName";
    /**
     * Method name: "blogger.getUsersBlogs".
     */
    private static final String METHOD_GET_USERS_BLOGS = "blogger.getUsersBlogs";
    /**
     * Method name: "metaWeblog.getCategories".
     */
    private static final String METHOD_GET_CATEGORIES =
            "metaWeblog.getCategories";
    /**
     * Method name: "metaWeblog.getRecentPosts".
     */
    private static final String METHOD_GET_RECENT_POSTS =
            "metaWeblog.getRecentPosts";
    /**
     * Method name: "metaWeblog.newPost".
     */
    private static final String MEHTOD_NEW_POST = "metaWeblog.newPost";
    /**
     * Argument "username" index.
     */
    private static final int INDEX_USER_EMAIL = 1;
    /**
     * Argument "password" index.
     */
    private static final int INDEX_USER_PWD = 2;
    /**
     * Argument "post" index.
     */
    private static final int INDEX_POST = 3;
    /**
     * Argument "publish" index.
     */
    private static final int INDEX_PUBLISH = 4;
    /**
     * Article abstract length.
     */
    private static final int ARTICLE_ABSTRACT_LENGTH = 500;

    /**
     * MetaWeblog requests processing.
     * 
     * <p>
     * Renders the response with a json object, for example,
     * <pre>
     * {
     *     "sc": boolean,
     *     "msg": ""
     * }
     * </pre>
     * // TODO: 88250, metaweblog response render
     * </p>
     *
     * @param request the specified http servlet request
     * @param response the specified http servlet response
     * @param context the specified http request context
     * @throws Exception exception
     */
    @RequestProcessing(value = "/apis/metaweblog",
                       method = HTTPRequestMethod.POST)
    public void metaWeblog(final HttpServletRequest request,
                           final HttpServletResponse response,
                           final HTTPRequestContext context)
            throws Exception {
        final TextXMLRenderer renderer = new TextXMLRenderer();
        context.setRenderer(renderer);

        try {
            final ServletInputStream inputStream = request.getInputStream();
            final String xml = IOUtils.toString(inputStream);
            final JSONObject requestJSONObject = XML.toJSONObject(xml);

            final JSONObject methodCall =
                    requestJSONObject.getJSONObject(METHOD_CALL);
            final String methodName = methodCall.getString(METHOD_NAME);

            final JSONArray params = methodCall.getJSONObject("params").
                    getJSONArray("param");
            final String userEmail = params.getJSONObject(INDEX_USER_EMAIL).
                    getJSONObject(
                    "value").getString("string");
            final JSONObject user = userQueryService.getUserByEmail(userEmail);
            if (null == user) {
                throw new Exception("No user[email=" + userEmail + "]");
            }

            final String userPwd = params.getJSONObject(INDEX_USER_PWD).
                    getJSONObject("value").getString("string");
            if (!user.getString(User.USER_PASSWORD).equals(userPwd)) {
                throw new Exception("Wrong password");
            }

            String responseContent = null;
            if (METHOD_GET_USERS_BLOGS.equals(methodName)) {
                responseContent = getUsersBlogs();
            } else if (METHOD_GET_CATEGORIES.equals(methodName)) {
                responseContent = getCategories();
            } else if (METHOD_GET_RECENT_POSTS.equals(methodName)) {
                responseContent = getRecentPosts(1); // TODO: metaWeblog.getRecentPosts
            } else if (MEHTOD_NEW_POST.equals(methodName)) {
                final JSONObject article = getArticle(methodCall);

                article.put(Article.ARTICLE_AUTHOR_EMAIL, userEmail);

                addArticle(article);
            }

            renderer.setContent(responseContent);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /**
     * Adds the specified article.
     * 
     * @param article the specified article
     * @throws Exception exception
     */
    private void addArticle(final JSONObject article) throws Exception {
        final Transaction transaction = articleRepository.beginTransaction();

        try {
            articleMgmtService.addArticleInternal(article);
            transaction.commit();
        } catch (final ServiceException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }

            throw e;
        }
    }

    /**
     * Gets an article with the specified method call.
     * 
     * @param methodCall the specified method call
     * @return article
     * @throws Exception exception 
     */
    private JSONObject getArticle(final JSONObject methodCall) throws Exception {
        final JSONObject ret = new JSONObject();

        final JSONArray params = methodCall.getJSONObject("params").
                getJSONArray("param");
        final JSONObject post = params.getJSONObject(INDEX_POST).getJSONObject(
                "value").getJSONObject("struct");
        final JSONArray members = post.getJSONArray("member");

        for (int i = 0; i < members.length(); i++) {
            final JSONObject member = members.getJSONObject(i);
            final String name = member.getString("name");

            if ("title".equals(name)) {
                ret.put(Article.ARTICLE_TITLE, member.getJSONObject("value").
                        getString("string"));
            } else if ("description".equals(name)) {
                final String content =
                        member.getJSONObject("value").getString("string");
                ret.put(Article.ARTICLE_CONTENT, content);
                if (content.length() > ARTICLE_ABSTRACT_LENGTH) {
                    ret.put(Article.ARTICLE_ABSTRACT,
                            content.substring(0, ARTICLE_ABSTRACT_LENGTH));
                } else {
                    ret.put(Article.ARTICLE_ABSTRACT, content);
                }
            } else if ("categories".equals(name)) {
                final StringBuilder tagBuilder = new StringBuilder();

                final Object value =
                        member.getJSONObject("value").
                        getJSONObject("array").getJSONObject("data").get("value");
                if (value instanceof JSONArray) {
                    final JSONArray tags = (JSONArray) value;
                    for (int j = 0; j < tags.length(); j++) {
                        final String tagTitle = tags.getJSONObject(j).getString(
                                "string");
                        tagBuilder.append(tagTitle);

                        if (j < tags.length() - 1) {
                            tagBuilder.append(",");
                        }
                    }
                } else {
                    final JSONObject tag = (JSONObject) value;
                    tagBuilder.append(tag.getString("string"));
                }

                ret.put(Article.ARTICLE_TAGS_REF, tagBuilder.toString());
            }
        }

        final boolean publish = 1 == params.getJSONObject(INDEX_PUBLISH).
                getJSONObject("value").getInt("boolean") ? true : false;
        ret.put(Article.ARTICLE_IS_PUBLISHED, publish);

        return ret;
    }

    /**
     * Processes {@value #METHOD_GET_RECENT_POSTS}.
     * 
     * @param fetchSize the specified fetch size
     * @return method response XML
     * @throws Exception exception
     */
    private String getRecentPosts(final int fetchSize) throws Exception {
        final StringBuilder stringBuilder =
                new StringBuilder(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?><methodResponse><params><param><value><array><data>");

        final String posts = buildRecentPosts(fetchSize);

        stringBuilder.append(posts);

        stringBuilder.append(
                "</data></array></value></param></params></methodResponse>");

        return stringBuilder.toString();
    }

    /**
     * Processes {@value #METHOD_GET_CATEGORIES}.
     * 
     * @return method response XML
     * @throws Exception exception
     */
    private String getCategories() throws Exception {
        final StringBuilder stringBuilder =
                new StringBuilder(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?><methodResponse><params><param><value><array><data>");

        final JSONObject preference =
                preferenceQueryService.getPreference();
        final String categories = buildCategories(preference);

        stringBuilder.append(categories);

        stringBuilder.append(
                "</data></array></value></param></params></methodResponse>");

        return stringBuilder.toString();
    }

    /**
     * Processes {@value #METHOD_GET_USERS_BLOGS}.
     * 
     * @return method response XML
     * @throws Exception exception
     */
    private String getUsersBlogs() throws Exception {
        final StringBuilder stringBuilder =
                new StringBuilder(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?><methodResponse><params><param><value><array><data><value><struct>");

        final JSONObject preference =
                preferenceQueryService.getPreference();
        final String blogInfo = buildBlogInfo(preference);

        stringBuilder.append(blogInfo);

        stringBuilder.append(
                "</struct></value></data></array></value></param></params></methodResponse>");

        return stringBuilder.toString();
    }

    /**
     * Builds recent posts (array of post info structs) with the specified 
     * fetch size.
     * 
     * @param fetchSize the specified fetch size
     * @return blog info XML
     * @throws Exception exception 
     */
    private String buildRecentPosts(final int fetchSize)
            throws Exception {

        final StringBuilder stringBuilder = new StringBuilder();

        final List<JSONObject> recentArticles =
                articleQueryService.getRecentArticles(fetchSize);

        for (final JSONObject article : recentArticles) {
            final Date createDate =
                    (Date) article.get(Article.ARTICLE_CREATE_DATE);
            final String articleTitle =
                    StringEscapeUtils.escapeXml(
                    article.getString(Article.ARTICLE_TITLE));

            stringBuilder.append("<value><struct>");

            stringBuilder.append("<member><name>dateCreated</name>").
                    append("<value><dateTime.iso8601>").append(articleTitle).
                    append(DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.format(
                    createDate)).append("</dateTime.iso8601></value></member>");

            stringBuilder.append("<member><name>description</name>").
                    append("<value>").append(StringEscapeUtils.escapeXml(
                    article.getString(Article.ARTICLE_CONTENT))).
                    append("</value></member>");

            stringBuilder.append("<member><name>title</name>").
                    append("<value>").append(articleTitle).
                    append("</value></member>");

            stringBuilder.append("</struct></value>");
        }

        return stringBuilder.toString();
    }

    /**
     * Builds categories (array of category info structs) with the specified 
     * preference.
     * 
     * @param preference the specified preference
     * @return blog info XML
     * @throws Exception exception 
     */
    private String buildCategories(final JSONObject preference)
            throws Exception {
        final String blogHost = "http://" + preference.getString(
                Preference.BLOG_HOST);

        final StringBuilder stringBuilder = new StringBuilder();

        final List<JSONObject> tags = tagQueryService.getTags();
        for (final JSONObject tag : tags) {
            final String tagTitle = StringEscapeUtils.escapeXml(
                    tag.getString(Tag.TAG_TITLE));
            final String tagId = tag.getString(Keys.OBJECT_ID);

            stringBuilder.append("<value><struct>");

            stringBuilder.append("<member><name>description</name>").
                    append("<value>").append(tagTitle).append(
                    "</value></member>");

            stringBuilder.append("<member><name>title</name>").
                    append("<value>").append(tagTitle).append(
                    "</value></member>");

            stringBuilder.append("<member><name>categoryid</name>").
                    append("<value>").append(tagId).append("</value></member>");

            stringBuilder.append("<member><name>htmlUrl</name>").
                    append("<value>").append(blogHost).
                    append("/tags/").append(tagTitle).append("</value></member>");

            stringBuilder.append("<member><name>rsslUrl</name>").
                    append("<value>").append(blogHost).
                    append("/tag-articles-rss.do?oId=").append(tagId).
                    append("</value></member>");

            stringBuilder.append("</struct></value>");
        }

        return stringBuilder.toString();
    }

    /**
     * Builds blog info struct with the specified preference.
     * 
     * @param preference the specified preference
     * @return blog info XML
     * @throws JSONException json exception 
     */
    private String buildBlogInfo(final JSONObject preference)
            throws JSONException {
        final String blogId = preference.getString(Keys.OBJECT_ID);

        final String blogTitle = StringEscapeUtils.escapeXml(
                preference.getString(Preference.BLOG_TITLE));

        final String blogURL = "http://" + preference.getString(
                Preference.BLOG_HOST);

        final StringBuilder stringBuilder = new StringBuilder(
                "<member><name>blogid</name><value>").append(blogId).append(
                "</value></member>");
        stringBuilder.append("<member><name>url</name><value>").
                append(blogURL).append("</value></member>");
        stringBuilder.append("<member><name>blogName</name><value>").
                append(blogTitle).append("</value></member>");

        return stringBuilder.toString();
    }
}
