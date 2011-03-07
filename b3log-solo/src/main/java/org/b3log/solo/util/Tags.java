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

package org.b3log.solo.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.b3log.solo.model.Article;
import org.b3log.solo.model.Tag;
import org.b3log.solo.repository.TagRepository;
import org.b3log.latke.Keys;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.solo.repository.ArticleRepository;
import org.b3log.solo.repository.impl.ArticleGAERepository;
import org.b3log.solo.repository.impl.TagGAERepository;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Tag utilities.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.6, Mar 8, 2011
 */
public final class Tags {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(Tags.class.getName());
    /**
     * Article repository.
     */
    private ArticleRepository articleRepository =
            ArticleGAERepository.getInstance();
    /**
     * Tag repository.
     */
    private TagRepository tagRepository =
            TagGAERepository.getInstance();
    /**
     * Article utilities.
     */
    private Articles articleUtils = Articles.getInstance();

    /**
     * Tags the specified article with the specified tag titles.
     *
     * @param tagTitles the specified tag titles
     * @param article the specified article
     * @return an array of tags
     * @throws RepositoryException repository exception
     * @throws JSONException json exception
     */
    public JSONArray tag(final String[] tagTitles,
                         final JSONObject article)
            throws RepositoryException, JSONException {
        final JSONArray ret = new JSONArray();
        for (int i = 0; i < tagTitles.length; i++) {
            final String tagTitle = tagTitles[i].trim();
            JSONObject tag = tagRepository.getByTitle(tagTitle);
            String tagId = null;
            if (null == tag) {
                LOGGER.log(Level.FINEST,
                           "Found a new tag[title={0}] in article[title={1}]",
                           new Object[]{
                            tagTitle, article.getString(Article.ARTICLE_TITLE)});
                tag = new JSONObject();
                tag.put(Tag.TAG_TITLE, tagTitle);
                tag.put(Tag.TAG_REFERENCE_COUNT, 1);
                if (article.getBoolean(
                        Article.ARTICLE_IS_PUBLISHED)) { // Publish article directly
                    tag.put(Tag.TAG_PUBLISHED_REFERENCE_COUNT, 1);
                } else { // Save as draft
                    tag.put(Tag.TAG_PUBLISHED_REFERENCE_COUNT, 0);
                }

                tagId = tagRepository.add(tag);
                tag.put(Keys.OBJECT_ID, tagId);
            } else {
                tagId = tag.getString(Keys.OBJECT_ID);
                LOGGER.log(Level.FINEST,
                           "Found a existing tag[title={0}, oId={1}] in article[title={2}]",
                           new Object[]{tag.getString(Tag.TAG_TITLE),
                                        tag.getString(Keys.OBJECT_ID),
                                        article.getString(Article.ARTICLE_TITLE)});
                final JSONObject tagTmp = new JSONObject();
                tagTmp.put(Keys.OBJECT_ID, tagId);
                tagTmp.put(Tag.TAG_TITLE, tagTitle);
                final int refCnt = tag.getInt(Tag.TAG_REFERENCE_COUNT);
                final int publishedRefCnt =
                        tag.getInt(Tag.TAG_PUBLISHED_REFERENCE_COUNT);
                tagTmp.put(Tag.TAG_REFERENCE_COUNT, refCnt + 1);
                if (article.getBoolean(Article.ARTICLE_IS_PUBLISHED)) {
                    tagTmp.put(Tag.TAG_PUBLISHED_REFERENCE_COUNT,
                               publishedRefCnt + 1);
                } else {
                    tagTmp.put(Tag.TAG_PUBLISHED_REFERENCE_COUNT,
                               publishedRefCnt);
                }
                tagRepository.update(tagId, tagTmp);
            }

            ret.put(tag);
        }

        return ret;
    }

