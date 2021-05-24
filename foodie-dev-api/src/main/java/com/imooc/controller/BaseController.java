package com.imooc.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


public class BaseController {

    public static final String FOODIE_SHOPCART = "shopcart";

    public static final Integer COMMON_PAGE_SIZE =10;
    public static final Integer PAGE_SIZE =10;
    //微信支付成功 ——> 支付中心 -->天天吃货平台

    //回调通知的url
    String payReturnUrl = "http://localhost:8080/orders/notifyMerchantOrderPaid";
}
