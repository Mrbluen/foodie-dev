package com.imooc.service.impl;

import com.imooc.enums.YesOrNo;
import com.imooc.mapper.OrdersMapper;
import com.imooc.pojo.Carousel;
import com.imooc.pojo.Orders;
import com.imooc.pojo.UserAddress;
import com.imooc.pojo.bo.SubmitOrderBO;
import com.imooc.service.AddressService;
import com.imooc.service.OrderService;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private Sid sid;
    @Autowired
    private AddressService addressService;

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void createOrder(SubmitOrderBO submitOrderBO) {
        String userId =submitOrderBO.getUserId();
        String itemSpecIds = submitOrderBO.getItemSpecIds();
        String addressId = submitOrderBO.getChoosedAddressId();
        Integer PayMethod =submitOrderBO.getChoosedPayMethod();
        String leftMsg = submitOrderBO.getLeftMsg();
        //包邮费用设置为0
        Integer postAmount = 0;

        String orderId = sid.nextShort();

        UserAddress address = addressService.queryUserAddress(userId,addressId);
        //1.新订单数据保存
        Orders newOrder = new Orders();
        newOrder.setId(orderId);

        newOrder.setReceiverName(address.getReceiver());
        newOrder.setReceiverMobile(address.getMobile());
        newOrder.setReceiverAddress(address.getProvince() +" "+
                address.getCity()+""+address.getDistrict()+""+address.getDetail());

//        newOrder.setTotalAmount();
//        newOrder.setRealAmount();
        newOrder.setPostAmount(postAmount);

        newOrder.setPayMethod(PayMethod);
        newOrder.setLeftMsg(leftMsg);
        newOrder.setIsComment(YesOrNo.NO.type);
        newOrder.setIsDelete(YesOrNo.NO.type);
        newOrder.setCreatedTime(new Date());
        newOrder.setUpdatedTime(new Date());
        //2.循环根据itemSpecIds保存订单商品信息表


        //3.保存订单状态

    }
}