    /**
     * Decrements reference count of every tag of an article specified by the
     * given article id.
     *
     * @param articleId the given article id
     * @throws JSONException json exception
     * @throws RepositoryException repository exception
     */
    public void decTagRefCount(final String articleId)
            throws JSONException, RepositoryException {
        final List<JSONObject> tags = tagRepository.getByArticleId(articleId);
        final JSONObject article = articleRepository.get(articleId);

        for (final JSONObject tag : tags) {
            final String tagId = tag.getString(Keys.OBJECT_ID);
            final int refCnt = tag.getInt(Tag.TAG_REFERENCE_COUNT);
            tag.put(Tag.TAG_REFERENCE_COUNT, refCnt - 1);
            final int publishedRefCnt =
                    tag.getInt(Tag.TAG_PUBLISHED_REFERENCE_COUNT);
            if (article.getBoolean(Article.ARTICLE_IS_PUBLISHED)) {
                tag.put(Tag.TAG_PUBLISHED_REFERENCE_COUNT, publishedRefCnt - 1);
            } else {
                tag.put(Tag.TAG_PUBLISHED_REFERENCE_COUNT, publishedRefCnt);
            }
            tagRepository.update(tagId, tag);
            LOGGER.log(Level.FINEST,
                       "Deced tag[title={0}, refCnt={1}, publishedRefCnt={2}] of article[oId={3}]",
                       new Object[]{tag.getString(Tag.TAG_TITLE),
                                    tag.getInt(Tag.TAG_REFERENCE_COUNT),
                                    tag.getInt(Tag.TAG_PUBLISHED_REFERENCE_COUNT),
                                    articleId});
        }

        LOGGER.log(Level.FINER,
                   "Deced all tag reference count of article[oId={0}]",
                   articleId);
    }

    /**
     * Processes tags for article update.
     *
     * <ul>
     *   <li>Un-tags old article, decrements tag reference count</li>
     *   <li>Removes old article-tag relations</li>
     *   <li>Saves new article-tag relations with tag reference count</li>
     * </ul>
     *
     * @param oldArticle the specified old article
     * @param newArticle the specified new article
     * @throws Exception exception
     */
    public void processTagsForArticleUpdate(final JSONObject oldArticle,
                                            final JSONObject newArticle)
            throws Exception {
        final String oldArticleId = oldArticle.getString(Keys.OBJECT_ID);
        final List<JSONObject> oldTags =
                tagRepository.getByArticleId(oldArticleId);
        final String tagsString =
                newArticle.getString(Article.ARTICLE_TAGS_REF);
        String[] tagStrings = tagsString.split(",");
        final List<JSONObject> newTags = new ArrayList<JSONObject>();
        for (int i = 0; i < tagStrings.length; i++) {
            final String tagTitle = tagStrings[i].trim();
            JSONObject newTag = tagRepository.getByTitle(tagTitle);
            if (null == newTag) {
                newTag = new JSONObject();
                newTag.put(Tag.TAG_TITLE, tagTitle);
            }
            newTags.add(newTag);
        }

        final List<JSONObject> tagsDropped = new ArrayList<JSONObject>();
        final List<JSONObject> tagsNeedToAdd = new ArrayList<JSONObject>();
        final List<JSONObject> tagsUnchanged = new ArrayList<JSONObject>();
        for (final JSONObject newTag : newTags) {
            final String newTagTitle = newTag.getString(Tag.TAG_TITLE);
            if (!tagExists(newTagTitle, oldTags)) {
                LOGGER.log(Level.FINER, "Tag need to add[title={0}]",
                           newTagTitle);
                tagsNeedToAdd.add(newTag);
            } else {
                tagsUnchanged.add(newTag);
            }
        }
        for (final JSONObject oldTag : oldTags) {
            final String oldTagTitle = oldTag.getString(Tag.TAG_TITLE);
            if (!tagExists(oldTagTitle, newTags)) {
                LOGGER.log(Level.FINER, "Tag dropped[title={0}]", oldTag);
                tagsDropped.add(oldTag);
            } else {
                tagsUnchanged.remove(oldTag);
            }
        }

        LOGGER.log(Level.FINER, "Tags unchanged[{0}]", tagsUnchanged);
        for (final JSONObject tagUnchanged : tagsUnchanged) {
            final String tagId = tagUnchanged.optString(Keys.OBJECT_ID);
            if (null == tagId) {
                continue; // Unchanged tag always exist id
            }
            final int publishedRefCnt =
                    tagUnchanged.getInt(Tag.TAG_PUBLISHED_REFERENCE_COUNT);
            if (oldArticle.getBoolean(Article.ARTICLE_IS_PUBLISHED)) {
                if (!newArticle.getBoolean(Article.ARTICLE_IS_PUBLISHED)) {
                    tagUnchanged.put(Tag.TAG_PUBLISHED_REFERENCE_COUNT,
                                     publishedRefCnt - 1);
                    tagRepository.update(tagId, tagUnchanged);
                }
            } else {
                if (newArticle.getBoolean(Article.ARTICLE_IS_PUBLISHED)) {
                    tagUnchanged.put(Tag.TAG_PUBLISHED_REFERENCE_COUNT,
                                     publishedRefCnt + 1);
                    tagRepository.update(tagId, tagUnchanged);
                }
            }
        }

        for (final JSONObject tagDropped : tagsDropped) {
            final String tagId = tagDropped.getString(Keys.OBJECT_ID);
            final int refCnt = tagDropped.getInt(Tag.TAG_REFERENCE_COUNT);
            tagDropped.put(Tag.TAG_REFERENCE_COUNT, refCnt - 1);
            final int publishedRefCnt =
                    tagDropped.getInt(Tag.TAG_PUBLISHED_REFERENCE_COUNT);
            if (oldArticle.getBoolean(Article.ARTICLE_IS_PUBLISHED)) {
                tagDropped.put(Tag.TAG_PUBLISHED_REFERENCE_COUNT,
                               publishedRefCnt - 1);
            }

            tagRepository.update(tagId, tagDropped);
        }

        final String[] tagIdsDropped = new String[tagsDropped.size()];
        for (int i = 0; i < tagIdsDropped.length; i++) {
            final JSONObject tag = tagsDropped.get(i);
            final String id = tag.getString(Keys.OBJECT_ID);
            tagIdsDropped[i] = id;
        }
        articleUtils.removeTagArticleRelations(
                oldArticleId, 0 == tagIdsDropped.length
                ? new String[]{"l0y0l"}
                : tagIdsDropped);

        tagStrings = new String[tagsNeedToAdd.size()];
        for (int i = 0; i < tagStrings.length; i++) {
            final JSONObject tag = tagsNeedToAdd.get(i);
            final String tagTitle = tag.getString(Tag.TAG_TITLE);
            tagStrings[i] = tagTitle;
        }
        final JSONArray tags = tag(tagStrings, newArticle);

        articleUtils.addTagArticleRelation(tags, newArticle);
    }

