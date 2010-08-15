/*
 * Copyright (C) 2009, 2010, B3log Team
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

package org.b3log.solo.csdn.blog.exporter;

import java.util.Set;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * {@link Exporter} test case.
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.0, Aug 15, 2010
 */
public final class ExporterTestCase {

    /**
     * Tests the {@linkplain Exporter#export(org.b3log.solo.csdn.blog.exporter.Blogger)}
     * method.
     *
     * @throws Exception exception
     */
    @Test
    public void export() throws Exception {
        final Blogger blogger = new Blogger();
        blogger.setArchiveStartDate("2006/12");
        blogger.setArchiveEndDate("2007/01");
        blogger.setId("DL88250");

        final Exporter exporter = new Exporter(blogger);
        final Set<Article> articles = exporter.export();

        assertNotSame(articles.size(), 0);
        
        for (final Article article : articles) {
            System.out.println("Article: ");
            System.out.println(article.getTitle());
            System.out.println(article.getTags());
            System.out.println(article.getCreateDate());
            System.out.println(article.getContent());
        }
    }
}
