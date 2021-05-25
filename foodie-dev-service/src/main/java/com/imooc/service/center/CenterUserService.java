package com.imooc.service.center;

import com.imooc.pojo.Users;
import com.imooc.pojo.bo.UserBO;
import com.imooc.pojo.bo.center.CenterUserBO;

public interface CenterUserService {

    /**
     * 查询用户信息
     */
    public Users queryUserInfo(String userId);

    //修改用户信息
    public Users updateUserInfo(String userId , CenterUserBO centerUserBO);
}
