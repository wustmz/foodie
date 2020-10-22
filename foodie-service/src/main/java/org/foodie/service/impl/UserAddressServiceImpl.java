package org.foodie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.foodie.enums.YesOrNo;
import org.foodie.mapper.UserAddressMapper;
import org.foodie.pojo.UserAddress;
import org.foodie.pojo.bo.AddressBO;
import org.foodie.service.IUserAddressService;
import org.n3r.idworker.Sid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

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

    @Autowired
    private UserAddressMapper userAddressMapper;

    @Autowired
    private Sid sid;


    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<UserAddress> queryAll(String userId) {
        QueryWrapper<UserAddress> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(UserAddress::getUserId, userId);
        return userAddressMapper.selectList(wrapper);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void addNewUserAddress(AddressBO addressBO) {
        // 1. 判断当前用户是否存在地址，如果没有，则新增为‘默认地址’
        int isDefault = 0;
        List<UserAddress> addressList = this.queryAll(addressBO.getUserId());
        if (CollectionUtils.isEmpty(addressList)) {
            isDefault = 1;
        }

        String addressId = sid.nextShort();

        // 2. 保存地址到数据库
        UserAddress newAddress = new UserAddress();
        BeanUtils.copyProperties(addressBO, newAddress);

        newAddress.setId(addressId);
        newAddress.setIsDefault(isDefault);
        newAddress.setCreatedTime(LocalDateTime.now());
        newAddress.setUpdatedTime(LocalDateTime.now());

        userAddressMapper.insert(newAddress);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateUserAddress(AddressBO addressBO) {
        String addressId = addressBO.getAddressId();

        UserAddress pendingAddress = new UserAddress();
        BeanUtils.copyProperties(addressBO, pendingAddress);

        pendingAddress.setId(addressId);
        pendingAddress.setUpdatedTime(LocalDateTime.now());

        QueryWrapper<UserAddress> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(UserAddress::getId, addressId);
        userAddressMapper.update(pendingAddress, wrapper);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void deleteUserAddress(String userId, String addressId) {
        UserAddress address = new UserAddress();
        address.setId(addressId);
        address.setUserId(userId);

        QueryWrapper<UserAddress> wrapper = new QueryWrapper<>();
        wrapper.lambda()
                .eq(UserAddress::getId, addressId)
                .eq(UserAddress::getUserId, userId);

        userAddressMapper.delete(wrapper);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateUserAddressToBeDefault(String userId, String addressId) {
        // 1. 查找默认地址，设置为不默认
        UserAddress queryAddress = new UserAddress();
        queryAddress.setUserId(userId);
        queryAddress.setIsDefault(YesOrNo.YES.type);

        QueryWrapper<UserAddress> wrapper = new QueryWrapper<>();
        wrapper.lambda()
                .eq(UserAddress::getUserId, userId)
                .eq(UserAddress::getIsDefault, YesOrNo.YES.type);

        List<UserAddress> list = userAddressMapper.selectList(wrapper);
        for (UserAddress ua : list) {
            ua.setIsDefault(YesOrNo.NO.type);
            userAddressMapper.updateById(ua);
        }

        // 2. 根据地址id修改为默认的地址
        UserAddress defaultAddress = new UserAddress();
        defaultAddress.setId(addressId);
        defaultAddress.setUserId(userId);
        defaultAddress.setIsDefault(YesOrNo.YES.type);

        UpdateWrapper<UserAddress> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda()
                .eq(UserAddress::getId, addressId)
                .eq(UserAddress::getUserId, userId);
        userAddressMapper.update(defaultAddress, updateWrapper);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public UserAddress queryUserAddres(String userId, String addressId) {
        QueryWrapper<UserAddress> wrapper = new QueryWrapper<>();
        wrapper.lambda()
                .eq(UserAddress::getId, addressId)
                .eq(UserAddress::getUserId, userId);


        return userAddressMapper.selectOne(wrapper);
    }
}
