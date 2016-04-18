package cn.edu.shou.missive.service;


import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.util.Date;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

/**
 * Created by seky on 14/12/2.
 */
@Component
public class SimpleMailSender {
    @Value("${spring.main.emailHost}")
    String emailHost;
    @Value("${spring.main.emailUserName}")
    String emailUserName;
    @Value("${spring.main.emailPassword}")
    String emailPassword;

    String emailTemplate="emailTemplate";
    String missiveFlag="$missiveTitle$";//公文标题标记
    String missiveUrl="#";//公文链接地址
    @Autowired
    private DeployRepository deR;


    //sping mail 简单发送文本邮件
    public boolean SingleMailSend(){
        JavaMailSenderImpl senderImpl = new JavaMailSenderImpl();
        // 设定mail server
        senderImpl.setHost(emailHost);
        senderImpl.setPort(25);

        // 建立邮件消息
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        // 设置收件人，寄件人 用数组发送多个邮件
        // String[] array = new String[] {"sun111@163.com","sun222@sohu.com"};
        // mailMessage.setTo(array);
        mailMessage.setTo("xlzheng@shou.edu.cn");
        mailMessage.setFrom(emailUserName);
        mailMessage.setSubject(" 测试简单文本邮件发送！ ");
        mailMessage.setText(" 测试我的简单邮件发送机制！！ ");

        senderImpl.setUsername(emailUserName); // 根据自己的情况,设置username
        senderImpl.setPassword(emailPassword); // 根据自己的情况, 设置password

        Properties prop = new Properties();
        prop.put("mail.smtp.auth", "true"); // 将这个参数设为true，让服务器进行认证,认证用户名和密码是否正确
        prop.put("mail.smtp.timeout", "25000");
        senderImpl.setJavaMailProperties(prop);
        // 发送邮件

        senderImpl.send(mailMessage);

        System.out.println(" 邮件发送成功.. ");

        return true;
    }
    //sping mail 发送html邮件
    public boolean HTMLMailSend(String emailTitle,String missiveUrlStr,String[] emailUsers){
        JavaMailSenderImpl senderImpl = new JavaMailSenderImpl();
        String emailText="";

        emailText=deR.getEmailTemplate(emailTemplate);//获取公文模板
        if(emailText.contains(missiveFlag)) {
            emailText = emailText.replace(missiveFlag, emailTitle);//模板里面的公文标题标记替换
        }
        if(emailText.contains(missiveUrl)) {
            emailText = emailText.replace(missiveUrl, missiveUrlStr);//模板里面的公文链接
        }
        // 设定mail server
        senderImpl.setHost(emailHost);
        senderImpl.setPort(25);
        // 建立邮件消息,发送简单邮件和html邮件的区别
        MimeMessage mailMessage = senderImpl.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mailMessage,"GBK");


        try {
            // 设置收件人，寄件人
            messageHelper.setTo(emailUsers);
            messageHelper.setFrom(emailUserName);
            messageHelper.setSubject(emailTitle);
            // true 表示启动HTML格式的邮件
            messageHelper
                    .setText(
                            emailText,
                            true);
            senderImpl.setUsername(emailUserName); // 根据自己的情况,设置username
            senderImpl.setPassword(emailPassword); // 根据自己的情况, 设置password
            Properties prop = new Properties();
            prop.put("mail.smtp.auth", "true"); // 将这个参数设为true，让服务器进行认证,认证用户名和密码是否正确
            prop.put("mail.smtp.timeout", "25000");
            senderImpl.setJavaMailProperties(prop);
            // 发送邮件
            senderImpl.send(mailMessage);

            System.out.println("邮件发送成功..");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return true;
    }
    //sping mail 发送嵌套图片的邮件 说明：嵌入图片<img src=\"cid:aaa\"/>，其中cid:是固定的写法，而aaa是一个contentId
    public boolean AttachedImageMail(){
        JavaMailSenderImpl senderImpl = new JavaMailSenderImpl();

        // 设定mail server
        senderImpl.setHost("smtp.163.com");

        // 建立邮件消息,发送简单邮件和html邮件的区别
        MimeMessage mailMessage = senderImpl.createMimeMessage();
        // 注意这里的boolean,等于真的时候才能嵌套图片，在构建MimeMessageHelper时候，所给定的值是true表示启用，
        // multipart模式
        MimeMessageHelper messageHelper = null;
        try {
            messageHelper = new MimeMessageHelper(mailMessage,
                    true);
            // 设置收件人，寄件人
            messageHelper.setTo("toMail@sina.com");
            messageHelper.setFrom("username@163.com");
            messageHelper.setSubject("测试邮件中嵌套图片!！");
            // true 表示启动HTML格式的邮件
            messageHelper.setText(
                    "<html><head></head><body><h1>hello!!spring image html mail</h1>"
                            + "<img src=\"cid:aaa\"/></body></html>", true);

            FileSystemResource img = new FileSystemResource(new File("g:/123.jpg"));

            messageHelper.addInline("aaa", img);

            senderImpl.setUsername("username"); // 根据自己的情况,设置username
            senderImpl.setPassword("password"); // 根据自己的情况, 设置password
            Properties prop = new Properties();
            prop.put("mail.smtp.auth", "true"); // 将这个参数设为true，让服务器进行认证,认证用户名和密码是否正确
            prop.put("mail.smtp.timeout", "25000");
            senderImpl.setJavaMailProperties(prop);

            // 发送邮件
            senderImpl.send(mailMessage);

            System.out.println("邮件发送成功..");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return true;
    }
    //发送包含附件的邮件
    public boolean AttachedFileMail(){
        try{
            JavaMailSenderImpl senderImpl = new JavaMailSenderImpl();

            // 设定mail server
            senderImpl.setHost("smtp.163.com");
            // 建立邮件消息,发送简单邮件和html邮件的区别
            MimeMessage mailMessage = senderImpl.createMimeMessage();
            // 注意这里的boolean,等于真的时候才能嵌套图片，在构建MimeMessageHelper时候，所给定的值是true表示启用，
            // multipart模式 为true时发送附件 可以设置html格式
            MimeMessageHelper messageHelper = new MimeMessageHelper(mailMessage,
                    true, "utf-8");

            // 设置收件人，寄件人
            messageHelper.setTo("toMail@sina.com");
            messageHelper.setFrom("username@163.com");
            messageHelper.setSubject("测试邮件中上传附件!！");
            // true 表示启动HTML格式的邮件
            messageHelper.setText(
                    "<html><head></head><body><h1>你好：附件中有学习资料！</h1></body></html>",
                    true);

            FileSystemResource file = new FileSystemResource(
                    new File("g:/test.rar"));
            // 这里的方法调用和插入图片是不同的。
            messageHelper.addAttachment("test.rar", file);

            senderImpl.setUsername("username"); // 根据自己的情况,设置username
            senderImpl.setPassword("password"); // 根据自己的情况, 设置password
            Properties prop = new Properties();
            prop.put("mail.smtp.auth", "true"); // 将这个参数设为true，让服务器进行认证,认证用户名和密码是否正确
            prop.put("mail.smtp.timeout", "25000");
            senderImpl.setJavaMailProperties(prop);
            // 发送邮件
            senderImpl.send(mailMessage);

            System.out.println("邮件发送成功..");
        }catch (MessagingException e){
            e.printStackTrace();
        }
        return true;
    }
}
