package com.wenge.gov.pass;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Inject;
import com.jfinal.core.Controller;
import com.wenge.gov.common.model.po.Idcard;
import com.wenge.gov.common.model.po.Pass;
import com.wenge.gov.common.model.po.PassportInfo;

import java.util.Date;


/**
 * @ClassName PassportController
 * @Description 护照模块控制器
 * @Author shijie.lyu
 * @Date 2019-01-09 15:36
 * @Version 1.0
 */
public class PassController extends Controller {
    private static final String TEMPLATESIGN = "a8f5c06d0c7b5ac2e5d801e026d200a1";
    @Inject
    PassService service;

    public void index() {
        render("index.html");
    }

    public void single_pass() {
        long start = System.currentTimeMillis();
        JSONObject result = new JSONObject();
        String imgFile = this.getPara("imgFile");
        result = service.getPassInfoByRecognise(imgFile);
        //解析机读码
        Pass pass = service.parsePass(result);
        //保存识别信息
        if (null != pass && null != pass.getId()) {
            pass.setInsertTime(new Date());
            String id = pass.getId();
            if (null != id && !id.isEmpty()) {
                Pass info = service.dao.findById(id);
                if (null == info) {
                    pass.save();
                } else {
                    pass.update();
                }
            }
        }
        long end = System.currentTimeMillis();
        System.out.println(end-start);
        renderJson(pass);
    }
    public void renewal() {
        getBean(Pass.class, "").update();
        renderText("ok");
    }

}
