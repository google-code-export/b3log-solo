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

import java.util.List;
import org.b3log.latke.mail.MailService;
import org.b3log.latke.mail.MailServiceFactory;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.latke.Keys;
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
import org.b3log.solo.repository.impl.ArticleGAERepository;
import org.b3log.solo.repository.impl.PreferenceGAERepository;
import org.b3log.solo.repository.impl.TagArticleGAERepository;
import org.b3log.solo.repository.impl.TagGAERepository;
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
            renderer.setContnet("Restores signs succeeded.");
        } catch (final Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }

            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            renderer.setContnet("Restores signs failed, error msg["
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

            renderer.setContnet("Repair sucessfully!");
        } catch (final Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }

            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            renderer.setContnet("Repairs failed, error msg[" + e.getMessage()
                                + "]");
        }
    }
}
