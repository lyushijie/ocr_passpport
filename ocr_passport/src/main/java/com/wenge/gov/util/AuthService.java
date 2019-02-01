package com.wenge.gov.util;

import com.wenge.gov.constans.WebConstans;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * @ClassName AuthService
 * @Description 获取token
 * @Author shijie.lyu
 * @Date 2019-01-10 8:45
 * @Version 1.0
 */
public class AuthService {
    /**
     * 获取权限token
     *
     * @return 返回示例：
     * {
     * "access_token": "24.460da4889caad24cccdb1fea17221975.2592000.1491995545.282335-1234567",
     * "expires_in": 2592000
     * }
     */
    public static String getAuth() {
        return getAuth(WebConstans.API_KEY, WebConstans.SECRET_KEY);
    }

    /**
     * 获取API访问token
     * 该token有一定的有效期，需要自行管理，当失效时需重新获取.
     *
     * @param ak - 百度云官网获取的 API Key
     * @param sk - 百度云官网获取的 Securet Key
     * @return assess_token 示例：
     * "24.460da4889caad24cccdb1fea17221975.2592000.1491995545.282335-1234567"
     */
    public static String getAuth(String ak, String sk) {
        // 获取token地址

        try {
            Connection connect = Jsoup.connect(WebConstans.AUTO_HOST);
            connect.data("grant_type", "client_credentials");
            connect.data("client_id", ak);
            connect.data("client_secret", sk);
            connect.ignoreContentType(true);
            Document document = connect.get();
            Element body = document.body();
            JSONObject jsonObject = new JSONObject(body.text());
            String access_token = jsonObject.getString("access_token");
            return access_token;
        } catch (Exception e) {
            System.err.printf("获取token失败！");
            e.printStackTrace(System.err);
        }
        return null;
    }

}
