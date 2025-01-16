import com.alibaba.fastjson2.JSONObject;
import util.HttpClientUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class DailyCheck {
    public static final String CHECKIN = "https://apiv3.shanbay.com/uc/checkin";

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

    public static void main(String[] args) throws Exception {

    }
}
