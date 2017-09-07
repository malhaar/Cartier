package xyz.vopen.cartier.commons.task;

import xyz.vopen.cartier.commons.email.CartierMail;

import javax.mail.Message;
import java.io.File;

/**
 * xyz.vopen.cartier.commons.task
 *
 * @author Elve.xu [xuhw@yyft.com]
 * @version v1.0 - 09/05/2017.
 */
public class EmailTest {

    public static void main (String[] args) {
        
        // 创建基本邮件基本配置
        final CartierMail.BasicMailConfig config = new CartierMail.BasicMailConfig.Builder("测试发件人名称")
                .hostAndPort("smtp.163.com", 25)
                .passwordAuthentication("<USERNAME>", "<PASSWORD>")
                .debug(true)
                .build();

        // 设置收件人地址
        String[] to = { "x_vivi@yeah.net" };

        // 创建和发送邮件
        new CartierMail.Builder(config)
                .subjectAndContent("测试邮件主题", "<html><body>测试邮件内容<b>加粗</b></body></html>")
                .address(Message.RecipientType.CC, to)
                .attachs(new File("/YOU_PATH_OF_YOUR_ATTACH"))
                .build()
                .send();
    }
    
}
