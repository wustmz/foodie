package org.foodie.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.foodie.pojo.Users;
import org.foodie.pojo.bo.UserBO;
import org.foodie.service.IUsersService;
import org.foodie.utils.CookieUtils;
import org.foodie.utils.JsonUtils;
import org.foodie.utils.MD5Utils;
import org.foodie.utils.ServerResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author steve.mei
 * @Version PassportController,  2020/10/19 8:59 下午
 **/
@Api(value = "注册登录", tags = {"用于注册登录的相关接口"})
@RestController
@RequestMapping("passport")
public class PassportController {

    @Autowired
    private IUsersService userService;

    @ApiOperation(value = "用户名是否存在", notes = "用户名是否存在", httpMethod = "GET")
    @GetMapping("/usernameIsExist")
    public ServerResponse usernameIsExist(@RequestParam String username) {
        //判断用户名是否为空
        if (StringUtils.isEmpty(username)) {
            return ServerResponse.errorMap("用户名不能为空");
        }

        //查找用户名是否存在
        boolean isExist = userService.queryUsernameIsExist(username);

        if (isExist) {
            return ServerResponse.errorMsg("用户名已经存在");
        }

        return ServerResponse.ok();
    }

    @ApiOperation(value = "用户注册", notes = "用户注册", httpMethod = "POST")
    @PostMapping("/register")
    public ServerResponse register(@RequestBody UserBO userBo,
                                   HttpServletRequest request,
                                   HttpServletResponse response) {
        String username = userBo.getUsername();
        String password = userBo.getPassword();
        String confirmPassword = userBo.getConfirmPassword();

        //判断用户名和密码必须不为空
        if (StringUtils.isEmpty(username) ||
                StringUtils.isEmpty(password) ||
                StringUtils.isEmpty(confirmPassword)) {
            return ServerResponse.errorMsg("用户名或密码不能为空");
        }

        //查询用户名是否存在
        boolean isExist = userService.queryUsernameIsExist(username);

        if (isExist) {
            return ServerResponse.errorMsg("用户名已经存在");
        }
        //密码长度判断
        if (password.length() < 6) {
            return ServerResponse.errorMsg("密码长度不能少于6");
        }
        //判断两次密码是否一致
        if (!password.equals(confirmPassword)) {
            return ServerResponse.errorMsg("两次密码输入不一致");
        }
        //实现注册
        Users userResult = userService.createUser(userBo);

        setNullProperty(userResult);

        CookieUtils.setCookie(request, response, "user",
                JsonUtils.objectToJson(userResult), true);

        // TODO 生成用户token，存入redis会话
        // TODO 同步购物车数据

        return ServerResponse.ok();
    }

    @ApiOperation(value = "用户登录", notes = "用户登录", httpMethod = "POST")
    @PostMapping("/login")
    public ServerResponse login(@RequestBody UserBO userBO,
                                HttpServletRequest request,
                                HttpServletResponse response) throws Exception {

        String username = userBO.getUsername();
        String password = userBO.getPassword();

        // 0. 判断用户名和密码必须不为空
        if (StringUtils.isBlank(username) ||
                StringUtils.isBlank(password)) {
            return ServerResponse.errorMsg("用户名或密码不能为空");
        }

        // 1. 实现登录
        Users userResult = userService.queryUserForLogin(username,
                MD5Utils.getMD5Str(password));

        if (userResult == null) {
            return ServerResponse.errorMsg("用户名或密码不正确");
        }

        setNullProperty(userResult);

        CookieUtils.setCookie(request, response, "user",
                JsonUtils.objectToJson(userResult), true);


        // TODO 生成用户token，存入redis会话
        // TODO 同步购物车数据

        return ServerResponse.ok(userResult);
    }

    @ApiOperation(value = "用户退出登录", notes = "用户退出登录", httpMethod = "POST")
    @PostMapping("/logout")
    public ServerResponse logout(@RequestParam String userId,
                                 HttpServletRequest request,
                                 HttpServletResponse response) {

        // 清除用户的相关信息的cookie
        CookieUtils.deleteCookie(request, response, "user");

        // TODO 用户退出登录，需要清空购物车
        // TODO 分布式会话中需要清除用户数据

        return ServerResponse.ok();
    }

    private void setNullProperty(Users userResult) {
        userResult.setPassword(null);
        userResult.setMobile(null);
        userResult.setEmail(null);
        userResult.setCreatedTime(null);
        userResult.setUpdatedTime(null);
        userResult.setBirthday(null);
    }


}
