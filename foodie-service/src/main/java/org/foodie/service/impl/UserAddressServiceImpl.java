package org.foodie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.foodie.mapper.UserAddressMapper;
import org.foodie.pojo.UserAddress;
import org.foodie.service.IUserAddressService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户地址表  服务实现类
 * </p>
 *
 * @author steve.mei
 * @since 2020-10-20
 */
@Service
public class UserAddressServiceImpl extends ServiceImpl<UserAddressMapper, UserAddress> implements IUserAddressService {

}
