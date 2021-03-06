package com.imooc.service.impl;

import com.imooc.enums.OrderStatusEnum;
import com.imooc.enums.YesOrNo;
import com.imooc.mapper.OrderItemsMapper;
import com.imooc.mapper.OrderStatusMapper;
import com.imooc.mapper.OrdersMapper;
import com.imooc.pojo.*;
import com.imooc.pojo.bo.SubmitOrderBO;
import com.imooc.pojo.vo.MerchantOrdersVO;
import com.imooc.pojo.vo.OrderVO;
import com.imooc.service.AddressService;
import com.imooc.service.ItemService;
import com.imooc.service.OrderService;

import com.imooc.utils.DateUtil;
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
    public OrderVO createOrder(SubmitOrderBO submitOrderBO) {
        String userId =submitOrderBO.getUserId();
        String itemSpecIds = submitOrderBO.getItemSpecIds();
        String addressId = submitOrderBO.getChoosedAddressId();
        Integer payMethod =submitOrderBO.getChoosedPayMethod();
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

        newOrder.setPayMethod(payMethod);
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
            //4.构建商户订单，用于传给支付中心
            MerchantOrdersVO merchantOrdersVO = new MerchantOrdersVO();
            merchantOrdersVO.setMerchantOrderId(orderId);
            merchantOrdersVO.setMerchantUserId(userId);
            merchantOrdersVO.setAmount(realPayAmount + postAmount);
            merchantOrdersVO.setPayMethod(payMethod);
            //自定义订单vo
            OrderVO orderVO = new OrderVO();
            orderVO.setOrderId(orderId);
            orderVO.setMerchantOrdersVO(merchantOrdersVO);
            return orderVO;
    }
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateOrderStatus(String orderId, Integer orderStatus) {
        OrderStatus paidStatus = new OrderStatus();
        paidStatus.setOrderId(orderId);
        paidStatus.setOrderStatus(orderStatus);
        paidStatus.setPayTime(new Date());

        orderStatusMapper.updateByPrimaryKeySelective(paidStatus);
    }
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public OrderStatus queryOrdersStatusInfo(String orderId) {
        return orderStatusMapper.selectByPrimaryKey(orderId);
    }
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void closeOrder() {
        //超时关闭交易
        OrderStatus queryOrder = new OrderStatus();
        queryOrder.setOrderStatus(OrderStatusEnum.WAIT_PAY.type);
        List<OrderStatus> list = orderStatusMapper.select(queryOrder);
        for (OrderStatus os:list
             ) {
            Date createdTime = os.getCreatedTime();

            int days = DateUtil.daysBetween(createdTime,new Date());
            if (days >=1 ){
                //超过一天关闭订单
                doCloseOrder(os.getOrderId());
            }
        }
    }
        @Transactional(propagation = Propagation.REQUIRED)
        void doCloseOrder(String orderId){
            OrderStatus close = new OrderStatus();
            close.setOrderId(orderId);
            close.setOrderStatus(OrderStatusEnum.CLOSE.type);
            close.setCloseTime(new Date());
            orderStatusMapper.updateByPrimaryKeySelective(close);
    }

}
