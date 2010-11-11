<%@page import="java.util.logging.Logger"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <h1>Hello World!</h1>
    </body>
</html>


<%!
    private static final Logger LOGGER = Logger.getAnonymousLogger();


    /**
     * Default preference.
     *
     * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
     * @version 1.0.0.2, Nov 1, 2010
     */
    public static final class DefaultPreference {

        /**
         * Default recent article display count.
         */
        public static final int DEFAULT_RECENT_ARTICLE_DISPLAY_COUNT = 10;
        /**
         * Default recent comment display count.
         */
        public static final int DEFAULT_RECENT_COMMENT_DISPLAY_COUNT = 10;
        /**
         * Default most used tag display count.
         */
        public static final int DEFAULT_MOST_USED_TAG_DISPLAY_COUNT = 20;
        /**
         * Default article list display count.
         */
        public static final int DEFAULT_ARTICLE_LIST_DISPLAY_COUNT = 20;
        /**
         * Default article list pagination window size.
         */
        public static final int DEFAULT_ARTICLE_LIST_PAGINATION_WINDOW_SIZE =
                15;
        /**
         * Default most comment article display count.
         */
        public static final int DEFAULT_MOST_COMMENT_ARTICLE_DISPLAY_COUNT = 5;
        /**
         * Default blog title.
         */
        public static final String DEFAULT_BLOG_TITLE = "Solo 示例";
        /**
         * Default blog subtitle.
         */
        public static final String DEFAULT_BLOG_SUBTITLE = "GAE 上的个人博客";
        /**
         * Default skin directory name.
         */
        public static final String DEFAULT_SKIN_DIR_NAME = "classic";
        /**
         * Default administrator mail.
         */
        public static final String DEFAULT_ADMIN_GMAIL = "b3log.solo@gmail.com";
        /**
         * Default blog host.
         */
        public static final String DEFAULT_BLOG_HOST = "localhost:8080";
        /**
         * Default language.
         */
        public static final String DEFAULT_LANGUAGE = "zh_CN";

        /**
         * Private default constructor.
         */
        private DefaultPreference() {
        }
    }
%>
