<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8" />
        <title>B3log Solo Admin First</title>
        <link rel="icon" type="image/png" href="/favicon.png" />
    </head>
    <body>
        <form action="/init-admin" method="POST">
            <input name="userEmail" />
            <input name="userName" />
            <input name="userPassword" />
            

            <button type="submit">${confirmLabel}</button>
        </form>

    </body>
</html>
