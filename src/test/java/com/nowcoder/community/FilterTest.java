package com.nowcoder.community;


import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.SensitiveFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class FilterTest {
    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Test
    public void testSensitive() {
        String ans = sensitiveFilter.filter("我又不赌博，只是开票,还有毒票");
        System.out.println(ans);
    }
}
