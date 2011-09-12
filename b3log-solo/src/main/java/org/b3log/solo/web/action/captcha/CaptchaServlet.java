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
package org.b3log.solo.web.action.captcha;

import org.b3log.latke.image.Image;
import org.b3log.latke.image.ImageService;
import org.b3log.latke.image.ImageServiceFactory;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import static org.b3log.solo.SoloServletListener.*;

/**
 * Captcha servlet..
 *
 * <p>
 *   See <a href="http://isend-blog.appspot.com/2010/03/25/captcha_on_GAE.html">
 *  在GAE上拼接生成图形验证码</a> for philosophy. Checkout
 *    <a href="http://toy-code.googlecode.com/svn/trunk/CaptchaGenerator">
 *    the sample captcha generator</a> for mor details.
 * </p>
 *
 * @author <a href="mailto:DL88250@gmail.com">Liang Ding</a>
 * @version 1.0.0.1, Aug 16, 2011
 */
public final class CaptchaServlet extends HttpServlet {

    /**
     * Logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(CaptchaServlet.class.getName());
    /**
     * Default serial version uid.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Length of captcha.
     */
    private static final int LENGTH = 4;
    /**
     * Images service.
     */
    private static final ImageService IMAGE_SERVICE =
            ImageServiceFactory.getImageService();
    /**
     * Random.
     */
    private static final Random RANDOM = new Random();
    /**
     * Key of captcha.
     */
    public static final String CAPTCHA = "captcha";

    @Override
    protected void doGet(final HttpServletRequest request,
                         final HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("image/jpeg");

        final String row = String.valueOf(RANDOM.nextInt(MAX_CAPTCHA_ROW));
        String captcha = "";
        final List<Image> images = new ArrayList<Image>();
        for (int i = 0; i < LENGTH; i++) {
            final String column = String.valueOf(RANDOM.nextInt(
                    MAX_CAPTCHA_COLUM));
            captcha += column;
            final String imageName = row + "/" + column + ".png";
            final Image captchaChar = CAPTCHAS.get(imageName);

            images.add(captchaChar);
        }

        final Image captchaImage = IMAGE_SERVICE.makeImage(images);

        final HttpSession httpSession = request.getSession();
        LOGGER.log(Level.FINER, "Captcha[{0}] for session[id={1}]",
                   new Object[]{captcha,
                                httpSession.getId()});
        httpSession.setAttribute(CAPTCHA, captcha);

        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/png");

        final OutputStream outputStream = response.getOutputStream();
        outputStream.write(captchaImage.getData());
        outputStream.close();
    }
}
