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
    //用户删除地址
    public void deleteUserAddress(String userId,String addressId);

    public void updateUserAddressToBeDefault(String userId,String addressId);

    public UserAddress queryUserAddress(String userId,String addressId);
}
