package com.nowcoder.community.util;

public class RedisKeyUtil {

    private static final String SPLIT = ":";
    private static final String PREFIX = "like:entity";


    //最终key的形式为“like:entity:entityType:entityId”
    public static String getRedisLikeKey(int entityType, int entityId) {
        return PREFIX + SPLIT + entityType + SPLIT + entityId;
    }
}
