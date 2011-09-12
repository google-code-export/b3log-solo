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

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.b3log.latke.mail.MailService;
import org.b3log.latke.mail.MailServiceFactory;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletResponse;
import org.b3log.latke.Keys;
import org.b3log.latke.action.util.PageCaches;
import org.b3log.latke.annotation.RequestProcessing;
import org.b3log.latke.annotation.RequestProcessor;
import org.b3log.latke.mail.MailService.Message;
import org.b3log.latke.repository.Query;
import org.b3log.latke.repository.Repository;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.HTTPRequestMethod;
import org.b3log.latke.servlet.renderer.TextHTMLRenderer;
import org.b3log.latke.util.CollectionUtils;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.Preference;
import org.b3log.solo.model.Tag;
import org.b3log.solo.repository.ArticleRepository;
import org.b3log.solo.repository.TagArticleRepository;
import org.b3log.solo.repository.TagRepository;
import org.b3log.solo.repository.impl.ArchiveDateArticleGAERepository;
import org.b3log.solo.repository.impl.ArchiveDateGAERepository;
import org.b3log.solo.repository.impl.ArticleGAERepository;
import org.b3log.solo.repository.impl.ArticleSignGAERepository;
import org.b3log.solo.repository.impl.BlogSyncMgmtGAERepository;
import org.b3log.solo.repository.impl.CommentGAERepository;
import org.b3log.solo.repository.impl.ExternalArticleSoloArticleGAERepository;
import org.b3log.solo.repository.impl.FileGAERepository;
import org.b3log.solo.repository.impl.LinkGAERepository;
import org.b3log.solo.repository.impl.PageGAERepository;
import org.b3log.solo.repository.impl.PreferenceGAERepository;
import org.b3log.solo.repository.impl.SkinGAERepository;
import org.b3log.solo.repository.impl.StatisticGAERepository;
import org.b3log.solo.repository.impl.TagArticleGAERepository;
import org.b3log.solo.repository.impl.TagGAERepository;
import org.b3log.solo.repository.impl.UserGAERepository;
import org.b3log.solo.util.Preferences;
import org.b3log.solo.util.Skins;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Provides patches on some special issues.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.1.0.0, Sep 12, 2011
 * @since 0.3.1
 */
@RequestProcessor
public final class RepairProcessor {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(RepairProcessor.class.getName());
    /**
     * Preference utilities.
     */
    private Preferences preferenceUtils = Preferences.getInstance();
    /**
     * Skin utilities.
     */
    private Skins skins = Skins.getInstance();
    /**
     * Mail service.
     */
    private static final MailService MAIL_SVC =
            MailServiceFactory.getMailService();
    /**
     * Tag repository.
     */
    private TagRepository tagRepository = TagGAERepository.getInstance();
    /**
     * Tag-Article repository.
     */
    private TagArticleRepository tagArticleRepository =
            TagArticleGAERepository.getInstance();
    /**
     * Article repository.
     */
    private ArticleRepository articleRepository =
            ArticleGAERepository.getInstance();

