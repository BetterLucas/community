package com.nowcoder.community.controller;

import com.google.code.kaptcha.Producer;

import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;


import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;

@Controller
public class LogInController implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(LogInController.class);
    @Autowired
    private UserService userService;

    @Autowired
    private Producer kaptchaProducer;

    //从配置文件注入上下文路径
    @Value("${server.servlet.context-path}")
    String contextPath;

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String getRegisterPage(){
        return "/site/register";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String getLoginPage() {
        return "/site/login";
    }


    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String register(Model model, User user) {
        Map<String, Object> map = userService.register(user);

        if (map == null || map.isEmpty()) {
            model.addAttribute("Msg", "注册成功，我们向您的邮箱发送了一封激活邮件，请尽快处理！");
            model.addAttribute("target","/index");
            return "/site/operate-result";
        }
        else {
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            model.addAttribute("emailMsg", map.get("emailMsg"));
            return "/site/register";
        }

    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(Model model, String username, String password, String code, boolean rememberme, HttpSession session,
                        HttpServletResponse response) {
        //在表现层校验验证码
        String kaptcha = (String)session.getAttribute("kaptcha");
        if (StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code) || !kaptcha.equalsIgnoreCase(code)) {
            model.addAttribute("codeMsg", "验证码不正确！");
            return "/site/login";
        }

        //校验用户名和密码
        int expiredTime = rememberme ? REMEMBER_EXPIRED_SECONDS : DEFAULT_EXPIRED_SECONDS;

        Map<String, Object> map = userService.signOn(username, password, expiredTime);

        //登录成功，返回ticket给客户端,跳转回index
        if (map.containsKey("ticket")) {
            //通过cookie将ticket返回给客户端
            Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
            cookie.setPath(contextPath);
            cookie.setMaxAge(expiredTime);
            response.addCookie(cookie);
            return "redirect:index";
        }
        else {
            //失败
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            return "/site/login";
        }
    }
    //退出登录
    @RequestMapping(value ="/logout",method = RequestMethod.GET)
    public String exit(@CookieValue("ticket") String ticket) {
        userService.signOut(ticket);
        return "redirect:index";
//        return "/site/login";
    }

    @RequestMapping(path = "/activation/{userId}/{code}", method = RequestMethod.GET)
    public String activation(Model model, @PathVariable("userId") int userId, @PathVariable("code") String code) {
        int activationResult = userService.activation(userId, code);
        if (activationResult == ACTIVATION_SUCCESS) {
            model.addAttribute("Msg", "激活成功，您现在可以正常使用该账号！");
            model.addAttribute("target", "/login");
        }
        else if (activationResult == ACTIVATION_REPEAT) {
            model.addAttribute("Msg", "重复激活，该账号已经被激活过了！");
            model.addAttribute("target", "/index");
        }
        else {
            model.addAttribute("Msg", "激活失败！");
            model.addAttribute("target", "/index");
        }

        return "/site/operate-result";
    }
    /*
    * 验证码请求路径
    * */
    @RequestMapping(value = "/kaptcha", method = RequestMethod.GET)
    public void getKaptcha(HttpServletResponse response, HttpSession session) {
        //生成验证码
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);

        //将验证码存入session
        session.setAttribute("kaptcha",text);

        //将图片输出给浏览器
        response.setContentType("image/png");

        try {
            ServletOutputStream outputStream = response.getOutputStream();
            ImageIO.write(image,"png",outputStream);
        } catch (IOException e) {
            logger.error("验证码响应失败：" + e.getMessage());
        }
    }
}
