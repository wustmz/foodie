package org.foodie.pojo.bo;

import lombok.Data;

/**
 * @Author steve.mei
 * @Version UserBo,  2020/10/19 9:20 下午
 **/
@Data
public class UserBo {

    private String username;

    private String password;

    private String confirmPassword;
}
