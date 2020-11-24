package com.imooc.service.center;

import com.imooc.pojo.Users;
import com.imooc.pojo.bo.UserBO;

public interface CenterUserService {

    /**
     * 根据userId查询用户信息
     * @param userId
     * @return
     */
    public Users queryUserInfo(String userId);
}
