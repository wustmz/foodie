package org.foodie.controller.intercepor;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.foodie.utils.JsonUtils;
import org.foodie.utils.RedisOperator;
import org.foodie.utils.ServerResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * @Author steve.mei
 * @Version UserTokenInterceptor,  2020/11/14 上午10:00
 **/
@Slf4j
public class UserTokenInterceptor implements HandlerInterceptor {

    public static final String REDIS_USER_TOKEN = "redis_user_token";

    @Autowired
    private RedisOperator redisOperator;

    /**
     * 拦截请求，在访问controller调用之前
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("进入到拦截器，被拦截。。。");

        String userId = request.getHeader("headerUserId");
        String headerUserToken = request.getHeader("headerUserToken");

        if (StringUtils.isBlank(userId) || StringUtils.isBlank(headerUserToken)) {
            returnErrorResponse(response, ServerResponse.errorMsg("请登录。。。"));
            return false;
        } else {
            String uniqueToken = redisOperator.get(REDIS_USER_TOKEN + ":" + userId);
            if (StringUtils.isBlank(uniqueToken)) {
                returnErrorResponse(response, ServerResponse.errorMsg("请登录。。。"));
                return false;
            } else {
                if (!StringUtils.equals(headerUserToken, uniqueToken)) {
                    returnErrorResponse(response, ServerResponse.errorMsg("账号在异地登录。。。"));
                    return false;
                }
            }
        }

        /**
         * false：请求呗拦截，被驳回，验证出现问题
         * true：请求在经过校验之后是OK的，可以放行
         */
        return true;
    }


    public void returnErrorResponse(HttpServletResponse response, ServerResponse serverResponse) {
        OutputStream out = null;
        try {
            response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
            response.setContentType("text/json");
            out = response.getOutputStream();
            out.write(JsonUtils.objectToJson(serverResponse).getBytes(StandardCharsets.UTF_8));
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                    ;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 请求访问controller之后，渲染视图之前
     *
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    /**
     * 请求访问controller之后，渲染视图之后
     *
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
