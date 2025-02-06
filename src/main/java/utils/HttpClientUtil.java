package utils;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpClientUtil {
    private static final Logger log = LoggerFactory.getLogger(HttpClientUtil.class);

    public static String doGet(String url, Map<String, String> cookie) {
        String cookieHeader = cookie.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("; "));

        String result = "";
        try(CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader("Cookie", cookieHeader);
            HttpContext context = null;
            result = client.execute(httpGet, response -> {
                if(response.getCode() == 200)
                    return EntityUtils.toString(response.getEntity());
                return "";
            });
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return result;
    }

    public static String doPost(String url, String jsonBody) {
        String result = "";
        try(CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(url);
            StringEntity entity = new StringEntity(jsonBody, ContentType.APPLICATION_JSON);
            httpPost.setEntity(entity);
            result = client.execute(httpPost, response -> {
                if(response.getCode() == 200)
                    return EntityUtils.toString(response.getEntity());
                return "";
            });
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return result;
    }
}
