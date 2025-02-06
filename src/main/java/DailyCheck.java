import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import utils.HttpClientUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class DailyCheck {
    public static final String CHECKIN = "https://apiv3.shanbay.com/uc/checkin";
    public static final String WXPUSH = "https://wxpusher.zjiecode.com/api/send/message";

    public static String checkStatus() throws IOException {
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

    public static void sendWxMessage(String content) throws IOException{
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

    public static void main(String[] args) throws Exception {
        String status = checkStatus();
        if(!"HAVE_CHECKIN".equals(status))
            sendWxMessage(status);
    }
}
