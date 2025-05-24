package com.learn.check;

import com.alibaba.fastjson2.JSONObject;
import com.learn.check.utils.HttpClientUtil;
import com.learn.check.utils.NotifyUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class DailyCheck {
    public static final String CHECKIN = "https://apiv3.shanbay.com/uc/checkin";

    public static String checkStatus() throws IOException {
        // 在resources文件夹下新建cookie.properties文件，将浏览器登录后的cookie复制到文件中
        Properties properties = new Properties();
        properties.load(ClassLoader.getSystemResourceAsStream("cookie.properties"));
        Map<String, String> cookie = new HashMap<>();
        properties.forEach((key, value) -> {
            cookie.put((String) key, (String) value);
        });
        String json = HttpClientUtil.doGet(CHECKIN, cookie);
        JSONObject jsonObject = JSONObject.parseObject(json);
        String date = jsonObject.getString("date");
        String checkinDaysNum = jsonObject.getString("checkin_days_num");
        String status = jsonObject.getString("status");
        // FORBIDDEN_CHECKIN_ANY  NOT_YET_CHECKIN 未打卡
        // HAVE_CHECKIN 已打卡
        return status;
    }

    public static void main(String[] args) throws Exception {
        String status = "";
        try {
            status = checkStatus();
        } catch (Exception e) {
            NotifyUtil.sendWxMessage(e.getMessage());
            NotifyUtil.sendMail("ShanbayDailCheck Exception", e.getMessage());
            throw new RuntimeException(e);
        }
        if(!"HAVE_CHECKIN".equals(status)) {
            NotifyUtil.sendMail(null, status);
            NotifyUtil.sendWxMessage(status);
        }
    }
}
