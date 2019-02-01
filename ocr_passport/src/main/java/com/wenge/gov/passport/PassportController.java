package com.wenge.gov.passport;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Inject;
import com.jfinal.config.JFinalConfig;
import com.jfinal.core.Controller;
import com.jfinal.core.JFinal;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.Config;
import com.wenge.gov.common.model.po.PassportInfo;
import sun.misc.BASE64Decoder;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

/**
 * @ClassName PassportController
 * @Description 护照模块控制器
 * @Author shijie.lyu
 * @Date 2019-01-09 15:36
 * @Version 1.0
 */
public class PassportController extends Controller {
    private static final String TEMPLATESIGN = "a8f5c06d0c7b5ac2e5d801e026d200a1";
    @Inject
    PassportService service;

    public void index() {
        render("index.html");
    }

    public void single_passport() {
        long start = System.currentTimeMillis();
        JSONObject result = new JSONObject();
        String imgFile = this.getPara("imgFile");
        //保存jpg图片
        //根据护照机读码识别，通用国内外护照，但是图片必须含有机读码。
        result = service.getPassportInfoByGeneralBasic(imgFile);
        //获取机读码
        List<String> list = service.catOutVaildInfoByGeneralBasic(result);
        //解析机读码
        PassportInfo passportInfo = service.formatGeneralBasic(list);
        if (null == passportInfo || null == passportInfo.getPid()) {
            //如果机读码解析失败，则调用模板识别接口
            //根据护照模板识别，只能识别大陆护照。
            System.out.println("模板解析");
            result = service.getPassportInfoByClassinfier(imgFile);
            passportInfo = service.parseClassinfier(result);
        }
        //保存识别信息
        if (null != passportInfo && null != passportInfo.getPid()) {
            passportInfo.setInsertTime(new Date());
            String pid = passportInfo.getPid();
            if (null != pid && !pid.isEmpty()) {
                PassportInfo info = service.dao.findById(pid);
                if (null == info) {
                    passportInfo.save();
                } else {
                    passportInfo.update();
                }
            }
        }
        long end = System.currentTimeMillis();
        System.out.println(end-start);
        renderJson(passportInfo);
    }

    public void renewal() {
        getBean(PassportInfo.class, "").update();
        renderText("ok");
    }

}
