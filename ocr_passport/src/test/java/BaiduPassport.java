import com.baidu.aip.ocr.AipOcr;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * @ClassName BaiduPassport
 * @Description 百度原生接口
 * @Author shijie.lyu
 * @Date 2019-01-14 16:16
 * @Version 1.0
 */
public class BaiduPassport {

    private  static  String APP_ID ="5002331";
    private  static  String API_KEY ="nvW3faPZa9FQwccMtetoYH7";
    private  static  String SECRET_KEY ="6IAgLW6vpODXqif8fQeEXlCeXe2yspY5";

    public static void main(String[] args) {
        long start = System.currentTimeMillis();

        AipOcr client = new AipOcr(APP_ID, API_KEY, SECRET_KEY);

        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);
        HashMap<String, String> options = new HashMap<String, String>();
        // 参数为本地路径
        String image = "E:\\护照信息\\111\\26.jpg";
        JSONObject passport = client.passport(image, options);
        long end = System.currentTimeMillis();
        System.out.println((end-start));
        System.out.println(passport);
        float a = 0.1f;
        float b = 0.2f;
        System.out.println(a+b);
    }
}
