package com.nowcoder.community.config;

import com.nowcoder.community.controller.interceptor.LoginInterceptor;
import com.nowcoder.community.controller.interceptor.LoginRequiredInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Autowired
    private LoginInterceptor loginInterceptor;

    @Autowired
    private LoginRequiredInterceptor loginRequiredInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //设置不需要拦截的资源,不设置拦截请求路径，默认会拦截所有的请求
        registry.addInterceptor(loginInterceptor).excludePathPatterns("/**/*.css","/**/*.jpg","/**/*.js","/**/*.png","/**/*.jpeg");

        //访问非法的访问路径，例如未登录访问用户设置页面
        registry.addInterceptor(loginRequiredInterceptor).excludePathPatterns("/**/*.css","/**/*.jpg","/**/*.js","/**/*.png","/**/*.jpeg");
    }

}
