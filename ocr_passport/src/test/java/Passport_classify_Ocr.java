import com.baidu.aip.util.Base64Util;
import com.wenge.gov.util.AuthService;
import com.wenge.gov.util.FileUtil;
import com.wenge.gov.util.HttpUtil;

import java.net.URLEncoder;

/**
 * @ClassName Passport_classify_Ocr
 * @Description 调用自己创建的模板
 * @Author shijie.lyu
 * @Date 2019-01-09 15:58
 * @Version 1.0
 */
public class Passport_classify_Ocr {

    public static void main(String[] args) {
       // String url ="https://aip.baidubce.com/rest/2.0/solution/v1/iocr/recognise";
        String url ="https://aip.baidubce.com/rest/2.0/ocr/v1/passport";
        AuthService service = new AuthService();
        String access_token = service.getAuth();
        String filePath ="E:\\护照信息\\111\\50.jpg";
        try {
            byte[] imgData = FileUtil.readFileByBytes(filePath);
            String imgStr = Base64Util.encode(imgData);
            String params = "classifierId=1&"+URLEncoder.encode("image", "UTF-8") + "=" + URLEncoder.encode(imgStr, "UTF-8");
            String post1 = HttpUtil.post(url, access_token, "application/x-www-form-urlencoded", params);
            System.out.println(post1);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
