package com.imooc.controller;

import com.imooc.pojo.bo.SubmitOrderBO;
import common.imooc.enums.PayMethod;
import common.imooc.utils.IMOOCJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@Api(value = "订单相关", tags = {"订单相关的api接口"})
@RestController
@RequestMapping("/orders")
public class OrdersController {

    @ApiOperation(value = "用户下单", notes = "用户下单")
    @PostMapping("/create")
    public IMOOCJSONResult create(@RequestBody SubmitOrderBO submitOrderBO){

        if (submitOrderBO.getPayMethod() != PayMethod.WEIXIN.type
                && submitOrderBO.getPayMethod() != PayMethod.ALIPAY.type){
            return IMOOCJSONResult.errorMsg("支付方式不支持");
        }

        // 1. 创建订单
        // 2. 创建订单以后，移除购物车中已结算的商品
        // 3. 向支付中心发送当前订单，用于保存支付中心的订单数据

        return IMOOCJSONResult.ok();
    }
}
