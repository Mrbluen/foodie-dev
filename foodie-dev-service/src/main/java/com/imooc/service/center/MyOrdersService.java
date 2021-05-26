package com.imooc.service.center;

import com.imooc.pojo.Orders;
import com.imooc.pojo.Users;
import com.imooc.pojo.bo.center.CenterUserBO;
import com.imooc.utils.PagedGridResult;

public interface MyOrdersService {

    /**
     * 查询用户订单
     */
    public PagedGridResult queryMyOrders(String userId,
                                         Integer orderStatus,
                                         Integer page,
                                         Integer pageSize);
    //确认收货
    public void updateDeliverOrderStatus(String orderId);
    /**
     * 查询我的订单
     *
     * @param userId
     * @param orderId
     * @return
     */
    public Orders queryMyOrder(String userId, String orderId);
    /**
     * 更新我的订单-》确认收货
     *
     * @param orderId

     * @return
     */
    public boolean updateReceiveOrderStatus(String orderId);


    /**
     * 删除我的订单
     *
     * @param userId
     * @param orderId
     * @return
     */
    public boolean deleteOrder(String orderId,String userId);
}
