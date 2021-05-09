package com.imooc.service;

import com.imooc.pojo.Carousel;
import com.imooc.pojo.UserAddress;

import java.util.List;

public interface AddressService {
    //查询所有地址
    public List<UserAddress> queryAll(String userId);

}
