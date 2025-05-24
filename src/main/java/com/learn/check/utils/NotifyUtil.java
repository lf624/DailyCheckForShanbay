package com.learn.check.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;

import java.io.IOException;
import java.util.*;

public class NotifyUtil {
    public static final String WXPUSH = "https://wxpusher.zjiecode.com/api/send/message";

    public static void sendWxMessage(String content) throws IOException {
        // 在resources文件夹下新建wxpusher.properties文件，并增加appToken和uid属性
        Properties properties = new Properties();
        properties.load(ClassLoader.getSystemResourceAsStream("wxpusher.properties"));
        Map<String, Object> body = new HashMap<>();
        body.put("appToken", properties.get("appToken"));
        body.put("uids", List.of(properties.get("uid")));
        body.put("content", content);
        body.put("summary", "扇贝打卡提醒");
        body.put("contentType", 1);
        String json = JSON.toJSONString(body);
        String result = HttpClientUtil.doPost(WXPUSH, json);
        JSONObject jsonObject = JSONObject.parseObject(result);
        if(!"1000".equals(jsonObject.getString("code")))
            throw new RuntimeException(result);
    }

    public static void sendMail(String subject, String text) throws IOException{
        // 在resources文件夹下新建mail.properties文件
        // 并增加：mail.smtp.host=smtp.xxxx
        // mail.smtp.port=587
        // mail.smtp.auth=true
        // mail.smtp.starttls.enable=true
        // mail.smtp.username=xxx@xxx.com
        // mail.smtp.password=xxxx
        // mail.smtp.to=xxxx@xxx.com
        Properties properties = new Properties();
        properties.load(ClassLoader.getSystemResourceAsStream("mail.properties"));
        String username = (String) properties.remove("mail.smtp.username");
        String password = (String) properties.remove("mail.smtp.password");
        String toMail = (String) properties.remove("mail.smtp.to");
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        session.setDebug(false);

        // 发送邮件
        Message message = new MimeMessage(session);
        try{
            message.setFrom(new InternetAddress(username));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(toMail));
            // 设置邮件主题
            message.setSubject(subject == null ? "扇贝打卡提醒" : subject);
            // 设置邮件正文
            message.setText(text);
            Transport.send(message);
        }catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
