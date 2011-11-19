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
package org.b3log.solo.model.feed.rss;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import org.apache.commons.lang.time.DateFormatUtils;

/**
 * RSS 2.0 channel.
 *
 * <p>
 * See <a href="http://cyber.law.harvard.edu/rss/rss.html">RSS 2.0 at Harvard Law</a> 
 * for more details.
 * </p>
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.1.0.1, Nov 19, 2011
 * @see Item
 * @see Category
 * @since 0.3.1
 */
public final class Channel {

    /**
     * Title.
     */
    private String title;
    /**
     * Link.
     */
    private String link;
    /**
     * Description.
     */
    private String description;
    /**
     * Generator.
     */
    private String generator;
    /**
     * Last build date.
     */
    private Date lastBuildDate;
    /**
     * Language.
     */
    private String language;
    /**
     * Items.
     */
    private List<Item> items = new ArrayList<Item>();
    /**
     * Time zone id.
     */
    public static final String TIME_ZONE_ID = "Asia/Shanghai";
    /**
     * Start.
     */
    private static final String START =
            "<?xml version='1.0' encoding='UTF-8'?><rss version=\"2.0\"><channel>";
    /**
     * End.
     */
    private static final String END = "</channel></rss>";
    /**
     * Start title element.
     */
    private static final String START_TITLE_ELEMENT = "<title>";
    /**
     * End title element.
     */
    private static final String END_TITLE_ELEMENT = "</title>";
    /**
     * Start link element.
     */
    private static final String START_LINK_ELEMENT = "<link>";
    /**
     * End link element.
     */
    private static final String END_LINK_ELEMENT = "</link>";
    /**
     * Start description element.
     */
    private static final String START_DESCRIPTION_ELEMENT = "<description>";
    /**
     * End description element.
     */
    private static final String END_DESCRIPTION_ELEMENT = "</description>";
    /**
     * Start generator element.
     */
    private static final String START_GENERATOR_ELEMENT = "<generator>";
    /**
     * End generator element.
     */
    private static final String END_GENERATOR_ELEMENT = "</generator>";
    /**
     * Start language element.
     */
    private static final String START_LANGUAGE_ELEMENT = "<language>";
    /**
     * End language element.
     */
    private static final String END_LANGUAGE_ELEMENT = "</language>";
    /**
     * Start last build date element.
     */
    private static final String START_LAST_BUILD_DATE_ELEMENT =
            "<lastBuildDate>";
    /**
     * End last build date  element.
     */
    private static final String END_LAST_BUILD_DATE_ELEMENT = "</lastBuildDate>";

    /**
     * Gets the last build date.
     * 
     * @return last build date
     */
    public Date getLastBuildDate() {
        return lastBuildDate;
    }

    /**
     * Sets the last build date with the specified last build date.
     * 
     * @param lastBuildDate the specified last build date
     */
    public void setLastBuildDate(final Date lastBuildDate) {
        this.lastBuildDate = lastBuildDate;
    }

    /**
     * Gets generator.
     * 
     * @return generator
     */
    public String getGenerator() {
        return generator;
    }

    /**
     * Sets the generator with the specified generator.
     * 
     * @param generator the specified generator
     */
    public void setGenerator(final String generator) {
        this.generator = generator;
    }

    /**
     * Gets the link.
     *
     * @return link
     */
    public String getLink() {
        return link;
    }

    /**
     * Sets the link with the specified link.
     *
     * @param link the specified link
     */
    public void setLink(final String link) {
        this.link = link;
    }

    /**
     * Gets the title.
     *
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title with the specified title.
     *
     * @param title the specified title
     */
    public void setTitle(final String title) {
        this.title = title;
    }

    /**
     * Adds the specified item.
     *
     * @param item the specified item
     */
    public void addItem(final Item item) {
        items.add(item);
    }

    /**
     * Gets the description.
     *
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description with the specified description.
     *
     * @param description the specified description
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * Gets the language.
     *
     * @return language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Sets the language with the specified language.
     *
     * @param language the specified language
     */
    public void setLanguage(final String language) {
        this.language = language;
    }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(START);

        stringBuilder.append(START_TITLE_ELEMENT);
        stringBuilder.append(title);
        stringBuilder.append(END_TITLE_ELEMENT);

        stringBuilder.append(START_LINK_ELEMENT);
        stringBuilder.append(link);
        stringBuilder.append(END_LINK_ELEMENT);

        stringBuilder.append(START_DESCRIPTION_ELEMENT);
        stringBuilder.append(description);
        stringBuilder.append(END_DESCRIPTION_ELEMENT);

        stringBuilder.append(START_GENERATOR_ELEMENT);
        stringBuilder.append(generator);
        stringBuilder.append(END_GENERATOR_ELEMENT);

        stringBuilder.append(START_LAST_BUILD_DATE_ELEMENT);
        stringBuilder.append(DateFormatUtils.format(
                lastBuildDate, "EEE, dd MMM yyyy HH:mm:ss z",
                TimeZone.getTimeZone(Channel.TIME_ZONE_ID)));
        stringBuilder.append(END_LAST_BUILD_DATE_ELEMENT);

        stringBuilder.append(START_LANGUAGE_ELEMENT);
        stringBuilder.append(language);
        stringBuilder.append(END_LANGUAGE_ELEMENT);

        for (final Item item : items) {
            stringBuilder.append(item.toString());
        }

        stringBuilder.append(END);

        return stringBuilder.toString();
    }
}
