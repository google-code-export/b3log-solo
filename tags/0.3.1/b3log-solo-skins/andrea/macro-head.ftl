<#macro head title>
<meta charset="utf-8" />
<title>${title}</title>
<#nested>
<meta name="author" content="B3log Team" />
<meta name="generator" content="B3log" />
<meta name="copyright" content="B3log" />
<meta name="revised" content="B3log, ${year}" />
<meta http-equiv="Window-target" content="_top" />
<link href='http://fonts.googleapis.com/css?family=Neucha' rel='stylesheet' type='text/css' charset="utf-8" />
<link href='http://fonts.googleapis.com/css?family=Reenie+Beanie' rel='stylesheet' type='text/css' charset="utf-8" />
<link type="text/css" rel="stylesheet" href="/css/default-base${miniPostfix}.css" charset="utf-8" />
<link type="text/css" rel="stylesheet" href="/skins/${skinDirName}/css/${skinDirName}.css" charset="utf-8" />
<link href="blog-articles-feed.do" title="ATOM" type="application/atom+xml" rel="alternate" />
<link rel="icon" type="image/png" href="/favicon.png" />
${htmlHead}
</#macro>