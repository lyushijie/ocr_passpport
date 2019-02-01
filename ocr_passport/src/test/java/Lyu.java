import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baidu.aip.util.Base64Util;
import com.wenge.gov.constans.WebConstans;
import com.wenge.gov.util.AuthService;
import com.wenge.gov.util.FileUtil;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName Lyu
 * @Description 测试httpclient
 * @Author shijie.lyu
 * @Date 2019-01-11 9:29
 * @Version 1.0
 */
public class Lyu {
    public static JSONObject getPassport() {
        String url = "https://aip.baidubce.com/rest/2.0/solution/v1/iocr/recognise";
        String general_basic = "https://aip.baidubce.com/rest/2.0/ocr/v1/general_basic";
        String passport = "https://aip.baidubce.com/rest/2.0/ocr/v1/passport";
        AuthService service = new AuthService();
        String access_token = service.getAuth();
        String filePath = "E:\\护照信息\\训练\\新护照\\20190109_1\\4.jpg";
        long start = System.currentTimeMillis();
        byte[] imgData = new byte[0];
        try {
            imgData = FileUtil.readFileByBytes(filePath);
            String imgStr = Base64Util.encode(imgData);

            //创建一个client对象
            CloseableHttpClient client = HttpClients.createDefault();
            //创建httpPost对象
            HttpPost httpPost = new HttpPost(url);
            //构造请求头
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
            //构造请求参数
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("access_token", access_token));
            nvps.add(new BasicNameValuePair("image", imgStr));
            nvps.add(new BasicNameValuePair("classifierId", "1"));
            //nvps.add(new BasicNameValuePair("detect_direction", "true"));
            httpPost.setEntity(new UrlEncodedFormEntity(nvps));

            //执行请求操作
            CloseableHttpResponse response = client.execute(httpPost);
            int code = response.getStatusLine().getStatusCode();
            if (code == 200) {    //请求成功
                HttpEntity entity = response.getEntity();
                JSONObject result = JSON.parseObject(EntityUtils.toString(entity));
                long end = System.currentTimeMillis();
                System.out.println((end-start));
                System.out.println(result);
                return result;
            } else {
                System.out.println("状态码：" + code);
                return null;
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        getPassport();
    }
}
