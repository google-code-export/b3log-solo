<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8" />
        <title>B3log Solo ${loginLabel}</title>
        <link rel="icon" type="image/png" href="/favicon.png" />
    </head>
    <body>
        <form action="/login?goto=${goto}" method="POST">
            UserEmail: <input name="userEmail" />
            UserPwd: <input name="userPassword" />

            <button type="submit">${loginLabel}</button>
        </form>

    </body>
</html>
