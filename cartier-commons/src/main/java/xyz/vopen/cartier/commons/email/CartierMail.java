package xyz.vopen.cartier.commons.email;


import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * <p>
 * 邮件发送客户端(支持 HTML , 文本 , 附件等)
 * </p>
 * <p>
 * 使用方式:
 * <pre>
 *
 *        // 创建基本邮件基本配置
 *        final CartierMail.BasicMailConfig config = new CartierMail.BasicMailConfig.Builder("测试发件人名称")
 *                   .hostAndPort("smtp.163.com", 25)
 *                   .passwordAuthentication("#USERNAME#", "#PASSWORD#")
 *                   .debug(true)
 *                   .build();
 *        // 设置收件人地址
 *        String[] to = { "x_vivi@yeah.net" };
 *
 *        // 创建和发送邮件
 *        new CartierMail.Builder(config)
 *                   .subjectAndContent("测试邮件主题", "测试邮件内容")
 *                   .address(Message.RecipientType.CC, to)
 *                   .attachs(new File("/YOU_PATH_OF_YOUR_ATTACH"))
 *                   .build()
 *               .send();
 *
 *     </pre>
 * <p>
 * </p>
 *
 * @author Elve.xu [xuhw@yyft.com]
 * @version v1.1 - 08/05/2017.
 */
public final class CartierMail {

    public static class Builder implements Serializable {

        private BasicMailConfig config;
        private String subject;
        private String content;
        private Map<Message.RecipientType, Address[]> to = new HashMap<>();
        private List<FileDataSource> fileDataSources = new ArrayList<>();


        public Builder (BasicMailConfig config) {
            this.config = config;
        }

        public Builder subjectAndContent (String subject, String content) {
            this.subject = subject;
            this.content = content;
            return this;
        }

        /**
         * 设置邮件发送的收件对象
         *
         * @param type
         *         收件对象类型
         * @param addresses
         *         收件对象地址
         *
         * @return
         */
        public Builder address (Message.RecipientType type, Address[] addresses) {
            if (addresses != null && addresses.length > 0) {
                to.put(type, addresses);
            }
            return this;
        }

        public Builder address (Message.RecipientType type, String[] addresses) {
            if (addresses != null && addresses.length > 0) {
                Address[] addresses1 = new Address[addresses.length];
                for (int i = 0; i < addresses.length; i++) {
                    try {
                        addresses1[i] = new InternetAddress(addresses[i]);
                    } catch (AddressException e) {
                        e.printStackTrace();
                    }
                }
                to.put(type, addresses1);
            }

            return this;
        }

        /**
         * 附件
         */
        public Builder attachs (File... files) {
            if (files != null && files.length > 0) {
                for (int i = 0; i < files.length; i++) {
                    fileDataSources.add(new FileDataSource(files[i]));
                }
            }
            return this;
        }

        public CartierMail build () {

            // check    
            if (to == null || to.size() == 0) {
                throw new RuntimeException("错误:设置收件人的邮件地址");
            }


            return new CartierMail(this);
        }
    }


    private CartierMail (Builder builder) {

        this.config = builder.config;
        this.subject = builder.subject;
        this.content = builder.content;
        this.to = builder.to;
        this.fileDataSources = builder.fileDataSources;

        properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.debug", config.getDebug());
        properties.put("mail.smtp.host", config.getSmtpHost());
        properties.put("mail.smtp.port", config.getSmtpPort());
        properties.put("mail.user", config.getMailUserName());
        properties.put("mail.password", config.getMailPassword());
    }


    private BasicMailConfig config;
    private Properties properties;

    // 消息
    private String subject;

    private String content;
    // 收件人
    private Map<Message.RecipientType, Address[]> to;

    private List<FileDataSource> fileDataSources;

    /**
     * Mail Send Method
     */
    public void send () {

        try {
            // 构建授权信息，用于进行SMTP进行身份验证
            Authenticator authenticator = new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication () {
                    // 用户名、密码
                    return new PasswordAuthentication(config.getMailUserName(), config.getMailPassword());
                }
            };

            // 使用环境属性和授权信息，创建邮件会话
            Session mailSession = Session.getInstance(properties, authenticator);
            // 创建邮件消息
            MimeMessage message = new MimeMessage(mailSession);

            String nickName = MimeUtility.encodeText(config.getMailSenderName());
            InternetAddress form = new InternetAddress(nickName + " <" + config.getMailUserName() + ">");
            message.setFrom(form);

            message.setSubject(subject);
            MimeMultipart mimeMultipart = new MimeMultipart();

            // set Recipient
            for (Map.Entry<Message.RecipientType, Address[]> entry : to.entrySet()) {
                Message.RecipientType key = entry.getKey();
                Address[] addresses = entry.getValue();

                for (Address address : addresses) {
                    message.setRecipient(key, address);
                }
            }

            // add content
            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setContent(content, "text/html;charset=utf-8");
            mimeMultipart.addBodyPart(mimeBodyPart);

            // set attach
            if (fileDataSources != null && fileDataSources.size() > 0) {
                for (FileDataSource fileDataSource : fileDataSources) {
                    mimeBodyPart = new MimeBodyPart();
                    mimeBodyPart.setDataHandler(new DataHandler(fileDataSource));
                    mimeBodyPart.setFileName(MimeUtility.encodeText(fileDataSource.getName(), "UTF-8", "B"));
                    mimeMultipart.addBodyPart(mimeBodyPart);
                }
            }

            message.setContent(mimeMultipart);

            // send
            Transport.send(message);

        } catch (Exception ignored) {
            System.out.println("ERROR: 邮件发送异常, 异常: " + ignored.getMessage());
        }
    }


    public static class BasicMailConfig implements Serializable {
        private final String smtpHost;

        private final Integer smtpPort;

        private final String mailUserName;

        private final String mailPassword;

        private final String mailSenderName;

        private final Boolean debug;

        private BasicMailConfig (Builder builder) {
            this.debug = builder.debug;
            this.smtpHost = builder.smtpHost;
            this.smtpPort = builder.smtpPort;
            this.mailUserName = builder.mailUserName;
            this.mailPassword = builder.mailPassword;
            this.mailSenderName = builder.mailSenderName;
        }

        public static class Builder implements Serializable {

            private String smtpHost;

            private Integer smtpPort;

            private String mailUserName;

            private String mailPassword;

            private String mailSenderName;

            private Boolean debug;

            public Builder (String name) {
                this.mailSenderName = name;
            }

            public Builder hostAndPort (String smtpHost, Integer smtpPort) {
                this.smtpHost = smtpHost;
                this.smtpPort = smtpPort;
                return this;
            }

            public Builder debug (Boolean debug) {
                this.debug = debug;
                return this;
            }

            public Builder passwordAuthentication (String mailUserName, String mailPassword) {
                this.mailUserName = mailUserName;
                this.mailPassword = mailPassword;
                return this;
            }


            public BasicMailConfig build () {
                return new BasicMailConfig(this);
            }
        }


        public String getSmtpHost () {
            return smtpHost;
        }

        public Integer getSmtpPort () {
            return smtpPort;
        }

        public String getMailUserName () {
            return mailUserName;
        }

        public String getMailPassword () {
            return mailPassword;
        }

        public String getMailSenderName () {
            return mailSenderName;
        }

        public Boolean getDebug () {
            return debug;
        }
    }
}
