package com.imooc.controller;

import com.imooc.enums.OrderStatusEnum;
import com.imooc.enums.PayMethod;
import com.imooc.pojo.bo.SubmitOrderBO;
import com.imooc.pojo.vo.MerchantOrdersVO;
import com.imooc.pojo.vo.OrderVO;
import com.imooc.service.AddressService;
import com.imooc.service.OrderService;
import com.imooc.utils.CookieUtils;
import com.imooc.utils.IMOOCJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


//@Controller
@Api(value = "订单相关",tags = {"订单相关的api接口"})
@RestController
@RequestMapping("orders")
public class OrdersController extends BaseController{
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
    @Autowired
    private OrderService orderService;
    @Autowired
    private RestTemplate restTemplate;

    @ApiOperation(value = "用户下单", notes = "用户下单", httpMethod = "POST")
    @PostMapping("/create")
        public IMOOCJSONResult create(@RequestBody SubmitOrderBO submitOrderBO,
                                      HttpServletResponse response, HttpServletRequest request) {
        //1.创建订单
        OrderVO orderVO =  orderService.createOrder(submitOrderBO);
        String orderId = orderVO.getOrderId();


        //2.创建订单以后，移除购物车的商品（已提交的）的商品
        //TODO 整合redis之后，完善购物车中的已结算的商品
        //CookieUtils.setCookie(request,response,FOODIE_SHOPCART,"",true);
        //3.向支付中心发送订单，用于保存支付中心的订单数据

        MerchantOrdersVO merchantOrdersVO = orderVO.getMerchantOrdersVO();
        merchantOrdersVO.setReturnUrl(payReturnUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("imoocUserId","imooc");
        headers.add("password","imooc");
        HttpEntity<MerchantOrdersVO> entity = new HttpEntity<>(merchantOrdersVO,headers);
        ResponseEntity<IMOOCJSONResult> resultResponseEntity =  restTemplate.postForEntity(paymentUrl,
                entity,IMOOCJSONResult.class);
        IMOOCJSONResult paymentResult = resultResponseEntity.getBody();
        if (paymentResult.getStatus() != 200){
            return IMOOCJSONResult.errorMsg("支付中心订单创建失败，请联系管理员");

        }
        return IMOOCJSONResult.ok(orderId);

//        if (submitOrderBO.getChoosedPayMethod() != PayMethod.WEIXIN.type &&
//                submitOrderBO.getChoosedPayMethod() != PayMethod.ALIPAY.type )
//            return IMOOCJSONResult.errorMsg("支付方式不支持");
//        return IMOOCJSONResult.ok(orderId);
        }
    @PostMapping("/notifyMerchantOrderPaid")
    public Integer notifyMerchantOrderPaid(String merchantOrderId){
        orderService.updateOrderStatus(merchantOrderId, OrderStatusEnum.WAIT_DELIVER.type);
        return HttpStatus.OK.value();
    }
}
