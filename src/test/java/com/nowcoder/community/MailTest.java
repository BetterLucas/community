package com.nowcoder.community;

import com.nowcoder.community.util.MailClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@SpringBootTest
public class MailTest {
    @Autowired
    private MailClient client;

    @Autowired
    private TemplateEngine templateEngine;

    @Test
    public void testTextMail() {
        client.sendMail("betterlucasxgq@outlook.com", "a test mail", "Test");
    }

    /*测试发送html的邮件*/
    @Test
    public void testHtmlMail() {
        Context context = new Context();
        context.setVariable("username", "lucas");
        String process = templateEngine.process("/mail/demo", context);
        client.sendMail("betterlucasxgq@outlook.com",process, "HTML");
    }
}
