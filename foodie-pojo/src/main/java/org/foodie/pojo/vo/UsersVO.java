package org.foodie.pojo.vo;

import lombok.Data;

/**
 * @Author steve.mei
 * @Version UsersVO,  2020/11/13 下午10:41
 **/
@Data
public class UsersVO {

    /**
     * 主键id 用户id
     */
    private String id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;
    /**
     * 头像
     */
    private String face;
    /**
     * 性别 1：男 0：女 2：保密
     */
    private Integer sex;
    /**
     * 用户会话token
     */
    private String userUniqueToken;
}
