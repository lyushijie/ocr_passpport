package com.wenge.gov.passport;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baidu.aip.util.Base64Util;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.PropKit;
import com.wenge.gov.common.model.po.PassportInfo;
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
import sun.misc.BASE64Decoder;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @ClassName HttpServer
 * @Description 发送http请求
 * @Author shijie.lyu
 * @Date 2019-01-11 10:19
 * @Version 1.0
 */
public class PassportService {
    protected PassportInfo dao = new PassportInfo().dao();

    public JSONObject getPassportInfoByClassinfier(String imgStr) {

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
            nvps.add(new BasicNameValuePair("classifierId", WebConstans.CLASSIFIERID));
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

/**
 * @Description 调用通用文字识别
 * @param imgStr
 * @return com.alibaba.fastjson.JSONObject
 * @Author shijie.lyu
 * @Date 2019/1/21 21:54
 */
    public JSONObject getPassportInfoByGeneralBasic(String imgStr) {

        AuthService service = new AuthService();
        String access_token = service.getAuth();
        try {
            //创建一个client对象
            CloseableHttpClient client = HttpClients.createDefault();
            //创建httpPost对象
            HttpPost httpPost = new HttpPost(WebConstans.GENERAL_BASIC_URL);
            //构造请求头
            //httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
            //构造请求参数
            List<NameValuePair> nvps = new ArrayList<>();
            nvps.add(new BasicNameValuePair("access_token", access_token));
            nvps.add(new BasicNameValuePair("image", imgStr));
            nvps.add(new BasicNameValuePair("detect_direction", "true"));
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

    /**
     * @return java.util.List<java.lang.String>
     * @Description 获取文本中的护照码
     * @Param [basicJson]
     * @Author shijie.lyu
     * @Date 2019/1/12 12:36
     */
    public List<String> catOutVaildInfoByGeneralBasic(JSONObject basicJson) {
        List<String> resultList = new ArrayList<>();
        if (null != basicJson && !basicJson.isEmpty()) {
            JSONArray words_result = basicJson.getJSONArray("words_result");
            if (null != words_result && !words_result.isEmpty()) {
                for (int i = 0; i < words_result.size(); i++) {
                    JSONObject word = (JSONObject) words_result.get(i);
                    String words = word.getString("words").trim();
                    if (null != words && !words.isEmpty()) {
                        words = words.replaceAll(" ", "");
                        if (words.contains("《")) {
                            words = words.replaceAll("《", "<");
                        }
                        if(words.length()>44){
                            words = words.substring(0,44);
                        }
                        //判断第一条机读码
                        if (words.length()>10 && words.contains("<<") && words.matches(".*[a-zA-z].*")) {
                            resultList.add(words);
                            continue;
                        }
                        //判断第二条机读码
                        if (words.length()>27 && words.matches("[A-Za-z].*[0-9]")) {
                            resultList.add(words);
                            continue;
                        }
                    }
                }
            }
        }
        if (null == resultList || resultList.size() != 2) {
            return null;
        }
        return resultList;
    }

    /**
     * @return com.wenge.gov.common.model.po.PassportInfo
     * @Description 格式化护照信息格式
     * @Param [list]
     * @Author shijie.lyu
     * @Date 2019/1/12 22:25
     */
    public PassportInfo formatGeneralBasic(List<String> list) {
        PassportInfo passportInfo = new PassportInfo();
        if (null != list && !list.isEmpty()) {
            String firstValid = list.get(0).toUpperCase();
            String secondValid = list.get(1).toUpperCase();
            //解析护照第一行有效信息，包含 姓、名、护照类型、国家码
            if (firstValid.length() >= 10) {
                String[] split = firstValid.split("<<");
                String a = split[0];
                String type = a.substring(0, 1);
                passportInfo.setType(type);
                String code = a.substring(2, 5);
                passportInfo.setCountryCode(code);
                String surname = a.substring(5, a.length());
                if (surname.contains("<")) {
                    surname = surname.replaceAll("<", " ");
                }
                //字母O和数字0识别的不准确，需要做一下判断
                if (surname.contains("0")) {
                    surname = surname.replaceAll("0", "O");
                }
                if (surname.contains("1")) {
                    surname = surname.replaceAll("1", "I");
                }
                passportInfo.setSurname(surname);
                String givenname = split[1];
                if (givenname.contains("<")) {
                    givenname = givenname.replaceAll("<", " ");
                }
                if (givenname.contains("0")) {
                    givenname = givenname.replaceAll("0", "O");
                }
                if (givenname.contains("1")) {
                    givenname = givenname.replaceAll("1", "I");
                }
                passportInfo.setGivenname(givenname);
            }

            //解析护照第二行有效信息，包含 护照号、国家码、性别、出生日期、护照有效期
            if (secondValid.length() >= 27) {
                String pid = secondValid.substring(0, 9);
                //护照号中不存在字母O
                if (pid.contains("O")) {
                    pid = pid.replaceAll("O", "0");
                }
                if (pid.contains("I")) {
                    pid = pid.replaceAll("I", "1");
                }
                passportInfo.setPid(pid);
                String code2 = secondValid.substring(10, 13);
                passportInfo.setCountryCode(code2);
                String birth = secondValid.substring(13, 19);
                //日期把识别的字母O转换为数字0
                if (birth.contains("O")) {
                    birth = birth.replaceAll("O", "0");
                }
                passportInfo.setBirthDate(formatter(birth));
                String sex = secondValid.substring(20, 21);
                passportInfo.setSex(sex);
                String expiry = secondValid.substring(21, 27);
                if (expiry.contains("O")) {
                    expiry = expiry.replaceAll("O", "0");
                }
                passportInfo.setExpiryDate(formatter(expiry));
            }
        }
        if (null != passportInfo) {
            String pid = passportInfo.getPid();
            if (null != pid && !pid.isEmpty()) {
                PassportInfo info = dao.findById(pid);
                if (null == info) {
                    passportInfo.save();
                } else {
                    passportInfo.update();
                }
            }
        }
        return passportInfo;
    }

    /**
     * @param date
     * @return java.lang.String
     * @Description 护照时间格式化
     * @Author shijie.lyu
     * @Date 2019/1/13 14:11
     */
    public String formatter(String date) {
        String result = null;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMdd");
            LocalDate dt = LocalDate.parse(date, formatter);
            result = dt.format(DateTimeFormatter.ofPattern("ddMMMyy", Locale.US)).toUpperCase();

        } catch (Exception e) {
            System.out.println("格式转换错误，日期为：" + date);
        }
        return result;
    }
/**
 * @Description 解析模板识别
 * @param classinfierJson
 * @return com.wenge.gov.common.model.po.PassportInfo
 * @Author shijie.lyu
 * @Date 2019/1/21 21:54
 */
    public PassportInfo parseClassinfier(JSONObject classinfierJson) {
        PassportInfo passportInfo = new PassportInfo();
        if (null != classinfierJson && !classinfierJson.isEmpty()) {
            Integer error_code = classinfierJson.getInteger("error_code");
            if (0 == error_code) {
                JSONObject data = classinfierJson.getJSONObject("data");
                String templateSign = data.getString("templateSign");
                JSONArray ret = data.getJSONArray("ret");
                System.out.println(ret);
                switch (templateSign) {
                    case WebConstans.TEMPLATESIGN_OLD_ID: {
                        if (null != ret && !ret.isEmpty()) {
                            this.formatOldTemplate(ret, passportInfo);
                        }
                    }
                    case WebConstans.TEMPLATESIGN_NEW_ID: {
                        if (null != ret && !ret.isEmpty()) {
                            this.formatNewTemplate(ret, passportInfo);
                        }
                    }
                }
            }
        }
        return passportInfo;
    }
/**
 * @Description 格式化旧护照模板
 * @param templateJson
	* @param passportInfo
 * @return void
 * @Author shijie.lyu
 * @Date 2019/1/21 21:55
 */
    public void formatOldTemplate(JSONArray templateJson, PassportInfo passportInfo) {
        if (null != templateJson && !templateJson.isEmpty()) {
            for (int i = 0; i < templateJson.size(); i++) {
                JSONObject item = (JSONObject) templateJson.get(i);
                String word_name = item.getString("word_name");
                String word = item.getString("word");
                switch (word_name) {
                    case "姓": {
                        if (null != word && !word.isEmpty()) {
                            try {
                                String[] wordSplit = word.split("/");
                                String surName = wordSplit[1].trim().toUpperCase();
                                if (surName.contains("<")) {
                                    surName = surName.replaceAll("<", " ");
                                }
                                if (surName.contains("0")) {
                                    surName = surName.replaceAll("0", "O");
                                }
                                if (surName.contains("1")) {
                                    surName = surName.replaceAll("1", "I");
                                }
                                passportInfo.setSurname(surName);
                            }catch (Exception e){
                                System.out.println(e);
                            }
                        }
                        break;
                    }
                    case "名": {
                        if (null != word && !word.isEmpty()) {
                            try {
                                String[] wordSplit = word.split("/");
                                String givenName = wordSplit[1].trim().toUpperCase();
                                if (givenName.contains("<")) {
                                    givenName = givenName.replaceAll("<", " ");
                                }
                                if (givenName.contains("0")) {
                                    givenName = givenName.replaceAll("0", "O");
                                }
                                if (givenName.contains("1")) {
                                    givenName = givenName.replaceAll("1", "I");
                                }
                                passportInfo.setGivenname(givenName);
                            }catch (Exception e){
                                System.out.println(e);
                            }
                        }
                        break;
                    }
                    case "性别": {
                        if (null != word && !word.isEmpty()) {
                            try {
                                String[] wordSplit = word.split("/");
                                passportInfo.setSex(wordSplit[1].trim().toUpperCase());
                            }catch (Exception e){
                                System.out.println(e);
                            }
                        }
                        break;
                    }
                    case "国家码": {
                        if (null != word && !word.isEmpty()) {
                            passportInfo.setCountryCode(word.trim().toUpperCase());
                        }
                        break;
                    }
                    case "护照号": {
                        if (null != word && !word.isEmpty()) {
                            if (word.contains("O")) {
                                word = word.replaceAll("O", "0");
                            }
                            if (word.contains("I")) {
                                word = word.replaceAll("I", "1");
                            }
                            passportInfo.setPid(word.trim().toUpperCase());
                        }
                        break;
                    }
                    case "护照类型": {
                        if (null != word && !word.isEmpty()) {
                            passportInfo.setType(word.trim().toUpperCase());
                        }
                        break;
                    }
                    case "出生日期": {
                        if (null != word && !word.isEmpty()) {
                            try {
                                word = word.substring(0, word.length() - 4) + word.substring(word.length() - 2);
                                passportInfo.setBirthDate(word.trim().toUpperCase());
                            }catch (Exception e){
                                System.out.println(e);
                            }
                        }
                        break;
                    }
                    case "护照有效期": {
                        if (null != word && !word.isEmpty()) {
                            try {
                                word = word.substring(0, word.length() - 4) + word.substring(word.length() - 2);
                                passportInfo.setExpiryDate(word.trim().toUpperCase());
                            }catch (Exception e){
                                System.out.println(e);
                            }
                        }
                        break;
                    }
                }
            }
        }

    }
/**
 * @Description 格式化新护照模板
 * @param templateJson
	* @param passportInfo
 * @return void
 * @Author shijie.lyu
 * @Date 2019/1/21 22:01
 */
    public void formatNewTemplate(JSONArray templateJson, PassportInfo passportInfo) {
        if (null != templateJson && !templateJson.isEmpty()) {
            for (int i = 0; i < templateJson.size(); i++) {
                JSONObject item = (JSONObject) templateJson.get(i);
                String word_name = item.getString("word_name");
                String word = item.getString("word");
                switch (word_name) {
                    case "英文名": {
                        if (null != word && !word.isEmpty()) {
                            try {
                                if(word.contains(",")){
                                    word = word.replaceAll(",", ".");
                                }
                                if(word.contains("，")){
                                    word = word.replaceAll("，", ".");
                                }
                                if(word.contains(" ")){
                                    word = word.replaceAll(" ", ".");
                                }
                                if(word.contains("..")){
                                    word = word.replaceAll("\\.\\.", ".");
                                }
                                if(word.contains(".")){
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
                                    passportInfo.setSurname(surName);
                                    passportInfo.setGivenname(givenName);
                                }
                            }catch (Exception e){
                                System.out.println(e);
                            }
                        }
                        break;
                    }
                    case "性别": {
                        if (null != word && !word.isEmpty()) {
                            if (word.contains("/")){
                                String[] wordSplit = word.split("/");
                                try {
                                    passportInfo.setSex(wordSplit[1].trim().toUpperCase());
                                }catch (Exception e){
                                    System.out.println(e);
                                }
                            }
                        }
                        break;
                    }
                    case "国家码": {
                        if (null != word && !word.isEmpty()) {
                            passportInfo.setCountryCode(word.trim().toUpperCase());
                        }
                        break;
                    }
                    case "护照号": {
                        if (null != word && !word.isEmpty()) {
                            if (word.contains("O")) {
                                word = word.replaceAll("O", "0");
                            }
                            if (word.contains("I")) {
                                word = word.replaceAll("I", "1");
                            }
                            passportInfo.setPid(word.trim().toUpperCase());
                        }
                        break;
                    }
                    case "护照类型": {
                        if (null != word && !word.isEmpty()) {
                            passportInfo.setType(word.trim().toUpperCase());
                        }
                        break;
                    }
                    case "出生日期": {
                        if (null != word && !word.isEmpty()) {
                            try {
                                word = word.substring(0, word.length() - 4) + word.substring(word.length() - 2);
                                passportInfo.setBirthDate(word.trim().toUpperCase());
                            }catch (Exception e){
                                System.out.println(e);
                            }
                        }
                        break;
                    }
                    case "护照有效期": {
                        if (null != word && !word.isEmpty()) {
                            try {
                                String[] split_date = word.trim().split("/");
                                String split_day = split_date[0];
                                String split_yea = split_date[1];
                                split_day = split_day.substring(0, 2);
                                split_yea = split_yea.substring(0, 3) + split_yea.substring(split_yea.length() - 2);
                                passportInfo.setExpiryDate((split_day + split_yea).toUpperCase());
                            }catch (Exception e){
                                System.out.println(e);
                            }
                        }
                        break;
                    }
                }
            }
        }
    }
}
