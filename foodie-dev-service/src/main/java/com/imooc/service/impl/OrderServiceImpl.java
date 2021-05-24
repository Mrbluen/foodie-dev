package com.imooc.service.impl;

import com.imooc.enums.OrderStatusEnum;
import com.imooc.enums.YesOrNo;
import com.imooc.mapper.OrderItemsMapper;
import com.imooc.mapper.OrderStatusMapper;
import com.imooc.mapper.OrdersMapper;
import com.imooc.pojo.*;
import com.imooc.pojo.bo.SubmitOrderBO;
import com.imooc.service.AddressService;
import com.imooc.service.ItemService;
import com.imooc.service.OrderService;

import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.Date;
import java.util.List;
@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private OrderItemsMapper orderItemsMapper;
    @Autowired
    private Sid sid;
    @Autowired
    private AddressService addressService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private OrderStatusMapper orderStatusMapper;


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
        String itemSpecIdArr[] = itemSpecIds.split(",");
        Integer totalAmount = 0;
        Integer realPayAmount = 0;
        for (String itemSpecId:itemSpecIdArr
             ) {

            //TODO 整合redis,商品购买数量重新从购物车中获取
            int buyCounts = 1;

            //2.1根据规格id，查询规格的具体信息，主要获取价格信息
            ItemsSpec itemsSpec = itemService.queryItemSpecById(itemSpecId);
            totalAmount += itemsSpec.getPriceNormal() * buyCounts;
            realPayAmount += itemsSpec.getPriceDiscount() *buyCounts;

            //2.2根据商品id，获得商品的信息以及商品图片
            String itemId = itemsSpec.getItemId();
            Items items = itemService.queryItemById(itemId);
            String imgUrl = itemService.queryItemMainImgById(itemId);

            //2.3循环保存子订单数据到数据库
            String subOrderId = sid.nextShort();
            OrderItems subOrderItems = new OrderItems();
            subOrderItems.setId(subOrderId);
            subOrderItems.setOrderId(orderId);
            subOrderItems.setItemId(itemId);
            subOrderItems.setItemName(items.getItemName());
            subOrderItems.setItemImg(imgUrl);
            subOrderItems.setBuyCounts(buyCounts);
            subOrderItems.setItemSpecId(itemSpecId);
            subOrderItems.setItemSpecName(itemsSpec.getName());
            subOrderItems.setPrice(itemsSpec.getPriceDiscount());
            orderItemsMapper.insert(subOrderItems);
            //2.4用户提交订单后扣除相应的库存
            itemService.decreaseItemSpecStock(itemSpecId,buyCounts);
        }
            newOrder.setTotalAmount(totalAmount);
            newOrder.setRealPayAmount(realPayAmount);
            ordersMapper.insert(newOrder);
            //3.保存订单状态
            OrderStatus waitPayOrderStatus = new OrderStatus();
            waitPayOrderStatus.setOrderId(orderId);
            waitPayOrderStatus.setOrderStatus(OrderStatusEnum.WAIT_PAY.type);
            waitPayOrderStatus.setCreatedTime(new Date());
            orderStatusMapper.insert(waitPayOrderStatus);
    }
}
