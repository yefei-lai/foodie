package com.imooc.controller;

import com.imooc.pojo.Users;
import com.imooc.pojo.bo.UserBO;
import com.imooc.service.UserService;
import common.imooc.utils.CookieUtils;
import common.imooc.utils.IMOOCJSONResult;
import common.imooc.utils.JsonUtils;
import common.imooc.utils.MD5Utils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Api(value = "注册登录", tags = "用于注册登录的相关接口")
@RestController
@RequestMapping("passport")
public class PassportController {

    @Autowired
    private UserService userService;

    @ApiOperation(value = "用户名是否存在", notes = "用户名是否存在", httpMethod = "GET")
    @GetMapping("/usernameIsExist")
    public IMOOCJSONResult usernameIsExist(@RequestParam String username){

        //1、判断用户名不能为空 isBlank还判断是否为空字符串
        if (StringUtils.isBlank(username)){
            return IMOOCJSONResult.errorMsg("用户名不能为空");
        }

        //2、查找用户名是否存在
        boolean isExist = userService.queryUsernameIsExist(username);
        if (isExist){
            return IMOOCJSONResult.errorMsg("用户名已存在");
        }

        //3、请求成功，用户名没有重复
        return IMOOCJSONResult.ok();
    }

    @ApiOperation(value = "用户注册", notes = "用户注册", httpMethod = "POST")
    @PostMapping("/regist")
    public IMOOCJSONResult register(@RequestBody UserBO userBO,
                                    HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse){

        String username = userBO.getUsername();
        String password = userBO.getPassword();
        String confirmPwd = userBO.getConfirmPassword();

        //0. 判断用户名与密码必须不能为空
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password) || StringUtils.isBlank(confirmPwd)){
            return IMOOCJSONResult.errorMsg("用户名或密码不能为空");
        }
        //1. 查询用户是否存在
        boolean isExist = userService.queryUsernameIsExist(username);
        if (isExist){
            return IMOOCJSONResult.errorMsg("用户名已存在");
        }
        //2. 密码长度不能少于6位
        if (password.length() < 6){
            return IMOOCJSONResult.errorMsg("密码长度不能少于6");
        }
        //3. 判断两次密码是否一致
        if (!password.equals(confirmPwd)){
            return IMOOCJSONResult.errorMsg("两次密码输入不一致");
        }
        //4. 实现注册
        Users users = userService.createUser(userBO);
        //放进cookie
        CookieUtils.setCookie(httpServletRequest, httpServletResponse, "user",
                JsonUtils.objectToJson(users), true); //isEncode 是否加密

        return IMOOCJSONResult.ok();
    }

    @ApiOperation(value = "用户登录", notes = "用户登录", httpMethod = "POST")
    @PostMapping("/login")
    public IMOOCJSONResult login(@RequestBody UserBO userBO,
                                 HttpServletRequest httpServletRequest,
                                 HttpServletResponse httpServletResponse) throws Exception{

        String username = userBO.getUsername();
        String password = userBO.getPassword();

        //0. 判断用户名与密码必须不能为空
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)){
            return IMOOCJSONResult.errorMsg("用户名或密码不能为空");
        }
        //实现登录
        Users users = userService.queryUserForLogin(username, MD5Utils.getMD5Str(password));

        if (null == users){
            return IMOOCJSONResult.errorMsg("用户名或密码不正确");
        }

        users = setNullProperty(users);

        CookieUtils.setCookie(httpServletRequest, httpServletResponse, "user",
                JsonUtils.objectToJson(users), true); //isEncode 是否加密

        return IMOOCJSONResult.ok(users);
    }

    private Users setNullProperty(Users users){
        users.setPassword(null);
        users.setMobile(null);
        users.setEmail(null);
        users.setCreatedTime(null);
        users.setUpdatedTime(null);
        users.setBirthday(null);
        return users;
    }
}
