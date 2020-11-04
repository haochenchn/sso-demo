package com.aaron.cas.controller;

import com.aaron.cas.utils.CaptchaUtil;
import org.apereo.cas.util.http.HttpMessage;
import org.apereo.cas.util.http.SimpleHttpClient;
import org.apereo.cas.util.http.SimpleHttpClientFactoryBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Aaron
 * @description
 * @date 2020/9/13
 */
@Controller
public class CaptchaController {
    public static final String KEY_CAPTCHA = "captcha";

    @RequestMapping("/captcha.jpg")
    public void getCaptcha(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
        // 设置相应类型,告诉浏览器输出的内容为图片
        response.setContentType("image/jpeg");
        // 不缓存此内容
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expire", 0);
        try {

            HttpSession session = request.getSession();

            CaptchaUtil tool = new CaptchaUtil();
            StringBuffer code = new StringBuffer();
            BufferedImage image = tool.genRandomCodeImage(code);
            session.removeAttribute(KEY_CAPTCHA);
            session.setAttribute(KEY_CAPTCHA, code.toString());

            // 将内存中的图片通过流动形式输出到客户端
            ImageIO.write(image, "JPEG", response.getOutputStream());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @RequestMapping("/test")
    public void test() throws MalformedURLException {
        SimpleHttpClientFactoryBean simpleHttpClientFactoryBean = new SimpleHttpClientFactoryBean();
        SimpleHttpClient object = simpleHttpClientFactoryBean.getObject();
        URL url = new URL("https://app2.cas.com:8082/");
        HttpMessage httpMessage = object.sendMessageToEndPoint(url);
        System.out.println(httpMessage);
    }
}