    /**
     * Restores the signs of preference to default.
     * 
     * @param context the specified context
     */
    @RequestProcessing(value = {"/fix/restore-signs.do"},
                       method = HTTPRequestMethod.GET)
    public void restoreSigns(final HTTPRequestContext context) {
        final TextHTMLRenderer renderer =
                new TextHTMLRenderer();
        context.setRenderer(renderer);

        final Repository repository = PreferenceGAERepository.getInstance();
        final Transaction transaction = repository.beginTransaction();

        try {
            final JSONObject preference = preferenceUtils.getPreference();
            final String originalSigns =
                    preference.getString(Preference.SIGNS);
            preference.put(Preference.SIGNS, Preference.Default.DEFAULT_SIGNS);

            preferenceUtils.setPreference(preference);

            transaction.commit();

            // Sends the sample signs to developer
            final Message msg = new MailService.Message();
            msg.setFrom(preference.getString(Preference.ADMIN_EMAIL));
            msg.addRecipient("DL88250@gmail.com");
            msg.setSubject("Restore signs");
            msg.setHtmlBody(originalSigns + "<p>Admin email: "
                            + preference.getString(Preference.ADMIN_EMAIL)
                            + "</p>");

            MAIL_SVC.send(msg);
            renderer.setContent("Restores signs succeeded.");
        } catch (final Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }

            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            renderer.setContent("Restores signs failed, error msg["
                                + e.getMessage() + "]");
        }
    }

    /**
     * Repairs tag article counter.
     * 
     * @param context the specified context
     */
    @RequestProcessing(value = {"/fix/tag-article-counter-repair.do"},
                       method = HTTPRequestMethod.GET)
    public void repairTagArticleCounter(final HTTPRequestContext context) {
        final TextHTMLRenderer renderer = new TextHTMLRenderer();
        context.setRenderer(renderer);

        final Transaction transaction = tagRepository.beginTransaction();
        try {
            final JSONObject result = tagRepository.get(new Query());
            final JSONArray tagArray = result.getJSONArray(Keys.RESULTS);
            final List<JSONObject> tags =
                    CollectionUtils.jsonArrayToList(tagArray);
            for (final JSONObject tag : tags) {
                final String tagId = tag.getString(Keys.OBJECT_ID);
                final JSONObject tagArticleResult =
                        tagArticleRepository.getByTagId(tagId, 1,
                                                        Integer.MAX_VALUE);
                final JSONArray tagArticles =
                        tagArticleResult.getJSONArray(Keys.RESULTS);
                final int tagRefCnt = tagArticles.length();
                int publishedTagRefCnt = 0;
                for (int i = 0; i < tagRefCnt; i++) {
                    final JSONObject tagArticle = tagArticles.getJSONObject(i);
                    final String articleId = tagArticle.getString(
                            Article.ARTICLE + "_" + Keys.OBJECT_ID);
                    final JSONObject article = articleRepository.get(articleId);
                    final boolean isPublished =
                            article.getBoolean(Article.ARTICLE_IS_PUBLISHED);
                    if (isPublished) {
                        publishedTagRefCnt++;
                    }
                }

                tag.put(Tag.TAG_REFERENCE_COUNT, tagRefCnt);
                tag.put(Tag.TAG_PUBLISHED_REFERENCE_COUNT, publishedTagRefCnt);

                tagRepository.update(tagId, tag);

                LOGGER.log(Level.INFO,
                           "Repaired tag[title={0}, refCnt={1}, publishedTagRefCnt={2}]",
                           new Object[]{tag.getString(Tag.TAG_TITLE),
                                        tagRefCnt, publishedTagRefCnt});
            }

            transaction.commit();

            renderer.setContent("Repair sucessfully!");
        } catch (final Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }

            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            renderer.setContent("Repairs failed, error msg[" + e.getMessage()
                                + "]");
        }
    }

    /**
     * Shows remove all data page.
     * 
     * @param context the specified context
     */
    @RequestProcessing(value = {"/rm-all-data.do"},
                       method = HTTPRequestMethod.GET)
    public void removeAllDataGET(final HTTPRequestContext context) {
        final TextHTMLRenderer renderer = new TextHTMLRenderer();
        context.setRenderer(renderer);

        try {
            final StringBuilder htmlBuilder = new StringBuilder();
            htmlBuilder.append("<html><head><title>WARNING!</title>");
            htmlBuilder.append("<script type='text/javascript'");
            htmlBuilder.append(
                    "src='http://ajax.googleapis.com/ajax/libs/jquery/1.4.3/jquery.min.js'");
            htmlBuilder.append("></script></head><body>");
            htmlBuilder.append("<button id='ok' onclick='remove()'>");
            htmlBuilder.append("Continue to delete ALL DATA</button></body>");
            htmlBuilder.append("<script type='text/javascript'>");
            htmlBuilder.append("function remove() {");
            htmlBuilder.append("$.ajax({type: 'POST',url: '/rm-all-data.do',");
            htmlBuilder.append(
                    "dataType: 'text/html',success: function(result){");
            htmlBuilder.append("$('html').html(result);}});}</script></html>");

            renderer.setContent(htmlBuilder.toString());
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);

            try {
                context.getResponse().sendError(
                        HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            } catch (final IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    /**
     * Removes all data.
     * 
     * @param context the specified context
     */
    @RequestProcessing(value = {"/rm-all-data.do"},
                       method = HTTPRequestMethod.POST)
    public void removeAllDataPOST(final HTTPRequestContext context) {
        LOGGER.info("Removing all data....");

        PageCaches.removeAll();

        boolean succeed = false;
        try {
            remove(ArchiveDateArticleGAERepository.getInstance());

            remove(ArchiveDateGAERepository.getInstance());

            remove(ArticleGAERepository.getInstance());

            remove(ArticleSignGAERepository.getInstance());

            remove(BlogSyncMgmtGAERepository.getInstance());

            remove(CommentGAERepository.getInstance());

            remove(ExternalArticleSoloArticleGAERepository.getInstance());

            remove(FileGAERepository.getInstance());

            remove(LinkGAERepository.getInstance());

            remove(PageGAERepository.getInstance());

            remove(PreferenceGAERepository.getInstance());

            remove(SkinGAERepository.getInstance());

            remove(StatisticGAERepository.getInstance());

            remove(TagArticleGAERepository.getInstance());

            remove(TagGAERepository.getInstance());

            remove(UserGAERepository.getInstance());

            succeed = true;
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            LOGGER.log(Level.WARNING, "Removed partial data only");
        }

        final StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append("<html><head><title>Result</title></head><body>");

        try {
            final TextHTMLRenderer renderer = new TextHTMLRenderer();
            context.setRenderer(renderer);
            if (succeed) {
                htmlBuilder.append("Removed all data!");
            } else {
                htmlBuilder.append(
                        "Refresh this page and run this remover again.");
            }
            htmlBuilder.append("</body></html>");

            renderer.setContent(htmlBuilder.toString());
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            try {
                context.getResponse().sendError(
                        HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            } catch (final IOException ex) {
                throw new RuntimeException(ex);
            }
        }

        LOGGER.info("Removed all data....");
    }

    /**
     * Removes data in the specified repository.
     *
     * @param repository the specified repository
     * @throws ExecutionException execution exception
     * @throws InterruptedException interrupted exception
     */
    private void remove(final Repository repository)
            throws ExecutionException, InterruptedException {
        final long startTime = System.currentTimeMillis();
        final long step = 20000;

        final Transaction transaction = repository.beginTransaction();

        try {
            final JSONObject result = repository.get(new Query());
            final JSONArray array = result.getJSONArray(Keys.RESULTS);
            for (int i = 0; i < array.length(); i++) {
                final JSONObject object = array.getJSONObject(i);
                repository.remove(object.getString(Keys.OBJECT_ID));

                if (System.currentTimeMillis() >= startTime + step) {
                    break;
                }
            }

            transaction.commit();
        } catch (final Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
        }
    }
}
