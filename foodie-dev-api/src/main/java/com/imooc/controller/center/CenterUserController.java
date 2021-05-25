package com.imooc.controller.center;

import com.imooc.pojo.Users;
import com.imooc.pojo.bo.center.CenterUserBO;
import com.imooc.service.center.CenterUserService;
import com.imooc.utils.CookieUtils;
import com.imooc.utils.IMOOCJSONResult;
import com.imooc.utils.JsonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(value = "用户信息接口",tags = {"用户信息接口"})
@RestController
@RequestMapping("userInfo")
public class CenterUserController {

    @Autowired
    private CenterUserService centerUserService;

    @ApiOperation(value = "修改用户信息",notes = "修改用户信息",httpMethod = "POST")
    @PostMapping("update")
    public IMOOCJSONResult update(@ApiParam(name = "userId",required = true)
                                    @RequestParam String userId,
                                  @RequestBody @Valid CenterUserBO centerUserBO,
                                  BindingResult result,
                                  HttpServletRequest request,
                                  HttpServletResponse response){
        if (result.hasErrors()){
            Map<String,String> errorMap = getErrors(result);

            return IMOOCJSONResult.errorMap(errorMap);
        }


        Users userResult = centerUserService.updateUserInfo(userId,centerUserBO);

        userResult = setNullProperty(userResult);
        //更新cookie
        CookieUtils.setCookie(request, response, "user",
                JsonUtils.objectToJson(userResult), true);
        // TODO 要改，整合分布式会话
        return IMOOCJSONResult.ok();
    }
    private Map<String, String> getErrors(BindingResult result){
        Map<String, String> map = new HashMap<>();
       List<FieldError> errorList = (List<FieldError>) result.getFieldError();
        for (FieldError err:errorList
             ) {
            String errorField =  err.getField();
            String errorMsg =  err.getDefaultMessage();
            map.put(errorField,errorMsg);

        }
        return map;
    }

    private Users setNullProperty(Users userResult) {
        userResult.setPassword(null);
        userResult.setMobile(null);
        userResult.setEmail(null);
        userResult.setCreatedTime(null);
        userResult.setUpdatedTime(null);
        userResult.setBirthday(null);
        return userResult;
    }
}
