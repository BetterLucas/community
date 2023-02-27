package com.nowcoder.community.controller;


import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;


@Controller
@RequestMapping(path = "/user")
public class UserController {

    private static Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.domain}")
    private String domainPath;

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder; //标识目前持有用户

    @LoginRequired
    @RequestMapping(path = "/setting", method = RequestMethod.GET)
    public String getSettingPage() {
        return "/site/setting";
    }

    //头像上传
    @LoginRequired
    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model) {

        if (headerImage == null) {
            model.addAttribute("error", "还未上传图片");
            return "/site/setting";
        }



        String filename = headerImage.getOriginalFilename();
        assert filename != null;
        String suffix = filename.substring(filename.lastIndexOf("."));
        if (StringUtils.isBlank(suffix)) {
            model.addAttribute("error", "还未选择图片");
            return "/site/setting";
        }
        filename = CommunityUtil.generateUUID() + suffix; //生成随机的文件名
        //将图片存入本地硬盘中
        File dst = new File(uploadPath+ "/" + filename);
        try {
            headerImage.transferTo(dst);
        } catch (IOException e) {
            logger.error("图片上传失败!" + e.getMessage());
        }
        //构建图片访问路径：http://localhost:8080/community/user/header/xxx.png.更改user中的图片路径
        //从hostholder中取持有的用户，并查询其id
        User user = hostHolder.getUser();
        String userName = user.getUserName();
        String webpath = domainPath + contextPath + "/user/header/" + filename;
        userService.updateHeaderURL(webpath,userName);



        return "redirect:/index";
    }


    //显示头像
    @RequestMapping(path = "/header/{filename}", method = RequestMethod.GET)
    public void getHeader(@PathVariable("filename") String filename, HttpServletResponse response) {
        //获取图片在服务器的存储路径
        filename = uploadPath + "/" + filename;

        //获取文件后缀
        String suffix = filename.substring(filename.lastIndexOf(".") + 1);

        //响应图片
        response.setContentType("image/" + suffix);
        try (            OutputStream os = response.getOutputStream();
                         FileInputStream stream = new FileInputStream(filename)){

            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = stream.read(buffer))!= -1) {
                os.write(buffer, 0, b);
            }

        } catch (IOException e) {
            logger.error("读取图像失败！" + e.getMessage());
        }

    }


    //修改密码
    @RequestMapping(path = "/update/password", method = RequestMethod.POST)
    public String updatePassword(String oldPassword, String newPassword, String confirmPassword, Model model) {
        if (StringUtils.isBlank(oldPassword) || oldPassword == null) {
            model.addAttribute("oldPasswordMsg", "请输入原密码！");
            return "/site/setting";
        }
        //将原密码进行加密与数据库中比对
        oldPassword = CommunityUtil.md5(oldPassword + hostHolder.getUser().getSalt());
        if (!oldPassword.equals(hostHolder.getUser().getPassword())) {
            model.addAttribute("oldPasswordMsg", "原密码输入错误！");
            return "/site/setting";
        }

        if (StringUtils.isBlank(newPassword) || newPassword == null) {
            model.addAttribute("newPasswordMsg", "新密码不能为空!");
            return "/site/setting";
        }

        if (StringUtils.isBlank(confirmPassword) || confirmPassword == null) {
            model.addAttribute("confPasswordMsg", "请确认密码！");
            return "/site/setting";
        }

        if (!newPassword.equals(confirmPassword) ) {
            model.addAttribute("confPasswordMsg", "两次密码输入不一致！");
            return "/site/setting";
        }
        //校验新密码是否与原密码一致
        newPassword = CommunityUtil.md5(newPassword + hostHolder.getUser().getSalt());
        if (newPassword.equals(oldPassword)) {
            model.addAttribute("confPasswordMsg", "新密码不能与原密码一致！");
            return "/site/setting";
        }

        userService.updatePassword(hostHolder.getUser().getUserName(), newPassword);

        return "redirect:/index";
    }

}
