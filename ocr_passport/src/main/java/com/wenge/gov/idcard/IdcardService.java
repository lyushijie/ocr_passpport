package com.wenge.gov.idcard;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wenge.gov.common.model.po.Idcard;
import com.wenge.gov.common.model.po.PassportInfo;
import com.wenge.gov.constans.WebConstans;
import com.wenge.gov.util.AuthService;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @ClassName HttpServer
 * @Description 发送http请求
 * @Author shijie.lyu
 * @Date 2019-01-11 10:19
 * @Version 1.0
 */
public class IdcardService {
    protected Idcard dao = new Idcard().dao();

    public JSONObject getPassportInfoByClassinfier(String imgStr) {

        AuthService service = new AuthService();
        String access_token = service.getAuth();
        try {
            //创建一个client对象
            CloseableHttpClient client = HttpClients.createDefault();
            //创建httpPost对象
            HttpPost httpPost = new HttpPost(WebConstans.IDCARD_URL);
            //构造请求头
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
            //构造请求参数
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("access_token", access_token));
            nvps.add(new BasicNameValuePair("image", imgStr));
            nvps.add(new BasicNameValuePair("detect_direction", "true"));//是否检测图像旋转角度
            nvps.add(new BasicNameValuePair("id_card_side", "front"));//front：身份证含照片的一面；back：身份证带国徽的一面
            nvps.add(new BasicNameValuePair("detect_risk", "true"));//是否开启身份证风险类型
            httpPost.setEntity(new UrlEncodedFormEntity(nvps));
            //执行请求操作
            CloseableHttpResponse response = client.execute(httpPost);
            int code = response.getStatusLine().getStatusCode();
            if (code == 200) {    //请求成功
                HttpEntity entity = response.getEntity();
                JSONObject result = JSON.parseObject(EntityUtils.toString(entity));
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

    public Idcard parseIdcard(JSONObject json) {
        Idcard idcard = new Idcard();
        if (null != json && !json.isEmpty()) {
            System.out.println(json);
            String image_status = json.getString("image_status");
            if ("normal" != image_status) {
                JSONObject words_result = json.getJSONObject("words_result");
                if(null!=words_result){
                    String name = words_result.getJSONObject("姓名").getString("words");
                    String sex = words_result.getJSONObject("性别").getString("words");
                    String nation = words_result.getJSONObject("民族").getString("words");
                    String birth = words_result.getJSONObject("出生").getString("words");
                    String address = words_result.getJSONObject("住址").getString("words");
                    String id = words_result.getJSONObject("公民身份号码").getString("words");
                    idcard.setId(id);
                    idcard.setName(name);
                    idcard.setSex(sex);
                    idcard.setBirth(birth);
                    idcard.setNation(nation);
                    idcard.setAddress(address);
                }
            }
        }
        return idcard;
    }

}
