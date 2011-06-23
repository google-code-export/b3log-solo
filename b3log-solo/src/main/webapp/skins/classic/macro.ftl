<#macro head title>
<title>${title}</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<#nested>
<meta name="author" content="B3log Team"/>
<meta name="generator" content="B3log"/>
<meta name="copyright" content="B3log"/>
<meta name="revised" content="B3log, ${year}"/>
<meta http-equiv="Window-target" content="_top"/>
<link type="text/css" rel="stylesheet" href="/styles/default-base.css"/>
<link type="text/css" rel="stylesheet" href="/skins/${skinDirName}/default-index.css"/>
<link href="blog-articles-feed.do" title="ATOM" type="application/atom+xml" rel="alternate" />
<link rel="icon" type="image/png" href="/favicon.png"/>
${htmlHead}
</#macro>