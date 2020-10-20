package org.foodie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.foodie.pojo.Users;
import org.foodie.pojo.bo.UserBo;

/**
 * <p>
 * 用户表  服务类
 * </p>
 *
 * @author steve.mei
 * @since 2020-10-20
 */
public interface IUsersService extends IService<Users> {

    boolean queryUsernameIsExist(String username);

    Users createUser(UserBo userBo);
}
