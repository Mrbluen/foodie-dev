package com.imooc.controller;

import com.imooc.enums.YesOrNo;
import com.imooc.pojo.Carousel;
import com.imooc.pojo.UserAddress;
import com.imooc.pojo.bo.AddressBO;
import com.imooc.service.AddressService;
import com.imooc.service.CarouselService;
import com.imooc.service.CategoryService;
import com.imooc.utils.IMOOCJSONResult;
import com.imooc.utils.MobileEmailUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
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
    @ApiOperation(value = "根据用户新增收货地址", notes = "根据用户新增收货地址", httpMethod = "POST")
    @PostMapping("/add")
    public IMOOCJSONResult add(
            @RequestBody AddressBO addressBO
    ) {
        IMOOCJSONResult checkRes = checkAddress(addressBO);
        if (checkRes.getStatus() != 200){
            return checkRes;
        }
        addressService.addNewAddress(addressBO);
        return IMOOCJSONResult.ok();
    }

    private IMOOCJSONResult checkAddress(AddressBO addressBO){
        String receiver = addressBO.getReceiver();
        if (StringUtils.isBlank(receiver)){
            return IMOOCJSONResult.errorMsg("收货人不能为空");
        }
        if (receiver.length() > 12){
        return IMOOCJSONResult.errorMsg("收货人姓名不能太长");
    }
        String mobile =addressBO.getMobile();
        if (StringUtils.isBlank(mobile)){
            return IMOOCJSONResult.errorMsg("收货人手机号不能为空");
        }
        if (mobile.length() != 11){
            return IMOOCJSONResult.errorMsg("手机号不正确");
        }
        boolean isMobileOk = MobileEmailUtils.checkMobileIsOk(mobile);
        if (!isMobileOk){
            return IMOOCJSONResult.errorMsg("收货人手机地址不正确");
        }
        String province = addressBO.getProvince();
        String city = addressBO.getCity();
        String district = addressBO.getDistrict();
        String detail = addressBO.getDetail();
        if (StringUtils.isBlank(province)||
                    StringUtils.isBlank(city) ||
                    StringUtils.isBlank(district) ||
                    StringUtils.isBlank(detail) ){
            return IMOOCJSONResult.errorMsg("收货地址信息不能为空");
        }
        return IMOOCJSONResult.ok();
    }
    @ApiOperation(value = "用户修改收货地址", notes = "用户修改收货地址", httpMethod = "POST")
    @PostMapping("/update")
    public IMOOCJSONResult update(
            @RequestBody AddressBO addressBO
    ) {
        if (StringUtils.isBlank(addressBO.getAddressId())){
            return IMOOCJSONResult.errorMsg("修改地址错误，addressId不能为空");
        }
        IMOOCJSONResult checkRes = checkAddress(addressBO);
        if (checkRes.getStatus() != 200){
            return checkRes;
        }
        addressService.updateUserAddress(addressBO);
        return IMOOCJSONResult.ok();
    }
    @ApiOperation(value = "用户删除收货地址", notes = "用户删除收货地址", httpMethod = "POST")
    @PostMapping("/delete")
    public IMOOCJSONResult delete(
            @RequestParam String userId,
            @RequestParam String addressId
    ) {
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(addressId) ){
            return IMOOCJSONResult.errorMsg("");
        }
        addressService.deleteUserAddress(userId,addressId);
        return IMOOCJSONResult.ok();
    }
    @ApiOperation(value = "用户设置默认地址地址", notes = "用户设置默认地址地址", httpMethod = "POST")
    @PostMapping("/setDefault")
    public IMOOCJSONResult setDefault(
            @RequestParam String userId,
            @RequestParam String addressId
    ) {
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(addressId) ){
            return IMOOCJSONResult.errorMsg("");
        }
        addressService.updateUserAddressToBeDefault(userId,addressId);
        return IMOOCJSONResult.ok();
    }
}
