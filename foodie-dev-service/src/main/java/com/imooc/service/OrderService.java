package com.imooc.service;

import com.imooc.pojo.Carousel;
import com.imooc.pojo.bo.SubmitOrderBO;

import java.util.List;

public interface OrderService {
    //创建订单相关信息
    public void createOrder(SubmitOrderBO submitOrderBO);
}
