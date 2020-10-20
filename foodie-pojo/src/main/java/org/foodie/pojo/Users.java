package org.foodie.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author steve.mei
 * @since 2020-10-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("users")
public class Users extends Model<Users> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id 用户id
     */
    @TableId("id")
    private String id;

    /**
     * 用户名 用户名
     */
    @TableField("username")
    private String username;

    /**
     * 密码 密码
     */
    @TableField("password")
    private String password;

    /**
     * 昵称 昵称
     */
    @TableField("nickname")
    private String nickname;

    /**
     * 真实姓名 真实姓名
     */
    @TableField("realname")
    private String realname;

    /**
     * 头像 头像
     */
    @TableField("face")
    private String face;

    /**
     * 手机号 手机号
     */
    @TableField("mobile")
    private String mobile;

    /**
     * 邮箱地址 邮箱地址
     */
    @TableField("email")
    private String email;

    /**
     * 性别 性别 1:男  0:女  2:保密
     */
    @TableField("sex")
    private Integer sex;

    /**
     * 生日 生日
     */
    @TableField("birthday")
    private Date birthday;

    /**
     * 创建时间 创建时间
     */
    @TableField("created_time")
    private Date createdTime;

    /**
     * 更新时间 更新时间
     */
    @TableField("updated_time")
    private Date updatedTime;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
