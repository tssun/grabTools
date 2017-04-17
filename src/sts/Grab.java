package sts;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.Consts;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author ts.Sun (2017/4/11).
 */
public class Grab {
    private static CloseableHttpClient httpclient = HttpClients.createDefault();

    public static void login() throws IOException {
        //登录页面的URL
        String Loginurl = "http://jiaoyi.mttxe.com/index.php?s=/index/login.html";
        HttpGet httpGet = new HttpGet(Loginurl);
        CloseableHttpResponse response = httpclient.execute(httpGet);
        response.close();

        //提交登录
        List<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
        pairs.add(new BasicNameValuePair("login_name", "14751069060"));
        pairs.add(new BasicNameValuePair("password", "lxx2tyy"));
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(pairs, Consts.UTF_8);   //返回的实体
        HttpPost httppost = new HttpPost(Loginurl);
        httppost.setEntity(entity);

        String useraget = "User-Agent: Mozilla/5.0 (Linux; U; Android 2.3.7; en-us; Nexus One Build/FRF91) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1";
        httpclient = HttpClients.custom().setUserAgent(useraget).build();
        CloseableHttpResponse responseLogin = httpclient.execute(httppost);
        responseLogin.close();
    }

    public static String getHashValue() throws IOException {
        //取hash值
        HttpGet getinfo = new HttpGet("http://jiaoyi.mttxe.com/index.php?s=/user/producttrade/code/1003.html");
        CloseableHttpResponse res;
        res = httpclient.execute(getinfo);
        String html = EntityUtils.toString(res.getEntity());
        String hash = htmlFiter(html);
        res.close();
        return hash;
    }

    public static JsonData getJsonData() throws IOException {
        //取动态值
        HttpGet jsonGet = new HttpGet("http://jiaoyi.mttxe.com/index.php?s=/user/queryquote/code/1003.html");
        CloseableHttpResponse jsonData = httpclient.execute(jsonGet);
        String jsonString = EntityUtils.toString(jsonData.getEntity());
        jsonData.close();
        JsonData data = JSON.parseObject(jsonString, JsonData.class);
        return data;
    }

    public static String submitData(String price, String hash) throws IOException {
        //提交数据
        StringBuffer submitUrl = new StringBuffer("http://jiaoyi.mttxe.com/index.php?s=/user/yjia.html");
        StringBuffer dataUrl = new StringBuffer();
        dataUrl.append("price=").append(price)
                .append("&volume=").append("1")
                .append("&direct=").append("S")
                .append("&code=").append("1003")
                .append("&__hash__=").append(hash);
        HttpPost submitPost = new HttpPost(submitUrl.toString());
        List<BasicNameValuePair> subPairs = new ArrayList<BasicNameValuePair>();
        subPairs.add(new BasicNameValuePair("data", dataUrl.toString()));
        UrlEncodedFormEntity subEntity = new UrlEncodedFormEntity(subPairs, Consts.UTF_8);
        submitPost.setEntity(subEntity);
        CloseableHttpResponse submitData = httpclient.execute(submitPost);
        String submitString = EntityUtils.toString(submitData.getEntity(), "UTF-8");
        submitData.close();
        return submitString;
    }


    private static String htmlFiter(String html) {
        String result = "";
        String reg = "<input[^<>]*?\\stype=\"hidden\"\\sname=\"__hash__\"?(\\s.*?)?>";
        Matcher m = Pattern.compile(reg).matcher(html);
        while (m.find()) {
            result = m.group(0);
            break;
        }
        if (result != null && result != "") {
            result = result.substring(result.indexOf("value=\"") + 7, result.lastIndexOf("\""));
        } else {
            result = "";
        }
        return result;
    }

    public static void doGrab() {
        try {
            login();

            File file = new File("log.txt");
            int count = 0;
            for (int i = 0; i < 100; i++) {
                String hash = getHashValue();
//                System.out.println("hash:" + hash);

                JsonData jsonData = getJsonData();
                List<String> flowsList = jsonData.getFlows();
                Flows flows = JSON.parseObject(flowsList.get(0), Flows.class);
                String price = flows.getPrice();
                Thread.sleep(1000);

                String result = submitData(price, hash);
                System.out.println("result:" + result);
                FileUtils.fileWriter(result, file);
                //String result = "{\"status\":1,\"msg\":\"\\u4ea4\\u6613\\u6210\\u529f\"}";
                Map<String, String> json = JSON.parseObject(result, Map.class);
                if (json.get("status") == "1") {
                    count++;
                }
                Thread.sleep(1000);
            }
            String countResult = "抢到数量：" + count + "个";
            FileUtils.fileWriter(countResult, file);
            System.out.println(countResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        Runnable runnable = new Runnable() {
            int count = 1;
            public void run() {
                doGrab();
                System.out.println("执行了" + count + "次");
                count++;
            }
        };
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        // 第二个参数为首次执行的延时时间，第三个参数为定时执行的间隔时间
        service.scheduleAtFixedRate(runnable, 3, 60 * 5, TimeUnit.SECONDS);
    }
}
