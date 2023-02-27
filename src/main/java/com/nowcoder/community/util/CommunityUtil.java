package com.nowcoder.community.util;

import com.alibaba.fastjson.JSONObject;
import org.springframework.util.DigestUtils;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
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

    //封装工具，从cookie中取对应的值
    public static String getValue(HttpServletRequest request, String keyname) {
        if (request == null || keyname == null) {
            throw new IllegalArgumentException("参数不能为空！");
        }
        Cookie[] cookies = request.getCookies();

        for (Cookie cookie : cookies) {
            if(cookie.getName().equals(keyname)) {
                return cookie.getValue();
            }
        }
        return null;

    }

    public static String getJSONString(int code, String msg, Map<String, Object> map) {
        JSONObject json = new JSONObject();
        json.put("code", code);
        json.put("msg", msg);

        if (map != null) {
            for (String key : map.keySet()) {
                json.put(key, map.get(key));
            }
        }

        return json.toJSONString();
    }

    public static String getJSONString(int code, String msg) {
        return getJSONString(code, msg, null);
    }

    public static String getJSONString(int code) {
        return getJSONString(code, null, null);
    }

}
