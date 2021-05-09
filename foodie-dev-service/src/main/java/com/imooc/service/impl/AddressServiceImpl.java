package com.imooc.service.impl;

import com.imooc.mapper.CarouselMapper;
import com.imooc.mapper.UserAddressMapper;
import com.imooc.pojo.Carousel;
import com.imooc.pojo.UserAddress;
import com.imooc.service.AddressService;
import com.imooc.service.CarouselService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {
    @Autowired
    private UserAddressMapper userAddressMapper;
    //查询所有地址
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<UserAddress> queryAll(String userId){
        UserAddress ua = new UserAddress();
        ua.setUserId(userId);

        return userAddressMapper.select(ua);
    }
}
