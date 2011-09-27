<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8" />
        <title>B3log Solo ${loginLabel}</title>
        <meta name="keywords" content="GAE 博客,GAE blog,b3log" />
        <meta name="description" content="An open source blog based on GAE Java,GAE Java 开源博客" />
        <meta name="author" content="B3log Team" />
        <meta name="generator" content="B3log" />
        <meta name="copyright" content="B3log" />
        <meta name="revised" content="B3log, ${year}" />
        <meta name="robots" content="noindex, follow" />
        <link rel="icon" type="image/png" href="/favicon.png" />
    </head>
    <body>
        <form action="/login?goto=${goto}" method="POST">
            <input name="userName" />
            <input name="userPassword" />
            
            <button type="submit">${loginLabel}</button>
        </form>

    </body>
</html>
