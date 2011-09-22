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
import java.net.URLEncoder;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.time.DateFormatUtils;
import org.b3log.latke.Keys;
import org.b3log.latke.annotation.RequestProcessing;
import org.b3log.latke.annotation.RequestProcessor;
import org.b3log.latke.repository.FilterOperator;
import org.b3log.latke.repository.Query;
import org.b3log.latke.repository.SortDirection;
import org.b3log.latke.servlet.HTTPRequestContext;
import org.b3log.latke.servlet.HTTPRequestMethod;
import org.b3log.latke.servlet.renderer.TextXMLRenderer;
import org.b3log.solo.model.ArchiveDate;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.Page;
import org.b3log.solo.model.Preference;
import org.b3log.solo.model.Tag;
import org.b3log.solo.model.sitemap.Sitemap;
import org.b3log.solo.model.sitemap.URL;
import org.b3log.solo.repository.ArchiveDateRepository;
import org.b3log.solo.repository.PageRepository;
import org.b3log.solo.repository.TagRepository;
import org.b3log.solo.repository.impl.ArchiveDateGAERepository;
import org.b3log.solo.repository.impl.ArticleGAERepository;
import org.b3log.solo.repository.impl.PageGAERepository;
import org.b3log.solo.repository.impl.TagGAERepository;
import org.b3log.solo.util.Preferences;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Site map (sitemap) processor.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Sep 22, 2011
 * @since 0.3.1
 */
@RequestProcessor
public final class SitemapProcessor {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(SitemapProcessor.class.getName());
    /**
     * Preference utility.
     */
    private Preferences preferences = Preferences.getInstance();
    /**
     * Article repository.
     */
    private ArticleGAERepository articleRepository =
            ArticleGAERepository.getInstance();
    /**
     * Page repository.
     */
    private PageRepository pageRepository = PageGAERepository.getInstance();
    /**
     * Tag repository.
     */
    private TagRepository tagRepository =
            TagGAERepository.getInstance();
    /**
     * Archive date repository.
     */
    private ArchiveDateRepository archiveDateRepository =
            ArchiveDateGAERepository.getInstance();

    /**
     * Returns the sitemap.
     * 
     * @param context the specified context
     */
    @RequestProcessing(value = {"/sitemap"}, method = HTTPRequestMethod.GET)
    public void sitemap(final HTTPRequestContext context) {
        final TextXMLRenderer renderer = new TextXMLRenderer();
        context.setRenderer(renderer);

        final Sitemap sitemap = new Sitemap();

        try {
            final JSONObject preference = preferences.getPreference();

            addArticles(sitemap, preference);
            addPages(sitemap, preference);
            addTags(sitemap, preference);
            addArchives(sitemap, preference);

            LOGGER.log(Level.INFO, "Generating sitemap....");
            final String content = sitemap.toString();
            LOGGER.log(Level.INFO, "Generated sitemap");
            renderer.setContent(content);
        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, "Get blog article feed error", e);

            try {
                context.getResponse().sendError(
                        HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            } catch (final IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    /**
     * Adds articles into the specified sitemap.
     * 
     * @param sitemap the specified sitemap
     * @param preference the specified preference
     * @throws Exception exception
     */
    private void addArticles(final Sitemap sitemap,
                             final JSONObject preference) throws Exception {
        final String host = preference.getString(Preference.BLOG_HOST);

        // XXX: query all articles?
        final Query query = new Query().setCurrentPageNum(1).
                addFilter(Article.ARTICLE_IS_PUBLISHED,
                          FilterOperator.EQUAL, true).
                addSort(Article.ARTICLE_CREATE_DATE, SortDirection.DESCENDING);

        // Closes cache avoid Java heap space out of memory while caching 
        // query results
        articleRepository.setCacheEnabled(false); 
        
        final JSONObject articleResult = articleRepository.get(query);
        
        articleRepository.setCacheEnabled(true); // Restores cache
        
        final JSONArray articles = articleResult.getJSONArray(Keys.RESULTS);
        for (int i = 0; i < articles.length(); i++) {
            final JSONObject article = articles.getJSONObject(i);
            final String permalink =
                    article.getString(Article.ARTICLE_PERMALINK);

            final URL url = new URL();
            url.setLoc("http://" + host + permalink);

            final Date updateDate =
                    (Date) article.get(Article.ARTICLE_UPDATE_DATE);
            final String lastMod =
                    DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.format(
                    updateDate);
            url.setLastMod(lastMod);

            sitemap.addURL(url);
        }
    }

    /**
     * Adds pages into the specified sitemap.
     * 
     * @param sitemap the specified sitemap
     * @param preference the specified preference
     * @throws Exception exception 
     */
    private void addPages(final Sitemap sitemap,
                          final JSONObject preference) throws Exception {
        final String host = preference.getString(Preference.BLOG_HOST);

        final JSONObject result = pageRepository.get(new Query());
        final JSONArray pages = result.getJSONArray(Keys.RESULTS);
        for (int i = 0; i < pages.length(); i++) {
            final JSONObject page = pages.getJSONObject(i);
            final String permalink = page.getString(Page.PAGE_PERMALINK);

            final URL url = new URL();
            url.setLoc("http://" + host + permalink);

            sitemap.addURL(url);
        }
    }

    /**
     * Adds tags (tag-articles) and tags wall (/tags.html) into the specified 
     * sitemap.
     * 
     * @param sitemap the specified sitemap
     * @param preference the specified preference
     * @throws Exception exception
     */
    private void addTags(final Sitemap sitemap,
                         final JSONObject preference) throws Exception {
        final String host = preference.getString(Preference.BLOG_HOST);

        final JSONObject result = tagRepository.get(new Query());
        final JSONArray tags = result.getJSONArray(Keys.RESULTS);
        for (int i = 0; i < tags.length(); i++) {
            final JSONObject tag = tags.getJSONObject(i);
            final String link = URLEncoder.encode(tag.getString(Tag.TAG_TITLE),
                                                  "UTF-8");

            final URL url = new URL();
            url.setLoc("http://" + host + "/tags/" + link);

            sitemap.addURL(url);
        }

        // Tags wall
        final URL url = new URL();
        url.setLoc("http://" + host + "/tags.html");
        sitemap.addURL(url);
    }

    /**
     * Adds archives (archive-articles) into the specified sitemap.
     * 
     * @param sitemap the specified sitemap
     * @param preference the specified preference
     * @throws Exception exception
     */
    private void addArchives(final Sitemap sitemap,
                             final JSONObject preference) throws Exception {
        final String host = preference.getString(Preference.BLOG_HOST);

        final JSONObject result = archiveDateRepository.get(new Query());
        final JSONArray archiveDates = result.getJSONArray(Keys.RESULTS);
        for (int i = 0; i < archiveDates.length(); i++) {
            final JSONObject archiveDate = archiveDates.getJSONObject(i);
            final long time =
                    archiveDate.getLong(ArchiveDate.ARCHIVE_TIME);
            final String dateString = ArchiveDate.DATE_FORMAT.format(time);

            final URL url = new URL();
            url.setLoc("http://" + host + "/archives/" + dateString);

            sitemap.addURL(url);
        }
    }
}
