package com.wenge.gov.constans;

import com.sun.xml.internal.bind.v2.model.core.ID;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName WebConstans
 * @Description web常量
 * @Author shijie.lyu
 * @Date 2019-01-12 9:04
 * @Version 1.0
 */
public class WebConstans {

    public static final String APP_ID = "15376904";
    public static final String API_KEY = "hxTG79TUjCG69kd5FmflOHpl";
    public static final String SECRET_KEY = "3tyZCfYRV4KT9ELviC77l2MepEb5KwRL";
    public static final String AUTO_HOST = "https://aip.baidubce.com/oauth/2.0/token";
    public static final String RECOGNISE_URL = "https://aip.baidubce.com/rest/2.0/solution/v1/iocr/recognise";//自定义模板接口
    public static final String GENERAL_BASIC_URL = "https://aip.baidubce.com/rest/2.0/ocr/v1/general_basic";//通用文字识别接口
    public static final String TEMPLATESIGN_OLD_ID = "e079d225d1d3fd3256daa209133fcd09";//旧护照模板id
    public static final String TEMPLATESIGN_NEW_ID = "a8f5c06d0c7b5ac2e5d801e026d200a1";//新护照模板id
    public static final String TEMPLATESIGN_PASS_ID = "0fd7d20201668dcb371090bd3beccb9e";//港澳台模板id
    public static final String IDCARD_URL = "https://aip.baidubce.com/rest/2.0/ocr/v1/idcard";//身份证识别接口

    public static Map<String,String> TEMPLATE = new HashMap<>();
    public static String CLASSIFIERID = "";
}
