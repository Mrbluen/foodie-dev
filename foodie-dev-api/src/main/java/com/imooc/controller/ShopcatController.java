package com.imooc.controller;

import com.imooc.pojo.bo.ShopcartBO;
import com.imooc.utils.IMOOCJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

//@Controller
@Api(value = "购物车接口controller",tags = {"购物车接口相关的api"})
@RequestMapping("shopcart")
@RestController
public class ShopcatController {

    @ApiOperation(value = "添加商品到购物车",notes = "添加商品到购物车",httpMethod = "POST")
    @PostMapping("/add")
    public IMOOCJSONResult add(@RequestParam String userId,
                               @RequestBody ShopcartBO shopcartBO,
                               HttpServletRequest request, HttpServletResponse response) {
        if (StringUtils.isBlank(userId)){
            return IMOOCJSONResult.errorMsg("");
        }
        // TODO 前端用户在登陆的情况下，添加商品到购物车，会同时在后端同步购物车到redis缓存
        // System.out.println(shopcartBO);
        return IMOOCJSONResult.ok();
    }
    @ApiOperation(value = "删除购物车中的商品",notes = "删除购物车中的商品",httpMethod = "POST")
    @PostMapping("/del")
    public IMOOCJSONResult del(@RequestParam String userId,
                               @RequestParam String itemSpecId,
                               HttpServletRequest request, HttpServletResponse response) {
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(itemSpecId)){
            return IMOOCJSONResult.errorMsg("参数不能为空");
        }
        // TODO 前端用户在页面删除购物车中的商品，如果用户此时已经登录，则需要删除同步后端购物车中的商品
        return IMOOCJSONResult.ok();
    }
}
