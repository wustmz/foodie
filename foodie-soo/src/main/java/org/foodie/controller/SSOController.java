package org.foodie.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author steve.mei
 * @Version SSOController,  2020/11/14 下午5:26
 **/
@Controller
public class SSOController {


    @GetMapping("/login")
    public String login(String returnUrl, Model model,
                        HttpServletRequest request,
                        HttpServletResponse response) {
        model.addAttribute("returnUrl", returnUrl);

        // TODO: 2020/11/14 后续完善是否登录

        //用户从未登陆过，第一次进入则跳转到CAS的统一登录页面
        return "login";
    }
}
