package org.foodie.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.foodie.pojo.Users;
import org.foodie.pojo.bo.ShopcartBO;
import org.foodie.pojo.bo.UserBO;
import org.foodie.pojo.vo.UsersVO;
import org.foodie.service.IUsersService;
import org.foodie.utils.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @Author steve.mei
 * @Version PassportController,  2020/10/19 8:59 下午
 **/
@Api(value = "注册登录", tags = {"用于注册登录的相关接口"})
@RestController
@RequestMapping("passport")
public class PassportController extends BaseController {

    @Autowired
    private RedisOperator redisOperator;

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
        UsersVO usersVO = convertUsersVO(userResult);


        CookieUtils.setCookie(request, response, "user",
                JsonUtils.objectToJson(usersVO), true);

        //同步购物车数据
        this.syncShopCartData(userResult.getId(), request, response);

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

        UsersVO usersVO = convertUsersVO(userResult);

        CookieUtils.setCookie(request, response, "user",
                JsonUtils.objectToJson(usersVO), true);

        //同步购物车数据
        this.syncShopCartData(userResult.getId(), request, response);

        return ServerResponse.ok(userResult);
    }

    /**
     * 注册登录成功后，同步cookie和redis中的购物车数据
     */
    private void syncShopCartData(String userId,
                                  HttpServletRequest request,
                                  HttpServletResponse response) {
        //1.redis中无数据，cookie中的购物车为空，那么这个时候不做任何处理，
        //  如果不为空，此时直接放入redis中
        //2.redis中有购物车数据，cookie中的购物车为空，那么直接把redis的购物车覆盖本地cookie，
        //  如果不为空，如果cookie中的某个商品在redis中存在，则以cookie为主，删除redis中的，把cookie中的商品直接覆盖redis中（参考京东）
        //3.同步到redis中去了以后，覆盖本地cookie购物车的数据，保证本地购物车的数据是同步最新的

        //从redis中获取购物车
        String shopCartJsonRedis = redisOperator.get(FOODIE_SHOPCART + ":" + userId);
        //从cookie中获取购物车
        String shopCartStrCookie = CookieUtils.getCookieValue(request, FOODIE_SHOPCART, true);

        if (StringUtils.isBlank(shopCartJsonRedis)) {
            //redis为空，cookie不为空，直接把cookie中的数据放入redis中
            if (StringUtils.isNotBlank(shopCartStrCookie)) {
                redisOperator.set(FOODIE_SHOPCART + ":" + userId, shopCartStrCookie);
            }
        } else {
            //redis不为空，cookie不为空，合并cookie和redis中购物车的商品数据（同一商品则覆盖）
            if (StringUtils.isNotBlank(shopCartStrCookie)) {
                //1.已经存在的，把cookie中对应的数量，覆盖redis
                //2.该项商品标记为待删除，统一放入一个待删除的list
                //3.从cookie中清理所有的待删除list
                //4.合并redis和cookie中的数据
                //5.更新到redis和cookie中

                List<ShopcartBO> shopCartListRedis = JsonUtils.jsonToList(shopCartJsonRedis, ShopcartBO.class);
                List<ShopcartBO> shopCartListCookie = JsonUtils.jsonToList(shopCartStrCookie, ShopcartBO.class);
                //定义一个待删除list
                List<ShopcartBO> pendingDeleteList = new ArrayList<>();

                for (ShopcartBO redisShopCart : shopCartListRedis) {
                    String redisSpecId = redisShopCart.getSpecId();

                    for (ShopcartBO cookieShopCart : shopCartListCookie) {
                        String cookieSpecId = cookieShopCart.getSpecId();

                        if (redisSpecId.equals(cookieSpecId)) {
                            //覆盖购买数量，不累加，参考京东
                            redisShopCart.setBuyCounts(cookieShopCart.getBuyCounts());
                            //把cookieShopCart放入待删除列表，用于最后的删除与合并
                            pendingDeleteList.add(cookieShopCart);
                        }
                    }
                }

                //从现有cookie中删除对应的覆盖过的商品数据
                shopCartListCookie.removeAll(pendingDeleteList);

                //合并两个list
                shopCartListRedis.addAll(shopCartListCookie);
                //更新到redis和cookie
                redisOperator.set(FOODIE_SHOPCART + ":" + userId, JsonUtils.objectToJson(shopCartListRedis));
                CookieUtils.setCookie(request, response, FOODIE_SHOPCART, JsonUtils.objectToJson(shopCartListRedis), true);
            } else {
                //redis不为空，cookie为空，直接把redis覆盖cookie
                CookieUtils.setCookie(request, response, FOODIE_SHOPCART, shopCartJsonRedis, true);
            }
        }
    }

    @ApiOperation(value = "用户退出登录", notes = "用户退出登录", httpMethod = "POST")
    @PostMapping("/logout")
    public ServerResponse logout(@RequestParam String userId,
                                 HttpServletRequest request,
                                 HttpServletResponse response) {

        // 清除用户的相关信息的cookie
        CookieUtils.deleteCookie(request, response, "user");

        //用户退出登录，清除redis中user的会话信息
        redisOperator.del(REDIS_USER_TOKEN + ":" + userId);

        // 用户退出登录，需要清空购物车
        CookieUtils.deleteCookie(request, response, FOODIE_SHOPCART);

        return ServerResponse.ok();
    }


}
