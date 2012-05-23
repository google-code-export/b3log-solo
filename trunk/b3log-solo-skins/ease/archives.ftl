<#include "macro-head.ftl">
<!DOCTYPE html>
<html>
    <head>
        <@head title="${blogTitle}">
        <meta name="keywords" content="${metaKeywords},${archiveLabel}"/>
        <meta name="description" content="${metaDescription},${archiveLabel}"/>
        </@head>
    </head>
    <body>
        ${topBarReplacement}
        <#include "header.ftl">
        <div class="body">
            <#if 0 != archiveDates?size>
            <div class="other-main archives">
                <#list archiveDates as archiveDate>
                <span data-year="${archiveDate.archiveDateYear}">
                    <#if "en" == localeString?substring(0, 2)>
                    <a href="${servePath}/archives/${archiveDate.archiveDateYear}/${archiveDate.archiveDateMonth}"
                       title="${archiveDate.monthName} ${archiveDate.archiveDateYear}(${archiveDate.archiveDatePublishedArticleCount})">
                        ${archiveDate.monthName} ${archiveDate.archiveDateYear}(${archiveDate.archiveDatePublishedArticleCount})</a>
                    <#else>
                    <a href="${servePath}/archives/${archiveDate.archiveDateYear}/${archiveDate.archiveDateMonth}"
                       title="${archiveDate.archiveDateYear} ${yearLabel} ${archiveDate.archiveDateMonth} ${monthLabel}(${archiveDate.archiveDatePublishedArticleCount})">
                        ${archiveDate.archiveDateYear} ${yearLabel} ${archiveDate.archiveDateMonth} ${monthLabel}(${archiveDate.archiveDatePublishedArticleCount})</a>
                    </#if>
                </span>
                 <span data-year="2011">
                    <a href="http://vanessa.b3log.org:80/archives/2011/12" title="2011 年 12 月(6)">
                        2011 年 12 月(6)
                    </a>
                </span>
                <span data-year="2011">
                    <a href="http://vanessa.b3log.org:80/archives/2011/11"
                       title="2011 年 11 月(15)">
                        2011 年 11 月(15)</a>
                </span>
                <span data-year="2011">
                    <a href="http://vanessa.b3log.org:80/archives/2011/10"
                       title="2011 年 10 月(5)">
                        2011 年 10 月(5)</a>
                </span>
                <span data-year="2011">
                    <a href="http://vanessa.b3log.org:80/archives/2011/09"
                       title="2011 年 09 月(5)">
                        2011 年 09 月(5)</a>
                </span>
                <span data-year="2011">
                    <a href="http://vanessa.b3log.org:80/archives/2011/08"
                       title="2011 年 08 月(12)">
                        2011 年 08 月(12)</a>
                </span>
                <span data-year="2011">
                    <a href="http://vanessa.b3log.org:80/archives/2011/07"
                       title="2011 年 07 月(5)">
                        2011 年 07 月(5)</a>
                </span>
                <span data-year="2011">
                    <a href="http://vanessa.b3log.org:80/archives/2011/06"
                       title="2011 年 06 月(5)">
                        2011 年 06 月(5)</a>
                </span>
                <span data-year="2011">
                    <a href="http://vanessa.b3log.org:80/archives/2011/05"
                       title="2011 年 05 月(2)">
                        2011 年 05 月(2)</a>
                </span>
                <span data-year="2011">
                    <a href="http://vanessa.b3log.org:80/archives/2011/04"
                       title="2011 年 04 月(3)">
                        2011 年 04 月(3)</a>
                </span>
                <span data-year="2011">
                    <a href="http://vanessa.b3log.org:80/archives/2011/03"
                       title="2011 年 03 月(2)">
                        2011 年 03 月(2)</a>
                </span>
                <span data-year="2011">
                    <a href="http://vanessa.b3log.org:80/archives/2011/03"
                       title="2011 年 03 月(5)">
                        2011 年 03 月(5)</a>
                </span>
                <span data-year="2011">
                    <a href="http://vanessa.b3log.org:80/archives/2011/02"
                       title="2011 年 02 月(5)">
                        2011 年 02 月(5)</a>
                </span>
                <span data-year="2011">
                    <a href="http://vanessa.b3log.org:80/archives/2011/01"
                       title="2011 年 01 月(5)">
                        2011 年 01 月(5)</a>
                </span>
                <span data-year="2010">
                    <a href="http://vanessa.b3log.org:80/archives/2010/12"
                       title="2010 年 12 月(6)">
                        2010 年 12 月(6)</a>
                </span>
                <span data-year="2010">
                    <a href="http://vanessa.b3log.org:80/archives/2010/12"
                       title="2010 年 12 月(3)">
                        2010 年 12 月(3)</a>
                </span>
                <span data-year="2010">
                    <a href="http://vanessa.b3log.org:80/archives/2010/11"
                       title="2010 年 11 月(6)">
                        2010 年 11 月(6)</a>
                </span>
                <span data-year="2010">
                    <a href="http://vanessa.b3log.org:80/archives/2010/11"
                       title="2010 年 11 月(1)">
                        2010 年 11 月(1)</a>
                </span>
                <span data-year="2010">
                    <a href="http://vanessa.b3log.org:80/archives/2010/10"
                       title="2010 年 10 月(6)">
                        2010 年 10 月(6)</a>
                </span>
                <span data-year="2010">
                    <a href="http://vanessa.b3log.org:80/archives/2010/09"
                       title="2010 年 09 月(7)">
                        2010 年 09 月(7)</a>
                </span>
                <span data-year="2010">
                    <a href="http://vanessa.b3log.org:80/archives/2010/08"
                       title="2010 年 08 月(8)">
                        2010 年 08 月(8)</a>
                </span>
                <span data-year="2010">
                    <a href="http://vanessa.b3log.org:80/archives/2010/07"
                       title="2010 年 07 月(9)">
                        2010 年 07 月(9)</a>
                </span>
                <span data-year="2010">
                    <a href="http://vanessa.b3log.org:80/archives/2010/06"
                       title="2010 年 06 月(8)">
                        2010 年 06 月(8)</a>
                </span>
                <span data-year="2010">
                    <a href="http://vanessa.b3log.org:80/archives/2010/05"
                       title="2010 年 05 月(6)">
                        2010 年 05 月(6)</a>
                </span>
                <span data-year="2010">
                    <a href="http://vanessa.b3log.org:80/archives/2010/04"
                       title="2010 年 04 月(3)">
                        2010 年 04 月(3)</a>
                </span>
                <span data-year="2010">
                    <a href="http://vanessa.b3log.org:80/archives/2010/03"
                       title="2010 年 03 月(4)">
                        2010 年 03 月(4)</a>
                </span>
                <span data-year="2010">
                    <a href="http://vanessa.b3log.org:80/archives/2010/02"
                       title="2010 年 02 月(7)">
                        2010 年 02 月(7)</a>
                </span>
                <span data-year="2010">
                    <a href="http://vanessa.b3log.org:80/archives/2010/01"
                       title="2010 年 01 月(8)">
                        2010 年 01 月(8)</a>
                </span>
                <span data-year="2009">
                    <a href="http://vanessa.b3log.org:80/archives/2009/12"
                       title="2009 年 12 月(8)">
                        2009 年 12 月(8)</a>
                </span>
                <span data-year="2009">
                    <a href="http://vanessa.b3log.org:80/archives/2009/11"
                       title="2009 年 11 月(10)">
                        2009 年 11 月(10)</a>
                </span>
                <span data-year="2009">
                    <a href="http://vanessa.b3log.org:80/archives/2009/10"
                       title="2009 年 10 月(20)">
                        2009 年 10 月(20)</a>
                </span>
                <span data-year="2009">
                    <a href="http://vanessa.b3log.org:80/archives/2009/09"
                       title="2009 年 09 月(18)">
                        2009 年 09 月(18)</a>
                </span>
                <span data-year="2009">
                    <a href="http://vanessa.b3log.org:80/archives/2009/08"
                       title="2009 年 08 月(29)">
                        2009 年 08 月(29)</a>
                </span>
                <span data-year="2009">
                    <a href="http://vanessa.b3log.org:80/archives/2009/07"
                       title="2009 年 07 月(18)">
                        2009 年 07 月(18)</a>
                </span>
                <span data-year="2009">
                    <a href="http://vanessa.b3log.org:80/archives/2009/06"
                       title="2009 年 06 月(24)">
                        2009 年 06 月(24)</a>
                </span>
                <span data-year="2009">
                    <a href="http://vanessa.b3log.org:80/archives/2009/05"
                       title="2009 年 05 月(38)">
                        2009 年 05 月(38)</a>
                </span>
                <span data-year="2009">
                    <a href="http://vanessa.b3log.org:80/archives/2009/04"
                       title="2009 年 04 月(43)">
                        2009 年 04 月(43)</a>
                </span>
                <span data-year="2009">
                    <a href="http://vanessa.b3log.org:80/archives/2009/03"
                       title="2009 年 03 月(35)">
                        2009 年 03 月(35)</a>
                </span>
                <span data-year="2009">
                    <a href="http://vanessa.b3log.org:80/archives/2009/02"
                       title="2009 年 02 月(34)">
                        2009 年 02 月(34)</a>
                </span>
                <span data-year="2009">
                    <a href="http://vanessa.b3log.org:80/archives/2009/01"
                       title="2009 年 01 月(18)">
                        2009 年 01 月(18)</a>
                </span>
                <span data-year="2008">
                    <a href="http://vanessa.b3log.org:80/archives/2008/12"
                       title="2008 年 12 月(24)">
                        2008 年 12 月(24)</a>
                </span>
                <span data-year="2008">
                    <a href="http://vanessa.b3log.org:80/archives/2008/11"
                       title="2008 年 11 月(32)">
                        2008 年 11 月(32)</a>
                </span>
                <span data-year="2008">
                    <a href="http://vanessa.b3log.org:80/archives/2008/10"
                       title="2008 年 10 月(16)">
                        2008 年 10 月(16)</a>
                </span>
                <span data-year="2008">
                    <a href="http://vanessa.b3log.org:80/archives/2008/09"
                       title="2008 年 09 月(20)">
                        2008 年 09 月(20)</a>
                </span>
                <span data-year="2008">
                    <a href="http://vanessa.b3log.org:80/archives/2008/08"
                       title="2008 年 08 月(6)">
                        2008 年 08 月(6)</a>
                </span>
                <span data-year="2008">
                    <a href="http://vanessa.b3log.org:80/archives/2008/07"
                       title="2008 年 07 月(10)">
                        2008 年 07 月(10)</a>
                </span>
                <span data-year="2008">
                    <a href="http://vanessa.b3log.org:80/archives/2008/06"
                       title="2008 年 06 月(18)">
                        2008 年 06 月(18)</a>
                </span>
                <span data-year="2008">
                    <a href="http://vanessa.b3log.org:80/archives/2008/05"
                       title="2008 年 05 月(15)">
                        2008 年 05 月(15)</a>
                </span>
                <span data-year="2008">
                    <a href="http://vanessa.b3log.org:80/archives/2008/04"
                       title="2008 年 04 月(19)">
                        2008 年 04 月(19)</a>
                </span>
                <span data-year="2008">
                    <a href="http://vanessa.b3log.org:80/archives/2008/03"
                       title="2008 年 03 月(55)">
                        2008 年 03 月(55)</a>
                </span>
                <span data-year="2008">
                    <a href="http://vanessa.b3log.org:80/archives/2008/02"
                       title="2008 年 02 月(2)">
                        2008 年 02 月(2)</a>
                </span>
                <span data-year="2008">
                    <a href="http://vanessa.b3log.org:80/archives/2008/01"
                       title="2008 年 01 月(1)">
                        2008 年 01 月(1)</a>
                </span>
                 <span data-year="2007">
                    <a href="http://vanessa.b3log.org:80/archives/2008/01"
                       title="2007年 01 月(1)">
                        2007 年 01 月(1)</a>
                </span>
                 <span data-year="2006">
                    <a href="http://vanessa.b3log.org:80/archives/2008/01"
                       title="2008 年 01 月(1)">
                        2006年 01 月(1)</a>
                </span>
                </#list>
            </div>
            </#if>
        </div>
        <#include "footer.ftl">
    </body>
</html>
