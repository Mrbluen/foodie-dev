package com.imooc.service;

import com.imooc.pojo.Carousel;
import com.imooc.pojo.UserAddress;
import com.imooc.pojo.bo.AddressBO;

import java.util.List;

public interface AddressService {
    //查询所有地址
    public List<UserAddress> queryAll(String userId);
    //添加地址
    public void addNewAddress(AddressBO addressBO);
    //用户修改地址
    public void updateUserAddress(AddressBO addressBO);
    //根据用户的id和地址id，删除对应的用户地址信息
    public void deleteUserAddress(String userId,String addressId);
}
