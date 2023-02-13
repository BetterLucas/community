package com.nowcoder.community.util;

import org.springframework.util.DigestUtils;
import org.thymeleaf.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class CommunityUtil {

    //生成随机字符串用作验证码
    public static String generateUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    //对输入密码进行md5加密
    public static String md5(String key) {
        if (StringUtils.isEmptyOrWhitespace(key)) {
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }
}
