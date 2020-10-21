package org.foodie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.foodie.pojo.Users;
import org.foodie.pojo.bo.UserBO;

/**
 * <p>
 * 用户表  服务类
 * </p>
 *
 * @author steve.mei
 * @since 2020-10-20
 */
public interface IUsersService extends IService<Users> {

    /**
     * 判断用户名是否存在
     */
    boolean queryUsernameIsExist(String username);

    /**
     * 创建用户
     */
    Users createUser(UserBO userBo);

    /**
     * 检索用户名和密码是否匹配，用于登录
     */
    Users queryUserForLogin(String username, String password);
}
