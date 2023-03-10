package com.nowcoder.community.util;

public interface CommunityConstant {
    /*
    * 激活成功
    * */
    int ACTIVATION_SUCCESS = 0;
    /*
    * 重复激活
    * */
    int ACTIVATION_REPEAT = 1;

    /*
    * 激活失败
    * */
    int ACTIVATION_FAILURE = 2;

    /*
    * 默认登录凭证的超时时间
    * */
    int DEFAULT_EXPIRED_SECONDS = 3600*12;


    /*
    * 记住状态的登录凭证的超市时间
    * */
    int REMEMBER_EXPIRED_SECONDS = 3600*24*100;


    /*
    * 表示为帖子类型
    * */

    int ENTITY_TYPE_POST = 1;

    /*
    * 表示为评论类型
    * */

    int ENTITY_TYPE_COMMENT = 2;
}
