package org.foodie.controller;

import org.apache.commons.lang3.StringUtils;
import org.foodie.pojo.Users;
import org.foodie.pojo.vo.UsersVO;
import org.foodie.service.IUsersService;
import org.foodie.utils.JsonUtils;
import org.foodie.utils.MD5Utils;
import org.foodie.utils.RedisOperator;
import org.foodie.utils.ServerResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * @Author steve.mei
 * @Version SSOController,  2020/11/14 下午5:26
 **/
@Controller
public class SSOController {

    @Autowired
    private IUsersService userService;

    @Autowired
    private RedisOperator redisOperator;

    public static final String REDIS_USER_TOKEN = "redis_user_token";
    public static final String REDIS_USER_TICKET = "redis_user_ticket";
    public static final String REDIS_TMP_TICKET = "redis_tmp_ticket";
    public static final String COOKIE_USER_TICKET = "cookie_user_ticket";

    @GetMapping("/login")
    public String login(String returnUrl,
                        Model model,
                        HttpServletRequest request,
                        HttpServletResponse response) {
        model.addAttribute("returnUrl", returnUrl);

        // 1.获取userTicket，门票，如果cookie能够获取到，证明用户登陆过，此时签发一个一次性的临时票据并且回跳
        String userTicket = getCookie(request);

        boolean isVerified = this.verifyUserTicket(userTicket);

        if (isVerified) {
            String tmpTicket = createTmpTicket();
            return "redirect:" + returnUrl + "?tmpTicket=" + tmpTicket;
        }

        // 2.用户从未登陆过，第一次进入则跳转到CAS的统一登录页面
        return "login";
    }

    /**
     * 校验CAS全局用户门票
     *
     * @param userTicket
     * @return
     */
    private boolean verifyUserTicket(String userTicket) {
        //0. 验证CAS门票不能为空
        if (StringUtils.isBlank(userTicket)) {
            return false;
        }

        //1. 验证CAS门票是否有效
        String userId = redisOperator.get(REDIS_USER_TICKET + ":" + userTicket);

        if (StringUtils.isBlank(userId)) {
            return false;
        }

        String token = redisOperator.get(REDIS_USER_TICKET + ":" + userId);

        if (StringUtils.isBlank(token)) {
            return false;
        }

        return true;
    }