    /**
     * Determines whether the specified tag title exists in the specified tags.
     *
     * @param tagTitle the specified tag title
     * @param tags the specified tags
     * @return {@code true} if it exists, {@code false} otherwise
     * @throws JSONException json exception
     */
    private static boolean tagExists(final String tagTitle,
                                     final List<JSONObject> tags)
            throws JSONException {
        for (final JSONObject tag : tags) {
            if (tag.getString(Tag.TAG_TITLE).equals(tagTitle)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Decrements reference count of every tag of an published article specified
     * by the given article id.
     *
     * @param articleId the given article id
     * @throws JSONException json exception
     * @throws RepositoryException repository exception
     */
    public void decTagPublishedRefCount(final String articleId)
            throws JSONException, RepositoryException {
        final List<JSONObject> tags = tagRepository.getByArticleId(articleId);

        for (final JSONObject tag : tags) {
            final String tagId = tag.getString(Keys.OBJECT_ID);
            final int refCnt = tag.getInt(Tag.TAG_REFERENCE_COUNT);
            tag.put(Tag.TAG_REFERENCE_COUNT, refCnt);
            final int publishedRefCnt =
                    tag.getInt(Tag.TAG_PUBLISHED_REFERENCE_COUNT);
            tag.put(Tag.TAG_PUBLISHED_REFERENCE_COUNT, publishedRefCnt - 1);
            tagRepository.update(tagId, tag);
        }
    }

    /**
     * Removes tags of unpublished articles from the specified tags.
     *
     * @param tags the specified tags
     * @throws JSONException json exception
     * @throws RepositoryException repository exception
     */
    public void removeForUnpublishedArticles(
            final List<JSONObject> tags) throws JSONException,
                                                RepositoryException {
        final Iterator<JSONObject> iterator = tags.iterator();
        while (iterator.hasNext()) {
            final JSONObject tag = iterator.next();
            if (0 == tag.getInt(Tag.TAG_PUBLISHED_REFERENCE_COUNT)) {
                iterator.remove();
            }
        }
    }

    /**
     * Gets the {@link Tags} singleton.
     *
     * @return the singleton
     */
    public static Tags getInstance() {
        return SingletonHolder.SINGLETON;
    }

    /**
     * Private default constructor.
     */
    private Tags() {
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
        private static final Tags SINGLETON = new Tags();

        /**
         * Private default constructor.
         */
        private SingletonHolder() {
        }
    }
}
