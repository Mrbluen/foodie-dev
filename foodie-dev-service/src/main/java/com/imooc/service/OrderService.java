package com.imooc.service;

import com.imooc.pojo.Carousel;
import com.imooc.pojo.OrderStatus;
import com.imooc.pojo.bo.SubmitOrderBO;
import com.imooc.pojo.vo.OrderVO;

import java.util.List;

public interface OrderService {
    //创建订单相关信息
    public OrderVO createOrder(SubmitOrderBO submitOrderBO);

    //修改订单状态
    public void updateOrderStatus(String orderId,Integer orderStatus);

    //查询订单状态
    public OrderStatus queryOrdersStatusInfo(String orderId);

    //查询未支付的订单
    public void closeOrder();


}
