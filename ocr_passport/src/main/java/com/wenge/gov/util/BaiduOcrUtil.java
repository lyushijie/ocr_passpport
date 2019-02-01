package com.wenge.gov.util;

import com.baidu.aip.ocr.AipOcr;
import com.wenge.gov.constans.WebConstans;

/**
 * @ClassName BaiduOcrUtil
 * @Description 百度ocr调用工具
 * @Author shijie.lyu
 * @Date 2019-01-09 16:22
 * @Version 1.0
 */
public class BaiduOcrUtil {


    private static BaiduOcrUtil INSTANCE = null;
    private static AipOcr client = null;

    public static BaiduOcrUtil getInstance() {
        if (null == INSTANCE) {
            synchronized (BaiduOcrUtil.class) {
                if (null == INSTANCE) {
                    INSTANCE = new BaiduOcrUtil();
                }
            }
        }
        return INSTANCE;
    }

    private BaiduOcrUtil() {
        this.client = new AipOcr(WebConstans.APP_ID, WebConstans.API_KEY, WebConstans.SECRET_KEY);
        // 可选：设置网络连接参数
        this.client.setConnectionTimeoutInMillis(2000);
        this.client.setSocketTimeoutInMillis(60000);
        // 可选：设置代理服务器地址, http和socket二选一，或者均不设置
        //client.setHttpProxy("proxy_host", proxy_port);  // 设置http代理
        // client.setSocketProxy("proxy_host", proxy_port);  // 设置socket代理
        // 可选：设置log4j日志输出格式，若不设置，则使用默认配置
        // 也可以直接通过jvm启动参数设置此环境变量
        //System.setProperty("aip.log4j.conf", "path/to/your/log4j.properties");
    }

    /**
     * @Description 获取一个api连接
     * @Param []
     * @return com.baidu.aip.ocr.AipOcr
     * @Author shijie.lyu
     * @Date 2019/1/9 16:50
     */
    public AipOcr getApiOcrClient(){
        return this.client;
    }

}
