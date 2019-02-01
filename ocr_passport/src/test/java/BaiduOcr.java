import com.baidu.aip.ocr.AipOcr;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * @ClassName BaiduOcr
 * @Description 测试连接百度API接口
 * @Author shijie.lyu
 * @Date 2019-01-09 15:38
 * @Version 1.0
 */
public class BaiduOcr {
    public static final String APP_ID = "15376904";
    public static final String API_KEY = "hxTG79TUjCG69kd5FmflOHpl";
    public static final String SECRET_KEY = "3tyZCfYRV4KT9ELviC77l2MepEb5KwRL";

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        // 初始化一个AipOcr
        AipOcr client = new AipOcr(APP_ID, API_KEY, SECRET_KEY);

        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);

        // 可选：设置代理服务器地址, http和socket二选一，或者均不设置
        //client.setHttpProxy("proxy_host", proxy_port);  // 设置http代理
        // client.setSocketProxy("proxy_host", proxy_port);  // 设置socket代理

        // 可选：设置log4j日志输出格式，若不设置，则使用默认配置
        // 也可以直接通过jvm启动参数设置此环境变量
        // System.setProperty("aip.log4j.conf", "path/to/your/log4j.properties");

        // 调用接口
        /*String path = "E:\\护照信息\\111\\33.jpg";
        JSONObject res = client.basicGeneral(path, new HashMap<String, String>());
        System.out.println(res.toString(2));
        System.out.println(res);*/

        String templateSign = "a8f5c06d0c7b5ac2e5d801e026d200a1";
        int classifierId = 1;
        HashMap<String, String> options = new HashMap<String, String>();


        // 参数为本地路径
        String image = "E:\\护照信息\\111.jpg";
        JSONObject passport = client.passport(image, options);
        long end = System.currentTimeMillis();
        System.out.println((end-start));
        System.out.println(passport);


    }
}
