package org.foodie.service.impl.center;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.foodie.mapper.UsersMapper;
import org.foodie.pojo.Users;
import org.foodie.pojo.bo.center.CenterUserBO;
import org.foodie.service.center.CenterUserService;
import org.n3r.idworker.Sid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @author wustmz
 */
@Service
public class CenterUserServiceImpl implements CenterUserService {

    @Autowired
    public UsersMapper usersMapper;

    @Autowired
    private Sid sid;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Users queryUserInfo(String userId) {
        Users user = usersMapper.selectById(userId);
        user.setPassword(null);
        return user;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Users updateUserInfo(String userId, CenterUserBO centerUserBO) {

        Users updateUser = new Users();
        BeanUtils.copyProperties(centerUserBO, updateUser);
        updateUser.setId(userId);
        updateUser.setUpdatedTime(new Date());

        UpdateWrapper<Users> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().eq(Users::getId, userId);

        usersMapper.update(updateUser, updateWrapper);

        return queryUserInfo(userId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Users updateUserFace(String userId, String faceUrl) {
        Users updateUser = new Users();
        updateUser.setId(userId);
        updateUser.setFace(faceUrl);
        updateUser.setUpdatedTime(new Date());

        UpdateWrapper<Users> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().eq(Users::getId, userId);

        usersMapper.update(updateUser, updateWrapper);

        return queryUserInfo(userId);
    }
}
