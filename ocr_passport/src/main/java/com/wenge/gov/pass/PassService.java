package com.wenge.gov.pass;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wenge.gov.common.model.po.Idcard;
import com.wenge.gov.common.model.po.Pass;
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
public class PassService {
    protected Pass dao = new Pass().dao();

    public JSONObject getPassInfoByRecognise(String imgStr) {

        AuthService service = new AuthService();
        String access_token = service.getAuth();
        try {
            //创建一个client对象
            CloseableHttpClient client = HttpClients.createDefault();
            //创建httpPost对象
            HttpPost httpPost = new HttpPost(WebConstans.RECOGNISE_URL);
            //构造请求头
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
            //构造请求参数
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("access_token", access_token));
            nvps.add(new BasicNameValuePair("image", imgStr));
            nvps.add(new BasicNameValuePair("templateSign", WebConstans.TEMPLATESIGN_PASS_ID));//模板ID
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

    public Pass parsePass(JSONObject json) {
        System.out.println(json);
        Pass pass = new Pass();
        String machineCode = null;
        if (null != json && !json.isEmpty()) {
            Integer error_code = json.getInteger("error_code");
            if (0 == error_code) {
                JSONArray ret = json.getJSONObject("data").getJSONArray("ret");
                if (null != ret && !ret.isEmpty()) {
                    for (int i = 0; i < ret.size(); i++) {
                        JSONObject item = (JSONObject) ret.get(i);
                        String word_name = item.getString("word_name");
                        String word = item.getString("word");
                        switch (word_name) {
                            case "通行证ID": {
                                if (null != word && !word.isEmpty()) {
                                    if (word.contains("O")) {
                                        word = word.replaceAll("O", "0");
                                    }
                                    if (word.contains("I")) {
                                        word = word.replaceAll("I", "1");
                                    }
                                    pass.setId(word.trim().toUpperCase());
                                }

                                break;
                            }
                            case "英文名": {
                                if (null != word && !word.isEmpty()) {
                                    try {
                                        if (word.contains(",")) {
                                            word = word.replaceAll(",", ".");
                                        }
                                        if (word.contains("，")) {
                                            word = word.replaceAll("，", ".");
                                        }
                                        if (word.contains(" ")) {
                                            word = word.replaceAll(" ", ".");
                                        }
                                        if (word.contains("..")) {
                                            word = word.replaceAll("\\.\\.", ".");
                                        }
                                        if (word.contains(".")) {
                                            String[] wordSplit = word.split("\\.");
                                            String surName = wordSplit[0].trim().toUpperCase();
                                            String givenName = wordSplit[1].trim().toUpperCase();
                                            if (surName.contains("<")) {
                                                surName = surName.replaceAll("<", " ");
                                            }
                                            if (surName.contains("0")) {
                                                surName = surName.replaceAll("0", "O");
                                            }
                                            if (surName.contains("1")) {
                                                surName = surName.replaceAll("1", "I");
                                            }

                                            if (givenName.contains("<")) {
                                                givenName = givenName.replaceAll("<", " ");
                                            }
                                            if (givenName.contains("0")) {
                                                givenName = givenName.replaceAll("0", "O");
                                            }
                                            if (givenName.contains("1")) {
                                                givenName = givenName.replaceAll("1", "I");
                                            }
                                            pass.setSurname(surName);
                                            pass.setGivenname(givenName);
                                        }
                                    } catch (Exception e) {
                                        System.out.println(e);
                                    }
                                }
                                break;
                            }
                            case "出生日期": {
                                if (null != word && !word.isEmpty()) {
                                    if (word.contains(".")) {
                                        word = word.replaceAll("\\.", "");
                                    }
                                    if (word.contains(" ")) {
                                        word = word.replaceAll(" ", "");
                                    }
                                    if (word.contains(",")) {
                                        word = word.replaceAll(",", "");
                                    }
                                    if (word.length() == 8) {
                                        String birth = formatter(word, "yyyyMMdd");
                                        pass.setBirth(birth);
                                    }
                                }
                                break;
                            }
                            case "性别": {
                                if (null != word && !word.isEmpty()) {
                                    if ("男".equals(word)) {
                                        pass.setSex("M");
                                    } else if ("女".equals(word)) {
                                        pass.setSex("F");
                                    }
                                }
                                break;
                            }
                            case "有效期限": {
                                if (null != word && !word.isEmpty()) {
                                    if (word.contains(".")) {
                                        word = word.replaceAll("\\.", "");
                                    }
                                    if (word.contains(" ")) {
                                        word = word.replaceAll(" ", "");
                                    }
                                    if (word.contains(",")) {
                                        word = word.replaceAll(",", "");
                                    }
                                    if (word.contains("-")) {
                                        String[] expiry = word.split("-");
                                        if (expiry[1].length() == 8) {
                                            String birth = formatter(expiry[1], "yyyyMMdd");
                                            pass.setExpiry(birth);
                                        }
                                    }
                                }
                                break;
                            }
                            case "机读码": {
                                if (null != word && !word.isEmpty()) {
                                    if (30 == word.length()) {
                                        if (word.contains("O")) {
                                            word = word.replaceAll("O", "0");
                                        }
                                        if (word.contains("I")) {
                                            word = word.replaceAll("I", "1");
                                        }
                                        machineCode = word;
                                    }
                                }
                                break;
                            }
                            case "目的地": {

                                if (null != word && !word.isEmpty()) {
                                    pass.setDestination(word);
                                }
                                break;
                            }

                        }

                    }
                }

            }
        }
        //解析机读码校验
        if (null != machineCode && !machineCode.isEmpty()) {
            String[] codes = machineCode.split("<");
            String id = codes[0];
            String expity = codes[1];
            String birth = codes[2];
            if(id.length()==12){
                id = id.substring(2,id.length()-1);
            }
            if(expity.length()==7){
                expity = expity.substring(0,expity.length()-1);
            }
            if(birth.length()==7){
                birth = birth.substring(0,birth.length()-1);
            }
            expity = formatter(expity,"yyMMdd");
            birth = formatter(birth,"yyMMdd");
            if(!id.equals(pass.getId())){
                pass.setId(id);
            }
            if(!expity.equals(pass.getExpiry())){
                pass.setExpiry(expity);
            }
            if(!birth.equals(pass.getBirth())){
                pass.setBirth(birth);
            }
        }
        return pass;
    }

    public String formatter(String date,String format) {
        String result = null;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
            LocalDate dt = LocalDate.parse(date, formatter);
            result = dt.format(DateTimeFormatter.ofPattern("ddMMMyy", Locale.US)).toUpperCase();

        } catch (Exception e) {
            System.out.println("格式转换错误，日期为：" + date);
        }
        return result;
    }

}
