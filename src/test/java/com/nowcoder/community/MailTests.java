package com.nowcoder.community;

import com.nowcoder.community.utils.MailClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MailTests {

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Test
    public void testTextMail(){
        mailClient.sendMail("1910330021@qq.com", "TEST Mail~~~", "hello, canon!");
    }

    @Test
    public void testHtmlMail(){
        Context context = new Context();
        context.setVariable("username", "canon");

        String content = templateEngine.process("/mail/activation", context);
        System.out.println(content);

        mailClient.sendMail("1910330021@qq.com", "TEST HTML Mail~~~", content);
    }
}

