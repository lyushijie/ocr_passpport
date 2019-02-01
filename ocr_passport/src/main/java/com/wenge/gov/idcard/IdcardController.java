package com.wenge.gov.idcard;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Inject;
import com.jfinal.core.Controller;
import com.wenge.gov.common.model.po.Idcard;

import java.util.Date;


/**
 * @ClassName PassportController
 * @Description 护照模块控制器
 * @Author shijie.lyu
 * @Date 2019-01-09 15:36
 * @Version 1.0
 */
public class IdcardController extends Controller {
    private static final String TEMPLATESIGN = "a8f5c06d0c7b5ac2e5d801e026d200a1";
    @Inject
    IdcardService service;

    public void index() {
        render("index.html");
    }

    public void single_idcard() {
        long start = System.currentTimeMillis();
        JSONObject result = new JSONObject();
        String imgFile = this.getPara("imgFile");
        result = service.getPassportInfoByClassinfier(imgFile);
        //解析机读码
        Idcard idcard = service.parseIdcard(result);
        //保存识别信息
        if (null != idcard && null != idcard.getId()) {
            idcard.setInsertTime(new Date());
            String id = idcard.getId();
            if (null != id && !id.isEmpty()) {
                Idcard info = service.dao.findById(id);
                if (null == info) {
                    idcard.save();
                } else {
                    idcard.update();
                }
            }
        }
        long end = System.currentTimeMillis();
        System.out.println(end-start);
        renderJson(idcard);
    }

}
