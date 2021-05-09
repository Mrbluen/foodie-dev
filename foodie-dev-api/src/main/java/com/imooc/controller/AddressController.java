package com.imooc.controller;

import com.imooc.enums.YesOrNo;
import com.imooc.pojo.Carousel;
import com.imooc.pojo.UserAddress;
import com.imooc.service.AddressService;
import com.imooc.service.CarouselService;
import com.imooc.service.CategoryService;
import com.imooc.utils.IMOOCJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

//@Controller
@Api(value = "地址相关",tags = {"地址相关的api接口"})
@RestController
@RequestMapping("address")
public class AddressController {
    /*
    * 用户在确认订单页面，可以针对收货地址做以下操作
    * 1.查询用户的所有收货地址的列表
    * 2.新增收货地址
    * 3。删除收货地址
    * 4.修改收货地址
    * 5.设置默认地址
    * */
    @Autowired
    private AddressService addressService;
    @ApiOperation(value = "根据用户id查询收货地址", notes = "根据用户id查询收货地址", httpMethod = "POST")
    @PostMapping("/list")
        public IMOOCJSONResult list(
                @RequestParam String userId
    ) {
            List<UserAddress> list = addressService.queryAll(userId);
            return IMOOCJSONResult.ok(list);
        }


}