    /**
     * CAS的统一登录接口
     * 目的：
     * 1.登录后创建用户的全局会话                       -> uniqueToken
     * 2.创建用户全局门票，用以表示在CAS端是否登录       -> userTicket
     * 3.创建用户的临时票据，用于回眺回传               -> tmpTicket
     *
     * @param username
     * @param password
     * @param returnUrl
     * @param model
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @PostMapping("/doLogin")
    public String doLogin(String username,
                          String password,
                          String returnUrl,
                          Model model,
                          HttpServletRequest request,
                          HttpServletResponse response) throws Exception {

        model.addAttribute("returnUrl", returnUrl);

        // 0. 判断用户名和密码必须不为空
        if (StringUtils.isBlank(username) ||
                StringUtils.isBlank(password)) {
            model.addAttribute("errmsg", "用户名或密码不能为空");
            return "login";
        }

        // 1. 实现登录
        Users userResult = userService.queryUserForLogin(username,
                MD5Utils.getMD5Str(password));

        if (userResult == null) {
            model.addAttribute("errmsg", "用户名或密码不正确");
            return "login";
        }


        //2.实现用户的redis会话
        String uniqueToken = UUID.randomUUID().toString().trim();
        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(userResult, usersVO);
        usersVO.setUserUniqueToken(uniqueToken);
        redisOperator.set(REDIS_USER_TOKEN + ":" + userResult.getId(), JsonUtils.objectToJson(usersVO));

        //3.生成ticket门票，全局门票，代表用户在CAS登录过
        String userTicket = UUID.randomUUID().toString().trim();

        //3.1 用户全局门票需要放入CAS端的cookie中
        setCookie(userTicket, response);

        //4. userTicket关联用户id，并且放入Redis中，代表这个用户有门票了，可以在各个景区游玩
        redisOperator.set(REDIS_USER_TICKET + ":" + userTicket, userResult.getId());

        //5.生成临时票据，回眺到调用端网址，是由CAS端发的一个一次性的临时ticket
        String tmpTicket = this.createTmpTicket();

        /**
         * userTicket：用于表示用户在CAS端的一个登陆状态：已经登陆
         * tmpTicket：用于颁发给用户进行一次性的验证的票据，有时效性
         */

//        return  "login";
        return "redirect:" + returnUrl + "?tmpTicket=" + tmpTicket;
    }

    @PostMapping("/verifyTmpTicket")
    @ResponseBody
    public ServerResponse verifyTmpTicket(String tmpTicket,
                                          HttpServletRequest request) throws Exception {


        // 使用一次性临时票据来校验用户是否登录，如果登陆过，把用户会话信息返回给站点
        // 使用完毕后，需要销毁临时票据
        String tmpTicketValue = redisOperator.get(REDIS_TMP_TICKET + ":" + tmpTicket);
        if (StringUtils.isBlank(tmpTicketValue)) {
            return ServerResponse.errorUserTicket("用户票据异常");
        }

        // 0.如果临时票据ok，则需要销毁，并且拿到CAS端cookie中的全局userTicket，以此再获取用户会话
        if (!tmpTicketValue.equals(MD5Utils.getMD5Str(tmpTicket))) {
            return ServerResponse.errorUserTicket("用户票据异常");
        } else {
            // 销毁临时票据
            redisOperator.del(REDIS_TMP_TICKET + ":" + tmpTicket);
        }

        //1. 验证并且获取用户的userTicket
        String userTicket = getCookie(request);
        String userId = redisOperator.get(REDIS_USER_TICKET + ":" + userTicket);
        if (StringUtils.isBlank(userId)) {
            return ServerResponse.errorUserTicket("用户票据异常");
        }

        //2. 验证门票对应的user会话是否存在
        String userRedis = redisOperator.get(REDIS_USER_TOKEN + ":" + userId);
        if (StringUtils.isBlank(userRedis)) {
            return ServerResponse.errorUserTicket("用户票据异常");
        }

        //验证成功，返回ok，携带用户会话

        return ServerResponse.ok(JsonUtils.jsonToPojo(userRedis, UsersVO.class));
    }


    @PostMapping("/logout")
    public ServerResponse logout(String userId,
                                 HttpServletRequest request,
                                 HttpServletResponse response) {
        //0. 获取CAS中的用户门票
        String userTicket = getCookie(request);

        //1. 清除userTicket
        this.deleteCookie(response);
        redisOperator.del(REDIS_USER_TICKET + ":" + userTicket);

        // 2. 清除用户全局会话
        redisOperator.del(REDIS_USER_TOKEN + ":" + userId);

        return ServerResponse.ok();
    }


    /**
     * 创建临时票据
     *
     * @return
     */
    private String createTmpTicket() {
        String tmpTicket = UUID.randomUUID().toString().trim();

        try {
            redisOperator.set(REDIS_TMP_TICKET + ":" + tmpTicket, MD5Utils.getMD5Str(tmpTicket), 600);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tmpTicket;
    }


    private void setCookie(String val,
                           HttpServletResponse response) {
        Cookie cookie = new Cookie(COOKIE_USER_TICKET, val);
        cookie.setDomain("sso.com");
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    private String getCookie(HttpServletRequest request) {


        Cookie[] cookies = request.getCookies();
        if (cookies == null || StringUtils.isBlank(COOKIE_USER_TICKET)) {
            return null;
        }

        String cookieValue = null;

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(COOKIE_USER_TICKET)) {
                cookieValue = cookie.getValue();
            }
        }
        return cookieValue;
    }


    private void deleteCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(COOKIE_USER_TICKET, null);
        cookie.setDomain("sso.com");
        cookie.setPath("/");
        cookie.setMaxAge(-1);
        response.addCookie(cookie);
    }
}
