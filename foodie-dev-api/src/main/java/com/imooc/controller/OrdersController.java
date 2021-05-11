package com.imooc.controller;

import com.imooc.enums.PayMethod;
import com.imooc.pojo.UserAddress;
import com.imooc.pojo.bo.AddressBO;
import com.imooc.pojo.bo.SubmitOrderBO;
import com.imooc.service.AddressService;
import com.imooc.utils.IMOOCJSONResult;
import com.imooc.utils.MobileEmailUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@Controller
@Api(value = "订单相关",tags = {"订单相关的api接口"})
@RestController
@RequestMapping("orders")
public class OrdersController {
    /*
    * 用户在确认订单页面，可以针对收货地址做以下操作
    * 1.查询用户的所有收货地址的列表
    * 2.新增收货地址
    * 3。删除收货地址
    * 4.修改收货地址
    * 5.设置默认地址
    * */
    @Autowired
    private AddressService addressService;


    @ApiOperation(value = "用户下单", notes = "用户下单", httpMethod = "POST")
    @PostMapping("/create")
        public IMOOCJSONResult create(@RequestBody SubmitOrderBO submitOrderBO) {
        //1.创建订单
        //2.创建订单以后，移除购物车的商品（已提交的）的商品
        //3.向支付中心发送订单，用于保存支付中心的订单数据
        if (submitOrderBO.getChoosedPayMethod() != PayMethod.WEIXIN.type &&
                submitOrderBO.getChoosedPayMethod() != PayMethod.ALIPAY.type )
            return IMOOCJSONResult.errorMsg("支付方式不支持");
        return IMOOCJSONResult.ok();
        }

}
